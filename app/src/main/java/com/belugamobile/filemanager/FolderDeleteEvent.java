package com.belugamobile.filemanager;

/**
 * Created by feng on 2014-10-26.
 */
public class FolderDeleteEvent {

    public final long time;
    public final String path;

    public FolderDeleteEvent(long time, String path) {
        this.time =time;
        this.path = path;
    }

}
