package com.belugamobile.filemanager;

/**
 * Created by feng on 2014-10-26.
 */
public class FolderShowEvent {

    public final long time;
    public final String path;
    public final String root;

    public FolderShowEvent(long time, String path, String root) {
        this.time =time;
        this.path = path;
        this.root = root;
    }

}
