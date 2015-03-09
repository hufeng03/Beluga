package com.hufeng.filemanager.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

import com.hufeng.filemanager.CategorySelectEvent;
import com.hufeng.filemanager.SortPreferenceReceiver;
import com.hufeng.filemanager.helper.BelugaSortHelper;
import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.provider.DataStructures;

/**
 * Created by Feng Hu on 15-02-15.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileCursorLoader extends CursorLoader {

    SortPreferenceReceiver mSortObserver;
    final int mCategory;
    private String mSearch;

    public FileCursorLoader(Context context, int category) {
        super(context);
        mCategory = category;
//        mSearch = string;

        Uri baseUri;
        String[] projection = null;
        switch (category) {
            case FileCategoryHelper.CATEGORY_TYPE_APK:
                baseUri = DataStructures.ApkColumns.CONTENT_URI;
                projection = DataStructures.ApkColumns.APK_PROJECTION;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                baseUri = DataStructures.AudioColumns.CONTENT_URI;
                projection = DataStructures.AudioColumns.AUDIO_PROJECTION;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                baseUri = DataStructures.ImageColumns.CONTENT_URI;
                projection = DataStructures.ImageColumns.IMAGE_PROJECTION;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                baseUri = DataStructures.VideoColumns.CONTENT_URI;
                projection = DataStructures.VideoColumns.VIDEO_PROJECTION;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                baseUri = DataStructures.DocumentColumns.CONTENT_URI;
                projection = DataStructures.DocumentColumns.DOCUMENT_PROJECTION;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                baseUri = DataStructures.ZipColumns.CONTENT_URI;
                projection = DataStructures.ZipColumns.ZIP_PROJECTION;
                break;
            default:
                baseUri = null; //Something is wrong here
                break;
        }

        if (baseUri != null) {
            BelugaSortHelper.SORTER sorter = BelugaSortHelper.getFileSorter(context, category);
            String sort_constraint = null;
            switch (sorter.field) {
                case NAME:
                    sort_constraint = DataStructures.FileColumns.NAME;
                    break;
                case DATE:
                    sort_constraint = DataStructures.FileColumns.DATE;
                    break;
                case SIZE:
                    sort_constraint = DataStructures.FileColumns.SIZE;
                    break;
                case EXTENSION:
                    sort_constraint = DataStructures.FileColumns.EXTENSION;
                    break;
            }
            if (!TextUtils.isEmpty(sort_constraint)) {
                if (sorter.order == BelugaSortHelper.SORT_ORDER.ASC) {
                    sort_constraint += " ASC";
                } else {
                    sort_constraint += " DESC";
                }
            }

            String search_constraint = null;
            String search_string = mSearch;
            if (!TextUtils.isEmpty(search_string)) {
                search_string = search_string.replace("[", "[[]");
                search_string = search_string.replace("%", "[%]");
                search_string = search_string.replace("_", "[_]");
                search_string = search_string.replace("^", "[^]");
                search_string = search_string.replace("'", "''");
                search_constraint = DataStructures.FileColumns.NAME + " LIKE '%" + search_string + "%'";
            }
            setUri(baseUri);
            setSelection(search_constraint);
            setProjection(projection);
            setSortOrder(sort_constraint);
        }
    }

    @Override
    public Cursor loadInBackground() {
        refreshSortOrder();
        return super.loadInBackground();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        // Start watching for changes in the app data.
        if (mSortObserver == null) {
            mSortObserver = new SortPreferenceReceiver(this, mCategory);
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Stop monitoring for changes.
        if (mSortObserver != null) {
            mSortObserver.dismiss(getContext());
            mSortObserver = null;
        }
    }

    private void refreshSortOrder() {
        BelugaSortHelper.SORTER sorter = BelugaSortHelper.getFileSorter(getContext(), mCategory);
        String sort_constraint = null;
        switch (sorter.field) {
            case NAME:
                sort_constraint = DataStructures.FileColumns.NAME;
                break;
            case DATE:
                sort_constraint = DataStructures.FileColumns.DATE;
                break;
            case SIZE:
                sort_constraint = DataStructures.FileColumns.SIZE;
                break;
            case EXTENSION:
                sort_constraint = DataStructures.FileColumns.EXTENSION;
                break;
        }
        if (!TextUtils.isEmpty(sort_constraint)) {
            if (sorter.order == BelugaSortHelper.SORT_ORDER.ASC) {
                sort_constraint += " ASC";
            } else {
                sort_constraint += " DESC";
            }
        }
        setSortOrder(sort_constraint);
    }
}
