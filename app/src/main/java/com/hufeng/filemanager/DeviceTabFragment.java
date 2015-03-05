package com.hufeng.filemanager;

import android.os.Bundle;
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

import java.io.File;


public class DeviceTabFragment extends FileTabFragment {

    private static final String LOG_TAG = "DirectoryTabFragment";

    private String mDevicePath;
    private NewDeviceFragment mDeviceFragment;

    private static final String DEVICE_TAB_ROOT_DIR = "device_tab_root_dir";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDevicePath = savedInstanceState.getString(DEVICE_TAB_ROOT_DIR);
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
    public void onDeviceSelected(DeviceSelectEvent event) {
        if (event != null) {
            mDevicePath = event.path;
            showFileBrowserFragment();
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
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private void showSingleDevicePanel() {
        if (TextUtils.isEmpty(mDevicePath) || !new File(mDevicePath).isDirectory()) {
            showDevicePanel();
        } else {
            showFileBrowserFragment();
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
            fragment = FileBrowserFragment.newRootFolderBrowser(mDevicePath);
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

	@Override
	public boolean onBackPressed() {
        if ( super.onBackPressed() ){
            return true;
        }
        if (mDeviceFragment == null) {
            showDevicePanel();
            return true;
        }

        return false;
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
    public FileEntry[] getAllFiles() {
        if (mCurrentChildFragment != null)
            return mCurrentChildFragment.getAllFiles();
        else
            return null;
    }

}
