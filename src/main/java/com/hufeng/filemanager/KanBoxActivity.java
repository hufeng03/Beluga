package com.hufeng.filemanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.dialog.FmDialogFragment;
import com.hufeng.filemanager.kanbox.KanBoxTabFragment;
import com.hufeng.filemanager.ui.FileOperation;

//import com.google.ads.AdView;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxActivity extends FileOperationActivity{

    KanBoxTabFragment mCurrentFragment;
    View mAdView = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.tool_name_kanbox);
        setContentView(R.layout.activity_kanbox);

//        getFileOperation().setOperationMode(FileOperation.OPERATION_MODE.ADD_CLOUD);
        showKanBoxTabFragment();

//        if (Constants.SHOW_AD) {
//            mAdView = AdmobDelegate.showAd(this, (LinearLayout) findViewById(R.id.root));
//        }
        enableImageAnimatorView((ViewGroup)getWindow().getDecorView());
    }


    @Override
    protected FileOperation getFileOperation() {
        if (mCurrentFragment != null) {
            return mCurrentFragment.getFileOperation();
        } else {
            return super.getFileOperation();
        }
    }

    private void showKanBoxTabFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        KanBoxTabFragment kanBoxFragment = (KanBoxTabFragment) fm.findFragmentByTag(KanBoxTabFragment.class.getSimpleName());
        if (kanBoxFragment == null) {
            kanBoxFragment = new KanBoxTabFragment();
            ft.replace(R.id.fragment_container, kanBoxFragment, KanBoxTabFragment.class.getSimpleName());
        } else {
            if (kanBoxFragment.isDetached()) {
                ft.attach(kanBoxFragment);
            }
        }
        mCurrentFragment = kanBoxFragment;
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
        if(mCurrentFragment!=null) {
            if (mCurrentFragment.onBackPressed()){
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constants.SHOW_AD) {
            if (mAdView != null) {
                AdmobDelegate.distroyAd(mAdView);
            }
        }
    }


    @Override
    public String[] getAllFiles() {
        if (mCurrentFragment != null) {
            return mCurrentFragment.getAllFiles();
        } else {
            return null;
        }
    }

    @Override
    public String getParentFile() {
        if (mCurrentFragment != null) {
            return mCurrentFragment.getParentFile();
        } else {
            return null;
        }
    }

    @Override
    public void refreshFiles() {
        if (mCurrentFragment != null) {
            mCurrentFragment.refreshFiles();
        }
        return;
    }

    @Override
    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
        super.onDialogDone(dialog, dialog_id, button, param);
        if (dialog_id == FmDialogFragment.ADD_TO_CLOUD_DIALOG) {
            onBackPressed();
        }
    }

}
