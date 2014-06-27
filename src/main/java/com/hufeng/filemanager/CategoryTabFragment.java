package com.hufeng.filemanager;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.provider.UiProvider;

import java.io.File;


public class CategoryTabFragment extends FileTabFragment implements
        CategoryFragment.CategoryFragmentListener {

	private static final String CATEGORY_TYPE = "category_type";
    private int mCategory = FileUtils.FILE_TYPE_ALL;
    private Fragment mCategoryFragment;
    ContentObserver mContentObserver;

    @Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        outState.putInt(CATEGORY_TYPE, mCategory);
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
//        getChildFragmentManager().addOnBackStackChangedListener(this);
//        showCategoryPanel();
        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getInt(CATEGORY_TYPE, FileUtils.FILE_TYPE_ALL);
        }
        showChildCategoryPanel(mCategory);

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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onPause(){
		super.onPause();
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
            showCategoryPanel();
            return true;
        }

		return false;
	}

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        if (mCategoryFragment != null) {
//            mCategoryFragment.onCreateOptionsMenu(menu, inflater);
//        } else{
//            super.onCreateOptionsMenu(menu, inflater);
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mCategoryFragment != null) {
//            return mCategoryFragment.onOptionsItemSelected(item);
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onDestroyOptionsMenu() {
//        if (mCategoryFragment != null) {
//            mCategoryFragment.onDestroyOptionsMenu();
//        } else {
//            super.onDestroyOptionsMenu();
//        }
//    }

    public void showCategoryPanel() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        CategoryFragment fragment = (CategoryFragment) getChildFragmentManager().findFragmentByTag(CategoryFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new CategoryFragment();
            ft.replace(R.id.fragment_container, fragment, CategoryFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        fragment.setListener(this);
        mCategory = FileUtils.FILE_TYPE_ALL;
        mCategoryFragment = fragment;
        mCurrentChildFragment = null;
	}


    private void toFileGrouper(int category) {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileGrouperFragment fragment = (FileGrouperFragment) fm.findFragmentByTag(FileGrouperFragment.class.getSimpleName());
        if(fragment == null) {
            fragment = FileGrouperFragment.newCategoryGrouperInstance(category);
            ft.replace(R.id.fragment_container, fragment, FileGrouperFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
//        ft.addToBackStack(null);
        ft.commit();
        fragment.setListener(this);
        mCurrentChildFragment = fragment;
        mCategoryFragment = null;
    }

    private void toFileBrowser(int type) {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(FileBrowserFragment.class.getSimpleName());
        if (fragment == null) {
            if (FileUtils.FILE_TYPE_DOWNLOAD == type) {
                fragment = FileBrowserFragment.newDownloadBrowser(null);
            } else if (FileUtils.FILE_TYPE_FAVORITE == type) {
                fragment = FileBrowserFragment.newFavoriteBrowser(null);
            }

            ft.replace(R.id.fragment_container, fragment, FileBrowserFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        fragment.setListener(this);
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
            fragment = new ResourceFragment();
            Bundle data = new Bundle();
            if (FileUtils.FILE_TYPE_RESOURCE_GAME == category) {
                data.putInt(ResourceFragment.RESOURCE_FRAGMENT_ARGUMENT_TYPE, ResourceType.GAME.ordinal());
            } else if (FileUtils.FILE_TYPE_RESOURCE_APP == category) {
                data.putInt(ResourceFragment.RESOURCE_FRAGMENT_ARGUMENT_TYPE, ResourceType.APP.ordinal());
            } else if (FileUtils.FILE_TYPE_RESOURCE_DOC == category) {
                data.putInt(ResourceFragment.RESOURCE_FRAGMENT_ARGUMENT_TYPE, ResourceType.DOC.ordinal());
            }
            fragment.setArguments(data);
            ft.replace(R.id.fragment_container, fragment, ResourceFragment.class.getSimpleName());
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

    @Override
    public void onCategorySelected(int category) {
        mCategory = category;
        showChildCategoryPanel(category);
    }

    private boolean showChildCategoryPanel(int category) {
        boolean result = true;
        switch(category) {
            case FileUtils.FILE_TYPE_ALL:
                showCategoryPanel();
                break;
            case FileUtils.FILE_TYPE_APP:
                toAppManager();
                break;
            case FileUtils.FILE_TYPE_FAVORITE:
                toFileBrowser(category);
                break;
            case FileUtils.FILE_TYPE_DOWNLOAD:
                toFileBrowser(category);
                break;
            case FileUtils.FILE_TYPE_RESOURCE_GAME:
            case FileUtils.FILE_TYPE_RESOURCE_APP:
            case FileUtils.FILE_TYPE_RESOURCE_DOC:
                toResourceFragment(category);
                break;
            case FileUtils.FILE_TYPE_AUDIO:
            case FileUtils.FILE_TYPE_IMAGE:
            case FileUtils.FILE_TYPE_VIDEO:
            case FileUtils.FILE_TYPE_APK:
            case FileUtils.FILE_TYPE_DOCUMENT:
            case FileUtils.FILE_TYPE_ZIP:
                toFileGrouper(category);
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
