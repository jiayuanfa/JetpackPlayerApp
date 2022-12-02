package com.example.libnetwork;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;

import com.example.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class Request<T, R extends Request> implements Cloneable {
    protected String mUrl;
    protected HashMap<String, String> headers = new HashMap<>();
    protected HashMap<String, Object> params = new HashMap<>();

    // 只访问本地缓存，即便本地缓存不存在，也不进行网络请求
    public static final int CACHE_ONLY = 1;
    // 先访问本地缓存，同时发起网络请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    // 仅仅只访问服务器，不进行任何网络请求
    public static final int NET_ONLY = 3;
    // 先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;

    private String cacheKey;
    private Type mType;
    private int mCacheStrategy = NET_ONLY;

    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_CACHE, NET_ONLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CacheStrategy {

    }

    public Request(String url) {
        mUrl = url;
    }

    public R addHeader(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }
    
    public R addParam(String key, Object value) {
        if (value == null) {
            return (R) this;
        }
        
        if (value.getClass() == String.class) {
            params.put(key, value);
        } else {
            Field field = null;
            try {
                field = value.getClass().getField("TYPE");
                Class claz = (Class) field.get(null);
                if (claz.isPrimitive()) {
                    params.put(key, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (R)this;
    }
    
    public R cacheStrategy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
        return (R)this;
    }
    
    public R cacheKey(String key) {
        this.cacheKey = key;
        return (R)this;
    }
    
    public R responseType(Type type) {
        mType = type;
        return (R)this;
    }
    
    public R responseType(Class claz) {
        mType = claz;
        return (R)this;
    }

    /**
     * 获取Call类
     * @return
     */
    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.okHttpClient.newCall(request);
        return call;
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 同步执行
     * @return
     */
    public ApiResponse<T> execute() {
        if (mType == null) {
            throw new RuntimeException("同步方法，response 返回值 类型必须设置");
        }

        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }

        if (mCacheStrategy != CACHE_ONLY) {
            ApiResponse<T> result;
            try {
                Response response = getCall().execute();
                result = parseResponse(response, null);
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                result = new ApiResponse<>();
                result.message = e.getMessage();
            }
        }
        return null;
    }

    /**
     * 异步执行
     * @param callback
     */
    @SuppressLint("RestrictedApi")
    public void execute(final JsonCallback callback) {
        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(() -> {
                ApiResponse<T> response = readCache();
                if (callback != null && response.body != null) {
                    callback.onCacheSuccess(response);
                }
            });
        }

        if (mCacheStrategy != CACHE_ONLY) {
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> result = new ApiResponse<>();
                    result.message = e.getMessage();
                    callback.onError(result);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> result = parseResponse(response, callback);
                    if (!result.success) {
                        callback.onError(result);
                    } else {
                        callback.onSuccess(result);
                    }
                }
            });
        }
    }

    /**
     * 通过缓存Key读取缓存
     * @return
     */
    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(cacheKey)? generateCacheKey() : cacheKey;
        Object cache = CacheManager.getCache(key);
        ApiResponse<T> result = new ApiResponse<>();
        result.status = 304;
        result.message = "缓存获取成功";
        result.body = (T) cache;
        result.success = true;
        return result;
    }

    /**
     * 解析返回的数据
     * @param response
     * @param callback
     * @return
     */
    private ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().string();
            if (success) {
                if (callback != null) {

                    /**
                     * 此处获取JsonCallback中的泛型，用于json转Model
                     */
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                } else {
                    Log.e("resuqest", "无法解析");
                }
            } else {
                message = content;
            }
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
            status = 0;
        }

        result.success = success;
        result.status = status;
        result.message = message;
        if (mCacheStrategy != NET_ONLY && result.success && result.body != null && result.body instanceof Serializable) {
            // 缓存
            saveCache(result.body);
        }
        return result;
    }

    /**
     * 缓存数据
     * @param body
     */
    private void saveCache(T body) {
        String key = TextUtils.isEmpty(cacheKey)? generateCacheKey(): cacheKey;
        CacheManager.save(key, body);
    }

    /**
     * 生成缓存的Key
     * @return
     */
    private String generateCacheKey() {
        cacheKey = UrlCreator.createUrlFromParams(mUrl, params);
        return cacheKey;
    }

    @NonNull
    @Override
    public Request<T, R> clone() throws CloneNotSupportedException {
        return (Request<T, R>)super.clone();
    }
}
