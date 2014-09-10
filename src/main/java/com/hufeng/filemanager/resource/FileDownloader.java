package com.hufeng.filemanager.resource;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.hufeng.filemanager.BusProvider;
import com.hufeng.filemanager.FileDownloadEvent;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.filedownload.impl.SmartDownloadProgressListener;
import com.hufeng.filemanager.filedownload.impl.SmartFileDownloader;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.ZipUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by feng on 13-9-27.
 */
public class FileDownloader extends BroadcastReceiver{

    private static final String LOG_TAG = FileDownloader.class.getSimpleName();

    public static enum STATUS {
        DOWNLOADING, FAILED, PAUSED, SUCCESS;
    }

    public static final String DOWNLOAING_FILENAME_SURFFIX = "_tmp";

    private static ArrayList<String> mDownloadingUrl = new ArrayList<String>();
    private static HashMap<Long, String> mDownloadingIdUrlMap = new HashMap<Long, String>();
    private static HashMap<Long, String> mDownloadingIdPathMap = new HashMap<Long, String>();

    private static HashMap<String, Integer> mDownloadProgress = new HashMap<String, Integer>();
    //-100: cancelled
    //-200: paused

    private static ScheduledExecutorService mScheduledExecutorService = null;

//    private static HandlerThread mHandlerThread;
//    private static Handler mHandler;
//
//    private static class MyHandler extends Handler {
//
//        public MyHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    mUpdateCommand.run();
//                    if (mHandler != null) {
//                        mHandler.sendEmptyMessageDelayed(0, 1000 * 2);
//                    }
//                    break;
//            }
//        }
//    }

//    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(LOG_TAG, "onReceive "+System.currentTimeMillis()+", "+intent);
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            //download completed
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (reference != -1) {
                String url = mDownloadingIdUrlMap.get(reference);
                String path = mDownloadingIdPathMap.get(reference);
                if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(path)) {

                    if(path.endsWith(DOWNLOAING_FILENAME_SURFFIX)) {
                        File file = new File(path);
                        if (file.exists()) {
                            int len = DOWNLOAING_FILENAME_SURFFIX.length();
                            String name = path.substring(0, path.length()-len);
                            path = getFilename(name);
                            file.renameTo(new File(path));
                            if (path.endsWith(".zip") && path.contains(ResourceListLoader.SELECTED_DOC_DIR_NAME)) {
                                String zip_dir = new File(path).getParent();
                                String zip_name = new File(path).getName();
                                int idx = zip_name.lastIndexOf(".zip");
                                String file_name = zip_name.substring(0, idx);
                                ZipUtil.unpackSingleZip(path, getFilename(new File(zip_dir, file_name).getAbsolutePath()));
                            }
                            FileDownloadEvent event = new FileDownloadEvent(url, path, DownloadManager.STATUS_SUCCESSFUL, 100);
//                            BusProvider.getInstance().post(event);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(event.buildIntentWithBundle());
                            LogUtil.i(LOG_TAG, "mUpdateCommand post success "+System.currentTimeMillis()+", "+event);
                        }
                    }
                } else {
                    //TODO:ui was destroyed, do something

                }
                if (!TextUtils.isEmpty(url)) {
                    mDownloadingUrl.remove(url);
                    mDownloadProgress.remove(url);
                }
                mDownloadingIdUrlMap.remove(reference);
                mDownloadingIdPathMap.remove(reference);

            }

            //stop it
            if (mDownloadingIdUrlMap.isEmpty() || mDownloadingUrl.isEmpty()) {
                if (mScheduledExecutorService != null) {
                    mScheduledExecutorService.shutdownNow();
                    mScheduledExecutorService = null;
                }
//                if (mHandler!= null) {
//                    mHandlerThread.quitSafely();
//                    mHandler = null;
//                }
            }
        }
    }

    private static Runnable mUpdateCommand = new Runnable() {
        @Override
        public void run() {
            long[] ids = null;
            LogUtil.i(LOG_TAG, "mUpdateCommand runs "+System.currentTimeMillis());
            if (mDownloadingIdUrlMap != null) {
                Set<Long> keys = mDownloadingIdUrlMap.keySet();
                if (keys != null) {
                    Long[] IDS = keys.toArray(new Long[0]);
                    if (IDS !=null) {
                       int len = IDS.length;
                       if (len > 0) {
                           ids = new long[IDS.length];
                           for (int i = 0; i < IDS.length; i++) {
                               ids[i] = IDS[i];
                           }
                       }
                    }
                }
            }
            if (ids == null || ids.length == 0) {
                if (mScheduledExecutorService != null) {
                    mScheduledExecutorService.shutdownNow();
                    mScheduledExecutorService = null;
                }
//                if (mHandler!= null) {
//                    mHandlerThread.quitSafely();
//                    mHandler = null;
//                }
                return;
            } else {

            }
            final DownloadManager manager = (DownloadManager) FileManager.getAppContext().getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(ids);
            Cursor c = null;
            try {
                c = manager.query(query);
                if (c != null) {
                    while (c.moveToNext()) {
                        long id = c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_ID));
                        int bytes_downloaded = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytes_total = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        String url = mDownloadingIdUrlMap.get(id);
                        String path = mDownloadingIdPathMap.get(id);
                        final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                        switch (status) {
                            case DownloadManager.STATUS_RUNNING:
                            case DownloadManager.STATUS_PENDING:
                                mDownloadProgress.put(url, dl_progress);
                                FileDownloadEvent event = new FileDownloadEvent(url, path, status, dl_progress);
                                //BusProvider.getInstance().post(event);
                                LocalBroadcastManager.getInstance(FileManager.getAppContext()).sendBroadcast(event.buildIntentWithBundle());
                                LogUtil.i(LOG_TAG, "mUpdateCommand post progress "+bytes_downloaded+","+bytes_total+","+System.currentTimeMillis()+", "+event);
                                break;
                            case DownloadManager.STATUS_PAUSED:
                                mDownloadProgress.put(url, -100);
                                FileDownloadEvent event_paused = new FileDownloadEvent(url, path, status, dl_progress);
                                //BusProvider.getInstance().post(event_paused);
                                LocalBroadcastManager.getInstance(FileManager.getAppContext()).sendBroadcast(event_paused.buildIntentWithBundle());
                                LogUtil.i(LOG_TAG, "mUpdateCommand post progress "+bytes_downloaded+","+bytes_total+","+System.currentTimeMillis()+", "+event_paused);
                                break;
                            case DownloadManager.STATUS_FAILED:
                                mDownloadingUrl.remove(url);
                                mDownloadProgress.remove(url);
                                mDownloadingIdUrlMap.remove(id);
                                mDownloadingIdPathMap.remove(id);
                                FileDownloadEvent event_failed = new FileDownloadEvent(url, path, DownloadManager.STATUS_FAILED, dl_progress);
                                //BusProvider.getInstance().post(event_failed);
                                LocalBroadcastManager.getInstance(FileManager.getAppContext()).sendBroadcast(event_failed.buildIntentWithBundle());
                                LogUtil.i(LOG_TAG, "mUpdateCommand runs "+System.currentTimeMillis()+", "+event_failed);
                                break;
                            case DownloadManager.STATUS_SUCCESSFUL:
                                //we do not handle success here, handle it in BroadcastReceiver
                                break;
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    };

    public static boolean downloadFile(Context context, String url, String path, String name){
        if(isDownloading(url)) {
            return false;
        } else {
            if (!new File(path).exists()) {
                new File(path).mkdirs();
            }
            int idx_1 = url.lastIndexOf("/");
            if (idx_1>0 && url.charAt(idx_1-1) != '/' ) {
                int idx_2 = url.substring(idx_1).indexOf(".");
                if (idx_2 != -1) {
                    String suffix = url.substring(idx_1 + idx_2);
                    if (!name.endsWith(suffix)) {
                        name += suffix;
                    }
                }
            }

//            if(mWeakListeners!=null) {
//                for(WeakReference<FileDownloaderListener> wlistener:mWeakListeners) {
//                    FileDownloaderListener listener = wlistener.get();
//                    if(listener!=null) {
//                        listener.onFileDownloading(url, null, 0);
//                    }
//                }
//            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//            request.setDescription("Testando");
//            request.setTitle("Download");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "teste.zip");
            String real_path = getFilename(new File(path, name+DOWNLOAING_FILENAME_SURFFIX).getAbsolutePath());
            request.setDestinationUri(Uri.fromFile(new File(real_path)));

            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            final long downloadId = manager.enqueue(request);

            mDownloadingUrl.add(url);
            mDownloadProgress.put(url, 0);
            mDownloadingIdUrlMap.put(downloadId, url);
            mDownloadingIdPathMap.put(downloadId, real_path);

//            if (mHandler == null) {
//                mHandlerThread = new HandlerThread("FileDownloader-HandlerThread");
//                mHandlerThread.start();
//                mHandler = new MyHandler(mHandlerThread.getLooper());
//
//                mHandler.sendEmptyMessageDelayed(0, 1000);
//            }

            if (mScheduledExecutorService == null) {
                mScheduledExecutorService = Executors.newScheduledThreadPool(2);
                mScheduledExecutorService.scheduleAtFixedRate(mUpdateCommand, 1, 2, TimeUnit.SECONDS);
            }

            return true;
        }
    }

    public static boolean isDownloading(String url) {
        return mDownloadingUrl.contains(url);
    }

    public static int getDownloadProgress(String url) {
        Integer pro =  mDownloadProgress.get(url);
        return (pro==null)?0:pro;
    }

    public static void cancelDownloader(String url) {
        //TODO: need to change this
//        FileDownloader downloader = mDownloadingIdUrlMap.get(url);
//        downloader.pause();
//        mDownloadingIdUrlMap.remove(url);
//        mDownloadingUrl.remove(url);
        final DownloadManager manager = (DownloadManager) FileManager.getAppContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Long key_selected = null;
        if (mDownloadingIdUrlMap != null) {
            Iterator<Long> iterator = mDownloadingIdUrlMap.keySet().iterator();
            while (iterator.hasNext()) {
                Long key = iterator.next();
                if (mDownloadingIdUrlMap.get(key).equals(url)) {
                    key_selected = key;
                    break;
                }
            }
        }
        if (key_selected != null) {
            manager.remove(key_selected);
            String path = mDownloadingIdPathMap.get(key_selected);
            mDownloadingUrl.remove(url);
            mDownloadProgress.remove(url);
            mDownloadingIdUrlMap.remove(key_selected);
            mDownloadingIdPathMap.remove(key_selected);
            if (!TextUtils.isEmpty(path)) {
                FileDownloadEvent event = new FileDownloadEvent(url, path, DownloadManager.STATUS_FAILED, -100);
//                BusProvider.getInstance().post(event);
                LocalBroadcastManager.getInstance(FileManager.getAppContext()).sendBroadcast(event.buildIntentWithBundle());
                LogUtil.i(LOG_TAG, "mUpdateCommand post success "+System.currentTimeMillis()+", "+event);
            }
        }

        //stop it
        if (mDownloadingIdUrlMap.isEmpty() || mDownloadingUrl.isEmpty()) {
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow();
                mScheduledExecutorService = null;
            }
//            if (mHandler!= null) {
//                mHandlerThread.quitSafely();
//                mHandler = null;
//            }
        }
    }

//    @Override
//    protected void onProgressUpdate(Integer... values) {
//        if(mWeakListeners!=null) {
//            for(WeakReference<FileDownloaderListener> wlistener:mWeakListeners) {
//                FileDownloaderListener listener = wlistener.get();
//                if(listener!=null) {
//                    int rst = values[0];
//                    if(rst>=0 && rst<=100) {
//                        mDownloadProgress.put(mUrl, rst);
//                        listener.onFileDownloading(mUrl, mFilename, rst);
//                    }
//                }
//            }
//        }
//    }

//    @Override
//    protected String doInBackground(String... params) {
////        android.os.Debug.waitForDebugger();
//        LogUtil.i(LOG_TAG, "do in background download" + params[0] + ", " + params[1] + ", " + params[2]);
//        mUrl = params[0];
//        try{
//            boolean append = params[1].endsWith(DOWNLOAING_FILENAME_SURFFIX);
//            int suffix_idx = mUrl.lastIndexOf(".");
//            String suffix = mUrl.substring(suffix_idx);
//            if (".zip".equals(suffix)) {
//                suffix_idx = mUrl.substring(0, suffix_idx).lastIndexOf(".");
//                if (suffix_idx != -1) {
//                    suffix = mUrl.substring(suffix_idx);
//                }
//            }
//            if(append) {
//                mDownloader = new SmartFileDownloader(mContext, mUrl, new File(params[2]), params[1], true, 3);
//            } else {
//                if (params[1].endsWith(suffix)) {
//                    mDownloader = new SmartFileDownloader(mContext, mUrl, new File(params[2]), params[1]+ DOWNLOAING_FILENAME_SURFFIX, false, 3);
//                } else {
//                    mDownloader = new SmartFileDownloader(mContext, mUrl, new File(params[2]), params[1]+suffix+ DOWNLOAING_FILENAME_SURFFIX, false, 3);
//                }
//            }
//            mFilename = mDownloader.getFileName();
//            final int file_size = mDownloader.getFileSize();
//            mDownloader.download(new SmartDownloadProgressListener() {
//                    @Override
//                    public void onDownloadSize(int size) {
//                        LogUtil.i(LOG_TAG, "apk download size is "+size+"/"+file_size);
//                        if(size>=0) {
//                            publishProgress(size * 100 / file_size);
//                        }
//                    }
//                });
//            if(mDownloader.isPaused()) {
//                mStatus = STATUS.PAUSED;
//            } else if(mDownloader.isFailed()) {
//                mStatus = STATUS.FAILED;
//            } else {
//                mStatus = STATUS.SUCCESS;
//            }
//            mDownloadProgress.remove(mUrl);
//        }catch(Exception e) {
//            mStatus = STATUS.FAILED;
//        }
//
//        return mFilename;
//    }

//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
//        mDownloadingUrl.remove(mUrl);
//        mDownloadingIdUrlMap.remove(this);
//        mDownloadProgress.remove(mUrl);
//        switch(mStatus) {
//            case SUCCESS:
//                if(mFilename!=null && mFilename.endsWith(DOWNLOAING_FILENAME_SURFFIX)) {
//                    File file = new File(mFilename);
//                    if (file.exists()) {
//                        int len = DOWNLOAING_FILENAME_SURFFIX.length();
//                        String name = mFilename.substring(0, mFilename.length()-len);
//                        mFilename = getFilename(name);
//                        file.renameTo(new File(mFilename));
//           //             updateResultInLocalDb();
//                        if (mFilename.endsWith(".zip") && mFilename.contains(ResourceListLoader.SELECTED_DOC_DIR_NAME)) {
//                            String zip_dir = new File(mFilename).getParent();
//                            String zip_name = new File(mFilename).getName();
//                            int idx = zip_name.lastIndexOf(".zip");
//                            String file_name = zip_name.substring(0, idx);
//                            ZipUtil.unpackSingleZip(mFilename, getFilename(new File(zip_dir, file_name).getAbsolutePath()));
//                        }
//                    }
//                }
//                break;
//            case DOWNLOADING:
//            case FAILED:
//            case PAUSED:
//                if(mFilename!=null && mFilename.endsWith(DOWNLOAING_FILENAME_SURFFIX)) {
//                    File file = new File(mFilename);
//                    if (file.exists()) {
//          //              updateResultInLocalDb();
//                    }
//                }
//                break;
//        }
//        if(mWeakListeners!=null) {
//            for(WeakReference<FileDownloaderListener> wlistener:mWeakListeners) {
//                FileDownloaderListener listener = wlistener.get();
//                if(listener!=null) {
//                    listener.onFileDownloaded(mUrl, mFilename, mStatus.ordinal());
//                }
//            }
//        }
//    }


//    public void pause() {
//        if (mDownloader!=null) {
//            mDownloader.pause();
//        }
//    }

//    public boolean isPaused() {
//        if (mDownloader!=null && !mDownloader.isPaused()) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    private static String getFilename(String path) {
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
