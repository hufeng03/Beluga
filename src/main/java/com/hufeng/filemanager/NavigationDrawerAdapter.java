package com.hufeng.filemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by feng on 14-5-19.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<DrawerItem> {

    private LayoutInflater mInflater;

    private Context mContext;

    public NavigationDrawerAdapter(Context context) {
        super(context, 0);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NavigationDrawerItem drawerItem = null;

        if (convertView != null) {
            drawerItem = (NavigationDrawerItem) convertView;
        } else {
            drawerItem = (NavigationDrawerItem) mInflater.inflate(R.layout.drawer_item_layout, parent, false);
        }

        drawerItem.bind(getItem(position));

        return drawerItem;
    }



}
