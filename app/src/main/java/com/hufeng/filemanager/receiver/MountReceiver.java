package com.hufeng.filemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.LocalIntentAction;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.SDCardUtil;

public class MountReceiver extends BroadcastReceiver {
	
	private static final String LOG_TAG = MountReceiver.class.getName();

	private static final String MEDIA_MOUNTED_ACTION = "android.intent.action.MEDIA_MOUNTED";
	private static final String MEDIA_UNMOUNTED_ACTION = "android.intent.action.MEDIA_UNMOUNTED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "FileManager receive intent with action = "+action);

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(LocalIntentAction.DEVICE_MOUNT));
	}

}
