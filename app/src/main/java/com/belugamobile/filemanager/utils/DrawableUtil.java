package com.belugamobile.filemanager.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Feng Hu on 15-03-30.
 * <p/>
 * TODO: Add a class header comment.
 */
public class DrawableUtil {

    public static Bitmap getBitmapFromDrawable(Drawable icon) {
        if (icon instanceof BitmapDrawable) {
            return ((BitmapDrawable)icon).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            icon.draw(canvas);
            return bitmap;
        }
    }

}
