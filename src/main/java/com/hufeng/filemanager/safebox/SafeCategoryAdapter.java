package com.hufeng.filemanager.safebox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hufeng.filemanager.GridAdapter;
import com.hufeng.filemanager.GridFragment;
import com.hufeng.filemanager.R;

import java.util.List;

/**
 * Created by feng on 13-10-3.
 */
public class SafeCategoryAdapter extends ArrayAdapter<SafeCategoryEntry> implements GridAdapter {

    LayoutInflater mInflater;

    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.GRID;

    int mItemResourceId = R.layout.safe_category_grid_row;

    public SafeCategoryAdapter(Context context) {
        super(context,0);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        SafeCategoryViewHolder holder = null;
        if(convertView == null) {
            view = mInflater.inflate(mItemResourceId, parent, false);
        } else {
            holder = (SafeCategoryViewHolder)convertView.getTag();
            if (holder.display == mMode.ordinal()) {
                view = convertView;
            } else {
                view = mInflater.inflate(mItemResourceId, parent, false);
                holder = null;
            }
        }

        if(holder == null){
            holder = new SafeCategoryViewHolder();
            view.setTag(holder);
            holder.name = (TextView) view.findViewById(R.id.category_name);
            holder.icon = (ImageView) view.findViewById(R.id.category_icon);
        }

        SafeCategoryEntry entry = getItem(position);
        holder.name.setText(entry.name);
        holder.icon.setImageDrawable(entry.icon);
        holder.category = entry.category;

        holder.display = mMode.ordinal();
        view.setBackgroundResource(R.drawable.list_selector_normal);
        return view;
    }

    public void setData(List<SafeCategoryEntry> data) {
        clear();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                add(data.get(i));
            }
        }
    }

    public void setData(SafeCategoryEntry[] entries) {
        clear();
        if (entries != null) {
            for (SafeCategoryEntry entry:entries) {
                add(entry);
            }
        }
    }

    @Override
    public void changeDisplayMode(GridFragment.DISPLAY_MODE mode) {
        mMode = mode;
        switch (mMode) {
            case GRID:
                mItemResourceId = R.layout.safe_category_grid_row;
                break;
            default:
                mItemResourceId = R.layout.safe_category_list_row;
                break;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public GridFragment.DISPLAY_MODE getDisplayMode() {
        return mMode;
    }
}
