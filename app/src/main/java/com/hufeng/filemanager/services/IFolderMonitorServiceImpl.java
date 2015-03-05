package com.hufeng.filemanager.services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;

import com.hufeng.filemanager.BelugaFolderObserver;
import com.hufeng.filemanager.BuildConfig;
import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.helper.BelugaProviderHelper;
import com.hufeng.filemanager.mount.MountPoint;
import com.hufeng.filemanager.mount.MountPointManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by feng on 14-3-6.
 */
public class IFolderMonitorServiceImpl extends IFolderMonitorService.Stub {

    Context mContext;
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String LOG = "IFolderMonitorServiceImpl";

    HashMap<String, BelugaFolderObserver> mPersistentFolderObservers;

    public IFolderMonitorServiceImpl(Context context) {
        mContext = context;
    }

    private MonitorTask mMonitorTask;

    private HandlerThread mObserverHandlerThread;
    private ObserverHandler mObserverHandler;

    private class ObserverHandler extends Handler {

        public ObserverHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // If we are still initializing monitor folder, observer event is ignored
            if (mMonitorTask != null) {
                return;
            }
            switch (msg.what) {
                case 0:
                {
                    String path = msg.getData().getString(BelugaFolderObserver.HANDLER_MESSAGE_FILE_PATH_KEY);
                    if (TextUtils.isEmpty(path) || new File(path).exists()) {
                        return;
                    }
                    BelugaProviderHelper.deleteInBelugaDatabase(mContext, path);
                }
                break;
                case 1:
                {
                    String path = msg.getData().getString(BelugaFolderObserver.HANDLER_MESSAGE_FILE_PATH_KEY);
                    if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                        return;
                    }
                    BelugaProviderHelper.insertInBelugaDatabase(mContext, path);
                }
                break;
            }
        }
    };

    public void onCreate() {
        // create handler thread
        mObserverHandlerThread = new HandlerThread("BelugaFolderObserver");
        mObserverHandlerThread.start();
        mObserverHandler = new ObserverHandler(mObserverHandlerThread.getLooper());
        initMonitorFolders();
    }

    public void onDestroy() {
        clearPersistentObservers();
        //quit handler thread
        mObserverHandlerThread.quit();
        mObserverHandlerThread = null;
        mObserverHandler = null;
    }

    private void clearPersistentObservers() {
        if (mPersistentFolderObservers != null) {
            Iterator<BelugaFolderObserver> iterator = mPersistentFolderObservers.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().stopWatching();
            }
            mPersistentFolderObservers.clear();
        }
    }

    public void initMonitorFolders() {
        // Cancel already started task
        if (mMonitorTask != null) {
            mMonitorTask.cancel(false);
            mMonitorTask = null;
        }
        mMonitorTask = new MonitorTask();
        mMonitorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void addMonitor(String dir) throws RemoteException {
    }

    @Override
    public boolean isMonitoring(String dir) throws RemoteException {
        return true;
    }

    @Override
    public void removeMonitor(String dir) throws RemoteException {
    }

    @Override
    public void clearMonitor() throws RemoteException {
    }

    private class MonitorTask extends AsyncTask<Void,Void,HashMap<String, BelugaFolderObserver>> {

        @Override
        protected HashMap<String, BelugaFolderObserver> doInBackground(Void... params) {
            if (isCancelled()) {
                return null;
            }
            // get folders for persistent observer
            List<String> folders = new ArrayList<String>();
            MountPointManager mangaer = MountPointManager.getInstance();
            if (mangaer != null) {
                List<MountPoint> mountPoints = mangaer.getMountPoints();
                for (MountPoint mountPoint : mountPoints) {
                    if (isCancelled())
                        break;
                    folders.add(mountPoint.mPath);
                    for (String name : IFolderMonitorUtil.IMPORTANT_FOLDER) {
                        if (isCancelled())
                            break;
                        File file = new File(mountPoint.mPath, name);
                        if (file.exists()) {
                            folders.add(file.getAbsolutePath());
                            File[] children = file.listFiles();
                            if (children != null) {
                                for (File child : children) {
                                    if (isCancelled())
                                        break;
                                    if (child.isDirectory() && !child.isHidden()) {
                                        folders.add(child.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // create persistent observers
            HashMap<String, BelugaFolderObserver> observers = new HashMap<String, BelugaFolderObserver>();
            for (String folder : folders) {
                if (isCancelled())
                    break;
                if (!observers.containsKey(folder)) {
                    BelugaFolderObserver observer = new BelugaFolderObserver(folder, mObserverHandler);
                    observers.put(folder, observer);
                    observer.startWatching();
                }
            }
            if (isCancelled()) {
                Iterator<BelugaFolderObserver> iterator = observers.values().iterator();
                while(iterator.hasNext()) {
                    iterator.next().stopWatching();
                }
            }
            return observers;
        }

        @Override
        protected void onPostExecute(HashMap<String, BelugaFolderObserver> observers) {
            mMonitorTask = null;
            clearPersistentObservers();
            mPersistentFolderObservers = observers;
        }

        @Override
        protected void onCancelled(HashMap<String, BelugaFolderObserver> observers) {
            Iterator<BelugaFolderObserver> iterator = observers.values().iterator();
            while(iterator.hasNext()) {
                iterator.next().stopWatching();
            }
        }
    }
}
