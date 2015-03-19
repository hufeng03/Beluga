package com.belugamobile.filemanager.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.PreferenceKeys;
import com.belugamobile.filemanager.utils.LogUtil;

/**
 * Created by feng on 2/23/2014.
 */
public class UiCallServiceHelper implements ServiceConnection {

    private static final String LOG_TAG = UiCallServiceHelper.class.getSimpleName();

    static UiCallServiceHelper mServiceHelper = null;

    private IFileManagerService mIFileManagerService = null;

    private Context mContext = null;

    synchronized public static UiCallServiceHelper getInstance() {
        if (mServiceHelper == null) {
            mServiceHelper = new UiCallServiceHelper();
        }
        return mServiceHelper;
    }

    public void connectService(Context context) {
        mContext = context;
        Intent service = new Intent(context, FileManagerService.class);
        context.bindService(service, this, Context.BIND_AUTO_CREATE);
    }

    public void forceScan () {
        getIFileObserverServiceWrapper().forceScan();
    }

    public boolean isScanning () {
        return getIFileObserverServiceWrapper().isScanning();
    }

//    public boolean isMonitoring (String path) {
//        return getIFolderMonitorServiceWrapper().isMonitoring(path);
//    }
//    public void addMonitor (String path) {
//        getIFolderMonitorServiceWrapper().addMonitor(path);
//    }
//
//    public void removeMonitor (String path) {
//        getIFolderMonitorServiceWrapper().removeMonitor(path);
//    }
//
//    public void clearMonitor () {
//        getIFolderMonitorServiceWrapper().clearMonitor();
//    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LogUtil.d(LOG_TAG, "file manager service connected!");
        mIFileManagerService = IFileManagerService.Stub.asInterface(service);

//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());
//        boolean forceScan = sp.getBoolean(PreferenceKeys.FORCE_SCAN_REQUIRED, false);
//        if (forceScan) {
//            getIFileObserverServiceWrapper().forceScan();
//            sp.edit().putBoolean(PreferenceKeys.FORCE_SCAN_REQUIRED, false).commit();
//        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtil.d(LOG_TAG, "file manager service disconnected!");
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


    private IFolderMonitorServiceWrapper getIFolderMonitorServiceWrapper() {
        if (mIFileManagerService == null)
            return new IFolderMonitorServiceWrapper(null);
        try {
            IBinder binder = mIFileManagerService.getFolderMonitorService();
            IFolderMonitorService service = IFolderMonitorService.Stub.asInterface(binder);
            IFolderMonitorServiceWrapper wrapper = new IFolderMonitorServiceWrapper(service);
            return wrapper;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new IFolderMonitorServiceWrapper(null);
    }


}
