package com.hufeng.filemanager.resource;

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
import com.hufeng.filemanager.browser.IconLoader;
import com.hufeng.filemanager.ui.FileViewHolder;
import com.hufeng.filemanager.utils.LogUtil;

import java.util.List;

/**
 * Created by feng on 13-9-20.
 */
public class ResourceListAdapter extends ArrayAdapter<ResourceEntry> implements GridAdapter {

    private static final String LOG_TAG = ResourceListAdapter.class.getSimpleName();

    LayoutInflater mInflater;
    IconLoader mIconLoader;
    ResourceIconLoader mGameIconLoader;

    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.LIST;

    int mItemResourceId = R.layout.file_list_row;

    public ResourceListAdapter(Context context) {
        super(context, 0);
        mInflater = LayoutInflater.from(context);
        mIconLoader = IconLoader.getInstance();
        mGameIconLoader = ResourceIconLoader.getInstance();
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
            view = mInflater.inflate(mItemResourceId, null);
            if (GridFragment.DISPLAY_MODE.LIST == mMode) {
                LinearLayout lframe = new LinearLayout(parent.getContext());
                lframe.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lframe.addView(view, params);
                view = mInflater.inflate(R.layout.seperate_line, lframe);
            }
        }

        if(holder == null){
            holder = new FileViewHolder(view);
            view.setTag(holder);
            holder.display = mMode.ordinal();
            holder.progress.setVisibility(View.GONE);
        }


        ResourceEntry entry = getItem(position);
        String name = entry.resource_name;
        if(TextUtils.isEmpty(name)) {
           name = entry.name;
        }
        holder.name.setText(name);
        holder.info.setText(entry.resource_description);


        if(!TextUtils.isEmpty(entry.path)) {
            holder.path = entry.path;
        } else {
            if( !TextUtils.isEmpty(entry.package_name) ) {
                holder.path = "app:"+entry.package_name;
            }
        }

        if (!TextUtils.isEmpty(entry.resource_icon_url)) {
            mIconLoader.remove(holder.icon);
            mGameIconLoader.loadIcon(holder.icon, null, null, entry.resource_icon_url);
        } else {
            mIconLoader.loadIcon(holder.icon, null, null, holder.path);
            mGameIconLoader.remove(holder.icon);
        }
        if (entry.needDownload()) {
            if(FileDownloader.isDownloading(entry.download_url)) {
                int progress = FileDownloader.getDownloadProgress(entry.download_url);
                LogUtil.i(LOG_TAG, "apk download progress is " + progress);
                if (progress == 0) {
                    holder.progress.setProgress(progress);
                    holder.progress.setIndeterminate(true);
                    holder.progress.setVisibility(View.VISIBLE);
                    holder.info.setVisibility(View.GONE);
                    holder.status.setText(R.string.btn_txt_pause);
                } else if (progress == -100) {
                    holder.progress.setVisibility(View.GONE);
                    holder.info.setVisibility(View.VISIBLE);
                    holder.info.setText(R.string.file_download_paused);
                    holder.status.setText(R.string.btn_txt_pause);
                } else {
                    holder.progress.setProgress(progress);
                    holder.progress.setIndeterminate(false);
                    holder.progress.setVisibility(View.VISIBLE);
                    holder.info.setVisibility(View.GONE);
                    holder.status.setText(R.string.btn_txt_pause);
                }
            } else {
                holder.progress.setVisibility(View.GONE);
                holder.info.setVisibility(View.VISIBLE);
                holder.status.setText(R.string.btn_txt_download);
            }
        } else {
            if(entry.isInstalled()) {
                if (entry.resource_category == 2) {
                    holder.status.setText(R.string.btn_txt_read);
                } else {
                    holder.status.setText(R.string.btn_txt_open);
                }
            } else {
                if (entry.resource_category == 2) {
                    holder.status.setText(R.string.btn_txt_read);
                } else {
                    holder.status.setText(R.string.btn_txt_install);
                }
            }
            holder.progress.setVisibility(View.GONE);
            holder.info.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void setData(List<ResourceEntry> data) {
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
