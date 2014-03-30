package com.hufeng.playzip;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.BaseActivity;

/**
 * Created by feng on 2014-03-24.
 */
public class PlayZipActivity extends BaseActivity{

    private static final String TAG = PlayZipActivity.class.getSimpleName();

    ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        FrameLayout root = new FrameLayout(this);
        root.setId(100);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        Uri uri = getIntent().getData();
        Log.i(TAG, "intent data is " + uri.getEncodedPath());
        addZipTreeFragment(uri.getEncodedPath());
    }

    private void addZipTreeFragment(String dir) {

        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(ZipTreeFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = ZipTreeFragment.newZipBrowser(dir);
            ft.replace(100, fragment, ZipTreeFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
