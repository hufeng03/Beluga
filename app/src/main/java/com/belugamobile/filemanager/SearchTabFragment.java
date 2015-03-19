package com.belugamobile.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.squareup.otto.Subscribe;

/**
 * Created by feng on 14-5-6.
 */
public class SearchTabFragment extends FileTabFragment {

    private String mSearchString = null;
//    private String mZipPath;

    private static final String SEARCH_STRING = "search_string";
//    private static final String ZIP_FILE_PATH = "zip_file_path";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSearchString = savedInstanceState.getString(SEARCH_STRING);
//            mZipPath = savedInstanceState.getString(ZIP_FILE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_tab_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showSingleSearchPanel();
//        showFileBrowserFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSearchString)) {
            outState.putString(SEARCH_STRING, mSearchString);
        }
//        if (!TextUtils.isEmpty(mZipPath)) {
//            outState.putString(ZIP_FILE_PATH, mZipPath);
//        }
    }

    @Override
    public boolean onBackPressed() {
        if (super.onBackPressed()) {
            return true;
        }
//        if (!TextUtils.isEmpty(mZipPath)) {
//            mZipPath = null;
//        } else {
            return false;
//        }
//        showSingleSearchPanel();
//        return true;
    }

    private void showSingleSearchPanel() {
//        if (!TextUtils.isEmpty(mZipPath)) {
//            showZipBrowserFragment();
//        } else {
            showFileSearchFragment();
//        }
    }

//    @Subscribe
//    public void onZipView(ZipViewEvent event) {
//        if (getUserVisibleHint() && event != null) {
//            mZipPath = event.path;
//            showZipBrowserFragment();
//        }
//    }

    public void showFileSearchFragment() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String TAG = "FileSearchFragment";
        FileSearchFragment fragment = (FileSearchFragment) getChildFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = FileSearchFragment.newFragment(mSearchString);
            ft.replace(R.id.fragment_container, fragment, TAG);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }

        mCurrentChildFragment = fragment;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("Search: "+mSearchString);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

//    public void showZipBrowserFragment() {
//        final FragmentManager fm = getChildFragmentManager();
//        final FragmentTransaction ft = fm.beginTransaction();
//        final String TAG = "ZipBrowserFragment";
//        ZipBrowserFragment fragment = (ZipBrowserFragment) fm.findFragmentByTag(TAG);
//        if (fragment == null) {
//            fragment = ZipBrowserFragment.newFragment(mZipPath);
//            ft.replace(R.id.fragment_container, fragment, TAG);
//            ft.commit();
//        } else {
//            if (fragment.isDetached()) {
//                ft.attach(fragment);
//                ft.commit();
//            }
//        }
//        mCurrentChildFragment = fragment;
//
//        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
//        t.setScreenName("Zip Browser: "+mZipPath);
//        t.send(new HitBuilders.AppViewBuilder().build());
//    }

//    public void showFileBrowserFragment() {
//        final FragmentManager fm = getChildFragmentManager();
//        final FragmentTransaction ft = fm.beginTransaction();
//        final String TAG = "FileBrowserFragment";
//        FileBrowserFragment fragment = (FileBrowserFragment) getChildFragmentManager().findFragmentByTag(TAG);
//        if (fragment == null) {
//            fragment = FileBrowserFragment.newRootFileListBrowser(new String[0]);
//            ft.replace(R.id.fragment_container, fragment, TAG);
//            ft.commit();
//        } else {
//            if (fragment.isDetached()) {
//                ft.attach(fragment);
//                ft.commit();
//            }
//        }
//
//        mCurrentChildFragment = fragment;
//    }

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
//        getActionController().performSearch(mSearchString);
        if (mCurrentChildFragment != null) {
            ((FileSearchFragment) mCurrentChildFragment).performSearch(mSearchString);
        }
    }


    @Override
    public void refreshUI() {

    }

    @Override
    public BelugaFileEntry[] getAllFiles() {
        return new BelugaFileEntry[0];
    }

}
