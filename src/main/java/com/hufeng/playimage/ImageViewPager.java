package com.hufeng.playimage;

/**
 * Created by feng on 14-1-19.
 */

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.hufeng.filemanager.BuildConfig;


public class ImageViewPager extends ViewPager {

    static final String TAG = "ImageViewPager";

    private OnPageChangeListener mOnPageChangeListener;

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrollStateChanged(state);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(arg0, arg1, arg2);
                }
            }

            @Override
            public void onPageSelected(int index) {
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageSelected(index);
                }
                mTouchDelgateView = findViewById((1 + index) * 1000);
                for (int i = 0; i < getChildCount(); i++) {
                    if (i != index) {
                        ((ZoomableImageView) getChildAt(i)).restore();
                    }
                }

            }

        });
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
        mTouchDelgateView = findViewById((1 + item) * 1000);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    private boolean mIsZoom = false;

    private float mInitX;

    private int mDragStatus = DRAG_UNKOWN;
    private static final int DRAG_UNKOWN = 0;
    private static final int CAN_DRAG = 1;
    private static final int CAN_NOT_DRAG = 2;
    private View mTouchDelgateView;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent me) {
        final View view = mTouchDelgateView;
        if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mIsZoom = false;
            mDragStatus = DRAG_UNKOWN;
            mInitX = me.getX();
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "down");
            }
        }

        if (me.getActionMasked() == MotionEvent.ACTION_CANCEL
                || me.getActionMasked() == MotionEvent.ACTION_UP) {
            mIsZoom = false;
            mDragStatus = DRAG_UNKOWN;
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "up");
            }

        }

        if (me.getActionMasked() != MotionEvent.ACTION_DOWN && !mIsZoom
                && mDragStatus == CAN_NOT_DRAG) {
            return super.onInterceptTouchEvent(me);
        }

        if (mDragStatus == DRAG_UNKOWN && !mIsZoom
                && me.getActionMasked() == MotionEvent.ACTION_MOVE
                && view instanceof IHorizontalScrollable) {
            float interval = me.getX() - mInitX;
            if (((IHorizontalScrollable) view).canScroll(view, (int) interval,
                    (int) me.getX(), (int) me.getY())) {
                mDragStatus = CAN_DRAG;
            } else {
                mDragStatus = CAN_NOT_DRAG;
            }
        }

        if (me.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            mIsZoom = true;
            return false;
        }

        if (mIsZoom || mDragStatus == CAN_DRAG) {
            return false;
        }

        return super.onInterceptTouchEvent(me);
    }

}

