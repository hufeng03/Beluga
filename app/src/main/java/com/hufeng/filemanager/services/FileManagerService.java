package com.hufeng.filemanager.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.channel.DoovUtil;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.LogUtil;

public class FileManagerService extends Service{
	
	private static final String LOG_TAG = FileManagerService.class.getName();
	
	private IBinder mBinder = null;
	private IFileSyncServiceImpl mFileSyncServiceImpl = null;
    private IDirectoryMonitorServiceImpl mDirectoryMonitorServiceImpl = null;

    private BroadcastReceiver mMediaReceiver = null;
    private BroadcastReceiver mDoovReceiver = null;
    private Handler mHandler;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onCreate");
		mBinder = new IFileManagerServiceImpl().asBinder();


        HandlerThread thread = new HandlerThread("FileManagerServiceHandlerThread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
        mHandler = new FileManagerServiceHandler(serviceLooper);

		mFileSyncServiceImpl = new IFileSyncServiceImpl(this.getApplicationContext());
		mFileSyncServiceImpl.onCreate();
        mDirectoryMonitorServiceImpl = new IDirectoryMonitorServiceImpl(this.getApplicationContext());
        mDirectoryMonitorServiceImpl.onCreate();

        registerMediaReceiver();
        if ("doov".equals(Constants.PRODUCT_FLAVOR_NAME)) {
            registerDoovReceiver();
        }
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onDestroy");
        unregisterMediaReceiver();
        unregisterDoovReceiver();
        mFileSyncServiceImpl.onDestroy();
        mDirectoryMonitorServiceImpl.onDestroy();
        mFileSyncServiceImpl = null;
        mDirectoryMonitorServiceImpl = null;
        ServiceCallUiHelper.getInstance().removeUiIBinder();
	}

    public class FileManagerServiceHandler extends Handler {

        public static final int HANDLER_MESSAGE_MOUNT = 1;

        public FileManagerServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_MESSAGE_MOUNT:
                    StorageManager.clear();
                    if (mDirectoryMonitorServiceImpl != null) {
                        mDirectoryMonitorServiceImpl.refresh();
                    }
                    ServiceCallUiHelper.getInstance().storageChanged();
                    break;
            }
        }
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
                    mHandler.sendEmptyMessageDelayed(FileManagerServiceHandler.HANDLER_MESSAGE_MOUNT, 1000);
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

    private void registerDoovReceiver(){
        mDoovReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (DoovUtil.ACTION_DOOV_VISTOR.equals(action)) {
                    DoovUtil.changeDoovVistor();
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(DoovUtil.ACTION_DOOV_VISTOR);
        registerReceiver(mDoovReceiver, filter);
    }

    private void unregisterDoovReceiver(){
        if (mDoovReceiver != null) {
            unregisterReceiver(mDoovReceiver);
            mDoovReceiver = null;
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
            ServiceCallUiHelper.getInstance().setUiIBinder(iBinder);
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
