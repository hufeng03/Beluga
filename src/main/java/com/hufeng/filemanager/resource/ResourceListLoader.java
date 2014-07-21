package com.hufeng.filemanager.resource;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.hufeng.filemanager.ResourceType;
import com.hufeng.filemanager.app.PackageIntentReceiver;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by feng on 13-9-20.
 */
public class ResourceListLoader extends AsyncTaskLoader<List<ResourceEntry>> {

    public static final String SELECTED_APP_DIR_NAME = "selected_app";
    public static final String SELECTED_GAME_DIR_NAME = "selected_game";
    public static final String SELECTED_DOC_DIR_NAME = "selected_ebook";
    private static final String LOG_TAG = ResourceListLoader.class.getSimpleName();

    final PackageManager mPm;

    List<ResourceEntry> mGames;

//    FileSorter.SORT_FIELD mField;
//    FileSorter.SORT_ORDER mOrder;
    String mSearch;

    ResourceType mResourceType;

    ResourceDatabaseObserver mGameObserver;
    PackageIntentReceiver mPackageObserver;

    private Handler mHandler = new Handler();

    public ResourceListLoader(Context context, String search, int category) {
        super(context);
        mSearch = search;

        if (FileUtils.FILE_TYPE_RESOURCE_DOC == category) {
            mResourceType = ResourceType.DOC;
        } else if(FileUtils.FILE_TYPE_RESOURCE_APP == category) {
            mResourceType = ResourceType.APP;
        } else if(FileUtils.FILE_TYPE_RESOURCE_GAME == category) {
            mResourceType = ResourceType.GAME;
        }

        mPm = getContext().getPackageManager();
    }

    @Override
    public List<ResourceEntry> loadInBackground() {
//       android.os.Debug.waitForDebugger();

        StorageManager manager = StorageManager.getInstance(getContext());

//        String[] storages = manager.getMountedStorages();

        String storage = manager.getPrimaryExternalStorage();
        List<ResourceEntry> entries = new ArrayList<ResourceEntry>();
 //       for (String storage : storages) {

            if (ResourceType.GAME == mResourceType || mResourceType == null) {
                File game_dir = new File(storage, SELECTED_GAME_DIR_NAME);
                if (game_dir.exists() && game_dir.isDirectory()) {
                    File[] game_apks = game_dir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            if (s.endsWith(".apk") && (TextUtils.isEmpty(mSearch) || s.contains(mSearch) || s.toLowerCase().contains(mSearch.toLowerCase()))) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                    for (File game : game_apks) {
                        ResourceEntry entry = new ResourceEntry(game.getAbsolutePath(), this);
                        if (entry.package_name != null/* && !entry.isInstalled() || entry.needAppUpgrade()*/) {
                            ListIterator<ResourceEntry> iterator = entries.listIterator();
                            boolean flag = false;
                            while (iterator.hasNext()) {
                                ResourceEntry old_entry = iterator.next();
                                if (entry.package_name.equals(old_entry.package_name)) {
                                    if (entry.version_code <= old_entry.version_code) {
                                        flag = true;
                                        break;
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                            if (!flag) {
                                entries.add(entry);
                            }
                        }
                        LogUtil.i(LOG_TAG, "game path is " + entry.path);
                    }

                }
            }
            if (ResourceType.APP == mResourceType || mResourceType == null) {
                File app_dir = new File(storage, SELECTED_APP_DIR_NAME);
                if (app_dir.exists() && app_dir.isDirectory()) {
                    File[] app_apks = app_dir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            if (s.endsWith(".apk") && (TextUtils.isEmpty(mSearch) || s.contains(mSearch))) {
//                            if(!mGameOnly || s.startsWith("g_")) {
//                                return true;
//                            } else {
                                return true;
//                            }
                            } else {
                                return false;
                            }
                        }
                    });

                    for(File app : app_apks) {
                        ResourceEntry entry = new ResourceEntry(app.getAbsolutePath(), this);
                        if(entry.package_name!=null/* && !entry.isInstalled() || entry.needAppUpgrade()*/) {
                            ListIterator<ResourceEntry> iterator = entries.listIterator();
                            boolean flag = false;
                            while(iterator.hasNext()) {
                                ResourceEntry old_entry = iterator.next();
                                if(entry.package_name.equals(old_entry.package_name)) {
                                    if(entry.version_code<=old_entry.version_code){
                                        flag = true;
                                        break;
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                            if(!flag) {
                                entries.add(entry);
                            }
                        }
                        LogUtil.i(LOG_TAG, "app path is " + entry.path);
                    }

                }
            }
            if (ResourceType.DOC == mResourceType || mResourceType == null) {
                File doc_dir = new File(storage, SELECTED_DOC_DIR_NAME);
                if (doc_dir.exists() && doc_dir.isDirectory()) {
                    File[] docs = doc_dir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String s) {
                            if ( (s.endsWith(".apk") || FileUtils.FILE_TYPE_DOCUMENT == FileUtils.getFileType(s)) && (TextUtils.isEmpty(mSearch) || s.contains(mSearch) || s.toLowerCase().contains(mSearch.toLowerCase()))) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });

                    for (File doc : docs) {
                        ResourceEntry entry = new ResourceEntry(doc.getAbsolutePath(), this);
                        if (entry.package_name != null/* && !entry.isInstalled() || entry.needAppUpgrade()*/) {
                            ListIterator<ResourceEntry> iterator = entries.listIterator();
                            boolean flag = false;
                            while (iterator.hasNext()) {
                                ResourceEntry old_entry = iterator.next();
                                if (entry.package_name.equals(old_entry.package_name)) {
                                    if (entry.version_code <= old_entry.version_code) {
                                        flag = true;
                                        break;
                                    } else {
                                        iterator.remove();
                                    }
                                }
                            }
                            if (!flag) {
                                entries.add(entry);
                            }
                        }
                        LogUtil.i(LOG_TAG, "doc path is " + entry.path);
                    }

                }

            }
 //       }

        //load the server push result
//        android.os.Debug.waitForDebugger();
        String selections = DataStructures.SelectedColumns.APP_CATEGORY_FIELD+"=?";
        String[] selectionArgs = new String[1];
        if (mResourceType == ResourceType.APP) {
            selectionArgs[0] = "0";
        } else if(mResourceType == ResourceType.GAME) {
            selectionArgs[0] = "1";
        } else if (mResourceType == ResourceType.DOC) {
            selectionArgs[0] = "2";
        } else {
            selections = null;
            selectionArgs = null;
        }
//        android.os.Debug.waitForDebugger();
        Cursor cursor = getContext().getContentResolver().query(DataStructures.SelectedColumns.CONTENT_URI, DataStructures.SelectedColumns.SELECTED_PROJECTION, selections, selectionArgs, null);
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                ResourceEntry new_entry = buildResourceEntry(cursor);
                LogUtil.i(LOG_TAG, "entry is "+new_entry);
                if(ResourceType.GAME == mResourceType && new_entry.resource_category != 1) {
                    continue;
                }
                if(ResourceType.APP == mResourceType && new_entry.resource_category != 0) {
                    continue;
                }
                if (ResourceType.DOC == mResourceType && new_entry.resource_category != 2) {
                    continue;
                }
                if(!TextUtils.isEmpty(mSearch) && (!new_entry.name.contains(mSearch) || new_entry.package_name.contains(mSearch))) {
                    continue;
                }
                //if(!entry.isInstalled() || entry.needAppUpgrade()) {
                    ListIterator<ResourceEntry> iterator = entries.listIterator();
                    boolean flag = false;
                    while(iterator.hasNext()) {
                        ResourceEntry old_entry = iterator.next();
                        if(new_entry.package_name.equals(old_entry.package_name)) {
                            old_entry.resource_description = new_entry.resource_description;
                            old_entry.resource_name = new_entry.resource_name;
                            old_entry.resource_icon_url = new_entry.resource_icon_url;
                            old_entry.download_url = new_entry.download_url;
                            if(new_entry.server_version_code>old_entry.version_code){
                                old_entry.resource_upgrade = true;
                                old_entry.version_code = new_entry.version_code;
                                old_entry.version_name = new_entry.version_name;
                                old_entry.resource_server_time = new_entry.resource_server_time;
                                old_entry.resource_icon_url = new_entry.resource_icon_url;
                                old_entry.download_url = new_entry.download_url;
//                                iterator.remove();
                            }
                            flag = true;
                            break;
                        }
                    }
                    if(!flag) {
                        entries.add(new_entry);
                    }
                //}
            }
        }

        // Sort the list.
        FileSorter.SORTER sorter = FileSorter.getFileSorter(getContext(), FileUtils.FILE_TYPE_RESOURCE_GAME);
        Collections.sort(entries, FileSorter.getComparator(sorter.field, sorter.order));
        return entries;
    }

    @Override
    public void deliverResult(List<ResourceEntry> games) {

        List<ResourceEntry> oldGames = games;
        mGames = games;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            if (isReset()) {
                if (mGames != null) {
                    onReleaseResources(games);
                    return;
                }
            }

            super.deliverResult(games);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldGames != null && oldGames!=games) {
            onReleaseResources(oldGames);
        }
    }


    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mGames != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mGames);
        }

        // Start watching for changes in the app data.
        if (mGameObserver == null) {
            mGameObserver = new ResourceDatabaseObserver(this, mHandler);
        }

        // Start watching for changes in the app data.
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }


        if (takeContentChanged() || mGames == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<ResourceEntry> games) {
        super.onCanceled(games);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(games);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mGames != null) {
            onReleaseResources(mGames);
            mGames = null;
        }

        // Stop monitoring for changes.
        if (mGameObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mGameObserver);
            mGameObserver = null;
        }

        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<ResourceEntry> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }

    private ResourceEntry buildResourceEntry(Cursor cursor) {
        ResourceEntry entry = new ResourceEntry();
        entry.download_url = cursor.getString(DataStructures.SelectedColumns.URL_FILED_INDEX);
        entry.resource_name = cursor.getString(DataStructures.SelectedColumns.SERVER_NAME_FIELD_INDEX);
        entry.package_name = cursor.getString(DataStructures.SelectedColumns.PACKAGE_FIELD_INDEX);
        entry.version_code = cursor.getInt(DataStructures.SelectedColumns.VERSION_FIELD_INDEX);
        entry.version_name = cursor.getString(DataStructures.SelectedColumns.VERSION_NAME_FIELD_INDEX);
        entry.resource_server_time = cursor.getLong(DataStructures.SelectedColumns.SERVER_DATE_FIELD_INDEX);
        entry.resource_description = cursor.getString(DataStructures.SelectedColumns.DESCRIPTION_FIELD_INDEX);
        entry.resource_category = cursor.getInt(DataStructures.SelectedColumns.APP_CATEGORY_FIELD_INDEX);
        entry.resource_icon_url = cursor.getString(DataStructures.SelectedColumns.ICON_FIELD_INDEX);
        entry.name = cursor.getString(DataStructures.FileColumns.FILE_NAME_FIELD_INDEX);
        entry.lastModified = cursor.getLong(DataStructures.FileColumns.FILE_DATE_FIELD_INDEX);
        entry.size = cursor.getLong(DataStructures.FileColumns.FILE_SIZE_FIELD_INDEX);
        if(TextUtils.isEmpty(entry.name)) {
            entry.name = entry.resource_name;
        }
        entry.is_directory = false;
        entry.server_version_code = entry.version_code;
        entry.server_version_name = entry.version_name;

        if (entry.download_url.endsWith(".apk")) {
            PackageInfo info = null;
            try {
                info = mPm.getPackageInfo(entry.package_name, PackageManager.GET_UNINSTALLED_PACKAGES);
                if(info!=null) {
                    entry.installed = true;
                    entry.installed_version_code = info.versionCode;
                    entry.installed_version_name = info.versionName;
                    if(entry.installed_version_code < entry.version_code) {
                        entry.app_upgrade = true;
                    } else if (entry.installed_version_code == info.versionCode) {
                        entry.app_same_version = true;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            entry.installed = false;
        }

        LogUtil.i(LOG_TAG, "build entry from database cursor:"+entry);
        return entry;

    }


}
