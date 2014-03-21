package com.hufeng.playmusic.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchPaintView extends View {

	private static final int FADE_DELAY = 2;

	private static final int FADE_ALPHA = 0x10;
	private static final int MAX_FADE_STEPS = 256 / FADE_ALPHA * 2 + 8;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Rect mRect = new Rect();
	private Paint mPaint;
	private boolean mCurDown;
	private int mCurX;
	private int mCurY;
	private float mCurSize;
	private int mCurWidth;
	private int mFadeSteps = 0;
	private int mColor = Color.WHITE;
	private EventListener mListener;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			mHandler.removeCallbacksAndMessages(null);
			if (mFadeSteps < MAX_FADE_STEPS) {
				fade();
				mHandler.sendEmptyMessageDelayed(0, FADE_DELAY);
			} else {
				mFadeSteps = 0;
			}
		}
	};

	public TouchPaintView(Context c) {
		super(c);
		init();
	}

	public TouchPaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mListener != null) {
			mListener.onTouchEvent(event);
		}
		int action = event.getAction();
		mCurDown = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE;
		drawPoint(event.getX(), event.getY(), event.getPressure(), event.getSize());
		mFadeSteps = 0;
		mHandler.sendEmptyMessageDelayed(0, FADE_DELAY);
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (mListener != null) {
			mListener.onTrackballEvent(event);
		}
		boolean oldDown = mCurDown;
		mCurDown = true;
		int baseX = mCurX;
		int baseY = mCurY;
		final float scaleX = event.getXPrecision();
		final float scaleY = event.getYPrecision();
		drawPoint(baseX + event.getX() * scaleX, baseY + event.getY() * scaleY,
				event.getPressure(), event.getSize());
		mCurDown = oldDown;
		mFadeSteps = 0;
		mHandler.sendEmptyMessageDelayed(0, FADE_DELAY);
		return true;
	}

	public void setColor(int color) {
		mColor = color;
	}

	public void setEventListener(EventListener listener) {
		mListener = listener;
	}

	private void drawPoint(float x, float y, float pressure, float size) {
		mCurX = (int) x;
		mCurY = (int) y;
		mCurSize = size;
		mCurWidth = (int) (mCurSize * 128);
		if (mCurWidth < 1) {
			mCurWidth = 1;
		}
		if (mCurDown && mBitmap != null) {
			int pressureLevel = (int) (Math.sqrt(pressure) * 128);
			mPaint.setARGB(pressureLevel, Color.red(mColor), Color.green(mColor),
					Color.blue(mColor));
			mPaint.setMaskFilter(new BlurMaskFilter(mCurWidth / 2, BlurMaskFilter.Blur.NORMAL));
			mCanvas.drawCircle(mCurX, mCurY, mCurWidth, mPaint);
			mRect.set(mCurX - mCurWidth - 2, mCurY - mCurWidth - 2, mCurX + mCurWidth + 2, mCurY
					+ mCurWidth + 2);
			invalidate(mRect);
		}
		mFadeSteps = 0;
	}

	private void fade() {
		if (mCanvas != null && mFadeSteps < MAX_FADE_STEPS) {
			mCanvas.drawColor(Color.argb(FADE_ALPHA, 0xFF, 0xFF, 0xFF), Mode.DST_OUT);
			invalidate();
			mFadeSteps++;
		} else if (mFadeSteps >= MAX_FADE_STEPS) {
			mFadeSteps = 0;
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	private void init() {
		setFocusable(true);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.TRANSPARENT);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		int curW = mBitmap != null ? mBitmap.getWidth() : 0;
		int curH = mBitmap != null ? mBitmap.getHeight() : 0;
		if (curW >= w && curH >= h) return;

		if (curW < w) {
			curW = w;
		}
		if (curH < h) {
			curH = h;
		}

		Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.ARGB_8888);
		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (mBitmap != null) {
			newCanvas.drawBitmap(mBitmap, 0, 0, null);
		}
		mBitmap = newBitmap;
		mCanvas = newCanvas;
		mFadeSteps = MAX_FADE_STEPS;
	}

	public interface EventListener {

		boolean onTouchEvent(MotionEvent event);

		boolean onTrackballEvent(MotionEvent event);
	}
}