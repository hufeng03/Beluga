package com.hufeng.filemanager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.hufeng.filemanager.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.HashSet;

/**
 * Created by Feng Hu on 15-02-04.
 * <p/>
 * TODO: Add a class header comment.
 */
public class MemoryCache extends LruCache<String, Bitmap> {

    static final String TAG = "MemoryCache";

    HashSet<WeakReference<Bitmap>> mCachedBitmaps;

    private static MemoryCache INSTANCE;

    public static MemoryCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryCache();
        }
        return INSTANCE;
    }

    static final int CACHE_SIZE;
    static final int LIMIT_SIZE;
    static final int MIN_MEMORY_SIZE = 4 * 1024; //Size of cache is 4 Mega bytes
//    static final int MAX_SINGLE_SIZE = 512; //If a bitmap is large that 512k, we do not cache it.
    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
		CACHE_SIZE = maxMemory < MIN_MEMORY_SIZE ? MIN_MEMORY_SIZE : maxMemory;
        LIMIT_SIZE = CACHE_SIZE / 8;
        if (LogUtil.DDBG) {
            LogUtil.d(TAG, "MemoryCache size is: " + CACHE_SIZE);
        }

    }

    public MemoryCache() {
        super(CACHE_SIZE);
        mCachedBitmaps = new HashSet<WeakReference<Bitmap>>();
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return getBitmapSize(value);
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap = get(url);
        if (bitmap != null) {
            if (LogUtil.DDBG) {
                LogUtil.d(TAG, "cache hit:" + url+", "+size());
            }
        } else {
            if (LogUtil.DDBG) {
                LogUtil.d(TAG, "cache miss:" + url+", "+size());
            }
        }
        return bitmap;
    }

    @SuppressLint("NewApi")
    private int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getWidth() * bitmap.getHeight() * 4 / 1024;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return bitmap.getByteCount() / 1024;
        } else {
            return bitmap.getAllocationByteCount() / 1024;
        }
    }

    private boolean isBimapShouldCache(Bitmap bitmap) {
		if (getBitmapSize(bitmap) > LIMIT_SIZE) {
			return false;
		}
        return true;
    }

    public void putBitmap(String url, Bitmap bitmap) {
        if (isBimapShouldCache(bitmap)) {
            put(url, bitmap);
            if (LogUtil.DDBG) {
                LogUtil.d(TAG, "cache put:" + url+", "+size());
            }
        } else  {
            if (LogUtil.DDBG) {
                LogUtil.d(
                        TAG, "cache ignore:	" + url
                               +"(" + bitmap.getWidth() + "*" + bitmap.getHeight()+")"+", "+size());
            }
        }

        mCachedBitmaps.add(new WeakReference<Bitmap>(bitmap));
    }

    public void recycle() {
        evictAll();
        HashSet<WeakReference<Bitmap>> bitmaps = mCachedBitmaps;
        for (WeakReference<Bitmap> bitmapReference : bitmaps) {
            Bitmap bitmap = bitmapReference.get();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        bitmaps.clear();
    }
}

