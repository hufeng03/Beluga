package com.hufeng.filemanager;

/**
 * Created by feng on 2014-06-26.
 */
public class CategorySelectEvent {

    public final long time;
    public final int category;

    public CategorySelectEvent(long time, int category) {
        this.time =time;
        this.category = category;
    }
}
