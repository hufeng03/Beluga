package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.GridView;

import com.hufeng.filemanager.app.AppEntry;
import com.hufeng.filemanager.app.AppListAdapter;
import com.hufeng.filemanager.app.AppListLoader;
import com.hufeng.filemanager.browser.AppAction;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.utils.LogUtil;

import java.util.List;

public class AppManagerFragment extends FileGridFragment implements LoaderManager.LoaderCallbacks<List<AppEntry>> {

    private static final String LOG_TAG = AppManagerFragment.class.getSimpleName();

    private static final int LOADER_ID_APPS = 202;

    private AppListAdapter mAdapter;

    public AppManagerFragment(){
        super();
        mMenuId = R.menu.app_manager_fragment_menu;
//        mLoaderId = LOADER_ID_APPS;
        mCategory = FileUtils.FILE_TYPE_APP;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if( getLoaderManager().getLoader(LOADER_ID_APPS)==null)	{
            getLoaderManager().initLoader(LOADER_ID_APPS, null, this);
        }else{
            getLoaderManager().restartLoader(LOADER_ID_APPS, null, this);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_apk));
        mAdapter = new AppListAdapter(getActivity());
        setGridAdapter(mAdapter);
        setGridShownNoAnimation(false);
        registerForContextMenu(getGridView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterForContextMenu(getGridView());
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        super.onGridItemClick(g, v, position, id);

        AppEntry ap = (AppEntry)g.getItemAtPosition(position);
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        SharedPreferences.Editor edit = sp.edit();
//        edit.putString("APP_MANAGER_SELECT", ap.getPackageName());
//        edit.commit();
        AppAction.showInstalledAppDetails(getActivity(), ap.getPackageName());

    }

    @Override
    public String getParentFile() {
        return null;
    }

    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public void reloadFiles() {
        getLoaderManager().restartLoader(LOADER_ID_APPS, null, this);
    }


    @Override
    public Loader<List<AppEntry>> onCreateLoader(int arg0, Bundle arg1) {
        LogUtil.i(LOG_TAG, "FileBrowserFragment onCreateLoader " + arg0);
        if(arg0 ==  LOADER_ID_APPS) {
            return new AppListLoader(getActivity(), mSearchString);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<AppEntry>> arg0,
                               List<AppEntry> arg1) {
        LogUtil.i(LOG_TAG, "onLoadFinished with length =  " + (arg1 == null ? 0 : arg1.size()));
        mAdapter.setData(arg1);
        setGridShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<AppEntry>> arg0) {
        mAdapter.setData(null);
    }

}




