package com.hufeng.filemanager.browser;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hufeng.filemanager.FileManager;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by feng on 14-1-5.
 */
public class ApkInfoLoader implements Handler.Callback {

    private static final String LOADER_THREAD_NAME = "ApkInfoLoader";

    /**
     * Type of message sent by the UI thread to itself to indicate that some
     * photos need to be loaded.
     */
    private static final int MESSAGE_REQUEST_LOADING = 1;

    /**
     * Type of message sent by the loader thread to indicate that some photos
     * have been loaded.
     */
    private static final int MESSAGE_INFO_LOADED = 2;

    private static class TextHolder {
        public static final int NEEDED = 0;

        public static final int LOADING = 1;

        public static final int LOADED = 2;

        int state;
        String text;

        public TextHolder(){
            state = NEEDED;
            text = null;
        }

        public void setText(String t)
        {
            text = t;
        }

        public boolean setTextView(TextView v)
        {
            if(isNull())
            {
                v.setText("");
                return false;
            }
            else{
                v.setText(text);
                return true;
            }
        }

        public boolean isNull()
        {
            return text==null;
        }

    }

    public static class FileId {
        public String mPath;

        public long mId; // database id

        public int mCategory;

        public FileId(String path, long id) {
            mPath = path;
            mId = id;
        }
    }

    /**
     * A soft cache for file information string. the key is file path
     */
    private final static ConcurrentHashMap<String, TextHolder> mTextCache = new ConcurrentHashMap<String, TextHolder>();

    /**
     * A map from TextView to the corresponding File ID. Please note that this
     * photo ID may change before the photo loading request is started.
     */
    private final ConcurrentHashMap<TextView, FileId> mPendingRequests = new ConcurrentHashMap<TextView, FileId>();

    /**
     * Handler for messages sent to the UI thread.
     */
    private final Handler mMainThreadHandler = new Handler(this);

    /**
     * Thread responsible for loading informations from the database. Created upon the
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

    private static ApkInfoLoader instance = null;

    public static ApkInfoLoader getInstance()
    {
        if(instance==null)
            instance = new ApkInfoLoader(FileManager.getAppContext());
        return instance;
    }

    /**
     * Constructor.
     *
     * @param context content context
     */
    private ApkInfoLoader(Context context) {
        mContext = context;
    }

    public void remove(TextView view){
        mPendingRequests.remove(view);
    }

    public boolean loadInfo(TextView name, String path)
    {
        boolean loaded = false;
        int cate = 0;
        name.setText("");
        if(path.equals("to_up_dir")){
            loaded = true;
        }else{
            if(name.getVisibility()== View.GONE)
            {
                loaded = true;
            }
            else
            {
                loaded = loadCachedInfo(name, path);
            }
        }
        if (loaded) {
            mPendingRequests.remove(name);
        } else {
            FileId p = new FileId(path, 0);
            mPendingRequests.put(name, p);
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
        mTextCache.clear();
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

    private boolean loadCachedInfo(TextView view, String path) {
        TextHolder holder = mTextCache.get(path);

        if (holder == null) {
            holder = new TextHolder();
            if (holder == null)
                return false;

            mTextCache.put(path, holder);
        } else if (holder.state == TextHolder.LOADED) {
            if (holder.isNull()) {
                view.setVisibility(View.GONE);
                return true;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if (holder.setTextView(view)) {
                view.setVisibility(View.VISIBLE);
                return true;
            }
        }

        holder.state = TextHolder.NEEDED;
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
                        mLoaderThread = new LoaderThread();
                        mLoaderThread.start();
                    }

                    mLoaderThread.requestLoading();
                }
                return true;
            }

            case MESSAGE_INFO_LOADED: {
                if (!mPaused) {
                    processLoadedInfos();
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
    private void processLoadedInfos() {
        Iterator<TextView> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            TextView view = iterator.next();
            FileId fileId = mPendingRequests.get(view);
            boolean loaded = loadCachedInfo(view, fileId.mPath);
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
     * The thread that performs loading of photos from the database.
     */
    private class LoaderThread extends HandlerThread implements Handler.Callback {
        private Handler mLoaderThreadHandler;

        public LoaderThread() {
            super(LOADER_THREAD_NAME);
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
            Iterator<FileId> iterator = mPendingRequests.values().iterator();
            while (iterator.hasNext()) {
                FileId id = iterator.next();
                TextHolder holder = mTextCache.get(id.mPath);
                if (holder != null && holder.state == TextHolder.NEEDED) {
                    // Assuming atomic behavior
                    holder.state = TextHolder.LOADING;
                    String apk_name = FileUtils.getUninstallApkLabel(mContext, id.mPath);
                    if(TextUtils.isEmpty(apk_name)) {
                        apk_name = new File(id.mPath).getName();
                    }
                    holder.setText(apk_name);

                    holder.state = TextHolder.LOADED;
                    mTextCache.put(id.mPath, holder);
                }
            }

            mMainThreadHandler.sendEmptyMessage(MESSAGE_INFO_LOADED);
            return true;
        }
    }

}

