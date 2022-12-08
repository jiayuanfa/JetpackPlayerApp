package com.mooc.fageplayer.exoplayer;

import android.app.Application;
import android.net.Uri;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.mooc.libcommon.AppGlobals;

import java.util.HashMap;

/**
 * 播放管理器
 * 为每一个页面添加一个播放器
 * 方便没管理每个页面的暂停/恢复操作
 */
public class PageListPlayManager {
    private static HashMap<String, PageListPlay> sPageListPlayHashMap = new HashMap<>();
    private static final ProgressiveMediaSource.Factory mediaSourceFactory;

    static {
        Application application = AppGlobals.getApplication();
        // 创建Http视频资源如何加载的工厂对象
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(application, application.getPackageName()));
        // 创建缓存、指定缓存位置、策略，为最近最少使用原则，最大200M
        Cache cache = new SimpleCache(application.getCacheDir(), new LeastRecentlyUsedCacheEvictor(1024*1024*200));
        // 把缓存对象cache和负责缓存数据读取、写入的工厂类CacheDataSinkFactory进行关联
        CacheDataSinkFactory cacheDataSinkFactory = new CacheDataSinkFactory(cache, Long.MAX_VALUE);

        /**
         * 创建能够 边播变缓存的 本地资源加载和http网络数据写入的工厂类
         * cache 缓存写入策略和缓存写入位置的对象
         */
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                cache,
                dataSourceFactory,
                new FileDataSourceFactory(),
                cacheDataSinkFactory,
                CacheDataSource.FLAG_BLOCK_ON_CACHE,
                null
        );

        // 最后 创建一个 MediaSource 媒体资源 加载的工厂类
        // 因为由它创建的MediaSource能够实现缓冲边播放的功能
        // 如果需要播放hls，m3u8 则需要创建DashMediaSource.Factory()
        mediaSourceFactory = new ProgressiveMediaSource.Factory(cacheDataSourceFactory);
    }

    public static MediaSource createMediaSource(String url) {
        return mediaSourceFactory.createMediaSource(Uri.parse(url));
    }

    public static PageListPlay get(String pageName) {
        PageListPlay pageListPlay = sPageListPlayHashMap.get(pageName);
        if (pageListPlay == null) {
            pageListPlay = new PageListPlay();
            sPageListPlayHashMap.put(pageName, pageListPlay);
        }
        return pageListPlay;
    }

    public static void release(String pageName) {
        PageListPlay pageListPlay = sPageListPlayHashMap.remove(pageName);
        if (pageListPlay != null) {
            pageListPlay.release();
        }
    }
}
