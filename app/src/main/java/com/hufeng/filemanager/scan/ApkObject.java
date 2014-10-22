package com.hufeng.filemanager.scan;


import android.content.ContentValues;

public class ApkObject extends FileObject{


	/**
	 * 
	 */
	private static final long serialVersionUID = -8867779432411656983L;

	public ApkObject(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public void toContentValues(ContentValues cv)
	{
//		cv.put(key, value)
		super.toContentValues(cv);
		
	}
}
