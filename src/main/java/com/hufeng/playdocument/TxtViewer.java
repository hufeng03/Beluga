package com.hufeng.playdocument;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import com.hufeng.filemanager.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TxtViewer extends TextView implements OnGestureListener {

	private Paint mTextPaint;
	private Vector<String> mText = null;
	private String mFileName = "";
	private String mTextCode = "";
	private int mFileEnd = 0;
	
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
	private int mFontHeight = 0;
	private int mPageLineNum = 0;
	private float mLineOffset = 0;
	public  static int mTotalTextHeight = 0;
	private long mTotalSkipBytes = 0;
	private int mCurrentByteInPage=5000;
	
	public TxtViewer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initTxtViewer(context);
	}
	
	public TxtViewer(Context context,  AttributeSet attrs) {
		super(context,attrs);
        // TODO Auto-generated constructor stub
		initTxtViewer(context);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        CharSequence s = typeArray.getString(R.styleable.MyView_Text);
        if(s != null) {
        	setText(s.toString());
        }
        
        setTextColor(typeArray.getColor(R.styleable.MyView_Color, Color.WHITE));
        int textSize = typeArray.getDimensionPixelOffset(R.styleable.MyView_TextSize, 0);
        if(textSize > 0) {
        	setTextSize(textSize);
        }
        typeArray.recycle();
        
//		int line=0;
//		int w=0;
		
//		for (int i = 0; i < mText.length(); i++)
//		{
//			char ch = mText.charAt(i);
//			float[] widths = new float[1];
//			String srt = String.valueOf(ch);
//			mTextPaint.getTextWidths(srt, widths);
//
//			if (ch == '\n')
//			{
//				line++;
//				w = 0;
//			}
//			else
//			{
//				w += (int) (Math.ceil(widths[0]));
//				if (w > mScreenWidth)
//				{
//					line++;
//					i--;
//					w = 0;
//				}
//				else
//				{
//					if (i == (mText.length() - 1))
//					{
//						line++;
//					}
//				}
//			}
//			
//			if((line+1) > mPageLineNum) break;
//		}
//		mTotalTextHeight = (int)(line * (mFontHeight + mLineOffset)+2);
	}
	
	private final void initTxtViewer(Context context) {
		System.out.println("Text Code = " + mTextCode);
    	mTextPaint = new Paint();
    	mTextPaint.setAntiAlias(true);
    	mTextPaint.setTextSize(16);
    	mTextPaint.setColor(Color.WHITE);
    	mTextPaint.setStrokeWidth(16);
    	WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    	mScreenWidth = wm.getDefaultDisplay().getWidth();
    	mScreenHeight = wm.getDefaultDisplay().getHeight();
		FontMetrics fm = mTextPaint.getFontMetrics();
		mFontHeight = (int) Math.ceil(fm.bottom - fm.top) + 4;
		mPageLineNum = mScreenHeight / mFontHeight;
		mLineOffset = (mScreenHeight % mFontHeight) / (float)mPageLineNum;
    }
	
//	private Vector<String> readText() {
//    	Vector<String> mString = new Vector<String>();
//    	char ch;
//		int w = 0;
//		int istart = 0;
//		int real_line = 0;
//		
//		for (int i = 0; i < mText.length(); i++)
//		{
//			ch = mText.charAt(i);
//			float[] widths = new float[1];
//			String srt = String.valueOf(ch);
//			mTextPaint.getTextWidths(srt, widths);
//
//			if (ch == '\n')
//			{
//				real_line++;
//				mString.addElement(mText.substring(istart, i));
//				istart = i + 1;
//				w = 0;
//			}
//			else
//			{
//				w += (int) (Math.ceil(widths[0]));
//				if (w > mScreenWidth)
//				{
//					real_line++;
//					mString.addElement(mText.substring(istart, i));
//					istart = i;
//					i--;
//					w = 0;
//				}
//				else
//				{
//					if (i == (mText.length() - 1))
//					{
//						real_line++;
//						mString.addElement(mText.substring(istart, mText.length()));
//					}
//				}
//			}
//			
//			mTotalSkipBytes = i;
//			if((real_line+1) > mPageLineNum) break;
//		}
//    	mTotalTextHeight=(int)(real_line * (mFontHeight + mLineOffset)+2);
//    	return mString;
//    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		   
		mTextPaint.setAntiAlias(true);
		FontMetrics fm = mTextPaint.getFontMetrics();
		mFontHeight = (int) Math.ceil(fm.bottom - fm.top) + 4;
		mPageLineNum = mScreenHeight / mFontHeight;
		mLineOffset = (mScreenHeight % mFontHeight) / (float)mPageLineNum;

		int x=0;
		float y = -(fm.top);
//		System.out.println("File Name1 = " + mFileName);
		mText = getStringFromFileForward(mTotalSkipBytes);
//		System.out.println("File Name2 = " + mFileName);
	
//		Vector<String>	m_String = readText();
	
		System.out.println("Screen Width = " + mScreenWidth);
//		canvas.setViewport(mScreenWidth, mScreenWidth);
		System.out.println("Set Canvas View port Done!");
		System.out.println("Text Size = " + mTextPaint.getTextSize());
		for (int i = 0, j = 0; i < mText.size(); i++, j++)
		{
			canvas.drawText((String)(mText.elementAt(i)), x, y + ((mFontHeight + mLineOffset) * j), mTextPaint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = measureHeight(heightMeasureSpec);  
    	int measuredWidth = measureWidth(widthMeasureSpec);  
    	  
    	this.setMeasuredDimension(measuredWidth, measuredHeight);
    	this.setLayoutParams(new LinearLayout.LayoutParams(measuredWidth,measuredHeight));
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private int measureHeight(int measureSpec) {  
		int specMode = MeasureSpec.getMode(measureSpec);  
    	int specSize = MeasureSpec.getSize(measureSpec);  
    	   
    	// Default size if no limits are specified.  
    	int result = mTotalTextHeight;  
    	  
    	if (specMode == MeasureSpec.AT_MOST)   
    	{  
    		// Calculate the ideal size of your  
    		// control within this maximum size.  
    		// If your control fills the available  
    		// space return the outer bound.  
    		result = specSize;  
    	}   
    	else if (specMode == MeasureSpec.EXACTLY)   
    	{  
    		// If your control can fit within these bounds return that value.  
    		result = specSize;  
    	}  
    	return result;  
    	  
	}
	
	private int measureWidth(int measureSpec) {  
		int specMode = MeasureSpec.getMode(measureSpec);  
    	int specSize = MeasureSpec.getSize(measureSpec);  
    	  
    	// Default size if no limits are specified.  
    	int result = 500;  
    	  
    	if (specMode == MeasureSpec.AT_MOST)  
    	{  
    		// Calculate the ideal size of your control  
    		// within this maximum size.   
    		// If your control fills the available space  
    		// return the outer bound.  
    		result = specSize;  
    	}   
    	else if (specMode == MeasureSpec.EXACTLY)   
    	{  
    		// If your control can fit within these bounds return that value.  
    		result = specSize;  
    	}  
    	return result;  
	}

	private void setText(Vector<String> text) {
		mText = text;
		requestLayout();
		invalidate();
	}
	
	public void setFileName (String fileName) {
		mFileName = fileName;
		requestLayout();
		invalidate();
	}
	
 	public void setTextSize(int size) {
		mTextPaint.setTextSize(size);
		requestLayout();
		invalidate();
	}
	
	public void setTextColor(int color) {
		mTextPaint.setColor(color);
		invalidate();
	}
	
	public void setTextCode(String textCode) {
		mTextCode = textCode;
		requestLayout();
		invalidate();
	}
	
	private Vector<String> getStringFromFileForward(long pagenumber) {
		Vector<String> mString = new Vector<String>();
		char buff[] = new char[(2*mCurrentByteInPage)];
		char ch;
		int w = 0;
		int istart = 0;
		int real_line = 0;
		int i=0;
		
		try {
			FileInputStream fInputStream = new FileInputStream(mFileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, mTextCode);
			BufferedReader in = new BufferedReader(inputStreamReader);
			in.skip(pagenumber);
			mFileEnd = in.read(buff, 0, (2 * mCurrentByteInPage));
			String string_temp = new String(buff);
			
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
			in.close();
			mCurrentByteInPage = i+1;
			mTotalTextHeight=(int)(real_line * (mFontHeight + mLineOffset)+2);
	    	return mString;
		} catch (Exception e) {
			System.out.println("Java Error!!!!!!!!!!");
			e.printStackTrace();
		}
		
		return null;
	}
	
	private Vector<String> getStringFromFileBackward(long pagenumber) {
		Vector<String> mString = new Vector<String>();
		char buff[] = new char[(2 * mCurrentByteInPage)];
		char ch;
		int w = 0;
		int istart = 0;
		int real_line = 0;
		int i=0;
		try {
			FileInputStream fInputStream = new FileInputStream(mFileName);
			InputStreamReader inputStreamReader = new InputStreamReader(fInputStream, mTextCode);
			BufferedReader in = new BufferedReader(inputStreamReader);
			pagenumber -= (3 * mCurrentByteInPage);
			if(pagenumber < 0) {
				pagenumber = 0;
			}
			in.skip(pagenumber);
			mFileEnd = in.read(buff, 0, (2 * mCurrentByteInPage));
			String string_temp = new String(buff);
			
			if(pagenumber == 0) {
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
			in.close();
			mCurrentByteInPage = i+1;
			mTotalTextHeight=(int)(real_line * (mFontHeight + mLineOffset)+2);
			return mString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
//		if(arg0.getX()<(mScreenWidth/2)) {
//			setText(getStringFromFileBackward(mTotalSkipBytes));
//			mTotalSkipBytes = mTotalSkipBytes - mCurrentByteInPage;
//			if(mTotalSkipBytes<0)
//				mTotalSkipBytes=0;
//		}
//		else {
//			if(mFileEnd != -1) {
//				mTotalSkipBytes = mTotalSkipBytes + mCurrentByteInPage;
//				setText(getStringFromFileForward(mTotalSkipBytes));
//			}
//		}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		if(arg1.getX()-arg0.getX()>-30)
		{
			setText(getStringFromFileBackward(mTotalSkipBytes));
			mTotalSkipBytes = mTotalSkipBytes - mCurrentByteInPage;
			if(mTotalSkipBytes<0)
				mTotalSkipBytes=0;
		}
		else if(arg1.getX()-arg0.getX()<30)
		{
			if(mFileEnd != -1) {
				mTotalSkipBytes = mTotalSkipBytes + mCurrentByteInPage;
				setText(getStringFromFileForward(mTotalSkipBytes));
			}
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
