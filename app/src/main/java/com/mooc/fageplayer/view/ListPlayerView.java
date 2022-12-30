package com.mooc.fageplayer.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.fageplayer.R;
import com.mooc.fageplayer.exoplayer.IPlayTarget;
import com.mooc.fageplayer.exoplayer.PageListPlay;
import com.mooc.fageplayer.exoplayer.PageListPlayManager;
import com.mooc.libcommon.utils.PixUtils;

/**
 * 列表视频播放专用
 */
public class ListPlayerView extends FrameLayout implements IPlayTarget, PlayerControlView.VisibilityListener, Player.EventListener {

    public View bufferView;
    public PPImageView cover, blur;
    protected ImageView playBtn;
    protected String mCategory;
    protected String mVideoUrl;
    protected boolean isPlaying;
    protected int mWidthPx;
    protected int mHeightPx;

    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true);

        // 缓冲转圈View
        bufferView = findViewById(R.id.buffer_view);
        // 封面View
        cover = findViewById(R.id.cover);
        // 高斯模糊背景图
        blur = findViewById(R.id.blur_background);
        // 播放和暂停的按钮
        playBtn = findViewById(R.id.play_btn);

        playBtn.setOnClickListener(view -> {
            if (isPlaying()) {
                inActive();
            } else {
                onActive();
            }
        });

        this.setTransitionName("listPlayerView");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 点击该区域的时候 主动让视频控制器显示出来
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        pageListPlay.controlView.show();
        return true;
    }

    public void binData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        mCategory = category;
        mVideoUrl = videoUrl;
        mWidthPx = widthPx;
        mHeightPx = heightPx;
        cover.setImageUrl(coverUrl);

        // 如果该视频的宽度小于高度，则高斯模糊背景图显示出来
        if (widthPx < heightPx) {
            PPImageView.setBlurImageUrl(blur, coverUrl, 10);
            blur.setVisibility(VISIBLE);
        } else {
            blur.setVisibility(GONE);
        }
        setSize(widthPx, heightPx);
    }

    /**
     * 视频播放器
     * 背景图
     * 高斯模糊图
     * 等比例缩放
     *
     * @param widthPx
     * @param heightPx
     */
    protected void setSize(int widthPx, int heightPx) {
        int maxWidth = PixUtils.getScreenWidth();
        int maxHeight = maxWidth;

        int layoutWidth = maxWidth;
        int layoutHeight;

        int coverWidth;
        int coverHeight;

        if (widthPx >= heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / (widthPx * 1.0f / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = layoutWidth;
        params.height = layoutHeight;
        setLayoutParams(params);

        ViewGroup.LayoutParams blurParams = blur.getLayoutParams();
        blurParams.width = layoutWidth;
        blurParams.height = layoutHeight;
        blur.setLayoutParams(blurParams);

        FrameLayout.LayoutParams coverParams = (LayoutParams) cover.getLayoutParams();
        coverParams.width = coverWidth;
        coverParams.height = coverHeight;
        coverParams.gravity = Gravity.CENTER;
        cover.setLayoutParams(coverParams);

        FrameLayout.LayoutParams playBtnParams = (LayoutParams) playBtn.getLayoutParams();
        playBtnParams.gravity = Gravity.CENTER;
        playBtn.setLayoutParams(playBtnParams);
    }

    @Override
    public ViewGroup getOwner() {
        return this;
    }

    @Override
    public void onActive() {
        // 视频播放 或者恢复播放
        /**
         * 通过Category字段取出管理该页面的Exoplayer播放器，ExoplayerView的播放View，控制器对象的PageListPlay
         */
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        PlayerView playerView = pageListPlay.playerView;
        PlayerControlView controlView = pageListPlay.controlView;
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playerView == null) {
            return;
        }

        /**
         * 此处我们需要主动调用一次 switchPlayerView，把播放器exoplayer和展示视频画面的view exoplayerView相关联
         * 为什么呢？因为在列表页点击视频Item跳转到视频详情页的时候，详情页会复用列表页的播放器exoplayer。然后和新创建的展示视频画面的View exoplayerView相关联 达到无缝续播的效果
         * 如果我们再次返回列表页 则需要再次把播放器和exoplayerView相关联
         */
        pageListPlay.switchPlayerView(playerView, true);

        // 添加播放器到Item上
        ViewParent parent = playerView.getParent();
        if (parent != this) {
            if (parent != null) {
                // 暂停掉正在播放的那个并移除
                ((ViewGroup) parent).removeView(playerView);
                ((ListPlayerView) parent).inActive();
            }
            ViewGroup.LayoutParams coverParams = cover.getLayoutParams();
            this.addView(playerView, 1, coverParams);
        }

        ViewParent ctrlParent = controlView.getParent();
        if (ctrlParent != this) {
            // 把控制器添加到ItemView容器
            if (ctrlParent != null) {
                ((ViewGroup) ctrlParent).removeView(controlView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM;
            this.addView(controlView, params);
        }

        //如果是同一个视频资源,则不需要从重新创建mediaSource。
        //但需要onPlayerStateChanged 否则不会触发onPlayerStateChanged()
        if (TextUtils.equals(pageListPlay.playUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY);
        } else {
            MediaSource mediaSource = PageListPlayManager.createMediaSource(mVideoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            pageListPlay.playUrl = mVideoUrl;
        }
        controlView.show();
        controlView.setVisibilityListener(this);
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isPlaying = false;
        bufferView.setVisibility(GONE);
        cover.setVisibility(VISIBLE);
        playBtn.setVisibility(VISIBLE);
        playBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public void inActive() {
        //暂停视频的播放并让封面图和 开始播放按钮 显示出来
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        if (pageListPlay.exoPlayer == null || pageListPlay.controlView == null || pageListPlay.exoPlayer == null)
            return;
        pageListPlay.exoPlayer.setPlayWhenReady(false);
        pageListPlay.controlView.setVisibilityListener(null);
        pageListPlay.exoPlayer.removeListener(this);
        cover.setVisibility(VISIBLE);
        playBtn.setVisibility(VISIBLE);
        playBtn.setImageResource(R.drawable.icon_video_play);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }


    @Override
    public void onVisibilityChange(int visibility) {
        playBtn.setVisibility(visibility);
        playBtn.setImageResource(isPlaying() ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        //监听视频播放的状态
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady) {
            cover.setVisibility(GONE);
            bufferView.setVisibility(GONE);
        } else if (playbackState == Player.STATE_BUFFERING) {
            bufferView.setVisibility(VISIBLE);
        }
        isPlaying = playbackState == Player.STATE_READY && exoPlayer.getBufferedPosition() != 0 && playWhenReady;
        playBtn.setImageResource(isPlaying ? R.drawable.icon_video_pause : R.drawable.icon_video_play);
    }

    public View getPlayController() {
        PageListPlay listPlay = PageListPlayManager.get(mCategory);
        return listPlay.controlView;
    }
}
