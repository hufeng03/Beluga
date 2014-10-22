package com.hufeng.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.NetworkUtil;
import com.hufeng.nanohttpd.HTTPServerService;
import com.hufeng.swiftp.Defaults;
import com.hufeng.swiftp.FTPServerService;
import com.hufeng.swiftp.Globals;

import java.net.InetAddress;

/**
 * Created by feng on 2014-07-10.
 */
public class PlayFtpFragment extends BaseFragment implements View.OnClickListener{

    private static final String LOG_TAG = "PlayFtpFragment";

    private TextView startStopButton;
    private ImageView wifiStateImage;


    private TextView wifiStatusText;
    private TextView ipText;
    private TextView lastErrorText;
    private TextView instructionText;

    public static PlayFtpFragment newFragment() {
        PlayFtpFragment fragment = new PlayFtpFragment();
        return fragment;
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            updateUi();
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.server_control_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ipText =           (TextView)view.findViewById(R.id.ip_address);

        instructionText = (TextView)view.findViewById(R.id.instruction);

        wifiStateImage = (ImageView)view.findViewById(R.id.wifi_state_image);

        wifiStatusText =   (TextView)view.findViewById(R.id.wifi_state);

        startStopButton = (TextView)view.findViewById(R.id.start_stop_button);

        startStopButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        requiredSettingsDefined();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mReceiver, filter);

        IntentFilter localfilter = new IntentFilter();
        localfilter.addAction(FTPServerService.INTENT_MESSAGE_FTP_SERVICE_STARTED);
        localfilter.addAction(FTPServerService.INTENT_MESSAGE_FTP_SERVICE_STOPPED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, localfilter);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    boolean requiredSettingsDefined() {
        SharedPreferences settings = getActivity().getSharedPreferences(
                Defaults.getSettingsName(), Defaults.getSettingsMode());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_stop_button:
                if(!NetworkUtil.isWifiConnected(getActivity()))
                {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    return;
                }
                Intent intent = new Intent(getActivity(),	FTPServerService.class);
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
                        getActivity().startService(intent);
                    }
                } else if (buttonText.equals(stopString)) {
        		/* The button had the "stop server" text. We stop the server now. */
                    getActivity().stopService(intent);
                } else {
                    // Do nothing
                    LogUtil.i(LOG_TAG, "Unrecognized start/stop text");
                }

                break;
        }
    }

    /**
     * This will be called by the static UiUpdater whenever the service has
     * changed state in a way that requires us to update our UI.
     *
     * We can't use any LogUtil.i() calls in this function, because that will
     * trigger an endless loop of UI updates.
     */
    public void updateUi() {
        WifiManager wifiMgr = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
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

                SharedPreferences settings = getActivity().getSharedPreferences(
                        Defaults.getSettingsName(),	Defaults.getSettingsMode());
                String username = settings.getString("username", null);
                String password = settings.getString("password", null);

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    ipText.setText("ftp://" + username + ":" + password + "@" + address.getHostAddress() +
                            ":" + FTPServerService.getPort() + "/");
                } else {
                    ipText.setText("ftp://" + address.getHostAddress() +
                            ":" + FTPServerService.getPort() + "/");
                }
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

        if(NetworkUtil.isWifiConnected(getActivity()))
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

