package com.hufeng.filemanager;

/**
 * Created by feng on 14-5-19.
 */
public abstract class FileDrawerActivity extends FileOperationActivity {

    public abstract void showCategoryFragment(int category, String title);
    public abstract void showDirectoryFragment(String path, String title);
    public abstract void showCloudFragment(int provider, String title);

    public abstract boolean isDrawerOpen();


}
