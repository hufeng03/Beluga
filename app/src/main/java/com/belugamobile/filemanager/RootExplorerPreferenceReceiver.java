package com.belugamobile.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;

/**
 * Created by feng on 13-9-6.
 */
public class RootExplorerPreferenceReceiver implements SharedPreferences.OnSharedPreferenceChangeListener {

    final Loader mLoader;
    final SharedPreferences mSharedPreference;

    public RootExplorerPreferenceReceiver(Loader loader) {
        mLoader = loader;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(loader.getContext());
        mSharedPreference.registerOnSharedPreferenceChangeListener(this);
    }

    public void dismiss(Context context) {
        mSharedPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceKeys.ROOT_EXPLORER_ENABLE)) {
            mLoader.onContentChanged();
        }
    }
}
