package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.data.FileEntry;

import java.util.List;

/**
 * Created by feng on 14-5-6.
 */
public class FileSearchFragment extends FileRecyclerFragment implements
        LoaderManager.LoaderCallbacks<List<FileEntry>>,
        BelugaEntryViewHolder.EntryClickListener{

    private static final String TAG = "SearchFragment";

    private String mSearchString = null;

    private static final int LOADER_ID = 1;

    BelugaArrayRecyclerAdapter<FileEntry, FileEntryListViewHolder> mAdapter;

    public static FileSearchFragment newFragment(String searchString) {
        FileSearchFragment fragment = new FileSearchFragment();
        fragment.mSearchString = searchString;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
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
                            return new FileEntryGridViewHolder(view, getActionController(), FileSearchFragment.this);
                        } else {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_list_row, parent, false);
                            return new FileEntryListViewHolder(view, getActionController(), FileSearchFragment.this);
                        }

                    }
                });

        setRecyclerAdapter(mAdapter);

        setEmptyViewShown(false);
        setListShownNoAnimation(false);
    }

    public void performSearch(String searchString) {
        if (mSearchString == null || !mSearchString.equalsIgnoreCase(searchString)) {
            mSearchString = searchString;
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<FileEntry>> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        if(arg0 ==  LOADER_ID) {
            return new FileSearchLoader(getActivity(), mSearchString);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<FileEntry>> arg0,
                               List<FileEntry> arg1) {
        Log.i(TAG, "onLoadFinished");

        mAdapter.setHighlight(mSearchString);
        mAdapter.setData(arg1);

        setRecyclerViewShown(true);

        if (TextUtils.isEmpty(mSearchString)) {
            setEmptyText(getResources().getString(R.string.no_search_string));
        } else {
            setEmptyText(getResources().getString(R.string.empty_file));
        }

        setEmptyViewShown(arg1.size()==0);
    }

    @Override
    public void onLoaderReset(Loader<List<FileEntry>> arg0) {
        Log.i(TAG, "onCreateReset");
        mAdapter.clear();
    }

    @Override
    public void onEntryClickedToOpen(View view, BelugaEntry entry) {
        FileEntry fileEntry = (FileEntry)entry;
        if (fileEntry.isDirectory) {
            //TODO: switch to show child folder
            BusProvider.getInstance().post(new FolderOpenEvent(System.currentTimeMillis(), fileEntry));
        } else {
            BelugaActionDelegate.view(view.getContext(), fileEntry);
        }
    }


    @Override
    public FileEntry[] getAllFiles() {
        return mAdapter.getAll();
    }
}
