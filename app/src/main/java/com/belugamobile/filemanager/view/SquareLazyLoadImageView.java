package com.belugamobile.filemanager.view;

import android.content.Context;
import android.util.AttributeSet;

import com.hufeng.playimage.BelugaLazyLoadImageView;

/**
 * Created by feng on 14-2-10.
 */
public class SquareLazyLoadImageView extends BelugaLazyLoadImageView {
    private static final String TAG = SquareLazyLoadImageView.class.getSimpleName();

    public SquareLazyLoadImageView(Context context) {
        super(context);
    }

    public SquareLazyLoadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (getDrawable() != null && getDrawable() instanceof BitmapDrawable) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        } else {
            int width = getDefaultSize(0, widthMeasureSpec);
            heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//        }
    }
}
