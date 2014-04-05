package com.hufeng.filemanager.services;

import android.os.RemoteException;


public class IFileSyncServiceWrapper {
	
	private static final String LOG_TAG = IFileSyncServiceWrapper.class.getName();
	
	private IFileSyncService mIFileSyncService;
	
    public IFileSyncServiceWrapper(IFileSyncService f) {
    	mIFileSyncService = f;
    }

    public void startScan() {
        if (mIFileSyncService != null) {
            try {
                mIFileSyncService.startScan();
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteUnexist(int type) {
        if (mIFileSyncService != null) {
            try {
                mIFileSyncService.deleteUnexist(type);
            } catch(RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    
    public boolean isScanning()
    {
		if(mIFileSyncService==null)
			return false;
		try {
			boolean rst =  mIFileSyncService.isScanning();
//			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, " isScanning return "+rst);
			return rst;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }


}
