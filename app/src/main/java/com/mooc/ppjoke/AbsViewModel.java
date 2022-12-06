package com.mooc.ppjoke;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

/**
 * 抽象的ViewModel
 * 核心：使用PagedList配置数据分页加载
 * 1：数据源的设定
 * 2：pageData的获取
 * 3：边界数据，用于外界监听有无更多数据，以关闭上拉加载
 * @param <T>
 */
public abstract class AbsViewModel<T> extends ViewModel {

    // Paging配置
    protected PagedList.Config config;
    // 数据源
    private DataSource dataSource;
    // 依靠数据源拿到 PageData 以供 Paging框架使用
    private LiveData<PagedList<T>> pageData;
    // 边界Paging数据 用来
    private MutableLiveData<Boolean> boundaryPageData = new MutableLiveData<>();

    public AbsViewModel() {
        config = new PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(12)
                .build();

        pageData = new LivePagedListBuilder(factory, config)
                .setInitialLoadKey(0)
                .setBoundaryCallback(callback)
                .build();
    }

    public LiveData<PagedList<T>> getPageData() {
        return pageData;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public LiveData<Boolean> getBoundaryPageData() {
        return boundaryPageData;
    }

    //PagedList数据被加载 情况的边界回调callback
    //但 不是每一次分页 都会回调这里，具体请看 ContiguousPagedList#mReceiver#onPageResult
    //deferBoundaryCallbacks
    PagedList.BoundaryCallback<T> callback = new PagedList.BoundaryCallback<T>() {
        @Override
        public void onZeroItemsLoaded() {
            //新提交的PagedList中没有数据
            boundaryPageData.postValue(false);
        }

        @Override
        public void onItemAtFrontLoaded(@NonNull T itemAtFront) {
            //新提交的PagedList中第一条数据被加载到列表上
            boundaryPageData.postValue(true);
        }

        @Override
        public void onItemAtEndLoaded(@NonNull T itemAtEnd) {
            //新提交的PagedList中最后一条数据被加载到列表上
        }
    };

    DataSource.Factory factory = new DataSource.Factory() {
        @NonNull
        @Override
        public DataSource create() {
            if (dataSource == null || dataSource.isInvalid()) {
                dataSource = createDataSource();
            }
            return dataSource;
        }
    };

    public abstract DataSource createDataSource();


    //可以在这个方法里 做一些清理 的工作
    @Override
    protected void onCleared() {

    }
}
