package com.hufeng.filemanager;

import android.content.Context;
import android.text.TextUtils;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.storage.StorageManager;

/**
 * Created by feng on 14-5-19.
 */
public class DirectoryItem extends DrawerItem{

    String mPath;
    String mName;
    int mIcon;
    int mCategoryType;

    public DirectoryItem(String path, String name, boolean removable) {
        mPath = path;
        mName = name;
        if (removable) {
            mIcon = R.drawable.sd_card_icon;
        } else {
            mIcon = R.drawable.phone;
        }
    }

    public DirectoryItem(int type, String name) {
        mCategoryType = type;
        mName = name;
        if (type == FileUtils.FILE_TYPE_DOWNLOAD) {
            mIcon = R.drawable.file_category_icon_download;
        } else if (type == FileUtils.FILE_TYPE_FAVORITE) {
            mIcon = R.drawable.file_category_icon_favorite;
        }

    }

    public static DirectoryItem[] getAllDirectoryItems(Context context) {
        String[] stors = StorageManager.getInstance(context).getMountedStorages();
        DirectoryItem[] items = new DirectoryItem[stors.length+2];
        int i = 0;
        for (i=0; i<stors.length; i++) {
            items[i] = new DirectoryItem(stors[i],
                    StorageManager.getInstance(context).getStorageDescription(stors[i]),
                    !StorageManager.getInstance(context).isInternalStorage(stors[i]));

        }
        items[i] = new DirectoryItem(FileUtils.FILE_TYPE_FAVORITE, context.getString(R.string.category_favorite));
        items[i+1] = new DirectoryItem(FileUtils.FILE_TYPE_DOWNLOAD, context.getString(R.string.category_download));
        return items;
    }

    @Override
    void render(NavigationDrawerItem layout) {
        layout.setText(mName);

        layout.setAsLabel(false, mIcon);
    }

    @Override
    void work(FileDrawerActivity activity) {
        if (!TextUtils.isEmpty(mPath)) {
            activity.showDirectoryFragment(mPath, mName);
        } else if (mCategoryType != 0) {
            activity.showCategoryFragment(mCategoryType, mName);
        }
    }
}
