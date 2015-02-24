package com.hufeng.filemanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcelable;

/**
 * Created by Feng Hu on 15-01-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public abstract class BelugaEntry implements BelugaSortableInterface, Parcelable{
    public abstract String getIdentity();

    public interface CursorInterface {
        public BelugaEntry fromCursor(Cursor cursor);
        public ContentValues toContentValues(BelugaEntry entry);
    }
}
