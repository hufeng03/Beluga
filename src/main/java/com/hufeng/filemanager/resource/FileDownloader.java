package com.hufeng.filemanager.resource;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import com.hufeng.filemanager.filedownload.impl.SmartDownloadProgressListener;
import com.hufeng.filemanager.filedownload.impl.SmartFileDownloader;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.ZipUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by feng on 13-9-27.
 */
public class FileDownloader extends AsyncTask<String, Integer, String>{

    private static final String LOG_TAG = FileDownloader.class.getSimpleName();

    public enum STATUS {
        DOWNLOADING, FAILED, PAUSED, SUCCESS;
    }

    public static final String DOWNLOAING_FILENAME_SURFFIX = "_tmp";

    private static ArrayList<String> mDownloadingUrl = new ArrayList<String>();
    private static HashMap<String, FileDownloader> mDownloadingTask = new HashMap<String, FileDownloader>();
    private static ConcurrentLinkedQueue<WeakReference<FileDownloaderListener>> mWeakListeners = new ConcurrentLinkedQueue<WeakReference<FileDownloaderListener>>();

    private static HashMap<String, Integer> mDownloadProgress = new HashMap<String, Integer>();

    @TargetApi(11)
    public static boolean downloadFile(Context context, String url, String path, String name){
        if(isDownloading(url)) {
            return false;
        } else {
            FileDownloader downloader = new FileDownloader(context);
            mDownloadingUrl.add(url);
            mDownloadingTask.put(url, downloader);

            if(mWeakListeners!=null) {
                for(WeakReference<FileDownloaderListener> wlistener:mWeakListeners) {
                    FileDownloaderListener listener = wlistener.get();
                    if(listener!=null) {
                        mDownloadProgress.put(url, 0);
                        listener.onFileDownloading(url, null, 0);
                    }
                }
            }


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                downloader.execute(url, name, path);
            } else {
                downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, name, path);
            }
            return true;
        }
    }

    public static interface FileDownloaderListener {
        public void onFileDownloading(String url, String path, int progress);
        public void onFileDownloaded(String url, String path, int status);
    }

    public static void addFileDownloaderListener(FileDownloaderListener listener) {
        mWeakListeners.add(new WeakReference<FileDownloaderListener>(listener));
    }

    public static void removeFileDownloaderListener(FileDownloaderListener listener) {
        for(WeakReference<FileDownloaderListener> wListener:mWeakListeners) {
            FileDownloaderListener lis = wListener.get();
            if (lis == listener){
                mWeakListeners.remove(wListener);
            }
        }
    }

    public static boolean isDownloading(String url) {
        return mDownloadingUrl.contains(url);
    }

    public static int getDownloadProgress(String url) {
        Integer pro =  mDownloadProgress.get(url);
        return (pro==null)?0:pro;
    }

    public static void pauseDownloader(String url) {

        FileDownloader downloader = mDownloadingTask.get(url);
        downloader.pause();
        mDownloadingTask.remove(url);
        mDownloadingUrl.remove(url);
    }

    public static void resumeDownloader(String url) {

    }

    private Context mContext;

    SmartFileDownloader mDownloader = null;

    public STATUS mStatus = STATUS.DOWNLOADING;

    private String mUrl;
    private String mFilename;

    public FileDownloader(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if(mWeakListeners!=null) {
            for(WeakReference<FileDownloaderListener> wlistener:mWeakListeners) {
                FileDownloaderListener listener = wlistener.get();
                if(listener!=null) {
                    int rst = values[0];
                    if(rst>=0 && rst<=100) {
                        mDownloadProgress.put(mUrl, rst);
                        listener.onFileDownloading(mUrl, mFilename, rst);
                    }
                }
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {
//        android.os.Debug.waitForDebugger();
        LogUtil.i(LOG_TAG, "do in background download" + params[0] + ", " + params[1] + ", " + params[2]);
        mUrl = params[0];
        try{
            boolean append = params[1].endsWith(DOWNLOAING_FILENAME_SURFFIX);
            int suffix_idx = mUrl.lastIndexOf(".");
            String suffix = mUrl.substring(suffix_idx);
            if (".zip".equals(suffix)) {
                suffix_idx = mUrl.substring(0, suffix_idx).lastIndexOf(".");
                if (suffix_idx != -1) {
                    suffix = mUrl.substring(suffix_idx);
                }
            }
            if(append) {
                mDownloader = new SmartFileDownloader(mContext, mUrl, new File(params[2]), params[1], true, 3);
            } else {
                if (params[1].endsWith(suffix)) {
                    mDownloader = new SmartFileDownloader(mContext, mUrl, new File(params[2]), params[1]+ DOWNLOAING_FILENAME_SURFFIX, false, 3);
                } else {
                    mDownloader = new SmartFileDownloader(mContext, mUrl, new File(params[2]), params[1]+suffix+ DOWNLOAING_FILENAME_SURFFIX, false, 3);
                }
            }
            mFilename = mDownloader.getFileName();
            final int file_size = mDownloader.getFileSize();
            mDownloader.download(new SmartDownloadProgressListener() {
                    @Override
                    public void onDownloadSize(int size) {
                        LogUtil.i(LOG_TAG, "apk download size is "+size+"/"+file_size);
                        if(size>=0) {
                            publishProgress(size * 100 / file_size);
                        }
                    }
                });
            if(mDownloader.isPaused()) {
                mStatus = STATUS.PAUSED;
            } else if(mDownloader.isFailed()) {
                mStatus = STATUS.FAILED;
            } else {
                mStatus = STATUS.SUCCESS;
            }
            mDownloadProgress.remove(mUrl);
        }catch(Exception e) {
            mStatus = STATUS.FAILED;
        }

        return mFilename;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mDownloadingUrl.remove(mUrl);
        mDownloadingTask.remove(this);
        mDownloadProgress.remove(mUrl);
        switch(mStatus) {
            case SUCCESS:
                if(mFilename!=null && mFilename.endsWith(DOWNLOAING_FILENAME_SURFFIX)) {
                    File file = new File(mFilename);
                    if (file.exists()) {
                        int len = DOWNLOAING_FILENAME_SURFFIX.length();
                        String name = mFilename.substring(0, mFilename.length()-len);
                        mFilename = getFilename(name);
                        file.renameTo(new File(mFilename));
           //             updateResultInLocalDb();
                        if (mFilename.endsWith(".zip") && mFilename.contains(ResourceListLoader.SELECTED_DOC_DIR_NAME)) {
                            String zip_dir = new File(mFilename).getParent();
                            String zip_name = new File(mFilename).getName();
                            int idx = zip_name.lastIndexOf(".zip");
                            String file_name = zip_name.substring(0, idx);
                            ZipUtil.unpackSingleZip(mFilename, getFilename(new File(zip_dir, file_name).getAbsolutePath()));
                        }
                    }
                }
                break;
            case DOWNLOADING:
            case FAILED:
            case PAUSED:
                if(mFilename!=null && mFilename.endsWith(DOWNLOAING_FILENAME_SURFFIX)) {
                    File file = new File(mFilename);
                    if (file.exists()) {
          //              updateResultInLocalDb();
                    }
                }
                break;
        }
        if(mWeakListeners!=null) {
            for(WeakReference<FileDownloaderListener> wlistener:mWeakListeners) {
                FileDownloaderListener listener = wlistener.get();
                if(listener!=null) {
                    listener.onFileDownloaded(mUrl, mFilename, mStatus.ordinal());
                }
            }
        }
    }


    public void pause() {
        if (mDownloader!=null) {
            mDownloader.pause();
        }
    }

    public boolean isPaused() {
        if (mDownloader!=null && !mDownloader.isPaused()) {
            return true;
        } else {
            return false;
        }
    }

    private String getFilename(String path) {
        File file = new File(path);
        if (file.exists()) {
            String dir = file.getParent();
            String name = file.getName();
            int i = 1;
            int idx = name.lastIndexOf('.');
            if (idx>0) {
                String real_name = name.substring(0,idx);
                String extension = name.substring(idx);
                while(!file.exists()) {
                    file = new File(dir, real_name+"("+(i++)+")"+extension);
                }
            } else {
                while(!file.exists()) {
                    file = new File(dir, name+"("+(i++)+")");
                }
            }
        }
        return file.getAbsolutePath();
    }
}
