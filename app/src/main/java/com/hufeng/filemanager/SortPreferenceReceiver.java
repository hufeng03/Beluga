package com.hufeng.filemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.Loader;

import com.hufeng.filemanager.helper.BelugaSortHelper;

/**
 * Created by feng on 13-9-6.
 */
public class SortPreferenceReceiver implements SharedPreferences.OnSharedPreferenceChangeListener {

    final Loader mLoader;
    final SharedPreferences mSharedPreference;
    final CategorySelectEvent.CategoryType mCategoryType;
    BelugaSortHelper.SORTER mSorter;

    public SortPreferenceReceiver(Loader loader, CategorySelectEvent.CategoryType categoryType) {
        mLoader = loader;
        mCategoryType = categoryType;
        mSorter = BelugaSortHelper.getFileSorter(loader.getContext(), categoryType);
        mSharedPreference = loader.getContext().getSharedPreferences(BelugaSortHelper.SORT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mSharedPreference.registerOnSharedPreferenceChangeListener(this);
    }

    public void dismiss(Context context) {
        mSharedPreference.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        BelugaSortHelper.SORTER sorter = BelugaSortHelper.getFileSorter(mLoader.getContext(), mCategoryType);
        if (!sorter.equals(mSorter)) {
            mSorter = sorter;
            mLoader.onContentChanged();
        }
    }
}
