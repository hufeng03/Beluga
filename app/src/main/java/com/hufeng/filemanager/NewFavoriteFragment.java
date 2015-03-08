package com.hufeng.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;
import com.hufeng.filemanager.helper.FileCategoryHelper;

import java.util.List;

/**
 * Created by Feng Hu on 15-02-19.
 * <p/>
 * TODO: Add a class header comment.
 */
public class NewFavoriteFragment extends FileRecyclerFragment implements LoaderManager.LoaderCallbacks<List<FileEntry>>,
        BelugaEntryViewHolder.EntryClickListener {

    private static final String TAG = "NewFavoriteFragment";

    private static final int LOADER_ID = 1;

    BelugaArrayRecyclerAdapter<FileEntry, FileEntryListViewHolder> mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String empty_text = getResources().getString(R.string.empty_favorite);
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
                            return new FileEntryGridViewHolder(view, getActionController(), NewFavoriteFragment.this);
                        } else {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_list_row, parent, false);
                            return new FileEntryListViewHolder(view, getActionController(), NewFavoriteFragment.this);
                        }

                    }
                });

        setRecyclerAdapter(mAdapter);

        setEmptyViewShown(false);
        setListShownNoAnimation(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getUserVisibleHint()) {
            inflater.inflate(R.menu.file_browser_fragment_menu, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!getUserVisibleHint()) {
            return;
        }
        final MenuItem displayMenu = menu.findItem(R.id.menu_browser_display);
        final MenuItem sortMenu = menu.findItem(R.id.menu_browser_sort);
        final MenuItem upMenu = menu.findItem(R.id.menu_up);

        boolean isFragmentVisible = getUserVisibleHint();
        final Activity parentActivity = getActivity();
        boolean isSearchMode = false;
        if (parentActivity != null && (parentActivity instanceof BelugaDrawerActivity)) {
            isSearchMode = ((BelugaDrawerActivity)getActivity()).isSearchMode();
        }

        final boolean menuVisible = isFragmentVisible && !isSearchMode;

        displayMenu.setVisible(menuVisible);
        sortMenu.setVisible(menuVisible);
        upMenu.setVisible(false);

        displayMenu.setIcon(getDisplayMode() == BelugaDisplayMode.LIST ?
                R.drawable.ic_action_view_as_grid : R.drawable.ic_action_view_as_list);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!getUserVisibleHint()) {
            return false;
        }
        switch(item.getItemId()){
            case R.id.menu_browser_display:
                switchDisplay();
                getActivity().supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_browser_sort:
                BelugaDialogFragment.showSortDialog(getActivity(), FileCategoryHelper.CATEGORY_TYPE_FAVORITE);
                return true;
            case R.id.menu_up:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<List<FileEntry>> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        if(arg0 ==  LOADER_ID) {
            return new FavoriteListLoader(getActivity());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<FileEntry>> arg0,
                               List<FileEntry> arg1) {
        Log.i(TAG, "onLoadFinished");

        mAdapter.setData(arg1);

        setRecyclerViewShown(true);
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
    public void refreshUI() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public FileEntry[] getAllFiles() {
        return mAdapter.getAll();
    }

}
