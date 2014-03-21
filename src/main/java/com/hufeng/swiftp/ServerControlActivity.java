/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.hufeng.swiftp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.hufeng.filemanager.AdmobDelegate;
import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.SettingsItemBaseActivity;
import com.hufeng.filemanager.skin.SkinManager;
import com.hufeng.filemanager.utils.LogUtil;

import java.net.InetAddress;

public class ServerControlActivity extends SettingsItemBaseActivity {
	
	private static final String LOG_TAG = ServerControlActivity.class.getSimpleName();
    
	private int mSkin = SkinManager.SKIN_BLACK;
	
    private TextView startStopButton;
    private ImageView wifiStateImage;

    
    private TextView wifiStatusText;
    private TextView ipText;
//    private TextView lastErrorText;
    private TextView instructionText;
    
    private BroadcastReceiver mReceiver;
    
    
    protected MyLog myLog = new MyLog("ServerControlActivity");
    
    protected Context activityContext = this;

    private View mAdView;
    
    public Handler handler = new Handler() {
    	public void handleMessage(Message msg) {
    		switch(msg.what) {
    		case 0:  // We are being told to do a UI update
    			// If more than one UI update is queued up, we only need to do one.
    			removeMessages(0);
    			updateUi();
    			break;
    		case 1:  // We are being told to display an error message
    			removeMessages(1);
    		}
    	}
    };
    
    public ServerControlActivity() {
    	
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the application-wide context global, if not already set
		Context myContext = Globals.getContext();
		if(myContext == null) {
			myContext = getApplicationContext();
			if(myContext == null) {
				throw new NullPointerException("Null context!?!?!?");
			}
			Globals.setContext(myContext);
		}
        // Inflate our UI from its XML layout description.
        setContentView(R.layout.server_control_activity);
        
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);
        
        
        ipText =           (TextView)findViewById(R.id.ip_address);
        
        instructionText = (TextView)findViewById(R.id.instruction);
        
        wifiStateImage = (ImageView)findViewById(R.id.wifi_state_image);

        wifiStatusText =   (TextView)findViewById(R.id.wifi_state);
        
        startStopButton = (TextView) findViewById(R.id.start_stop_button);
        
        startStopButton.setOnClickListener(startStopListener);
 
        updateUi();
        
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
//            mAdView = AdmobUtil.showAd(this, (LinearLayout) findViewById(R.id.root));
            mAdView = AdmobDelegate.showAd(this, (LinearLayout) findViewById(R.id.root));
        }

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


    /**
     * Whenever we regain focus, we should update the button text depending
     * on the state of the server service.
     */
    protected void onStart() {
    	super.onStart();
		UiUpdater.registerClient(handler);
		updateUi();
    }
    
    protected void onResume() {
    	super.onResume();
    	// If the required preferences are not present, launch the configuration
        // Activity.
    	setUI();
        if(!requiredSettingsDefined()) {
        	launchConfigureActivity();
        }
        UiUpdater.registerClient(handler);
		updateUi();
		// Register to receive wifi status broadcasts
		LogUtil.i(LOG_TAG, "Registered for wifi updates");
		this.registerReceiver(wifiReceiver, 
				new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    /* Whenever we lose focus, we must unregister from UI update messages from
     * the FTPServerService, because we may be deallocated.
     */
    protected void onPause() {
    	super.onPause();
		UiUpdater.unregisterClient(handler);
		LogUtil.i(LOG_TAG, "Unregistered for wifi updates");
		this.unregisterReceiver(wifiReceiver);
    }
    
    protected void onStop() {
    	super.onStop();
    	UiUpdater.unregisterClient(handler);
    }
    
    protected void onDestroy() {
    	super.onDestroy();
        if (Constants.SHOW_AD) {
//            if(mAdView!=null){
//                mAdView.destroy();
//            }
            AdmobDelegate.distroyAd(mAdView);
        }
    	UiUpdater.unregisterClient(handler);
    	unregisterReceiver(mReceiver);
    }
    
    /**
     * This will be called by the static UiUpdater whenever the service has
     * changed state in a way that requires us to update our UI.
     * 
     * We can't use any LogUtil.i() calls in this function, because that will
     * trigger an endless loop of UI updates.
     */
    public void updateUi() {
    	WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
    	int wifiState = wifiMgr.getWifiState();
    	LogUtil.i(LOG_TAG, "Updating UI");
    	ipText.setVisibility(View.VISIBLE);
    	instructionText.setVisibility(View.VISIBLE);
    	if(FTPServerService.isRunning()) {
    		LogUtil.i(LOG_TAG, "updateUi: server is running");
    		// Put correct text in start/stop button
       		startStopButton.setText(R.string.stop_server);
       		
       		// Fill in wifi status and address
    		InetAddress address =  FTPServerService.getWifiIp();
        	if(address != null) {
        		ipText.setText("ftp://" + address.getHostAddress() + 
    		               ":" + FTPServerService.getPort() + "/");
        	} else {
        		LogUtil.i(LOG_TAG, "Null address from getServerAddress()");
        		ipText.setText(R.string.cant_get_url);
        	}
        	ipText.setVisibility(View.VISIBLE);
        	instructionText.setText(R.string.please_connect_after_started);
    	} else {
    		LogUtil.i(LOG_TAG, "updateUi: server is not running");
       		// Update the start/stop button to show the correct text
    		startStopButton.setText(R.string.start_server);
    		ipText.setText(R.string.no_url_yet);
    		startStopButton.setText(R.string.start_server);
    		ipText.setVisibility(View.GONE);
    		instructionText.setText(R.string.if_ftp_started);
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
    	String errString;
    	if((errString = Globals.getLastError()) != null) {
    		Globals.setLastError(null);  // Clear the error condition after retrieving
//    		lastErrorText.setText(errString);
//    		lastErrorText.setVisibility(View.VISIBLE);      	
    	}
    	
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

    OnClickListener startStopListener = new OnClickListener() {
        public void onClick(View v) {
        	
        	if(!isWifiConnected())
        	{
        		startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        		return;
        	}
        	
    		Context context = getApplicationContext();
    		Intent intent = new Intent(context,	FTPServerService.class);
    		/*
        	 * In order to choose whether to stop or start the server, we check
        	 * the text on the button to see which action the user was 
        	 * expecting.
        	 */
    		String startString = getString(R.string.start_server);
    		String stopString = getString(R.string.stop_server);
    		String buttonText = startStopButton.getText().toString(); 
        	if(buttonText.equals(startString)) { 
    			/* The button had the "start server" text  */
        		if(!FTPServerService.isRunning()) {
        			warnIfNoExternalStorage();
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

    private void warnIfNoExternalStorage() {
    	String storageState = Environment.getExternalStorageState();
    	if(!storageState.equals(Environment.MEDIA_MOUNTED)) {
    		myLog.i("Warning due to storage state " + storageState);
    		Toast toast = Toast.makeText(this, R.string.storage_warning,
					Toast.LENGTH_LONG);
	    	toast.setGravity(Gravity.CENTER, 0, 0);
	    	toast.show();
    	}
    }
    
    OnClickListener addUserListener = new OnClickListener() {
        public void onClick(View v) {
        	LogUtil.i(LOG_TAG, "Add user stub");
        }
    };

    OnClickListener manageUsersListener = new OnClickListener() {
        public void onClick(View v) {
        	LogUtil.i(LOG_TAG, "Manage users stub");
        }
    };

    OnClickListener serverOptionsListener = new OnClickListener() {
        public void onClick(View v) {
        	LogUtil.i(LOG_TAG, "Server options stub");
        }
    };
    
    DialogInterface.OnClickListener ignoreDialogListener = 
    	new DialogInterface.OnClickListener() 
    {
    	public void onClick(DialogInterface dialog, int which) {
    	}
    };
    
    /**
     * A call-back for when the user presses the "setup" button.
     */
    OnClickListener setupListener = new OnClickListener() {
        public void onClick(View v) {
        	launchConfigureActivity();
        }
    };
    
    void launchConfigureActivity() {
    	if(!requiredSettingsDefined()) {
	    	Toast toast = Toast.makeText(this, R.string.must_config,
					Toast.LENGTH_SHORT);
	    	toast.setGravity(Gravity.CENTER, 0, 0);
	    	toast.show();
    	}
    	Intent intent = new Intent(activityContext, ConfigureActivity.class);
    	startActivity(intent);
    }
    
    /**
     * A callback for when the user toggles the session monitor on or off
     */
    OnClickListener sessionMonitorCheckBoxListener = new OnClickListener() {
        public void onClick(View v) {
        	// Trigger a UI update message to our Activity
            UiUpdater.updateClients();
        	//updateUi();
        }
    };

    /**
     * A callback for when the user toggles the server log on or off
     */
    OnClickListener serverLogCheckBoxListener = new OnClickListener() {
        public void onClick(View v) {
        	// Trigger a UI update message to our Activity
        	UiUpdater.updateClients();
            //updateUi();
        }
    };
    
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
    	public void onReceive(Context ctx, Intent intent) {
        	LogUtil.i(LOG_TAG, "Wifi status broadcast received");
    		updateUi();
    	}
    };
    
    boolean requiredSettingsDefined() {
    	SharedPreferences settings = getSharedPreferences(
        		Defaults.getSettingsName(),	Defaults.getSettingsMode());
		String username = settings.getString("username", null);
		String password = settings.getString("password", null);
				
		if(username == null || password == null) {
			SharedPreferences.Editor edit = settings.edit();
			edit.putString("username", Defaults.username);
			edit.putString("password", Defaults.password);
			edit.commit();
			return true;
		} else {
			return true;
		}
    }
    
    /** Get the settings from the FTPServerService if it's running, otherwise
     * load the settings directly from persistent storage.
     */
    SharedPreferences getSettings() {
    	SharedPreferences settings = FTPServerService.getSettings();
    	if(settings != null) {
    		return settings;
    	} else {
    		return this.getPreferences(MODE_PRIVATE);
    	}
    }
    
	private void setUI()
	{
		SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(this);
		int skin = sp.getInt(SkinManager.SKIN_SELECTION, SkinManager.SKIN_UNDEFINED);
		if(mSkin!=skin)
		{
//			mBack.setBackgroundResource(SkinManager.getBackButtonResourceId(skin));
//			mTopTab.setBackgroundResource(SkinManager.getTabBarResourceId(skin));
			mSkin = skin;
		}
	}

}
