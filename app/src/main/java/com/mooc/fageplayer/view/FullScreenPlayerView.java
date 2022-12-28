package com.mooc.fageplayer.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.mooc.fageplayer.R;
import com.mooc.fageplayer.exoplayer.PageListPlay;
import com.mooc.fageplayer.exoplayer.PageListPlayManager;
import com.mooc.libcommon.utils.PixUtils;

/**
 * 视频详情页全屏播放专用
 */
public class FullScreenPlayerView extends ListPlayerView {

    private PlayerView exoPlayerView;

    public FullScreenPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public FullScreenPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FullScreenPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        exoPlayerView = (PlayerView) LayoutInflater.from(context).inflate(R.layout.layout_exo_player_view, null, false);
    }

    @Override
    protected void setSize(int widthPx, int heightPx) {
        if (widthPx >= heightPx) {
            super.setSize(widthPx, heightPx);
            return;
        }
        int maxWidth = PixUtils.getScreenWidth();
        int maxHeight = PixUtils.getScreenHeight();

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = maxWidth;
        params.height = maxHeight;
        setLayoutParams(params);

        FrameLayout.LayoutParams coverLayoutParams = (LayoutParams) cover.getLayoutParams();
        coverLayoutParams.width = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        coverLayoutParams.height = maxHeight;
        coverLayoutParams.gravity = Gravity.CENTER;
        cover.setLayoutParams(coverLayoutParams);
    }

    /**
     * 外界通过此方法来设置播放器界面的宽高比例
     * @param params
     */
    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (mHeightPx > mWidthPx) {
            int layoutWidth = params.width;
            int layoutHeight = params.height;

            ViewGroup.LayoutParams coverLayoutParams = cover.getLayoutParams();
            coverLayoutParams.width = (int) (mWidthPx / (mHeightPx * 1.0f / layoutHeight));
            coverLayoutParams.height = layoutHeight;

            cover.setLayoutParams(coverLayoutParams);
            if (exoPlayerView != null) {
                ViewGroup.LayoutParams layoutParams = exoPlayerView.getLayoutParams();
                if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                    float scaleX = coverLayoutParams.width * 1.0f / layoutParams.width;
                    float scaleY = coverLayoutParams.height * 1.0f / layoutParams.height;

                    exoPlayerView.setScaleX(scaleX);
                    exoPlayerView.setScaleY(scaleY);
                }
            }
        }

        super.setLayoutParams(params);
    }

    @Override
    public void onActive() {
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        PlayerView playerView = exoPlayerView;
        PlayerControlView controlView = pageListPlay.controlView;
        SimpleExoPlayer exoPlayer = pageListPlay.exoPlayer;
        if (playerView == null) {
            return;
        }

        // 主动关联播放器与exoPlayerView
        pageListPlay.switchPlayerView(playerView, true);

        ViewParent parent = playerView.getParent();
        if (parent != this) {
            if (parent != null) {
                ((ViewGroup) parent).removeView(playerView);
            }
            ViewGroup.LayoutParams coverParams = cover.getLayoutParams();
            this.addView(playerView, 1, coverParams);
        }

        ViewParent ctrlParent = controlView.getParent();
        if (ctrlParent != this) {
            if (ctrlParent != null) {
                ((ViewGroup) ctrlParent).removeView(controlView);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.BOTTOM;
            this.addView(controlView, params);
        }

        /**
         * 如果是同一个视频资源，则不需要重新创建mediaSource
         * 但是需要onPlayerStateChanged 否则不会触发onPlayerStateChanged()
         */
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
    public void inActive() {
        super.inActive();
        PageListPlay pageListPlay = PageListPlayManager.get(mCategory);
        // 主动切断exoplayer与视频播放器的联系
        pageListPlay.switchPlayerView(exoPlayerView, false);
    }
}
