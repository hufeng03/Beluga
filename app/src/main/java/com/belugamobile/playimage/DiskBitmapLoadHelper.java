package com.belugamobile.playimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class DiskBitmapLoadHelper {

	static final int MAX_WIDTH = 1024;
	static final int MAX_HEIGHT = 1024;

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	public static Bitmap decodeSampledBitmapFromFile(String filename) {
		return decodeSampledBitmapFromFile(filename, MAX_WIDTH, MAX_HEIGHT);
	}

}
