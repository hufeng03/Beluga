package com.hufeng.filemanager.data;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hufeng.filemanager.BuildConfig;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.services.ServiceUiHelper;
import com.hufeng.filemanager.storage.StorageManager;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by feng on 14-2-15.
 */
public class ImportantDirectoryMonitor {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String LOG_TAG = ImportantDirectoryMonitor.class.getSimpleName();

    ConcurrentHashMap<String, SingleDirectoryObserver> mPersistentObservers = new ConcurrentHashMap<String, SingleDirectoryObserver>();
    ConcurrentHashMap<String, SingleDirectoryObserver> mDynamicObservers = new ConcurrentHashMap<String, SingleDirectoryObserver>();

    private MyHandler mHandler;

    private static class MyHandler extends Handler{

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String path = msg.getData().getString("path");
                if (!TextUtils.isEmpty(path)) {
                    FileAction.scanSinglePath(path);
                    ServiceUiHelper.getInstance().changeMonitored(path);
                }
            }
        }
    }


    private static final String[] IMPORTANT_DIRECTORY = {
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_ALARMS,
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_MOVIES,
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_NOTIFICATIONS,
            Environment.DIRECTORY_PODCASTS,
            Environment.DIRECTORY_RINGTONES,
            Environment.DIRECTORY_PICTURES,
            "Documents"
    };

    private void addImportantDir(String stor) {
        for(String dir:IMPORTANT_DIRECTORY) {
            if (new File(stor, dir).exists() && new File(stor, dir).isDirectory()) {
                addSelfAndChildren(new File(stor, dir).getAbsolutePath());
            }
        }
    }

    private void addSelfAndChildren(String path){
        SingleDirectoryObserver observer = new SingleDirectoryObserver(path);
        mPersistentObservers.put(path, observer);
        observer.startWatching();

        File[] childs = new File(path).listFiles();
        if (childs != null) {
            for (File child : childs) {
                if (child.isDirectory()) {
                    observer = new SingleDirectoryObserver(child.getAbsolutePath());
                    mPersistentObservers.put(child.getAbsolutePath(), observer);
                    observer.startWatching();
                }
            }
        }
    }

    public ImportantDirectoryMonitor() {
        HandlerThread thread = new HandlerThread("ImportantDirectoryMonitor");
        thread.start();
        mHandler = new MyHandler(thread.getLooper());
    }

    public void addMonitorDir(String dir) {
        if (mPersistentObservers.get(dir) != null) {
            return;
        }
        SingleDirectoryObserver observer = mDynamicObservers.get(dir);
        if (observer == null) {
            observer = new SingleDirectoryObserver(dir);
            mDynamicObservers.put(dir, observer);
        }
        observer.startWatching();
    }

    public void removeMonitorDir(String dir) {
        SingleDirectoryObserver observer = mDynamicObservers.remove(dir);
        if (observer != null) {
            observer.stopWatching();
        }
    }

    public void clearMonitorDir() {
        //clear dynamic observers
        SingleDirectoryObserver observer = null;
        Iterator<SingleDirectoryObserver> iter = mDynamicObservers.values().iterator();
        while(iter.hasNext()) {
            observer = iter.next();
            if (observer != null) {
                observer.stopWatching();
            }
        }
        mDynamicObservers.clear();
    }

    public boolean isMonitoring(String dir) {
        SingleDirectoryObserver observer = mDynamicObservers.get(dir);
        if (observer == null || !observer.watching) {
            return false;
        } else {
            return true;
        }
    }

    public void init(Context context) {
        String[] stors = StorageManager.getInstance(context).getMountedStorages();
        if (stors != null) {
            for (String stor : stors) {
                SingleDirectoryObserver observer = new SingleDirectoryObserver(stor);
                mPersistentObservers.put(stor, observer);
                observer.startWatching();

                addImportantDir(stor);
            }
        }

    }

    public void destroy() {
        //clear dynamic observers
        SingleDirectoryObserver observer = null;
        Iterator<SingleDirectoryObserver> iter = mDynamicObservers.values().iterator();
        while(iter.hasNext()) {
            observer = iter.next();
            if (observer != null) {
                observer.stopWatching();
            }
        }
        mDynamicObservers.clear();
        //clean persistent observers
        iter = mPersistentObservers.values().iterator();
        while(iter.hasNext()) {
            observer = iter.next();
            if (observer != null) {
                observer.stopWatching();
            }
        }
        mPersistentObservers.clear();
    }

    private class SingleDirectoryObserver extends FileObserver {

        private String dir;
        private boolean watching = false;

        public SingleDirectoryObserver(String dir) {
            //CREATE: 子目录或者文件创建, 0x00000100
            //DELETE: 文件被删除, 0x00000200
            //MOVE_FROM: 子目录或者文件被移动, 0x00000040
            //MOVE_TO: 文件或者目录移入到当前监控目录, 0x00000080
            //DELETE_SELF:
            //MOVE_SELF:
            super(dir, FileObserver.CREATE | FileObserver.DELETE | FileObserver.MOVED_FROM | FileObserver.MOVED_TO | FileObserver.DELETE_SELF | FileObserver.MOVE_SELF);
            this.dir = dir;
        }

        @Override
        public void startWatching() {
            if (!watching) {
                super.startWatching();
                watching = true;
                if (DEBUG)
                    Log.i(LOG_TAG, this.hashCode()+" start observe "+ dir);
            }
        }

        @Override
        public void stopWatching() {
            if (watching) {
                super.stopWatching();
                watching = false;
                if (DEBUG)
                    Log.i(LOG_TAG, this.hashCode()+" stop  observe "+ dir);
            }
        }

        @Override
        public void onEvent(int event, String path) {
            if (TextUtils.isEmpty(path)) {
                return;
            }
            String event_str = "unknown";
            switch (event) {
                case FileObserver.CREATE:
                    event_str = "create";
                    break;
                case FileObserver.DELETE:
                    event_str = "delete";
                    break;
                case FileObserver.MOVED_FROM:
                    event_str = "moved_from";
                    break;
                case FileObserver.MOVED_TO:
                    event_str = "moved_to";
                    break;
                case FileObserver.MOVE_SELF:
                    stopWatching();
                    mDynamicObservers.remove(dir);
                    break;
                case FileObserver.DELETE_SELF:
                    stopWatching();
                    mDynamicObservers.remove(dir);
                    break;
            }
            if (DEBUG)
                Log.i(LOG_TAG, this.hashCode()+" on  event "+ path+" of "+dir+" "+event_str+"("+event+")");
            String full_path = new File(dir, path).getAbsolutePath();
            Message msg = new Message();
            msg.what = 1;
            Bundle data = new Bundle();
            data.putString("path", full_path);
            msg.setData(data);
            mHandler.sendMessageDelayed(msg, 1000);
        }
    }


}
