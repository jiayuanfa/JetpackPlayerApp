package com.mooc.fageplayer.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.mooc.fageplayer.ui.AbsListFragment;
import com.mooc.fageplayer.ui.MutablePageKeyedDataSource;
import com.mooc.fageplayer.exoplayer.PageListPlayDetector;
import com.mooc.fageplayer.model.Feed;
import com.mooc.fageplayer.utils.AppConstant;
import com.mooc.libnavannotation.FragmentDestination;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {

    private String feedType;
    private PageListPlayDetector playDetector;

    /**
     * 通过feedType实例化一个HomeFragment
     * @param feedType
     * @return
     */
    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        /**
         * 监听缓存数据的变换
         */
        mViewModel.getCacheLiveData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                System.out.println(AppConstant.LOG_HOME_FRAGMENT + "缓存数据发生变化被监听到，提交数据给PagedListAdapter");
                submitList(feeds);
            }
        });

        playDetector = new PageListPlayDetector(this, mRecyclerView);

        mViewModel.setFeedType(feedType);
    }

    @Override
    public PagedListAdapter getAdapter() {
        feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(), feedType) {
            @Override
            public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
                if (holder.isVideoItem()) {
                    playDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
                playDetector.removeTarget(holder.getListPlayerView());
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        mRecyclerView.scrollToPosition(0);
                    }
                }
            }
        };
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

        System.out.println(AppConstant.LOG_HOME_FRAGMENT + "onLoadMore");

        final PagedList<Feed> currentList = adapter.getCurrentList();
        if (currentList == null || currentList.size() <= 0) {
            System.out.println(AppConstant.LOG_HOME_FRAGMENT + "onLoadMore: 无数据结束上拉加载");
            finishRefresh(false);
            return;
        }

        /**
         * 处理缓存的加载更多
         */
        Feed feed = currentList.get(adapter.getItemCount() - 1);
        System.out.println(AppConstant.LOG_HOME_FRAGMENT + "onLoadMore: loadAfter" + "FeedId:" + feed.id);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {
            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = currentList.getConfig();
                if (data != null && data.size() > 0) {
                    //这里 咱们手动接管 分页数据加载的时候 使用MutableItemKeyedDataSource也是可以的。
                    //由于当且仅当 paging不再帮我们分页的时候，我们才会接管。所以 就不需要ViewModel中创建的DataSource继续工作了，所以使用
                    //MutablePageKeyedDataSource也是可以的
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource();

                    //这里要把列表上已经显示的先添加到dataSource.data中
                    //而后把本次分页回来的数据再添加到dataSource.data中
                    dataSource.data.addAll(currentList);
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);

                    System.out.println(AppConstant.LOG_HOME_FRAGMENT + "onLoadMore: loadAfterCallBack:" + "currentListSize:" + currentList.size() + "\n" + "dataSize:" + data.size());
                    submitList(pagedList);
                }
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        //invalidate 之后Paging会重新创建一个DataSource 重新调用它的loadInitial方法加载初始化数据
        //详情见：LivePagedListBuilder#compute方法
        System.out.println(AppConstant.LOG_HOME_FRAGMENT + "onRefresh:");
        mViewModel.getDataSource().invalidate();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        playDetector.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * 沙发页面的几个子页面 复用了HomeFragment
         * 我们需要判断下 当前页面 它是否有ParentFragment
         * 当且仅当 它和它的ParentFragment均可见的时候 才能恢复视频播放
         */
        if (getParentFragment() != null) {
            if (getParentFragment().isVisible() && isVisible()) {
                playDetector.onResume();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}