package com.hufeng.filemanager;

/**
 * Created by feng on 14-5-19.
 */
public abstract class DrawerItem {
    abstract void render(NavigationDrawerItem layout);

    abstract void work(FileDrawerActivity activity);
}
