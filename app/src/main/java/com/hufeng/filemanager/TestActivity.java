package com.hufeng.filemanager;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.hufeng.filemanager.resource.FileDownloader;

import com.hufeng.filemanager.server.BelugaServer;

import org.java_websocket.drafts.Draft_17;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * Created by feng on 2014-06-30.
 */
public class TestActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Button button = new Button(this);
        setContentView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FileDownloader.downloadFile(TestActivity.this.getApplicationContext(), Constants.KANBOX_APK_URL, Environment.getExternalStorageDirectory().getAbsolutePath(),"test.apk");
                int port = 8080;
                try {
                    new BelugaServer(port, new Draft_17()).start();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

//                button.setText(getWifiIp().getHostAddress()+":8080");

            }
        });
    }


    /**
     * Gets the IP address of the wifi connection.
     * @return The integer IP address if wifi enabled, or null if not.
     */
//    public static InetAddress getWifiIp() {
////		Context myContext = Globals.getContext();
////		if(myContext == null) {
////			throw new NullPointerException("Global context is null");
////		}
//        WifiManager wifiMgr = (WifiManager)FileManager.getAppContext()
//                .getSystemService(Context.WIFI_SERVICE);
////        if(isWifiEnabled()) {
//            int ipAsInt = wifiMgr.getConnectionInfo().getIpAddress();
//            if(ipAsInt == 0) {
//                return null;
//            } else {
//                return Util.intToInet(ipAsInt);
//            }
////        } else {
////            return null;
////        }
//    }
}
