package com.mooc.fageplayer.exoplayer;

import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表视频自动播放 检测逻辑
 */
public class PageListPlayDetector {

    // 收集一个个的能够播放视频的对象 面向接口开发
    private List<IPlayTarget> mTargets = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private IPlayTarget playingTarget;

    public void addTarget(IPlayTarget target) {
        mTargets.add(target);
    }

    public void removeTarget(IPlayTarget target) {
        mTargets.remove(target);
    }

    /**
     * 初始化
     * 传入Lifecycle
     * 传入RecycleView用来检测滚动
     * @param owner
     * @param recyclerView
     */
    public PageListPlayDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    playingTarget = null;
                    mTargets.clear();
                    mRecyclerView.removeCallbacks(delayAutoPlay);
                    recyclerView.removeOnScrollListener(scrollListener);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });
        recyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);
        recyclerView.addOnScrollListener(scrollListener);
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay();
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dx == 0 && dy == 0) {
                //时序问题。当执行了AdapterDataObserver#onItemRangeInserted  可能还没有被布局到RecyclerView上。
                //所以此时 recyclerView.getChildCount()还是等于0的。
                //等childView 被布局到RecyclerView上之后，会执行onScrolled（）方法
                //并且此时 dx,dy都等于0
                postAutoPlay();
            } else {
                // 如果有正在播放的 且滑动的时候被划出了屏幕 则停止它
                if (playingTarget != null && playingTarget.isPlaying() && !isTargetInBounds(playingTarget)) {
                    playingTarget.inActive();
                }
            }
        }
    };

    private void postAutoPlay() {
        mRecyclerView.post(delayAutoPlay);
    }

    Runnable delayAutoPlay = new Runnable() {
        @Override
        public void run() {
            autoPlay();
        }
    };

    /**
     * 如果RV有新数据插入 则直接播放
     */
    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            postAutoPlay();
        }
    };

    private void autoPlay() {
        if (mTargets.size() <= 0 || mRecyclerView.getChildCount() <= 0) {
            return;
        }

        if (playingTarget != null && playingTarget.isPlaying() && isTargetInBounds(playingTarget)) {
            return;
        }

        IPlayTarget activeTarget = null;
        for (IPlayTarget target : mTargets) {
            boolean isBounds = isTargetInBounds(target);
            if (isBounds) {
                activeTarget = target;
                break;
            }
        }

        if (activeTarget != null) {
            if (playingTarget != null) {
                playingTarget.inActive();
            }
            playingTarget = activeTarget;
            activeTarget.onActive();
        }
    }

    /**
     * 判断播放对象是不是在屏幕内部
     * 检测播放对象所在的ViewGroup 是否至少还有一半以上的大小在屏幕内部
     * @param playingTarget
     * @return
     */
    private boolean isTargetInBounds(IPlayTarget playingTarget) {
        ViewGroup owner = playingTarget.getOwner();

        // 拿到RV原点位置
        ensureRecyclerViewLocation();
        if (!owner.isShown() || !owner.isAttachedToWindow()) {
            return false;
        }

        // 拿到播放器原点位置
        int[] location = new int[2];
        owner.getLocationOnScreen(location);

        // 获取播放器的中心点 也就是View的一半的底边中心点的Y值
        int centerY = location[1] + owner.getHeight() / 2;
        // 承载视频播放画面的ViewGroup它需要至少一半的大小 在RV上下范围内
        // 判断播放器的中心点的Y值，大于RV可视部分的Top，小于RV可视部分的Bottom
        return centerY >= rvLocation.first && centerY <= rvLocation.second;
    }

    /**
     * 获取RecyclerView在
     */
    private Pair<Integer, Integer> rvLocation = null;
    private Pair<Integer, Integer> ensureRecyclerViewLocation() {
        if (rvLocation == null) {
            int[] location = new int[2];
            mRecyclerView.getLocationOnScreen(location);
            int top = location[1];
            int bottom = top + mRecyclerView.getHeight();
            rvLocation = new Pair(top, bottom);
        }
        return rvLocation;
    }
}
