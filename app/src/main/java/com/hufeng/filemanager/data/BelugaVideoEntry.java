package com.hufeng.filemanager.data;

import android.content.ContentValues;

import com.hufeng.filemanager.provider.DataStructures;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaVideoEntry extends BelugaFileEntry {

    public long playDuration;

    public BelugaVideoEntry() {
        //This is used for Parcelable
    }

    public BelugaVideoEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public BelugaVideoEntry(File file) {
        init(file);
    }

    public BelugaVideoEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

    @Override
    public void fillContentValues(ContentValues cv) {
        super.fillContentValues(cv);
        cv.put(DataStructures.VideoColumns.PLAY_DURATION, playDuration);
    }
}
