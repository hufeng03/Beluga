package com.belugamobile.filemanager.data;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaZipEntry extends BelugaFileEntry {

    public BelugaZipEntry() {
        //This is used for Parcelable
    }

    public BelugaZipEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public BelugaZipEntry(File file) {
        init(file);
    }

    public BelugaZipEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

}
