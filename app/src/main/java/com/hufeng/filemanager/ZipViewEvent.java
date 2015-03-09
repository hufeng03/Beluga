package com.hufeng.filemanager;

/**
 * Created by feng on 2014-10-26.
 */
public class ZipViewEvent {

    public final long time;
    public final String path;

    public static final String INTENT_ACTION = "DEVICE_SELECT_EVENT";

    public ZipViewEvent(long time, String path) {
        this.time =time;
        this.path = path;
    }


}
