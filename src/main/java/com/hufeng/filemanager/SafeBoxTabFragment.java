package com.hufeng.filemanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.dialog.FmDialogFragment;

/**
 * Created by feng on 14-6-1.
 */
public class SafeBoxTabFragment extends FileTabFragment implements
        LockPatternFragment.LockPatternListener,
        SafeBoxCategoryFragment.SafeBoxCategoryListener,
        SafeBoxGrouperFragment.SafeBoxDetailListener,
        FileGrouperFragment.FileGrouperCallbacks,
        FmDialogFragment.OnDialogDoneListener{



    public static SafeBoxTabFragment newFragment() {
        return new SafeBoxTabFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.safebox_fragment, container, false);
        showLockFragment();
        return view;
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

    private void gotoSafeBox(){
        showSafeBoxCategoryFragment();
    }

    public void showSafeBoxCategoryFragment() {
        final FragmentManager fm = getChildFragmentManager();
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
        ft.commit();
    }

    public void showLockFragment() {
        final FragmentManager fm = getChildFragmentManager();
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
//        mCurrentFragment = null;
//        transaction.addToBackStack(null);
        ft.commit();
    }


    public void showSafeBoxGrouperFragment(int category) {
        final FragmentManager fm = getChildFragmentManager();
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
//        mCurrentFragment = null;
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showFileGrouperFragment(int category) {
        final FragmentManager fm = getChildFragmentManager();
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
//        getFileOperation().setFileOperationProvider(this);
//        mCurrentFragment = fragment;
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
        switch(dialog_id) {
            case FmDialogFragment.ADD_TO_SAFE_DIALOG:
                getFileOperation().onOperationAddToSafeConfirm(getActivity());
                onBackPressed();
                break;
        }
    }


    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    protected void showFile(String path) {

    }

    @Override
    protected void closeFile(String path) {

    }

    @Override
    public void refreshFiles() {

    }

    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public String getParentFile() {
        return null;
    }

    @Override
    public void onFileBrowserDirShown(String path) {

    }
}
