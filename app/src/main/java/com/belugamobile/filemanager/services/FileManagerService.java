package com.belugamobile.filemanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.belugamobile.filemanager.BelugaMountReceiver;
import com.belugamobile.filemanager.utils.LogUtil;

public class FileManagerService extends Service{
	
	private static final String LOG_TAG = "FileManagerService";
	
	private IBinder mBinder = null;
	private IFileSyncServiceImpl mFileSyncServiceImpl = null;
    private IFolderMonitorServiceImpl mFolderMonitorServiceImpl = null;
    private IWebServiceImpl mWebServiceImpl = null;
    private BelugaMountReceiver mBelugaMountReceiver;
    private ServiceMountListener mMountListener;

    public static final int MSG_DO_MOUNTED = 0;
    public static final int MSG_DO_EJECTED = 1;
    public static final int MSG_DO_UNMOUNTED = 2;
    public static final int MSG_DO_SDSWAP = 3;
    private Handler mMainThreadHandler = new MainThreadHandler();

    private class MainThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DO_MOUNTED:
                    doOnMounted((String) msg.obj);
                    break;
                case MSG_DO_UNMOUNTED:
                    doOnEjected((String) msg.obj);
                    break;
                case MSG_DO_EJECTED:
                    doOnUnMounted((String) msg.obj);
                    break;
                case MSG_DO_SDSWAP:
                    doOnSdSwap();
                    break;
            }
        }
    }

    private void doOnMounted(String mountPointPath) {
        // TODO: handle only files in mountPointPath
        mFolderMonitorServiceImpl.startToMonitorFolders();
    }

    private void doOnEjected(String ejectdPointPath) {
        // TODO: handle only files in mountPointPath
        mFolderMonitorServiceImpl.startToMonitorFolders();
    }

    private void doOnUnMounted(String unmountedPointPath) {
        // TODO: handle only files in mountPointPath
        mFolderMonitorServiceImpl.startToMonitorFolders();
    }

    private void doOnSdSwap() {

    }
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onCreate");
		mBinder = new IFileManagerServiceImpl().asBinder();

		mFileSyncServiceImpl = new IFileSyncServiceImpl(this.getApplicationContext());
		mFileSyncServiceImpl.onCreate();
        mFolderMonitorServiceImpl = new IFolderMonitorServiceImpl(this.getApplicationContext());
        mFolderMonitorServiceImpl.onCreate();
        mWebServiceImpl = new IWebServiceImpl(this.getApplicationContext());
        mWebServiceImpl.onCreate();

        mBelugaMountReceiver = BelugaMountReceiver.registerMountReceiver(this);
        mMountListener = new ServiceMountListener();
        mBelugaMountReceiver.registerMountListener(mMountListener);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onDestroy");

        mBelugaMountReceiver.unregisterMountListener(mMountListener);
        unregisterReceiver(mBelugaMountReceiver);

        mFileSyncServiceImpl.onDestroy();
        mFolderMonitorServiceImpl.onDestroy();
        mWebServiceImpl.onDestroy();

        mFileSyncServiceImpl = null;
        mFolderMonitorServiceImpl = null;
        mWebServiceImpl = null;

        mMountListener = null;
        mBelugaMountReceiver = null;
	}


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onStartCommand");
        return result;
    }

    @Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onBind");
		return mBinder;
	}
	
    
    /************************** AIDL interface *****************************/
    private class IFileManagerServiceImpl extends IFileManagerService.Stub {

        @Override
		public IBinder getService() throws RemoteException {
			// TODO Auto-generated method stub
			return mBinder;
		}

        @Override
        public IBinder getFileSyncService() throws RemoteException {
            return mFileSyncServiceImpl.asBinder();
        }

        @Override
        public IBinder getFolderMonitorService() throws RemoteException {
            return mFileSyncServiceImpl.asBinder();
        }

        @Override
        public IBinder getWebService() throws RemoteException {
            return mWebServiceImpl.asBinder();
        }
    }

    private class ServiceMountListener implements BelugaMountReceiver.MountListener {
        @Override
        public void onMounted(String mountPoint) {
            clear(mountPoint);
            Message msg = Message.obtain(mMainThreadHandler, MSG_DO_MOUNTED, mountPoint.intern());
            mMainThreadHandler.sendMessageDelayed(msg, 500);
        }

        @Override
        public void onUnMounted(String unMountPoint) {
            clear(unMountPoint);
            Message msg = Message.obtain(mMainThreadHandler, MSG_DO_UNMOUNTED, unMountPoint.intern());
            mMainThreadHandler.sendMessageDelayed(msg, 500);
        }

        @Override
        public void onEjected(String unMountPoint) {
            clear(unMountPoint);
            Message msg = Message.obtain(mMainThreadHandler, MSG_DO_EJECTED, unMountPoint.intern());
            mMainThreadHandler.sendMessageDelayed(msg, 500);
        }

        @Override
        public void onSdSwap() {
            Message.obtain(mMainThreadHandler, MSG_DO_SDSWAP).sendToTarget();
        }

        private void clear(String mountPoint) {
            mMainThreadHandler.removeMessages(MSG_DO_MOUNTED, mountPoint.intern());
            mMainThreadHandler.removeMessages(MSG_DO_UNMOUNTED, mountPoint.intern());
            mMainThreadHandler.removeMessages(MSG_DO_EJECTED, mountPoint.intern());
        }
    }

}
