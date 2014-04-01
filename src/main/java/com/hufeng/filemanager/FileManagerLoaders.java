package com.hufeng.filemanager;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;

/**
 * Created by feng on 14-1-19.
 */
public class FileManagerLoaders {

    public static CursorLoader getCursorLoader(Context context, int type, String search_string) {
        Uri baseUri;
        String[] projection = null;
        switch (type) {
            case FileUtils.FILE_TYPE_APK:
                baseUri = DataStructures.ApkColumns.CONTENT_URI;
                projection = DataStructures.ApkColumns.APK_PROJECTION;
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                baseUri = DataStructures.AudioColumns.CONTENT_URI;
                projection = DataStructures.AudioColumns.AUDIO_PROJECTION;
                break;
            case FileUtils.FILE_TYPE_IMAGE:
                baseUri = DataStructures.ImageColumns.CONTENT_URI;
                projection = DataStructures.ImageColumns.IMAGE_PROJECTION;
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                baseUri = DataStructures.VideoColumns.CONTENT_URI;
                projection = DataStructures.VideoColumns.VIDEO_PROJECTION;
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                baseUri = DataStructures.DocumentColumns.CONTENT_URI;
                projection = DataStructures.DocumentColumns.DOCUMENT_PROJECTION;
                break;
            case FileUtils.FILE_TYPE_ZIP:
                baseUri = DataStructures.ZipColumns.CONTENT_URI;
                projection = DataStructures.ZipColumns.ZIP_PROJECTION;
                break;
            default:
                baseUri = null;
                break;
        }

        if(baseUri!=null){
            FileSorter.SORTER sorter = FileSorter.getFileSorter(context, type);
            String sort_constraint = null;
            switch(sorter.field){
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
            if(!TextUtils.isEmpty(sort_constraint)){
                if(sorter.order == FileSorter.SORT_ORDER.ASC){
                    sort_constraint += " ASC";
                }else{
                    sort_constraint += " DESC";
                }
            }

            String search_constraint = null;
            if(!TextUtils.isEmpty(search_string)) {
                search_constraint = DataStructures.FileColumns.FILE_NAME_FIELD+" LIKE '%"+search_string+"%'";
            }

            return new CursorLoader(context, baseUri,
                    projection, search_constraint, null,
                    sort_constraint);
        }
        else{
            return null;
        }
    }
}
