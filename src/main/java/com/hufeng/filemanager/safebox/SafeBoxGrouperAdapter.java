package com.hufeng.filemanager.safebox;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hufeng.filemanager.GridAdapter;
import com.hufeng.filemanager.GridFragment;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.ui.FileGridAdapterListener;
import com.hufeng.filemanager.ui.FileViewHolder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by feng on 13-10-4.
 */
public class SafeBoxGrouperAdapter extends CursorAdapter implements GridAdapter {

    LayoutInflater mInflater;
    SafeBoxIconLoader mIconLoader;

    GridFragment.DISPLAY_MODE mMode = GridFragment.DISPLAY_MODE.GRID;

    int mItemResourceId = R.layout.file_grid_row;

    private ArrayList<String> mOperationPaths;

    public SafeBoxGrouperAdapter(Context context, Cursor c, ArrayList<String> operationPaths) {
        super(context, c);
        mInflater = LayoutInflater.from(context);
        mIconLoader = SafeBoxIconLoader.getInstance();
        mOperationPaths = operationPaths;
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
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
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
            holder = new FileViewHolder(view);
            view.setTag(holder);
            holder.display = mMode.ordinal();
            holder.progress.setVisibility(View.GONE);
            holder.status.setVisibility(View.GONE);
        }

        String name = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGINAL_PATH);
        String safe_path = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_SAFE_PATH);
        holder.name.setText(name);


        Bitmap bm = mIconLoader.getIcon(safe_path);
        if(bm!=null) {
            holder.icon.setImageBitmap(bm);
        }else {
            byte[] bytes = cursor.getBlob(SafeDataStructs.SafeColumns.FIELD_INDEX_THUMBNAIL);
            if (bytes!=null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                        bytes.length, null);
                holder.icon.setImageBitmap(bitmap);
                mIconLoader.saveIcon(safe_path, bitmap);
            } else {
            }
        }

        String size = FileUtils.getFileSize(new File(safe_path));
        String date = FileUtils.getFileDate(new File(safe_path));
        holder.info.setText(size+" "+date);

        if( mOperationPaths!=null ) {
            if (mOperationPaths.contains(safe_path)) {
                view.setBackgroundResource(R.drawable.list_selector_activated);
            } else {
                view.setBackgroundResource(R.drawable.list_selector_normal);
            }
        } else {
            view.setBackgroundResource(R.drawable.list_selector_normal);
        }
    }


    FileGridAdapterListener mFileGridAdapterListener;

    public void setFileGridAdapterListener(FileGridAdapterListener listener){
        mFileGridAdapterListener = listener;
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
}
