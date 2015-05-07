package com.belugamobile.filemanager;

/**
 * Created by feng on 2014-10-26.
 */
public class FileDeleteEvent {

    public final long time;
    public final String path;

    public FileDeleteEvent(long time, String path) {
        this.time =time;
        this.path = path;
    }

}
