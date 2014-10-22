package com.hufeng.filemanager;

import android.content.Intent;

/**
 * Created by feng on 2014-06-26.
 */
public class KanboxAuthResultEvent {

    public final long time;
    public final boolean success;

    public static final String INTENT_ACTION = "KANBOX_AUTH_RESULT_EVENT";

    public KanboxAuthResultEvent(long time, boolean success) {
        this.time =time;
        this.success = success;
    }

    public Intent buildIntentWithBundle() {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra("time", time);
        intent.putExtra("success", success);
        return intent;
    }

    public KanboxAuthResultEvent(Intent intent) {
        this.time = intent.getLongExtra("time", 0);
        this.success = intent.getBooleanExtra("success", false);
    }
}
