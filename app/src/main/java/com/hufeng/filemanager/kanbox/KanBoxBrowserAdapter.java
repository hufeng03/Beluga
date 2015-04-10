package com.hufeng.filemanager.kanbox;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hufeng.filemanager.GridAdapter;
import com.hufeng.filemanager.GridFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.IconLoader;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.ui.FileViewHolder;
import com.hufeng.filemanager.utils.TimeUtil;

/**
 * Created by feng on 13-11-22.
 */
public class KanBoxBrowserAdapter extends CursorAdapter implements GridAdapter {

    private static final String TAG = KanBoxBrowserAdapter.class.getSimpleName();

    LayoutInflater mInflater;
    IconLoader mIconLoader;
    KanboxIconLoader mKanboxIconLoader;
    FileViewHolder.FileViewClicked mCallback;

    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.LIST;

    int mItemResourceId = R.layout.file_list_row;

    public KanBoxBrowserAdapter(Context context, Cursor c, FileViewHolder.FileViewClicked callback) {
        super(context, c);
        mInflater = LayoutInflater.from(context);
        mIconLoader = IconLoader.getInstance();
        mKanboxIconLoader = KanboxIconLoader.getInstance();
        mCallback = callback;
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
    public GridFragment.DISPLAY_MODE getDisplayMode() {
        return mMode;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView != null) {
            FileViewHolder holder = (FileViewHolder)convertView.getTag();
            if(holder.display != mMode.ordinal()) {
                view = null;
            }
        }
        return super.getView(position, view, parent);
    }
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(mItemResourceId, null);
        if (GridFragment.DISPLAY_MODE.LIST == mMode) {
            LinearLayout lframe = new LinearLayout(context);
            lframe.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lframe.addView(view, params);
            return mInflater.inflate(R.layout.seperate_line, lframe);
        } else {
            return view;
        }

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        FileViewHolder holder = (FileViewHolder)view.getTag();
        if(holder == null){
            holder = new FileViewHolder(view, mCallback);
            view.setTag(holder);
            holder.display = mMode.ordinal();
        }

        holder.name.setText(cursor.getString(DataStructures.FileColumns.FILE_NAME_FIELD_INDEX));
        holder.path = cursor.getString(DataStructures.FileColumns.FILE_PATH_FIELD_INDEX);
        int is_folder = cursor.getInt(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD_INDEX);
        int type = cursor.getInt(DataStructures.FileColumns.FILE_TYPE_FIELD_INDEX);
        long date = cursor.getLong(DataStructures.FileColumns.FILE_DATE_FIELD_INDEX);
        String local_file = cursor.getString(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD_INDEX);
        if(is_folder == 1) {
            holder.icon.setDefaultResource(GridFragment.DISPLAY_MODE.GRID.ordinal() == holder.display?R.drawable.file_icon_folder_square:R.drawable.file_icon_folder);
            holder.icon.requestDisplayKanboxThumbnail(holder.path, true);
            holder.progress.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setText(TimeUtil.getDateString(date));
            holder.check.setVisibility(View.GONE);
        } else {
            holder.icon.setDefaultResource(IconLoaderHelper.getFileIcon(context, holder.path, GridFragment.DISPLAY_MODE.GRID.ordinal() == holder.display));
            if(!TextUtils.isEmpty(local_file)) {
                holder.icon.requestDisplayLocalThumbnail(local_file);
                holder.progress.setVisibility(View.GONE);
                holder.status.setVisibility(View.GONE);
                holder.info.setVisibility(View.VISIBLE);
                holder.info.setText(TimeUtil.getDateString(date));
                holder.check.setVisibility(View.VISIBLE);
            } else {
                holder.check.setVisibility(View.GONE);
                holder.icon.requestDisplayKanboxThumbnail(holder.path, false);

                if(KanBoxApi.isDownloading(holder.path)) {
                    if (KanBoxApi.isDownloadCancelling(holder.path)) {
                        holder.status.setVisibility(View.GONE);
                        holder.info.setVisibility(View.VISIBLE);
                        holder.progress.setVisibility(View.GONE);
                        holder.info.setText(context.getString(R.string.kanbox_download_info_cancelling, TimeUtil.getDateString(date)));
                    } else if (KanBoxApi.isDownloadWaiting(holder.path)) {
                        holder.info.setVisibility(View.VISIBLE);
                        holder.progress.setVisibility(View.GONE);
                        if (holder.display == GridFragment.DISPLAY_MODE.LIST.ordinal()) {
                            holder.status.setVisibility(View.VISIBLE);
                            holder.status.setText(R.string.btn_txt_pause);
                        } else {
                            holder.status.setVisibility(View.GONE);
                        }
                        holder.info.setText(context.getString(R.string.kanbox_download_info_waiting, TimeUtil.getDateString(date)));
                    } else {
                        holder.info.setVisibility(View.GONE);
                        holder.progress.setVisibility(View.VISIBLE);
                        if (holder.display == GridFragment.DISPLAY_MODE.LIST.ordinal()) {
                            holder.status.setVisibility(View.VISIBLE);
                            holder.status.setText(R.string.btn_txt_pause);
                        } else {
                            holder.status.setVisibility(View.GONE);
                        }
                        int progress = KanBoxApi.getDownloadingProgress(holder.path);
                        if (progress == 0) {
                            holder.progress.setIndeterminate(true);
                            holder.progress.setProgress(progress);
                        } else {
                            holder.progress.setIndeterminate(false);
                            holder.progress.setProgress(progress);
                        }
                    }
                } else {
                    holder.info.setVisibility(View.VISIBLE);
                    holder.progress.setVisibility(View.GONE);
                    if (holder.display == GridFragment.DISPLAY_MODE.LIST.ordinal()) {
                        holder.status.setVisibility(View.VISIBLE);
                        holder.status.setVisibility(View.GONE);
//                        holder.status.setText(R.string.btn_txt_download);
                    } else {
                        holder.status.setVisibility(View.GONE);
                    }
                    holder.info.setText(TimeUtil.getDateString(date));
                }
            }
        }
        view.setBackgroundResource(R.drawable.list_selector_normal);

        holder.position = cursor.getPosition();

        Log.i(TAG, "bind view " + holder.path + ", " + is_folder + ", " + type);
    }

    public String[] getAllFiles() {
        return null;
    }


}