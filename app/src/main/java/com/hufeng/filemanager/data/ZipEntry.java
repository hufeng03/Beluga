package com.hufeng.filemanager.data;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class ZipEntry extends FileEntry {

    public ZipEntry() {
        //This is used for Parcelable
    }

    public ZipEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public ZipEntry(File file) {
        init(file);
    }

    public ZipEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

}
