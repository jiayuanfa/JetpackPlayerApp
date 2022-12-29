package com.mooc.fageplayer.ui.detail;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;

import com.mooc.fageplayer.view.FullScreenPlayerView;

public class VideoViewHandler extends ViewHandler{

    private final CoordinatorLayout coordinatorLayout;
    private FullScreenPlayerView playerView;


    public VideoViewHandler(FragmentActivity activity) {
        super(activity);
    }
}
