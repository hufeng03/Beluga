package com.hufeng.filemanager.browser;

import java.io.File;

public class FileEntry {
	public String path;
    public String name;
	public long size;
	public int type;
	public boolean hidden;
    public long lastModified;
    public boolean is_directory;
    public boolean is_readable;
    public boolean is_writable;
    public String parent_path;
    public boolean exist;

    public FileEntry() {

    }

    public FileEntry(String path) {
        File file = new File(path);
        if (file.exists())
            buildFromFile(file);
        else {
            this.exist = false;
            this.path = file.getAbsolutePath();
            this.name = file.getName();
        }
    }

    public FileEntry(File file) {
        if (file.exists())
            buildFromFile(file);
    }

    public FileEntry(String dir, String name) {
        File file = new File(dir, name);
        if (file.exists())
            buildFromFile(file);
    }

    public void buildFromFile (File file) {
        this.exist = true;
        this.path = file.getAbsolutePath();
        this.name = file.getName();
        this.size = file.length();
        this.lastModified = file.lastModified();
        this.type = FileUtils.getFileType(file);
        this.hidden = file.isHidden();
        this.is_directory = file.isDirectory();
        this.is_writable = file.canWrite();
        this.is_readable = file.canRead();
        File parent = file.getParentFile();
        if (parent != null) {
            this.parent_path = parent.getAbsolutePath();
        }
    }

    public String getName () {
        return name;
    }

    public long length () {
        return size;
    }

    public boolean isDirectory () {
        return is_directory;
    }

    public long lastModified () {
        return lastModified;
    }

    public String getParentPath() {
        return parent_path;
    }


    @Override
    public String toString() {
        return "path("+this.path+")"
                +"name("+this.name+")"
                +"is_directory("+this.is_directory+")"
                +"type("+this.type+")"
                +"size("+this.size+")"
                +"lastModified("+this.lastModified+")"
                +"hidden("+this.hidden+")"
                +"is_writable("+this.is_writable+")"
                +"is_readable("+this.is_readable+")";
    }
}
