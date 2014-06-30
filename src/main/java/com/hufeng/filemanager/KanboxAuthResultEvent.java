package com.hufeng.filemanager;

/**
 * Created by feng on 2014-06-26.
 */
public class KanboxAuthResultEvent {

    public final long time;
    public final boolean success;

    public KanboxAuthResultEvent(long time, boolean success) {
        this.time =time;
        this.success = success;
    }
}
