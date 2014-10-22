package com.hufeng.filemanager.skin;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.utils.ChannelUtil;

public class SkinManager {
	
	public static final String SKIN_SELECTION = "skin_selection";
	public static final int SKIN_UNDEFINED = 0;
	public static final int SKIN_RED = 1;
	public static final int SKIN_BLACK = 2;

	
	public static int getBackButtonResourceId(int skin)
	{
		if(skin==SKIN_UNDEFINED)
		{
			if(ChannelUtil.isDOOV_ROOMChannel(FileManager.getAppContext()))
			{
				skin = SKIN_RED;
			}
		}
		if(skin==SKIN_RED)
			return R.drawable.doov_btn_back_bg;
		else
			return R.drawable.btn_back_bg;
	}
	
	public static int getTabBarResourceId(int skin)
	{
		if(skin==SKIN_UNDEFINED)
		{
			if(ChannelUtil.isDOOV_ROOMChannel(FileManager.getAppContext()))
			{
				skin = SKIN_RED;
			}
		}
		if(skin==SKIN_RED)
			return R.drawable.doov_top_tab_bg;
		else
			return R.drawable.top_tab_bg;

	}
	
	public static int getLeftTabResourceId(int skin)
	{
		if(skin==SKIN_UNDEFINED)
		{
			if(ChannelUtil.isDOOV_ROOMChannel(FileManager.getAppContext()))
			{
				skin = SKIN_RED;
			}
		}
		if(skin==SKIN_RED)
			return R.drawable.doov_top_tab_left;
		else
			return R.drawable.top_tab_left;
	}
	
	public static int getMiddleTabResourceId(int skin)
	{
		if(skin==SKIN_UNDEFINED)
		{
			if(ChannelUtil.isDOOV_ROOMChannel(FileManager.getAppContext()))
			{
				skin = SKIN_RED;
			}
		}
		if(skin==SKIN_RED)
			return R.drawable.doov_top_tab_middle;
		else
			return R.drawable.top_tab_middle;
	}
	
	public static int getRightTabResourceId(int skin)
	{
		if(skin==SKIN_UNDEFINED)
		{
			if(ChannelUtil.isDOOV_ROOMChannel(FileManager.getAppContext()))
			{
				skin = SKIN_RED;
			}
		}
		if(skin==SKIN_RED)
			return R.drawable.doov_top_tab_right;
		else
			return R.drawable.top_tab_right;
	}

}
