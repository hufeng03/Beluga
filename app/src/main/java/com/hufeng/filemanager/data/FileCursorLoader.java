package com.hufeng.filemanager.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

import com.hufeng.filemanager.CategorySelectEvent;
import com.hufeng.filemanager.SortPreferenceReceiver;
import com.hufeng.filemanager.helper.BelugaSortHelper;
import com.hufeng.filemanager.provider.DataStructures;

/**
 * Created by Feng Hu on 15-02-15.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileCursorLoader extends CursorLoader {

    SortPreferenceReceiver mSortObserver;
    final CategorySelectEvent.CategoryType mCategoryType;

    public FileCursorLoader(Context context, CategorySelectEvent.CategoryType categoryType, String search) {
        super(context);
        mCategoryType = categoryType;
        Uri baseUri;
        String[] projection = null;
        switch (categoryType) {
            case APK:
                baseUri = DataStructures.ApkColumns.CONTENT_URI;
                projection = DataStructures.ApkColumns.APK_PROJECTION;
                break;
            case AUDIO:
                baseUri = DataStructures.AudioColumns.CONTENT_URI;
                projection = DataStructures.AudioColumns.AUDIO_PROJECTION;
                break;
            case PHOTO:
                baseUri = DataStructures.ImageColumns.CONTENT_URI;
                projection = DataStructures.ImageColumns.IMAGE_PROJECTION;
                break;
            case VIDEO:
                baseUri = DataStructures.VideoColumns.CONTENT_URI;
                projection = DataStructures.VideoColumns.VIDEO_PROJECTION;
                break;
            case DOC:
                baseUri = DataStructures.DocumentColumns.CONTENT_URI;
                projection = DataStructures.DocumentColumns.DOCUMENT_PROJECTION;
                break;
            case ZIP:
                baseUri = DataStructures.ZipColumns.CONTENT_URI;
                projection = DataStructures.ZipColumns.ZIP_PROJECTION;
                break;
            case FAVORITE:
                baseUri = DataStructures.FavoriteColumns.CONTENT_URI;
                projection = DataStructures.FavoriteColumns.FAVORITE_PROJECTION;
                break;
            default:
                baseUri = null; //Something is wrong here
                break;
        }

        if (baseUri != null) {
            BelugaSortHelper.SORTER sorter = BelugaSortHelper.getFileSorter(context, categoryType);
            String sort_constraint = null;
            switch (sorter.field) {
                case NAME:
                    sort_constraint = DataStructures.FileColumns.FILE_NAME_FIELD;
                    break;
                case DATE:
                    sort_constraint = DataStructures.FileColumns.FILE_DATE_FIELD;
                    break;
                case SIZE:
                    sort_constraint = DataStructures.FileColumns.FILE_SIZE_FIELD;
                    break;
                case EXTENSION:
                    sort_constraint = DataStructures.FileColumns.FILE_EXTENSION_FIELD;
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
            String search_string = search;
            if (!TextUtils.isEmpty(search_string)) {
                search_string.replace("[", "[[]");
                search_string.replace("%", "[%]");
                search_string.replace("_", "[_]");
                search_string.replace("^", "[^]");
                search_string = search_string.replace("'", "''");
                search_constraint = DataStructures.FileColumns.FILE_NAME_FIELD + " LIKE '%" + search_string + "%'";
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
            mSortObserver = new SortPreferenceReceiver(this, mCategoryType);
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
        BelugaSortHelper.SORTER sorter = BelugaSortHelper.getFileSorter(getContext(), mCategoryType);
        String sort_constraint = null;
        switch (sorter.field) {
            case NAME:
                sort_constraint = DataStructures.FileColumns.FILE_NAME_FIELD;
                break;
            case DATE:
                sort_constraint = DataStructures.FileColumns.FILE_DATE_FIELD;
                break;
            case SIZE:
                sort_constraint = DataStructures.FileColumns.FILE_SIZE_FIELD;
                break;
            case EXTENSION:
                sort_constraint = DataStructures.FileColumns.FILE_EXTENSION_FIELD;
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
