package com.hufeng.filemanager.ui;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hufeng.filemanager.FileSearchUtil;
import com.hufeng.filemanager.GridAdapter;
import com.hufeng.filemanager.GridFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.ApkInfoLoader;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.IconLoader;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.browser.InfoLoader;
import com.hufeng.filemanager.browser.InfoUtil;
import com.hufeng.filemanager.provider.DataStructures.FileColumns;

import java.io.File;
import java.util.ArrayList;

public class FileCursorAdapter extends CursorAdapter implements GridAdapter {
	
	LayoutInflater mInflater;
//    IconLoader mIconLoader;
    InfoLoader mInfoLoader;
    ApkInfoLoader mApkInfoLoader;
    String mSearchString = null;

    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.LIST;
    int mItemResourceId = R.layout.file_list_row;

    FileOperation mFileOperation = null;

	public FileCursorAdapter(Context context, Cursor c, FileOperation fileOperation) {
		super(context, c);
        mFileOperation = fileOperation;
		mInflater = LayoutInflater.from(context);
//        mIconLoader = IconLoader.getInstance();
        mInfoLoader = InfoLoader.getInstance();
        mApkInfoLoader = ApkInfoLoader.getInstance();
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
	public void bindView(View view, Context context, Cursor cursor) {
        FileViewHolder holder = (FileViewHolder)view.getTag();
        if(holder == null){
        	holder = new FileViewHolder(view);
        	view.setTag(holder);
            holder.display = mMode.ordinal();
            holder.progress.setVisibility(View.GONE);
        }

        holder.path = cursor.getString(FileColumns.FILE_PATH_FIELD_INDEX);
        holder.icon.setDefaultResource(IconLoaderHelper.getFileIcon(context, holder.path, GridFragment.DISPLAY_MODE.GRID.ordinal() == holder.display));
        holder.icon.requestDisplayLocalThumbnail(holder.path);

        String filename = cursor.getString(FileColumns.FILE_NAME_FIELD_INDEX);
        SpannableStringBuilder span = FileSearchUtil.highlightSearchText(context, filename, mSearchString);
        File file = new File(holder.path);
        if (!file.exists()) {
            if (mFileGridAdapterListener != null) {
                mFileGridAdapterListener.reportNotExistFile();
            }
        }
        if (span != null) {
            holder.name.setText(span);
        } else {
            holder.name.setText(filename);
        }

        mInfoLoader.loadInfo(holder.info, holder.path);

        if( mFileOperation!=null ) {
            if(mFileOperation.isMovingOrCopying()) {
                if(mFileOperation.isMoving(holder.path)) {
                    holder.status.setBackgroundResource(R.drawable.file_move_indicator);
                    holder.status.setVisibility(View.VISIBLE);
                }
                else if(mFileOperation.isCopying(holder.path)) {
                    holder.status.setBackgroundResource(R.drawable.file_copy_indicator);
                    holder.status.setVisibility(View.VISIBLE);
                } else {
                    holder.status.setVisibility(View.GONE);
                }
            } else {
                holder.status.setVisibility(View.GONE);
            }
            if (mFileOperation.isSelected(holder.path)) {
                view.setBackgroundResource(R.drawable.list_selector_activated);
            } else {
                view.setBackgroundResource(R.drawable.list_selector_normal);
            }
        } else {
            holder.status.setVisibility(View.GONE);
            view.setBackgroundResource(R.drawable.list_selector_normal);
        }
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        View view = mInflater.inflate(mItemResourceId, null);
        if (GridFragment.DISPLAY_MODE.LIST == mMode) {
            LinearLayout lframe = new LinearLayout(arg0);
            lframe.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lframe.addView(view, params);
            return mInflater.inflate(R.layout.seperate_line, lframe);
        } else {
            return view;
        }
	}

    public String[] getAllFiles(){
        Cursor cursor = getCursor();
        ArrayList<String> files = new ArrayList<String>();
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                files.add(cursor.getString(FileColumns.FILE_PATH_FIELD_INDEX));
            } while(cursor.moveToNext());
        }
        return files.toArray(new String[files.size()]);
    }

    FileGridAdapterListener mFileGridAdapterListener;

    public void setFileGridAdapterListener(FileGridAdapterListener listener){
        mFileGridAdapterListener = listener;
    }

    @Override
    public GridFragment.DISPLAY_MODE getDisplayMode() {
        return mMode;
    }


    @Override
    public void notifyDataSetChanged() {
        if (mFileGridAdapterListener != null) {
            mSearchString = mFileGridAdapterListener.getSearchString();
        }
        super.notifyDataSetChanged();
    }
}
