package com.hufeng.filemanager;

/**
 * Created by feng on 2014-06-26.
 */
public class CloudUploadCategorySelectEvent {


    public final long time;
    public final CategorySelectEvent.CategoryType category;

//    public static final String INTENT_ACTION = "CATEGORY_SELECT_EVENT";

    public CloudUploadCategorySelectEvent(long time, CategorySelectEvent.CategoryType category) {
        this.time =time;
        this.category = category;
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
