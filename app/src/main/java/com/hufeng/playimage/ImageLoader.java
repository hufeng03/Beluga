package com.hufeng.playimage;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import com.hufeng.filemanager.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public class ImageLoader implements Callback {

    private static ImageLoader INSTANCE;

    public static final String THUMBNAIL_PREFIX = "Thumb://";

    public synchronized static ImageLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ImageLoader();
        }
        return INSTANCE;
    }

    private MemoryCache mCache;
    TaskManager mTaskManager;
    Handler mHandler;

    private HashMap<String, HashSet<WeakReference<BaseLazyLoadImageView>>> mImageUriMap;

    public static final int BITMAP_LOADED = 1000;

    private ImageLoader() {
        mCache = MemoryCache.getInstance();
        mImageUriMap = new HashMap<String, HashSet<WeakReference<BaseLazyLoadImageView>>>();
        mHandler = new Handler(this);
        mTaskManager = new TaskManager(mHandler);
    }

    private void cacheImageViewWithUri(BaseLazyLoadImageView img, String uri) {
        synchronized (mImageUriMap) {
            if (mImageUriMap.containsKey(uri)) {
                HashSet<WeakReference<BaseLazyLoadImageView>> set = mImageUriMap
                        .get(uri);
                boolean existImageView = false;
                for (Iterator<WeakReference<BaseLazyLoadImageView>> i = set
                        .iterator(); i.hasNext();) {
                    WeakReference<BaseLazyLoadImageView> imageViewReference = i
                            .next();
                    BaseLazyLoadImageView imageView = imageViewReference.get();
                    if (imageView == null) {
                        i.remove();
                        continue;
                    }

                    if (img == imageView) {
                        existImageView = true;
                        break;
                    }
                }

                if (!existImageView) {
                    set.add(new WeakReference<BaseLazyLoadImageView>(img));
                }
            } else {
                HashSet<WeakReference<BaseLazyLoadImageView>> set = new HashSet<WeakReference<BaseLazyLoadImageView>>();
                set.add(new WeakReference<BaseLazyLoadImageView>(img));
                mImageUriMap.put(uri, set);
            }
        }
    }

    public void displayImage(BaseLazyLoadImageView img, String uri) {
        mHandler.removeCallbacks(mPurgeCache);
        if (displayImageFromCache(img, uri)) {
            return;
        }

        cacheImageViewWithUri(img, uri);
        img.useDefaultResource();
        // use task to load the bitmap
        mTaskManager.startLoad(img.getContext().getApplicationContext(), uri);
    }

    private boolean displayImageFromCache(BaseLazyLoadImageView img, String uri) {
        Bitmap bitmap = mCache.getBitmap(uri);
        if (bitmap == null) {
            if (LogUtil.DDBG) {
                LogUtil.d("ImageLoader", "uri not find:" + uri);
            }
        } else {
            if (LogUtil.DDBG) {
                LogUtil.d("ImageLoader", "uri found, display bitmap from cache:" + uri);
            }
            img.setImageBitmap(bitmap, uri);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case BITMAP_LOADED:
                precessBitmapLoaded(msg);
                return true;
        }
        return false;
    }

    private void precessBitmapLoaded(Message msg) {
        Bitmap bitmap = (Bitmap) msg.obj;
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        String uri = msg.getData().getString("uri");
        boolean flag = false;
        synchronized (mImageUriMap) {
            if (mImageUriMap.containsKey(uri)) {
                HashSet<WeakReference<BaseLazyLoadImageView>> set = mImageUriMap
                        .get(uri);
                for (Iterator<WeakReference<BaseLazyLoadImageView>> i = set
                        .iterator(); i.hasNext();) {
                    WeakReference<BaseLazyLoadImageView> imageViewReference = i
                            .next();
                    BaseLazyLoadImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        if(imageView.setImageBitmapIfNeeds(bitmap, uri)){
                            flag = true;
                        }
                    }
                    i.remove();
                }
                if (set.isEmpty()) {
                    mImageUriMap.remove(uri);
                }
            }
        }

        if(flag){
            mCache.putBitmap(uri, bitmap);
        }else{
            bitmap.recycle();
        }
    }

    private Runnable mPurgeCache = new Runnable(){
        public void run(){
            if(LogUtil.DDBG){
                LogUtil.d("ImageLoader", "clean up the memory");
            }
            mCache.recycle();
            mTaskManager.cancelAllTask();
            mImageUriMap.clear();
        }
    };

    public void cleanup() {
        mHandler.postDelayed(mPurgeCache, 5000);
    }

    public void onLowMemory() {
        Log.e("ImageLoader", "onLowMemory");
        // TODO low memory cleanup
    }
}

