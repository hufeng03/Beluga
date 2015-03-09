package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.data.BelugaZipElementEntry;
import com.hufeng.filemanager.loader.ZipBrowserLoader;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Feng Hu on 15-03-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class ZipBrowserFragment extends FileRecyclerFragment implements LoaderManager.LoaderCallbacks<List<BelugaZipElementEntry>>,
        BelugaEntryViewHolder.EntryClickListener{

    private static final String TAG = "ZipBrowserFragment";

    private static final int LOADER_ID = 1;

    private String mRootDir;
    private String mSelectedPath;

    BelugaArrayRecyclerAdapter<BelugaZipElementEntry> mAdapter;

    public static final String ARGUMENT_ZIP_PATH = "zip_path";

    public static ZipBrowserFragment newFragment(String zipPath) {
        ZipBrowserFragment fragment = new ZipBrowserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_ZIP_PATH, zipPath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mRootDir = arguments.getString(ARGUMENT_ZIP_PATH);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(mRootDir);

        mAdapter = new BelugaArrayRecyclerAdapter<BelugaZipElementEntry>(
                getActivity(),
                BelugaDisplayMode.LIST,
                new BelugaEntryViewHolder.Builder() {
                    @Override
                    public BelugaEntryViewHolder createViewHolder(ViewGroup parent, int type) {

                        if (type == BelugaDisplayMode.GRID.ordinal()) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.zip_element_grid_row, parent, false);
                            return new ZipElementEntryGridViewHolder(view, getActionController(), ZipBrowserFragment.this);
                        } else {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.zip_element_list_row, parent, false);
                            return new ZipElementEntryListViewHolder(view, getActionController(), ZipBrowserFragment.this);
                        }

                    }
                });

        setRecyclerAdapter(mAdapter);

        setEmptyViewShown(false);
        setRecyclerViewShownNoAnimation(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onBackPressed() {
        if (mRootDir != null) {
            String initDir = getArguments().getString(ARGUMENT_ZIP_PATH);
            if (!TextUtils.isEmpty(initDir)) {
                if (mRootDir.equals(initDir)) {
                    return false;
                }
            }

            int idx = mRootDir.lastIndexOf("/");
            if (idx > 0) {
                String parent = mRootDir.substring(0, idx);
                if (!TextUtils.isEmpty(parent)) {
                    showDir(parent);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Loader<List<BelugaZipElementEntry>> onCreateLoader(int i, Bundle bundle) {
        Log.i(TAG, "onCreateLoader");
        if(i ==  LOADER_ID) {
            return new ZipBrowserLoader(getActivity(), getArguments().getString(ARGUMENT_ZIP_PATH), mRootDir);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<BelugaZipElementEntry>> listLoader, List<BelugaZipElementEntry> zipElementEntries) {
        Log.i(TAG, "onLoadFinished");
        int pos = -1;
        if (!TextUtils.isEmpty(mSelectedPath)) {
            Iterator<BelugaZipElementEntry> iterator =  zipElementEntries.iterator();
            while (iterator.hasNext()) {
                pos++;
                BelugaZipElementEntry entry = iterator.next();
                if (entry.path.equals(mSelectedPath)){
                    break;
                }
            }
        }

        mAdapter.setData(zipElementEntries);
        if ( pos > 5 ) {
            getLayoutManager().scrollToPosition(pos);
        }

        mSelectedPath = null;
        setRecyclerViewShown(true);

        setEmptyViewShown(zipElementEntries.size()==0);
    }

    @Override
    public void onLoaderReset(Loader<List<BelugaZipElementEntry>> listLoader) {
        Log.i(TAG, "onCreateReset");
        mAdapter.clear();
    }

    public void showDir(String path) {
        mSelectedPath = mRootDir;
        mRootDir = path;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public BelugaFileEntry[] getAllFiles() {
        return new BelugaFileEntry[0];
    }

    @Override
    public void onEntryClickedToOpen(View view, BelugaEntry entry) {
        if (((BelugaZipElementEntry)entry).isDirectory) {
            showDir(((BelugaZipElementEntry) entry).path);
        }
    }
}
