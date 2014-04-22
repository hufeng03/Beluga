package com.hufeng.playzip;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hufeng.filemanager.BaseActivity;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileUtils;

/**
 * Created by feng on 2014-03-24.
 */
public class PlayZipActivity extends BaseActivity implements
        ZipTreeFragment.ZipTreeFragmentListener,
        ZipWorkFragment.ZipWorkProgressInterface {

    private static final String TAG = PlayZipActivity.class.getSimpleName();

    ActionBar mActionBar;

    private ProgressDialog mProgressDialog;

    private Uri mUri;

    private ZipWorkFragment mZipWorkFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        FrameLayout root = new FrameLayout(this);
        root.setId(100);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);
        mActionBar = getActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mUri = getIntent().getData();

        mUri = FileUtils.getPathFromMediaContent(this, mUri);

        Log.i(TAG, "intent data is " + mUri.getEncodedPath());
        addZipWorkFragment();
        addZipTreeFragment(mUri.getEncodedPath());
    }


    private void addZipWorkFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(ZipWorkFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = ZipWorkFragment.newZipWorkFragment();
            ft.add(fragment, ZipWorkFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        mZipWorkFragment = (ZipWorkFragment)fragment;
        ft.commit();
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
        ((ZipTreeFragment)fragment).setListener(this);

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

    @Override
    public void onZipTreeItemClick(FileEntry entry) {
        if(!entry.isDirectory()) {
            unZipSingleFile(entry.path);
        }
    }

    private void unZipSingleFile(String file) {
        if (mZipWorkFragment != null) {
            mZipWorkFragment.unZipSingleFile(mUri.getEncodedPath(), file);
        }
    }

    private void showProgressDialog(String title, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
            mProgressDialog.setTitle(title);//设置标题
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(false);//设置进度条是否为不明确
            mProgressDialog.setCancelable(false);//设置进度条是否可以按退回键取消
            mProgressDialog.setCanceledOnTouchOutside(false);
        } else {
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    @Override
    public void onUnzipProgress(String path, int progress) {

    }

    @Override
    public void onZipProgress(String path, int progress) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


}
