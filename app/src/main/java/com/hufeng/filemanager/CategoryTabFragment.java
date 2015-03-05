package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hufeng.filemanager.data.FileEntry;
import com.squareup.otto.Subscribe;



public class CategoryTabFragment extends FileTabFragment {

	private static final String CATEGORY_TYPE = "category_type";
    private static final String FOLDER_PATH = "folder_path";
    private CategorySelectEvent.CategoryType mCategory = CategorySelectEvent.CategoryType.NONE;
    private Fragment mCategoryFragment;

    private String mFolderPath;

    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String categoryValue = savedInstanceState.getString(CATEGORY_TYPE);
            if (categoryValue != null) {
                mCategory = CategorySelectEvent.CategoryType.valueOf(categoryValue);
            }
            mFolderPath = savedInstanceState.getString(FOLDER_PATH);
        }
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putString(CATEGORY_TYPE, mCategory.toString());
        outState.putString(FOLDER_PATH, mFolderPath);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.category_tab_fragment, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        showSingleCategoryPanel();
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
    public void onCategorySelected(CategorySelectEvent event) {
        if (event != null) {
            mCategory = event.category;
            showSingleCategoryPanel();
        }
    }

    @Subscribe
    public void onFolderOpen(FolderOpenEvent event) {
        if (event != null) {
            mFolderPath = event.entry.path;
            toFileBrowser();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
	public boolean onBackPressed() {
        if ( super.onBackPressed() ){
            return true;
        }
        switch (mCategory) {
            case DOWNLOAD:
                if (!TextUtils.isEmpty(mFolderPath)) {
                    toDownloadList();
                    mFolderPath  = null;
                    return true;
                }
                break;
            case FAVORITE:
                if (!TextUtils.isEmpty(mFolderPath)) {
                    toFileGrouper();
                    mFolderPath  = null;
                    return true;
                }
                break;
            case NONE:
                return false;
            default:
                break;
        }
        mCategory = CategorySelectEvent.CategoryType.NONE;
        showSingleCategoryPanel();

		return true;
	}

    private void toCategoryPanel() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        NewCategoryFragment fragment = (NewCategoryFragment) getChildFragmentManager().findFragmentByTag(NewCategoryFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new NewCategoryFragment();
            ft.replace(R.id.fragment_container, fragment, NewCategoryFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        mCategoryFragment = fragment;
        mCurrentChildFragment = null;
        mCategory = CategorySelectEvent.CategoryType.NONE;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("Category Panel");
        t.send(new HitBuilders.AppViewBuilder().build());
	}


    private void toFileGrouper() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "FileGrouper";
        FileGrouperFragment fragment = (FileGrouperFragment) fm.findFragmentByTag(tag);
        if(fragment == null) {
            fragment = FileGrouperFragment.newCategoryGrouperInstance(mCategory);
            ft.replace(R.id.fragment_container, fragment, tag);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        mCurrentChildFragment = fragment;
        mCategoryFragment = null;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("File Grouper: "+mCategory);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void toDownloadList() {
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
        mCategoryFragment = null;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("Download List: "+mCategory);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void toFileBrowser() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "FileBrowser";
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = FileBrowserFragment.newRootFolderBrowser(mFolderPath);
            ft.replace(R.id.fragment_container, fragment, tag);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentChildFragment = fragment;
        mCategoryFragment = null;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("File Browser: "+mCategory);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showSingleCategoryPanel() {
        switch(mCategory) {
            case NONE:
                toCategoryPanel();
                break;
            case FAVORITE:
                if (TextUtils.isEmpty(mFolderPath)) {
                    toFileGrouper();
                } else {
                    toFileBrowser();
                }
                break;
            case DOWNLOAD:
                if (TextUtils.isEmpty(mFolderPath)) {
                    toDownloadList();
                } else {
                    toFileBrowser();
                }
                break;
            case AUDIO:
            case PHOTO:
            case VIDEO:
            case APK:
            case DOC:
            case ZIP:
                toFileGrouper();
                break;
            default:
                break;
        }
    }

    @Override
    protected void showInitialState() {
        super.showInitialState();
        toCategoryPanel();
    }

    @Override
    public void refreshUI() {
        if(mCurrentChildFragment != null) {
            mCurrentChildFragment.refreshUI();
        }
    }

    @Override
    public FileEntry[] getAllFiles() {
        if(mCurrentChildFragment != null) {
            return mCurrentChildFragment.getAllFiles();
        } else {
            return null;
        }
    }

}
