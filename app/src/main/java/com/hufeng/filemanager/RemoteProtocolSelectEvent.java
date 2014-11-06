package com.hufeng.filemanager;

import android.content.Intent;

/**
 * Created by feng on 2014-10-26.
 */
public class RemoteProtocolSelectEvent {

    public final long time;
    public final String protocol;

    public static final String INTENT_ACTION = "REMOTE_PROTOCOL_SELECT_EVENT";

    public RemoteProtocolSelectEvent(long time, String protocol) {
        this.time =time;
        this.protocol = protocol;
    }

    public Intent buildIntentWithBundle() {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra("time", time);
        intent.putExtra("protocol", protocol);
        return intent;
    }

    public RemoteProtocolSelectEvent(Intent intent) {
        this.time = intent.getLongExtra("time", 0);
        this.protocol = intent.getStringExtra("protocol");
    }


}
