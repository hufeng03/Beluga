package com.hufeng.filemanager.browser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.hufeng.filemanager.FileManager;

import java.io.File;
import java.io.InputStream;

/**
 * Created by feng on 14-1-10.
 */
public class ImageEntry extends FileEntry {

    int width;
    int height;
    public ImageEntry(String path) {
        super(path);
        fetchImageWidthAndHeight(path);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void fetchImageWidthAndHeight(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        try{
            InputStream is = FileManager.getAppContext().getContentResolver().openInputStream(Uri.fromFile(new File(path)));
            opts.inJustDecodeBounds = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeStream(is, null, opts);
            width = opts.outWidth;
            height = opts.outHeight;
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

}
