package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.ui.FileOperation;

//import com.google.ads.AdView;

/**
 * Created by feng on 13-11-28.
 */
public class ResourceActivity extends FileOperationActivity{

    ResourceFragment mCurrentFragment;
    private FileOperation mFileOperation;

    private View mAdView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.tool_name_selected);
        setContentView(R.layout.activity_kanbox);

        showResourceFragment();

        if (Constants.SHOW_AD) {
            mAdView = AdmobDelegate.showAd(this, (LinearLayout) findViewById(R.id.root));
        }

    }

    private void showResourceFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ResourceFragment ResourceFragment = (ResourceFragment) fm.findFragmentByTag(ResourceFragment.class.getSimpleName());
        if (ResourceFragment == null) {
            ResourceFragment = new ResourceFragment();
            ft.replace(R.id.fragment_container, ResourceFragment, ResourceFragment.class.getSimpleName());
        } else {
            if (ResourceFragment.isDetached()) {
                ft.attach(ResourceFragment);
            }
        }
        mCurrentFragment = ResourceFragment;
//        mFileOperation.setListener(mCurrentFragment);
//        transaction.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mCurrentFragment!=null && mCurrentFragment.onBackPressed()){
            return;
        }
        super.onBackPressed();
    }

    @Override
    public FileOperation getFileOperation() {
        return mFileOperation;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constants.SHOW_AD) {
            if(mAdView!=null){
                AdmobDelegate.distroyAd(mAdView);
            }
        }
    }

    @Override
    public String getParentFile() {
        return null;
    }

    @Override
    public String[] getAllFiles() {
        return null;
    }

    @Override
    public void refreshFiles() {
        super.refreshUI();
    }
}
