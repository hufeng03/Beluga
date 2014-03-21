package com.hufeng.playimage;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Scroller;

import com.hufeng.filemanager.browser.ImageEntry;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by feng on 14-1-19.
 */
public class ZoomableImageView extends ViewGroup implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener, IHorizontalScrollable  {

    private static final String TAG = "ScalableImageView";

    static final int ZOOM_MAX = 3;
    private OnClickListener mOnClickListener;

    private static final class ImageParams {
        float initTop;
        float initLeft;
        float initWidth;
        float initHeight;
        float top;
        float left;
        float scale;
        float ratio;
        boolean isInitialized;

        public ImageParams() {
            scale = 1.0f;
        }

        public float getRight() {
            return left + initWidth * scale;
        }

        public float getWidth() {
            return initWidth * scale;
        }

        public float getBottom() {
            return top + initHeight * scale;
        }

        public float getHeight() {
            return initHeight * scale;
        }
    }

    private MyLazyLoadImageView mChild;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private Scroller mScroller;
    private ImageParams mImageParams;

    public ZoomableImageView(Context context) {
        super(context);
        initImageViewChild();
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setOnDoubleTapListener(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mScroller = new Scroller(context, new DecelerateInterpolator());
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initImageViewChild();
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setOnDoubleTapListener(this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mScroller = new Scroller(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retScale = mScaleGestureDetector.onTouchEvent(event);
        boolean retGesture = mGestureDetector.onTouchEvent(event);
        return retScale || retGesture || super.onTouchEvent(event);
    }

    private void initImageViewChild() {
        mChild = new MyLazyLoadImageView(getContext());
        mChild.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView(mChild);
        mImageParams = new ImageParams();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View view = mChild;
        final ImageParams config = mImageParams;
        view.layout((int) config.left, (int) config.top,
                (int) (config.left + mChild.getMeasuredWidth()),
                (int) (config.top + mChild.getMeasuredHeight()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (mImageParams.ratio > 1.0 * parentHeight / parentWidth) {
            final int width = (int) (parentHeight * mImageParams.scale / mImageParams.ratio);
            final int height = (int) (parentHeight * mImageParams.scale);
            mChild.measure(width | MeasureSpec.EXACTLY, height
                    | MeasureSpec.EXACTLY);
            if (!mImageParams.isInitialized) {
                mImageParams.initLeft = (parentWidth - width) / 2;
                mImageParams.left = mImageParams.initLeft;
                mImageParams.isInitialized = true;
                mImageParams.initWidth = width;
                mImageParams.initHeight = height;
            }
        } else {
            final int width = (int) (parentWidth * mImageParams.scale);
            final int height = (int) (parentWidth * mImageParams.scale * mImageParams.ratio);
            mChild.measure(width | MeasureSpec.EXACTLY, height
                    | MeasureSpec.EXACTLY);
            if (!mImageParams.isInitialized) {
                mImageParams.top = (parentHeight - height) / 2;
                mImageParams.initTop = mImageParams.top;
                mImageParams.isInitialized = true;
                mImageParams.initWidth = width;
                mImageParams.initHeight = height;
            }
        }
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int dx;
        int dy;
        if (mImageParams.left >= 0
                || mImageParams.getRight() <= this.getMeasuredWidth()) {
            dx = 0;
        } else {
            dx = (int) (velocityX / 5);
        }

        if (mImageParams.top >= 0
                || mImageParams.getBottom() <= this.getMeasuredHeight()) {
            dy = 0;
        } else {
            dy = (int) (velocityY / 5);
        }

        if (dx != 0 || dy != 0) {
            mScroller.startScroll((int) mImageParams.left,
                    (int) mImageParams.top, dx, dy, 350);
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            // Log.d(TAG, "compute scroll:" + mScroller.getCurrX() + " "
            // + mScroller.getCurrY());
            if (mScroller.getCurrX() > 0) {
                mImageParams.left = 0;
            } else if (mScroller.getCurrX() + mImageParams.getWidth() < this
                    .getMeasuredWidth()) {
                mImageParams.left = this.getMeasuredWidth()
                        - mImageParams.getWidth();
            } else {
                mImageParams.left = mScroller.getCurrX();
            }

            if (mScroller.getCurrY() > 0) {
                mImageParams.top = 0;
            } else if (mScroller.getCurrY() + mImageParams.getHeight() < this
                    .getMeasuredHeight()) {
                mImageParams.top = this.getMeasuredHeight()
                        - mImageParams.getHeight();
            } else {
                mImageParams.top = mScroller.getCurrY();
            }

            this.requestLayout();
            invalidate();
        }
        super.computeScroll();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Log.d(TAG, "onScroll:" + distanceX + " " + distanceY);
        if (mImageParams.scale == 1.0f) {
            return false;
        }
        float targetLeft;
        if ((distanceX > 0 && mImageParams.left > 0)
                || (distanceX < 0 && mImageParams.getRight() < this
                .getMeasuredWidth())) {
            targetLeft = mImageParams.left;
        } else {
            targetLeft = mImageParams.left - distanceX;
            if (targetLeft > 0 && distanceX < 0) {
                targetLeft = 0;
            } else if (targetLeft + mImageParams.initWidth * mImageParams.scale < this
                    .getMeasuredWidth() && distanceX > 0) {
                targetLeft = this.getMeasuredWidth() - mImageParams.initWidth
                        * mImageParams.scale;
            }
        }

        float targetTop;
        if ((distanceY > 0 && mImageParams.top > 0)
                || (distanceY < 0 && mImageParams.getBottom() < this
                .getMeasuredHeight())) {
            targetTop = mImageParams.top;
        } else {
            targetTop = mImageParams.top - distanceY;
            if (targetTop > 0 && distanceY < 0) {
                targetTop = 0;
            } else if (targetTop + mImageParams.initHeight * mImageParams.scale < this
                    .getMeasuredHeight() && distanceY > 0) {
                targetTop = this.getMeasuredHeight() - mImageParams.initHeight
                        * mImageParams.scale;
            }
        }

        Log.d(TAG, mImageParams.left + " " + targetLeft + " "
                + mImageParams.top + "  " + targetTop);
        mImageParams.left = targetLeft;
        mImageParams.top = targetTop;

        requestLayout();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mImageParams.scale > 1.0f) {
            zoomIn();
        } else {
            zoomOut(e.getX(), e.getY());
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public void restore() {
        mImageParams.left = mImageParams.initLeft;
        mImageParams.top = mImageParams.initTop;
        mImageParams.scale = 1.0f;
        requestLayout();
    }

    public void setImageData(ImageEntry media) {
//        mChild.setBitmapUrl(media.mContentResourceUrl,
//                media.mContentThumbnailUrl);
//        mChild.setImagePath(media.path);
        mImageParams.ratio = 1.0f * media.getHeight() / media.getWidth();

        mChild.requestDisplayLocalThumbnail(media.path);
        mChild.requestDisplayLocalImage(media.path);
    }

    private void zoomIn() {
        final int duration = 250;
        ValueAnimator leftAnimator = ValueAnimator.ofFloat(mImageParams.left,
                mImageParams.initLeft).setDuration(duration);
        ValueAnimator topAnimator = ValueAnimator.ofFloat(mImageParams.top,
                mImageParams.initTop).setDuration(duration);
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(mImageParams.scale,
                1.0f).setDuration(duration);
        leftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mImageParams.left = (Float) animator.getAnimatedValue();
            }
        });

        topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mImageParams.top = (Float) animator.getAnimatedValue();
            }

        });

        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mImageParams.scale = (Float) animator.getAnimatedValue();
                requestLayout();
            }

        });
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new DecelerateInterpolator());
        set.playTogether(leftAnimator, topAnimator, scaleAnimator);
        set.start();
    }

    private void zoomOut(float x, float y) {
        float scale = ZOOM_MAX;
        if (mImageParams.initTop > 0) {

            // 当占比不到1/2的时候，双击仅仅是充满
            if (mImageParams.initHeight < 1.0f * this.getHeight() / 2) {
                scale = 1.0f * this.getHeight() / mImageParams.initHeight;
                y = 1.0f * this.getHeight() / 2;
            } else {
                // 防止边缘出现在中间的情况
                float targetTop = mImageParams.initTop
                        - (y - mImageParams.initTop) * (scale - 1.0f);
                float targetBottom = targetTop + mImageParams.initHeight
                        * scale;
                if (targetTop > 0) {
                    y = mImageParams.initTop / (scale - 1)
                            + mImageParams.initTop;
                } else if (targetBottom < this.getHeight()) {
                    targetTop = getHeight() - mImageParams.initHeight * scale;
                    y = (mImageParams.initTop - targetTop) / (scale - 1)
                            + mImageParams.initTop;
                }
            }
        } else if (mImageParams.initLeft > 0) {
            if (mImageParams.initWidth < 1.0 * this.getWidth() * 2 / 3) {
                scale = 1.0f * this.getWidth() / mImageParams.initWidth;
                x = 1.0f * this.getWidth() / 2;
            } else {
                float targetLeft = mImageParams.initLeft
                        - (x - mImageParams.initLeft) * (scale - 1.0f);
                float targetRight = targetLeft + mImageParams.initWidth * scale;
                if (targetLeft > 0) {
                    x = mImageParams.initLeft / (scale - 1)
                            + mImageParams.initLeft;
                } else if (targetRight < this.getWidth()) {
                    targetLeft = getWidth() - mImageParams.initWidth * scale;
                    x = (mImageParams.initLeft - targetLeft) / (scale - 1)
                            + mImageParams.initLeft;
                }
            }
        }

        final ValueAnimator animator = ValueAnimator.ofFloat(1.0f, scale);
        final float downX = x;
        final float downY = y;
        Log.d(TAG, "downX:" + x + " downY:" + y);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (Float) animation.getAnimatedValue();
                mImageParams.scale = scale;
                mImageParams.left = mImageParams.initLeft - (scale - 1.0f)
                        * (downX - mImageParams.initLeft);
                mImageParams.top = mImageParams.initTop - (scale - 1.0f)
                        * (downY - mImageParams.initTop);
                requestLayout();
            }
        });
        animator.setDuration(400);
        animator.start();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (this.mImageParams.scale != 1.0f) {
            return true;
        }

        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
        }
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = detector.getScaleFactor();
        float x = detector.getFocusX();
        float y = detector.getFocusY();

        float percentX = (x - mImageParams.left)
                / (mImageParams.initWidth * mImageParams.scale);
        float percentY = (y - mImageParams.top)
                / (mImageParams.initHeight * mImageParams.scale);
        float newScale = mImageParams.scale * scale;
        float newWidth = mImageParams.initWidth * newScale;
        float newHeight = mImageParams.initHeight * newScale;
        mImageParams.left = x - newWidth * percentX;
        mImageParams.top = y - newHeight * percentY;
        mImageParams.scale = newScale;
        requestLayout();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (mImageParams.scale < 1.0f) {
            zoomIn();
        } else {
//            bounce();
        }
    }

    private void bounce() {
        Log.d(TAG, "bounce");
        float targetLeft = mImageParams.left;
        if (mImageParams.getWidth() < this.getWidth()) {
            targetLeft = (getWidth() - mImageParams.getWidth()) / 2.0f;
        } else if (mImageParams.left > 0) {
            targetLeft = 0;
        } else if (mImageParams.getRight() < this.getWidth()) {
            targetLeft = this.getWidth() - mImageParams.getWidth();
        }

        float targetTop = mImageParams.top;
        if (mImageParams.getHeight() < this.getHeight()) {
            targetTop = (getHeight() - mImageParams.getHeight()) / 2.0f;
        } else if (mImageParams.left > 0) {
            targetTop = 0;
        } else if (mImageParams.getBottom() < this.getHeight()) {
            targetTop = this.getHeight() - mImageParams.getHeight();
        }

        if (targetLeft == mImageParams.left && targetTop == mImageParams.top) {
            return;
        }

        AnimatorSet set = new AnimatorSet();
        final ValueAnimator xAnimator = ValueAnimator.ofFloat(
                mImageParams.left, targetLeft).setDuration(200);
        final ValueAnimator yAnimator = ValueAnimator.ofFloat(mImageParams.top,
                targetTop).setDuration(200);
        xAnimator.setInterpolator(new DecelerateInterpolator());
        yAnimator.setInterpolator(new DecelerateInterpolator());
        xAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mImageParams.left = (Float) animation.getAnimatedValue();
                requestLayout();
            }
        });
        yAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mImageParams.top = (Float) animation.getAnimatedValue();
            }
        });
        set.playTogether(xAnimator, yAnimator);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationCancel(Animator arg0) {
                requestLayout();
            }

            @Override
            public void onAnimationEnd(Animator arg0) {

            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationStart(Animator arg0) {
            }
        });
    }

    @Override
    public boolean canScroll(View v, int dx, int x, int y) {
        Log.e(TAG, this.getId() + "canScroll: " + dx);
        Log.d(TAG,
                "left:" + mImageParams.left + "  right:"
                        + mImageParams.getRight() + "  width:"
                        + this.getMeasuredWidth());
        if (mImageParams.left < 0 && dx > 0) {
            Log.d(TAG, "canScroll -- right -- true");
            return true;
        } else if (mImageParams.getRight() > this.getMeasuredWidth() && dx < 0) {
            Log.d(TAG, "canScroll -- left -- true");
            return true;
        }
        Log.d(TAG, "canScroll -- false");
        return false;
    }

//    public void setOnImageLongClickListener(OnLongClickListener listener) {
//        mOnLongClickListener = listener;
//    }
//
    public void setOnImageClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }



}
