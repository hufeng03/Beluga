package com.hufeng.filemanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.dialog.FmDialogFragment;
import com.hufeng.filemanager.ui.FileOperation;

import java.io.File;

public class SafeBoxActivity extends FileOperationActivity implements LockPatternFragment.LockPatternListener,
        SafeBoxCategoryFragment.SafeBoxCategoryListener,
        SafeBoxGrouperFragment.SafeBoxDetailListener,
        FileGrouperFragment.FileGrouperFragmentListener,
        FmDialogFragment.OnDialogDoneListener{

    private FileGridFragment mCurrentFragment;
    private View mAdView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.tool_name_safe);
		setContentView(R.layout.activity_lock);

        getFileOperation().setOperationMode(FileOperation.OPERATION_MODE.ADD_SAFE);
//        mFileOperation.setListener(this);
		showLockFragment();
        if (Constants.SHOW_AD) {
            mAdView = AdmobDelegate.showAd(this, (LinearLayout) findViewById(R.id.root));
        }
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    public File getFilesDir() {
        return super.getFilesDir();
    }

    public void showLockFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        LockPatternFragment lockPatternFragment = (LockPatternFragment) fm.findFragmentByTag(LockPatternFragment.class.getSimpleName());
        if (lockPatternFragment == null) {
            lockPatternFragment = new LockPatternFragment();
            ft.replace(R.id.fragment_container, lockPatternFragment, LockPatternFragment.class.getSimpleName());
        } else {
            if (lockPatternFragment.isDetached()) {
                ft.attach(lockPatternFragment);
            }
        }
        lockPatternFragment.setLockPatternListener(this);
        mCurrentFragment = null;
//        transaction.addToBackStack(null);
        ft.commit();
    }

    public void showSafeBoxCategoryFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        SafeBoxCategoryFragment safeBoxCategoryFragment = (SafeBoxCategoryFragment) fm.findFragmentByTag(SafeBoxCategoryFragment.class.getSimpleName());
        if (safeBoxCategoryFragment == null) {
            safeBoxCategoryFragment = new SafeBoxCategoryFragment();
            ft.replace(R.id.fragment_container, safeBoxCategoryFragment, SafeBoxCategoryFragment.class.getSimpleName());
        } else {
            if (safeBoxCategoryFragment.isDetached()) {
                ft.attach(safeBoxCategoryFragment);
            }
        }
        safeBoxCategoryFragment.setSafeBoxCategoryListener(this);
        mCurrentFragment = null;
        ft.commit();
    }


    public void showSafeBoxGrouperFragment(int category) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        SafeBoxGrouperFragment safeBoxGrouperFragment = (SafeBoxGrouperFragment) fm.findFragmentByTag(SafeBoxGrouperFragment.class.getSimpleName());
        if (safeBoxGrouperFragment == null) {
            safeBoxGrouperFragment = new SafeBoxGrouperFragment();
            Bundle data = new Bundle();
            data.putInt(SafeBoxGrouperFragment.ARGUMENT_SAFE_BOX_CATEGORY, category);
            safeBoxGrouperFragment.setArguments(data);
            ft.replace(R.id.fragment_container, safeBoxGrouperFragment, SafeBoxGrouperFragment.class.getSimpleName());
        } else {
            if (safeBoxGrouperFragment.isDetached()) {
                ft.attach(safeBoxGrouperFragment);
            }
        }
        safeBoxGrouperFragment.setSafeBoxDetailListener(this);
        mCurrentFragment = null;
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showFileGrouperFragment(int category) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileGrouperFragment fragment = (FileGrouperFragment) fm.findFragmentByTag(FileGrouperFragment.class.getSimpleName());
        if(fragment == null) {
//            fragment = new FileGrouperFragment();
//            Bundle data = new Bundle();
//            data.putInt(FileGrouperFragment.FILE_GROUPER_ARGUMENT_CATEGORY, category);
//            data.putBoolean(FileGrouperFragment.FILE_GROUPER_ARGUMENT_SELECTION, true);
//            fragment.setArguments(data);
            fragment = FileGrouperFragment.newSafeBoxAddSelectInstance(category);
            ft.replace(R.id.fragment_container, fragment, FileGrouperFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        fragment.setListener(this);
//        getFileOperation().setFileOperationProvider(this);
        mCurrentFragment = fragment;
        ft.addToBackStack(null);
        ft.commit();
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
            // This is called when the Home (Up) button is pressed
            // in the Action Bar.
//            Intent parentActivityIntent = new Intent(this, SettingsActivity.class);
//            parentActivityIntent.addFlags(
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(parentActivityIntent);
//            finish();
        	onBackPressed();
            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

    @Override
    public void onPatternSetSuccess() {
        gotoSafeBox();
    }

    @Override
    public void onPatternCheckSuccess() {
        gotoSafeBox();
    }

    @Override
    public void onSafeBoxCategoryClicked(int category) {
        showSafeBoxGrouperFragment(category);
    }

    @Override
    public void onSafeDetailAdd(int category) {
        showFileGrouperFragment(category);
    }

    @Override
    public void onFileGrouperItemClick(View v, FileEntry entry) {
        if (getFileOperation().getFileSelectedSize() > 0) {
            getFileOperation().toggleSelection(entry.path);
        }
    }

    @Override
    public void onFileGrouperItemSelect(View v, FileEntry entry) {
        getFileOperation().toggleSelection(entry.path);
    }


//    @Override
//    public void reloadData() {
//        if(mCurrentFragment instanceof FileGridFragment) {
//            ((FileGridFragment) mCurrentFragment).reloadData();
//        }
//    }

//    @Override
//    public String[] getAllFiles() {
//        if(mCurrentFragment instanceof FileGridFragment) {
//            return ((FileGridFragment) mCurrentFragment).getAllFiles();
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public String getRootDir() {
//        if(mCurrentFragment instanceof FileGridFragment) {
//            return ((FileGridFragment) mCurrentFragment).getRootDir();
//        } else {
//            return null;
//        }
//    }

    private void gotoSafeBox(){
        showSafeBoxCategoryFragment();
    }


    @Override
    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
        switch(dialog_id) {
            case FmDialogFragment.ADD_TO_SAFE_DIALOG:
                getFileOperation().onOperationAddToSafeConfirm(this);
                onBackPressed();
                break;
        }
    }

    @Override
    public void refreshFiles() {
        if(mCurrentFragment != null && mCurrentFragment instanceof FileGridFragment) {
            mCurrentFragment.refreshUI();
        }
        invalidateOptionsMenu();
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
    protected FileOperation getFileOperation() {
        if (mCurrentFragment != null) {
            return mCurrentFragment.getFileOperation();
        } else {
            return super.getFileOperation();
        }
    }
}
