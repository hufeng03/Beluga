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
import com.squareup.otto.Subscribe;


public class DeviceTabFragment extends FileTabFragment {

    private static final String LOG_TAG = "DirectoryTabFragment";

    private NewDeviceFragment mDeviceFragment;

    private String mDevicePath;
    private String mFolderPath;
    private String mZipPath;

    private static final String DEVICE_PATH = "device_tab_root_dir";
    private static final String ZIP_FILE_PATH = "zip_file_path";
    private static final String FOLDER_PATH = "folder_path";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDevicePath = savedInstanceState.getString(DEVICE_PATH);
            mFolderPath = savedInstanceState.getString(FOLDER_PATH);
            mZipPath = savedInstanceState.getString(ZIP_FILE_PATH);
        }
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.device_tab_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeviceFragment = (NewDeviceFragment)getChildFragmentManager().findFragmentById(R.id.device_fragment);
        refreshDeviceDetailPane();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mDevicePath)) {
            outState.putString(DEVICE_PATH, mDevicePath);
        }
        if (!TextUtils.isEmpty(mFolderPath)) {
            outState.putString(FOLDER_PATH, mFolderPath);
        }
        if (!TextUtils.isEmpty(mZipPath)) {
            outState.putString(ZIP_FILE_PATH, mZipPath);
        }
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

    @Subscribe
    public void onTabLongPress(TabLongPressEvent event) {
        if (getUserVisibleHint() && event != null) {
            if (event.name.equalsIgnoreCase(getString(R.string.tab_device))) {
                Toast.makeText(getActivity(), "test_device", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Subscribe
    public void onDeviceSeleted(DeviceSelectEvent event) {
        if (getUserVisibleHint() && event != null) {
            mDevicePath = event.path;
            mFolderPath = event.path;
            mZipPath = null;
            refreshDeviceDetailPane();
        }
    }

    @Subscribe
    public void onFolderSelected(FolderSelectEvent event) {
        if (getUserVisibleHint() && event != null) {
            if (!event.root.equals(mDevicePath)) {
                mDevicePath = event.root;
            }
            mZipPath = null;
            mFolderPath = event.path;
            refreshDeviceDetailPane();
        }
    }

    @Subscribe
    public void onZipSelected(ZipSelectEvent event) {
        if (getUserVisibleHint() && event != null) {
            mZipPath = event.path;
            showZipBrowser();
        }
    }

    @Subscribe
    public void onFolderShown(FolderShowEvent event) {
        if (getUserVisibleHint() && event != null) {
            if (!event.root.equals(mDevicePath)) {
                mDevicePath = event.root;
            }
            mZipPath = null;
            mFolderPath = event.path;
            mDeviceFragment.setSelectedDeviceAndFolder(mDevicePath, mFolderPath);
        }
    }


    private void refreshDeviceDetailPane() {
        if (!TextUtils.isEmpty(mZipPath)) {
            showZipBrowser();
        } else if (!TextUtils.isEmpty(mDevicePath) || !TextUtils.isEmpty(mFolderPath)) {
            showFileBrowser();
        } else {
            showEmptyDetail();
        }
        if (mDeviceFragment != null) {
            mDeviceFragment.setSelectedDeviceAndFolder(mDevicePath, mFolderPath);
        }
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

    public void showFileBrowser() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String TAG = "FileBrowserFragment";
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = FileBrowserFragment.newRootFolderBrowser(mDevicePath, mFolderPath);
            ft.replace(R.id.fragment_container, fragment, TAG);
            ft.commit();
        } else {
            fragment.setRootAndCurrentFolder(mDevicePath, mFolderPath);
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentChildFragment = fragment;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("File Browser: "+mDevicePath);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void showZipBrowser() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String TAG = "ZipBrowserFragment";
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

	@Override
	public boolean onBackPressed() {
        if ( super.onBackPressed() ){
            return true;
        }
        if (!TextUtils.isEmpty(mZipPath)) {
            mZipPath = null;
        } else if (!TextUtils.isEmpty(mDevicePath) || !TextUtils.isEmpty(mFolderPath)) {
            mDevicePath = null;
            mFolderPath = null;
        } else {
            return false;
        }
        refreshDeviceDetailPane();

        return true;
	}

    @Override
    protected void showInitialState() {
        super.showInitialState();
        mZipPath = null;
        mDevicePath = null;
        mFolderPath = null;
        refreshDeviceDetailPane();
    }

    @Override
    public void refreshUI() {
        if (mCurrentChildFragment != null)
            mCurrentChildFragment.refreshUI();
    }

    @Override
    public BelugaFileEntry[] getAllFiles() {
        if (mCurrentChildFragment != null)
            return mCurrentChildFragment.getAllFiles();
        else
            return null;
    }

}
