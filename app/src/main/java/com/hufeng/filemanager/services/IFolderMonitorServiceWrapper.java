package com.hufeng.filemanager.services;

import android.os.RemoteException;

/**
 * Created by feng on 14-3-6.
 */
public class IFolderMonitorServiceWrapper {

    IFolderMonitorService mIFolderMonitorService;

    public IFolderMonitorServiceWrapper(IFolderMonitorService f) {
        mIFolderMonitorService = f;
    }

    public void addMonitor(String path) {
        if (mIFolderMonitorService != null) {
            try {
                mIFolderMonitorService.addMonitor(path);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isMonitoring(String path) {
        boolean monitoring = false;
        if (mIFolderMonitorService != null) {
            try {
                monitoring = mIFolderMonitorService.isMonitoring(path);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
        return monitoring;
    }

    public void removeMonitor(String path){
        if (mIFolderMonitorService != null) {
            try {
                mIFolderMonitorService.removeMonitor(path);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearMonitor(){
        if (mIFolderMonitorService != null) {
            try {
                mIFolderMonitorService.clearMonitor();
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
