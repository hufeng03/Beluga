package com.hufeng.filemanager.scan;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.data.DataStructures;

import java.io.File;
import java.io.InputStream;

public class ImageObject extends FileObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6540261557330003663L;

	private int imageWidth;
	private int imageHeight;
	
	public int getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	
	public ImageObject(String path) {
		super(path);

        fetchImageResolution(path);
	}

    private void fetchImageResolution(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();

        try{
            InputStream is = FileManager.getAppContext().getContentResolver().openInputStream(Uri.fromFile(new File(path)));
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(is, null, opts);
            imageWidth = opts.outWidth;
            imageHeight = opts.outHeight;
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
	
	public void toContentValues(ContentValues cv)
	{
		super.toContentValues(cv);
		cv.put(DataStructures.ImageColumns.IMAGE_WIDTH_FIELD, this.imageWidth);
		cv.put(DataStructures.ImageColumns.IMAGE_HEIGHT_FIELD, this.imageHeight);
		
	}
	
}
