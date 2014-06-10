package com.hufeng.filemanager;

import android.content.Context;

import com.hufeng.filemanager.storage.StorageManager;

/**
 * Created by feng on 14-5-19.
 */
public class DirectoryItem extends DrawerItem{

    String mPath;
    String mName;
    int mIcon;

    public DirectoryItem(Context context, String path, String description, boolean removable) {
        mPath = path;
        mName = description;
        if (removable) {
            mIcon = R.drawable.sd_card_icon;
        } else {
            mIcon = R.drawable.phone;
        }
    }

    public static DirectoryItem[] getAllDirectoryItems(Context context) {
        String[] stors = StorageManager.getInstance(context).getMountedStorages();
        DirectoryItem[] items = new DirectoryItem[stors.length];
        for (int i=0; i<stors.length; i++) {
            items[i] = new DirectoryItem(context, stors[i],
                    StorageManager.getInstance(context).getStorageDescription(stors[i]),
                    !StorageManager.getInstance(context).isInternalStorage(stors[i]));

        }
        return items;
    }

    @Override
    void render(NavigationDrawerItem layout) {
        layout.setText(mName);

        layout.setAsLabel(false, mIcon);
    }

    @Override
    void work(FileDrawerActivity activity) {
        activity.showDirectoryFragment(mPath, mName);
    }
}
