package com.hufeng.filemanager.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class DirectoryViewGroup extends ViewGroup{

	public DirectoryViewGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DirectoryViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DirectoryViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
	  
	    View tree = getChildAt(0);
		View list = getChildAt(1);
		
		int orientation =
				getResources().getConfiguration().orientation;
		if(Configuration.ORIENTATION_LANDSCAPE == orientation){
			int newWidthSize = heightSize;
			if (heightSize  > widthSize/3) {
				newWidthSize = widthSize/3;
			}
//			int newWidthSize = 50;
		    tree.measure(
		    		MeasureSpec.makeMeasureSpec(newWidthSize, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
		    
		    list.measure(
					MeasureSpec.makeMeasureSpec(widthSize - newWidthSize, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
		}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
		    list.measure(
		    		MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
		    
		    tree.measure(
		    		MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
            tree.setVisibility(View.GONE);
		}else{
		    list.measure(
		    		MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
		    
		    tree.measure(
		    		MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));
            tree.setVisibility(View.GONE);
		}
	      
	    setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		View tree = getChildAt(0);
		View list = getChildAt(1);
		
		int orientation =
				getResources().getConfiguration().orientation;
		if(Configuration.ORIENTATION_LANDSCAPE == orientation){
			tree.layout(l, t, l+tree.getMeasuredWidth(), b);
			list.layout(l+tree.getMeasuredWidth(), t, r, b);
		}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
			list.layout(l, t, r, b);
			tree.layout(0, 0, 0, 0);
		}else{
			list.layout(l, t, r, b);
			tree.layout(0, 0, 0, 0);			
		}
	}
}
