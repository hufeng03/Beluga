package com.hufeng.nanohttpd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hufeng.filemanager.AdmobDelegate;
import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.SettingsItemBaseActivity;
import com.hufeng.filemanager.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class ServerControlActivity extends SettingsItemBaseActivity {
	
	private static final String LOG_TAG = ServerControlActivity.class.getSimpleName();
	
	
    private TextView startStopButton;
    private ImageView wifiStateImage;

    
    private TextView wifiStatusText;
    private TextView ipText;
    private TextView lastErrorText;
    private TextView instructionText;
    
    private BroadcastReceiver mReceiver;
    
    private Handler handler =new MyHandler(this);

    private View mAdView;
    
    
    private static class MyHandler extends Handler {
    	
    	private WeakReference<ServerControlActivity> activity;
    	
    	public MyHandler(ServerControlActivity act){
    		activity = new WeakReference<ServerControlActivity>(act);
    	}
    	
    	public void handleMessage(Message msg) {
    		switch(msg.what) {
    		case 0:  // We are being told to do a UI update
    			// If more than one UI update is queued up, we only need to do one.
    			removeMessages(0);
    			ServerControlActivity act = activity.get();
    			if(act!=null)
    				act.updateUi();
    			break;
    		case 1:  // We are being told to display an error message
    			removeMessages(1);
    		}
    	}
    };
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.http_server_control_activity);

		ipText = (TextView) findViewById(R.id.ip_address);

		instructionText = (TextView) findViewById(R.id.instruction);

		wifiStateImage = (ImageView) findViewById(R.id.wifi_state_image);

		wifiStatusText = (TextView) findViewById(R.id.wifi_state);

		startStopButton = (TextView) findViewById(R.id.start_stop_button);

		startStopButton.setOnClickListener(startStopListener);
		
		
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				if(ConnectivityManager.CONNECTIVITY_ACTION.equals(arg1.getAction()))
				{
					updateUi();
				}
			}
			
		};
        
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, filter);

        if (Constants.SHOW_AD) {
            mAdView = AdmobDelegate.showAd(this, (LinearLayout) findViewById(R.id.root));
        }

	}

	@Override
	protected void onResume(){
		super.onResume();
		updateUi();
		UiUpdater.registerClient(handler);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		UiUpdater.unregisterClient(handler);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
        if (Constants.SHOW_AD) {
            if(mAdView!=null){
                AdmobDelegate.distroyAd(mAdView);
            }
        }
		if(mReceiver!=null)
		    unregisterReceiver(mReceiver);
	}
	
	
    private static boolean isWifiConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
            FileManager.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }
    
    OnClickListener startStopListener = new OnClickListener() {
        public void onClick(View v) {
        	
        	if(!isWifiConnected())
        	{
        		startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        		return;
        	}
        	
    		Context context = getApplicationContext();
    		Intent intent = new Intent(context,	HTTPServerService.class);
    		/*
        	 * In order to choose whether to stop or start the server, we check
        	 * the text on the button to see which action the user was 
        	 * expecting.
        	 */
    		String startString = getString(R.string.start_http_server);
    		String stopString = getString(R.string.stop_http_server);
    		String buttonText = startStopButton.getText().toString(); 
        	if(buttonText.equals(startString)) { 
    			/* The button had the "start server" text  */
        		if(!HTTPServerService.isRunning()) {
        		//	warnIfNoExternalStorage();
        			context.startService(intent);
        		}
        	} else if (buttonText.equals(stopString)) {
        		/* The button had the "stop server" text. We stop the server now. */
        		context.stopService(intent);
        	} else {
        		// Do nothing
        		LogUtil.i(LOG_TAG, "Unrecognized start/stop text");
        	}
        }
    };

	
	
    public void updateUi() {
    	WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    	int wifiState = wifiMgr.getWifiState();
    	LogUtil.i(LOG_TAG, "Updating UI");
    	ipText.setVisibility(View.VISIBLE);
    	instructionText.setVisibility(View.VISIBLE);
    	if(HTTPServerService.isRunning()) {
    		LogUtil.i(LOG_TAG, "updateUi: server is running");
    		// Put correct text in start/stop button
       		startStopButton.setText(R.string.stop_http_server);
       		
       		// Fill in wifi status and address
    		InetAddress address =  HTTPServerService.getWifiIp();
        	if(address != null) {
        		ipText.setText("http://" + address.getHostAddress() + 
    		               ":" + HTTPServerService.getPort() + "/");
        	} else {
        		LogUtil.i(LOG_TAG, "Null address from getServerAddress()");
        		ipText.setText(R.string.cant_get_url);
        	}
        	ipText.setVisibility(View.VISIBLE);
        	instructionText.setText(R.string.please_connect_http_after_started);
    	} else {
    		LogUtil.i(LOG_TAG, "updateUi: server is not running");
       		// Update the start/stop button to show the correct text
    		startStopButton.setText(R.string.start_http_server);
    		ipText.setText(R.string.no_url_yet);
    		startStopButton.setText(R.string.start_http_server);
    		ipText.setVisibility(View.GONE);
    		instructionText.setText(R.string.if_http_started);
    	}
    	
    	
    	// Manage the text of the wifi enable/disable button and the 
    	// wifi status text.
    	switch(wifiState) {
    	case WifiManager.WIFI_STATE_ENABLED:
    		//wifiButton.setText(R.string.disable_wifi);
    		wifiStatusText.setText(R.string.enabled);
    		break;
    	case WifiManager.WIFI_STATE_DISABLED:
    		//wifiButton.setText(R.string.enable_wifi);
    		wifiStatusText.setText(R.string.disabled);
    		break;
    	default:
    		// We're in some transient state that will eventually
    		// become one of the other two states.
    		wifiStatusText.setText(R.string.waiting);
    		break;
    	}

    	// Manage the visibility and text of the "last error" display
    	// and popup a dialog box, if there has been an error
//    	String errString;
//    	if((errString = Globals.getLastError()) != null) {
//    		Globals.setLastError(null);  // Clear the error condition after retrieving
//    		lastErrorText.setText(errString);
//    		lastErrorText.setVisibility(View.VISIBLE);      	
//    	}
    	
        if(isWifiConnected())
        {
        	wifiStateImage.setImageResource(R.drawable.wifi_state4);
        	
        }
        else
        {
        	wifiStateImage.setImageResource(R.drawable.wifi_state0);
        	
        	startStopButton.setText(R.string.open_wifi_settings);
        	ipText.setVisibility(View.GONE);
        	instructionText.setVisibility(View.GONE);
        }
    }
}
