package com.belugamobile.filemanager.utils;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;


public class IconUtil {

    public static Bitmap makeGroupAvatarWithChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
    	ArrayList<Drawable> copyDrawables = new ArrayList<Drawable>();
    	for(Drawable dw:childDrawables)
    	{
    		if(dw!=null)
    			copyDrawables.add(dw);
    	}
    	switch(copyDrawables.size())
    	{
    	case 0:
    		return null;
    	case 1:
    		return makeGroupAvatarWithOneChild(context, copyDrawables, mPhotoWidth);
    	case 2:
    		return makeGroupAvatarWithTwoChildren(context, copyDrawables, mPhotoWidth);
    	case 3:
    		return makeGroupAvatarWithThreeChildren(context, copyDrawables, mPhotoWidth);
    	case 4:
    		return makeGroupAvatarWithFourChildren(context, copyDrawables, mPhotoWidth);
    	case 5:
    		return makeGroupAvatarWithFiveChildren(context, copyDrawables, mPhotoWidth);
    	case 6:
    		return makeGroupAvatarWithSixChildren(context, copyDrawables, mPhotoWidth);
    	case 7:
    		return makeGroupAvatarWithSevenChildren(context, copyDrawables, mPhotoWidth);
    	case 8:
    		return makeGroupAvatarWithEightChildren(context, copyDrawables, mPhotoWidth);
    	case 9:
    		return makeGroupAvatarWithNineChildren(context, copyDrawables, mPhotoWidth);
    	default:
    		return makeGroupAvatarWithNinePlusChildren(context, copyDrawables, mPhotoWidth);
    	}
    }
    
    
    private static Bitmap makeGroupAvatarWithOneChild(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds((mPhotoWidth>>2), (mPhotoWidth>>2), (mPhotoWidth>>2)*3, (mPhotoWidth>>2)*3);
        drawable.draw(canvas);
        return bitmap;
    }
    
    private static Bitmap makeGroupAvatarWithTwoChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
       
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds(6, (mPhotoWidth>>2)+4, (mPhotoWidth>>1)-3, (mPhotoWidth>>2)*3-5);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds((mPhotoWidth>>1)+3, (mPhotoWidth>>2)+4, (mPhotoWidth)-6, (mPhotoWidth>>2)*3-5);
        drawable.draw(canvas);
        return bitmap;
    }
    
    private static Bitmap makeGroupAvatarWithThreeChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
        
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds((mPhotoWidth>>2)+4, 6, (mPhotoWidth>>2)*3-5, (mPhotoWidth>>1)-3);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds(6, (mPhotoWidth>>1)+3, (mPhotoWidth>>1)-3, mPhotoWidth-6);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(2);
        drawable.setBounds((mPhotoWidth>>1)+3, (mPhotoWidth>>1)+3, (mPhotoWidth)-6, mPhotoWidth-6);
        drawable.draw(canvas);
        
        return bitmap;
    }
    
    private static Bitmap makeGroupAvatarWithFourChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds(6, 6, (mPhotoWidth>>1)-3, (mPhotoWidth>>1)-3);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds((mPhotoWidth>>1)+3, 6, mPhotoWidth-6, (mPhotoWidth>>1)-3);
        drawable.draw(canvas); 
        
        drawable = childDrawables.get(2);
        drawable.setBounds(6, (mPhotoWidth>>1)+3, (mPhotoWidth>>1)-3, mPhotoWidth-6);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(3);
        drawable.setBounds((mPhotoWidth>>1)+3, (mPhotoWidth>>1)+3, (mPhotoWidth)-6, mPhotoWidth-6);
        drawable.draw(canvas);
        
        return bitmap;
    }
    
    private static Bitmap makeGroupAvatarWithFiveChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
       
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds(5, 5, (mPhotoWidth/3)-2, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds((mPhotoWidth/3)+2, 5, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(2);
        drawable.setBounds((mPhotoWidth/3)*2+2, 5, mPhotoWidth-5, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(3);
        drawable.setBounds(5, (mPhotoWidth/3)+2, (mPhotoWidth/3)-2, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(4);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)+2, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);

        return bitmap;
    }
    private static Bitmap makeGroupAvatarWithSixChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
        
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds(5, 5, (mPhotoWidth/3)-2, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds((mPhotoWidth/3)+2, 5, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(2);
        drawable.setBounds((mPhotoWidth/3)*2+2, 5, mPhotoWidth-5, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(3);
        drawable.setBounds(5, (mPhotoWidth/3)+2, (mPhotoWidth/3)-2, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(4);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)+2, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(5);
        drawable.setBounds((mPhotoWidth/3)*2+2, (mPhotoWidth/3)+2, mPhotoWidth-5, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        return bitmap;
    }
    
    private static Bitmap makeGroupAvatarWithSevenChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
        
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds(5, 5, (mPhotoWidth/3)-2, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds((mPhotoWidth/3)+2, 5, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(2);
        drawable.setBounds((mPhotoWidth/3)*2+2, 5, mPhotoWidth-5, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(3);
        drawable.setBounds(5, (mPhotoWidth/3)+2, (mPhotoWidth/3)-2, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(4);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)+2, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(5);
        drawable.setBounds((mPhotoWidth/3)*2+2, (mPhotoWidth/3)+2, mPhotoWidth-5, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(6);
        drawable.setBounds(5, (mPhotoWidth/3)*2+2, (mPhotoWidth/3)-2, mPhotoWidth-5);
        drawable.draw(canvas);
        
        return bitmap;
    }
    private static Bitmap makeGroupAvatarWithEightChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));
        
        Drawable drawable = childDrawables.get(0);
        drawable.setBounds(5, 5, (mPhotoWidth/3)-2, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds((mPhotoWidth/3)+2, 5, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(2);
        drawable.setBounds((mPhotoWidth/3)*2+2, 5, mPhotoWidth-5, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(3);
        drawable.setBounds(5, (mPhotoWidth/3)+2, (mPhotoWidth/3)-2, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(4);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)+2, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(5);
        drawable.setBounds((mPhotoWidth/3)*2+2, (mPhotoWidth/3)+2, mPhotoWidth-5, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(6);
        drawable.setBounds(5, (mPhotoWidth/3)*2+2, (mPhotoWidth/3)-2, mPhotoWidth-5);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(7);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)*2+2, (mPhotoWidth/3)*2-4, mPhotoWidth-5);
        drawable.draw(canvas);
    
        return bitmap;
    }
    private static Bitmap makeGroupAvatarWithNineChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));

        Drawable drawable = childDrawables.get(0);
        drawable.setBounds(5, 5, (mPhotoWidth/3)-2, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(1);
        drawable.setBounds((mPhotoWidth/3)+2, 5, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(2);
        drawable.setBounds((mPhotoWidth/3)*2+2, 5, mPhotoWidth-5, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(3);
        drawable.setBounds(5, (mPhotoWidth/3)+2, (mPhotoWidth/3)-2, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(4);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)+2, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(5);
        drawable.setBounds((mPhotoWidth/3)*2+2, (mPhotoWidth/3)+2, mPhotoWidth-5, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(6);
        drawable.setBounds(5, (mPhotoWidth/3)*2+2, (mPhotoWidth/3)-2, mPhotoWidth-5);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(7);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)*2+2, (mPhotoWidth/3)*2-4, mPhotoWidth-5);
        drawable.draw(canvas);
        
        drawable = childDrawables.get(8);
        drawable.setBounds((mPhotoWidth/3)*2+2, (mPhotoWidth/3)*2+2, mPhotoWidth-5, mPhotoWidth-5);
        drawable.draw(canvas);
        
        return bitmap;
    }
    
    private static Bitmap makeGroupAvatarWithNinePlusChildren(Context context, ArrayList<Drawable>childDrawables, int mPhotoWidth)
    {
        Bitmap bitmap = Bitmap.createBitmap(mPhotoWidth, mPhotoWidth, Bitmap.Config.RGB_565);     // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);         // 建立对应bitmap的画布
        canvas.drawColor(Color.parseColor("#ffffffff"));

        int painting_pos = 0;
        int child_pos = -1;
        int child_size = childDrawables.size();
        Drawable drawable = null;
        ArrayList<Integer> expels = new ArrayList<Integer>();
        for(int i=0;i<child_size;i++)
        {
        	drawable = childDrawables.get(i);
            if(drawable==null)
            {
            	expels.add(i);
            	if(expels.size()+9==child_size)
            	{
            		break;
            	}
            }
        }

        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds(5, 5, (mPhotoWidth/3)-2, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds((mPhotoWidth/3)+2, 5, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds((mPhotoWidth/3)*2+2, 5, mPhotoWidth-5, (mPhotoWidth/3)-2);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds(5, (mPhotoWidth/3)+2, (mPhotoWidth/3)-2, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)+2, (mPhotoWidth/3)*2-4, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds((mPhotoWidth/3)*2+2, (mPhotoWidth/3)+2, mPhotoWidth-5, (mPhotoWidth/3)*2-4);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds(5, (mPhotoWidth/3)*2+2, (mPhotoWidth/3)-2, mPhotoWidth-5);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds((mPhotoWidth/3)+2, (mPhotoWidth/3)*2+2, (mPhotoWidth/3)*2-4, mPhotoWidth-5);
        drawable.draw(canvas);
        
        child_pos++;
        while(true)
        {
        	if(expels.contains(child_pos))
        		child_pos++;
        	else
        		break;
        }
        drawable = childDrawables.get(child_pos);
        drawable.setBounds((mPhotoWidth/3)*2+2, (mPhotoWidth/3)*2+2, mPhotoWidth-5, mPhotoWidth-5);
        drawable.draw(canvas);
        
        return bitmap;
    }
    
}
