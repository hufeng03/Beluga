package com.hufeng.playimage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class AnimatedImageView extends ViewGroup {

	private MyLazyLoadImageView mImageView;
	private int mInitCenterX;
	private int mInitCenterY;
	private int mInitWidth;
	private int mInitHeight;

	private int mWidth;
	private int mHeight;
	private int mCenterX = mInitCenterX;
	private int mCenterY = mInitCenterY;

	static final int DURATION = 250;

	public AnimatedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		initImage();
	}

	public void setImage(int x, int y, int width, int height) {
		mInitWidth = width;
		mInitHeight = height;
		mInitCenterX = x + width / 2;
		mInitCenterY = y + height / 2;

		mCenterX = mInitCenterX;
		mCenterY = mInitCenterY;
		mWidth = width;
		mHeight = height;
	}

	public MyLazyLoadImageView getImage() {
		return mImageView;
	}

	void initImage() {
		mImageView = new MyLazyLoadImageView(getContext());
		mImageView.setScaleType(ScaleType.FIT_CENTER);
		addView(mImageView);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int left = mCenterX - mImageView.getMeasuredWidth() / 2;
		int top = mCenterY - mImageView.getMeasuredHeight() / 2;
		mImageView.layout(left, top, left + mImageView.getMeasuredWidth(), top
				+ mImageView.getMeasuredHeight());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMeasuredWidth = MeasureSpec.getSize(widthMeasureSpec);
		mMeasureHeight = MeasureSpec.getSize(heightMeasureSpec);
		mImageView.measure(MeasureSpec.EXACTLY | mWidth, MeasureSpec.EXACTLY
				| mHeight);
		mSystemCenterX = mMeasuredWidth / 2;
		mSystemCenterY = mMeasureHeight / 2;
	}

	private int mMeasuredWidth;
	private int mMeasureHeight;

	private int mDestWidth;
	private int mDestHeight;
	private int mSystemCenterX;
	private int mSystemCenterY;


	public void setBitmapRate(float rate) {
		mRate = rate;
		mDestHeight = (int) ((mMeasuredWidth) / rate);
		// if (isAnimatedEnd()) {
		// this.mHeight = mDestHeight;
		// requestLayout();
		// }
	}

	private void updateDestWidthAndHeight(float destRate) {
		if ((1.0f) * mMeasuredWidth / mMeasureHeight < destRate) {
			mDestWidth = mMeasuredWidth;
			mDestHeight = (int) (mMeasuredWidth / destRate);
		} else {
			mDestHeight = mMeasureHeight;
			mDestWidth = (int) (mDestHeight * destRate);
		}
	}

	private float mRate = 1.0f;

	public void startAnimate(final boolean isRevert) {
		updateDestWidthAndHeight(mRate);
		ValueAnimator rate = null;
		if (isRevert) {
			rate = ValueAnimator.ofFloat(1, 0).setDuration(DURATION);
			rate.addListener(new AnimatorListener() {

				@Override
				public void onAnimationCancel(Animator animator) {

				}

				@Override
				public void onAnimationEnd(Animator animator) {
					mImageView.requestDisplayImage(null);
				}

				@Override
				public void onAnimationRepeat(Animator animator) {

				}

				@Override
				public void onAnimationStart(Animator animator) {

				}

			});
		} else {
			rate = ValueAnimator.ofFloat(0, 1).setDuration(DURATION);
		}

		rate.addUpdateListener(mAnimatorUpdateListener);
		rate.start();
	}

	AnimatorUpdateListener mAnimatorUpdateListener = new AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animator) {
			float rate = (Float) animator.getAnimatedValue();
			mWidth = (int) ((mDestWidth - mInitWidth) * rate) + mInitWidth;
			mHeight = (int) ((mDestHeight - mInitHeight) * rate) + mInitHeight;

			mCenterX = mInitCenterX
					+ (int) ((mSystemCenterX - mInitCenterX) * rate);
			mCenterY = mInitCenterY
					+ (int) ((mSystemCenterY - mInitCenterY) * rate);
			requestLayout();
		}
	};
}
