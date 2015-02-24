package com.hufeng.filemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.browser.FileEntry;

/**
 * Created by feng on 14-5-6.
 */
public class SearchFragment extends FileRecyclerFragment implements BelugaEntryViewHolder.EntryClickListener{

    private static final String LOG_TAG = "SearchFragment";

    private String mSearchString = null;

    private static final int LOADER_ID = 1;

    BelugaArrayRecyclerAdapter<FileEntry, FileEntryListViewHolder> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String empty_text = getResources().getString(R.string.empty_file);
        setEmptyText(empty_text);

        mAdapter = new BelugaArrayRecyclerAdapter<FileEntry, FileEntryListViewHolder>(
                getActivity(),
                BelugaDisplayMode.LIST,
                new BelugaEntryViewHolder.Builder() {
                    @Override
                    public BelugaEntryViewHolder createViewHolder(ViewGroup parent, int type) {

                        if (type == BelugaDisplayMode.GRID.ordinal()) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_grid_row, parent, false);
                            return new FileEntryGridViewHolder(view, getActionController(), SearchFragment.this);
                        } else {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_list_row, parent, false);
                            return new FileEntryListViewHolder(view, getActionController(), SearchFragment.this);
                        }

                    }
                });

        setRecyclerAdapter(mAdapter);

        setEmptyViewShown(false);
        setListShownNoAnimation(false);
    }

//    @Override
//    public FileEntry getParentFile() {
//        return null;
//    }

    @Override
    public FileEntry[] getAllFiles() {
        return new FileEntry[0];
    }

    @Override
    public void onEntryClickedToOpen(View view, BelugaEntry entry) {

    }
}
