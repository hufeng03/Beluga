package com.belugamobile.filemanager;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.dialog.BelugaDialogFragment;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.intent.Constant;
import com.belugamobile.filemanager.ui.BelugaActionController;

import refactor.com.android.contacts.common.util.ViewUtil;

/**
 * Created by feng on 2014-07-02.
 */
public class BelugaPickActivity extends BelugaActionControllerActivity {

    private BelugaFragmentInterface mCurrentFragment;

    @Override
    public BelugaActionController getActionController() {
        return getGlobalActionController();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beluga_pick_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String action = getIntent().getAction();

        if (Constant.ACTION_PICK_FILE.equals(action)) {
            String type = getIntent().getType();
            if (type.equals("image/*")) {
                showFileGrouperPickFragment(FileCategoryHelper.CATEGORY_TYPE_IMAGE);
            } else if (type.equals("audio/*")) {
                showFileGrouperPickFragment(FileCategoryHelper.CATEGORY_TYPE_AUDIO);
            } else if (type.equals("video/*")) {
                showFileGrouperPickFragment(FileCategoryHelper.CATEGORY_TYPE_VIDEO);
            } else if (type.equals("apk/*")) {
                showFileGrouperPickFragment(FileCategoryHelper.CATEGORY_TYPE_APK);
            } else if (type.equals("zip/*")) {
                showFileGrouperPickFragment(FileCategoryHelper.CATEGORY_TYPE_ZIP);
            } else if (type.equals("doc/*")) {
                showFileGrouperPickFragment(FileCategoryHelper.CATEGORY_TYPE_DOCUMENT);
            } else {
                showFileBrowserPickFragment();
            }
            getActionController().setOperationMode(BelugaActionController.OPERATION_MODE.PICK);
        } else {
            if (Constant.ACTION_PICK_FOLDER_TO_COPY_FILE.equals(action)) {
                setTitle("Paste");
                BelugaFileEntry[] entries = BelugaFileEntry.toFileEntries(getIntent().getParcelableArrayExtra(BelugaDialogFragment.FILE_ARRAY_DATA));
                getActionController().setEntrySelection(true, entries);
                getActionController().setOperationMode(BelugaActionController.OPERATION_MODE.COPY_PASTE);
                showFileBrowserPasteFragment();
            } else if (Constant.ACTION_PICK_FOLDER_TO_MOVE_FILE.equals(action)) {
                setTitle("Paste");
                BelugaFileEntry[] entries = BelugaFileEntry.toFileEntries(getIntent().getParcelableArrayExtra(BelugaDialogFragment.FILE_ARRAY_DATA));
                getActionController().setEntrySelection(true, entries);
                getActionController().setOperationMode(BelugaActionController.OPERATION_MODE.CUT_PASTE);
                showFileBrowserPasteFragment();
            } else if (Constant.ACTION_PICK_FOLDER_TO_EXTRACT_ARCHIVE.equals(action)) {
                setTitle("Extract");
                BelugaFileEntry[] entries = BelugaFileEntry.toFileEntries(getIntent().getParcelableArrayExtra(BelugaDialogFragment.FILE_ARRAY_DATA));
                getActionController().setEntrySelection(true, entries);
                getActionController().setOperationMode(BelugaActionController.OPERATION_MODE.EXTRACT_ARCHIVE);
                showFileBrowserPasteFragment();
            } else if (Constant.ACTION_PICK_FOLDER_TO_CREATE_ARCHIVE.equals(action)) {
                setTitle("Archive");
                BelugaFileEntry[] entries = BelugaFileEntry.toFileEntries(getIntent().getParcelableArrayExtra(BelugaDialogFragment.FILE_ARRAY_DATA));
                getActionController().setEntrySelection(true, entries);
                getActionController().setOperationMode(BelugaActionController.OPERATION_MODE.CREATE_ARCHIVE);
                showFileBrowserPasteFragment();
            } else {
                getActionController().setOperationMode(BelugaActionController.OPERATION_MODE.PICK);
                showFileBrowserPickFragment();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Add shadow under toolbar
            ViewUtil.addRectangularOutlineProvider(findViewById(R.id.toolbar_parent), getResources());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, R.string.click_to_select, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentFragment == null || !mCurrentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void showFileGrouperPickFragment(int category) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "FileGrouperSelect";
        FileGrouperFragment fragment = (FileGrouperFragment) fm.findFragmentByTag(tag);
        if(fragment == null) {
            fragment = FileGrouperFragment.newSelectionGrouper(category);
            ft.replace(R.id.fragment_container, fragment, tag);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        mCurrentFragment = fragment;
        ft.commit();
    }

    private void showFileBrowserPickFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "DeviceTabFragment";
        DeviceTabFragment fragment = (DeviceTabFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new DeviceTabFragment();
            ft.replace(R.id.fragment_container, fragment, tag);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentFragment = fragment;
    }

    private void showFileBrowserPasteFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "DeviceTabFragment";
        DeviceTabFragment fragment = (DeviceTabFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new DeviceTabFragment();
            ft.replace(R.id.fragment_container, fragment, tag);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentFragment = fragment;
    }


    @Override
    public void invalidate() {
        super.invalidate();
        if (mCurrentFragment != null) {
            mCurrentFragment.refreshUI();
        }
    }

    @Override
    public BelugaFileEntry[] getAllEntries() {
        if (mCurrentFragment != null) {
            return mCurrentFragment.getAllFiles();
        } else {
            return null;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
