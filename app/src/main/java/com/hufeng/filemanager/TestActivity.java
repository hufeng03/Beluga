package com.hufeng.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.hufeng.filemanager.resource.FileDownloader;
import com.hufeng.swiftp.Account;

/**
 * Created by feng on 2014-06-30.
 */
public class TestActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        setContentView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDownloader.downloadFile(TestActivity.this.getApplicationContext(), Constants.KANBOX_APK_URL, Environment.getExternalStorageDirectory().getAbsolutePath(),"test.apk");
            }
        });
    }
}
