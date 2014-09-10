package com.hufeng.filemanager;

import android.content.Intent;

/**
 * Created by feng on 2014-06-26.
 */
public class KanboxAuthStartEvent {

    public final long time;

    public static final String INTENT_ACTION = "KANBOX_AUTH_START_EVENT";

    public KanboxAuthStartEvent(long time) {
        this.time =time;
    }

    public Intent buildIntentWithBundle() {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra("time", time);
        return intent;
    }

    public KanboxAuthStartEvent(Intent intent) {
        this.time = intent.getLongExtra("time", 0);
    }
}
