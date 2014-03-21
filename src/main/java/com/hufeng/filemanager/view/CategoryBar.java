package com.hufeng.filemanager.view;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.utils.LogUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class CategoryBar extends View{
	
	private static final String LOG_TAG = CategoryBar.class.getName();
	
	private CategoryBarEngine mEngine;
	
	private Vector<Long> mDatas = new Vector<Long>(9);
	
	private Vector<Integer> mColors = new Vector<Integer>(9);
	
	private static final String CATEGORY_COLOR_SPACE = "#f2f2f2";
	private static final String CATEGORY_COLOR_APK = "#9b7ada";
	private static final String CATEGORY_COLOR_PICTURE = "#6eba2a";
	private static final String CATEGORY_COLOR_MUSIC = "#db588d";
	private static final String CATEGORY_COLOR_VIDEO = "#4593ce";
	private static final String CATEGORY_COLOR_THEME = "#de8f20";
	private static final String CATEGORY_COLOR_DOCUMENT = "#dfcf44";
	private static final String CATEGORY_COLOR_ZIP = "#47dae3";
	private static final String CATEGORY_COLOR_OTHER = "#8692b9";
	
	private static final int CATEGORY_LOCATION_SPACE = 0;
	private static final int CATEGORY_LOCATION_APK = 4;
	private static final int CATEGORY_LOCATION_PICTURE = 3;
	private static final int CATEGORY_LOCATION_MUSIC = 1;
	private static final int CATEGORY_LOCATION_VIDEO = 2;
	private static final int CATEGORY_LOCATION_DOCUMENT = 5;
	private static final int CATEGORY_LOCATION_ZIP = 6;
	private static final int CATEGORY_LOCATION_OTHER = 7;
	

	public CategoryBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mEngine = new CategoryBarEngine();
		mEngine.init(context);
		
		mDatas.add(10000L);
		mDatas.add(1L);
		mDatas.add(1L);
		mDatas.add(1L);
		mDatas.add(1L);
		mDatas.add(1L);
		mDatas.add(1L);
		mDatas.add(1L);
		
		
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_MUSIC)));
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_VIDEO)));
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_PICTURE)));
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_APK)));
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_DOCUMENT)));
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_ZIP)));
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_OTHER)));
		mColors.add((Integer)(Color.parseColor(CATEGORY_COLOR_SPACE)));
	}
	
	public void refresh(Map<Integer, Long> map)
	{
		synchronized(mDatas)
		{
			Iterator<Entry<Integer, Long>> iterator = map.entrySet().iterator();
			for(int i=0;i<mDatas.size();i++)
				mDatas.setElementAt((long)0, i);
	        while (iterator.hasNext()) {
	            Entry<Integer, Long> entry = iterator.next();
	            int category = entry.getKey();
	            long size = entry.getValue();
	            switch(category)
	            {
	            case FileUtils.FILE_TYPE_APK:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_APK);
	            	break;
	            case FileUtils.FILE_TYPE_AUDIO:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_MUSIC);
	            	break;
	            case FileUtils.FILE_TYPE_IMAGE:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_PICTURE);
	            	break;
	            case FileUtils.FILE_TYPE_VIDEO:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_VIDEO);
	            	break;
	            case FileUtils.FILE_TYPE_DOCUMENT:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_DOCUMENT);
	            	break;
	            case FileUtils.FILE_TYPE_ZIP:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_ZIP);
	            	break;
	            case FileUtils.FILE_TYPE_FILE:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_OTHER);
	            	break;
	            default:
	            	mDatas.setElementAt(size, CATEGORY_LOCATION_SPACE);
	            	break;
	            }
	        }
		}
		invalidate();
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		synchronized(mDatas)
		{
			mEngine.drawCategoryBarOnCanvas(canvas, getWidth(), getHeight(), mDatas, mColors);
		}
	}

}

class CategoryBarEngine
{
	private static final String LOG_TAG = CategoryBarEngine.class.getName();
	
	Canvas mCanvas;
	int mWidth, mHeight;
	Vector<Long> mDatas;
	Paint mPaint;
	float mScale;
	
	public void init(Context context)
	{
		mPaint = new Paint();
		mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Style.FILL);
		mScale = context.getResources().getDisplayMetrics().density;
	}
	
	public void release()
	{
		
	}
	
	public void drawCategoryBarOnCanvas(Canvas canvas, int width, int height, Vector<Long> datas, Vector<Integer> colors)
	{
		mCanvas = canvas;

		long capacity = datas.elementAt(0);
		
		if(capacity==0)
			return;
		
		Vector<Integer> width_seperators = new Vector<Integer>();
		//data_accumulated.add(width);
		int w = 0;
		for(int i=1;i<datas.size();i++)
		{
			long data = datas.elementAt(i);
			w += (int)((long)width*data/capacity);
			width_seperators.add(w);
		}
		width_seperators.add(width);
		
		for(int i=width_seperators.size()-1;i>=0;i--)
		{
			Integer color = colors.elementAt(i);
			drawOneSegmentOfCategoryBar(canvas, width, height, color, width_seperators.elementAt(i));
		}
		
	}
	
	private void drawOneSegmentOfCategoryBar(Canvas canvas, float width, float height, Integer color, int stop)
	{	
//		if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"drawOneSegmentOfCategoryBar with width = "+width+" height = "+height+" color = "+color+" stop = "+stop);
		mPaint.setColor(color);
		mPaint.setStyle(Style.FILL);
		
		float stop_tmp;
		if(stop>height/2)
		{
			stop_tmp = height/2;
		}
		else
		{
			stop_tmp = stop;
		}
		//0 ~ stop_tmp, arc
		float angle = (float)(Math.acos((height-stop_tmp*2)/height)*180.0/Math.PI);
//		if(LogUtil.IDBG) LogUtil.i(LOG_TAG,"... draw arc angle = "+angle+" height = "+height);
		canvas.drawArc(new RectF(0,0,height,height), 180-angle, angle*2, false, mPaint);
		
		if(stop>height/2)
		{
			if(stop>width-height/2)
			{
				stop_tmp = width-height/2;
			}
			else
			{
				stop_tmp = stop;
			}
			//height/2 ~ stop_tmp, rect
			canvas.drawRect(new RectF(height/2-1,0,stop_tmp,height), mPaint);
		}
		
		if(stop>width-height/2)
		{
			//width-height/2 ~ stop, arc
			angle = (float)(Math.acos((height+(stop-width)*2.0)/height)*180.0/Math.PI);
			canvas.drawArc(new RectF(width-height,0,width,height), angle, 360-angle*2, false, mPaint);
		}

	}
}
