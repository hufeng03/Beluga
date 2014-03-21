package com.hufeng.filemanager.app;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.hufeng.filemanager.GridAdapter;
import com.hufeng.filemanager.GridFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.InfoLoader;
import com.hufeng.filemanager.ui.FileViewHolder;

import java.util.List;

/**
 * Created by feng on 13-9-6.
 */
public class AppListAdapter extends ArrayAdapter<AppEntry> implements GridAdapter {

    InfoLoader mInfoLoader;

    LayoutInflater mInflater;
    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.LIST;

    int mItemResourceId = R.layout.file_list_row;

    public AppListAdapter(Context context) {
        super(context,0);
        mInfoLoader = InfoLoader.getInstance();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void changeDisplayMode(GridFragment.DISPLAY_MODE mode) {
        mMode = mode;
        switch (mMode) {
            case GRID:
                mItemResourceId = R.layout.file_grid_row;
                break;
            default:
                mItemResourceId = R.layout.file_list_row;
                break;
        }
        this.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        FileViewHolder holder = null;
        if(convertView != null) {
            holder = (FileViewHolder)convertView.getTag();
            if(holder.display == mMode.ordinal()) {
                view = convertView;
            } else {
                holder = null;
            }
        }

        if(view == null) {
            view = mInflater.inflate(mItemResourceId, parent, false);
            if (GridFragment.DISPLAY_MODE.LIST == mMode) {
                LinearLayout lframe = new LinearLayout(parent.getContext());
                lframe.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lframe.addView(view, params);
                mInflater.inflate(R.layout.seperate_line, lframe);
                view = lframe;
            }
        }

        if(holder == null){
            holder = new FileViewHolder(view);
            view.setTag(holder);
            holder.display = mMode.ordinal();
            holder.progress.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
        }


        AppEntry entry = getItem(position);

        holder.icon.setImageDrawable(entry.getIcon());
        holder.name.setText(entry.getLabel());

        if(!TextUtils.isEmpty(entry.getPackageName())) {
            holder.path = "app:"+entry.getPackageName();
        }
        mInfoLoader.loadInfo(holder.info, entry.getAppSourcePath());
        view.setBackgroundResource(R.drawable.list_selector_normal);
//        holder.path = item.path;
        return view;
    }

    public void setData(List<AppEntry> data) {
        clear();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                add(data.get(i));
            }
        }
    }

    @Override
    public GridFragment.DISPLAY_MODE getDisplayMode() {
        return mMode;
    }
}
