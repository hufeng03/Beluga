package com.hufeng.filemanager.utils;

import android.provider.MediaStore;

import com.hufeng.filemanager.browser.FileUtils;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by feng on 13-10-28.
 */
public class MediaStoreUtil {

//    public static String sZipFileMimeType = "application/zip";

    public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
        {
            add("text/plain");
            add("text/html");
            add("application/pdf");
            add("application/msword");
            add("application/vnd.ms-excel");
        }
    };

    public static HashSet<String> sZipMimeTypesSet = new HashSet<String>() {
        {
            add("application/zip");
            add("application/x-zip-compressed");
            add("application/x-gtar");
            add("application/x-tgz");
            add("application/x-gzip");
            add("application/x-tar");
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

    private static String buildZipSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = sZipMimeTypesSet.iterator();
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
//                selection = "(" + MediaStore.Files.FileColumns.MIME_TYPE + " == '" + buildZipSelection + "')";
                selection = buildZipSelection();
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