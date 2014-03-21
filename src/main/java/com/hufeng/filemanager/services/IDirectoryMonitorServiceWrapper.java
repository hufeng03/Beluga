package com.hufeng.filemanager.services;

import android.os.RemoteException;

/**
 * Created by feng on 14-3-6.
 */
public class IDirectoryMonitorServiceWrapper {

    IDirectoryMonitorService mIDirectoryMonitorService;

    public IDirectoryMonitorServiceWrapper(IDirectoryMonitorService f) {
        mIDirectoryMonitorService = f;
    }

    public void addMonitor(String path) {
        if (mIDirectoryMonitorService != null) {
            try {
                mIDirectoryMonitorService.addMonitor(path);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMonitoring(String path) {
        boolean monitoring = false;
        if (mIDirectoryMonitorService != null) {
            try {
                monitoring = mIDirectoryMonitorService.isMonitoring(path);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
        return monitoring;
    }

    public void removeMonitor(String path){
        if (mIDirectoryMonitorService != null) {
            try {
                mIDirectoryMonitorService.removeMonitor(path);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearMonitor(){
        if (mIDirectoryMonitorService != null) {
            try {
                mIDirectoryMonitorService.clearMonitor();
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
