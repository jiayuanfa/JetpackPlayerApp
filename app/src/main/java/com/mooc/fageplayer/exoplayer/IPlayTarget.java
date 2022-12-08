package com.mooc.fageplayer.exoplayer;

import android.view.ViewGroup;

public interface IPlayTarget {

    ViewGroup getOwner();

    // 活跃状态 视频课播放
    void onActive();

    // 非活跃状态 暂停视频播放
    void inActive();

    boolean isPlaying();
}
