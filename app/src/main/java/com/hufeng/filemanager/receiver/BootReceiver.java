package com.hufeng.filemanager.receiver;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.services.FileManagerService;
import com.hufeng.filemanager.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	private static final String LOG_TAG = BootReceiver.class.getName();
	
	private final static String BOOT_COMPLETE_ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "BootReceiver receiver intent with action = "+intent.getAction());
//		if(intent.getAction().equals(BOOT_COMPLETE_ACTION) || intent.getAction().equals(ACTION_MEDIA))
//		{
//			context.startService(new Intent(context, FileManagerService.class));
//		}
	}

}
