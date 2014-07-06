package com.hufeng.filemanager;

/**
 * Created by feng on 2014-06-26.
 */
public class FileDownloadEvent {

    public final String url;
    public final String path;
    public final int progress;
    public final int status;

    public FileDownloadEvent(String url, String path, int status, int progress) {
        this.url = url;
        this.path = path;
        this.progress = progress;
        this.status = status;
    }

    @Override
    public String toString() {
        return "("+url+","+path+","+status+","+progress+")";
    }
}
