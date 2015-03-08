package com.hufeng.filemanager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;

import com.hufeng.filemanager.receiver.MountReceiver;
import com.hufeng.filemanager.utils.LogUtil;

public abstract class BelugaBaseActionBarActivity extends ActionBarActivity {

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private BelugaMountReceiver mBelugaMountReceiver;
	
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


        mBelugaMountReceiver = BelugaMountReceiver.registerMountReceiver(this);

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
        unregisterReceiver(mBelugaMountReceiver);
	}

    @Override
    public void supportInvalidateOptionsMenu() {
        if (DEBUG)
            LogUtil.i(((Object)this).getClass().getSimpleName(), "supportInvalidateOptionsMenu");
        super.supportInvalidateOptionsMenu();
    }
}
