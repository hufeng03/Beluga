package com.hufeng.filemanager.data;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class ApkEntry extends FileEntry{
    public ApkEntry() {
        //This is used for Parcelable
    }

    public ApkEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public ApkEntry(File file) {
        init(file);
    }

    public ApkEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }
}
