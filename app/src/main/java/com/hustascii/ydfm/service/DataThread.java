package com.hustascii.ydfm.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;


import java.util.List;


/**
 * Created by JianGuo on 16/1/22.
 * Data Handler Thread for cache and read cache data.
 */
public abstract class DataThread<T extends List> extends HandlerThread {
    private final static String TAG = DataThread.class.getSimpleName();
    private final static int CACHE = 0;
    private final static int READ = 1;
    private JSONLruCache mCache;
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private boolean mReady;

    public abstract void onReadSuccess(T data);
    public abstract void onCacheSuccess();
    public abstract void cacheData(T data);
    public abstract T readData(String tag);


    public DataThread(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mCache = new JSONLruCache(5);
    }

    /**
     * Cache data
     * @param tag the data tag
     * @param data the data
     * @throws IllegalArgumentException if you call this method before {@link #prepareHandler()}
     */
    public void queueCache(String tag, T data) {
        if (!mReady) {
            throw new IllegalArgumentException("You should call prepareHandler first!");
        }
        mCache.put(tag, data);
        Log.i(TAG, tag + " added to the lru");
        mWorkerHandler.obtainMessage(CACHE, tag).sendToTarget();
    }

    /**
     * Read the cache
     * @param tag data tag
     * @throws IllegalArgumentException if you call this method before {@link #prepareHandler()}
     */
    public void readCache(String tag) {
        if (!mReady) {
            throw new IllegalArgumentException("You should call prepareHandler first!");
        }
        mWorkerHandler.obtainMessage(READ, tag).sendToTarget();
    }

    /**
     * Prepare Handler to work
     */
    public void prepareHandler() {

        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case CACHE:
                        String tag = (String) msg.obj;
                        Log.i(TAG, "caching " + mCache.get(tag));
                        cache(mCache.get(tag));
                        break;
                    case READ:
                        String data = (String) msg.obj;
                        Log.i(TAG, "reading from" + data);
                        read(data);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown message" + msg.toString());
                }
                return true;
            }
        });
        mReady = true;
    }

    private void read(final String data) {
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                onReadSuccess(readData(data));
            }
        });
    }

    public boolean ismReady() {
        return mReady;
    }

    private void cache(final T tag) {
        cacheData(tag);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                onCacheSuccess();
            }
        });
    }


    private class JSONLruCache extends LruCache<String, T> {


        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public JSONLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, T value) {

            return super.sizeOf(key, value);
        }
    }

    @Override
    public boolean quit() {
        Log.i(TAG, "thread quit");
        mReady = false;
        return super.quit();

    }
}
