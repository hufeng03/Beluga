package com.hufeng.filemanager.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.hufeng.filemanager.utils.LogUtil;

public class FileManagerService extends Service{
	
	private static final String LOG_TAG = FileManagerService.class.getName();
	
	private IBinder mBinder = null;
	private IFileSyncServiceImpl mFileSyncServiceImpl = null;
    private IDirectoryMonitorServiceImpl mDirectoryMonitorServiceImpl = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onCreate");
		mBinder = new IFileManagerServiceImpl().asBinder();
		
		mFileSyncServiceImpl = new IFileSyncServiceImpl(this.getApplicationContext());
		mFileSyncServiceImpl.onCreate();
        mDirectoryMonitorServiceImpl = new IDirectoryMonitorServiceImpl(this.getApplicationContext());
        mDirectoryMonitorServiceImpl.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onDestroy");
        mFileSyncServiceImpl.onDestroy();
        mDirectoryMonitorServiceImpl.onDestroy();
        mFileSyncServiceImpl = null;
        mDirectoryMonitorServiceImpl = null;
        ServiceUiHelper.getInstance().removeUiIBinder();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onStart");
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
        public void setUiIBinder(IBinder iBinder) throws RemoteException {
            ServiceUiHelper.getInstance().setUiIBinder(iBinder);
        }

        @Override
		public IBinder getService() throws RemoteException {
			// TODO Auto-generated method stub
			return mBinder;
		}

		@Override
		public IBinder getFileSyncService() throws RemoteException {
			// TODO Auto-generated method stub
			return mFileSyncServiceImpl.asBinder();
		}

        @Override
        public IBinder getDirectoryMonitorService() throws RemoteException {
            return mDirectoryMonitorServiceImpl.asBinder();
        }
    }
	

}
