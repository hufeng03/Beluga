package com.hufeng.filemanager.kanbox;

import android.os.Handler;
import android.util.Log;

public class JavascriptInterface {
	
	private static final String LOG_TAG = "JavascriptInterface";
	
	private Handler mHandler;
	
	public JavascriptInterface(Handler handler) {
		// TODO Auto-generated constructor stub
		mHandler = handler;
	}
	
	public void getHTML(String html)
	{
		Log.i(LOG_TAG, "html is " + html);
	}
	
	public void postLogin()
	{
	if(mHandler!=null)
		mHandler.sendEmptyMessage(0);
	}
}
