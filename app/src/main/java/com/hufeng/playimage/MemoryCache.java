package com.hufeng.playimage;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.hufeng.filemanager.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.HashSet;


public class MemoryCache extends LruCache<String, Bitmap> {

    static final String TAG = "MemoryCache";
    static final int MAX_SIZE = 400;

    HashSet<WeakReference<Bitmap>> mCachedBitmaps;

    private static MemoryCache INSTANCE;

    public static MemoryCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MemoryCache();
        }
        return INSTANCE;
    }

    static final int CACHE_SIZE;
    static final int MIN_MEMORY_SIZE = 4 * 1024 * 1024; //4 M
    static {
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024) / 16;
//		CACHE_SIZE = maxMemory < MIN_MEMORY_SIZE ? MIN_MEMORY_SIZE : maxMemory;
        CACHE_SIZE = MIN_MEMORY_SIZE;

    }

    private MemoryCache() {
        super(CACHE_SIZE);
        mCachedBitmaps = new HashSet<WeakReference<Bitmap>>();
    }

    @SuppressLint("NewApi")
    @Override
    protected int sizeOf(String key, Bitmap value) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return value.getWidth() * value.getHeight() * 4;
        } else {
            return value.getByteCount();
        }
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap = get(url);
        if (bitmap != null) {
            if (LogUtil.DDBG) {
                LogUtil.d(TAG, "cache hit:" + url);
            }
        } else {
            if (LogUtil.DDBG) {
                LogUtil.d(TAG, "cache not hit!");
            }
        }
        return bitmap;
    }

    static boolean isBimapShouldCache(Bitmap bitmap) {
        //缓存限制放宽
//		if (bitmap.getWidth() > MAX_SIZE || bitmap.getHeight() > MAX_SIZE) {
//			return false;
//		}
        return true;
    }

    public void putBitmap(String url, Bitmap bitmap) {
        if (isBimapShouldCache(bitmap)) {
            if (LogUtil.DDBG) {
                LogUtil.d(TAG, "putBitmap for url="+url);
            }
            put(url, bitmap);
        } else if (LogUtil.DDBG) {
            LogUtil.w(
                    TAG, "cache bitmap ignored!!! bitmap size is:	"
                            + bitmap.getWidth() + "*" + bitmap.getHeight());
        }
        if (LogUtil.DDBG) {
            LogUtil.d(TAG, "cache:" + url);
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

