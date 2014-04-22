package com.hufeng.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.provider.UiProvider;

import java.io.File;


public class DirectoryTabFragment extends FileTabFragment implements
        FileTreeFragment.FileTreeFragmentListener{

    private static final String LOG_TAG = DirectoryTabFragment.class.getSimpleName();

    private FileBrowserFragment mFileBrowserFragment;
    private FileTreeFragment mFileTreeFragment;

    private final String DEVICE_TAB_ROOT_DIR = "device_tab_root_dir";

    BroadcastReceiver mReceiver;

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.directory_tab_fragment, container, false);
        return view;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int orientation = getResources().getConfiguration().orientation;

        String dir = null;
        if (savedInstanceState != null) {
            dir = savedInstanceState.getString(DEVICE_TAB_ROOT_DIR);
        }

        if(Configuration.ORIENTATION_LANDSCAPE == orientation) {
            showFileTree(dir);
        }
        showFileBrowser(dir);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(LOG_TAG, "mReceiver receive" + intent.getAction());
                String[] files = UiProvider.getStorageDirs();
                if (mFileBrowserFragment != null) {
                    mFileBrowserFragment.setInitDirs(files);
                }
                if (mFileTreeFragment != null) {
                    mFileTreeFragment.setInitDirs(files);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("SHOW_ROOT_FILES_ACTION");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, filter);
    }
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		refreshView();
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       if (mFileBrowserFragment != null) {
           String dir = mFileBrowserFragment.getParentFile();
           if (!TextUtils.isEmpty(dir)) {
               outState.putString(DEVICE_TAB_ROOT_DIR, dir);
           }
       }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        }
    }

    public void showFileTree(String dir) {
        Log.i(LOG_TAG, "showFileTree:"+((dir!=null)?dir:"null"));
        String parent_dir = null;
        if(!TextUtils.isEmpty(dir)) {
            FileEntry entry = new FileEntry(dir);
            if (entry != null) {
                parent_dir = entry.getParentPath();
            }
        }
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        mFileTreeFragment = (FileTreeFragment) fm.findFragmentByTag(FileTreeFragment.class.getSimpleName());
        if (mFileTreeFragment == null) {
            Log.i(LOG_TAG, "showFileTree: create new");
//            mFileTreeFragment = new FileTreeFragment();
//            Bundle data = new Bundle();
//            if (!TextUtils.isEmpty(dir)) {
//                data.putString(FileTreeFragment.ARGUMENT_INIT_ROOT_DIR, dir);
//            }
//            mFileTreeFragment.setArguments(data);
            mFileTreeFragment = FileTreeFragment.newStorageBrowser(dir);
            ft.replace(R.id.fragment_container_tree, mFileTreeFragment, FileTreeFragment.class.getSimpleName());
        } else {
            if (mFileTreeFragment.isDetached()) {
                Log.i(LOG_TAG, "showFileTree: attach old");
                ft.attach(mFileTreeFragment);
            }
            if (!TextUtils.isEmpty(dir)) {
                mFileTreeFragment.showDir(dir);
            }
        }
        mFileTreeFragment.setListener(this);
        ft.commit();
    }


    public void showFileBrowser(String dir) {
        Log.i(LOG_TAG, "showFileBrowser:"+((dir!=null)?dir:"null"));
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        mFileBrowserFragment = (FileBrowserFragment) fm.findFragmentByTag(FileBrowserFragment.class.getSimpleName());
        if (mFileBrowserFragment == null) {
            Log.i(LOG_TAG, "showFileBrowser: create new");
            mFileBrowserFragment = FileBrowserFragment.newStorageBrowser(dir);
            if (mFileTreeFragment != null) {
                mFileBrowserFragment.workWithTree(true);
            } else {
                mFileBrowserFragment.workWithTree(false);
            }
            ft.replace(R.id.fragment_container, mFileBrowserFragment, FileBrowserFragment.class.getSimpleName());
        } else {
            if (mFileBrowserFragment.isDetached()) {
                Log.i(LOG_TAG, "showFileBrowser: attach old");
                ft.attach(mFileBrowserFragment);
            }
            if (!TextUtils.isEmpty(dir)) {
                mFileBrowserFragment.showDir(dir);
            }
        }
        mFileBrowserFragment.setListener(this);
        ft.commit();
        mCurrentChildFragment = mFileBrowserFragment;
    }

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
//		if(mFileBrowser!=null)
//			return mFileBrowser.back();
        if (closeImage()) {
            return true;
        } else {
           return mFileBrowserFragment.onBackPressed();
        }
//		return false;
	}

    public void showBrowserRoot() {
        mFileBrowserFragment.showDir(null);
    }

    @Override
    public void onFileBrowserItemClose(FileEntry entry) {
        if (entry.isDirectory()) {
            if (mFileTreeFragment != null) {
                mFileTreeFragment.closeDir(entry.path);
            }
        } else {
            //do nothing
        }
    }

    @Override
    public void onFileBrowserItemSelect(View v, FileEntry entry) {
        selectFile((ImageView) v, entry);
    }

    @Override
    public void onFileTreeItemClick(FileEntry entry, boolean close) {
        if (entry.isDirectory()) {
            if (mFileBrowserFragment != null) {
                if (!close) {
                    mFileBrowserFragment.showDir(entry.path);
                } else {
                    String parent = new File(entry.path).getParent();
                    if (TextUtils.isEmpty(parent)) parent = null;
                    mFileBrowserFragment.showDir(parent);
                }
            }
        }
    }

    @Override
    protected void showFile(String path) {
        if (new File(path).isDirectory()) {
            if (mFileBrowserFragment != null)
                mFileBrowserFragment.showDir(path);
            if (mFileTreeFragment != null)
                mFileTreeFragment.showDir(path);
        } else {
            if (mFileTreeFragment != null)
                mFileTreeFragment.showDir(new File(path).getParent());
        }
    }

    @Override
    protected void closeFile(String path) {
        if (new File(path).isDirectory()) {
            if (mFileTreeFragment != null) {
                mFileTreeFragment.closeDir(path);
            }
        } else {
            //do nothing
        }
    }

    @Override
    public void refreshFiles() {
        mFileBrowserFragment.refreshUI();
        if (getActivity() != null) {
//            getActivity().supportInvalidateOptionsMenu();
        }
    }

    @Override
    public String[] getAllFiles() {
        return mFileBrowserFragment.getAllFiles();
    }

    @Override
    public String getParentFile() {
        return mFileBrowserFragment.getParentFile();
    }

}
