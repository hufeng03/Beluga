package com.hufeng.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;


public class DeviceTabFragment extends FileTabFragment /*implements FileTreeFragment.FileTreeFragmentListener*/{

    private static final String LOG_TAG = "DirectoryTabFragment";

    private String mDevicePath;
    private DeviceSelectionFragment mDeviceSelectionFragment;

    private LinearLayout mAdLayout;

    private final String DEVICE_TAB_ROOT_DIR = "device_tab_root_dir";

    BroadcastReceiver mReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

    BroadcastReceiver mEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == DeviceSelectEvent.INTENT_ACTION) {
                DeviceSelectEvent event = new DeviceSelectEvent(intent);
                onDeviceSelected(event);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mEventReceiver, new IntentFilter(DeviceSelectEvent.INTENT_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mEventReceiver);
    }

    public void onDeviceSelected(DeviceSelectEvent event) {
        if (event != null) {
            mDevicePath = event.path;
            showSingleDevicePanel(mDevicePath);
        }
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

        mAdLayout = (LinearLayout)view.findViewById(R.id.device_ad_layout);
        mAdLayout.setVisibility(View.GONE);

        if (savedInstanceState != null) {
            mDevicePath = savedInstanceState.getString(DEVICE_TAB_ROOT_DIR);
        }
        showSingleDevicePanel(mDevicePath);
//        if(Configuration.ORIENTATION_LANDSCAPE == orientation) {
//            showFileTree(dir);
//        }
//        showFileBrowser(mDevicePath);

    }
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		refreshView();
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       if (mCurrentChildFragment != null) {
           String dir = mCurrentChildFragment.getParentFile();
           if (!TextUtils.isEmpty(dir)) {
               outState.putString(DEVICE_TAB_ROOT_DIR, dir);
           }
       }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void showSingleDevicePanel(String device_path) {
        if (TextUtils.isEmpty(device_path) || !new File(device_path).isDirectory()) {
            showDevicePanel();
        } else {
//            if(Configuration.ORIENTATION_LANDSCAPE == orientation) {
//                showFileTree(device_path);
//            }
            showStorageBrowser(device_path);
        }
    }


    public void showDevicePanel() {

        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        DeviceSelectionFragment fragment = (DeviceSelectionFragment) getChildFragmentManager().findFragmentByTag(DeviceSelectionFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new DeviceSelectionFragment();
            ft.replace(R.id.fragment_container, fragment, DeviceSelectionFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
        mDeviceSelectionFragment = fragment;
        mCurrentChildFragment = null;

    }



//    public void showFileTree(String dir) {
//        LogUtil.i(LOG_TAG, "showFileTree:"+((dir!=null)?dir:"null"));
//        String parent_dir = null;
//        if(!TextUtils.isEmpty(dir)) {
//            FileEntry entry = new FileEntry(dir);
//            if (entry != null) {
//                parent_dir = entry.getParentPath();
//            }
//        }
//        final FragmentManager fm = getChildFragmentManager();
//        final FragmentTransaction ft = fm.beginTransaction();
//        mFileTreeFragment = (FileTreeFragment) fm.findFragmentByTag(FileTreeFragment.class.getSimpleName());
//        if (mFileTreeFragment == null) {
//            LogUtil.i(LOG_TAG, "showFileTree: create new");
//            mFileTreeFragment = FileTreeFragment.newStorageBrowser(dir);
//            ft.replace(R.id.fragment_container_tree, mFileTreeFragment, FileTreeFragment.class.getSimpleName());
//        } else {
//            if (mFileTreeFragment.isDetached()) {
//                LogUtil.i(LOG_TAG, "showFileTree: attach old");
//                ft.attach(mFileTreeFragment);
//            }
//            if (!TextUtils.isEmpty(dir)) {
//                mFileTreeFragment.showDir(dir);
//            }
//        }
//        mFileTreeFragment.setListener(this);
//        ft.commit();
//    }


    public void showStorageBrowser(String dir) {
        LogUtil.i(LOG_TAG, "showFileBrowser:"+((dir!=null)?dir:"null"));
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(FileBrowserFragment.class.getSimpleName());
        if (fragment == null) {
            LogUtil.i(LOG_TAG, "showFileBrowser: create new");
            fragment = FileBrowserFragment.newStorageBrowser(dir);
//            if (mFileTreeFragment != null) {
//                mFileBrowserFragment.workWithTree(true);
//            } else {
            fragment.workWithTree(false);
//            }
            ft.replace(R.id.fragment_container, fragment, FileBrowserFragment.class.getSimpleName());
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
//            if (!TextUtils.isEmpty(dir)) {
//                mFileBrowserFragment.showDir(dir);
//            }
        }
        fragment.setListener(this);
        ft.commit();
        mCurrentChildFragment = fragment;
        mDeviceSelectionFragment = null;
    }

	@Override
	public boolean onBackPressed() {
        if ( super.onBackPressed() ){
            return true;
        }
        if (mDeviceSelectionFragment == null) {
            showDevicePanel();
            return true;
        }

        return false;
	}


    @Override
    public void onFileBrowserItemSelect(View v, FileEntry entry) {
        selectFile((ImageView) v, entry);
    }


//    @Override
//    public void onFileTreeItemClick(FileEntry entry, boolean close) {
//        if (entry.isDirectory()) {
//            if (mFileBrowserFragment != null) {
//                if (!close) {
//                    mFileBrowserFragment.showDir(entry.path);
//                } else {
//                    String parent = new File(entry.path).getParent();
//                    if (TextUtils.isEmpty(parent)) parent = null;
//                    mFileBrowserFragment.showDir(parent);
//                }
//            }
//        }
//    }

    private void refreshAd(String path) {
        if (path != null) {
            boolean can_write = new File(path).canWrite();
            boolean can_read = new File(path).canRead();
            if (can_write && Constants.TRY_TO_TEST_WRITE) {
                if(new File(path, ".test_writable").mkdir()){
                    new File(path, ".test_writable").delete();
                } else {
                    can_write = false;
                }
            }
            if (!can_write && !can_read) {
                ((TextView)mAdLayout.findViewById(R.id.device_ad_tip)).setText(R.string.dir_not_write_nor_read);
                mAdLayout.setVisibility(View.VISIBLE);
            } else if (!can_write) {
                ((TextView)mAdLayout.findViewById(R.id.device_ad_tip)).setText(R.string.dir_not_write);
                mAdLayout.setVisibility(View.VISIBLE);
            } else {
                mAdLayout.setVisibility(View.GONE);
            }
        } else {
            mAdLayout.setVisibility(View.GONE);
        }
    }


    @Override
    public void refreshFiles() {
        if (mCurrentChildFragment != null)
            mCurrentChildFragment.refreshUI();
    }

    @Override
    public String[] getAllFiles() {
        if (mCurrentChildFragment != null)
            return mCurrentChildFragment.getAllFiles();
        else
            return null;
    }

    @Override
    public String getParentFile() {
        if (mCurrentChildFragment != null)
            return mCurrentChildFragment.getParentFile();
        else
            return null;
    }

}
