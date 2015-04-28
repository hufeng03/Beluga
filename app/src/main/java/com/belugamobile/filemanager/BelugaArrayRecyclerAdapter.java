package com.belugamobile.filemanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.belugamobile.filemanager.data.BelugaFileEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng Hu on 15-01-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaArrayRecyclerAdapter<Entry extends BelugaEntry>
        extends RecyclerView.Adapter<BelugaEntryViewHolder> implements BelugaDisplayModeAdapter{

    private Context mContext;
    private List<Entry> mEntries;
//    private int mEntryLayout;
    private String mHighlightString;
    private BelugaEntryViewHolder.Builder mEntryViewHolderBuilder;

    private BelugaDisplayMode mDisplayMode = BelugaDisplayMode.LIST;

    public BelugaArrayRecyclerAdapter(Context context, BelugaDisplayMode mode,/*int entryLayout,*/ BelugaEntryViewHolder.Builder builder) {
        init(context, new ArrayList<Entry>(), mode, /*entryLayout,*/ builder);
    }

    private void init(Context context, ArrayList<Entry> entries, BelugaDisplayMode mode,/*int entryLayout,*/ BelugaEntryViewHolder.Builder builder) {
        mContext = context;
        mEntries = entries;
//        mEntryLayout = entryLayout;
        mDisplayMode = mode;
        mEntryViewHolderBuilder = builder;
    }

    @Override
    public BelugaEntryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return mEntryViewHolderBuilder.createViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(BelugaEntryViewHolder fileViewHolder, int i) {
        fileViewHolder.bindEntry(mEntries.get(i), mHighlightString);
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
        if (pos < mEntries.size()) {
            mEntries.add(pos, newEntry);
        } else {
            mEntries.add(newEntry);
        }
        notifyItemInserted(pos);
    }

    public void remove(BelugaFileEntry entry) {
        int position = mEntries.indexOf(entry);
        if (position != -1) {
            mEntries.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setData(final List<Entry> entries) {
        mEntries = entries;
        notifyDataSetChanged();
    }

    public void clear() {
        int count = getItemCount();
        mEntries.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void setHighlight(String highlightString) {
        mHighlightString = highlightString;
    }

//    public void addAll(final List<Entry> entries) {
//        if (entries != null) {
//            int count = getItemCount();
//            mEntries.addAll(entries);
//            notifyItemRangeInserted(count, entries.size());
//        }
//    }

    public BelugaFileEntry[] getAll() {
        return mEntries.toArray(new BelugaFileEntry[mEntries.size()]);
    }

    public boolean contains(BelugaFileEntry entry) {
        return mEntries.contains(entry);
    }

}
