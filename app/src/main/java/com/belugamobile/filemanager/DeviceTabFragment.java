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

import java.io.File;


public class DeviceTabFragment extends FileTabFragment {

    private static final String LOG_TAG = "DirectoryTabFragment";

    private String mDevicePath;
    private NewDeviceFragment mDeviceFragment;

    private String mZipPath;

    private static final String DEVICE_TAB_ROOT_DIR = "device_tab_root_dir";
    private static final String ZIP_FILE_PATH = "zip_file_path";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDevicePath = savedInstanceState.getString(DEVICE_TAB_ROOT_DIR);
            mZipPath = savedInstanceState.getString(ZIP_FILE_PATH);
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
    public void onDeviceSelected(DeviceSelectEvent event) {
        if (getUserVisibleHint() && event != null) {
            mDevicePath = event.path;
            showFileBrowserFragment();
        }
    }

    @Subscribe
    public void onZipView(ZipViewEvent event) {
        if (getUserVisibleHint() && event != null) {
            mZipPath = event.path;
            showZipBrowserFragment();
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
        showSingleDevicePanel();
    }
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mDevicePath)) {
            outState.putString(DEVICE_TAB_ROOT_DIR, mDevicePath);
        }
        if (!TextUtils.isEmpty(mZipPath)) {
            outState.putString(ZIP_FILE_PATH, mZipPath);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void showSingleDevicePanel() {
        if (!TextUtils.isEmpty(mZipPath) && new File(mZipPath).exists()) {
            showZipBrowserFragment();
        } else if (!TextUtils.isEmpty(mDevicePath) && new File(mDevicePath).exists()) {
            showFileBrowserFragment();
        } else {
            showDevicePanel();
        }
    }


    private void showDevicePanel() {

        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String TAG = "DeviceFragment";
        NewDeviceFragment fragment = (NewDeviceFragment) getChildFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new NewDeviceFragment();
            ft.replace(R.id.fragment_container, fragment, TAG);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mDeviceFragment = fragment;
        mCurrentChildFragment = null;
        mDevicePath = null;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("Device Panel");
        t.send(new HitBuilders.AppViewBuilder().build());
    }


    public void showFileBrowserFragment() {
        final FragmentManager fm = getChildFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String TAG = "FileBrowserFragment";
        FileBrowserFragment fragment = (FileBrowserFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = FileBrowserFragment.newRootFolderBrowser(mDevicePath, mZipPath);
            ft.replace(R.id.fragment_container, fragment, TAG);
            ft.commit();
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
                ft.commit();
            }
        }
        mCurrentChildFragment = fragment;
        mDeviceFragment = null;

        Tracker t = ((FileManager)getActivity().getApplication()).getTracker(FileManager.TrackerName.APP_TRACKER);
        t.setScreenName("File Browser: "+mDevicePath);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void showZipBrowserFragment() {
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
        mDeviceFragment = null;

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
        } else if (!TextUtils.isEmpty(mDevicePath)) {
            mDevicePath = null;
        } else {
            return false;
        }
        showSingleDevicePanel();

        return true;
	}

    @Override
    protected void showInitialState() {
        super.showInitialState();
        showDevicePanel();
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
