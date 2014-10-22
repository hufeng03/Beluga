package com.hufeng.filemanager.resource;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.LogUtil;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by feng on 13-9-29.
 */
public class ResourceIconLoader implements Handler.Callback{

    private static final String LOG_TAG = ResourceIconLoader.class.getSimpleName();

    private static final String LOADER_THREAD_NAME = "GameIconLoader";

    /**
     * Type of message sent by the UI thread to itself to indicate that some
     * photos need to be loaded.
     */
    private static final int MESSAGE_REQUEST_LOADING = 1;

    /**
     * Type of message sent by the loader thread to indicate that some photos
     * have been loaded.
     */
    private static final int MESSAGE_ICON_LOADED = 2;

    private static class BitmapHolder {

        public static final int NEEDED = 0;

        public static final int LOADING = 1;

        public static final int LOADED = 2;

        int state;

        SoftReference<Drawable> bitmapRef;

        public boolean setBitmapForImageView(ImageView v) {
            if (bitmapRef.get() == null)
                return false;
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            v.setImageDrawable(bitmapRef.get());
            return true;
        }

        public boolean isNull() {
            return bitmapRef == null;
        }

    }

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final String[] COLUMNS = new String[] { DataStructures.SelectedColumns.ICON_FIELD,
            DataStructures.SelectedColumns.PHOTO_FIELD};

    /**
     * A soft cache for image thumbnails. the key is file path
     */
    private final static ConcurrentHashMap<String, BitmapHolder> mBitmapCache = new ConcurrentHashMap<String, BitmapHolder>();

    /**
     * A map from ImageView to the corresponding photo ID. Please note that this
     * photo ID may change before the photo loading request is started.
     */
    private final ConcurrentHashMap<ImageView, String> mPendingRequests = new ConcurrentHashMap<ImageView, String>();
    private final ConcurrentHashMap<ImageView, String> mPendingServerRequests = new ConcurrentHashMap<ImageView, String>();

    /**
     * Handler for messages sent to the UI thread.
     */
    private final Handler mMainThreadHandler = new Handler(this);

    /**
     * Thread responsible for loading photos from the database. Created upon the
     * first request.
     */
    private LoaderThread mLoaderThread;

    /**
     * A gate to make sure we only send one instance of MESSAGE_PHOTOS_NEEDED at
     * a time.
     */
    private boolean mLoadingRequested;

    /**
     * Flag indicating if the image loading is paused.
     */
    private boolean mPaused;

    private final Context mContext;

    private final ContentResolver mResolver;

    private static ResourceIconLoader instance = null;

    public static ResourceIconLoader getInstance()
    {
        if(instance==null)
            instance = new ResourceIconLoader(FileManager.getAppContext());
        return instance;
    }

    /**
     * Constructor.
     *
     * @param context content context
     */
    private ResourceIconLoader(Context context) {
        mContext = context;
        mResolver = mContext.getContentResolver();
    }

    public void remove(ImageView view){
        mPendingRequests.remove(view);
    }

    public boolean loadIcon(ImageView view, RelativeLayout background, LinearLayout detail, String path)
    {
        boolean loaded;
        if(background!=null) {
            background.setBackgroundResource(R.drawable.grid_item_image_bg);
        }
        if(detail!=null) {
            detail.setVisibility(View.VISIBLE);
        }
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        view.setImageResource(IconLoaderHelper.getFileIcon(FileUtils.FILE_TYPE_APK));
        loaded = loadCachedIcon(view, path);
        if (loaded) {
            mPendingRequests.remove(view);
        } else {
            mPendingRequests.put(view, path);
//            String url = mPendingServerRequests.get(view);
//            if(url!=null && !path.equals(url)) {
//                mPendingServerRequests.remove(view);
//            }
            if (!mPaused) {
                // Send a request to start loading photos
                requestLoading();
            }
        }
        return loaded;
    }


    /**
     * Stops loading images, kills the image loader thread and clears all
     * caches.
     */
    public void stop() {
        pause();

        if (mLoaderThread != null) {
            mLoaderThread.quit();
            mLoaderThread = null;
        }

        clear();
    }

    public void clear() {
        mPendingRequests.clear();
        mBitmapCache.clear();
    }

    /**
     * Temporarily stops loading
     */
    public void pause() {
        mPaused = true;
    }

    /**
     * Resumes loading
     */
    public void resume() {
        mPaused = false;
        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    private boolean loadCachedIcon(ImageView view, String path) {
        BitmapHolder holder = mBitmapCache.get(path);

        if (holder == null) {
            holder = new BitmapHolder();
            mBitmapCache.put(path, holder);
        } else if (holder.state == BitmapHolder.LOADED) {
            if (holder.isNull()) {
                return true;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if (holder.setBitmapForImageView(view)) {
                return true;
            }
        }

        holder.state = BitmapHolder.NEEDED;
        return false;
    }

    /**
     * Sends a message to this thread itself to start loading images. If the
     * current view contains multiple image views, all of those image views will
     * get a chance to request their respective photos before any of those
     * requests are executed. This allows us to load images in bulk.
     */
    private void requestLoading() {
        if (!mLoadingRequested) {
            mLoadingRequested = true;
            mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
        }
    }

    /**
     * Processes requests on the main thread.
     */
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_REQUEST_LOADING: {
                mLoadingRequested = false;
                if (!mPaused) {
                    if (mLoaderThread == null) {
                        mLoaderThread = new LoaderThread(
                                mContext.getContentResolver());
                        mLoaderThread.start();
                    }

                    mLoaderThread.requestLoading();
                }
                return true;
            }

            case MESSAGE_ICON_LOADED: {
                if (!mPaused) {
                    processLoadedIcons();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Goes over pending loading requests and displays loaded photos. If some of
     * the photos still haven't been loaded, sends another request for image
     * loading.
     */
    private void processLoadedIcons() {
        Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            ImageView view = iterator.next();
            String path = mPendingRequests.get(view);
            if(path==null)
                continue;
            boolean loaded = loadCachedIcon(view, path);
            if (loaded) {
                iterator.remove();
//	                iconLoadListener.onIconLoadFinished(view);
            }
        }

        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }


    /**
     * Stores the supplied bitmap in cache.
     */
    private boolean cacheBitmap(String path, byte[] bytes) {
        if (mPaused) {
            return false;
        }

        BitmapHolder holder = new BitmapHolder();
        holder.state = BitmapHolder.LOADED;
        if (bytes != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                        bytes.length, null);
                if (bitmap != null) {
                    Drawable drawable = new BitmapDrawable(
                            mContext.getResources(), bitmap);
                    holder.bitmapRef = new SoftReference<Drawable>(drawable);
                    // } else {
                    // holder.bitmapRef = new SoftReference<Drawable>(new
                    // BitmapDrawable(mContext
                    // .getResources(),
                    // bitmap));
                    // }
                } else {
                    // 为null时在设置时是使用默认的,这里不需要赋值@dangweifeng 2013-05-08
                    holder.bitmapRef = null;
                }
            } catch (OutOfMemoryError e) {
                // Do nothing - the photo will appear to be missing
                mBitmapCache.put(path, holder);
                return true;
            } catch (Exception e) {
                mBitmapCache.put(path, holder);
                return true;
            }
        }
        mBitmapCache.put(path, holder);
        return bytes != null;
    }

    /**
     * Populates an array of photo IDs that need to be loaded.
     */
    private void obtainUrlsToLoad(ArrayList<String> urls) {
        urls.clear();

		/*
		 * Since the call is made from the loader thread, the map could be
		 * changing during the iteration. That's not really a problem:
		 * ConcurrentHashMap will allow those changes to happen without throwing
		 * exceptions. Since we may miss some requests in the situation of
		 * concurrent change, we will need to check the map again once loading
		 * is complete.
		 */

        Iterator<String> iterator = mPendingRequests.values().iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            BitmapHolder holder = mBitmapCache.get(path);
            if (holder != null && holder.state == BitmapHolder.NEEDED) {
                // Assuming atomic behavior
                holder.state = BitmapHolder.LOADING;
                urls.add(path);
            }
        }
    }




    /**
     * The thread that performs loading of photos from the database.
     */
    private class LoaderThread extends HandlerThread implements Handler.Callback {
        private Handler mLoaderThreadHandler;
        private final ContentResolver mResolver;
        private final ArrayList<String> mUrls = new ArrayList<String>();
        private final StringBuilder mStringBuilder = new StringBuilder();

        public LoaderThread(ContentResolver mResolver) {
            super(LOADER_THREAD_NAME);
            this.mResolver = mResolver;
            this.setPriority(MIN_PRIORITY);
        }

        /**
         * Sends a message to this thread to load requested photos.
         */
        public void requestLoading() {
            if (mLoaderThreadHandler == null) {
                mLoaderThreadHandler = new Handler(getLooper(), this);
            }
            mLoaderThreadHandler.sendEmptyMessage(0);
        }

        /**
         * Receives the above message, loads photos and then sends a message to
         * the main thread to process them.
         */
        public boolean handleMessage(Message msg) {
            loadIconsFromDatabase();
            mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
            return true;
        }


        public void loadIconsFromDatabase() {
            obtainUrlsToLoad(mUrls);
            int count = mUrls.size();
            mStringBuilder.setLength(0);
            mStringBuilder.append(DataStructures.SelectedColumns.ICON_FIELD + " IN(");
            for (int i = 0; i < count; i++) {
                if (i != 0) {
                    mStringBuilder.append(',');
                }
                mStringBuilder.append('?');
            }
            mStringBuilder.append(')');


            Cursor cursor = null;
            try {
                cursor = mResolver
                        .query(DataStructures.SelectedColumns.CONTENT_URI,
                                COLUMNS,
                                mStringBuilder.toString(),
                                mUrls.toArray(EMPTY_STRING_ARRAY), null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(0);
                        byte[] bytes = cursor
                                .getBlob(1);
                        if (cacheBitmap(path, bytes)) {
                            mUrls.remove(path);
                        } else {
                            // load from server in a sync Thread, so must delete
                            // load request.will reload after save in database;
                            if (!TextUtils.isEmpty(path)) {
                                boolean flag = hasServerRequest(path);
                                removeServerRequest(path);
                                mUrls.remove(path);
                                if(!flag) {
                                    loadPhotoFromServer(mResolver, path);
                                }
                            }
                        }
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public boolean hasServerRequest(String url) {
        Iterator<Map.Entry<ImageView, String>> iterator = mPendingServerRequests.entrySet()
                .iterator();
        boolean flag = false;
        while (iterator.hasNext()) {
            Map.Entry<ImageView, String> entry = iterator.next();
            if (entry.getValue().equals(url)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void removeServerRequest(String url) {
        Iterator<Map.Entry<ImageView, String>> iterator = mPendingRequests.entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Map.Entry<ImageView, String> entry = iterator.next();
            if (entry.getValue().equals(url)) {
                mPendingRequests.remove(entry.getKey());
                mPendingServerRequests.put(entry.getKey(), url);
            }
        }
    }

    public int putServerRequest(String url) {
        int count = 0;
        Iterator<Map.Entry<ImageView, String>> iterator = mPendingServerRequests
                .entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ImageView, String> entry = iterator.next();
            if (entry.getValue().equals(url)) {
                ImageView key = entry.getKey();
                BitmapHolder holder = mBitmapCache.get(key);
                if (holder == null) {
                    holder = new BitmapHolder();
                    holder.state = BitmapHolder.NEEDED;
                    mBitmapCache.put(url, holder);
                } else {
                    holder.state = BitmapHolder.NEEDED;
                }
                mPendingServerRequests.remove(key);
                mPendingRequests.put(key, url);
                count++;
            }
        }
        return count;
    }

    /**
     * load photo from server with url, the lowest priority
     *
     * @author gaozefeng
     */
    @TargetApi(11)
    public void loadPhotoFromServer(ContentResolver resolver, String url) {
        if (mPaused) {
            return;
        }
        if (url!=null) {
            ResourceIconDownloader task = new ResourceIconDownloader(this, url);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                task.execute();
            } else {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }


    public void iconDownloaded(String url, byte[] data){
        LogUtil.i(LOG_TAG, "iconDownloaded for " + url + (data == null ? "null" : data));
        if (data == null || TextUtils.isEmpty(url)) {
            return;
        }
        final int result = saveIconIntoDb(url, data);
        mBitmapCache.remove(url);
        if (result > 0 && putServerRequest(url) > 0) {
            requestLoading();
        }
    }

    private int saveIconIntoDb(String url, byte[] data) {

        ContentValues values = new ContentValues();
        values.put(DataStructures.SelectedColumns.PHOTO_FIELD, data);
        int result = mResolver.update(DataStructures.SelectedColumns.CONTENT_URI, values,
            DataStructures.SelectedColumns.ICON_FIELD + "=?", new String[]{url});
        return result;
    }




}
