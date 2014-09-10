package com.hufeng.filemanager;

import android.content.Intent;

/**
 * Created by feng on 2014-06-26.
 */
public class FileDownloadEvent {

    public static final String INTENT_ACTION = "FILE_DOWNLOAD_EVENT";

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


    public Intent buildIntentWithBundle() {
        Intent intent = new Intent(INTENT_ACTION);
        intent.putExtra("url", url);
        intent.putExtra("path", path);
        intent.putExtra("progress", progress);
        intent.putExtra("status", status);
        return intent;
    }

    public FileDownloadEvent(Intent intent) {
        this.url = intent.getStringExtra("url");
        this.path = intent.getStringExtra("path");
        this.progress = intent.getIntExtra("progress", 0);
        this.status = intent.getIntExtra("status", 0);
    }
}
