package com.hufeng.filemanager;

import android.app.DownloadManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.hufeng.filemanager.browser.AppAction;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.resource.FileDownloader;
import com.hufeng.filemanager.resource.ResourceEntry;
import com.hufeng.filemanager.resource.ResourceListAdapter;
import com.hufeng.filemanager.resource.ResourceListDownloader;
import com.hufeng.filemanager.resource.ResourceListLoader;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.NetworkUtil;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.List;


/**
 * Created by feng on 13-9-29.
 */
public class ResourceFragment extends FileGridFragment implements LoaderManager.LoaderCallbacks<List<ResourceEntry>> {

    public static final String RESOURCE_FRAGMENT_ARGUMENT_TYPE = "resource_fragment_argument_type";

    public ResourceFragment(){
        mMenuId = R.menu.selected_game_fragment_menu;
        mCategory = FileUtils.FILE_TYPE_RESOURCE_ALL;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int type_ordinal = arguments.getInt(RESOURCE_FRAGMENT_ARGUMENT_TYPE, -1);
            ResourceType type = ResourceType.valueOf(type_ordinal);
            if (type != null) {
                switch (type) {
                    case DOC:
                        mCategory = FileUtils.FILE_TYPE_RESOURCE_DOC;
                        break;
                    case APP:
                        mCategory = FileUtils.FILE_TYPE_RESOURCE_APP;
                        break;
                    case GAME:
                        mCategory = FileUtils.FILE_TYPE_RESOURCE_GAME;
                        break;
                    default:
                        mCategory = FileUtils.FILE_TYPE_RESOURCE_ALL;
                        break;
                }
            }
        }
    }

    private ResourceListAdapter mAdapter;


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( getLoaderManager().getLoader(LoaderIDs.getLoaderId(mCategory))==null)	{
            getLoaderManager().initLoader(LoaderIDs.getLoaderId(mCategory), null, this);
        }else{
            getLoaderManager().restartLoader(LoaderIDs.getLoaderId(mCategory), null, this);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (FileUtils.FILE_TYPE_RESOURCE_GAME == mCategory) {
            setEmptyText(getResources().getString(R.string.empty_selected_game));
        } else if(FileUtils.FILE_TYPE_RESOURCE_APP == mCategory) {
            setEmptyText(getResources().getString(R.string.empty_selected_app));
        } else if(FileUtils.FILE_TYPE_RESOURCE_DOC == mCategory) {
            setEmptyText(getResources().getString(R.string.empty_selected_doc));
        } else {
            setEmptyText(getResources().getString(R.string.empty_selected_resource));
        }
        mAdapter = new ResourceListAdapter(getActivity());
        setGridAdapter(mAdapter);
        setGridShownNoAnimation(false);
        registerForContextMenu(getGridView());

        if(true){
            ResourceListDownloader downloader = new ResourceListDownloader(getActivity().getApplicationContext());
            //  downloader.setGameListDownloaderListener(this);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                downloader.execute();
            } else {
                downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

//        FileDownloader.addFileDownloaderListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        FileDownloader.removeFileDownloaderListener(this);
        unregisterForContextMenu(getGridView());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!mMenuCreated) {
            return;
        }
        MenuItem item = menu.findItem(R.id.menu_back);
        if(getParentFragment()==null) {
            if (item != null) {
                item.setVisible(false);
            }
        } else {
            if (item != null) {
                item.setVisible(true);
            }
        }
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        super.onGridItemClick(g, v, position, id);
        ResourceEntry entry = (ResourceEntry)g.getAdapter().getItem(position);
        if(entry.needDownload()) {
            if(FileDownloader.isDownloading(entry.download_url)) {
                FileDownloader.cancelDownloader(entry.download_url);
            } else {
                String name = entry.getName();
                if (ResourceType.valueOf(entry.resource_category) == ResourceType.DOC && !entry.download_url.endsWith(".apk")) {
                    name = entry.package_name+"_"+entry.server_version_code;
                }

                String dir_name = ResourceListLoader.SELECTED_APP_DIR_NAME;
                if(ResourceType.valueOf(entry.resource_category) == ResourceType.GAME) {
                    dir_name = ResourceListLoader.SELECTED_GAME_DIR_NAME;
                } else if(ResourceType.valueOf(entry.resource_category) == ResourceType.DOC) {
                    dir_name = ResourceListLoader.SELECTED_DOC_DIR_NAME;
                }
                String primary_stor = StorageManager.getInstance(FileManager.getAppContext()).getPrimaryExternalStorage();
                String dir_path = new File(primary_stor, dir_name).getAbsolutePath();
                if (!NetworkUtil.isNetworkConnected(g.getContext())) {
                    Toast.makeText(getActivity(), R.string.no_network_check_network, Toast.LENGTH_SHORT).show();
                } else {
                    FileDownloader.downloadFile(g.getContext(), entry.download_url, dir_path, name);
                }
            }
        } else if(entry.isInstalled()) {
                AppAction.launchApp(g.getContext(), entry.package_name);
        } else if(!TextUtils.isEmpty(entry.path) && new File(entry.path).exists()) {
            FileAction.viewFile(g.getContext(), entry.path);
        }
    }


    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public String getParentFile() {
        return null;
    }

    @Override
    public void reloadFiles() {
        getLoaderManager().restartLoader(LoaderIDs.getLoaderId(mCategory), null, this);
    }


    @Override
    public Loader<List<ResourceEntry>> onCreateLoader(int i, Bundle bundle) {
        return new ResourceListLoader(getActivity(), mSearchString, mCategory);
    }

    @Override
    public void onLoadFinished(Loader<List<ResourceEntry>> listLoader, List<ResourceEntry> gameEntries) {
        mAdapter.setData(gameEntries);
        setGridShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<ResourceEntry>> listLoader) {
        mAdapter.setData(null);
    }


    @Subscribe
    public void onFileDownloadEvent(FileDownloadEvent event) {
        if (event.url.startsWith("http://www.kanbox.com/")) {
            return;
        }
        String path = event.path;
        String name = "";
        if(!TextUtils.isEmpty(path)) {
            name = new File(path).getName();
            if(name.endsWith("_tmp")) {
                name = name.substring(0,name.length()-4);
            }
        }
        if (event.status == DownloadManager.STATUS_SUCCESSFUL) {
            //success
            if (!TextUtils.isEmpty(name)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.apk_download_success, name), Toast.LENGTH_SHORT).show();
            }
            reloadFiles();
        } else if (event.status == DownloadManager.STATUS_PAUSED) {
            //paused网络原因
            mAdapter.notifyDataSetChanged();
        } else if (event.status == DownloadManager.STATUS_FAILED) {
            //failed
            if (!TextUtils.isEmpty(name)) {
                if (event.progress == -100) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.apk_download_cancelled, name), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.apk_download_failed, name), Toast.LENGTH_SHORT).show();
                }
            }
            reloadFiles();
        } else {
            //progress
            mAdapter.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onFileDownloading(String url, String path, int progress) {
//
//    }
//
//    @Override
//    public void onFileDownloaded(String url, String path, int status) {
//        Activity act = getActivity();
//        String name = "";
//        if(!TextUtils.isEmpty(path)) {
//            name = new File(path).getName();
//            if(name.endsWith("_tmp")) {
//                name = name.substring(0,name.length()-4);
//            }
//        }
//        if(act!=null) {
//            if(status == FileDownloader.STATUS.SUCCESS.ordinal()) {
//
//            } else if(status == FileDownloader.STATUS.PAUSED.ordinal()){
//                Toast.makeText(act, getResources().getString(R.string.apk_download_paused, name),Toast.LENGTH_SHORT).show();
//            } else if(status == FileDownloader.STATUS.FAILED.ordinal()) {
//
//            }
//        }
//        reloadFiles();
//    }

}
