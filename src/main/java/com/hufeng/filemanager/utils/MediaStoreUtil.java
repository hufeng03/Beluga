package com.hufeng.filemanager.utils;

import android.provider.MediaStore;

import com.hufeng.filemanager.browser.FileUtils;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by feng on 13-10-28.
 */
public class MediaStoreUtil {

    public static String sZipFileMimeType = "application/zip";

    public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
        {
            add("text/plain");
            add("text/plain");
            add("application/pdf");
            add("application/msword");
            add("application/vnd.ms-excel");
        }
    };

    private static String buildDocSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = sDocMimeTypesSet.iterator();
        while(iter.hasNext()) {
            selection.append("(" + MediaStore.Files.FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return  selection.substring(0, selection.lastIndexOf(")") + 1);
    }



    public static String buildSelectionByCategory(int cat) {
        String selection = null;
        switch (cat) {
            case FileUtils.FILE_TYPE_DOCUMENT:
                selection = buildDocSelection();
                break;
            case FileUtils.FILE_TYPE_ZIP:
                selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + " == '" + sZipFileMimeType + "')";
                break;
            case FileUtils.FILE_TYPE_APK:
                selection = MediaStore.Files.FileColumns.DATA + " LIKE '%.apk'";
                break;
            default:
                selection = null;
        }
        return selection;
    }

}