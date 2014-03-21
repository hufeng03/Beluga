package com.hufeng.filemanager.scan;

import android.content.ContentValues;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.data.DataStructures;
import com.hufeng.filemanager.storage.StorageManager;

import java.io.File;
import java.io.Serializable;

public class FileObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6630851319619423077L;
	
	private String path;
	private String name;
//	private int type;
	private long size;
	private String extension;
	private long date;
	private boolean hidden;
	private String directory;
	
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
//	public int getType() {
//		return type;
//	}
//	public void setType(int type) {
//		this.type = type;
//	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	public String getDirectory()
	{
		return directory;
	}
	
	public FileObject(String path)
	{
		this.path = path;
		int idx = path.lastIndexOf(".");
		if(idx>-1 && idx<path.length()-1)
		{
			this.extension = path.substring(idx+1);
		}
		File file = new File(this.path);
		if(file.exists())
		{
			this.directory = file.getParent();
			this.hidden = file.isHidden();
			this.name = file.getName();
//			if(file.isDirectory())
//				this.size = 0;
//			else
			this.size = file.length();
			this.date = file.lastModified()/1000;
		}
//		this.type = 0;
	}
	
	public void toContentValues(ContentValues cv)
	{
//		cv.put(DataStructures.FileColumns.FILE_TYPE_FIELD, this.type);
		cv.put(DataStructures.FileColumns.FILE_DATE_FIELD, this.date);
		cv.put(DataStructures.FileColumns.FILE_SIZE_FIELD, this.size);
		cv.put(DataStructures.FileColumns.FILE_EXTENSION_FIELD, this.extension);
		cv.put(DataStructures.FileColumns.FILE_PATH_FIELD, this.path);
		cv.put(DataStructures.FileColumns.FILE_NAME_FIELD, this.name);
		cv.put(DataStructures.FileColumns.FILE_STORAGE_FIELD, StorageManager.getInstance(FileManager.getAppContext()).getStoragePath(this.path));
	}
	
	
	
	
	
	
}
