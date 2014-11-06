package com.hufeng.filemanager;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ListView;

import com.hufeng.filemanager.storage.StorageUnit;

import java.util.List;

/**
 * Created by feng on 2014-10-26.
 */
public class DeviceSelectionFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<StorageUnit>>{

    private static final String LOG_TAG = DeviceSelectionFragment.class.getSimpleName();

    private static final int LOADER_ID_DEVICE = 2;

    private DeviceAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new DeviceAdapter(getActivity());
        setListAdapter(mAdapter);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getLoaderManager().getLoader(LOADER_ID_DEVICE) != null) {
            getLoaderManager().restartLoader(LOADER_ID_DEVICE, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_ID_DEVICE, null, this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        StorageUnit storage = mAdapter.getItem(position);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new DeviceSelectEvent(System.currentTimeMillis(), storage.path).buildIntentWithBundle());
    }

    @Override
    public Loader<List<StorageUnit>> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID_DEVICE) {
            return new DeviceLoader(getActivity());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<StorageUnit>> loader, List<StorageUnit> data) {
        mAdapter.clear();
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<StorageUnit>> loader) {

    }
}
