package com.hufeng.filemanager.browser;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.mp3.Mp3ReadId3v2;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class InfoLoader implements Callback {

    private static final String LOADER_THREAD_NAME = "InfoLoader";

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

    private static class InfoHolder {
        public static final int NEEDED = 0;

        public static final int LOADING = 1;

        public static final int LOADED = 2;

        int state;
        String text;

        public InfoHolder() {
            state = NEEDED;
            text = null;
        }

        public void setText(String t) {
            text = t;
        }

        public boolean bindTextView(TextView v) {
            if (isNull()) {
                v.setText("");
                return false;
            } else {
                v.setText(text);
                return true;
            }
        }

        public boolean isNull() {
            return text == null;
        }
    }

//    public static class FileId {
//        public String mPath;
//
////        public long mId; // database id
//
////        public int mCategory;
//
//        public FileId(String path/*, long id, int cate*/) {
//            mPath = path;
////            mId = id;
////            mCategory = cate;
//        }
//    }

    /**
     * A soft cache for image thumbnails. the key is file path
     */
    private final static ConcurrentHashMap<String, InfoHolder> mTextCache = new ConcurrentHashMap<String, InfoHolder>();

    /**
     * A map from ImageView to the corresponding photo ID. Please note that this
     * photo ID may change before the photo loading request is started.
     */
    private final ConcurrentHashMap<TextView, String> mPendingRequests = new ConcurrentHashMap<TextView, String>();

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

    private static InfoLoader instance = null;

    public static InfoLoader getInstance() {
        if (instance == null)
            instance = new InfoLoader(FileManager.getAppContext());
        return instance;
    }

    /**
     * Constructor.
     *
     * @param context content context
     */
    private InfoLoader(Context context) {
        mContext = context;
    }

//    public void remove(TextView view) {
//        mPendingRequests.remove(view);
//    }

    public boolean loadInfo(TextView view, String path) {
        boolean loaded;
        view.setText("");
        if (path.equals("to_up_dir")) {
            loaded = true;
        } else {
            if (view.getVisibility() == View.GONE) {
                loaded = true;
            } else {
                loaded = loadCachedInfo(view, path);
            }
        }
        if (loaded) {
            mPendingRequests.remove(view);
        } else {
            mPendingRequests.put(view, path);
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
        InfoHolder holder = mTextCache.get(path);

        if (holder == null) {
            holder = new InfoHolder();
            if (holder == null)
                return false;

            mTextCache.put(path, holder);
        } else if (holder.state == InfoHolder.LOADED) {
            if (holder.isNull()) {
//                view.setVisibility(View.GONE);
                return true;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if (holder.bindTextView(view)) {
//                view.setVisibility(View.VISIBLE);
                return true;
            }
        }

        holder.state = InfoHolder.NEEDED;
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
            String path = mPendingRequests.get(view);
            boolean loaded = loadCachedInfo(view, path);
            if (loaded) {
                iterator.remove();
//	                iconLoadListener.onIconLoadFinished(view);
            }
            // do not cache it anyway
//            mTextCache.remove(path);
        }

        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
    }

    /**
     * The thread that performs loading of photos from the database.
     */
    private class LoaderThread extends HandlerThread implements Callback {
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
            Iterator<String> iterator = mPendingRequests.values().iterator();
            while (iterator.hasNext()) {
                String path = iterator.next();
                InfoHolder holder = mTextCache.get(path);
                if (holder != null && holder.state == InfoHolder.NEEDED) {
                    // Assuming atomic behavior
                    holder.state = InfoHolder.LOADING;
                    int cate = FileUtils.getFileType(new File(path));
                    String info = InfoUtil.getFileInfo(path, cate);
//                    switch (id.mCategory) {
//                        case FileUtils.FILE_TYPE_DIRECTORY: {
//                            int count = getDirectoryChildrenCount(new File(id));
//                            String date = FileUtils.getFileDate(new File(id));
//                            if (count == 0) {
//                                holder.setText("(" + count + ") " + date);
//                            } else {
//                                holder.setText("(" + count + ") " + date);
//                            }
//                            break;
//                        }
//                        case FileUtils.FILE_TYPE_AUDIO: {
//                            String info = getAudioInfoFromFile(id);
//                            String size = FileUtils.getFileSize(new File(id));
//                            String date = FileUtils.getFileDate(new File(id));
//                            if (TextUtils.isEmpty(info)) {
//                                holder.setText(size + " " + date);
//                            } else {
//                                holder.setText(info + " " + size + " " + date);
//                            }
//                            break;
//                        }
//                        case FileUtils.FILE_TYPE_APK:
//                        default: {
//                            String size = FileUtils.getFileSize(new File(id));
//                            String date = FileUtils.getFileDate(new File(id));
//                            holder.setText(size + " " + date);
//                            break;
//                        }
//                    }
                    holder.setText(info);
                    holder.state = InfoHolder.LOADED;
                    mTextCache.put(path, holder);
                }
            }

            mMainThreadHandler.sendEmptyMessage(MESSAGE_INFO_LOADED);
            return true;
        }

        public int getDirectoryChildrenCount(File file) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null)
                    return files.length;
                else
                    return 0;
            } else {
                return 0;
            }
        }

        public String getAudioInfoFromFile(String path) {
            String info = null;
            if ("mp3".equalsIgnoreCase(IconLoaderHelper.getExtFromFilename(path))) {
                try {

                    Mp3ReadId3v2 mp3Id3v2 = new Mp3ReadId3v2(new FileInputStream(path));
                    mp3Id3v2.readId3v2(1024 * 100);
                    String special = mp3Id3v2.getSpecial();
                    String author = mp3Id3v2.getAuthor();
                    if (!TextUtils.isEmpty(special) && !TextUtils.isEmpty(author)) {
                        info = special + " " + author;
                    } else if (!TextUtils.isEmpty(special)) {
                        info = special;
                    } else if (!TextUtils.isEmpty(author)) {
                        info = author;
                    } else {
                        info = null;
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return info;
        }

    }

}
