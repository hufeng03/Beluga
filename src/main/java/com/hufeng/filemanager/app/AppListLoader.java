package com.hufeng.filemanager.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.browser.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by feng on 13-9-6.
 */
public class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {
    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
    final PackageManager mPm;

    List<AppEntry> mApps;
    PackageIntentReceiver mPackageObserver;

    String mSearch;

//    /**
//     * Perform alphabetical comparison of application entry objects.
//     */
//    public static final Comparator<AppEntry> NAME_ASC_COMPARATOR = new Comparator<AppEntry>() {
//        private final Collator sCollator = Collator.getInstance();
//        @Override
//        public int compare(AppEntry object1, AppEntry object2) {
//            return sCollator.compare(object1.getLabel(), object2.getLabel());
//        }
//    };
//
//    public static final Comparator<AppEntry> NAME_DESC_COMPARATOR = new Comparator<AppEntry>() {
//        private final Collator sCollator = Collator.getInstance();
//        @Override
//        public int compare(AppEntry object1, AppEntry object2) {
//            return sCollator.compare(object2.getLabel(), object1.getLabel());
//        }
//    };
//
//    public static final Comparator<AppEntry> SIZE_ASC_COMPARATOR = new Comparator<AppEntry>() {
//        @Override
//        public int compare(AppEntry object1, AppEntry object2) {
//            return 0;
//        }
//    };
//
//    public static final Comparator<AppEntry> SIZE_DESC_COMPARATOR = new Comparator<AppEntry>() {
//        @Override
//        public int compare(AppEntry object1, AppEntry object2) {
//            return 0;
//        }
//    };
//
//    public static final Comparator<AppEntry> DATE_ASC_COMPARATOR = new Comparator<AppEntry>() {
//        @Override
//        public int compare(AppEntry object1, AppEntry object2) {
//            return 0;
//        }
//    };
//
//    public static final Comparator<AppEntry> DATE_DESC_COMPARATOR = new Comparator<AppEntry>() {
//        @Override
//        public int compare(AppEntry object1, AppEntry object2) {
//            return 0;
//        }
//    };



//    public static final Comparator<AppEntry> getComparator(FileSorter.SORT_FIELD field, FileSorter.SORT_ORDER order) {
//        Comparator<AppEntry> comparator;
//        switch (field) {
//            case NAME:
//                comparator = (order == FileSorter.SORT_ORDER.DESC)?NAME_DESC_COMPARATOR:NAME_ASC_COMPARATOR;
//                break;
//            case SIZE:
//                comparator = (order == FileSorter.SORT_ORDER.DESC)?SIZE_DESC_COMPARATOR:SIZE_ASC_COMPARATOR;
//                break;
//            case DATE:
//                comparator = (order == FileSorter.SORT_ORDER.DESC)?DATE_DESC_COMPARATOR:DATE_ASC_COMPARATOR;
//                break;
//            default:
//                comparator = NAME_ASC_COMPARATOR;
//                break;
//        }
//        return comparator;
//    }

    public AppListLoader(Context context, String search) {
        super(context);
        mSearch = search;

        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
        mPm = getContext().getPackageManager();
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<AppEntry> loadInBackground() {
        // Retrieve all known applications.
        List<ApplicationInfo> apps = mPm.getInstalledApplications(
                PackageManager.GET_UNINSTALLED_PACKAGES |
                        PackageManager.GET_DISABLED_COMPONENTS);
        if (apps == null) {
            apps = new ArrayList<ApplicationInfo>();
        }

        final Context context = getContext();

        // Create corresponding array of entries and load their labels.
        List<AppEntry> entries = new ArrayList<AppEntry>(apps.size());
        for (int i=0; i<apps.size(); i++) {
            if ((apps.get(i).flags & ApplicationInfo.FLAG_SYSTEM) > 0 ){
                if((apps.get(i).flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) ==0 )
                    continue;
            }
            AppEntry entry = new AppEntry(this, apps.get(i));
            entry.loadLabel(context);
            if(TextUtils.isEmpty(mSearch) || entry.getLabel().contains(mSearch) || entry.getApplicationInfo().processName.contains(mSearch)) {
                entries.add(entry);
            }
        }
        // Sort the list.
        FileSorter.SORTER sorter = FileSorter.getFileSorter(getContext(), FileUtils.FILE_TYPE_APP);
        Collections.sort(entries, FileSorter.getComparator(sorter.field, sorter.order));

        // Done!
        return entries;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<AppEntry> apps) {

        List<AppEntry> oldApps = apps;
        mApps = apps;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            if (isReset()) {
                if (mApps != null) {
                    onReleaseResources(apps);
                    return;
                }
            }

            super.deliverResult(apps);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null && oldApps!=apps) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mApps);
        }

        // Start watching for changes in the app data.
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }

        // Has something interesting in the configuration changed since we
        // last built the app list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());

        if (takeContentChanged() || mApps == null || configChange) {
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
    @Override public void onCanceled(List<AppEntry> apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
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
        if (mApps != null) {
            onReleaseResources(mApps);
            mApps = null;
        }

        // Stop monitoring for changes.
        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<AppEntry> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}