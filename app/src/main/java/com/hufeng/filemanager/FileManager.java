package com.hufeng.filemanager;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.hufeng.filemanager.channel.DoovUtil;
import com.hufeng.filemanager.provider.DataStructures.MatchColumns;
import com.hufeng.filemanager.provider.DataStructures.PreferenceColumns;
import com.hufeng.filemanager.services.UiCallServiceHelper;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.OSUtil;
import com.hufeng.filemanager.utils.PackageUtil;

import java.util.HashMap;


public class FileManager extends Application {
	
	private static final String LOG_TAG = FileManager.class.getName();
	
	public static final String FILEMANAGER_FIRST_OPEN = "filemanager_first_open";
	public static final String FILEMANAGER_LAST_SCAN = "filemanager_last_scan";
    
	static FileManager mContext;

    public static final String AUDIO_FILTER_SMALL = "Audio_Filter_Small";
    public static final String IMAGE_FILTER_SMALL = "Image_Filter_Small";
    public static final String SCAN_HIDDEN_FILES = "Scan_Hidden_Files";
    public static final String SCAN_GAME_FILES = "Scan_Game_Files";
	
	
	@Override
	public void onCreate() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
		super.onCreate();
		
		mContext = this;
				
        String name = OSUtil.getCurrProcessName(this);
        String pkgName = getPackageName();
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "oncreate begin "+name);

        if (name.equalsIgnoreCase(pkgName)) {
            initUI();
        } else {
            initService();
        }

        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "oncreate end "+name);

	}
	
	private void initUI(){
        Thread.setDefaultUncaughtExceptionHandler(new GlobalUncaughtExceptionHandler(this.getApplicationContext()));
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int old_version = sp.getInt("PACKAGE_VERSION_CODE", 0);
        int new_version = PackageUtil.getVersionCode(this);
        if(new_version!=old_version){
			FileManager.setPreference(FILEMANAGER_FIRST_OPEN, "1");
			Editor edit = sp.edit();
			edit.putBoolean("has_new_version", false);
            edit.putBoolean("need_new_refresh",true);
			edit.putInt("PACKAGE_VERSION_CODE", PackageUtil.getVersionCode(this));
			edit.commit();
        }

        UiCallServiceHelper.getInstance().connectService(this);
	}

    private void initService() {

    }

    
    public static FileManager getAppContext()
    {
    	return mContext;
    }

    
    
    /**
     * ****************************************************************************************
     */
    public static void setPreference(String name, String value) {
        LogUtil.e(LOG_TAG, "setPreference:" + name + "," + value);
        ContentValues values = new ContentValues();
        values.put(PreferenceColumns.VALUE, value);
        try{
        FileManager
                .getAppContext()
                .getContentResolver()
                .update(PreferenceColumns.CONTENT_URI, values, PreferenceColumns.NAME + "=?",
                        new String[] {
                            name
                        });
        }catch(IllegalArgumentException e){
        	e.printStackTrace();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void removePreference(String name) {
        LogUtil.e(LOG_TAG, "removePreference:" + name);
        try{
        FileManager
                .getAppContext()
                .getContentResolver()
                .delete(PreferenceColumns.CONTENT_URI,
                        PreferenceColumns.NAME + "=?", new String[] { name });
        }catch(IllegalArgumentException e){
        	e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getPreference(String name, String defValues) {
        Cursor cursor = null;
        String value = defValues;
        try {
            cursor = FileManager
                    .getAppContext()
                    .getContentResolver()
                    .query(PreferenceColumns.CONTENT_URI, new String[] {
                            PreferenceColumns.VALUE
                    }, PreferenceColumns.NAME + "=?", new String[] {
                            name
                    },
                            null);
            if (cursor != null && cursor.moveToFirst()) {
                value = cursor.getString(0);
            }
        } catch(Exception e){
        	e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        LogUtil.e(LOG_TAG, "getPreference:" + name + "," + "def:" + defValues + "," + value);
        return value;
    }


 	@Override
 	public void onTerminate() {
 		// TODO Auto-generated method stub
 		super.onTerminate();
 	}
 	//............................................

    private static final String PROPERTY_ID = "UA-59522104-1";

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
//        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
//        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }


}
