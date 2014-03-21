package com.hufeng.playimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

public abstract class BaseLazyLoadImageView extends ImageView {

    protected String currentUri;
    protected String targetUri;


    public BaseLazyLoadImageView(Context context) {
        super(context);
    }

    public BaseLazyLoadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void requestDisplayImage(String uri) {
        if (TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("the uri can not be NULL");
        }

        if (uri.equals(currentUri)) {
            return;
        }
        targetUri = uri;
        ImageLoader.getInstance().displayImage(this, uri);
    }

    public void setImageBitmap(Bitmap bm, String url) {
        super.setImageBitmap(bm);
        currentUri = url;
    }

    public void setImageDrawable(Drawable drawable, String url) {
        super.setImageDrawable(drawable);
        currentUri = url;
    }

    public void setImageResource(int resId, String url){
        super.setImageResource(resId);
        currentUri = url;
    }

    public boolean setImageBitmapIfNeeds(Bitmap bm, String uri) {
        if (uri.equals(targetUri)) {
            this.setImageBitmap(bm, uri);
            return true;
        }
        return false;
    }

    public abstract void useDefaultBitmap();

}
