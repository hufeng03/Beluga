package com.hufeng.filemanager.scan;

import java.io.File;

import com.hufeng.filemanager.data.DataStructures;
import com.hufeng.filemanager.data.DataStructures.AudioColumns;
import com.hufeng.filemanager.reader.Mp3Reader;

import android.content.ContentValues;

public class AudioObject extends FileObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3083424023816030746L;

	private long playDuration;
	
	private String album;

	private String singer;

	public long getPlayDuration() {
		return playDuration;
	}
	
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public void setPlayDuration(long playDuration) {
		this.playDuration = playDuration;
	}
	
	public AudioObject(String path) {
		super(path);
		// TODO Auto-generated constructor stub
		if(path.endsWith(".mp3"))
		{
			Mp3Reader reader = new Mp3Reader(new File(path));
			this.singer = reader.getSinger();
			this.album = reader.getAlbum();
		}
	}
	
	public void toContentValues(ContentValues cv)
	{
		super.toContentValues(cv);
		cv.put(DataStructures.AudioColumns.PLAY_DURATION_FIELD, this.playDuration);
		cv.put(DataStructures.AudioColumns.ALBUM_FIELD, this.album);
		cv.put(DataStructures.AudioColumns.SINGER_FIELD, this.singer);
		
	}
	
}
