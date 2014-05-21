package com.hufeng.filemanager;

import android.content.Context;

/**
 * Created by feng on 14-5-19.
 */
public class ToolItem extends DrawerItem{

    String mToolName;
    String mToolDesc;
    int mIcon;
    String mPackage;
    String mActivity;

    public ToolItem(Context context, int name, int des, int icon, String pkg, String act) {
        mToolName = context.getString(name);
        mToolDesc = context.getString(des);
        mIcon = icon;
        mPackage = pkg;
        mActivity = act;
    }

    public static ToolItem[] getAllToolItems(Context context) {
        return new ToolItem[] {
            new ToolItem(context, R.string.tool_name_ftp, R.string.tool_description_ftp, R.drawable.tool_icn_ftp, "com.hufeng.filemanager","com.hufeng.swiftp.ServerControlActivity"),
            new ToolItem(context, R.string.tool_name_http, R.string.tool_description_http, R.drawable.tool_icn_http, "com.hufeng.filemanager","com.hufeng.nanohttpd.ServerControlActivity"),
            new ToolItem(context, R.string.tool_name_safe, R.string.tool_description_safe, R.drawable.tool_icn_safe, "com.hufeng.filemanager", "com.hufeng.filemanager.SafeBoxActivity"),
            new ToolItem(context, R.string.tool_name_kanbox, R.string.tool_description_kanbox, R.drawable.tool_icn_kanbox, "com.hufeng.filemanager", "com.hufeng.filemanager.KanBoxActivity"),
            new ToolItem(context, R.string.tool_name_selected, R.string.tool_description_selected, R.drawable.tool_icn_recommend, "com.hufeng.filemanager", "com.hufeng.filemanager.ResourceActivity")
        };
    }

    @Override
    void render(NavigationDrawerItem layout) {
        layout.setText(mToolName);
        layout.setAsLabel(false, mIcon);
    }

    @Override
    void work(DrawerActivity activity) {

    }
}
