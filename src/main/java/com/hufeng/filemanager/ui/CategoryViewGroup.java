package com.hufeng.filemanager.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CategoryViewGroup extends ViewGroup{

	public CategoryViewGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CategoryViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CategoryViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);  
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);  
//	    int diff = Math.abs(heightSize-widthSize);
//	    if(diff>150){
//	    	diff = 150;
//	    }else if(diff<10){
//	    	diff = 0;
//	    }
	  
	    View nine_squared = getChildAt(0);
		View information = getChildAt(1);
		
		int orientation =
				getResources().getConfiguration().orientation;
		if(Configuration.ORIENTATION_LANDSCAPE == orientation){
			int diff = widthSize/4;
		    nine_squared.measure(
		    		MeasureSpec.makeMeasureSpec(/*heightSize*/widthSize-diff, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
		    
		    information.measure(
		    		MeasureSpec.makeMeasureSpec(/*widthSize-heightSize*/diff, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));  
		}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
			int diff = heightSize/4;
		    nine_squared.measure(
		    		MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(/*widthSize*/heightSize-diff, MeasureSpec.EXACTLY));
		    
		    information.measure(
		    		MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(/*heightSize-widthSize*/diff, MeasureSpec.EXACTLY));  
		}else{
			int diff = 0;
		    nine_squared.measure(
		    		MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
		    
		    information.measure(
		    		MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY),
		    		MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY));  
		}
	      
	    setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		View nine_squared = getChildAt(0);
		View information = getChildAt(1);
		
//		int diff = Math.abs(l-r-b+t);
//	    if(diff>150){
//	    	diff = 150;
//	    }else if(diff<10){
//	    	diff = 0;
//	    }
	    
		int orientation =
				getResources().getConfiguration().orientation;
		if(Configuration.ORIENTATION_LANDSCAPE == orientation){
//			nine_squared.layout(l, t, l+(b-t), b);
//			information.layout(l+(b-t)+1, t, r, b);
			int diff = (r-l+1)/4;
			nine_squared.layout(l, t, r-diff, b);
			information.layout(r-diff+1, t, r, b);
		}else if(Configuration.ORIENTATION_PORTRAIT == orientation){
//			nine_squared.layout(l, t, r, t+(r-l));
//			information.layout(l, t+(r-l)+1, r, b);
			int diff = (b-t+1)/4;
			nine_squared.layout(l, t, r, b-diff);
			information.layout(l, b-diff+1, r, b);
		}else{
			int diff = 0;
			nine_squared.layout(l, t, r, b);
			information.layout(0, 0, 0, 0);			
		}
	}
}
