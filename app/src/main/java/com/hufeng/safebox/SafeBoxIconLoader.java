package com.hufeng.safebox;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by feng on 13-10-10.
 */
public class SafeBoxIconLoader {

    private static class BitmapHolder{
        SoftReference<Bitmap> bitmapRef;

        public boolean setImageView(ImageView v) {
            if (bitmapRef.get() == null)
                return false;
            v.setScaleType(ImageView.ScaleType.CENTER_CROP);
            v.setImageBitmap(bitmapRef.get());
            return true;
        }

        public boolean isNull() {
            return bitmapRef == null;
        }

        public void setBitmap(Bitmap image) {
            bitmapRef = image == null ? null : new SoftReference<Bitmap>((Bitmap) image);
        }

        public Bitmap getBitmap() {
            return bitmapRef.get();
        }
    }

    /**
     * A soft cache for image thumbnails. the key is file path
     */
    private final static ConcurrentHashMap<String, BitmapHolder> mImageCache = new ConcurrentHashMap<String, BitmapHolder>();

    private static SafeBoxIconLoader instance = null;

    public static SafeBoxIconLoader getInstance()
    {
        if(instance==null)
            instance = new SafeBoxIconLoader();
        return instance;
    }

    private SafeBoxIconLoader() {

    }

//    public void loadIcon(ImageView view, String path){
//        BitmapHolder holder = mImageCache.get(path);
//        holder.setImageView(view);
//    }

    public Bitmap getIcon(String path){
        Bitmap bitmap = null;
        BitmapHolder holder = mImageCache.get(path);
        if (holder != null)
           bitmap = holder.getBitmap();
        return bitmap;
    }

    public void saveIcon(String path, Bitmap bm) {
        BitmapHolder holder = mImageCache.get(path);
        if (holder == null) {
            holder = new BitmapHolder();
            holder.setBitmap(bm);
            mImageCache.put(path, holder);
        } else {
            holder.setBitmap(bm);
        }
    }



}
