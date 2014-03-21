package com.hufeng.filemanager.services;

import android.os.RemoteException;

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
}
