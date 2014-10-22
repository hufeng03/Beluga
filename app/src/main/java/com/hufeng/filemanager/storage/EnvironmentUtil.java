package com.hufeng.filemanager.storage;

import android.content.Context;
import android.os.Environment;

import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;

/**
 * Created by feng on 2014-03-30.
 */
public class EnvironmentUtil {

    private static final String TAG = EnvironmentUtil.class.getSimpleName();


    public static void test() {
        //System directories
        File file;
        file = Environment.getDataDirectory();           ///data
        print(file, "getDataDirectory");
        file = Environment.getDownloadCacheDirectory();  ///cache
        print(file, "getDownloadCacheDirectory");
        file = Environment.getRootDirectory();           ///system
        print(file, "getRootDirectory");

        //External storage directories
        file = Environment.getExternalStorageDirectory();	///storage/sdcard0
        print(file, "getExternalStorageDirectory");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);	///storage/sdcard0/Alarms
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);	///storage/sdcard0/DCIM
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);	///storage/sdcard0/Download
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);	 ///storage/sdcard0/Movies
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);	 ///storage/sdcard0/Music
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS);	///storage/sdcard0/Notifications
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);	///storage/sdcard0/Pictures
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);	///storage/sdcard0/Podcasts
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
        print(file, "getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES)");
    }

    public static void test2(Context context) {
        File file;
        //Application directories
        file = context.getCacheDir();	///data/data/package/cache
        print(file,"getCacheDir");
        file = context.getFilesDir();	///data/data/package/files
        print(file,"getFilesDir");
        file = context.getFilesDir().getParentFile();	///data/data/package
        print(file,"getFilesDir().getParentFile()");



        //Application External storage directories
        file = context.getExternalCacheDir();	///storage/sdcard0/Android/data/package/cache
        print(file,"getExternalCacheDir");
        file = context.getExternalFilesDir(null);	///storage/sdcard0/Android/data/package/files
        print(file,"getExternalFilesDir(null)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_ALARMS);	///storage/sdcard0/Android/data/package/files/Alarms
        print(file,"getExternalFilesDir(Environment.DIRECTORY_ALARMS)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_DCIM);	///storage/sdcard0/Android/data/package/files/DCIM
        print(file,"getExternalFilesDir(Environment.DIRECTORY_DCIM)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);	///storage/sdcard0/Android/data/package/files/Download
        print(file,"getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);	///storage/sdcard0/Android/data/package/files/Movies
        print(file,"getExternalFilesDir(Environment.DIRECTORY_MOVIES)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);	///storage/sdcard0/Android/data/package/files/Music
        print(file,"getExternalFilesDir(Environment.DIRECTORY_MUSIC)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS);	///storage/sdcard0/Android/data/package/files/Notifications
        print(file,"getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);	///storage/sdcard0/Android/data/package/files/Pictures
        print(file,"getExternalFilesDir(Environment.DIRECTORY_PICTURES)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS);	///storage/sdcard0/Android/data/package/files/Podcasts
        print(file,"getExternalFilesDir(Environment.DIRECTORY_PODCASTS)");
        file = context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES);	///storage/sdcard0/Android/data/package/files/Ringtones
        print(file,"getExternalFilesDir(Environment.DIRECTORY_RINGTONES)");
    }

    private static void print(File file, String tag_info){
        LogUtil.i(TAG, tag_info + ": " + ((file == null) ? "" : file.getAbsolutePath()));
    }

}
