package com.hufeng.filemanager.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures.ApkColumns;
import com.hufeng.filemanager.provider.DataStructures.AudioColumns;
import com.hufeng.filemanager.provider.DataStructures.CategoryColumns;
import com.hufeng.filemanager.provider.DataStructures.CloudBoxColumns;
import com.hufeng.filemanager.provider.DataStructures.DocumentColumns;
import com.hufeng.filemanager.provider.DataStructures.FavoriteColumns;
import com.hufeng.filemanager.provider.DataStructures.FileColumns;
import com.hufeng.filemanager.provider.DataStructures.ImageColumns;
import com.hufeng.filemanager.provider.DataStructures.MatchColumns;
import com.hufeng.filemanager.provider.DataStructures.PreferenceColumns;
import com.hufeng.filemanager.provider.DataStructures.SelectedColumns;
import com.hufeng.filemanager.provider.DataStructures.VideoColumns;
import com.hufeng.filemanager.provider.DataStructures.ZipColumns;
import com.hufeng.filemanager.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FileManagerProvider extends ContentProvider{
	
	private static final String LOG_TAG = FileManagerProvider.class.getSimpleName();

	private static final HashMap<String, String> mFileProjectionMap;
	private static final HashMap<String, String> mImageProjectionMap;
	private static final HashMap<String, String> mAudioProjectionMap;
	private static final HashMap<String, String> mApkProjectionMap;
	private static final HashMap<String, String> mVideoProjectionMap;
	private static final HashMap<String, String> mCategoryProjectionMap;
	private static final HashMap<String, String> mDocumentProjectionMap;
	private static final HashMap<String, String> mZipProjectionMap;
	private static final HashMap<String, String> mFavoriteProjectionMap;
	private static final HashMap<String, String> mPreferenceProjectionMap;
	private static final HashMap<String, String> mMatchProjectionMap;
    private static final HashMap<String, String> mSelectedProjectionMap;
    private static final HashMap<String, String> mCloudProjectionMap;
	
	private static final int FILES = 1;
	private static final int FILE_ID = 2;
	private static final int IMAGES = 3;
	private static final int IMAGE_ID = 4;
	private static final int AUDIOS = 5;
	private static final int AUDIO_ID = 6;
	private static final int APKS = 7;
	private static final int APK_ID = 8;
	private static final int VIDEOS = 9;
	private static final int VIDEO_ID = 10;
	private static final int CATEGORYS = 11;
	private static final int CATEGORY_ID = 12;
	private static final int DOCUMENTS = 13;
	private static final int DOCUMENT_ID = 14;
	private static final int ZIPS = 15;
	private static final int ZIP_ID = 16;
	private static final int FAVORITES = 17;
	private static final int FAVORITE_ID = 18;
	private static final int PREFERENCES = 19;
	private static final int MATCHS = 20;
//	private static final int PREFERENCE_ID = 20;
    private static final int SELECTED_ID = 21;
    private static final int SELECTEDS = 22;
    private static final int CLOUD_ID = 23;
    private static final int CLOUDS = 24;
	
	
	private static final UriMatcher URI_MATCHER;
	
	static{
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FileColumns.TABLE, FILES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FileColumns.TABLE+"/#", FILE_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ImageColumns.TABLE, IMAGES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ImageColumns.TABLE+"/#", IMAGE_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, AudioColumns.TABLE, AUDIOS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, AudioColumns.TABLE+"/#", AUDIO_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, VideoColumns.TABLE, VIDEOS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, VideoColumns.TABLE+"/#", VIDEO_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ApkColumns.TABLE, APKS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ApkColumns.TABLE+"/#", APK_ID); 
        URI_MATCHER.addURI(DataStructures.AUTHORITY, CategoryColumns.TABLE, CATEGORYS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, CategoryColumns.TABLE+"/#", CATEGORY_ID); 
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DocumentColumns.TABLE, DOCUMENTS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DocumentColumns.TABLE+"/#", DOCUMENT_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ZipColumns.TABLE, ZIPS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ZipColumns.TABLE+"/#", ZIP_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FavoriteColumns.TABLE, FAVORITES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FavoriteColumns.TABLE+"/#", FAVORITE_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, PreferenceColumns.TABLE, PREFERENCES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, MatchColumns.TABLE, MATCHS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.SelectedColumns.TABLE, SELECTEDS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.SelectedColumns.TABLE+"/#", SELECTED_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.CloudBoxColumns.TABLE, CLOUDS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.CloudBoxColumns.TABLE+"/#", CLOUD_ID);
        
//        URI_MATCHER.addURI(DataStructures.AUTHORITY, PreferenceColumns.TABLE+"/#", PREFERENCE_ID);
        
        mFileProjectionMap = new HashMap<String,String>();
        mFileProjectionMap.put(FileColumns._ID, FileColumns._ID);
        mFileProjectionMap.put(FileColumns.FILE_DATE_FIELD, FileColumns.FILE_DATE_FIELD);
        mFileProjectionMap.put(FileColumns.FILE_TYPE_FIELD, FileColumns.FILE_TYPE_FIELD);
        mFileProjectionMap.put(FileColumns.FILE_SIZE_FIELD, FileColumns.FILE_SIZE_FIELD);
        mFileProjectionMap.put(FileColumns.FILE_PATH_FIELD, FileColumns.FILE_PATH_FIELD);
        mFileProjectionMap.put(FileColumns.FILE_NAME_FIELD, FileColumns.FILE_NAME_FIELD);
        mFileProjectionMap.put(FileColumns.FILE_EXTENSION_FIELD, FileColumns.FILE_EXTENSION_FIELD);
        mFileProjectionMap.put(FileColumns.FILE_STORAGE_FIELD, FileColumns.FILE_STORAGE_FIELD);
        mFileProjectionMap.put(FileColumns.FILE_SYNC_FIELD, FileColumns.FILE_SYNC_FIELD);
        
        mImageProjectionMap = new HashMap<String,String>();
        mImageProjectionMap.put(ImageColumns._ID, ImageColumns._ID);
        mImageProjectionMap.put(ImageColumns.FILE_DATE_FIELD, ImageColumns.FILE_DATE_FIELD);
        mImageProjectionMap.put(ImageColumns.FILE_TYPE_FIELD, ImageColumns.FILE_TYPE_FIELD);
        mImageProjectionMap.put(ImageColumns.FILE_SIZE_FIELD, ImageColumns.FILE_SIZE_FIELD);
        mImageProjectionMap.put(ImageColumns.FILE_PATH_FIELD, ImageColumns.FILE_PATH_FIELD);
        mImageProjectionMap.put(ImageColumns.FILE_NAME_FIELD, ImageColumns.FILE_NAME_FIELD);
        mImageProjectionMap.put(ImageColumns.FILE_EXTENSION_FIELD, ImageColumns.FILE_EXTENSION_FIELD);
        mImageProjectionMap.put(ImageColumns.FILE_STORAGE_FIELD, ImageColumns.FILE_STORAGE_FIELD);
        mImageProjectionMap.put(ImageColumns.FILE_SYNC_FIELD, ImageColumns.FILE_SYNC_FIELD);
        mImageProjectionMap.put(ImageColumns.IMAGE_WIDTH_FIELD, ImageColumns.IMAGE_WIDTH_FIELD);
        mImageProjectionMap.put(ImageColumns.IMAGE_HEIGHT_FIELD, ImageColumns.IMAGE_HEIGHT_FIELD);
  
        mAudioProjectionMap = new HashMap<String,String>();
        mAudioProjectionMap.put(AudioColumns._ID, AudioColumns._ID);
        mAudioProjectionMap.put(AudioColumns.FILE_DATE_FIELD, AudioColumns.FILE_DATE_FIELD);
        mAudioProjectionMap.put(AudioColumns.FILE_TYPE_FIELD, AudioColumns.FILE_TYPE_FIELD);
        mAudioProjectionMap.put(AudioColumns.FILE_SIZE_FIELD, AudioColumns.FILE_SIZE_FIELD);
        mAudioProjectionMap.put(AudioColumns.FILE_PATH_FIELD, AudioColumns.FILE_PATH_FIELD);
        mAudioProjectionMap.put(AudioColumns.FILE_NAME_FIELD, AudioColumns.FILE_NAME_FIELD);
        mAudioProjectionMap.put(AudioColumns.FILE_EXTENSION_FIELD, AudioColumns.FILE_EXTENSION_FIELD);
        mAudioProjectionMap.put(AudioColumns.FILE_STORAGE_FIELD, AudioColumns.FILE_STORAGE_FIELD);
        mAudioProjectionMap.put(AudioColumns.FILE_SYNC_FIELD, AudioColumns.FILE_SYNC_FIELD);
        mAudioProjectionMap.put(AudioColumns.PLAY_DURATION_FIELD, AudioColumns.PLAY_DURATION_FIELD);
        mAudioProjectionMap.put(AudioColumns.ALBUM_FIELD, AudioColumns.ALBUM_FIELD);
        mAudioProjectionMap.put(AudioColumns.SINGER_FIELD, AudioColumns.SINGER_FIELD);
        mAudioProjectionMap.put(AudioColumns.TITLE_FIELD, AudioColumns.TITLE_FIELD);
        
        mVideoProjectionMap = new HashMap<String,String>();
        mVideoProjectionMap.put(VideoColumns._ID, VideoColumns._ID);
        mVideoProjectionMap.put(VideoColumns.FILE_DATE_FIELD, VideoColumns.FILE_DATE_FIELD);
        mVideoProjectionMap.put(VideoColumns.FILE_TYPE_FIELD, VideoColumns.FILE_TYPE_FIELD);
        mVideoProjectionMap.put(VideoColumns.FILE_SIZE_FIELD, VideoColumns.FILE_SIZE_FIELD);
        mVideoProjectionMap.put(VideoColumns.FILE_NAME_FIELD, VideoColumns.FILE_NAME_FIELD);
        mVideoProjectionMap.put(VideoColumns.FILE_PATH_FIELD, VideoColumns.FILE_PATH_FIELD);
        mVideoProjectionMap.put(VideoColumns.FILE_EXTENSION_FIELD, VideoColumns.FILE_EXTENSION_FIELD);
        mVideoProjectionMap.put(VideoColumns.FILE_STORAGE_FIELD, VideoColumns.FILE_STORAGE_FIELD);
        mVideoProjectionMap.put(VideoColumns.FILE_SYNC_FIELD, VideoColumns.FILE_SYNC_FIELD);
        mVideoProjectionMap.put(VideoColumns.PLAY_DURATION_FIELD, VideoColumns.PLAY_DURATION_FIELD);
        
        mApkProjectionMap = new HashMap<String,String>();
        mApkProjectionMap.put(ApkColumns._ID, ApkColumns._ID);
        mApkProjectionMap.put(ApkColumns.FILE_DATE_FIELD, ApkColumns.FILE_DATE_FIELD);
        mApkProjectionMap.put(ApkColumns.FILE_TYPE_FIELD, ApkColumns.FILE_TYPE_FIELD);
        mApkProjectionMap.put(ApkColumns.FILE_SIZE_FIELD, ApkColumns.FILE_SIZE_FIELD);
        mApkProjectionMap.put(ApkColumns.FILE_NAME_FIELD, ApkColumns.FILE_NAME_FIELD);
        mApkProjectionMap.put(ApkColumns.FILE_PATH_FIELD, ApkColumns.FILE_PATH_FIELD);
        mApkProjectionMap.put(ApkColumns.FILE_EXTENSION_FIELD, ApkColumns.FILE_EXTENSION_FIELD);
        mApkProjectionMap.put(ApkColumns.FILE_STORAGE_FIELD, ApkColumns.FILE_STORAGE_FIELD);
        mApkProjectionMap.put(ApkColumns.FILE_SYNC_FIELD, ApkColumns.FILE_SYNC_FIELD);

        mCategoryProjectionMap = new HashMap<String,String>();
        mCategoryProjectionMap.put(CategoryColumns._ID, CategoryColumns._ID);
        mCategoryProjectionMap.put(CategoryColumns.CATEGORY_FIELD, CategoryColumns.CATEGORY_FIELD);
        mCategoryProjectionMap.put(CategoryColumns.SIZE_FIELD, CategoryColumns.SIZE_FIELD);
        mCategoryProjectionMap.put(CategoryColumns.NUMBER_FIELD, CategoryColumns.NUMBER_FIELD);
        mCategoryProjectionMap.put(CategoryColumns.STORAGE_FIELD, CategoryColumns.STORAGE_FIELD);
        
        mDocumentProjectionMap = new HashMap<String,String>();
        mDocumentProjectionMap.put(DocumentColumns._ID, DocumentColumns._ID);
        mDocumentProjectionMap.put(DocumentColumns.FILE_DATE_FIELD, DocumentColumns.FILE_DATE_FIELD);
        mDocumentProjectionMap.put(DocumentColumns.FILE_TYPE_FIELD, DocumentColumns.FILE_TYPE_FIELD);
        mDocumentProjectionMap.put(DocumentColumns.FILE_SIZE_FIELD, DocumentColumns.FILE_SIZE_FIELD);
        mDocumentProjectionMap.put(DocumentColumns.FILE_NAME_FIELD, DocumentColumns.FILE_NAME_FIELD);
        mDocumentProjectionMap.put(DocumentColumns.FILE_PATH_FIELD, DocumentColumns.FILE_PATH_FIELD);
        mDocumentProjectionMap.put(DocumentColumns.FILE_STORAGE_FIELD, DocumentColumns.FILE_STORAGE_FIELD);
        mDocumentProjectionMap.put(DocumentColumns.FILE_EXTENSION_FIELD, DocumentColumns.FILE_EXTENSION_FIELD);
        mDocumentProjectionMap.put(DocumentColumns.FILE_SYNC_FIELD, DocumentColumns.FILE_SYNC_FIELD);
        
        mZipProjectionMap = new HashMap<String,String>();
        mZipProjectionMap.put(ZipColumns._ID, ZipColumns._ID);
        mZipProjectionMap.put(ZipColumns.FILE_DATE_FIELD, ZipColumns.FILE_DATE_FIELD);
        mZipProjectionMap.put(ZipColumns.FILE_TYPE_FIELD, ZipColumns.FILE_TYPE_FIELD);
        mZipProjectionMap.put(ZipColumns.FILE_SIZE_FIELD, ZipColumns.FILE_SIZE_FIELD);
        mZipProjectionMap.put(ZipColumns.FILE_NAME_FIELD, ZipColumns.FILE_NAME_FIELD);
        mZipProjectionMap.put(ZipColumns.FILE_PATH_FIELD, ZipColumns.FILE_PATH_FIELD);
        mZipProjectionMap.put(ZipColumns.FILE_EXTENSION_FIELD, ZipColumns.FILE_EXTENSION_FIELD);
        mZipProjectionMap.put(ZipColumns.FILE_STORAGE_FIELD, ZipColumns.FILE_STORAGE_FIELD);
        mZipProjectionMap.put(ZipColumns.FILE_SYNC_FIELD, ZipColumns.FILE_SYNC_FIELD);

        mFavoriteProjectionMap = new HashMap<String,String>();
        mFavoriteProjectionMap.put(FavoriteColumns._ID, FavoriteColumns._ID);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_DATE_FIELD, FavoriteColumns.FILE_DATE_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_TYPE_FIELD, FavoriteColumns.FILE_TYPE_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_SIZE_FIELD, FavoriteColumns.FILE_SIZE_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_PATH_FIELD, FavoriteColumns.FILE_PATH_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_NAME_FIELD, FavoriteColumns.FILE_NAME_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_EXTENSION_FIELD, FavoriteColumns.FILE_EXTENSION_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_STORAGE_FIELD, FavoriteColumns.FILE_STORAGE_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.IS_DIRECTORY_FIELD, FavoriteColumns.IS_DIRECTORY_FIELD);
        mFavoriteProjectionMap.put(FavoriteColumns.FILE_SYNC_FIELD, FavoriteColumns.FILE_SYNC_FIELD);

        mSelectedProjectionMap = new HashMap<String,String>();
        mSelectedProjectionMap.put(SelectedColumns._ID, SelectedColumns._ID);
        mSelectedProjectionMap.put(SelectedColumns.FILE_DATE_FIELD, SelectedColumns.FILE_DATE_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.FILE_TYPE_FIELD, SelectedColumns.FILE_TYPE_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.FILE_SIZE_FIELD, SelectedColumns.FILE_SIZE_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.FILE_NAME_FIELD, SelectedColumns.FILE_NAME_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.FILE_PATH_FIELD, SelectedColumns.FILE_PATH_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.FILE_EXTENSION_FIELD, SelectedColumns.FILE_EXTENSION_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.FILE_STORAGE_FIELD, SelectedColumns.FILE_STORAGE_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.FILE_SYNC_FIELD, SelectedColumns.FILE_SYNC_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.URL_FIELD, SelectedColumns.URL_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.SERVER_NAME_FIELD, SelectedColumns.SERVER_NAME_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.PACKAGE_FIELD, SelectedColumns.PACKAGE_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.VERSION_FIELD, SelectedColumns.VERSION_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.VERSION_NAME_FIELD, SelectedColumns.VERSION_NAME_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.SERVER_DATE_FIELD, SelectedColumns.SERVER_DATE_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.DESCRIPTION_FIELD, SelectedColumns.DESCRIPTION_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.APP_CATEGORY_FIELD, SelectedColumns.APP_CATEGORY_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.ICON_FIELD, SelectedColumns.ICON_FIELD);
        mSelectedProjectionMap.put(SelectedColumns.PHOTO_FIELD, SelectedColumns.PHOTO_FIELD);
        
        mCloudProjectionMap = new HashMap<String, String>();
        mCloudProjectionMap.put(CloudBoxColumns._ID, CloudBoxColumns._ID);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_DATE_FIELD, CloudBoxColumns.FILE_DATE_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_TYPE_FIELD, CloudBoxColumns.FILE_TYPE_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_SIZE_FIELD, CloudBoxColumns.FILE_SIZE_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_NAME_FIELD, CloudBoxColumns.FILE_NAME_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_PATH_FIELD, CloudBoxColumns.FILE_PATH_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_EXTENSION_FIELD, CloudBoxColumns.FILE_EXTENSION_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_STORAGE_FIELD, CloudBoxColumns.FILE_STORAGE_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.FILE_SYNC_FIELD, CloudBoxColumns.FILE_SYNC_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.PARENT_FOLDER_FIELD, CloudBoxColumns.PARENT_FOLDER_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.IS_FOLDER_FIELD, CloudBoxColumns.IS_FOLDER_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.HASH_FIELD, CloudBoxColumns.HASH_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.LOCAL_FILE_FIELD, CloudBoxColumns.LOCAL_FILE_FIELD);
        mCloudProjectionMap.put(CloudBoxColumns.ICON_DATA_FIELD, CloudBoxColumns.ICON_DATA_FIELD);
        
        mPreferenceProjectionMap = new HashMap<String,String>();
//        mPreferenceProjectionMap.put(PreferenceColumns._ID, PreferenceColumns._ID);
        mPreferenceProjectionMap.put(PreferenceColumns.NAME, PreferenceColumns.NAME);
        mPreferenceProjectionMap.put(PreferenceColumns.VALUE, PreferenceColumns.VALUE);
        
        mMatchProjectionMap = new HashMap<String, String>();
        mMatchProjectionMap.put(MatchColumns.EXTENSION_FIELD, MatchColumns.EXTENSION_FIELD);
        mMatchProjectionMap.put(MatchColumns.CATEGORY_FIELD, MatchColumns.CATEGORY_FIELD);
        mMatchProjectionMap.put(MatchColumns.APP_FIELD, MatchColumns.APP_FIELD);
        mMatchProjectionMap.put(MatchColumns.DATE_FIELD, MatchColumns.DATE_FIELD);
        
	}
	
	private DatabaseHelper mOpenHelper;

	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
	    @Override
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			super.onOpen(db);
		}

		private static final String DATABASE_NAME = "hufeng.db";
	    private static final int DATABASE_VERSION = 6;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "execute create sqls ...start");
			db.execSQL(FileColumns.SQL.CREATE);
			db.execSQL(ImageColumns.SQL.CREATE);
			db.execSQL(AudioColumns.SQL.CREATE);
			db.execSQL(VideoColumns.SQL.CREATE);
			db.execSQL(ApkColumns.SQL.CREATE);
			db.execSQL(CategoryColumns.SQL.CREATE);
			db.execSQL(DocumentColumns.SQL.CREATE);
			db.execSQL(ZipColumns.SQL.CREATE);
			db.execSQL(FavoriteColumns.SQL.CREATE);
			db.execSQL(PreferenceColumns.SQL.CREATE);
			db.execSQL(MatchColumns.SQL.CREATE);
            db.execSQL(SelectedColumns.SQL.CREATE);
            db.execSQL(CloudBoxColumns.SQL.CREATE);
			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "execute create sqls ...end");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			if(newVersion==2)
			{
				if(oldVersion==1)
				{
					db.execSQL(AudioColumns.SQL.DROP);
					db.execSQL(AudioColumns.SQL.CREATE);
				}
			}
			else if(newVersion==3)
			{
				if(oldVersion==2 || oldVersion==1)
				{
					db.execSQL(FileColumns.SQL.DROP);
					db.execSQL(ImageColumns.SQL.DROP);
					db.execSQL(AudioColumns.SQL.DROP);
					db.execSQL(VideoColumns.SQL.DROP);
					db.execSQL(ApkColumns.SQL.DROP);
					db.execSQL(DocumentColumns.SQL.DROP);
					db.execSQL(ZipColumns.SQL.DROP);
					db.execSQL(FavoriteColumns.SQL.DROP);
					db.execSQL(CategoryColumns.SQL.DROP);
					
					db.execSQL(FileColumns.SQL.CREATE);
					db.execSQL(ImageColumns.SQL.CREATE);
					db.execSQL(AudioColumns.SQL.CREATE);
					db.execSQL(VideoColumns.SQL.CREATE);
					db.execSQL(ApkColumns.SQL.CREATE);
					db.execSQL(DocumentColumns.SQL.CREATE);
					db.execSQL(ZipColumns.SQL.CREATE);
					db.execSQL(FavoriteColumns.SQL.CREATE);
					db.execSQL(CategoryColumns.SQL.CREATE);
				}
			}else if(newVersion==4){
				if(oldVersion ==3 || oldVersion==2 || oldVersion==1) {
					db.execSQL(FileColumns.SQL.DROP);
					db.execSQL(ImageColumns.SQL.DROP);
					db.execSQL(AudioColumns.SQL.DROP);
					db.execSQL(VideoColumns.SQL.DROP);
					db.execSQL(ApkColumns.SQL.DROP);
					db.execSQL(DocumentColumns.SQL.DROP);
					db.execSQL(ZipColumns.SQL.DROP);
					db.execSQL(FavoriteColumns.SQL.DROP);
					db.execSQL(CategoryColumns.SQL.DROP);
					
					db.execSQL(FileColumns.SQL.CREATE);
					db.execSQL(ImageColumns.SQL.CREATE);
					db.execSQL(AudioColumns.SQL.CREATE);
					db.execSQL(VideoColumns.SQL.CREATE);
					db.execSQL(ApkColumns.SQL.CREATE);
					db.execSQL(DocumentColumns.SQL.CREATE);
					db.execSQL(ZipColumns.SQL.CREATE);
					db.execSQL(FavoriteColumns.SQL.CREATE);
					db.execSQL(CategoryColumns.SQL.CREATE);
				}
			} else if(newVersion == 5) {
                if(oldVersion ==4 || oldVersion==3 || oldVersion==2 || oldVersion==1) {
                    db.execSQL(FileColumns.SQL.DROP);
                    db.execSQL(ImageColumns.SQL.DROP);
                    db.execSQL(AudioColumns.SQL.DROP);
                    db.execSQL(VideoColumns.SQL.DROP);
                    db.execSQL(ApkColumns.SQL.DROP);
                    db.execSQL(DocumentColumns.SQL.DROP);
                    db.execSQL(ZipColumns.SQL.DROP);
                    db.execSQL(FavoriteColumns.SQL.DROP);
                    db.execSQL(CategoryColumns.SQL.DROP);
                    db.execSQL(SelectedColumns.SQL.DROP);
                    db.execSQL(CloudBoxColumns.SQL.DROP);

                    db.execSQL(FileColumns.SQL.CREATE);
                    db.execSQL(ImageColumns.SQL.CREATE);
                    db.execSQL(AudioColumns.SQL.CREATE);
                    db.execSQL(VideoColumns.SQL.CREATE);
                    db.execSQL(ApkColumns.SQL.CREATE);
                    db.execSQL(DocumentColumns.SQL.CREATE);
                    db.execSQL(ZipColumns.SQL.CREATE);
                    db.execSQL(FavoriteColumns.SQL.CREATE);
                    db.execSQL(CategoryColumns.SQL.CREATE);
                    db.execSQL(SelectedColumns.SQL.CREATE);
                    db.execSQL(CloudBoxColumns.SQL.CREATE);
                }
            } else if (newVersion==6) {
                if (oldVersion == 5) {
                    db.execSQL(FileColumns.SQL.DROP);
                    db.execSQL(ImageColumns.SQL.DROP);
                    db.execSQL(AudioColumns.SQL.DROP);
                    db.execSQL(VideoColumns.SQL.DROP);
                    db.execSQL(ApkColumns.SQL.DROP);
                    db.execSQL(DocumentColumns.SQL.DROP);
                    db.execSQL(ZipColumns.SQL.DROP);
                    db.execSQL(CategoryColumns.SQL.DROP);

                    db.execSQL(FileColumns.SQL.CREATE);
                    db.execSQL(ImageColumns.SQL.CREATE);
                    db.execSQL(AudioColumns.SQL.CREATE);
                    db.execSQL(VideoColumns.SQL.CREATE);
                    db.execSQL(ApkColumns.SQL.CREATE);
                    db.execSQL(DocumentColumns.SQL.CREATE);
                    db.execSQL(ZipColumns.SQL.CREATE);
                    db.execSQL(CategoryColumns.SQL.CREATE);
                } else {
                    db.execSQL(FileColumns.SQL.DROP);
                    db.execSQL(ImageColumns.SQL.DROP);
                    db.execSQL(AudioColumns.SQL.DROP);
                    db.execSQL(VideoColumns.SQL.DROP);
                    db.execSQL(ApkColumns.SQL.DROP);
                    db.execSQL(DocumentColumns.SQL.DROP);
                    db.execSQL(ZipColumns.SQL.DROP);
                    db.execSQL(FavoriteColumns.SQL.DROP);
                    db.execSQL(CategoryColumns.SQL.DROP);
                    db.execSQL(SelectedColumns.SQL.DROP);
                    db.execSQL(CloudBoxColumns.SQL.DROP);

                    db.execSQL(FileColumns.SQL.CREATE);
                    db.execSQL(ImageColumns.SQL.CREATE);
                    db.execSQL(AudioColumns.SQL.CREATE);
                    db.execSQL(VideoColumns.SQL.CREATE);
                    db.execSQL(ApkColumns.SQL.CREATE);
                    db.execSQL(DocumentColumns.SQL.CREATE);
                    db.execSQL(ZipColumns.SQL.CREATE);
                    db.execSQL(FavoriteColumns.SQL.CREATE);
                    db.execSQL(CategoryColumns.SQL.CREATE);
                    db.execSQL(SelectedColumns.SQL.CREATE);
                    db.execSQL(CloudBoxColumns.SQL.CREATE);
                }
            }
		}
		
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        String tablename = null;
        int type = -1;
        switch (URI_MATCHER.match(uri)) {
        	case FILES:
        		tablename = FileColumns.TABLE;
        		type = FileUtils.FILE_TYPE_FILE;
        		count = db.delete(FileColumns.TABLE, where, whereArgs);
        		break;
        	case FILE_ID:
        		tablename = FileColumns.TABLE;
        		type = FileUtils.FILE_TYPE_FILE;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(FileColumns.TABLE, where, whereArgs);
        		break;
        	case IMAGES:
        		tablename = ImageColumns.TABLE;
        		type = FileUtils.FILE_TYPE_IMAGE;
        		count = db.delete(ImageColumns.TABLE, where, whereArgs);
        		break;
        	case IMAGE_ID:
        		tablename = ImageColumns.TABLE;
        		type = FileUtils.FILE_TYPE_IMAGE;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(ImageColumns.TABLE, where, whereArgs);
        		break;
        	case AUDIOS:
        		tablename = AudioColumns.TABLE;
        		type = FileUtils.FILE_TYPE_AUDIO;
        		count = db.delete(AudioColumns.TABLE, where, whereArgs);
        		break;
        	case AUDIO_ID:
        		tablename = AudioColumns.TABLE;
        		type = FileUtils.FILE_TYPE_AUDIO;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(AudioColumns.TABLE, where, whereArgs);
        		break;
        	case VIDEOS:
        		tablename = VideoColumns.TABLE;
        		type = FileUtils.FILE_TYPE_VIDEO;
        		count = db.delete(VideoColumns.TABLE, where, whereArgs);
        		break;
        	case VIDEO_ID:
        		tablename = VideoColumns.TABLE;
        		type = FileUtils.FILE_TYPE_VIDEO;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(VideoColumns.TABLE, where, whereArgs);
        		break;
        	case APKS:
        		tablename = ApkColumns.TABLE;
        		type = FileUtils.FILE_TYPE_APK;
        		count = db.delete(ApkColumns.TABLE, where, whereArgs);
        		break;
        	case APK_ID:
        		tablename = ApkColumns.TABLE;
        		type = FileUtils.FILE_TYPE_APK;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(ApkColumns.TABLE, where, whereArgs);
        		break;
        	case DOCUMENTS:
        		tablename = DocumentColumns.TABLE;
        		type = FileUtils.FILE_TYPE_DOCUMENT;
        		count = db.delete(DocumentColumns.TABLE, where, whereArgs);
        		break;
        	case DOCUMENT_ID:
        		tablename = DocumentColumns.TABLE;
        		type = FileUtils.FILE_TYPE_DOCUMENT;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(DocumentColumns.TABLE, where, whereArgs);
        		break;
        	case ZIPS:
        		tablename = ZipColumns.TABLE;
        		type = FileUtils.FILE_TYPE_ZIP;
        		count = db.delete(ZipColumns.TABLE, where, whereArgs);
        		break;
        	case ZIP_ID:
        		tablename = ZipColumns.TABLE;
        		type = FileUtils.FILE_TYPE_ZIP;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(ZipColumns.TABLE, where, whereArgs);
        		break;
        	case FAVORITES:
        		count = db.delete(FavoriteColumns.TABLE, where, whereArgs);
        		break;
        	case FAVORITE_ID:
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(FavoriteColumns.TABLE, where, whereArgs);
        		break;
            case SELECTEDS:
                count = db.delete(SelectedColumns.TABLE, where, whereArgs);
                break;
            case SELECTED_ID:
                where = "_id=" + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
                count = db.delete(SelectedColumns.TABLE, where, whereArgs);
                break;
            case CLOUDS:
                count = db.delete(CloudBoxColumns.TABLE, where, whereArgs);
                break;
            case CLOUD_ID:
                where = "_id=" + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
                count = db.delete(CloudBoxColumns.TABLE, where, whereArgs);
                break;
        	case PREFERENCES:
        		count = db.delete(PreferenceColumns.TABLE, where, whereArgs);
        		break;
        	case MATCHS:
        		count = db.delete(MatchColumns.TABLE, where, whereArgs);
//        	case PREFERENCE_ID:
//        		where = "name=" + uri.getPathSegments().get(1)
//                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
//        		count = db.delete(ZipColumns.TABLE, where, whereArgs);
//        		break;
        		break;
        	default:
        		throw new IllegalArgumentException("Unknown URI " + uri);
        }
        LogUtil.i(LOG_TAG, "delete:" + uri.toString() + "," + (whereArgs==null?"":whereArgs[0]) + " return "+count);
        if(count>0 && type!=-1){
        	updateCategoryData(db,tablename, type);
        }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch(URI_MATCHER.match(uri))
		{
		case FILES:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.file";
		case FILE_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.file";
		case IMAGES:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.image";
		case IMAGE_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.image";
		case AUDIOS:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.audio";
		case AUDIO_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.audio";
		case VIDEOS:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.video";
		case VIDEO_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.video";
		case APKS:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.apk";
		case APK_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.apk";
		case DOCUMENTS:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.document";
		case DOCUMENT_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.document";
		case ZIPS:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.zip";
		case ZIP_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.zip";
		case CATEGORYS:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.category";
		case CATEGORY_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.category";
		case FAVORITES:
			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.favorite";
		case FAVORITE_ID:
			return "vnd.android.cursor.item/vnd.hufeng.filemanager.favorite";
        case SELECTEDS:
            return "vnd.android.cursor.dir/vnd.hufeng.filemanager.selected";
        case SELECTED_ID:
            return "vnd.android.cursor.item/vnd.hufeng.filemanager.selected";
        case CLOUDS:
            return "vnd.android.cursor.dir/vnd.hufeng.filemanager.cloud";
        case CLOUD_ID:
            return "vnd.android.cursor.item/vnd.hufeng.filemanager.cloud";
//		case PREFERENCE_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.preference";
		default:
            throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        LogUtil.i(LOG_TAG, "insert " + uri + " " + values);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = 0;
        String tablename = null;
        int type = -1;
        switch(URI_MATCHER.match(uri)){
	        case FILES:
                if (!values.containsKey(FileColumns.FILE_SYNC_FIELD)) {
                    values.put(FileColumns.FILE_SYNC_FIELD , 0);
                }
	        	tablename = FileColumns.TABLE;
	        	type = FileUtils.FILE_TYPE_FILE;
	        	rowId = db.insert(FileColumns.TABLE, null, values);
	        	break;
	        case IMAGES:
                if (!values.containsKey(FileColumns.FILE_SYNC_FIELD)) {
                    values.put(FileColumns.FILE_SYNC_FIELD , 0);
                }
                tablename = ImageColumns.TABLE;
	        	type = FileUtils.FILE_TYPE_IMAGE;
	        	rowId = db.insert(ImageColumns.TABLE, null, values);
	        	break;
	        case AUDIOS:
                if (!values.containsKey(FileColumns.FILE_SYNC_FIELD)) {
                    values.put(FileColumns.FILE_SYNC_FIELD , 0);
                }
                tablename = AudioColumns.TABLE;
	        	type = FileUtils.FILE_TYPE_AUDIO;
	        	rowId = db.insert(AudioColumns.TABLE, null, values);
	        	break;
	        case VIDEOS:
                if (!values.containsKey(FileColumns.FILE_SYNC_FIELD)) {
                    values.put(FileColumns.FILE_SYNC_FIELD , 0);
                }
                tablename = VideoColumns.TABLE;
	        	type = FileUtils.FILE_TYPE_VIDEO;
	        	rowId = db.insert(VideoColumns.TABLE, null, values);
	        	break;
	        case APKS:
                if (!values.containsKey(FileColumns.FILE_SYNC_FIELD)) {
                    values.put(FileColumns.FILE_SYNC_FIELD , 0);
                }
                tablename = ApkColumns.TABLE;
	        	type = FileUtils.FILE_TYPE_APK;
	        	rowId = db.insert(ApkColumns.TABLE, null, values);
	        	break;
	        case CATEGORYS:
	        	rowId = db.insert(CategoryColumns.TABLE, null, values);
	        	break;
	        case DOCUMENTS:
                if (!values.containsKey(FileColumns.FILE_SYNC_FIELD)) {
                    values.put(FileColumns.FILE_SYNC_FIELD , 0);
                }
                tablename = DocumentColumns.TABLE;
	        	type = FileUtils.FILE_TYPE_DOCUMENT;
	        	rowId = db.insert(DocumentColumns.TABLE, null, values);
	        	break;
	        case ZIPS:
                if (!values.containsKey(FileColumns.FILE_SYNC_FIELD)) {
                    values.put(FileColumns.FILE_SYNC_FIELD , 0);
                }
                tablename = ZipColumns.TABLE;
	        	type = FileUtils.FILE_TYPE_ZIP;
	        	rowId = db.insert(ZipColumns.TABLE, null, values);
	        	break;
	        case FAVORITES:
	        	rowId = db.insert(FavoriteColumns.TABLE, null, values);
	        	break;
            case SELECTEDS:
                rowId = db.insert(SelectedColumns.TABLE, null, values);
                break;
            case CLOUDS:
                rowId = db.insert(CloudBoxColumns.TABLE, null, values);
                break;
	        case PREFERENCES:
	        	db.insert(PreferenceColumns.TABLE, null, values);
	        	return null;
	        case MATCHS:
	        	db.insert(MatchColumns.TABLE, null, values);
	        	return null;
	        default:
	        	throw new SQLException("Failed to insert row into " + uri);	
        }
        if (rowId > 0) {
        	if(type!=-1){
                updateCategoryData(db,tablename,type);
        	}
            Uri uri_with_id = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(uri_with_id, null);
            return uri_with_id;
        }
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
//		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "oncreate of provider ... start");
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String orderBy;
        switch (URI_MATCHER.match(uri)) {
            case FILES:
            	qb.setTables(FileColumns.TABLE);
            	qb.setProjectionMap(mFileProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = FileColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case FILE_ID:
            	qb.setTables(FileColumns.TABLE);
            	qb.setProjectionMap(mFileProjectionMap);
            	qb.appendWhere(FileColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = FileColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case IMAGES:
            	qb.setTables(ImageColumns.TABLE);
            	qb.setProjectionMap(mImageProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ImageColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case IMAGE_ID:
            	qb.setTables(ImageColumns.TABLE);
            	qb.setProjectionMap(mImageProjectionMap);
            	qb.appendWhere(ImageColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ImageColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case AUDIOS:
            	qb.setTables(AudioColumns.TABLE);
            	qb.setProjectionMap(mAudioProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = AudioColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case AUDIO_ID:
            	qb.setTables(AudioColumns.TABLE);
            	qb.setProjectionMap(mAudioProjectionMap);
            	qb.appendWhere(AudioColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = AudioColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case VIDEOS:
            	qb.setTables(VideoColumns.TABLE);
            	qb.setProjectionMap(mVideoProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = VideoColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case VIDEO_ID:
            	qb.setTables(VideoColumns.TABLE);
            	qb.setProjectionMap(mVideoProjectionMap);
            	qb.appendWhere(VideoColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = VideoColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case APKS:
            	qb.setTables(ApkColumns.TABLE);
            	qb.setProjectionMap(mApkProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ApkColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case APK_ID:
            	qb.setTables(ApkColumns.TABLE);
            	qb.setProjectionMap(mApkProjectionMap);
            	qb.appendWhere(ApkColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ApkColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case CATEGORYS:
            	qb.setTables(CategoryColumns.TABLE);
            	qb.setProjectionMap(mCategoryProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = CategoryColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case DOCUMENTS:
            	qb.setTables(DocumentColumns.TABLE);
            	qb.setProjectionMap(mDocumentProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = DocumentColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case DOCUMENT_ID:
            	qb.setTables(DocumentColumns.TABLE);
            	qb.setProjectionMap(mDocumentProjectionMap);
            	qb.appendWhere(DocumentColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = DocumentColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case ZIPS:
            	qb.setTables(ZipColumns.TABLE);
            	qb.setProjectionMap(mZipProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ZipColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case ZIP_ID:
            	qb.setTables(ZipColumns.TABLE);
            	qb.setProjectionMap(mZipProjectionMap);
            	qb.appendWhere(ZipColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ZipColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case FAVORITES:
            	qb.setTables(FavoriteColumns.TABLE);
            	qb.setProjectionMap(mFavoriteProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = FavoriteColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case SELECTEDS:
                qb.setTables(SelectedColumns.TABLE);
                qb.setProjectionMap(mSelectedProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = SelectedColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case SELECTED_ID:
                qb.setTables(SelectedColumns.TABLE);
                qb.setProjectionMap(mSelectedProjectionMap);
                qb.appendWhere(SelectedColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = SelectedColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case CLOUDS:
                qb.setTables(CloudBoxColumns.TABLE);
                qb.setProjectionMap(mCloudProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = CloudBoxColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case CLOUD_ID:
                qb.setTables(CloudBoxColumns.TABLE);
                qb.setProjectionMap(mSelectedProjectionMap);
                qb.appendWhere(CloudBoxColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = CloudBoxColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case PREFERENCES:
            	qb.setTables(PreferenceColumns.TABLE);
            	qb.setProjectionMap(mPreferenceProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = PreferenceColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            case MATCHS:
            	qb.setTables(MatchColumns.TABLE);
            	qb.setProjectionMap(mMatchProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = MatchColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
            	break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy, null);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count  = 0;
        switch (URI_MATCHER.match(uri)) {
	        case FILES:
	            count = db.update(FileColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case FILE_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(FileColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case IMAGES:
	            count = db.update(ImageColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case IMAGE_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(ImageColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case AUDIOS:
	            count = db.update(AudioColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case AUDIO_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(AudioColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case VIDEOS:
	            count = db.update(VideoColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case VIDEO_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(VideoColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case APKS:
	            count = db.update(ApkColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case APK_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(ApkColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case DOCUMENTS:
	            count = db.update(DocumentColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case DOCUMENT_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(DocumentColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case ZIPS:
	            count = db.update(ZipColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case ZIP_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(ZipColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case CATEGORYS:
	            count = db.update(CategoryColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case CATEGORY_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(CategoryColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case FAVORITES:
	            count = db.update(FavoriteColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case FAVORITE_ID:
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(FavoriteColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
            case SELECTEDS:
                count = db.update(SelectedColumns.TABLE, values, selection,
                        selectionArgs);
                break;
            case SELECTED_ID:
                selection = "_id=" + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.update(SelectedColumns.TABLE, values, selection,
                        selectionArgs);
                break;
            case CLOUDS:
                count = db.update(CloudBoxColumns.TABLE, values, selection,
                        selectionArgs);
                break;
            case CLOUD_ID:
                selection = "_id=" + uri.getPathSegments().get(1)
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
                count = db.update(CloudBoxColumns.TABLE, values, selection,
                        selectionArgs);
                break;
//	        case PREFERENCES:
//	            count = db.update(PreferenceColumns.TABLE, values, selection,
//	                    selectionArgs);
//	            break;
//	        case PREFERENCE_ID:
//	            selection = "name=" + uri.getPathSegments().get(1)
//	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
//	            count = db.update(PreferenceColumns.TABLE, values, selection,
//	                    selectionArgs);
//	            break;
	        case PREFERENCES:
                if (selectionArgs == null) {
                    LogUtil.e(LOG_TAG, "selectinArgs is null");
                    throw new NullPointerException("selectionArgs could not be null ");
                }

                count = db.update(PreferenceColumns.TABLE, values, selection, selectionArgs);
                if (count <=0) {
                    values.put(PreferenceColumns.NAME, selectionArgs[0]);
                    insert(PreferenceColumns.CONTENT_URI, values);
                }
                break;
	        case MATCHS:
	        	if (selectionArgs == null) {
                    LogUtil.e(LOG_TAG, "selectinArgs is null");
                    throw new NullPointerException("selectionArgs could not be null ");
                }

                count = db.update(MatchColumns.TABLE, values, selection, selectionArgs);
                if (count <=0) {
                    values.put(MatchColumns.EXTENSION_FIELD, selectionArgs[0]);
                    insert(MatchColumns.CONTENT_URI, values);
                }
                break;
	      	default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		// TODO Auto-generated method stub
        int insertedNumber = 0;
        String tablename = null;
        int type = -1;
        switch (URI_MATCHER.match(uri)) {
        case FILES:
        	tablename = FileColumns.TABLE;
        	type = FileUtils.FILE_TYPE_FILE;
        	break;
        case IMAGES:
        	tablename = ImageColumns.TABLE;
        	type = FileUtils.FILE_TYPE_IMAGE;
        	break;
        case AUDIOS:
        	tablename = AudioColumns.TABLE;
        	type = FileUtils.FILE_TYPE_AUDIO;
        	break;
        case VIDEOS:
        	tablename = VideoColumns.TABLE;
        	type = FileUtils.FILE_TYPE_VIDEO;
        	break;
        case APKS:
        	tablename = ApkColumns.TABLE;
        	type = FileUtils.FILE_TYPE_APK;
        	break;
        case CATEGORYS:
        	tablename = CategoryColumns.TABLE;
        	break;
        case DOCUMENTS:
        	tablename = DocumentColumns.TABLE;
        	type = FileUtils.FILE_TYPE_DOCUMENT;
        	break;
        case ZIPS:
        	tablename = ZipColumns.TABLE;
        	type = FileUtils.FILE_TYPE_ZIP;
        	break;
        case FAVORITES:
        	tablename = FavoriteColumns.TABLE;
        	break;
        case SELECTEDS:
            tablename = SelectedColumns.TABLE;
            break;
        case CLOUDS:
            tablename = CloudBoxColumns.TABLE;
            break;
//        case PREFERENCES:
//        	tablename = PreferenceColumns.TABLE;
//        	break;
        }
        if(tablename==null)
        	return 0;

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        InsertHelper ih = new InsertHelper(db, tablename);
        db.beginTransaction();
        try {
        	
        	if (values.length < 1) {
        		return 0;
        	}
        	
			// put column information in this HashMap, key is column name , and
			// int[] is information which include column index and data type;
//			HashMap<String, String[]> columnInfoMap = new HashMap<String, String[]>();
//        	Iterator<Map.Entry<String, Object>> iterator = values[0].valueSet().iterator();
//        	while (iterator.hasNext()) {
//            	Entry<String, Object> entry = iterator.next();
//            	String[] columnInfo = new String[2];
//            	columnInfo[0] = ih.getColumnIndex(entry.getKey())+"";
//            	columnInfo[1] = entry.getValue().getClass().getName();
//            	columnInfoMap.put(entry.getKey(), columnInfo);
//            }
            for (ContentValues value : values) {
            	
            	// Get the InsertHelper ready to insert a single row
                ih.prepareForInsert();
     
                // Add the data for each column
                Iterator<Map.Entry<String, Object>> iterator = value.valueSet().iterator();
                while (iterator.hasNext()) {
                	Entry<String, Object> entry = iterator.next();
                	String key = entry.getKey();
                	int index = ih.getColumnIndex(key);
                	Object data = entry.getValue();
                	if(data!=null)
                	{
	                	String dataType = data.getClass().getName();
	                	if ("java.lang.String".equals(dataType)) {
	                		ih.bind(index, (String)entry.getValue());
	                	} else if ("java.lang.Integer".equals(dataType)) {
	                		ih.bind(index, (Integer)entry.getValue());
	                	} else if ("java.lang.Boolean".equals(dataType)) {
	                		ih.bind(index, (Boolean)entry.getValue());
	                	} else if ("java.lang.Long".equals(dataType)) {
	                		ih.bind(index, (Long)entry.getValue());
	                	} else if ("java.lang.Float".equals(dataType)) {
	                		ih.bind(index, (Float)entry.getValue());
	                	} else {
	                		ih.bind(index, (byte[])entry.getValue());
	                	}
                	}
                	else
                	{
                		ih.bind(index, (byte[])null);
                	}
                	
                }
     
                // Insert the row into the database.
                ih.execute();
//                insert(uri, value);
            }
            db.setTransactionSuccessful();
            insertedNumber = values.length;
        } finally {
            db.endTransaction();
            ih.close();
        }

        if(type!=-1){
        	updateCategoryData(db, tablename, type);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        
        return insertedNumber;
	}
	
	private void updateCategoryData(SQLiteDatabase db, String tablename, int type){
		
        String[] projection = new String[] { "count(*) as count", "sum("+FileColumns.FILE_SIZE_FIELD+") as size", FileColumns.FILE_STORAGE_FIELD + " as storage"  };
        Cursor cursor = db.query(tablename, projection, 
        		/*FileColumns.FILE_TYPE_FIELD+"=?", 
        		new String[]{""+type},*/
        		null,
        		null,
        		FileColumns.FILE_STORAGE_FIELD, 
        		null, null);
        boolean flag = false;
        ArrayList<String> stors = new ArrayList<String>();
        boolean flag_find = false;
        if(cursor!=null){
	        while(cursor.moveToNext()){
                flag_find = true;
		        int count = cursor.getInt(0);
		        long size = cursor.getLong(1);
		        String storage = cursor.getString(2);
		        if(LogUtil.IDBG) LogUtil.i(LOG_TAG , tablename+" has count="+count+" size="+size);
		        ContentValues cvs = new ContentValues();
		        cvs.put(CategoryColumns.NUMBER_FIELD, count);
		        cvs.put(CategoryColumns.SIZE_FIELD, size);
		        int row = db.update(CategoryColumns.TABLE, cvs, 
		        		CategoryColumns.CATEGORY_FIELD+"=? AND "+CategoryColumns.STORAGE_FIELD+"=?",new String[]{type+"", storage});
		        long id = 0;
		        if(row<1){
		        	cvs.put(CategoryColumns.CATEGORY_FIELD, type);
		        	cvs.put(CategoryColumns.STORAGE_FIELD, storage);
		        	id = db.insert(CategoryColumns.TABLE, null, cvs);
		        }
		        if(row>0 || id>0){
		        	flag = true;
		        }
                stors.add(storage);
	        }
        	cursor.close();

        }
        if (!flag_find) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(CategoryColumns.CATEGORY_FIELD).append("=").append(type);
            if (stors.size() > 0) {
                buffer.append(" AND ").append(CategoryColumns.STORAGE_FIELD).append(" NOT IN (");
                for (int i = 0; i < stors.size(); i++) {
                    if (i == 0) {
                        buffer.append('\'');
                        buffer.append(stors.get(i).replace("'", "\""));
                        buffer.append('\'');
                    } else {
                        buffer.append(',');
                        buffer.append('\'');
                        buffer.append(stors.get(i).replace("'", "\""));
                        buffer.append('\'');
                    }
                }
                buffer.append(")");
            }
            ContentValues cvs = new ContentValues();
            cvs.put(CategoryColumns.NUMBER_FIELD, 0);
            cvs.put(CategoryColumns.SIZE_FIELD, 0);
            int row = db.update(CategoryColumns.TABLE, cvs, buffer.toString(),null);
            if (row > 0) {
                flag = true;
            }
        }
        if(flag)
            getContext().getContentResolver().notifyChange(CategoryColumns.CONTENT_URI, null);
	}

}
