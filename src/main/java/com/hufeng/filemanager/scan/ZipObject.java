package com.hufeng.filemanager.scan;


import android.content.ContentValues;

public class ZipObject extends FileObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9185112274114109794L;

	public ZipObject(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public void toContentValues(ContentValues cv)
	{
		super.toContentValues(cv);
	}
}
