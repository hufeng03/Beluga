package com.hufeng.filemanager.kanbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.hufeng.filemanager.GridAdapter;
import com.hufeng.filemanager.GridFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.browser.InfoLoader;
import com.hufeng.filemanager.ui.FileViewHolder;
import com.hufeng.filemanager.utils.TimeUtil;

/**
 * Created by feng on 3/10/2014.
 */
public class KanBoxUploadAdapter extends ArrayAdapter<FileEntry> implements GridAdapter{

    private static final String TAG = KanBoxUploadAdapter.class.getSimpleName();

    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.LIST;

    LayoutInflater mInflater;
    int mItemResourceId = R.layout.file_list_row;
    InfoLoader mInfoLoader;
    FileViewHolder.FileViewClicked mCallback;

//    public KanBoxUploadAdapter(Context context, Cursor c) {
//        super(context, c);
//        mInflater = LayoutInflater.from(context);
//    }

    public KanBoxUploadAdapter(Context context, FileViewHolder.FileViewClicked callback) {
        super(context,0);
        mInflater = LayoutInflater.from(context);
        mInfoLoader = InfoLoader.getInstance();
        mCallback = callback;
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
            holder = new FileViewHolder(view, mCallback);
            view.setTag(holder);
            holder.display = mMode.ordinal();
        }

        FileEntry item = getItem(position);

        holder.path = item.path;
        holder.icon.setDefaultResource(IconLoaderHelper.getFileIcon(parent.getContext(), holder.path, GridFragment.DISPLAY_MODE.GRID.ordinal() == holder.display));
        holder.icon.requestDisplayLocalThumbnail(holder.path);

        String filename = item.getName();

        holder.name.setText(filename);
//        mInfoLoader.loadInfo(holder.info, holder.path);
        int progress = KanBoxApi.getInstance().getUploadingingProgress(holder.path);
        holder.progress.setProgress(progress);
        holder.progress.setVisibility(View.GONE);

        if (KanBoxApi.isUploading(holder.path)) {
            holder.progress.setVisibility(View.VISIBLE);
            holder.info.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);

            if (progress == 0) {
                holder.progress.setIndeterminate(true);
            } else {
                holder.progress.setIndeterminate(false);
            }
            holder.status.setText(R.string.btn_txt_pause);
        } else {
            holder.info.setVisibility(View.VISIBLE);
            holder.progress.setIndeterminate(false);
            long time = 0;
            if( (time = KanBoxApi.getUploadCancellingTime(holder.path)) != 0) {
                holder.status.setVisibility(View.GONE);
                holder.info.setText(getContext().getResources().getString(R.string.kanbox_upload_info_cancelling, TimeUtil.getDateString(time)));
            } else if( (time = KanBoxApi.getUploadWaitingTime(holder.path)) != 0) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(R.string.btn_txt_pause);
                holder.info.setText(getContext().getResources().getString(R.string.kanbox_upload_info_waiting, TimeUtil.getDateString(time)));
            } else if ( (time = KanBoxApi.getUploadCancelledTime(holder.path)) != 0) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(R.string.btn_txt_upload_again);
                holder.info.setText(getContext().getResources().getString(R.string.kanbox_upload_info_cancelled, TimeUtil.getDateString(time)));
            } else if ( (time = KanBoxApi.getUploadFailedTime(holder.path)) != 0) {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(R.string.btn_txt_upload_again);
                holder.info.setText(getContext().getResources().getString(R.string.kanbox_upload_info_failed, TimeUtil.getDateString(time)));
            } else if ( (time = KanBoxApi.getUploadSuccessTime(holder.path)) != 0) {
                holder.status.setVisibility(View.GONE);
                holder.info.setText(getContext().getResources().getString(R.string.kanbox_upload_info_success, TimeUtil.getDateString(time)));
            }
        }


        holder.position = position;
        view.setBackgroundResource(R.drawable.list_selector_normal);

        return view;
    }

    @Override
    public GridFragment.DISPLAY_MODE getDisplayMode() {
        return mMode;
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


}
