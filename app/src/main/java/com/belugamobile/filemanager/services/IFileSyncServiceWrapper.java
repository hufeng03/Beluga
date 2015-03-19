package com.belugamobile.filemanager.services;

import android.os.RemoteException;


public class IFileSyncServiceWrapper {
	
	private static final String LOG_TAG = IFileSyncServiceWrapper.class.getName();
	
	private IFileSyncService mIFileSyncService;
	
    public IFileSyncServiceWrapper(IFileSyncService f) {
    	mIFileSyncService = f;
    }

    public void forceScan() {
        if (mIFileSyncService != null) {
            try {
                mIFileSyncService.forceScan();
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
			return  mIFileSyncService.isScanning();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }


}
