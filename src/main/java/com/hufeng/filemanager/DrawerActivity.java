package com.hufeng.filemanager;

/**
 * Created by feng on 14-5-19.
 */
public abstract class DrawerActivity extends FileOperationActivity {

    public abstract void showCategoryFragment(int category);
    public abstract void showDirectoryFragment(String path);

}
