package com.belugamobile.filemanager.data;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaApkEntry extends BelugaFileEntry {
    public BelugaApkEntry() {
        //This is used for Parcelable
    }

    public BelugaApkEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public BelugaApkEntry(File file) {
        init(file);
    }

    public BelugaApkEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }
}
