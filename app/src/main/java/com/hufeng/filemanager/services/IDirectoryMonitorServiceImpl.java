package com.hufeng.filemanager.services;

import android.content.Context;
import android.os.RemoteException;

/**
 * Created by feng on 14-3-6.
 */
public class IDirectoryMonitorServiceImpl extends IDirectoryMonitorService.Stub {

    Context mContext;
    DirectoryMonitorImpl mMonitor;

    public IDirectoryMonitorServiceImpl(Context context)
    {
        mContext = context;
        mMonitor = new DirectoryMonitorImpl();
    }

    public void onCreate() {
        mMonitor.init();
    }

    public void onDestroy() {
        mMonitor.destroy();
    }

    public void refresh() {
        mMonitor.refresh(mContext);
    }

    @Override
    public void addMonitor(String dir) throws RemoteException {
        mMonitor.addMonitorDir(dir);
    }

    @Override
    public boolean isMonitoring(String dir) throws RemoteException {
        return mMonitor.isMonitoring(dir);
    }

    @Override
    public void removeMonitor(String dir) throws RemoteException {
        mMonitor.removeMonitorDir(dir);
    }

    @Override
    public void clearMonitor() throws RemoteException {
        mMonitor.clearMonitorDir();
    }
}
