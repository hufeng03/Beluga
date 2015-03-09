package com.hufeng.filemanager;

import com.hufeng.filemanager.data.BelugaFileEntry;

/**
 * Created by feng on 2014-10-26.
 */
public class FolderOpenEvent {

    public final long time;
    public final BelugaFileEntry entry;

    public static final String INTENT_ACTION = "DEVICE_SELECT_EVENT";

    public FolderOpenEvent(long time, BelugaFileEntry entry) {
        this.time =time;
        this.entry = entry;
    }


}
