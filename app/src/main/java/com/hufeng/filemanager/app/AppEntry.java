package com.hufeng.filemanager.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import com.hufeng.filemanager.browser.FileEntry;

import java.io.File;

/**
 * This class holds the per-item data in our Loader.
 */
public class AppEntry extends FileEntry{

    public AppEntry(AppListLoader loader, ApplicationInfo info) {
        super();
        mLoader = loader;
        mInfo = info;
        mApkFile = new File(info.sourceDir);
    }

    public ApplicationInfo getApplicationInfo() {
        return mInfo;
    }

    public String getPackageName(){
        return mInfo.packageName;
    }

    public String getAppSourcePath() {
        return (mInfo==null)?"":mInfo.sourceDir;
    }

    public String getLabel() {
        return mLabel;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                mIcon = mInfo.loadIcon(mLoader.mPm);
                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mLoader.mPm);
                super.buildFromFile(mApkFile);
                return mIcon;
            }
        } else {
            return mIcon;
        }

        return mLoader.getContext().getResources().getDrawable(
                android.R.drawable.sym_def_app_icon);
    }

    public void getDescription() {

    }

    @Override public String toString() {
        return mLabel;
    }

    void loadLabel(Context context) {
        if (mLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mLabel = mInfo.packageName;
               // mInfo.
            } else {
                mMounted = true;
                CharSequence label = mInfo.loadLabel(context.getPackageManager());
                mLabel = label != null ? label.toString() : mInfo.packageName;
            }
        }
    }

    private boolean mFileEntryBuilt;

    public String getName () {
        return mLabel;
    }

    public long length () {
        if (!mFileEntryBuilt && mApkFile.exists()) {
            buildFromFile(mApkFile);
            mFileEntryBuilt = true;
        }
        return super.length();
    }

    public boolean isDirectory () {
        if (!mFileEntryBuilt && mApkFile.exists()) {
            buildFromFile(mApkFile);
            mFileEntryBuilt = true;
        }
        return super.isDirectory();
    }

    public long lastModified () {
        if (!mFileEntryBuilt && mApkFile.exists()) {
            buildFromFile(mApkFile);
            mFileEntryBuilt = true;
        }
        return super.lastModified();
    }

    private final AppListLoader mLoader;
    private final ApplicationInfo mInfo;
    private final File mApkFile;
    private String mLabel;
    private Drawable mIcon;
    private boolean mMounted;
}