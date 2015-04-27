package com.belugamobile.filemanager;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.squareup.otto.Subscribe;



public class CategoryTabFragment extends FileTabFragment {

	private static final String CATEGORY = "category";
    private static final String FOLDER_PATH = "folder_path";
    private static final String ZIP_FILE_PATH = "zip_file_path";
    private int mCategory;
    private String mFolderPath;
    private String mZipPath;

    public NewCategoryFragment mCategoryFragment;

    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getInt(CATEGORY);
            mFolderPath = savedInstanceState.getString(FOLDER_PATH);
            mZipPath = savedInstanceState.getString(ZIP_FILE_PATH);
        }
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putInt(CATEGORY, mCategory);
        if (!TextUtils.isEmpty(mFolderPath)) {
            outState.putString(FOLDER_PATH, mFolderPath);
        }
        if (!TextUtils.isEmpty(mZipPath)) {
            outState.putString(ZIP_FILE_PATH, mZipPath);
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.category_tab_fragment, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        mCategoryFragment = (NewCategoryFragment)getChildFragmentManager().findFragmentById(R.id.category_fragment);
        refreshCategoryDetailPane();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	
	@Override
	public void onResume(){
		super.onResume();
        BusProvider.getInstance().register(this);
	}
	
	@Override
	public void onPause(){
		super.onPause();
        BusProvider.getInstance().unregister(this);
	}

    @Subscribe
    public void onTabLongPress(TabLongPressEvent event) {
        if (getUserVisibleHint() && event != null) {
            if (event.name.equalsIgnoreCase(getString(R.string.tab_category))) {
                Toast.makeText(getActivity(), "test_category", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Subscribe
    public void onCategorySelected(CategorySelectEvent event) {
        if (getUserVisibleHint() && event != null) {
            mCategory = event.category;
            mZipPath = null;
            mFolderPath = null;
            refreshCategoryDetailPane();
        }
    }

    @Subscribe
    public void onFolderOpen(FolderOpenEvent event) {
        if (getUserVisibleHint() && event != null) {
            mFolderPath = event.entry.path;
            showFileBrowser();
        }
    }

    @Subscribe
    public void onZipView(ZipSelectEvent event) {
        if (getUserVisibleHint() && event != null) {
            mZipPath = event.path;
            showZipBrowser();
        }
    }

    @Override
	public boolean onBackPressed() {
        if ( super.onBackPressed() ){
            return true;
        }

        if (!TextUtils.isEmpty(mZipPath)) {
            mZipPath = null;
        } else if (!TextUtils.isEmpty(mFolderPath)) {
            mFolderPath  = null;
        } else if (mCategory != FileCategoryHelper.CATEGORY_TYPE_UNKNOW) {
            mCategory = FileCategoryHelper.CATEGORY_TYPE_UNKNOW;
        } else {
            return false;
        }
        refreshCategoryDetailPane();

        return true;
	}


    private void showEmptyDetail() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        if (mCurrentChildFragment != null) {
            ft.remove(mCurrentChildFragment);
            ft.commit();
            mCurrentChildFragment = null;
        }
    }

    private void showFileGrouper() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "FileGrouper";
        FileGrouperFragment fragment = (FileGrouperFragment) fm.findFragmentByTag(tag);
        if(fragment == null) {
            fragment = FileGrouperFragment.newCategoryGrouperInstance(mCategory);
            ft.replace(R.id.fragment_container, fragment, tag);
        } else {
            fragment.setCategory(mCategory);
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        mCurrentChildFragment = fragment;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("File Grouper: "+mCategory);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showFavoriteList() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "FavoriteList";
        NewFavoriteFragment fragment = (NewFavoriteFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new NewFavoriteFragment();
            ft.replace(R.id.fragment_container, fragment, tag);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentChildFragment = fragment;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("Favorite List: "+mCategory);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showDownloadList() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "DownloadList";
        NewDownloadFragment fragment = (NewDownloadFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new NewDownloadFragment();
            ft.replace(R.id.fragment_container, fragment, tag);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentChildFragment = fragment;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("Download List: "+mCategory);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showFileBrowser() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "FileBrowser";
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = FileBrowserFragment.newRootFolderBrowser(mFolderPath, null);
            ft.replace(R.id.fragment_container, fragment, tag);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentChildFragment = fragment;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("File Browser: "+mCategory);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showZipBrowser() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String TAG = "ZipBrowser";
        ZipBrowserFragment fragment = (ZipBrowserFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = ZipBrowserFragment.newFragment(mZipPath);
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
        t.setScreenName("Zip Browser: "+mZipPath);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void refreshCategoryDetailPane() {
        if (!TextUtils.isEmpty(mZipPath)) {
            showZipBrowser();
        } else if (!TextUtils.isEmpty(mFolderPath)) {
            showFileBrowser();
        } else {
            switch (mCategory) {
                case FileCategoryHelper.CATEGORY_TYPE_FAVORITE:
                    showFavoriteList();
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_DOWNLOAD:
                    showDownloadList();
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_UNKNOW:
                    showEmptyDetail();
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                case FileCategoryHelper.CATEGORY_TYPE_APK:
                case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                    showFileGrouper();
                    break;
                default:
                    break;
            }
        }
        if (mCategoryFragment != null) {
            mCategoryFragment.setSelectedCategory(mCategory);
        }
    }

    @Override
    protected void showInitialState() {
        super.showInitialState();
        mZipPath = null;
        mFolderPath  = null;
        mCategory = FileCategoryHelper.CATEGORY_TYPE_UNKNOW;
        refreshCategoryDetailPane();
    }

    @Override
    public void refreshUI() {
        if(mCurrentChildFragment != null) {
            mCurrentChildFragment.refreshUI();
        }
    }

    @Override
    public BelugaFileEntry[] getAllFiles() {
        if(mCurrentChildFragment != null) {
            return mCurrentChildFragment.getAllFiles();
        } else {
            return null;
        }
    }

}
