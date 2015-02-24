package com.hufeng.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.Loader;

import com.hufeng.filemanager.browser.BelugaSorter;

/**
 * Created by feng on 13-9-6.
 */
public class SortPreferenceReceiver implements SharedPreferences.OnSharedPreferenceChangeListener {

    final Loader mLoader;
    final SharedPreferences mSharedPreference;
    final CategorySelectEvent.CategoryType mCategoryType;
    BelugaSorter.SORTER mSorter;

    public SortPreferenceReceiver(Loader loader, CategorySelectEvent.CategoryType categoryType) {
        mLoader = loader;
        mCategoryType = categoryType;
        mSorter = BelugaSorter.getFileSorter(loader.getContext(), categoryType);
        mSharedPreference = loader.getContext().getSharedPreferences(BelugaSorter.SORT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mSharedPreference.registerOnSharedPreferenceChangeListener(this);
    }

    public void dismiss(Context context) {
        mSharedPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        BelugaSorter.SORTER sorter = BelugaSorter.getFileSorter(mLoader.getContext(), mCategoryType);
        if (!sorter.equals(mSorter)) {
            mSorter = sorter;
            mLoader.onContentChanged();
        }
    }
}
