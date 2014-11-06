package com.hufeng.nanohttpd;


import java.io.IOException;
import java.net.InetAddress;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.storage.StorageManager;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;

import be.ppareit.swiftp.Util;

public class HTTPServerService extends Service {

	static SimpleWebServer server = null;
	
	final static int port = 8080;
	
	NotificationManager notificationMgr = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		if(server==null){
			try{
				String host = getWifiIp().getHostAddress();
	//			server = new SimpleWebServer(host, port, new File(StorageManager.getInstance(FileManager.getAppContext()).getExternalStorages()[0]));
				server = new SimpleWebServer(host, port, FileManager.getAppContext().getFilesDir(), StorageManager.getInstance(FileManager.getAppContext()).getMountedStorages());
				server.start();
				setupNotification();
				UiUpdater.updateClients();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				server = null;
			} catch(Exception e){
				e.printStackTrace();
				server = null;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(server!=null){
			server.stop();
			server = null;
		}
		clearNotification();
		UiUpdater.updateClients();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static boolean isRunning(){
		return server!=null;
	}

	/**
	 * Gets the IP address of the wifi connection.
	 * @return The integer IP address if wifi enabled, or null if not.
	 */
	public static InetAddress getWifiIp() {
		WifiManager wifiMgr = (WifiManager)FileManager.getAppContext()
		                        .getSystemService(Context.WIFI_SERVICE);
		if(isWifiEnabled()) {
			int ipAsInt = wifiMgr.getConnectionInfo().getIpAddress();
			if(ipAsInt == 0) {
				return null;
			} else {
				return Util.intToInet(ipAsInt);
			}
		} else {
			return null;
		}
	}
	
	public static int getPort() {
		return port;
	}
	
	public static boolean isWifiEnabled() {
		WifiManager wifiMgr = (WifiManager)FileManager.getAppContext()
		                        .getSystemService(Context.WIFI_SERVICE);
		if(wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			return true;
		} else {
			return false;
		}
	}
	
	
	private void setupNotification() {
		// http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		
		// Get NotificationManager reference
		String ns = Context.NOTIFICATION_SERVICE;
		notificationMgr = (NotificationManager) getSystemService(ns);
		
		// Instantiate a Notification
		int icon = R.drawable.notification_http;
		CharSequence tickerText = getString(R.string.notif_http_server_starting);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// Define Notification's message and Intent
		CharSequence contentTitle = getString(R.string.notif_http_title);
		CharSequence contentText = getString(R.string.notif_http_text);
		Intent notificationIntent = new Intent(this, ServerControlActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, 
				notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), 
				contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		// Pass Notification to NotificationManager
		notificationMgr.notify(0, notification);

	}
	
	private void clearNotification() {
		if(notificationMgr == null) {
			// Get NotificationManager reference
			String ns = Context.NOTIFICATION_SERVICE;
			notificationMgr = (NotificationManager) getSystemService(ns);
		}
		notificationMgr.cancelAll();
	}
	

}
