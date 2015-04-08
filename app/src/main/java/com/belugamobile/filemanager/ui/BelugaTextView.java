package com.belugamobile.filemanager.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Feng Hu on 15-04-02.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaTextView extends View {

    private Paint mPaint;
    private int mViewWidth;
    private int mViewHeight;
    private int mFontHeight;
    private int mPageLineNum;
    private float mLineOffset;
    private int mFontSize;
    private int mPadding;

    private List<String> mContent;


    public BelugaTextView(Context context, int padding, int fontSize) {
        super(context);
        mPadding = padding;
        mFontSize = fontSize;
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(fontSize);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(fontSize);

    }

    public void setLines(List<String> lines) {
        if (lines != mContent) {
            mContent = lines;
            invalidate();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        Paint.FontMetrics fm = mPaint.getFontMetrics();
        mFontHeight = (int) Math.ceil(fm.bottom - fm.top) + 4;
        mPageLineNum = (mViewHeight - mPadding * 2)/ mFontHeight;
        mLineOffset = ((mViewHeight - mPadding * 2) % mFontHeight) / (float)mPageLineNum;

        float y = -(fm.top);

        if (mContent != null) {
            for (int j = 0, k = 0; j < mContent.size(); j++, k++) {
                canvas.drawText(mContent.get(j), mPadding, mPadding + y + ((mFontHeight + mLineOffset) * k), mPaint);
            }
        }
    }
}
