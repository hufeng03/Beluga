package com.hufeng.filemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.app.AppEntry;
import com.hufeng.filemanager.storage.StorageUnit;

/**
 * Created by feng on 2014-10-26.
 */
public class DeviceAdapter extends ArrayAdapter<StorageUnit>{

    int mItemResourceId = R.layout.layout_device_item;

    LayoutInflater mInflater;

    public DeviceAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            view = mInflater.inflate(mItemResourceId, parent, false);
        }

        ImageView icon = (ImageView)view.findViewById(R.id.icon);
        TextView name = (TextView)view.findViewById(R.id.name);

        StorageUnit entry = getItem(position);

        if (entry.isRemovable()) {
            name.setText(R.string.external_storage);
            icon.setImageResource(R.drawable.sd_card_icon);
        } else {
            name.setText(R.string.internal_storage);
            icon.setImageResource(R.drawable.phone_data_icon);
        }

        return view;
    }
}
