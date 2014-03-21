package com.hufeng.filemanager.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by feng on 13-12-9.
 */
public class FileViewPager extends ViewPager {

    private static final String TAG = FileViewPager.class.getSimpleName();

    private boolean enabled;

    public FileViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
        Log.i(TAG, "setPagingEnabled " + enabled);
    }

    public boolean getPagingEnabled() {
        return this.enabled;
    }
}
