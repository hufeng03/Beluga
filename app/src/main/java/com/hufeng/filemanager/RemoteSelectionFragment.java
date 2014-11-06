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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by feng on 2014-10-26.
 */
public class RemoteSelectionFragment extends BaseFragment implements View.OnClickListener, RadioButton.OnCheckedChangeListener{

    private static final String LOG_TAG = "RemoteFragment";


    public enum RemoteProtocol {
        HTTP, FTP, SOCKET;

        public static RemoteProtocol valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                ordinal = 0;
            }
            return values()[ordinal];
        }

        @Override
        public String toString() {
            switch (this) {
                case HTTP:
                    return "HTTP";
                case FTP:
                    return "FTP";
                case SOCKET:
                    return "SOCKET";
            }
            return null;
        }
    }

    private static final String REMOTE_PROTOCOL_KEY = "REMOTE_PROTOCOL";

    private SharedPreferences mSharedPreference = null;

    private TextView mActionButton;
    private ImageView mWifiStateImage;

    private TextView mWifiStatusText;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioFtp;
    private RadioButton mRadioHttp;

    private RemoteProtocol mProtocol = RemoteProtocol.HTTP;

    private int mWifiStatus;


    public static RemoteSelectionFragment newFragment() {
        RemoteSelectionFragment fragment = new RemoteSelectionFragment();
        return fragment;
    }

    BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            updateWifiCondition();
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.remote_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mWifiStateImage = (ImageView)view.findViewById(R.id.wifi_state_image);
        mWifiStatusText =   (TextView)view.findViewById(R.id.wifi_state);
        mActionButton = (TextView)view.findViewById(R.id.start_stop_button);
        mRadioHttp = (RadioButton)view.findViewById(R.id.radio_http);
        mRadioFtp = (RadioButton)view.findViewById(R.id.radio_ftp);
        mRadioGroup = (RadioGroup)view.findViewById(R.id.radio_group);

        if (savedInstanceState != null) {
            mProtocol = RemoteProtocol.valueOf(
                    savedInstanceState.getString(REMOTE_PROTOCOL_KEY, RemoteProtocol.HTTP.toString()));
        } else {
            mProtocol = RemoteProtocol.HTTP;
        }

        switch (mProtocol) {
            case HTTP:
                mRadioHttp.setChecked(true);
                break;
            case FTP:
                mRadioFtp.setChecked(true);
                break;
        }
        mActionButton.setText(getResources().getString(R.string.start_remote_server, mProtocol.toString()));

        mActionButton.setOnClickListener(this);
        mRadioFtp.setOnCheckedChangeListener(this);
        mRadioHttp.setOnCheckedChangeListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        updateWifiCondition();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(mConnectivityReceiver, filter);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mConnectivityReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(REMOTE_PROTOCOL_KEY, mProtocol.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_stop_button:

                if (mWifiStatus == WifiManager.WIFI_STATE_ENABLED) {
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new RemoteProtocolSelectEvent(System.currentTimeMillis(), mProtocol.toString()).buildIntentWithBundle());
                } else {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                }


                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked) return;
        switch (buttonView.getId()) {
            case R.id.radio_http:
                mProtocol = RemoteProtocol.HTTP;
                break;
            case R.id.radio_ftp:
                mProtocol = RemoteProtocol.FTP;
                break;
        }
        mActionButton.setText(getResources().getString(R.string.start_remote_server, mProtocol.toString()));
    }




    public void updateWifiCondition() {
        WifiManager wifiMgr = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiStatus = wifiMgr.getWifiState();
        // Manage the text of the wifi enable/disable button and the
        // wifi status text.
        switch(mWifiStatus) {
            case WifiManager.WIFI_STATE_ENABLED:
                mWifiStateImage.setImageResource(R.drawable.wifi_state4);
                mWifiStatusText.setText(R.string.enabled);
                mRadioGroup.setVisibility(View.VISIBLE);
                mActionButton.setText(getResources().getString(R.string.start_remote_server, mProtocol.toString()));
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                mWifiStateImage.setImageResource(R.drawable.wifi_state0);
                mWifiStatusText.setText(R.string.disabled);
                mRadioGroup.setVisibility(View.INVISIBLE);
                mActionButton.setText(R.string.open_wifi_settings);
                break;
            default:
                // We're in some transient state that will eventually
                // become one of the other two states.
                mWifiStateImage.setImageResource(R.drawable.wifi_state0);
                mWifiStatusText.setText(R.string.waiting);
                mRadioGroup.setVisibility(View.INVISIBLE);
                mActionButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

}
