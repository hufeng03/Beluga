package com.belugamobile.filemanager.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.belugamobile.filemanager.BelugaEntry;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.utils.MimeUtil;

import java.io.File;
import java.util.zip.ZipEntry;

/**
 * Created by Feng Hu on 15-03-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaZipElementEntry extends BelugaEntry{

    public String path;
    public String parentPath;
    public String name;
    public long size;
    public long time;
    public boolean isDirectory;
    public int category;
    public int type;
    public boolean hidden;

    public int childFileCount;
    public int childFolderCount;

    public BelugaZipElementEntry() {
        //This is used for Parcelable
    }


    public BelugaZipElementEntry(String path, boolean isDirectory, long size, long time) {
        this.path = path;
        int idx = this.path.lastIndexOf("/");
        this.name = this.path.substring(idx+1);
        this.parentPath = this.path.substring(0, idx);
        this.size = size;
        this.time = time;
        this.isDirectory = isDirectory;
        this.hidden = name.startsWith(".");
        String extension = MimeUtil.getExtension(this.path);
        this.category = FileCategoryHelper.getFileCategoryForExtension(extension);
        this.type = FileCategoryHelper.getFileTypeForExtension(extension);

    }

    @Override
    public String getIdentity() {
        return path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(size);
        dest.writeLong(time);
        dest.writeInt(type);
        dest.writeInt(category);
        dest.writeInt(hidden? 1 : 0);
        dest.writeInt(isDirectory? 1 : 0);
    }

    public static final Creator<BelugaZipElementEntry> CREATOR = new Creator<BelugaZipElementEntry>() {
        @Override
        public BelugaZipElementEntry createFromParcel(Parcel source) {
            BelugaZipElementEntry entry = new BelugaZipElementEntry();
            entry.path = source.readString();
            entry.name = source.readString();
            entry.size = source.readLong();
            entry.time = source.readLong();
            entry.type = source.readInt();
            entry.category = source.readInt();
            entry.hidden = source.readInt() == 1;
            entry.isDirectory = source.readInt() == 1;
            return entry;
        }

        @Override
        public BelugaZipElementEntry[] newArray(int size) {
            return new BelugaZipElementEntry[size];
        }
    };

    public static BelugaZipElementEntry[] toZipElementEntries(Parcelable[] parcelables) {
        BelugaZipElementEntry[] objects = new BelugaZipElementEntry[parcelables.length];
        System.arraycopy(parcelables, 0, objects, 0, parcelables.length);
        return objects;
    }
}
