package com.hufeng.filemanager.data;

import android.content.ContentValues;

import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.reader.Mp3Reader;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class AudioEntry extends FileEntry {

    private long playDuration;
    private String album;
    private String singer;

    public AudioEntry() {
        //This is used for Parcelable
    }

    public AudioEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public AudioEntry(File file) {
        init(file);
    }

    public AudioEntry(String dir, String name) {
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

        cv.put(DataStructures.AudioColumns.PLAY_DURATION_FIELD, this.playDuration);
        cv.put(DataStructures.AudioColumns.ALBUM_FIELD, this.album);
        cv.put(DataStructures.AudioColumns.SINGER_FIELD, this.singer);
    }
}
