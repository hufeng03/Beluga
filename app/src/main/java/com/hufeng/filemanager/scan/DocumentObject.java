package com.hufeng.filemanager.scan;


import android.content.ContentValues;

public class DocumentObject extends FileObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6238590582262621850L;

	public DocumentObject(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public void toContentValues(ContentValues cv)
	{
//		cv.put(key, value)
		super.toContentValues(cv);
		
	}
}
