package com.belugamobile.filemanager.data;

import android.content.ContentValues;

import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.provider.DataStructures;
import com.belugamobile.filemanager.reader.Mp3Reader;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaAudioEntry extends BelugaFileEntry {

    private long playDuration;
    private String album;
    private String singer;

    public BelugaAudioEntry() {
        //This is used for Parcelable
    }

    public BelugaAudioEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public BelugaAudioEntry(File file) {
        init(file);
    }

    public BelugaAudioEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

    @Override
    protected void init(File file) {
        super.init(file);
        if (FileCategoryHelper.FILE_TYPE_MP3 == this.type) {
            Mp3Reader reader = new Mp3Reader(file);
            this.singer = reader.getSinger();
            this.album = reader.getAlbum();
        }
    }

    @Override
    public void fillContentValues(ContentValues cv) {
        super.fillContentValues(cv);

        cv.put(DataStructures.AudioColumns.PLAY_DURATION, this.playDuration);
        cv.put(DataStructures.AudioColumns.ALBUM, this.album);
        cv.put(DataStructures.AudioColumns.SINGER, this.singer);
    }
}
