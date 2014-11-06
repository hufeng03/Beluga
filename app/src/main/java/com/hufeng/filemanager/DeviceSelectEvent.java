package com.hufeng.filemanager;

import android.content.Intent;

/**
 * Created by feng on 2014-10-26.
 */
public class DeviceSelectEvent {

    public final long time;
    public final String path;

    public static final String INTENT_ACTION = "DEVICE_SELECT_EVENT";

    public DeviceSelectEvent(long time, String path) {
        this.time =time;
        this.path = path;
    }

    public Intent buildIntentWithBundle() {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra("time", time);
        intent.putExtra("path", path);
        return intent;
    }

    public DeviceSelectEvent(Intent intent) {
        this.time = intent.getLongExtra("time", 0);
        this.path = intent.getStringExtra("path");
    }


}
