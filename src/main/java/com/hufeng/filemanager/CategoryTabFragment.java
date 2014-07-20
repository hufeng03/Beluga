package com.hufeng.filemanager;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.provider.UiProvider;
import com.hufeng.filemanager.utils.LogUtil;
import com.squareup.otto.Subscribe;

import java.io.File;


public class CategoryTabFragment extends FileTabFragment {

    public static final String TAG = "CategoryTabFragment";

	private static final String CATEGORY_TYPE = "category_type";
    private int mCategory = FileUtils.FILE_TYPE_ALL;
    private Fragment mCategoryFragment;
    ContentObserver mContentObserver;

    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if (data != null) {
            mCategory = data.getInt(CATEGORY_TYPE);
        } else {
            mCategory = FileUtils.FILE_TYPE_ALL;
        }
        if(savedInstanceState == null) {
            showChildCategoryPanel();
        }
	}



	@Override
	public void onDestroy(){
		super.onDestroy();
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

        mContentObserver = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
//                LogUtil.i(TAG, "receive onChange");
                super.onChange(selfChange);
                if (mCategory == FileUtils.FILE_TYPE_FAVORITE && mCurrentChildFragment!=null) {
                    ((FileBrowserFragment)mCurrentChildFragment).setInitDirs( UiProvider.getFavoriteFiles() );
                }
            }
        };

        getActivity().getContentResolver().registerContentObserver(DataStructures.FavoriteColumns.CONTENT_URI, true, mContentObserver);
	}


    public void setCategory(int category) {
        mCategory = category;
        persistCategoryType(mCategory);
        showChildCategoryPanel();
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}


    public static CategoryTabFragment newCategoryTabFragment(int type) {
        CategoryTabFragment fragment = new CategoryTabFragment();
        fragment.persistCategoryType(type);
        return fragment;
    }

    private void persistCategoryType(int category) {
        Bundle data = getArguments();
        boolean first_inited = false;
        if (data == null) {
            data = new Bundle();
            first_inited = true;
        }
        data.putInt(CATEGORY_TYPE, category);
        if (first_inited) {
            setArguments(data);
        }
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
            setCategory(event.category);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mContentObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }

    @Override
	public boolean onBackPressed() {
        if ( super.onBackPressed() ){
            return true;
        }
        if (mCategoryFragment == null) {
            if (getActivity() instanceof FileManagerTabActivity) {
                showCategoryPanel();
                return true;
            }
        }

		return false;
	}

    public void showCategoryPanel() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        CategoryFragment fragment = (CategoryFragment) getChildFragmentManager().findFragmentByTag(CategoryFragment.class.getSimpleName());
        if (fragment == null) {
            Log.i(TAG, "create new CategoryFragment");
            fragment = new CategoryFragment();
            ft.replace(R.id.fragment_container, fragment, CategoryFragment.class.getSimpleName());
            ft.commit();
        } else {
            Log.i(TAG, "use old CategoryFragment");
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
                Log.i(TAG, "old CategoryFragment attach again");
            }
        }
        mCategory = FileUtils.FILE_TYPE_ALL;
        mCategoryFragment = fragment;
        mCurrentChildFragment = null;
	}


    private void toFileGrouper(int category) {
        Log.i(TAG, "toFileGrouper: " + category);
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileGrouperFragment fragment = (FileGrouperFragment) fm.findFragmentByTag(FileGrouperFragment.TAG);
        if(fragment == null) {
            fragment = FileGrouperFragment.newCategoryGrouperInstance(category);
            ft.replace(R.id.fragment_container, fragment, FileGrouperFragment.class.getSimpleName());
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                Log.i(TAG, "toFileGrouper attached again");
            } else {
                Log.i(TAG, "toFileGrouper just set category");
            }
            fragment.setCategory(category);
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentChildFragment = fragment;
        mCategoryFragment = null;
    }

    private void toFileBrowser(int type) {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(FileBrowserFragment.TAG);
        if (fragment == null) {
            if (FileUtils.FILE_TYPE_DOWNLOAD == type) {
                fragment = FileBrowserFragment.newDownloadBrowser();
            } else if (FileUtils.FILE_TYPE_FAVORITE == type) {
                fragment = FileBrowserFragment.newFavoriteBrowser();
            }

            ft.replace(R.id.fragment_container, fragment, FileBrowserFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        mCurrentChildFragment = fragment;
        mCategoryFragment = null;
    }

    private void toAppManager() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        AppManagerFragment fragment = (AppManagerFragment) fm.findFragmentByTag(AppManagerFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new AppManagerFragment();
            ft.replace(R.id.fragment_container, fragment, AppManagerFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
//        ft.addToBackStack(null);
        ft.commit();
        mCurrentChildFragment = fragment;
        mCategoryFragment = null;
    }

    private void toResourceFragment(int category) {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ResourceFragment fragment = (ResourceFragment) fm.findFragmentByTag(ResourceFragment.class.getSimpleName());
        if(fragment == null) {
            switch (category) {
                case FileUtils.FILE_TYPE_RESOURCE_GAME:
                    fragment = ResourceFragment.newGameResourceFragment();
                    break;
                case FileUtils.FILE_TYPE_RESOURCE_APP:
                    fragment = ResourceFragment.newAppResourceFragment();
                    break;
                case FileUtils.FILE_TYPE_RESOURCE_DOC:
                    fragment = ResourceFragment.newDocResourceFragment();
                    break;
                case FileUtils.FILE_TYPE_RESOURCE_ALL:
                default:
                    fragment = ResourceFragment.newAllResourceFragment();
                    break;
            }
            ft.replace(R.id.fragment_container, fragment, ResourceFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        mCurrentChildFragment = fragment;
        mCategoryFragment = null;
    }

//    @Override
//    public void onCategorySelected(int category) {
//        mCategory = category;
//        showChildCategoryPanel(category);
//    }

    private boolean showChildCategoryPanel() {
        Log.i(TAG, "showChildCategoryPanel: "+mCategory);
        boolean result = true;
        switch(mCategory) {
            case FileUtils.FILE_TYPE_ALL:
                showCategoryPanel();
                break;
            case FileUtils.FILE_TYPE_APP:
                toAppManager();
                break;
            case FileUtils.FILE_TYPE_FAVORITE:
                toFileBrowser(mCategory);
                break;
            case FileUtils.FILE_TYPE_DOWNLOAD:
                toFileBrowser(mCategory);
                break;
            case FileUtils.FILE_TYPE_RESOURCE_GAME:
            case FileUtils.FILE_TYPE_RESOURCE_APP:
            case FileUtils.FILE_TYPE_RESOURCE_DOC:
                toResourceFragment(mCategory);
                break;
            case FileUtils.FILE_TYPE_AUDIO:
            case FileUtils.FILE_TYPE_IMAGE:
            case FileUtils.FILE_TYPE_VIDEO:
            case FileUtils.FILE_TYPE_APK:
            case FileUtils.FILE_TYPE_DOCUMENT:
            case FileUtils.FILE_TYPE_ZIP:
                toFileGrouper(mCategory);
                break;
            case FileUtils.FILE_TYPE_CLOUD:
                Activity activity = getActivity();
                if(activity instanceof FileManagerTabActivity) {
                    ((FileManagerTabActivity)activity).gotoCloud();
                }
                break;
            default:
                result = false;
                break;
        }
        return result;
    }

    @Override
    protected void showFile(String path) {
        if (new File(path).isDirectory() && mCurrentChildFragment !=null && mCurrentChildFragment instanceof FileBrowserFragment) {
            ((FileBrowserFragment)mCurrentChildFragment).showDir(path);
        }
    }

    @Override
    protected void closeFile(String path) {
        return;
    }

    @Override
    public void refreshFiles() {
        if(mCurrentChildFragment != null) {
            mCurrentChildFragment.refreshUI();
        }
//        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public String[] getAllFiles() {
        if(mCurrentChildFragment != null) {
            return mCurrentChildFragment.getAllFiles();
        } else {
            return null;
        }
    }

    @Override
    public String getParentFile() {
        if(mCurrentChildFragment != null) {
            return mCurrentChildFragment.getParentFile();
        } else {
            return null;
        }
    }

//    @Override
//    public void onBackStackChanged() {
//       mCurrentChildFragment = (SherlockFragment)getChildFragmentManager().findFragmentById(R.id.fragment_container);
//       if (mCurrentChildFragment instanceof CategoryFragment) {
//            ((CategoryFragment)mCurrentChildFragment).setListener(this);
//       }
//    }


    @Override
    public void onFileBrowserDirShown(String path) {

    }

}
