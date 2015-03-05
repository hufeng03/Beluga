package com.hufeng.filemanager;

import com.hufeng.filemanager.data.FileEntry;

/**
 * Created by feng on 2014-10-26.
 */
public class FolderOpenEvent {

    public final long time;
    public final FileEntry entry;

    public static final String INTENT_ACTION = "DEVICE_SELECT_EVENT";

    public FolderOpenEvent(long time, FileEntry entry) {
        this.time =time;
        this.entry = entry;
    }


}
