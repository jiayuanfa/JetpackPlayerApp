package com.mooc.fageplayer.ui.home;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.mooc.fageplayer.ui.AbsViewModel;
import com.mooc.fageplayer.ui.MutablePageKeyedDataSource;
import com.mooc.fageplayer.model.Feed;
import com.mooc.fageplayer.ui.login.UserManager;
import com.mooc.fageplayer.utils.AppConstant;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.mooc.libnetwork.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends AbsViewModel<Feed> {

    // 是否使用缓存
    private volatile boolean witchCache = true;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();

    // 多线程安全 也就是原子访问 布尔判断是否另一个线程在执行代码，本线程等待
    private AtomicBoolean loadAfter = new AtomicBoolean(false);
    private String mFeedType;

    /**
     * 给父类数据源
     * @return
     */
    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    public void setFeedType(String feedType) {
        mFeedType = feedType;
    }

    /**
     * 数据源类的生成
     */
    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据的
            loadData(0, params.requestedLoadSize, callback);
            witchCache = false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            System.out.println(AppConstant.LOG_HOME_FRAGMENT + "loadAfter:" + "key:" + params.key);
            // 向后加载分页数据
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
            //能够向前加载数据的
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }

    /**
     * 真正去加载数据的接口处理
     * @param key
     * @param count
     * @param callback
     */
    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {

        // 正在处理
        if (key > 0) {
            loadAfter.set(true);
        }

        /**
         * feeds/queryHotFeedsList
         * 设置responseType
         */
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());


        /**
         * 获取缓存数据
         */
        if (witchCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {

                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource<Feed>();
                    dataSource.data.addAll(response.body);

                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    System.out.println(AppConstant.LOG_HOME_FRAGMENT + "loadDataCacheOnlyLog:" + "key:" + key + "dataSize:" + pagedList.size());
                    cacheLiveData.postValue(pagedList);
                }
            });
        }

        /**
         * 处理接口回调
         * 由于是在子线程，直接同步请求即可
         */
        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;
            callback.onResult(data);

            System.out.println(AppConstant.LOG_HOME_FRAGMENT + "loadDataLog:" + "key:" + key + "dataSize:" + data.size());

            if (key > 0) {
                // 通过BoundaryPageData发送数据 告诉UI层 是否应该主动关闭上拉加载分页的动画
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);

                // 接口请求完毕
                loadAfter.set(false);
            }

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {

        System.out.println(AppConstant.LOG_HOME_FRAGMENT + "loadAfter:" + "key:" + id);

        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }

        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id, config.pageSize, callback);
            }
        });
    }
}