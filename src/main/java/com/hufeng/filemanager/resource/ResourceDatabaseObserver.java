package com.hufeng.filemanager.resource;

import android.database.ContentObserver;
import android.os.Handler;

import com.hufeng.filemanager.data.DataStructures;

/**
 * Created by feng on 13-9-28.
 */
public class ResourceDatabaseObserver extends ContentObserver{

    final ResourceListLoader mLoader;

    public ResourceDatabaseObserver(ResourceListLoader loader, Handler handler) {
        super(handler);
        mLoader = loader;
//        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
//        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
//        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
//        filter.addDataScheme("package");
//        mLoader.getContext().registerReceiver(this, filter);
//        // Register for events related to sdcard installation.
//        IntentFilter sdFilter = new IntentFilter();
//        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
//        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
//        mLoader.getContext().registerReceiver(this, sdFilter);

        mLoader.getContext().getContentResolver().registerContentObserver(DataStructures.SelectedColumns.CONTENT_URI, false, this);


    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        mLoader.onContentChanged();
    }

}
