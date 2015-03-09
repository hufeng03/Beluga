package com.hufeng.filemanager.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.hufeng.filemanager.BelugaEntry;
import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.mount.MountPointManager;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.MimeUtil;

import java.io.File;

public class BelugaFileEntry extends BelugaEntry {
	public String path;
    public String name;
	public long size;
    public int type;
	public int category;
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
    public boolean isFavorite;

    public BelugaFileEntry() {
        //This is used for Parcelable
    }

    public BelugaFileEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public BelugaFileEntry(File file) {
        init(file);
    }

    public BelugaFileEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

    public BelugaFileEntry(Cursor cursor) {
        this.path = cursor.getString(DataStructures.FileColumns.PATH_INDEX);
        this.name = cursor.getString(DataStructures.FileColumns.NAME_INDEX);
        this.size = cursor.getLong(DataStructures.FileColumns.SIZE_INDEX);
        this.extension = cursor.getString(DataStructures.FileColumns.EXTENSION_INDEX);
        this.lastModified = cursor.getLong(DataStructures.FileColumns.DATE_INDEX);
        this.isFavorite = !cursor.isNull(DataStructures.FileColumns.FAVORITE_ID_INDEX);

        this.category = FileCategoryHelper.getFileCategoryForFile(path);
        this.type = FileCategoryHelper.getFileTypeForFile(path);
    }

    public void fillContentValues(ContentValues cv) {
        cv.put(DataStructures.FileColumns.DATE, this.lastModified);
        cv.put(DataStructures.FileColumns.SIZE, this.size);
        cv.put(DataStructures.FileColumns.EXTENSION, this.extension);
        cv.put(DataStructures.FileColumns.PATH, this.path);
        cv.put(DataStructures.FileColumns.NAME, this.name);
        cv.put(DataStructures.FileColumns.STORAGE, MountPointManager.getInstance().getRealMountPointPath(this.path));
    }


    protected void init (File file) {
        this.exist = file.exists();
        this.path = file.getAbsolutePath();
        this.name = file.getName();
        this.size = file.length();
        this.lastModified = file.lastModified()/1000;
        this.category = FileCategoryHelper.getFileCategoryForFile(path);
        this.type = FileCategoryHelper.getFileTypeForFile(path);
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

    public boolean checkExistance() {
        return new File(path).exists();
    }

    @Override
    public boolean equals(Object o) {
        return (o != null) && (o instanceof BelugaFileEntry) && ((BelugaFileEntry)o).path.equals(path);
    }

    public File getFile() {
        return new File(path);
    }

    public File getParentFile() {
        return new File(parentPath);
    }

    // This api should not be accessed from main thread
    public BelugaFileEntry[] listFiles() {
        String[] filenames = new File(path).list();
        if (filenames == null) {
            return null;
        }
        int count = filenames.length;
        BelugaFileEntry[] result = new BelugaFileEntry[count];
        for (int i = 0; i < count; ++i) {
            result[i] = new BelugaFileEntry(path, filenames[i]);
        }
        return result;
    }

    // This api should not be accessed from main thread
    public boolean delete() {
        boolean deleted = new File(path).delete();
        if (deleted)
            exist = false;
        return deleted;
    }

    // This api should not be accessed from main thread
    public boolean renameTo(File newPath) {
        boolean renamed = new File(path).renameTo(newPath);
        if (renamed)
            exist = false;
        return renamed;
    }


    @Override
    public String toString() {
        return "path("+this.path+")"
                +"name("+this.name+")"
                +"size("+this.size+")"
                +"type("+this.type+")"
                +"category("+this.category+")"
                +"hidden("+this.hidden+")"
                +"exists("+this.exist+")"
                +"parent_path("+this.parentPath+")"
                +"lastModified("+this.lastModified+")"
                +"is_directory("+this.isDirectory+")"
                +"is_writable("+this.isWritable+")"
                +"is_readable("+this.isReadable+")"
                +"extension("+this.extension+")"
                +"isFavorite("+this.isFavorite+")";
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
        dest.writeInt(category);
        dest.writeInt(hidden? 1 : 0);
        dest.writeInt(isDirectory? 1 : 0);
        dest.writeInt(isWritable? 1 : 0);
        dest.writeInt(isReadable? 1 : 0);
        dest.writeString(parentPath);
        dest.writeInt(childFileCount);
        dest.writeInt(childFolderCount);
        dest.writeString(extension);
        dest.writeInt(isFavorite? 1 : 0);
    }

    public static final Creator<BelugaFileEntry> CREATOR = new Creator<BelugaFileEntry>() {
        @Override
        public BelugaFileEntry createFromParcel(Parcel source) {
            BelugaFileEntry entry = new BelugaFileEntry();
            entry.exist = source.readInt() == 1;
            entry.path = source.readString();
            entry.name = source.readString();
            entry.size = source.readLong();
            entry.lastModified = source.readLong();
            entry.type = source.readInt();
            entry.category = source.readInt();
            entry.hidden = source.readInt() == 1;
            entry.isDirectory = source.readInt() == 1;
            entry.isWritable = source.readInt() == 1;
            entry.isReadable = source.readInt() == 1;
            entry.parentPath = source.readString();
            entry.childFileCount = source.readInt();
            entry.childFolderCount = source.readInt();
            entry.extension = source.readString();
            entry.isFavorite = source.readInt() == 1;
            return entry;
        }

        @Override
        public BelugaFileEntry[] newArray(int size) {
            return new BelugaFileEntry[size];
        }
    };

    public static BelugaFileEntry[] toFileEntries(Parcelable[] parcelables) {
        BelugaFileEntry[] objects = new BelugaFileEntry[parcelables.length];
        System.arraycopy(parcelables, 0, objects, 0, parcelables.length);
        return objects;
    }

}
