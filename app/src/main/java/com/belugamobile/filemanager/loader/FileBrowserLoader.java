package com.belugamobile.filemanager.loader;

/**
 * Created by feng on 14-2-15.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.belugamobile.filemanager.BelugaFolderObserver;
import com.belugamobile.filemanager.DisplayHiddenPreferenceReceiver;
import com.belugamobile.filemanager.PreferenceKeys;
import com.belugamobile.filemanager.SortPreferenceReceiver;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaSortHelper;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.mount.MountPointManager;
import com.belugamobile.filemanager.provider.DataStructures;
import com.belugamobile.filemanager.root.BelugaRootManager;
import com.belugamobile.filemanager.utils.LogUtil;
import com.belugamobile.filemanager.utils.MimeUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * A custom Loader that loads all of the installed applications.
 */
public class FileBrowserLoader extends AsyncTaskLoader<List<BelugaFileEntry>> {

    private static final String LOG_TAG = FileBrowserLoader.class.getSimpleName();

    private Context mContext;

    private String mFolder;
    private List<BelugaFileEntry> mFiles;
    private boolean mShowChildFolders;

    SortPreferenceReceiver mSortObserver;
    DisplayHiddenPreferenceReceiver mDisplayHiddenObserver;


    public FileBrowserLoader(Context context, String folder, /*String[] folders,*/ boolean showChildFolders) {
        super(context);
        mContext = context;
        mFolder = folder;
        mShowChildFolders = showChildFolders;
    }

    @Override
    public List<BelugaFileEntry> loadInBackground() {
        LogUtil.i(LOG_TAG, this.hashCode() + " FileListLoader loadinbackground()");

        boolean displayHidden = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(PreferenceKeys.DISPLAY_HIDDEN_ENABLE, false);

        List<BelugaFileEntry> entries = new ArrayList<BelugaFileEntry>();
        if (!TextUtils.isEmpty(mFolder)/* && new File(mRoot).exists() && new File(mRoot).isDirectory()*/) {
            String[] names = new File(mFolder).list();
            if (names == null || names.length == 0) {
                String mountPoints = MountPointManager.getInstance().getRealMountPointPath(mFolder);
                if (TextUtils.isEmpty(mountPoints)) {
                    names = BelugaRootManager.getInstance().listSync(mFolder);
                }
            }
            if (names != null) {
                HashSet<String> failedNames = new HashSet<String>();
                for (String name : names) {
                    if (!name.startsWith(".") || displayHidden) {
                        File file = new File(mFolder, name);
                        if (file.exists()) {
                            BelugaFileEntry entry = new BelugaFileEntry(file);
                            if (mShowChildFolders || !entry.isDirectory) {
                                entries.add(entry);
                            }
                        } else {
                            failedNames.add(name);
                        }
                    }
                }
                if (failedNames.size() > 0) {
                    String[] infos = BelugaRootManager.getInstance().infoListSync(mFolder);
                    if (infos != null) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                        for (String info : infos) {
                            String[] elements = info.split("\\s");
                            if (elements.length >= 6) {
                                String name;
                                int size = 0;
                                Date date = null;
                                if (elements[0].charAt(0) == 'd') {
                                    if (!mShowChildFolders) {
                                        continue;
                                    }
                                    name = elements[elements.length-1];
                                    try {
                                        date = format.parse(elements[elements.length-3]+" "+elements[elements.length-2]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else if (elements[0].charAt(0) == 'l') {
                                    name = elements[elements.length-3];
                                    try {
                                        date = format.parse(elements[elements.length - 5] + " " + elements[elements.length - 4]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    name = elements[elements.length-1];
                                    int idx = 3;
                                    while (idx < elements.length-1) {
                                        try {
                                            size = Integer.parseInt(elements[idx]);
                                            break;
                                        }catch (NumberFormatException e) {
                                            e.printStackTrace();
                                        }
                                        idx++;
                                    }
                                    try {
                                        date = format.parse(elements[elements.length - 3] + " " + elements[elements.length - 2]);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                boolean isDirectory = elements[0].charAt(0) == 'd';
                                boolean isReadable = elements[0].charAt(1) == 'r';
                                boolean isWritable = elements[0].charAt(2) == 'w';

                                if (!TextUtils.isEmpty(name) && date != null && failedNames.contains(name)) {
                                    BelugaFileEntry entry = new BelugaFileEntry();
                                    entry.parentPath = mFolder;
                                    entry.name = name;
                                    entry.path = mFolder + "/" + name;
                                    entry.hidden = name.startsWith(".");
                                    entry.exist = true;
                                    entry.extension = MimeUtil.getExtension(name);
                                    entry.type = FileCategoryHelper.getFileTypeForExtension(entry.extension);
                                    entry.category = FileCategoryHelper.getFileCategoryForExtension(entry.extension);
                                    entry.isDirectory = isDirectory;
                                    entry.isReadable = isReadable;
                                    entry.isWritable = isWritable;
                                    entry.lastModified = date.getTime()/1000;
                                    entry.size = size;
                                    entries.add(entry);
                                }
                            }
                        }
                    }
                    //entries.addAll()
                }
            }
        }

        if (entries.size() > 0) {
            // Retrieve favorite status from database
            StringBuilder whereClause = new StringBuilder();
            String[] whereArgs = new String[entries.size()];
            whereClause.append("?");
            whereArgs[0] = entries.get(0).path;
            for (int i = 1; i < entries.size(); i++) {
                whereClause.append(",?");
                whereArgs[i] = entries.get(i).path;
            }
            String where = DataStructures.FavoriteColumns.PATH + " IN(" + whereClause.toString() + ")";
            ContentResolver cr = getContext().getContentResolver();
            Cursor cursor = null;
            try {
                cursor = cr.query(DataStructures.FavoriteColumns.CONTENT_URI, new String[]{DataStructures.FavoriteColumns.PATH}, where, whereArgs, null);
                HashSet<String> favoritePaths = new HashSet<String>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    favoritePaths.add(path);
                }
                for (int i = 0; i < entries.size(); i++) {
                    BelugaFileEntry entry = entries.get(i);
                    entry.isFavorite = favoritePaths.contains(entry.path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            // Sort the list.
            Collections.sort(entries, BelugaSortHelper.getComparator(FileCategoryHelper.CATEGORY_TYPE_UNKNOW));
        }
        return entries;
    }

    @Override
    public void deliverResult(List<BelugaFileEntry> data) {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader deliverResult with "+(data==null?0:data.size()));
        if (isReset()) {
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        List<BelugaFileEntry> oldFiles = mFiles;
        mFiles = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldFiles != null && oldFiles != data) {
            releaseResources(oldFiles);
        }

        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onStartLoading");
        if (mFiles != null ) {
            deliverResult(mFiles);
        }

        // Start watching for changes in the app data.
        if (mSortObserver == null) {
            mSortObserver = new SortPreferenceReceiver(this, FileCategoryHelper.CATEGORY_TYPE_UNKNOW);
        }

        if (mDisplayHiddenObserver == null) {
            mDisplayHiddenObserver = new DisplayHiddenPreferenceReceiver(this);
        }

        if(takeContentChanged() || mFiles == null) {
            forceLoad();
        }
    }


    @Override
    protected void onStopLoading() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onStopLoading");
        cancelLoad();
    }


    @Override
    protected void onReset() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onReset");
        onStopLoading();

        if (mFiles != null) {
            releaseResources(mFiles);
            mFiles = null;
        }

        // Stop monitoring for changes.
        if (mSortObserver != null) {
            mSortObserver.dismiss(getContext());
            mSortObserver = null;
        }
        if (mDisplayHiddenObserver != null) {
            mDisplayHiddenObserver.dismiss(getContext());
            mDisplayHiddenObserver = null;
        }

    }


    @Override
    public void onCanceled(List<BelugaFileEntry> data) {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onCanceled");
        super.onCanceled(data);
        releaseResources(data);
    }

    @Override
    public void forceLoad() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader forceLoad");
        super.forceLoad();
    }

    private void releaseResources(List<BelugaFileEntry> data) {
        // do nothing
    }
}
