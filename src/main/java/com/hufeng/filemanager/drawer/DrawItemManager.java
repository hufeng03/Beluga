package com.hufeng.filemanager.drawer;

import android.content.Context;

import com.hufeng.filemanager.CategoryItem;
import com.hufeng.filemanager.CloudItem;
import com.hufeng.filemanager.DirectoryItem;
import com.hufeng.filemanager.DrawerItem;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.LabelItem;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.ToolItem;

import java.util.ArrayList;

/**
 * Created by feng on 2014-07-10.
 */
public enum DrawItemManager {
    INSTANCE;

    private static ArrayList<DrawerItem> mDrawItems;

    static {
        mDrawItems = new ArrayList<DrawerItem>();
        Context context = FileManager.getAppContext();
        mDrawItems.add(new LabelItem(context.getString(R.string.category)));
        DrawerItem[] items = CategoryItem.getAllCategoryItems(context);
        if (items != null) {
            for (DrawerItem item : items) {
                mDrawItems.add(item);
            }
        }
        mDrawItems.add(new LabelItem(context.getString(R.string.directory)));
        items = DirectoryItem.getAllDirectoryItems(context);
        if (items != null) {
            for (DrawerItem item : items) {
                mDrawItems.add(item);
            }
        }

        mDrawItems.add(new LabelItem(context.getString(R.string.cloud)));
        items = CloudItem.getAllCloudItems(context);
        if (items != null) {
            for (DrawerItem item : items) {
                mDrawItems.add(item);
            }
        }

        mDrawItems.add(new LabelItem(context.getString(R.string.tools)));
        items = ToolItem.getAllToolItems(context);
        if (items != null) {
            for (DrawerItem item : items) {
                mDrawItems.add(item);
            }
        }
    }

    public static DrawerItem[] getAllDrawerItems() {
        return mDrawItems.toArray(new DrawerItem[0]);
    }

    public static DrawerItem getDrawerItemAtPosition(int position) {
        return mDrawItems.get(position);
    }

}
