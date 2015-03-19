package com.belugamobile.filemanager.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.belugamobile.filemanager.BelugaArrayRecyclerAdapter;
import com.belugamobile.filemanager.BelugaDisplayMode;
import com.belugamobile.filemanager.BelugaDrawerActivity;
import com.belugamobile.filemanager.BelugaEntryViewHolder;
import com.belugamobile.filemanager.FileRecyclerFragment;
import com.belugamobile.filemanager.FileTabFragment;
import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.dialog.BelugaDialogFragment;
import com.belugamobile.filemanager.utils.LogUtil;

import java.util.List;

public class AppManagerFragment extends FileRecyclerFragment implements LoaderManager.LoaderCallbacks<List<AppEntry>> {

    private static final String LOG_TAG = AppManagerFragment.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private BelugaArrayRecyclerAdapter<AppEntry> mAdapter;

    public AppManagerFragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if( getLoaderManager().getLoader(LOADER_ID)==null)	{
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }else{
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_apk));
//        mAdapter = new AppListAdapter(getActivity());
        mAdapter = new BelugaArrayRecyclerAdapter<AppEntry>(
                getActivity(),
//                R.layout.app_list_row,
                BelugaDisplayMode.LIST,
                new BelugaEntryViewHolder.Builder(){
                    @Override
                    public BelugaEntryViewHolder createViewHolder(ViewGroup parent, int type) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.app_list_row, parent, false);
                        return new AppEntryViewHolder(view);
                    }
                });
        setRecyclerAdapter(mAdapter);
        setEmptyViewShown(false);
        setRecyclerViewShownNoAnimation(false);
        registerForContextMenu(getRecyclerView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterForContextMenu(getRecyclerView());
    }

    // TODO: recover this
//    @Override
//    public void onGridItemClick(GridView g, View v, int position, long id) {
//        super.onGridItemClick(g, v, position, id);
//
//        AppEntry ap = (AppEntry)g.getItemAtPosition(position);
//        AppAction.showInstalledAppDetails(getActivity(), ap.getPackageName());
//    }


    @Override
    public BelugaFileEntry[] getAllFiles() {
        return new BelugaFileEntry[0];
    }


    @Override
    public Loader<List<AppEntry>> onCreateLoader(int arg0, Bundle arg1) {
        LogUtil.i(LOG_TAG, "FileBrowserFragment onCreateLoader " + arg0);
        if(arg0 ==  LOADER_ID) {
            return new AppListLoader(getActivity());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<AppEntry>> arg0,
                               List<AppEntry> arg1) {
        LogUtil.i(LOG_TAG, "onLoadFinished with length =  " + (arg1 == null ? 0 : arg1.size()));
        mAdapter.setData(arg1);
        setRecyclerViewShown(true);
        setEmptyViewShown(arg1.size()==0);
    }

    @Override
    public void onLoaderReset(Loader<List<AppEntry>> arg0) {
        mAdapter.setData(null);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_manager_fragment_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem displayMenu = menu.findItem(R.id.menu_app_display);
        final MenuItem sortMenu = menu.findItem(R.id.menu_app_sort);

        final Fragment parentFragment = getParentFragment();
        boolean isFragmentVisible = true;
        if(parentFragment != null && (parentFragment instanceof FileTabFragment)) {
            isFragmentVisible = parentFragment.getUserVisibleHint();
        }
        final Activity parentActivity = getActivity();
        boolean isSearchMode = false;
        if (parentActivity != null && (parentActivity instanceof BelugaDrawerActivity)) {
            isSearchMode = ((BelugaDrawerActivity)getActivity()).isSearchMode();
        }

        final boolean menuVisible = isFragmentVisible && !isSearchMode;
        displayMenu.setVisible(menuVisible);
        sortMenu.setVisible(menuVisible);


        displayMenu.setIcon(getDisplayMode() == BelugaDisplayMode.LIST ?
                R.drawable.ic_action_view_as_list : R.drawable.ic_action_view_as_grid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_app_display:
                switchDisplay();
                return true;
            case R.id.menu_app_sort:
                BelugaDialogFragment.showAppSortDialog(getActivity());
                return true;
//            case R.id.menu_app_sort_by_size:
//                BelugaSorter.saveFileSorter(getActivity(), CategorySelectEvent.CategoryType.APP, new BelugaSorter.SORTER(BelugaSorter.SORT_FIELD.SIZE, BelugaSorter.SORT_ORDER.DESC));
//                getLoaderManager().restartLoader(LOADER_ID, null, this);
//                return true;
//            case R.id.menu_app_sort_by_date:
//                BelugaSorter.saveFileSorter(getActivity(), CategorySelectEvent.CategoryType.APP, new BelugaSorter.SORTER(BelugaSorter.SORT_FIELD.DATE, BelugaSorter.SORT_ORDER.DESC));
//                getLoaderManager().restartLoader(LOADER_ID, null, this);
//                return true;
//            case R.id.menu_app_sort_by_name:
//                BelugaSorter.saveFileSorter(getActivity(), CategorySelectEvent.CategoryType.APP, new BelugaSorter.SORTER(BelugaSorter.SORT_FIELD.NAME, BelugaSorter.SORT_ORDER.ASC));
//                getLoaderManager().restartLoader(LOADER_ID, null, this);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}




