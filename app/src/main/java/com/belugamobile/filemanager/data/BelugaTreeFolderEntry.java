package com.belugamobile.filemanager.data;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.util.Log;

import com.belugamobile.filemanager.BelugaEntry;
import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.mount.MountPoint;
import com.belugamobile.filemanager.utils.FolderUtil;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by Feng on 2015-04-22.
 */
public class BelugaTreeFolderEntry extends BelugaEntry {

    private static final String TAG = "BelugaTreeFolderEntry";

    public int icon;
    public String path;
    public String name;
    public boolean expanded;
    public boolean expandable;
    public boolean isRoot;
    public String root;
    public int depth;
    public String parent;

    public BelugaTreeFolderEntry(MountPoint mountPoint, boolean expanded) {
        if (mountPoint.mIsExternal) {
            this.icon = R.drawable.ic_sd_storage;
        } else {
            this.icon = R.drawable.ic_phone_android;
        }
        this.name = mountPoint.mDescription;
        this.expanded = expanded;
        this.path = mountPoint.mPath;
        this.expandable = true;
        this.isRoot = true;
        this.root = this.path;
        this.depth = 0;
        this.parent = null;
    }

    public BelugaTreeFolderEntry(String dir, String name, String root, int depth, boolean expanded) {
        Log.i(TAG, "dir = " + dir + ", name=" + name);
        this.icon = R.drawable.ic_type_folder;
        this.name = name;
        this.path = FolderUtil.concatenateDirAndName(dir, name);
        this.expanded = expanded;
        File[] childFolders = new File(dir, name).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        this.expandable = childFolders != null && childFolders.length > 0;
        this.isRoot = false;
        this.root = root;
        this.parent = dir;
        this.depth = depth;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public String getIdentity() {
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
