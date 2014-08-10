package com.hufeng.filemanager;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import com.hufeng.filemanager.skin.SkinManager;
import com.hufeng.filemanager.utils.LogUtil;

public class BaseActivity extends FragmentActivity {

    private static final boolean DEBUG = BuildConfig.DEBUG;

	private int mSkin = SkinManager.SKIN_BLACK;
	
	@Override
	protected void onCreate(Bundle arg0) {
        if (DEBUG)
    		LogUtil.i(((Object) this).getClass().getSimpleName(), "onCreate");
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(arg0);
        if (!Constants.ENABLE_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
	}

    @Override
    protected void onStart() {
        if (DEBUG)
            LogUtil.i(((Object)this).getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
	protected void onResume() {
		if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onResume");
		super.onResume();
		changeSkinIfNeeded();
        if (Constants.USE_UMENG) {
		    UmengDelegate.umengAnalysisResume(this);
        }
	}

	@Override
	protected void onPause() {
		if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onPause");
		super.onPause();
        if (Constants.USE_UMENG) {
            UmengDelegate.umengAnalysisPause(this);
        }
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        LogUtil.i(((Object)this).getClass().getSimpleName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
	
	@Override
	protected void onStop() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onStop");
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
        if (DEBUG)
    		LogUtil.i(((Object)this).getClass().getSimpleName(), "onDestroy");
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
