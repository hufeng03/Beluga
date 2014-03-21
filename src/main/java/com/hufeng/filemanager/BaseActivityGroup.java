package com.hufeng.filemanager;

import com.hufeng.filemanager.skin.SkinManager;

import android.app.ActivityGroup;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BaseActivityGroup extends ActivityGroup{

	private int mSkin = SkinManager.SKIN_BLACK;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeSkinIfNeeded();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void changeSkinIfNeeded()
	{
		SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(this);
		int skin = sp.getInt(SkinManager.SKIN_SELECTION, SkinManager.SKIN_UNDEFINED);
		if(mSkin!=skin)
		{
			setNewSkin(skin);
			mSkin = skin;
		}
	}
	
	protected void setNewSkin(int skin)
	{
		
	}

}
