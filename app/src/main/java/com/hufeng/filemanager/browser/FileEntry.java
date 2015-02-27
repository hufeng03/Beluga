package com.hufeng.filemanager.browser;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.hufeng.filemanager.BelugaEntry;
import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.MimeUtil;

import java.io.File;

public class FileEntry extends BelugaEntry {
	public String path;
    public String name;
	public long size;
	public int type;
	public boolean hidden;
    public String parentPath;
    public boolean exist;
    public long lastModified;
    public boolean isDirectory;
    public boolean isReadable;
    public boolean isWritable;
    public int childFileCount;
    public int childFolderCount;
    public String extension;

    public FileEntry() {
        //This is used for Parcelable
    }

    public FileEntry(String identity) {
        File file = new File(identity);
        init(file);
    }

    public FileEntry(File file) {
        init(file);
    }

    public FileEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

    public FileEntry(Cursor cursor) {
        this.path = cursor.getString(DataStructures.FileColumns.FILE_PATH_FIELD_INDEX);
        File file = new File(this.path);
        init(file);
    }

    private void init (File file) {
        this.exist = file.exists();
        this.path = file.getAbsolutePath();
        this.name = file.getName();
        this.size = file.length();
        this.lastModified = file.lastModified();
        this.type = FileCategoryHelper.getFileCategoryForFile(path);
        this.hidden = file.isHidden();
        this.isDirectory = file.isDirectory();
        this.isWritable = file.canWrite();
        this.isReadable = file.canRead();
        File parent = file.getParentFile();
        if (parent != null) {
            this.parentPath = parent.getAbsolutePath();
        }
        if (this.isDirectory) {
            File[] children = file.listFiles();
            this.childFileCount = 0;
            this.childFolderCount = 0;
            for (File child:children) {
                if (child.isDirectory()) {
                    this.childFileCount++;
                } else {
                    this.childFolderCount++;
                }
            }
        } else {
            this.extension = MimeUtil.getExtension(path);
        }
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
    public long getSize () {
        return size;
    }

    @Override
    public long getTime () {
        return lastModified;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public boolean checkExistance() {
        return new File(path).exists();
    }

    @Override
    public boolean equals(Object o) {
        return (o != null) && (o instanceof FileEntry) && ((FileEntry)o).path.equals(path);
    }

    @Override
    public String toString() {
        return "path("+this.path+")"
                +"name("+this.name+")"
                +"size("+this.size+")"
                +"type("+this.type+")"
                +"hidden("+this.hidden+")"
                +"exists("+this.exist+")"
                +"parent_path("+this.parentPath+")"
                +"lastModified("+this.lastModified+")"
                +"is_directory("+this.isDirectory+")"
                +"is_writable("+this.isWritable+")"
                +"is_readable("+this.isReadable+")"
                +"extension("+this.extension+")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(exist? 1 : 0);
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(size);
        dest.writeLong(lastModified);
        dest.writeInt(type);
        dest.writeInt(hidden? 1 : 0);
        dest.writeInt(isDirectory? 1 : 0);
        dest.writeInt(isWritable? 1 : 0);
        dest.writeInt(isReadable? 1 : 0);
        dest.writeString(parentPath);
        dest.writeInt(childFileCount);
        dest.writeInt(childFolderCount);
        dest.writeString(extension);
    }

    public static final Creator<FileEntry> CREATOR = new Creator<FileEntry>() {
        @Override
        public FileEntry createFromParcel(Parcel source) {
            FileEntry entry = new FileEntry();
            entry.exist = source.readInt() == 1;
            entry.path = source.readString();
            entry.name = source.readString();
            entry.size = source.readLong();
            entry.lastModified = source.readLong();
            entry.type = source.readInt();
            entry.hidden = source.readInt() == 1;
            entry.isDirectory = source.readInt() == 1;
            entry.isWritable = source.readInt() == 1;
            entry.isReadable = source.readInt() == 1;
            entry.parentPath = source.readString();
            entry.childFileCount = source.readInt();
            entry.childFolderCount = source.readInt();
            entry.extension = source.readString();
            return entry;
        }

        @Override
        public FileEntry[] newArray(int size) {
            return new FileEntry[size];
        }
    };

    public static FileEntry[] toFileEntries(Parcelable[] parcelables) {
        FileEntry[] objects = new FileEntry[parcelables.length];
        System.arraycopy(parcelables, 0, objects, 0, parcelables.length);
        return objects;
    }

}
