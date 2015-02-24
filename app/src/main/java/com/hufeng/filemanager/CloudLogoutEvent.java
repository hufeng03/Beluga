package com.hufeng.filemanager;

/**
 * Created by feng on 2014-06-26.
 */
public class CloudLogoutEvent {

    public final long time;

    public CloudLogoutEvent(long time) {
        this.time =time;
    }

//    public Intent buildIntentWithBundle() {
//        Intent intent = new Intent(INTENT_ACTION);
//        intent.putExtra("time", time);
//        intent.putExtra("category", category);
//        return intent;
//    }
//
//    public CategorySelectEvent(Intent intent) {
//        this.time = intent.getLongExtra("time", 0);
//        this.category = intent.getIntExtra("category", 0);
//    }
}
