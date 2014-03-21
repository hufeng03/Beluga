package com.hufeng.filemanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.SDCardUtil;

public class MountReceiver extends BroadcastReceiver{
	
	private static final String LOG_TAG = MountReceiver.class.getName();
	
	private final static String BOOT_COMPLETE_ACTION = "android.intent.action.BOOT_COMPLETED";
	private static final String SDCARD_UPDATE_ACTION = "android.sdcard.action.SDCARD_UPDATE";
	private static final String SDCARD_DELETE_ACTION = "android.sdcard.action.SDCARD_DELETE";
	private static final String MEDIA_MOUNTED_ACTION = "android.intent.action.MEDIA_MOUNTED";
	private static final String MEDIA_UNMOUNTED_ACTION = "android.intent.action.MEDIA_UNMOUNTED";
	private static final String MEDIA_REMOVED_ACTION = "android.intent.action.MEDIA_REMOVED";
	private static final String MEDIA_EJECT_ACTION = "android.intent.action.MEDIA_EJECT";
	private static final String MEDIA_BAD_REMOVAL_ACTION = "android.intent.action.MEDIA_BAD_REMOVAL";
	private static final String MEDIA_SCANNER_FINISHED_ACTION = "android.intent.action.MEDIA_SCANNER_FINISHED";
	private static final String MEDIA_SCANNER_STARTED_ACTION = "android.intent.action.MEDIA_SCANNER_STARTED";
	
	private static final int HANDLER_MESSAGE_SCAN_FILE_START = 1;
	private static final int HANDLER_MESSAGE_SCAN_FILE_STOP = 2;
	
	private static final int HANDLER_MESSAGE_SYNC_MEDIA_START = 3;
	private static final int HANDLER_MESSAGE_SYNC_MEDIA_STOP = 4;
	
	static boolean mFirstScanned = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "myfilemanager receive intent with action = "+action);
		
		if(action.equals(BOOT_COMPLETE_ACTION)){
			if(handler.hasMessages(HANDLER_MESSAGE_SCAN_FILE_START)){
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"remove old message in handler");
				handler.removeMessages(HANDLER_MESSAGE_SCAN_FILE_START);	
			}else{
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"no old message in handler");
			}
			if(!mFirstScanned){
				if("0".equals(FileManager.getPreference("prepare_to_scan", "0"))){
					FileManager.setPreference("prepare_to_scan", "1");
				}
				Message msg = handler.obtainMessage();
				msg.what = HANDLER_MESSAGE_SCAN_FILE_START;
				handler.sendMessageDelayed(msg, 15000L);
			}
		}else if(action.equals(SDCARD_UPDATE_ACTION)){
			
		}
		else if(action.equals(SDCARD_DELETE_ACTION))
		{
			
		}
		else if(action.equals(MEDIA_MOUNTED_ACTION))
		{	
			if(handler.hasMessages(HANDLER_MESSAGE_SCAN_FILE_START)){
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"remove old message in handler");
				handler.removeMessages(HANDLER_MESSAGE_SCAN_FILE_START);	
			}else{
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"no old message in handler");
			}
			if(!mFirstScanned){
				if("0".equals(FileManager.getPreference("prepare_to_scan", "0"))){
					FileManager.setPreference("prepare_to_scan", "1");
				}
				Message msg = handler.obtainMessage();
				msg.what = HANDLER_MESSAGE_SCAN_FILE_START;
				handler.sendMessageDelayed(msg, 8000L);
			}
		}
		else if(action.equals(MEDIA_UNMOUNTED_ACTION)){
			
		}
		else if(action.equals(MEDIA_REMOVED_ACTION))
		{
//			Message msg = handler.obtainMessage();
//			msg.what = HANDLER_MESSAGE_SCAN_FILE_STOP;
//			handler.sendMessageDelayed(msg, 5000L);
		}
		else if(action.equals(MEDIA_EJECT_ACTION))
		{
			
		}
		else if(action.equals(MEDIA_BAD_REMOVAL_ACTION))
		{
			
		}
		else if(action.equals(MEDIA_SCANNER_STARTED_ACTION))
		{
			if(handler.hasMessages(HANDLER_MESSAGE_SCAN_FILE_START)){
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"remove old message in handler");
				handler.removeMessages(HANDLER_MESSAGE_SCAN_FILE_START);	
			}else{
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"no old message in handler");
			}
			if(!mFirstScanned){
				if("0".equals(FileManager.getPreference("prepare_to_scan", "0"))){
					FileManager.setPreference("prepare_to_scan", "1");
				}
//				Message msg = handler.obtainMessage();
//				msg.what = HANDLER_MESSAGE_SCAN_FILE_START;
//				handler.sendMessageDelayed(msg, 7000L);
			}
		}
		else if(action.equals(MEDIA_SCANNER_FINISHED_ACTION))
		{
//			Message msg = handler.obtainMessage();
//			msg.what = HANDLER_MESSAGE_SCAN_FILE_START;
//			handler.sendMessageDelayed(msg, 1000L);			
//			Message msg = handler.obtainMessage();
//			msg.what = HANDLER_MESSAGE_SYNC_MEDIA_START;
//			handler.sendMessageDelayed(msg, 1000L);
			if(handler.hasMessages(HANDLER_MESSAGE_SCAN_FILE_START)){
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"remove old message in handler");
				handler.removeMessages(HANDLER_MESSAGE_SCAN_FILE_START);	
			}else{
				if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"no old message in handler");
			}
			if(!mFirstScanned){
				if("0".equals(FileManager.getPreference("prepare_to_scan", "0"))){
					FileManager.setPreference("prepare_to_scan", "1");
				}
				Message msg = handler.obtainMessage();
				msg.what = HANDLER_MESSAGE_SCAN_FILE_START;
				handler.sendMessageDelayed(msg, 2000L);
			}
		}
	}
	
	private static boolean checkChange(){
		long all_size = SDCardUtil.getAllSize();
		long available_size = SDCardUtil.getAvailaleSize();
		SharedPreferences sp = FileManager.getAppContext().getSharedPreferences("scan_preference", Context.MODE_PRIVATE);
		long last_all_size = sp.getLong("sdcard_all_size", -100*1024*1024);
		long last_available_size = sp.getLong("sdcard_available_size", -100*1024*1024);
		if(LogUtil.IDBG)
			LogUtil.i(LOG_TAG, "sdcard current/pervious size/available is "+all_size+" "+available_size +" "+last_all_size+" "+last_available_size);
		boolean flag = false;
		if((all_size!=0 && available_size!=0)){
			if(Math.abs(all_size-last_all_size)>10*1024*1024){
				flag = true;
			}else if(Math.abs(available_size-last_available_size)>10*1024*1024){
				flag = true;
			}
			SharedPreferences.Editor edit = sp.edit();
			edit.putLong("sdcard_all_size", all_size);
			edit.putLong("sdcard_available_size", available_size);
			edit.commit();
		}
		if(LogUtil.IDBG)
			LogUtil.i(LOG_TAG, "checkChange return " + flag);

		return flag;
	}
	
	private static Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what==HANDLER_MESSAGE_SCAN_FILE_START)
			{
				if(!mFirstScanned){
					if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "broadcast start_scan_file_action in handler");
					if(checkChange()){
//						Intent intent = new Intent();
//						intent.setAction(IFileObserverServiceImpl.START_SCAN_FILE_ACTION);
//						FileManager.getAppContext().sendBroadcast(intent);
//                        if(!FileManager.getAppContext().getIFileObserverServiceWrapper().isScanned()){
//                            Intent intent = new Intent();
//                            intent.setAction(IFileObserverServiceImpl.CLEAN_SCAN_FILE_ACTION);
//                            FileManager.getAppContext().sendBroadcast(intent);
//                            FileManager.setPreference(FileManager.FILEMANAGER_LAST_SCAN, "0");
//                        }
					}
					FileManager.setPreference("prepare_to_scan", "0");
					mFirstScanned = true;
				}
			}
//			else if(msg.what==HANDLER_MESSAGE_SYNC_MEDIA_START)
//			{
//				Intent intent2 = new Intent();
//				intent2.setAction(IMediaSynchronizerServiceImpl.START_SYNC_MEDIA_ACTION);
//				FileManager.getAppContext().sendBroadcast(intent2);
//			}
		}
		
	};

}
