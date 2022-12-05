package com.example.jetpackplayerapp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jetpackplayerapp.BR;
import com.example.jetpackplayerapp.R;
import com.example.jetpackplayerapp.databinding.LayoutFeedTypeImageBinding;
import com.example.jetpackplayerapp.databinding.LayoutFeedTypeVideoBinding;
import com.example.jetpackplayerapp.model.Feed;
import com.example.jetpackplayerapp.view.ListPlayerView;

public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    protected Context mContext;
    protected String mCategory;

    public FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });

        inflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }

    /**
     * 实现父类的ViewHolder方法，以提供xml布局
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    /**
     * 实现数据的绑定
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Feed feed = getItem(position);
        holder.bindData(feed);
    }

    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        if (feed.itemType == Feed.TYPE_IMAGE_TEXT) {
            return R.layout.layout_feed_type_image;
        } else if (feed.itemType == Feed.TYPE_VIDEO) {
            return R.layout.layout_feed_type_video;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewDataBinding mBinding;
        public ListPlayerView listPlayerView;
        public ImageView feedImage;

        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Feed item) {
            //这里之所以手动绑定数据的原因是 图片 和视频区域都是需要计算的
            //而dataBinding的执行默认是延迟一帧的。
            //当列表上下滑动的时候 ，会明显的看到宽高尺寸不对称的问题

            // 给binding类型的xml设置数据
            mBinding.setVariable(com.example.jetpackplayerapp.BR.feed, item);
            mBinding.setVariable(BR.lifeCycleOwner, mContext);

            // 给非binding类型的组件手动设置数据
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                feedImage = imageBinding.feedImage;
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
            } else if (mBinding instanceof LayoutFeedTypeVideoBinding) {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.listPlayerView.binData(mCategory, item.width, item.height, item.cover, item.url);
                listPlayerView = videoBinding.listPlayerView;
            }
        }
    }
}
