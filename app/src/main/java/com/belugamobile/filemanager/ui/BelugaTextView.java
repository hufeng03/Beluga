package com.belugamobile.filemanager.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
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

    private Paint mTextPaint;
    private int mFontHeight;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mPageLineNum;
    private float mLineOffset;

    private Uri mUri;
    private String mTextCodec = "UTF-8";

    private int mTotalSkipBytes = 0;



    private static int mCurrentByteInPage = 5000;

    public BelugaTextView(Context context, Uri uri) {
        super(context);
        mUri = uri;
        initTextViewer(context);
    }

    public void setData(Uri uri) {
        mUri = uri;
    }

    private final void initTextViewer(Context context) {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(16);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setStrokeWidth(16);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mFontHeight = (int) Math.ceil(fm.bottom - fm.top) + 4;
        mPageLineNum = mScreenHeight / mFontHeight;
        mLineOffset = (mScreenHeight % mFontHeight) / (float)mPageLineNum;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        setMeasuredDimension(mScreenWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        mTextPaint.setAntiAlias(true);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mFontHeight = (int) Math.ceil(fm.bottom - fm.top) + 4;
        mPageLineNum = mScreenHeight / mFontHeight;
        mLineOffset = (mScreenHeight % mFontHeight) / (float)mPageLineNum;

        float y = -(fm.top);
        List<String> text = getStringFromFileForward(mTotalSkipBytes);

        System.out.println("Screen Width = " + mScreenWidth);
//		canvas.setViewport(mScreenWidth, mScreenWidth);
        System.out.println("Set Canvas View port Done!");
        System.out.println("Text Size = " + mTextPaint.getTextSize());
        if (text != null) {
                for (int j = 0, k = 0; j < text.size(); j++, k++) {
                    canvas.drawText(text.get(j), mScreenWidth*0, y + ((mFontHeight + mLineOffset) * k), mTextPaint);
                }
        }
    }




    private List<String> getStringFromFileForward(long bytePosition) {
        List<String> mContentLines = new ArrayList<String>();

        InputStream fInStream = null;
        try {
            fInStream =  FileManager.getAppContext().getContentResolver().openInputStream(mUri);
            BufferedReader br = new BufferedReader(new InputStreamReader(fInStream, mTextCodec));
            br.skip(bytePosition);

            char buff[] = new char[(2*mCurrentByteInPage)];
            int mFileEnd = br.read(buff, 0, (2 * mCurrentByteInPage));
            String string_temp = new String(buff);

            int width = 0;
            int i = 0;
            int iStart = 0;
            int lineCount = 0;
            for(i=0; i < (2 * mCurrentByteInPage); i++) {
                char ch = buff[i];
                float[] widths = new float[1];
                String srt = String.valueOf(ch);
                mTextPaint.getTextWidths(srt, widths);
                if (ch == '\n')
                {
                    lineCount++;
                    mContentLines.add(string_temp.substring(iStart, i));
                    iStart = i + 1;
                    width = 0;
                }
                else
                {
                    width += (int) (Math.ceil(widths[0]));
                    if (width > mScreenWidth)
                    {
                        lineCount++;
                        mContentLines.add(string_temp.substring(iStart, i));
                        iStart = i;
                        i--;
                        width = 0;
                    }
                    else
                    {
                        if (i == (2 * mCurrentByteInPage -1))
                        {
                            lineCount++;
                            mContentLines.add(string_temp.substring(iStart, (2 * mCurrentByteInPage)));
                        }
                    }
                }

                if((lineCount+1) > mPageLineNum) break;
            }
            mCurrentByteInPage = i+1;
//            mTotalTextHeight = (int)(lineCount * (mFontHeight + mLineOffset)+2);
            return mContentLines;
        } catch (Exception e) {
            System.out.println("Java Error!!!!!!!!!!");
            e.printStackTrace();
        } finally {
            if (fInStream != null) {
                try {
                    fInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private Vector<String> getStringFromFileBackward(long bytePosition) {
        Vector<String> mString = new Vector<String>();
        char buff[] = new char[(2 * mCurrentByteInPage)];
        char ch;
        int w = 0;
        int istart = 0;
        int real_line = 0;
        int i=0;
        InputStream fInStream = null;
        try {
            fInStream =  FileManager.getAppContext().getContentResolver().openInputStream(mUri);
            BufferedReader br = new BufferedReader(new InputStreamReader(fInStream, mTextCodec));
            bytePosition -= (3 * mCurrentByteInPage);
            if(bytePosition < 0) {
                bytePosition = 0;
            }
            br.skip(bytePosition);
            int mFileEnd = br.read(buff, 0, (2 * mCurrentByteInPage));
            String string_temp = new String(buff);

            if(bytePosition == 0) {
                for(i=0; i < (2 * mCurrentByteInPage); i++) {
                    ch = buff[i];
                    float[] widths = new float[1];
                    String srt = String.valueOf(ch);
                    mTextPaint.getTextWidths(srt, widths);
                    if (ch == '\n')
                    {
                        real_line++;
                        mString.addElement(string_temp.substring(istart, i));
                        istart = i + 1;
                        w = 0;
                    }
                    else
                    {
                        w += (int) (Math.ceil(widths[0]));
                        if (w > mScreenWidth) {
                            real_line++;
                            mString.addElement(string_temp.substring(istart, i));
                            istart = i;
                            i--;
                            w = 0;
                        } else {
                            if (i == (2 * mCurrentByteInPage -1))
                            {
                                real_line++;
                                mString.addElement(string_temp.substring(istart, (2 * mCurrentByteInPage)));
                            }
                        }
                    }

                    if((real_line+1) > mPageLineNum) break;
                }
            } else {
                for(i=(2 * mCurrentByteInPage); i > 0; i--) {
                    ch = buff[i];
                    float[] widths = new float[1];
                    String srt = String.valueOf(ch);
                    mTextPaint.getTextWidths(srt, widths);

                    if (ch == '\n')
                    {
                        real_line++;
                        w = 0;
                    }
                    else
                    {
                        w += (int) (Math.ceil(widths[0]));
                        if (w > mScreenWidth)
                        {
                            real_line++;
                            i++;
                            w = 0;
                        }
                        else
                        {
                        }
                    }
                    if((real_line+1) > mPageLineNum) break;
                }
                int j=0;
                for(; i < (2 * mCurrentByteInPage); i++, j++) {
                    ch = buff[i];
                    float[] widths = new float[1];
                    String srt = String.valueOf(ch);
                    mTextPaint.getTextWidths(srt, widths);
                    if (ch == '\n')
                    {
                        real_line++;
                        mString.addElement(string_temp.substring(istart, i));
                        istart = i + 1;
                        w = 0;
                    }
                    else
                    {
                        w += (int) (Math.ceil(widths[0]));
                        if (w > mScreenWidth)
                        {
                            real_line++;
                            mString.addElement(string_temp.substring(istart, i));
                            istart = i;
                            i--;
                            w = 0;
                        }
                        else
                        {
                            if (i == (2 * mCurrentByteInPage -1))
                            {
                                real_line++;
                                mString.addElement(string_temp.substring(istart, (2 * mCurrentByteInPage)));
                            }
                        }
                    }

                    if((real_line+1) > mPageLineNum) break;
                }
                i = j;
            }
            mCurrentByteInPage = i+1;
//            mTotalTextHeight=(int)(real_line * (mFontHeight + mLineOffset)+2);
            return mString;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fInStream != null) {
                try {
                    fInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
