package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileUtils;

/**
 * Created by feng on 14-5-6.
 */
public class SearchTabFragment extends FileTabFragment {

    private String mSearchString = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_tab_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        showFileBrowserFragment();
    }

    public void showFileBrowserFragment() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String TAG = "FileBrowserFragment";
        FileBrowserFragment fragment = (FileBrowserFragment) getChildFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = FileBrowserFragment.newRootFileListBrowser(new String[0]);
            ft.replace(R.id.fragment_container, fragment, TAG);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }

        mCurrentChildFragment = fragment;
    }

    public void setSearchString(String query) {
        if (TextUtils.isEmpty(mSearchString)) {
            if (!TextUtils.isEmpty(query)) {
                mSearchString = query;
                performSearch();
            }
        } else {
            if (TextUtils.isEmpty(query) || !mSearchString.equals(query)) {
                mSearchString = query;
                performSearch();
            }
        }
    }



    private void performSearch() {
        getActionController().onOperationSearchFile(mSearchString);
    }


    @Override
    public void refreshUI() {

    }

    @Override
    public FileEntry[] getAllFiles() {
        return new FileEntry[0];
    }

}
