package com.belugamobile.filemanager;

/**
 * Created by feng on 2014-10-26.
 */
public class FolderCreateEvent {

    public final long time;
    public final String path;

    public FolderCreateEvent(long time, String path) {
        this.time =time;
        this.path = path;
    }

}
