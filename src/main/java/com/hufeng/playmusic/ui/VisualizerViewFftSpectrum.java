/*
 *              Copyright (C) 2011 The MusicMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hufeng.playmusic.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerViewFftSpectrum extends View {

	// Namespaces to read attributes
	private static final String VISUALIZER_NS = "http://schemas.android.com/apk/res/org.yammp";

	// Attribute names
	private static final String ATTR_ANTIALIAS = "antialias";
	private static final String ATTR_COLOR = "color";

	// Default values for defaults
	private static final boolean DEFAULT_ANTIALIAS = true;
	private static final int DEFAULT_COLOR = Color.WHITE;

	// Real defaults
	private final boolean mAntiAlias;
	private final int mColor;
	private int mFftSamples = 48;

	private byte[] mData = null;
	private float[] mPoints;
	private Rect mRect = new Rect();
	private Paint mForePaint = new Paint();

	public VisualizerViewFftSpectrum(Context context) {
		super(context);

		mAntiAlias = DEFAULT_ANTIALIAS;
		mColor = DEFAULT_COLOR;
		mForePaint.setStrokeWidth((float) getWidth() / (float) mFftSamples / 2.0f);
		setAntiAlias(mAntiAlias);
		setColor(mColor);
	}

	public VisualizerViewFftSpectrum(Context context, AttributeSet attrs) {
		super(context, attrs);

		mAntiAlias = attrs.getAttributeBooleanValue(VISUALIZER_NS, ATTR_ANTIALIAS,
				DEFAULT_ANTIALIAS);
		mColor = attrs.getAttributeIntValue(VISUALIZER_NS, ATTR_COLOR, DEFAULT_COLOR);
		mForePaint.setStrokeWidth((float) getWidth() / (float) mFftSamples / 2.0f);
		setAntiAlias(mAntiAlias);
		setColor(mColor);
	}

	public void setAntiAlias(boolean antialias) {
		mForePaint.setAntiAlias(antialias);
	}

	public void setColor(int color) {
		mForePaint.setColor(Color.argb(0xA0, Color.red(color), Color.green(color),
				Color.blue(color)));
	}

	public void setFftSamples(int samples) {
		mFftSamples = samples;
		mForePaint.setStrokeWidth((float) getWidth() / (float) samples / 2.0f);
		mPoints = null;
	}

	public void updateVisualizer(byte[] data) {
		byte[] model = new byte[data.length / 2 + 1];
		int j = 0;
		for (int i = 0; i <= mFftSamples * 2; i += 2) {
			model[j] = (byte) Math.hypot(data[i], data[i + 1]);
			j++;
		}
		mData = model;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mData == null) return;

		if (mPoints == null || mPoints.length < mData.length * 4) {
			mPoints = new float[mData.length * 4];
		}
		mRect.set(0, 0, getWidth(), getHeight() * 2);

		for (int i = 0; i <= mFftSamples; i++) {
			if (mData[i] < 0) {
				mData[i] = 127;
			}
			mPoints[i * 4] = mRect.width() * i / mFftSamples + getWidth() / mFftSamples / 2.0f;
			mPoints[i * 4 + 1] = mRect.height() / 2;
			mPoints[i * 4 + 2] = mRect.width() * i / mFftSamples + getWidth() / mFftSamples / 2.0f;
			mPoints[i * 4 + 3] = mRect.height() / 2 - 2 - mData[i] * 2;
		}
		canvas.drawLines(mPoints, mForePaint);

	}

	@Override
	protected void onSizeChanged(int width, int height, int old_width, int old_height) {
		mForePaint.setStrokeWidth((float) width / (float) mFftSamples / 2.0f);
	}
}
