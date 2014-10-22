package com.hufeng.filemanager;

import com.hufeng.filemanager.browser.FileUtils;

/**
 * Created by feng on 14-1-19.
 */
public class LoaderIDs {
    public static final int LOADER_ID_CATEGORY_FILE = 1000;
    public static final int LOADER_ID_CATEGORY_IMAGE = 1001;
    public static final int LOADER_ID_CATEGORY_VIDEO = 1002;
    public static final int LOADER_ID_CATEGORY_AUDIO = 1003;
    public static final int LOADER_ID_CATEGORY_APK = 1004;
    public static final int LOADER_ID_CATEGORY_ZIP = 1005;
    public static final int LOADER_ID_CATEGORY_DOCUMENT = 1006;
    public static final int LOADER_ID_RESOURCE_GAME = 2001;
    public static final int LOADER_ID_RESOURCE_APP = 2002;
    public static final int LOADER_ID_RESOURCE_DOC = 2003;

    public static int getLoaderId(int type){
        int id;
        switch(type) {
            case FileUtils.FILE_TYPE_IMAGE:
                id = LOADER_ID_CATEGORY_IMAGE;
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                id = LOADER_ID_CATEGORY_AUDIO;
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                id = LOADER_ID_CATEGORY_VIDEO;
                break;
            case FileUtils.FILE_TYPE_APK:
                id = LOADER_ID_CATEGORY_APK;
                break;
            case FileUtils.FILE_TYPE_ZIP:
                id = LOADER_ID_CATEGORY_ZIP;
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                id = LOADER_ID_CATEGORY_DOCUMENT;
                break;
            case FileUtils.FILE_TYPE_RESOURCE_APP:
                id = LOADER_ID_RESOURCE_APP;
                break;
            case FileUtils.FILE_TYPE_RESOURCE_GAME:
                id = LOADER_ID_RESOURCE_GAME;
                break;
            case FileUtils.FILE_TYPE_RESOURCE_DOC:
                id = LOADER_ID_RESOURCE_DOC;
                break;
            default:
                id = LOADER_ID_CATEGORY_FILE;
                break;
        }
        return id;
    }
}
