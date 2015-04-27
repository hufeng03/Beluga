package com.belugamobile.filemanager;

import android.content.Intent;

/**
 * Created by feng on 2014-10-26.
 */
public class FolderSelectEvent {

    public final long time;
    public final String path;
    public final String root;

    public static final String INTENT_ACTION = "DEVICE_SELECT_EVENT";

    public FolderSelectEvent(long time, String path, String root) {
        this.time =time;
        this.path = path;
        this.root = root;
    }

}
