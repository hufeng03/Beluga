package com.hufeng.filemanager.data;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class DocumentEntry extends FileEntry{

    public DocumentEntry() {
        //This is used for Parcelable
    }

    public DocumentEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public DocumentEntry(File file) {
        init(file);
    }

    public DocumentEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

}
