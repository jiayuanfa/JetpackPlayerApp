package com.mooc.fageplayer;

import android.annotation.SuppressLint;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.mooc.fageplayer.model.Feed;
import com.mooc.fageplayer.model.User;
import com.mooc.fageplayer.ui.login.UserManager;
import com.mooc.libcommon.AppGlobals;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;

/**
 * 接口点赞修改数据
 */
public class InteractionPresenter {

    public static final String DATA_FROM_INTERACTION = "data_from_interaction";

    private static final String URL_TOGGLE_FEED_LIK = "/ugc/toggleFeedLike";

    private static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";

    private static final String URL_SHARE = "/ugc/increaseShareCount";

    private static final String URL_TOGGLE_COMMENT_LIKE = "/ugc/toggleCommentLike";

    /**
     * 给一个帖子点赞
     * 和踩一踩互斥
     *
     * @param owner
     * @param feed
     */
    public static void toggleFeedLike(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedLikeInternal(feed);
            }
        })) {

        } else {
            toggleFeedLikeInternal(feed);
        }
    }

    /**
     * 给一个帖子踩一踩
     * 和点赞是互斥的
     *
     * @param owner
     * @param feed
     */
    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedDissInternal(feed);
            }
        })) {

        } else {
            toggleFeedDissInternal(feed);
        }
    }

    /**
     * 调用帖子踩一踩接口
     * 成功了设置给Model
     *
     * @param feed
     */
    private static void toggleFeedDissInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_DISS)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked");
                            feed.getUgc().setHasdiss(hasLiked);
                        }
                    }

                    @Override
                    public void onError(ApiResponse response) {
                        showToast(response.message);
                    }
                });
    }

    /**
     * 调用点赞接口
     * 成功了设置给Model
     *
     * @param feed
     */
    private static void toggleFeedLikeInternal(Feed feed) {
        //        http://123.56.232.18:8080/serverdemo/ugc/toggleFeedLike?itemId=1669860034454&userId=1669859900
        ApiService.get(URL_TOGGLE_FEED_LIK)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked");
                            feed.getUgc().setHasLiked(hasLiked);
                        }
                    }

                    @Override
                    public void onError(ApiResponse response) {
                        showToast(response.message);
                    }
                });
    }

    /**
     * 会主线程弹出Toast的方法
     *
     * @param message
     */
    @SuppressLint("RestrictedApi")
    private static void showToast(String message) {
        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppGlobals.getApplication(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 判断登录状态
     *
     * @param owner
     * @param observer
     * @return
     */
    private static boolean isLogin(LifecycleOwner owner, Observer<User> observer) {
        if (UserManager.get().isLogin()) {
            return true;
        } else {
            LiveData<User> liveData = UserManager.get().login(AppGlobals.getApplication());
            if (owner == null) {
                liveData.observeForever(loginObserver(observer, liveData));
            } else {
                liveData.observe(owner, loginObserver(observer, liveData));
            }
            return false;
        }
    }

    /**
     * 监听登录成功过之后的LiveData是否发生改变
     * 发生改变之后
     * 移除监听者
     * 并把数据的变化传给监听者
     *
     * @param observer
     * @param liveData
     * @return
     */
    @NonNull
    private static Observer<User> loginObserver(Observer<User> observer, LiveData<User> liveData) {
        return new Observer<User>() {
            @Override
            public void onChanged(User user) {
                liveData.removeObserver(this);
                if (user != null && observer != null) {
                    observer.onChanged(user);
                }
            }
        };
    }
}
