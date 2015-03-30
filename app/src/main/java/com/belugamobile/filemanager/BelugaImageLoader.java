package com.belugamobile.filemanager;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.belugamobile.filemanager.utils.LogUtil;
import com.belugamobile.playimage.BaseLazyLoadImageView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Feng Hu on 15-02-04.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaImageLoader implements Handler.Callback{

    private static final String TAG = "ImageLoader";

    private MemoryCache mCache;
    TaskManager mTaskManager;
    Handler mHandler;

    public static final int BITMAP_LOADED = 1000;

    private HashMap<String, HashSet<WeakReference<BaseLazyLoadImageView>>> mUrlImageMap;

    private static BelugaImageLoader INSTANCE;

    public synchronized static BelugaImageLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BelugaImageLoader();
        }
        return INSTANCE;
    }


    private BelugaImageLoader() {
        mCache = new MemoryCache();
        mUrlImageMap = new HashMap<String, HashSet<WeakReference<BaseLazyLoadImageView>>>();
        mHandler = new Handler(this);
        mTaskManager = new TaskManager(mHandler);
    }

    public MemoryCache getMemoryCache() {
        return mCache;
    }

    private void cacheImageViewWithUrl(BaseLazyLoadImageView img, String url) {
        synchronized (mUrlImageMap) {
            if (mUrlImageMap.containsKey(url)) {
                HashSet<WeakReference<BaseLazyLoadImageView>> set = mUrlImageMap
                        .get(url);
                boolean existImageView = false;
                for (Iterator<WeakReference<BaseLazyLoadImageView>> i = set
                        .iterator(); i.hasNext();) {
                    WeakReference<BaseLazyLoadImageView> imageViewReference = i
                            .next();
                    ImageView imageView = imageViewReference.get();
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
                mUrlImageMap.put(url, set);
            }
        }
    }

    public void displayImage(BaseLazyLoadImageView imageView, String url) {
        mHandler.removeCallbacks(mPurgeCache);
        if (displayImageFromCache(imageView, url)) {
            return;
        }
        cacheImageViewWithUrl(imageView, url);
        imageView.useDefaultResource();
        mTaskManager.startLoad(url);
    }

    private boolean displayImageFromCache(BaseLazyLoadImageView img, String url) {
        Bitmap bitmap = mCache.getBitmap(url);

        if (bitmap != null) {
            img.setImageBitmap(bitmap, url);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case BITMAP_LOADED:
                if (LogUtil.DDBG) {
                    String url = msg.getData().getString("url");
                    Log.d(TAG, "load image success: " + url);
                }
                processBitmapLoadedSuccess(msg);
                return true;
        }
        return false;
    }

    private void processBitmapLoadedSuccess(Message msg) {
        Bitmap bitmap = (Bitmap) msg.obj;
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        String url = msg.getData().getString("url");
        boolean isBitmapUsed = false;
        synchronized (mUrlImageMap) {
            if (mUrlImageMap.containsKey(url)) {
                HashSet<WeakReference<BaseLazyLoadImageView>> set = mUrlImageMap
                        .get(url);
                for (Iterator<WeakReference<BaseLazyLoadImageView>> i = set
                        .iterator(); i.hasNext();) {
                    WeakReference<BaseLazyLoadImageView> imageViewReference = i
                            .next();
                    BaseLazyLoadImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        if(imageView.setImageBitmapIfNeeds(bitmap, url)){
                            isBitmapUsed = true;
                        } else {
                            //this image view has been reused to load other url
                        }
                    }
                }
                mUrlImageMap.remove(url);
            }
        }

        if(isBitmapUsed){
            mCache.putBitmap(url, bitmap);
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
            mUrlImageMap.clear();
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
