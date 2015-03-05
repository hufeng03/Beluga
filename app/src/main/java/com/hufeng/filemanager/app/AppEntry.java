package com.hufeng.filemanager.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Parcel;

import com.hufeng.filemanager.BelugaEntry;
import com.hufeng.filemanager.data.FileEntry;

/**
 * This class holds the per-item data in our Loader.
 */
public class AppEntry extends BelugaEntry {

    public FileEntry apkEntry;
    public String appName;
    public Drawable appIcon;
    public String packageName;
    public String versionName;
    public int versionCode;


    public AppEntry(Context context, String packageName) {
        this.packageName = packageName;
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            appName = info.applicationInfo.loadLabel(context.getPackageManager()).toString();
            appIcon = info.applicationInfo.loadIcon(context.getPackageManager());
            versionCode = info.versionCode;
            versionName = info.versionName;
            apkEntry = new FileEntry(info.applicationInfo.sourceDir);
        }
    }

    @Override
    public String getIdentity() {
        return "app://"+packageName;
    }

    @Override
    public String getName() {
        return appName;
    }

    @Override
    public long getSize() {
        return apkEntry.getSize();
    }

    @Override
    public long getTime() {
        return apkEntry.getTime();
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return appIcon;
    }

    public void getDescription() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}