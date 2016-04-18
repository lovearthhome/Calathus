package com.zky.articleproj.cache;

import android.util.LruCache;

import io.vov.vitamio.utils.Log;

/**
 * Created by zhaoliang on 16/4/17.
 */
public class CacheManager {

    private LruCache<String, Object> mLruCache;

    /* 对象 */
    public static CacheManager instance = new CacheManager();

    /**
     * private constructor
     */
    private CacheManager() {

        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;

        Log.e("-----------申请缓存内存大小:" + mCacheSize);
        //给LruCache分配1/8 4M
        mLruCache = new LruCache<String, Object>(mCacheSize);
    }

    /**
     * 获取缓存实例
     *
     * @return
     */
    public static CacheManager getInstance() {
        return instance;
    }

    /**
     * 向内存中添加缓存
     *
     * @param key
     * @param o
     */
    public void addToCache(String key, Object o) {
        if (mLruCache.get(key) == null && o != null) {
            mLruCache.put(key, o);
        }
    }

    /**
     * 从内存中读取数据
     *
     * @param key
     * @return
     */
    public Object getFromCache(String key) {
        return mLruCache.get(key);
    }
}
