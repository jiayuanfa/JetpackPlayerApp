package com.mooc.fageplayer.ui.detail;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.fageplayer.R;
import com.mooc.fageplayer.databinding.ActivityFeedDetailTypeImageBinding;
import com.mooc.fageplayer.databinding.LayoutFeedDetailTypeImageHeaderBinding;
import com.mooc.fageplayer.model.Feed;
import com.mooc.fageplayer.view.PPImageView;

public class ImageViewHandler extends ViewHandler{

    protected ActivityFeedDetailTypeImageBinding mImageBinding;
    protected LayoutFeedDetailTypeImageHeaderBinding mHeaderBinding;

    public ImageViewHandler(FragmentActivity activity) {
        super(activity);

        mImageBinding = DataBindingUtil.setContentView(activity, R.layout.activity_feed_detail_type_image);

        mInateractionBinding = mImageBinding.interactionLayout;

        mRecyclerView = mImageBinding.recyclerView;

        mImageBinding.actionClose.setOnClickListener(view -> {
            mActivity.finish();
        });
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);

        mImageBinding.setFeed(mFeed);

        mHeaderBinding = LayoutFeedDetailTypeImageHeaderBinding.inflate(LayoutInflater.from(mActivity), mRecyclerView, false);
        mHeaderBinding.setFeed(mFeed);
        PPImageView headerImage = mHeaderBinding.headerImage;
        headerImage.bindData(mFeed.width, mFeed.height, mFeed.width > mFeed.height ? 0 : 16, mFeed.cover);

        // 把HeaderView添加到Adapter中
        listAdapter.addHeaderView(mHeaderBinding.getRoot());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean visible = mHeaderBinding.getRoot().getTop() <= -mImageBinding.titleLayout.getMeasuredHeight();
                mImageBinding.authorInfoLayout.getRoot().setVisibility(visible? View.VISIBLE : View.GONE);
                mImageBinding.title.setVisibility(visible? View.GONE : View.VISIBLE);
            }
        });
    }
}
