package com.hufeng.filemanager.ui;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.hufeng.filemanager.FileSearchUtil;
import com.hufeng.filemanager.GridAdapter;
import com.hufeng.filemanager.GridFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.ApkInfoLoader;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.IconLoader;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.browser.InfoLoader;
import com.hufeng.filemanager.browser.InfoUtil;

import java.io.File;
import java.util.ArrayList;

public class FileArrayAdapter extends ArrayAdapter<FileEntry> implements GridAdapter {

    private static final String LOG_TAG = FileArrayAdapter.class.getSimpleName();

	LayoutInflater mInflater;
    IconLoader mIconLoader;
    InfoLoader mInfoLoader;
    ApkInfoLoader mApkInfoLoader;
    String mSearchString = null;


    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.LIST;

    int mItemResourceId = R.layout.file_list_row;

    FileOperation mFileOperation = null;

	public FileArrayAdapter(Context context, FileOperation fileOperation) {
		super(context,0);
        mFileOperation = fileOperation;
		mInflater = LayoutInflater.from(context);
        mIconLoader = IconLoader.getInstance();
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
        }

        FileEntry item = getItem(position);

        holder.path = item.path;
        holder.icon.setDefaultResource(IconLoaderHelper.getFileIcon(parent.getContext(), holder.path, GridFragment.DISPLAY_MODE.GRID.ordinal() == holder.display));
        holder.icon.requestDisplayLocalThumbnail(holder.path);

        String filename = item.getName();
        if (TextUtils.isEmpty(filename)) {
            filename = item.path;
        }
        SpannableStringBuilder span = FileSearchUtil.highlightSearchText(parent.getContext(), filename, mSearchString);
        File file = new File(holder.path);
        if (!file.exists()) {
            if (mFileGridAdapterListener != null) {
                mFileGridAdapterListener.reportNotExistFile();
            }
        }
        int type = FileUtils.getFileType(file);
        if (FileUtils.FILE_TYPE_APK == type) {
            if (span != null) {
//                mInfoLoader.remove(holder.info);
                holder.name.setText(span);
            } else {
                holder.name.setText(filename);
            }
            mApkInfoLoader.loadInfo(holder.info, holder.path);
        } else {
            mApkInfoLoader.remove(holder.info);
            if (span != null) {
                holder.name.setText(span);
            } else {
                holder.name.setText(filename);
            }
//            mInfoLoader.loadInfo(holder.info, holder.path);
            String info = InfoUtil.getFileInfo(holder.path, type);
            holder.info.setText(info);
        }

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

        return view;
    }

    public String[] getAllFiles(){
        int count = getCount();
        ArrayList<String> files = new ArrayList<String>(count);
        while(count-->0) {
            FileEntry entry = getItem(count);
            files.add(entry.path);
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
