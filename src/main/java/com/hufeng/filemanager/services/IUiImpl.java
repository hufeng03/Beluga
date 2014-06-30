package com.hufeng.filemanager.services;

import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.storage.StorageManager;

import java.util.ArrayList;

/**
 * Created by feng on 2/23/2014.
 */
public class IUiImpl extends IUi.Stub {

    ArrayList<UiCallback> mCallbacks = new ArrayList<UiCallback>();

    public interface UiCallback {
        public void scanStarted();
        public void scanCompleted();
        public void changeMonitored(String dir);
    }

    public void addCallback(UiCallback callback) {
        mCallbacks.add(callback);
    }

    public void removeCallback(UiCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public void scanStarted() throws RemoteException {
        if (mCallbacks != null) {
            for (UiCallback callback : mCallbacks) {
                callback.scanStarted();
            }
        }
    }

    @Override
    public void scanCompleted() throws RemoteException {
        for (UiCallback callback : mCallbacks) {
            callback.scanCompleted();
        }
    }

    @Override
    public void changeMonitored(String dir) throws RemoteException {
        for (UiCallback callback : mCallbacks) {
            callback.changeMonitored(dir);
        }
    }

    @Override
    public void storageChanged() throws RemoteException {
        StorageManager.clear();
        Intent intent = new Intent("SHOW_ROOT_FILES_ACTION");
        LocalBroadcastManager.getInstance(FileManager.getAppContext()).sendBroadcast(intent);
    }
}
