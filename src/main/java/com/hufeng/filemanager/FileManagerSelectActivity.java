package com.hufeng.filemanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.ui.FileOperation;

import java.io.File;

/**
 * Created by feng on 2014-07-02.
 */
public class FileManagerSelectActivity extends FileOperationActivity implements FileBrowserFragment.FileBrowserCallbacks{

    private FileBrowserFragment mFileBrowserFragment;

    @Override
    protected FileOperation getFileOperation() {
        return mFileBrowserFragment.getFileOperation();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_select);
        showFileSelectFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean flag = preferences.getBoolean("LONG_PRESS_TO_SELECT_SHOWN", false);
        if (flag) {
            Toast.makeText(this, R.string.long_press_to_select, Toast.LENGTH_SHORT).show();
            preferences.edit().putBoolean("LONG_PRESS_TO_SELECT_SHOWN", true).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFileBrowserFragment == null || !mFileBrowserFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void showFileSelectFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(FileBrowserFragment.class.getSimpleName());
        if(fragment == null) {
            fragment = FileBrowserFragment.newSelectionBrowser();
            ft.replace(R.id.fragment_container, fragment, FileBrowserFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
//        ft.addToBackStack(null);
        mFileBrowserFragment = fragment;
        ft.commit();
    }


    @Override
    public void onFileBrowserItemClick(View v, FileEntry entry) {
        if (entry.is_directory) {
            mFileBrowserFragment.showDir(entry.path);
        } else {
            //view file
            FileAction.viewFile(this, entry.path);
        }
    }

    @Override
    public void onFileBrowserItemSelect(View v, FileEntry entry) {
        if (!entry.exist) {
            return;
        }
//        if (StorageManager.getInstance(this).isStorage(entry.path)) {
//            return;
//        }
        if (entry.is_directory) {
            Toast.makeText(this, R.string.can_not_select_dir, Toast.LENGTH_SHORT).show();
            return;
        }
        if (getFileOperation() != null) {
            getFileOperation().setSelection(entry.path);
        }
    }

    @Override
    public void onFileBrowserItemClose(FileEntry entry) {

    }

    @Override
    public void onFileBrowserDirShown(String path) {

    }

    @Override
    public void refreshFiles() {
        mFileBrowserFragment.refreshUI();
    }

    @Override
    public String[] getAllFiles() {
        return mFileBrowserFragment.getAllFiles();
    }

    @Override
    public String getParentFile() {
        return mFileBrowserFragment.getParentFile();
    }
}
