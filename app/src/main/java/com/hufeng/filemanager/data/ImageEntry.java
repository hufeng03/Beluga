package com.hufeng.filemanager.data;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.provider.DataStructures;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class ImageEntry extends FileEntry{

    public int imageWidth;
    public int imageHeight;

    public ImageEntry() {
        //This is used for Parcelable
    }

    public ImageEntry(String path) {
        File file = new File(path);
        init(file);
    }

    public ImageEntry(File file) {
        init(file);
    }

    public ImageEntry(String dir, String name) {
        File file = new File(dir, name);
        init(file);
    }

    @Override
    protected void init(File file) {
        super.init(file);
        fetchImageResolution(file);
    }

    @Override
    public void fillContentValues(ContentValues cv) {
        super.fillContentValues(cv);
        cv.put(DataStructures.ImageColumns.IMAGE_WIDTH, imageWidth);
        cv.put(DataStructures.ImageColumns.IMAGE_HEIGHT, imageHeight);
    }

    private void fetchImageResolution(File image) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        InputStream is = null;
        try{
            is = FileManager.getAppContext().getContentResolver().openInputStream(Uri.fromFile(image));
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(is, null, opts);
            imageWidth = opts.outWidth;
            imageHeight = opts.outHeight;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
