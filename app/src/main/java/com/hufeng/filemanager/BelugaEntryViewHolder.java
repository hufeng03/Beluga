package com.hufeng.filemanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.browser.FileEntry;

/**
 * Created by Feng Hu on 15-01-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public abstract class BelugaEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    protected Context context;

    public BelugaEntryViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public abstract void bindEntry(BelugaEntry entry);

    protected void bindEntry(Cursor entry) {

    }

    public interface Builder {
        public BelugaEntryViewHolder createViewHolder(ViewGroup parent, int type);
    }

    public static interface EntryClickListener {
        public abstract void onEntryClickedToOpen(View view, BelugaEntry entry);
    }
}

