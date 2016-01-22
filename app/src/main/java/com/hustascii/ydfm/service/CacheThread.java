package com.hustascii.ydfm.service;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import com.hustascii.ydfm.util.FileUtils;


/**
 * Created by JianGuo on 16/1/22.
 */
public class CacheThread extends HandlerThread {
    private final static String TAG = CacheThread.class.getSimpleName();
    private final static int CACHE = 0;
    private final static int READ = 1;
    private JSONLruCache mCache;
    private Context mContext;
    private Handler mWorkerHandler;
    private Handler mResponseHandler;
    private Callback mCallback;
    public interface Callback {
        void onReadSuccess();
        void onCacheSuccess(String name);
    }

    public CacheThread(Context context, Handler responseHandler, Callback callback) {
        super(TAG);
        mResponseHandler = responseHandler;
        mCallback = callback;
        mContext = context;
        mCache = new JSONLruCache(5);
    }

    public void queueCache(String tag, String data) {
        mCache.put(tag, data);
        Log.i(TAG, tag + " added to the lru");
        mWorkerHandler.obtainMessage(CACHE, tag).sendToTarget();
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case CACHE:
                        String tag = (String) msg.obj;
                        Log.i(TAG, "caching " + mCache.get(tag));
                        cache(tag);
                        break;
                    case READ:

                        break;
                    default:
                        throw new IllegalArgumentException("Unknown message" + msg.toString());
                }
                return true;
            }
        });
    }

    private void cache(final String tag) {
        FileUtils.writeStringToFileCache(mContext, tag, mCache.get(tag));
        mCache.remove(tag);
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onCacheSuccess(tag);
            }
        });
    }


    private class JSONLruCache extends LruCache<String, String> {


        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *                the maximum number of entries in the cache. For all other caches,
         *                this is the maximum sum of the sizes of the entries in this cache.
         */
        public JSONLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, String value) {

            return super.sizeOf(key, value);
        }
    }
}
