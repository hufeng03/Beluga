package com.hufeng.filemanager;

import android.content.Context;

/**
 * Created by feng on 14-6-1.
 */
public class CloudItem extends DrawerItem {


    enum CloudProvider {
        KANBOX,
    }

    String mCloudName;
    String mCloudDesc;
    int mIcon;
    CloudProvider mProvider;


    public CloudItem(Context context, int name, int des, int icon, CloudProvider provider) {
        mCloudName = context.getString(name);
        mCloudDesc = context.getString(des);
        mIcon = icon;
        mProvider = provider;
    }

    public static CloudItem[] getAllCloudItems(Context context) {
        return new CloudItem[] {
            new CloudItem(context, R.string.tool_name_kanbox, R.string.tool_description_kanbox, R.drawable.file_category_icon_kanbox, CloudProvider.KANBOX),
         };
    }

    @Override
    void render(NavigationDrawerItem layout) {
        layout.setText(mCloudName);
        layout.setAsLabel(false, mIcon);
    }

    @Override
    void work(FileDrawerActivity activity) {
        activity.showCloudFragment(mProvider.ordinal(), mCloudName);
    }
}
