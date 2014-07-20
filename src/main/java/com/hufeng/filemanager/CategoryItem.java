package com.hufeng.filemanager;

import android.content.Context;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.utils.FileUtil;

/**
 * Created by feng on 14-5-19.
 */
public class CategoryItem extends DrawerItem {

    String mCategoryName;
    int mCategoryType;

    public CategoryItem(String name, int type) {
        mCategoryName = name;
        mCategoryType = type;
    }

    public static CategoryItem[] getAllCategoryItems(Context context) {
        return new CategoryItem[]{
//            new CategoryItem(context.getString(R.string.category_home), FileUtils.FILE_TYPE_ALL),
            new CategoryItem(context.getString(R.string.category_picture), FileUtils.FILE_TYPE_IMAGE),
            new CategoryItem(context.getString(R.string.category_music), FileUtils.FILE_TYPE_AUDIO),
            new CategoryItem(context.getString(R.string.category_video), FileUtils.FILE_TYPE_VIDEO),
            new CategoryItem(context.getString(R.string.category_document), FileUtils.FILE_TYPE_DOCUMENT),
            new CategoryItem(context.getString(R.string.category_apk), FileUtils.FILE_TYPE_APK),
            new CategoryItem(context.getString(R.string.category_zip), FileUtils.FILE_TYPE_ZIP)
        };
    }

    @Override
    void render(NavigationDrawerItem layout) {
        layout.setText(mCategoryName);
        int icon = 0;
        switch (mCategoryType) {
            case FileUtils.FILE_TYPE_AUDIO:
                icon = R.drawable.file_category_icon_audio;
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                icon = R.drawable.file_category_icon_video;
                break;
            case FileUtils.FILE_TYPE_IMAGE:
                icon = R.drawable.file_category_icon_image;
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                icon = R.drawable.file_category_icon_document;
                break;
            case FileUtils.FILE_TYPE_APK:
                icon = R.drawable.file_category_icon_apk;
                break;
            case FileUtils.FILE_TYPE_ZIP:
                icon = R.drawable.file_category_icon_zip;
                break;
            case FileUtils.FILE_TYPE_ALL:
                icon = R.drawable.file_category_icon_all;
        }
        layout.setAsLabel(false, icon);
    }

    @Override
    void work(FileDrawerActivity activity) {
        activity.showCategoryFragment(mCategoryType, mCategoryName);
    }
}
