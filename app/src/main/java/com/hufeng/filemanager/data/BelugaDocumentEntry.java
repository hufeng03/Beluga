package com.hufeng.filemanager.data;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaDocumentEntry extends BelugaFileEntry {

    public BelugaDocumentEntry() {
        //This is used for Parcelable
    }

    public BelugaDocumentEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public BelugaDocumentEntry(File file) {
        init(file);
    }

    public BelugaDocumentEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

}
