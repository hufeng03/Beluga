package com.belugamobile.filemanager.ui;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by Feng Hu on 15-04-02.
 * <p/>
 * TODO: Add a class header comment.
 */
public class TextViewPager extends ViewPager{

    private static final String TAG = "TextViewPager";

    public TextViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean result =  super.onInterceptTouchEvent(ev);
////        if (result) {
////            gestureDetectorCompat.onTouchEvent(ev);
////        }
//        return result;
//    }
//
//    @Override
//    public boolean onGenericMotionEvent(MotionEvent event) {
//        boolean result =  super.onGenericMotionEvent(event);
//        if (result) {
//            gestureDetectorCompat.onTouchEvent(event);
//        }
//        return result;
//    }
}
