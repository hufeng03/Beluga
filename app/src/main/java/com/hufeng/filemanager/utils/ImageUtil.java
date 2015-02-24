package com.hufeng.filemanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
	
	private static final String LOG_TAG = ImageUtil.class.getName();
	
	public static Uri saveBitmapToSDCard(Context context, Bitmap image, File file)
	{
	   	 Uri uri = null;
	   	 if(image==null || file==null)
	   		 return null;
	   	 if(file.exists())
	   	 {
	   		 boolean rst = file.delete();
	   		 if(!rst)
	   			 return null;
	   	 }
		 try{
	       FileOutputStream fileOS = new FileOutputStream(file);
	       uri = Uri.fromFile(file);
	       image.compress(Bitmap.CompressFormat.JPEG, 80, fileOS);
	       fileOS.close();		
		 }catch(Exception e)
		 {
		     e.printStackTrace();
		 }
		 return uri;
	}
	
	public static Bitmap loadBitmapWithSizeLimitation(Context context, int maximum_size, Uri uri)
	{		
	    Bitmap image = null;
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    
        try{
            InputStream is = context.getContentResolver().openInputStream(uri);
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(is, null, opts);
            int size = opts.outWidth*opts.outHeight;
            int downScale = 1;
            while(size>maximum_size){
                size = (size>>2);
                downScale = (downScale<<1);
            }
            try {
                 is.close();
            } catch (java.io.IOException e) {
                  e.printStackTrace();
            }
            
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = downScale;
            is = context.getContentResolver().openInputStream(uri);
            image = BitmapFactory.decodeStream(is, null, opts);
            try {
                is.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            if(image!=null){
	            int width = image.getWidth();
	            int height = image.getHeight();
	            
	            if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "compressed image width and height is "+width+" "+height);
            }
        }catch(java.io.FileNotFoundException e){
             e.printStackTrace();
        }catch (OutOfMemoryError e) {
            if (LogUtil.VDBG) {
                e.printStackTrace();
            }
            // Do nothing - the photo will appear to be missing
        } catch (Exception e) {
            if (LogUtil.VDBG) {
                e.printStackTrace();
            }
        }
//      saveBitmapWithGivenSize(context,image);
        return image;
	}
	
    public static int getImageRotateDegree(String filepath) {
        ExifInterface exif = null;
          int orientation = -1;
          try {
            LogUtil.d("ShowAttachment", "filepath = "+filepath);
            exif = new ExifInterface(filepath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            LogUtil.d("ShowAttachment", "orientation = "+orientation);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            LogUtil.d("ShowAttachment", "read exif error");
        }
          int degree = 0;
          switch(orientation){
              case ExifInterface.ORIENTATION_ROTATE_90:
                  degree = 90;
                  break;
              case ExifInterface.ORIENTATION_ROTATE_180:
                  degree = 180;
                  break;
              case ExifInterface.ORIENTATION_ROTATE_270:
                  degree = 270;
                  break;
          }
        return degree;
    }

    public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
// canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
