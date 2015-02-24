package com.hufeng.filemanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.browser.BelugaSorter;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.BelugaEntryViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Feng Hu on 15-01-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaArrayRecyclerAdapter<Entry extends BelugaEntry,
        EntryViewHolder extends BelugaEntryViewHolder>
        extends RecyclerView.Adapter<EntryViewHolder> implements BelugaDisplayModeAdapter{

    private Context mContext;
    private List<Entry> mEntries;
//    private int mEntryLayout;
    private EntryViewHolder.Builder mEntryViewHolderBuilder;

    private BelugaDisplayMode mDisplayMode = BelugaDisplayMode.LIST;

    public BelugaArrayRecyclerAdapter(Context context, BelugaDisplayMode mode,/*int entryLayout,*/ EntryViewHolder.Builder builder) {
        init(context, new ArrayList<Entry>(), mode, /*entryLayout,*/ builder);
    }

    private void init(Context context, ArrayList<Entry> entries, BelugaDisplayMode mode,/*int entryLayout,*/ EntryViewHolder.Builder builder) {
        mContext = context;
        mEntries = entries;
//        mEntryLayout = entryLayout;
        mDisplayMode = mode;
        mEntryViewHolderBuilder = builder;
    }

    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return (EntryViewHolder)mEntryViewHolderBuilder.createViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(EntryViewHolder fileViewHolder, int i) {
        fileViewHolder.bindEntry(mEntries.get(i));
    }

    @Override
    public int getItemCount() {
        return mEntries == null ? 0 : mEntries.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDisplayMode.ordinal();
    }

    @Override
    public void switchDisplayMode() {
        if (mDisplayMode == BelugaDisplayMode.LIST) {
            mDisplayMode = BelugaDisplayMode.GRID;
        } else {
            mDisplayMode = BelugaDisplayMode.LIST;
        }
    }

    @Override
    public BelugaDisplayMode getDisplayMode() {
        return mDisplayMode;
    }

    public void add(Entry newEntry, int pos) {
        mEntries.add(pos, newEntry);
        notifyItemInserted(pos);
    }

    public void remove(FileEntry entry) {
        int position = mEntries.indexOf(entry);
        if (position != -1) {
            mEntries.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setData(final List<Entry> entries) {
//        clear();
//        addAll(entries);
        mEntries = entries;
        notifyDataSetChanged();
    }

    public void clear() {
        int count = getItemCount();
        mEntries.clear();
        notifyItemRangeRemoved(0, count);
    }

//    public void addAll(final List<Entry> entries) {
//        if (entries != null) {
//            int count = getItemCount();
//            mEntries.addAll(entries);
//            notifyItemRangeInserted(count, entries.size());
//        }
//    }

    public FileEntry[] getAll() {
        return mEntries.toArray(new FileEntry[mEntries.size()]);
    }

}
