package com.hufeng.filemanager.resource;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.hufeng.filemanager.ResourceType;
import com.hufeng.filemanager.browser.FileEntry;

import java.io.File;

/**
 * Created by feng on 13-9-20.
 */
public class ResourceEntry extends FileEntry{

    private static final String LOG_TAG = ResourceEntry.class.getSimpleName();

    private ResourceListLoader mLoader;

    public String resource_name;
    public String package_name;
    public int version_code;
    public String version_name;
    public long resource_server_time;
    public String resource_description;
    public String resource_icon_url;
    public String download_url;
    public int resource_category;

    public boolean installed;
    public int server_version_code;
    public String server_version_name;
    public int installed_version_code;
    public String installed_version_name;
    public boolean app_upgrade;
    public boolean app_same_version;
    public boolean resource_upgrade;


    public ResourceEntry() {
        super();
    }

    public ResourceEntry(String path, ResourceListLoader loader) {
        super(path);
        if (path.contains(ResourceListLoader.SELECTED_GAME_DIR_NAME)) {
            resource_category = ResourceType.GAME.ordinal();
        } else if (path.contains(ResourceListLoader.SELECTED_DOC_DIR_NAME)) {
            resource_category = ResourceType.DOC.ordinal();
        } else if (path.contains( ResourceListLoader.SELECTED_APP_DIR_NAME )) {
            resource_category = ResourceType.APP.ordinal();
        }
        mLoader = loader;
        loadResourceInfo();
    }

    public boolean isInstalled() {
        return installed;
    }

    public boolean needAppUpgrade() {
        return app_upgrade;
    }

    public boolean isVersionEqual() {
        return app_same_version;
    }

    public boolean needDownload() {
        return (!TextUtils.isEmpty(download_url) && (!isInstalled() || needAppUpgrade()) && (TextUtils.isEmpty(path) || server_version_code>version_code));
    }

    public void loadResourceInfo(){
        PackageInfo info = null;
        if(path != null && /*(ResourceType.valueOf(resource_category) == ResourceType.APP
                || ResourceType.valueOf(resource_category) == ResourceType.GAME)*/path.endsWith(".apk")) {
            info = mLoader.mPm.getPackageArchiveInfo(path,
                    PackageManager.GET_ACTIVITIES);
            ApplicationInfo appInfo = null;
            if (info != null) {
                appInfo = info.applicationInfo;
                package_name = appInfo.packageName;
                version_code = info.versionCode;
                version_name = info.versionName;
            }
        }
        if (package_name != null && /*(ResourceType.valueOf(resource_category) == ResourceType.APP || ResourceType.valueOf(resource_category) == ResourceType.GAME)*/
                path.endsWith(".apk")) {
            try{
                info = mLoader.mPm.getPackageInfo(package_name, PackageManager.GET_UNINSTALLED_PACKAGES);
                if(info!=null) {
                    installed = true;
                    installed_version_code = info.versionCode;
                    installed_version_name = info.versionName;
                    if(installed_version_code < version_code) {
                        app_upgrade = true;
                    } else {
                        app_upgrade = false;
                    }
                    if(installed_version_code == version_code) {
                        app_same_version = true;
                    } else {
                        app_same_version = false;
                    }
                } else {
                    installed = false;
                }

            }catch(PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                installed = false;
            }

        }

        if(path != null && ResourceType.valueOf(resource_category) == ResourceType.DOC) {
            String name = new File(path).getName();
            String[] name_split = name.split("_");
            if (name_split != null && name_split.length == 2) {
                package_name = name_split[0];
                name_split = name_split[1].split("[.()]");
                if (name_split.length >= 2) {
                    version_code = Integer.parseInt(name_split[0]);
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString()+"game_name("+this.resource_name+")"
                +"package_name("+this.package_name+")"
                +"version_code("+this.version_code+")"
                +"version_name("+this.version_name+")"
                +"game_server_time("+this.resource_server_time+")"
                +"game_description("+this.resource_description+")"
                +"game_icon_url("+this.resource_icon_url+")"
                +"download_url("+this.download_url+")"
                +"app_category("+this.resource_category+")"
                +"installed("+this.installed+")"
                +"server_version_code("+this.server_version_code+")"
                +"server_version_name("+this.server_version_name+")"
                +"installed_version_code("+this.installed_version_code+")"
                +"installed_version_name("+this.installed_version_name+")"
                +"need_upgrade("+this.app_upgrade+")";
    }
}
