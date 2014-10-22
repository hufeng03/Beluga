package com.hufeng.filemanager.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by feng on 2014-03-27.
 */
public class AppImageView extends ImageView {


    public AppImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        super.onDraw(canvas);
    }
}
