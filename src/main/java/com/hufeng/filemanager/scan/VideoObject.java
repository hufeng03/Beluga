package com.hufeng.filemanager.scan;

import com.hufeng.filemanager.data.DataStructures;
import com.hufeng.filemanager.data.DataStructures.VideoColumns;

import android.content.ContentValues;

public class VideoObject extends FileObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7176968525800738550L;

	/**
	 * 
	 */

	private long playDuration;
	
	private String album;

	private String singer;

	public long getPlayDuration() {
		return playDuration;
	}

	public void setPlayDuration(long playDuration) {
		this.playDuration = playDuration;
	}
	
	public VideoObject(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public void toContentValues(ContentValues cv)
	{
		super.toContentValues(cv);
		cv.put(DataStructures.VideoColumns.PLAY_DURATION_FIELD, this.playDuration);
		
	}
	
}
