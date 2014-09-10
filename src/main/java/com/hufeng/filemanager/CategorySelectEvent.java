package com.hufeng.filemanager;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by feng on 2014-06-26.
 */
public class CategorySelectEvent {

    public final long time;
    public final int category;

    public static final String INTENT_ACTION = "CATEGORY_SELECT_EVENT";

    public CategorySelectEvent(long time, int category) {
        this.time =time;
        this.category = category;
    }

    public Intent buildIntentWithBundle() {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra("time", time);
        intent.putExtra("category", category);
        return intent;
    }

    public CategorySelectEvent(Intent intent) {
        this.time = intent.getLongExtra("time", 0);
        this.category = intent.getIntExtra("category", 0);
    }
}
