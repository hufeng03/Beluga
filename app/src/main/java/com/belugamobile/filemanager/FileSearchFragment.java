package com.belugamobile.filemanager;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.FileCategoryHelper;

import java.util.List;

/**
 * Created by feng on 14-5-6.
 */
public class FileSearchFragment extends FileRecyclerFragment implements
        LoaderManager.LoaderCallbacks<List<BelugaFileEntry>>,
        BelugaEntryViewHolder.EntryClickListener{

    private static final String TAG = "SearchFragment";

    private String mSearchString = null;

    private static final int LOADER_ID = 1;

    BelugaArrayRecyclerAdapter<BelugaFileEntry> mAdapter;

    public static final String ARGUMENT_SEARCH_STRING = "search_string";

    private static final String SAVE_INSTANCE_SEARCH_STRING = "searchString";

    public static FileSearchFragment newFragment(String searchString) {
        FileSearchFragment fragment = new FileSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_SEARCH_STRING, searchString);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                mSearchString = arguments.getString(ARGUMENT_SEARCH_STRING);
            }
        } else {
            mSearchString = savedInstanceState.getString(SAVE_INSTANCE_SEARCH_STRING, null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSearchString)) {
            outState.putString(SAVE_INSTANCE_SEARCH_STRING, mSearchString);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (TextUtils.isEmpty(mSearchString)) {
            setEmptyText(getResources().getString(R.string.no_search_string));
        } else {
            setEmptyText(getResources().getString(R.string.empty_file));
        }

        mAdapter = new BelugaArrayRecyclerAdapter<BelugaFileEntry>(
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
        setRecyclerViewShownNoAnimation(false);
    }

    public void performSearch(String searchString) {
        if (mSearchString == null || !mSearchString.equalsIgnoreCase(searchString)) {
            mSearchString = searchString;
            setEmptyViewShown(false);
            setRecyclerViewShown(false);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<BelugaFileEntry>> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        if(arg0 ==  LOADER_ID) {
            return new FileSearchLoader(getActivity(), mSearchString);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<BelugaFileEntry>> arg0,
                               List<BelugaFileEntry> arg1) {
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
    public void onLoaderReset(Loader<List<BelugaFileEntry>> arg0) {
        Log.i(TAG, "onCreateReset");
        mAdapter.clear();
    }

    @Override
    public void onEntryClickedToOpen(View view, BelugaEntry entry) {
        BelugaFileEntry belugaFileEntry = (BelugaFileEntry)entry;
        if (belugaFileEntry.isDirectory) {
            //TODO: switch to show child folder
            BusProvider.getInstance().post(new FolderOpenEvent(System.currentTimeMillis(), belugaFileEntry));
        } else if (belugaFileEntry.type == FileCategoryHelper.FILE_TYPE_ZIP) {
            BusProvider.getInstance().post(new ZipSelectEvent(System.currentTimeMillis(), ((BelugaFileEntry) entry).path));
        } else {
            BelugaActionDelegate.view(view.getContext(), belugaFileEntry);
        }
    }


    @Override
    public BelugaFileEntry[] getAllFiles() {
        return mAdapter.getAll();
    }
}
