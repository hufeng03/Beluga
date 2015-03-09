package com.hufeng.filemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hufeng.filemanager.data.BelugaFileEntry;

import java.util.ArrayList;

/**
 * Created by Feng Hu on 15-01-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaCursorRecyclerAdapter extends RecyclerView.Adapter<BelugaEntryViewHolder>
    implements BelugaDisplayModeAdapter{

    private Context mContext;
    private Cursor mCursor;

    private boolean mDataValid;
    private int mRowIDColumn;

    private BelugaEntryViewHolder.Builder mEntryViewHolderBuilder;

    private DataSetObserver mDataSetObserver;

    private String mHighlightString;


    private BelugaDisplayMode mDisplayMode = BelugaDisplayMode.LIST;

    public BelugaCursorRecyclerAdapter(Context context, Cursor cursor, BelugaDisplayMode mode,/*int entryLayout,*/ BelugaEntryViewHolder.Builder builder) {
        init(context, cursor, mode, builder);
    }

    private void init(Context context, Cursor cursor, BelugaDisplayMode mode,/*int entryLayout,*/ BelugaEntryViewHolder.Builder builder) {
        boolean cursorExists = cursor != null;
        mContext = context;
        mCursor = cursor;
        mDisplayMode = mode;
        mEntryViewHolderBuilder = builder;
        mRowIDColumn = cursorExists ? cursor.getColumnIndexOrThrow("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    @Override
    public BelugaEntryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return mEntryViewHolderBuilder.createViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(BelugaEntryViewHolder belugaEntryViewHolder, int i) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(i)) {
            throw new IllegalStateException("couldn't move cursor to position "+i);
        }
        belugaEntryViewHolder.bindEntry(mCursor, mHighlightString);
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
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

    /**
     * Returns the cursor.
     * @return the cursor.
     */
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * @see android.widget.ListAdapter#getCount()
     */
    public int getCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * @see android.widget.ListAdapter#getItem(int)
     */
    public Object getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        } else {
            return null;
        }
    }

    public BelugaFileEntry[] getAll() {
        Cursor cursor = getCursor();
        ArrayList<BelugaFileEntry> files = new ArrayList<BelugaFileEntry>();
        if(cursor!=null && cursor.moveToFirst()) {
            do {
                files.add(new BelugaFileEntry(cursor));
            } while(cursor.moveToNext());
        }
        return files.toArray(new BelugaFileEntry[files.size()]);
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mCursor = newCursor;
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
        }
    }
}
