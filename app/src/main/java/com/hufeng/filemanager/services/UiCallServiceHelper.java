package com.hufeng.filemanager.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.utils.LogUtil;

/**
 * Created by feng on 2/23/2014.
 */
public class UiCallServiceHelper implements ServiceConnection {

    private static final String LOG_TAG = UiCallServiceHelper.class.getSimpleName();

    static UiCallServiceHelper mServiceHelper = null;

    private IFileManagerService mIFileManagerService = null;

    private Context mContext = null;

    private IUiImpl mIUiImpl = new IUiImpl();

    synchronized public static UiCallServiceHelper getInstance() {
        if (mServiceHelper == null) {
            mServiceHelper = new UiCallServiceHelper();
        }
        return mServiceHelper;
    }

    public void addCallback(IUiImpl.UiCallback callback) {
        mIUiImpl.addCallback(callback);
    }

    public void removeCallback(IUiImpl.UiCallback callback) {
        mIUiImpl.removeCallback(callback);
    }

    public void connectService(Context context) {
        mContext = context;
        Intent service = new Intent(context, FileManagerService.class);
        context.bindService(service, this, Context.BIND_AUTO_CREATE);
    }

    public void startScan () {
        getIFileObserverServiceWrapper().startScan();
    }

    public void deleteUnexist (int type) {
        getIFileObserverServiceWrapper().deleteUnexist(type);
    }

    public boolean isScanning () {
        return getIFileObserverServiceWrapper().isScanning();
    }

    public boolean isMonitoring (String path) {
        return getIDirectoryMonitorServiceWrapper().isMonitoring(path);
    }

    public void addMonitor (String path) {
        getIDirectoryMonitorServiceWrapper().addMonitor(path);
    }

    public void removeMonitor (String path) {
        getIDirectoryMonitorServiceWrapper().removeMonitor(path);
    }

    public void clearMonitor () {
        getIDirectoryMonitorServiceWrapper().clearMonitor();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LogUtil.d(LOG_TAG, "Service filobserver connected!");
        mIFileManagerService = IFileManagerService.Stub.asInterface(service);

        try {
            mIFileManagerService.setUiIBinder(mIUiImpl.asBinder());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        String last_time_str = FileManager.getPreference(FileManager.FILEMANAGER_LAST_SCAN, "0");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());
        boolean refresh = sp.getBoolean("need_new_refresh", false);
        long last_time = Long.parseLong(last_time_str);
        if (refresh || last_time == 0 || Math.abs(System.currentTimeMillis() - last_time) > 1000 * 60 * 60 * 24) {
            getIFileObserverServiceWrapper().startScan();
            sp.edit().putBoolean("need_new_refresh", false).commit();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtil.d(LOG_TAG, "Service fileobserver disconnected!");
        connectService(mContext);
    }


    private IFileSyncServiceWrapper getIFileObserverServiceWrapper() {
        if (mIFileManagerService == null)
            return new IFileSyncServiceWrapper(null);
        try {
            IBinder binder = mIFileManagerService.getFileSyncService();
            IFileSyncService service = IFileSyncService.Stub.asInterface(binder);
            IFileSyncServiceWrapper wrapper = new IFileSyncServiceWrapper(service);
            return wrapper;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new IFileSyncServiceWrapper(null);
    }


    private IDirectoryMonitorServiceWrapper getIDirectoryMonitorServiceWrapper() {
        if (mIFileManagerService == null)
            return new IDirectoryMonitorServiceWrapper(null);
        try {
            IBinder binder = mIFileManagerService.getDirectoryMonitorService();
            IDirectoryMonitorService service = IDirectoryMonitorService.Stub.asInterface(binder);
            IDirectoryMonitorServiceWrapper wrapper = new IDirectoryMonitorServiceWrapper(service);
            return wrapper;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new IDirectoryMonitorServiceWrapper(null);
    }


}
