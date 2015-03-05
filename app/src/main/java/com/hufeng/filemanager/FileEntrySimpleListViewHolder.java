package com.hufeng.filemanager;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.playimage.BelugaLazyLoadImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Feng Hu on 15-01-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileEntrySimpleListViewHolder extends BelugaEntryViewHolder{

    @InjectView(R.id.icon)
    BelugaLazyLoadImageView icon;
    @InjectView(R.id.name) TextView name;

    private FileEntry entry;

    public FileEntrySimpleListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    @Override
    public void bindEntry(BelugaEntry entry) {
        if (!(entry instanceof FileEntry)) {
            //throw new Exception("AppEntryViewHolder can only bind AppEntry");
        }
        this.entry = (FileEntry)entry;
        name.setText(this.entry.getName());
        icon.requestDisplayImage(this.entry.path);
    }

    @Override
    public void bindEntry(Cursor cursor) {
        FileEntry entry = new FileEntry(cursor);
        bindEntry(entry);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

}
