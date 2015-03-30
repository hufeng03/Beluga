package com.belugamobile.filemanager;

/**
 * Created by feng on 2014-06-26.
 */
public class TabLongPressEvent {

    public final long time;
    public final String name;

    public TabLongPressEvent(long time, String name) {
        this.time =time;
        this.name = name;
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
