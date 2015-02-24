package com.hufeng.filemanager;

import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.hufeng.filemanager.app.AppEntry;
import com.hufeng.filemanager.app.PackageIntentReceiver;
import com.hufeng.filemanager.storage.StorageUnit;
import com.hufeng.filemanager.storage.StorageUtil;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by feng on 2014-10-26.
 */
public class DeviceLoader extends AsyncTaskLoader<List<StorageUnit>> {

    private static final String LOG_TAG = "DeviceLoader";
    List<StorageUnit> mDevices;

    MountIntentReceiver mDeviceObserver;

    /**
     * Stores away the application context associated with context. Since Loaders can be used
     * across multiple activities it's dangerous to store the context directly.
     *
     * @param context used to retrieve the application context.
     */
    public DeviceLoader(Context context) {
        super(context);
    }

    @Override
    public List<StorageUnit> loadInBackground() {

        List<StorageUnit> storageUnits = com.hufeng.filemanager.utils.StorageUtil.getMountedStorageUnits(getContext());

        int internal_count = 0;
        int external_count = 0;
        for (StorageUnit unit : storageUnits) {
            if (unit.isRemovable()) {
                external_count++;
            } else {
                internal_count++;
            }
        }
        for (StorageUnit unit : storageUnits) {
            if (unit.description == null) {
                if (unit.isRemovable()) {
                    if (external_count <= 1) {
                        unit.description = getContext().getString(R.string.external_storage);
                    } else {
                        unit.description = getContext().getString(R.string.external_storage)+ " [1]";
                    }
                } else {
                    if (internal_count <= 1) {
                        unit.description = getContext().getString(R.string.internal_storage);
                    } else {
                        unit.description = getContext().getString(R.string.internal_storage)+ " [1]";
                    }
                }
            }
        }


        if(storageUnits.size()>0) {
            Collections.sort(storageUnits, new Comparator<StorageUnit>() {
                @Override
                public int compare(StorageUnit lhs, StorageUnit rhs) {
                    return lhs.path.compareTo(rhs.path);
                }
            });
            int idx = 0;
            for(StorageUnit storage:storageUnits) {
                LogUtil.i(LOG_TAG, "storage unit " + idx + ":" + storage.toString());
                idx ++;
            }
        } else {
            LogUtil.i(LOG_TAG, "opps, no storage unit");
        }

        return storageUnits;
    }


    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<StorageUnit> devices) {

        List<StorageUnit> oldDevices = devices;
        mDevices = devices;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            if (isReset()) {
                if (mDevices != null) {
                    onReleaseResources(devices);
                    return;
                }
            }

            super.deliverResult(devices);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldDevices != null && oldDevices!=devices) {
            onReleaseResources(oldDevices);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mDevices != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mDevices);
        }

        // Start watching for changes in the app data.
        if (mDeviceObserver == null) {
            mDeviceObserver = new MountIntentReceiver(this);
        }

        // Has something interesting in the configuration changed since we
        // last built the app list?
//        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || mDevices == null/* || configChange*/) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<StorageUnit> devices) {
        super.onCanceled(devices);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(devices);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mDevices != null) {
            onReleaseResources(mDevices);
            mDevices = null;
        }

        // Stop monitoring for changes.
        if (mDeviceObserver != null) {
            getContext().unregisterReceiver(mDeviceObserver);
            mDeviceObserver = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<StorageUnit> mDevices) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

}
