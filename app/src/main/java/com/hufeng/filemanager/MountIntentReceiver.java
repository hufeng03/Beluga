package com.hufeng.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.Loader;

/**
 * Created by feng on 13-9-6.
 */
public class MountIntentReceiver extends BroadcastReceiver {

    final Loader mLoader;

    public MountIntentReceiver(Loader loader) {
        mLoader = loader;
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        filter.addDataScheme("file");
        mLoader.getContext().registerReceiver(this, filter);
    }

    @Override public void onReceive(Context context, Intent intent) {
        // Tell the loader about the change.
        mLoader.onContentChanged();
    }
}

