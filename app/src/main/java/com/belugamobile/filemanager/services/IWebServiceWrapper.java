package com.belugamobile.filemanager.services;

import android.os.RemoteException;

/**
 * Created by Feng on 2015-05-10.
 */
public class IWebServiceWrapper {
    IWebService mIWebService;

    public IWebServiceWrapper(IWebService w) {
        mIWebService = w;
    }

    public void startServer() {
        try {
            mIWebService.startServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            mIWebService.stopServer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
