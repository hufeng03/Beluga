package com.hufeng.filemanager.browser;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.storage.StorageManager;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class IconLoader implements Callback {

    private static final String LOADER_THREAD_NAME = "IconLoader";

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

    private static abstract class ImageHolder {

        public static final int NEEDED = 0;

        public static final int LOADING = 1;

        public static final int LOADED = 2;

        int state;

        public static ImageHolder create(int cate) {
            switch (cate) {
                case FileUtils.FILE_TYPE_APK:
                case FileUtils.FILE_TYPE_IMAGE:
                case FileUtils.FILE_TYPE_VIDEO:
                case FileUtils.FILE_TYPE_AUDIO:
                    return new BitmapHolder();
            }

            return null;
        }

        public abstract boolean setBitmapForImageView(ImageView v);

        public abstract boolean isNull();

        public abstract void setImage(Object image);
    }

    private static class BitmapHolder extends ImageHolder {
        SoftReference<Bitmap> bitmapRef;

        @Override
        public boolean setBitmapForImageView(ImageView v) {
            if (bitmapRef.get() == null)
                return false;
            if (v == null) {
                return true;
            }
            v.setScaleType(ScaleType.FIT_CENTER);
            v.setImageBitmap(bitmapRef.get());
            return true;
        }

        @Override
        public boolean isNull() {
            return bitmapRef == null;
        }

        @Override
        public void setImage(Object image) {
            bitmapRef = image == null ? null : new SoftReference<Bitmap>((Bitmap) image);
        }
    }

    public static class FileId {
        public String mPath;

        public long mId; // database id

        public int mCategory;

        public FileId(String path, long id, int cate) {
            mPath = path;
            mId = id;
            mCategory = cate;
        }
    }

    /**
     * A soft cache for bitmap thumbnails. the key is file path
     */
    private final static ConcurrentHashMap<String, ImageHolder> mImageCache = new ConcurrentHashMap<String, ImageHolder>();

    /**
     * A map from ImageView to the corresponding file path. Please note that this
     * file path may change before the photo loading request is started.
     */
    private final ConcurrentHashMap<WeakReference<ImageView>, FileId> mPendingRequests = new ConcurrentHashMap<WeakReference<ImageView>, FileId>();

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

    private static IconLoader instance = null;

    public static IconLoader getInstance() {
        if (instance == null)
            instance = new IconLoader(FileManager.getAppContext());
        return instance;
    }

    /**
     * Constructor.
     *
     * @param context content context
     */
    private IconLoader(Context context) {
        mContext = context;
    }

    public void remove(ImageView view) {
        mPendingRequests.remove(view);
    }

    public boolean loadIcon(ImageView view, RelativeLayout background, LinearLayout detail, String path) {
        if (background != null) {
            background.setBackgroundResource(R.drawable.grid_item_image_bg);
        }
        if (detail != null) {
            detail.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        boolean loaded;
        int cate = 0;
        if ("to_up_dir".equals(path)) {
            loaded = true;
            view.setImageResource(R.drawable.to_up_dir);
            view.setScaleType(ScaleType.CENTER_INSIDE);
        }
//           else if(path.startsWith("http://")){
//               view.setScaleType(ScaleType.CENTER_INSIDE);
//               view.setImageResource(IconLoaderHelper.getFileIcon(FileUtils.FILE_TYPE_APK));
//               loaded = loadCachedIcon(view, path, -1);
//           }
        else if (new File(path).isDirectory()) {
            loaded = true;
            if (StorageManager.getInstance(mContext).isStorage(path)) {
                if (StorageManager.getInstance(mContext).isExternalStorage(path)) {
                    view.setImageResource(R.drawable.sdcard);
                } else if (StorageManager.getInstance(mContext).isInternalStorage(path)) {
                    view.setImageResource(R.drawable.phone);
                } else {
                    view.setImageResource(R.drawable.sdcard);
                }
            } else {
                view.setImageResource(R.drawable.file_icon_folder);
            }
            view.setScaleType(ScaleType.CENTER_INSIDE);
        } else {
            cate = FileUtils.getFileType(new File(path));
            view.setScaleType(ScaleType.CENTER_INSIDE);
            view.setImageResource(IconLoaderHelper.getFileIcon(view.getContext(), path));
            if (cate == FileUtils.FILE_TYPE_ZIP || cate == FileUtils.FILE_TYPE_DOCUMENT) {
                loaded = true;
            } else if (cate == FileUtils.FILE_TYPE_AUDIO && !"mp3".equals(IconLoaderHelper.getExtFromFilename(path))) {
                loaded = true;
            } else {
                loaded = loadCachedIcon(view, path, cate);
            }
        }
        if (loaded) {
            mPendingRequests.remove(view);
        } else {
            FileId p = new FileId(path, 0, cate);
            mPendingRequests.put(new WeakReference<ImageView>(view), p);
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
        mImageCache.clear();
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

    private boolean loadCachedIcon(ImageView view, String path, int cate) {
        ImageHolder holder = mImageCache.get(path);

        if (holder == null) {
//                if (path.startsWith("http://")) {
//                    holder = new BitmapHolder();
//                } else {
            holder = ImageHolder.create(cate);
//                }
            if (holder == null)
                return false;

            mImageCache.put(path, holder);
        } else if (holder.state == ImageHolder.LOADED) {
            if (holder.isNull()) {
                return true;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if (holder.setBitmapForImageView(view)) {
                return true;
            }
        }

        holder.state = ImageHolder.NEEDED;
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
        Iterator<WeakReference<ImageView>> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            WeakReference<ImageView> view = iterator.next();
            FileId fileId = mPendingRequests.get(view);
            if (fileId == null)
                continue;
            boolean loaded = loadCachedIcon(view.get(), fileId.mPath, fileId.mCategory);
            if (loaded) {
                iterator.remove();
            }
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
            Iterator<FileId> iterator = mPendingRequests.values().iterator();
            while (iterator.hasNext()) {
                FileId id = iterator.next();
                ImageHolder holder = mImageCache.get(id.mPath);
                if (holder != null && holder.state == ImageHolder.NEEDED) {
                    // Assuming atomic behavior
                    holder.state = ImageHolder.LOADING;

//                        if(id.mPath.startsWith("http://")){
//
//                        } else {
                    switch (id.mCategory) {
                        case FileUtils.FILE_TYPE_APK:
                            holder.setImage(FileUtils.getUninstallAPKIcon(mContext, id.mPath));
                            break;
                        case FileUtils.FILE_TYPE_AUDIO:
                        case FileUtils.FILE_TYPE_IMAGE:
                        case FileUtils.FILE_TYPE_VIDEO:
                            if (id.mId == 0)
                                id.mId = getDbId(id.mPath, id.mCategory);
                            if (id.mId == 0) {
                                Log.e("FileIconLoader", "Fail to get dababase id for:" + id.mPath);
                            }
                            Bitmap bm = null;
                            if (id.mCategory == FileUtils.FILE_TYPE_VIDEO) {
                                bm = IconUtil.getVideoThumbnail(id.mPath, id.mId);
                            } else if (id.mCategory == FileUtils.FILE_TYPE_IMAGE) {
                                bm = IconUtil.getImageThumbnail(id.mPath, id.mId);
                            } else if (id.mCategory == FileUtils.FILE_TYPE_AUDIO) {
                                bm = IconUtil.getAudioThumbnail(id.mPath, id.mId);
                            }
                            if (bm != null) {
                                holder.setImage(bm);
                            }
                            break;
                        default:
                            //should not happen
                            break;
                    }
//                        }
                    holder.state = BitmapHolder.LOADED;
                    mImageCache.put(id.mPath, holder);
                }
            }

            mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
            return true;
        }
    }

    public long getDbId(String path, int cate) {
        String volumeName = "external";
        Uri uri = null;
        if (cate == FileUtils.FILE_TYPE_VIDEO) {
            uri = Video.Media.getContentUri(volumeName);
        } else if (cate == FileUtils.FILE_TYPE_IMAGE) {
            uri = Images.Media.getContentUri(volumeName);
        } else if (cate == FileUtils.FILE_TYPE_AUDIO) {
            uri = Audio.Media.getContentUri(volumeName);
        } else {

        }

        String selection = FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[]{
                path
        };

        String[] columns = new String[]{
                FileColumns._ID, FileColumns.DATA
        };

        Cursor c = mContext.getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return 0;
        }
        long id = 0;
        if (c.moveToNext()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }
}
