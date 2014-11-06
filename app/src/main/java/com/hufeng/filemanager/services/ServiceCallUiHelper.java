package com.hufeng.filemanager.services;

import android.os.IBinder;
import android.os.RemoteException;

/**
 * Created by feng on 2/23/2014.
 */
public class ServiceCallUiHelper {

    private static final String LOG_TAG = UiCallServiceHelper.class.getSimpleName();

    static ServiceCallUiHelper mUiHelper = null;

    private IUi mIUi = null;

    synchronized public static ServiceCallUiHelper getInstance() {
        if (mUiHelper == null) {
            mUiHelper = new ServiceCallUiHelper();
        }
        return mUiHelper;
    }

    public void setUiIBinder(IBinder iBinder) {
        mIUi = IUi.Stub.asInterface(iBinder);
    }

    public void removeUiIBinder() {
        mIUi = null;
    }

    public void scanStarted() {
        if (mIUi != null) {
            try {
                mIUi.scanStarted();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void scanCompleted() {
        if (mIUi != null) {
            try {
                mIUi.scanCompleted();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeMonitored(String dir) {
        if (mIUi != null) {
            try {
                mIUi.changeMonitored(dir);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void storageChanged() {
        if (mIUi != null) {
            try {
                mIUi.storageChanged();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
