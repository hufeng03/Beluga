package com.hufeng.filemanager;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.hufeng.filemanager.skin.SkinManager;

public class BaseActivity extends SherlockFragmentActivity{

    private static final boolean DEBUG = BuildConfig.DEBUG;

	private int mSkin = SkinManager.SKIN_BLACK;
	
	@Override
	protected void onCreate(Bundle arg0) {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onCreate");
		super.onCreate(arg0);
        if (!Constants.ENABLE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
	}

    @Override
    protected void onStart() {
        if (DEBUG)
            Log.i(((Object)this).getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
	protected void onResume() {
		if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onResume");
		super.onResume();
		changeSkinIfNeeded();
        if (Constants.USE_UMENG) {
		    UmengDelegate.umengAnalysisResume(this);
        }
	}

	@Override
	protected void onPause() {
		if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onPause");
		super.onPause();
        if (Constants.USE_UMENG) {
            UmengDelegate.umengAnalysisPause(this);
        }
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(((Object)this).getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
	
	@Override
	protected void onStop() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
        if (DEBUG)
    		Log.i(((Object)this).getClass().getSimpleName(), "onDestroy");
		super.onDestroy();
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
