package com.belugamobile.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.Loader;

import com.belugamobile.filemanager.helper.BelugaSortHelper;

/**
 * Created by feng on 13-9-6.
 */
public class DisplayHiddenPreferenceReceiver implements SharedPreferences.OnSharedPreferenceChangeListener {

    final Loader mLoader;
    final SharedPreferences mSharedPreference;

    public DisplayHiddenPreferenceReceiver(Loader loader) {
        mLoader = loader;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(loader.getContext());
        mSharedPreference.registerOnSharedPreferenceChangeListener(this);
    }

    public void dismiss(Context context) {
        mSharedPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceKeys.DISPLAY_HIDDEN_ENABLE)) {
            mLoader.onContentChanged();
        }
    }
}
