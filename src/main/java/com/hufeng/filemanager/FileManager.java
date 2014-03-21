package com.hufeng.filemanager;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.hufeng.filemanager.data.DataStructures.MatchColumns;
import com.hufeng.filemanager.data.DataStructures.PreferenceColumns;
import com.hufeng.filemanager.services.UiServiceHelper;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.OSUtil;
import com.hufeng.filemanager.utils.PackageUtil;
import com.hufeng.playmusic.util.MediaUtils;
import com.hufeng.playmusic.util.ServiceInterface;

//import com.hufeng.playimagewithgl.util.GalleryUtils;
//import com.hufeng.playimagewithgl.GalleryApp;
//import com.hufeng.playimagewithgl.data.DataManager;
//import com.hufeng.playimagewithgl.data.ImageCacheService;
//import com.hufeng.playimagewithgl.util.ThreadPool;

public class FileManager extends Application/* implements GalleryApp*/{
	
	private static final String LOG_TAG = FileManager.class.getName();
	
	public static final String FILEMANAGER_FIRST_OPEN = "filemanager_first_open";
	public static final String FILEMANAGER_LAST_SCAN = "filemanager_last_scan";
    
	static FileManager mContext;
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mContext = this;
		
		//for play music ...............................
		mUtils = new MediaUtils(this);
		mServiceInterface = new ServiceInterface(this);
		//..............................................
				
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int old_version = sp.getInt("PACKAGE_VERSION_CODE", 0);
        int new_version = PackageUtil.getVersionCode(this);
        if(new_version!=old_version){
			FileManager.setPreference(FILEMANAGER_FIRST_OPEN, "1");
			Editor edit = sp.edit();
			edit.putBoolean("has_new_version", false);
            edit.putBoolean("need_new_refresh",true);
			edit.putInt("PACKAGE_VERSION_CODE", PackageUtil.getVersionCode(this));
			String image_default_viewer = sp.getString("IMAGE_DEFAULT_VIEW_ACTIVITY", "");
			String audio_default_viewer = sp.getString("AUDIO_DEFAULT_VIEW_ACTIVITY", "");
			String video_default_viewer = sp.getString("VIDEO_DEFAULT_VIEW_ACTIVITY", "");
			boolean flag_image = false, flag_audio = false, flag_video = false;
			if(!TextUtils.isEmpty(image_default_viewer) && image_default_viewer.equals("com.hufeng.playimage.PlayImageActivity")){
				flag_image = true;
			}
			if(!TextUtils.isEmpty(audio_default_viewer) && audio_default_viewer.equals("com.hufeng.playmusic.PlayMusicActivity")){
				flag_audio = true;
			}
			if(!TextUtils.isEmpty(video_default_viewer) && video_default_viewer.equals("com.hufeng.playvideo.PlayVideoActivity")){
				flag_video = true;
			}
			if(flag_image || flag_audio || flag_video){
				boolean flag_image_exist = false,flag_audio_exist = false, flag_video_exist = false;
				try{
					PackageInfo packageInfo = getPackageManager().getPackageInfo("com.hufeng.filemanager", 0);
					ActivityInfo[] activities = packageInfo.activities;
					if(activities!=null){
						for(ActivityInfo activity:activities){
							if("com.hufeng.playimage.PlayImageActivity".equals(activity.name)){
								flag_image_exist = true;
							}
							if("com.hufeng.playmusic.PlayMusicActivity".equals(activity.name)){
								flag_audio_exist = true;
							}
							if("com.hufeng.playvideo.PlayVideoActivity".equals(activity.name)){
								flag_video_exist = true;
							}
							if((flag_image_exist || !flag_image) && (flag_audio_exist || !flag_audio) && (flag_video_exist || !flag_video)){
								break;
							}
						}
					}
				}catch(NameNotFoundException e){
					e.printStackTrace();
				}
				if(flag_image && !flag_image_exist){
					edit.putString("IMAGE_DEFAULT_VIEW_APP", "");
					edit.putString("IMAGE_DEFAULT_VIEW_ACTIVITY", "");
				}
				if(flag_audio && !flag_audio_exist){
					edit.putString("AUDIO_DEFAULT_VIEW_APP", "");
					edit.putString("AUDIO_DEFAULT_VIEW_ACTIVITY", "");
				}
				if(flag_video && !flag_video_exist){
					edit.putString("VIDEO_DEFAULT_VIEW_APP", "");
					edit.putString("VIDEO_DEFAULT_VIEW_ACTIVITY", "");
				}
			}
			edit.commit();
        }

        UiServiceHelper.getInstance().connectService(this);
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
        	System.exit(0);
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
        	System.exit(0);
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
    
    /**
     * ****************************************************************************************
     */
    public static void setCategoryMatch(String name, int category) {
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "setMatch:" + name + "," + category);
        ContentValues values = new ContentValues();
        values.put(MatchColumns.CATEGORY_FIELD, category);
        try{
        FileManager
                .getAppContext()
                .getContentResolver()
                .update(MatchColumns.CONTENT_URI, values, MatchColumns.EXTENSION_FIELD + "=?",
                        new String[] {
                            name
                        });
        }catch(IllegalArgumentException e){
        	e.printStackTrace();
        	System.exit(0);
        }
    }

    public static void removeMatch(String name) {
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "removePreference:" + name);
        try{
        FileManager
                .getAppContext()
                .getContentResolver()
                .delete(MatchColumns.CONTENT_URI,
                        MatchColumns.EXTENSION_FIELD + "=?", new String[] { name });
        }catch(IllegalArgumentException e){
        	e.printStackTrace();
        	System.exit(0);
        }
    }

    public static String getCategoryMatch(String name, String defValues) {
        Cursor cursor = null;
        String value = defValues;
        try {
            cursor = FileManager
                    .getAppContext()
                    .getContentResolver()
                    .query(MatchColumns.CONTENT_URI, new String[] {
                            MatchColumns.CATEGORY_FIELD
                    }, MatchColumns.EXTENSION_FIELD + "=?", new String[] {
                            name
                    },
                            null);
            if (cursor != null && cursor.moveToFirst()) {
                value = cursor.getString(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "getMatch:" + name + "," + "def:" + defValues + "," + value);
        return value;
    }

    //for play music .............................
 	private MediaUtils mUtils;
 	
 	private ServiceInterface mServiceInterface;
 	
 	public ServiceInterface getServiceInterface() {
 		return mServiceInterface;
 	}	
 	
 	public MediaUtils getMediaUtils() {
 		return mUtils;
 	}

 	@Override
 	public void onTerminate() {
 		// TODO Auto-generated method stub
 		super.onTerminate();
 		mUtils = null;
 	}
 	//............................................




}
