package com.hufeng.filemanager.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PatternView extends View{

	int mInputPass = 0;
	int mWidth = 0;
	int mHeight = 0;
	Integer[] mLandMarkX = new Integer[9];
	Integer[] mLandMarkY = new Integer[9];
	int mMoveMotionX;
	int mMoveMotionY;
	Paint mPathPaint;
	Path mPath;
	
	public PatternView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPathPaint = new Paint();
		mPathPaint.setAntiAlias(true);
		mPathPaint.setColor(Color.BLUE);
		mPathPaint.setStrokeWidth((float)20.0);
		mPathPaint.setStyle(Style.STROKE);
		mPath = new Path();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getActionMasked();
		switch(action)
		{
		case MotionEvent.ACTION_UP:
			
			break;
		case MotionEvent.ACTION_MOVE:
			mMoveMotionX = (int)event.getX();
			mMoveMotionY = (int)event.getY();
			int mark = hitLandMark(mMoveMotionX,mMoveMotionY);
			if(mark!=-1)
			{
				int divider = 1;
				for(int j=0;j<9;j++)
				{
					divider = divider*10;
					int old_mark = mInputPass%divider;
					if(old_mark==0)
					{
						break;
					}
				}
				mInputPass+=mark*divider/10;
			}
			invalidate();
			break;
		case MotionEvent.ACTION_DOWN:
			
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		canvas.drawColor(Color.WHITE);
		int mWidth = getWidth();
		int mHeight = getHeight();
		int x = mWidth/6;
		int y = mHeight/6;
		mLandMarkX[0] = x*1;
		mLandMarkX[1] = x*3;
		mLandMarkX[2] = x*5;
		mLandMarkX[3] = x*1;
		mLandMarkX[4] = x*3;
		mLandMarkX[5] = x*5;
		mLandMarkX[6] = x*1;
		mLandMarkX[7] = x*3;
		mLandMarkX[8] = x*5;
		mLandMarkY[0] = y*1;
		mLandMarkY[3] = y*3;
		mLandMarkY[6] = y*5;
		mLandMarkY[1] = y*1;
		mLandMarkY[4] = y*3;
		mLandMarkY[7] = y*5;
		mLandMarkY[2] = y*1;
		mLandMarkY[5] = y*3;
		mLandMarkY[8] = y*5;
		//draw gesture path
		int divider = 1;
		for(int i=0;i<9;i++)
		{
			divider = divider*10;
			int mark1 = mInputPass%divider;
			int mark2 = mInputPass%(divider*10);
			if(mark1>0 && mark2>0)
			{
				//connect mark1 and mark2
				DrawLinePath(canvas,mLandMarkX[i],mLandMarkY[i],mLandMarkX[i+1],mLandMarkY[i+1]);
			}
			else if(mark1>0)
			{
				//connect mark1 and current move position
				DrawLinePath(canvas,mLandMarkX[i],mLandMarkY[i],mMoveMotionX, mMoveMotionY);
			}
			else
			{
				break;
			}
		}
		
		
		//draw 9 landmarks
		
		
		
	}

	
	private int hitLandMark(int posx, int posy)
	{
		for(int i=0;i<9;i++)
		{
			int landmarkx = mLandMarkX[i];
			int landmarky = mLandMarkY[i];
			if((posx-landmarkx)*(posx-landmarkx)+(posy-landmarky)*(posy-landmarky)<50)
			{
				//check whether already hit
				boolean flag = false;
				int divider = 1;
				for(int j=0;j<9;j++)
				{
					divider = divider*10;
					int mark = mInputPass%divider;
					if(mark==i)
					{
						flag=true;
						break;
					}
				}
				if(flag)//already hit this landmark
				{
					return -1;
				}
				else
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	private void DrawLinePath(Canvas canvas, int startx, int starty, int endx, int endy)
	{
		mPath.moveTo(startx, starty);
		mPath.lineTo(endx, endy);
		canvas.drawPath(mPath, mPathPaint);
	}

	
	
}
