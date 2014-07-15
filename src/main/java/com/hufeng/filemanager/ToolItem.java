package com.hufeng.filemanager;

import android.content.Context;

import com.hufeng.PlayHttpFragment;

/**
 * Created by feng on 14-5-19.
 */
public class ToolItem extends DrawerItem{

    final String mToolName;
    final String mToolDesc;
    final int mIcon;
    final Class<?> mFragmentClass;
    final String mFragmentTag;
    final Object[] mParams;

    public ToolItem(Context context, int name, int des, int icon, Class<?> fragment_class, String tag, Object... params) {
        mToolName = context.getString(name);
        mToolDesc = context.getString(des);
        mIcon = icon;
        mFragmentClass = fragment_class;
        mFragmentTag = tag;
        mParams = params;
    }

    public static ToolItem[] getAllToolItems(Context context) {
        return new ToolItem[] {
            new ToolItem(context, R.string.tool_name_ftp, R.string.tool_description_ftp, R.drawable.tool_icn_ftp, PlayFtpFragment.class,"PlayFtpFragment"),
            new ToolItem(context, R.string.tool_name_http, R.string.tool_description_http, R.drawable.tool_icn_http,PlayHttpFragment.class,"PlayHttpFragment"),
            new ToolItem(context, R.string.tool_name_safe, R.string.tool_description_safe, R.drawable.tool_icn_safe, SafeBoxTabFragment.class,"SafeBoxTabFragment"),
            new ToolItem(context, R.string.tool_name_selected, R.string.tool_description_selected, R.drawable.tool_icn_recommend, ResourceTabFragment.class,"ResourceTabFragment")
        };
    }

    @Override
    void render(NavigationDrawerItem layout) {
        layout.setText(mToolName);
        layout.setAsLabel(false, mIcon);
    }

    @Override
    void work(FileDrawerActivity activity) {
        activity.showFragment(mToolName, mFragmentClass, mFragmentTag, mParams);
    }
}
