package com.hufeng.filemanager.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;

import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.LogUtil;

public class FileManagerService extends Service{
	
	private static final String LOG_TAG = FileManagerService.class.getName();
	
	private IBinder mBinder = null;
	private IFileSyncServiceImpl mFileSyncServiceImpl = null;
    private IDirectoryMonitorServiceImpl mDirectoryMonitorServiceImpl = null;

    private BroadcastReceiver mMediaReceiver = null;
	
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

        registerMediaReceiver();
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onDestroy");
        unregisterMediaReceiver();
        mFileSyncServiceImpl.onDestroy();
        mDirectoryMonitorServiceImpl.onDestroy();
        mFileSyncServiceImpl = null;
        mDirectoryMonitorServiceImpl = null;
        ServiceUiHelper.getInstance().removeUiIBinder();
	}

    private void registerMediaReceiver() {
        mMediaReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(intent.getAction())){
                    if (mFileSyncServiceImpl != null) {
                        mFileSyncServiceImpl.refresh();
                    }

                } else if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction()) || Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())) {
                    StorageManager.clear();
                    if (mDirectoryMonitorServiceImpl != null) {
                        mDirectoryMonitorServiceImpl.refresh();
                    }
                    ServiceUiHelper.getInstance().storageChanged();
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        filter.setPriority(2147483647);
        registerReceiver(mMediaReceiver, filter);
    }

    private void unregisterMediaReceiver() {
        if (mMediaReceiver != null) {
            unregisterReceiver(mMediaReceiver);
            mMediaReceiver = null;
        }
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
