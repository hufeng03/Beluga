package com.belugamobile.filemanager.provider;

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

import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.provider.DataStructures.ApkColumns;
import com.belugamobile.filemanager.provider.DataStructures.AudioColumns;
import com.belugamobile.filemanager.provider.DataStructures.CategoryColumns;
import com.belugamobile.filemanager.provider.DataStructures.CloudBoxColumns;
import com.belugamobile.filemanager.provider.DataStructures.DocumentColumns;
import com.belugamobile.filemanager.provider.DataStructures.FavoriteColumns;
import com.belugamobile.filemanager.provider.DataStructures.FileColumns;
import com.belugamobile.filemanager.provider.DataStructures.ImageColumns;
import com.belugamobile.filemanager.provider.DataStructures.MatchColumns;
import com.belugamobile.filemanager.provider.DataStructures.PreferenceColumns;
import com.belugamobile.filemanager.provider.DataStructures.SelectedColumns;
import com.belugamobile.filemanager.provider.DataStructures.VideoColumns;
import com.belugamobile.filemanager.provider.DataStructures.ZipColumns;
import com.belugamobile.filemanager.utils.LogUtil;

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
	private static final int ID = 2;
	private static final int IMAGES = 3;
	private static final int IMAGE_ID = 4;
	private static final int AUDIOS = 5;
	private static final int AUDIO_ID = 6;
	private static final int APKS = 7;
	private static final int APK_ID = 8;
	private static final int VIDEOS = 9;
	private static final int VIDEO_ID = 10;
	private static final int CATEGORIES = 11;
	private static final int CATEGORY_ID = 12;
	private static final int DOCUMENTS = 13;
	private static final int DOCUMENT_ID = 14;
	private static final int ZIPS = 15;
	private static final int ZIP_ID = 16;
	private static final int FAVORITES = 17;
	private static final int FAVORITE_ID = 18;
	private static final int PREFERENCES = 19;
	private static final int MATCHES = 20;
//	private static final int PREFERENCE_ID = 20;
    private static final int SELECTED_ID = 21;
    private static final int SELECTEDS = 22;
    private static final int CLOUD_ID = 23;
    private static final int CLOUDS = 24;
	
	
	private static final UriMatcher URI_MATCHER;
	
	static{
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FileColumns.TABLE, FILES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FileColumns.TABLE+"/#", ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ImageColumns.TABLE, IMAGES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ImageColumns.TABLE+"/#", IMAGE_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, AudioColumns.TABLE, AUDIOS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, AudioColumns.TABLE+"/#", AUDIO_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, VideoColumns.TABLE, VIDEOS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, VideoColumns.TABLE+"/#", VIDEO_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ApkColumns.TABLE, APKS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ApkColumns.TABLE+"/#", APK_ID); 
        URI_MATCHER.addURI(DataStructures.AUTHORITY, CategoryColumns.TABLE, CATEGORIES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, CategoryColumns.TABLE+"/#", CATEGORY_ID); 
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DocumentColumns.TABLE, DOCUMENTS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DocumentColumns.TABLE+"/#", DOCUMENT_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ZipColumns.TABLE, ZIPS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, ZipColumns.TABLE+"/#", ZIP_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FavoriteColumns.TABLE, FAVORITES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, FavoriteColumns.TABLE+"/#", FAVORITE_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, PreferenceColumns.TABLE, PREFERENCES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, MatchColumns.TABLE, MATCHES);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.SelectedColumns.TABLE, SELECTEDS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.SelectedColumns.TABLE+"/#", SELECTED_ID);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.CloudBoxColumns.TABLE, CLOUDS);
        URI_MATCHER.addURI(DataStructures.AUTHORITY, DataStructures.CloudBoxColumns.TABLE+"/#", CLOUD_ID);
        
//        URI_MATCHER.addURI(DataStructures.AUTHORITY, PreferenceColumns.TABLE+"/#", PREFERENCE_ID);
        
        mFileProjectionMap = new HashMap<String,String>();
        mFileProjectionMap.put(FileColumns._ID, FileColumns.TABLE + "." + FileColumns._ID);
        mFileProjectionMap.put(FileColumns.DATE, FileColumns.TABLE + "." + FileColumns.DATE);
        mFileProjectionMap.put(FileColumns.TYPE, FileColumns.TABLE + "." + FileColumns.TYPE);
        mFileProjectionMap.put(FileColumns.SIZE, FileColumns.TABLE + "." + FileColumns.SIZE);
        mFileProjectionMap.put(FileColumns.PATH, FileColumns.TABLE + "." + FileColumns.PATH);
        mFileProjectionMap.put(FileColumns.NAME, FileColumns.TABLE + "." + FileColumns.NAME);
        mFileProjectionMap.put(FileColumns.EXTENSION, FileColumns.TABLE + "." + FileColumns.EXTENSION);
        mFileProjectionMap.put(FileColumns.STORAGE, FileColumns.TABLE + "." + FileColumns.STORAGE);
        mFileProjectionMap.put(FileColumns.SYNC, FileColumns.TABLE + "." + FileColumns.SYNC);
        mFileProjectionMap.put("favorite_id", FavoriteColumns.TABLE + "." + FavoriteColumns._ID + " AS "+"favorite_id");
        
        mImageProjectionMap = new HashMap<String,String>();
        mImageProjectionMap.put(ImageColumns._ID, ImageColumns.TABLE + "." + ImageColumns._ID);
        mImageProjectionMap.put(ImageColumns.DATE, ImageColumns.TABLE + "." + ImageColumns.DATE);
        mImageProjectionMap.put(ImageColumns.TYPE, ImageColumns.TABLE + "." + ImageColumns.TYPE);
        mImageProjectionMap.put(ImageColumns.SIZE, ImageColumns.TABLE + "." + ImageColumns.SIZE);
        mImageProjectionMap.put(ImageColumns.PATH, ImageColumns.TABLE + "." + ImageColumns.PATH);
        mImageProjectionMap.put(ImageColumns.NAME, ImageColumns.TABLE + "." + ImageColumns.NAME);
        mImageProjectionMap.put(ImageColumns.EXTENSION, ImageColumns.TABLE + "." + ImageColumns.EXTENSION);
        mImageProjectionMap.put(ImageColumns.STORAGE, ImageColumns.TABLE + "." + ImageColumns.STORAGE);
        mImageProjectionMap.put(ImageColumns.SYNC, ImageColumns.TABLE + "." + ImageColumns.SYNC);
        mImageProjectionMap.put("favorite_id", FavoriteColumns.TABLE + "." + FavoriteColumns._ID + " AS "+"favorite_id");
        mImageProjectionMap.put(ImageColumns.IMAGE_WIDTH, ImageColumns.TABLE + "." + ImageColumns.IMAGE_WIDTH);
        mImageProjectionMap.put(ImageColumns.IMAGE_HEIGHT, ImageColumns.TABLE + "." + ImageColumns.IMAGE_HEIGHT);
  
        mAudioProjectionMap = new HashMap<String,String>();
        mAudioProjectionMap.put(AudioColumns._ID, AudioColumns.TABLE + "." + AudioColumns._ID);
        mAudioProjectionMap.put(AudioColumns.DATE, AudioColumns.TABLE + "." + AudioColumns.DATE);
        mAudioProjectionMap.put(AudioColumns.TYPE, AudioColumns.TABLE + "." + AudioColumns.TYPE);
        mAudioProjectionMap.put(AudioColumns.SIZE, AudioColumns.TABLE + "." + AudioColumns.SIZE);
        mAudioProjectionMap.put(AudioColumns.PATH, AudioColumns.TABLE + "." + AudioColumns.PATH);
        mAudioProjectionMap.put(AudioColumns.NAME, AudioColumns.TABLE + "." + AudioColumns.NAME);
        mAudioProjectionMap.put(AudioColumns.EXTENSION, AudioColumns.TABLE + "." + AudioColumns.EXTENSION);
        mAudioProjectionMap.put(AudioColumns.STORAGE, AudioColumns.TABLE + "." + AudioColumns.STORAGE);
        mAudioProjectionMap.put(AudioColumns.SYNC, AudioColumns.TABLE + "." + AudioColumns.SYNC);
        mAudioProjectionMap.put("favorite_id", FavoriteColumns.TABLE + "." + FavoriteColumns._ID + " AS "+"favorite_id");
        mAudioProjectionMap.put(AudioColumns.PLAY_DURATION, AudioColumns.TABLE + "." + AudioColumns.PLAY_DURATION);
        mAudioProjectionMap.put(AudioColumns.ALBUM, AudioColumns.TABLE + "." + AudioColumns.ALBUM);
        mAudioProjectionMap.put(AudioColumns.SINGER, AudioColumns.TABLE + "." + AudioColumns.SINGER);
        mAudioProjectionMap.put(AudioColumns.TITLE, AudioColumns.TABLE + "." + AudioColumns.TITLE);
        
        mVideoProjectionMap = new HashMap<String,String>();
        mVideoProjectionMap.put(VideoColumns._ID, VideoColumns.TABLE + "." + VideoColumns._ID);
        mVideoProjectionMap.put(VideoColumns.DATE, VideoColumns.TABLE + "." + VideoColumns.DATE);
        mVideoProjectionMap.put(VideoColumns.TYPE, VideoColumns.TABLE + "." + VideoColumns.TYPE);
        mVideoProjectionMap.put(VideoColumns.SIZE, VideoColumns.TABLE + "." + VideoColumns.SIZE);
        mVideoProjectionMap.put(VideoColumns.NAME, VideoColumns.TABLE + "." + VideoColumns.NAME);
        mVideoProjectionMap.put(VideoColumns.PATH, VideoColumns.TABLE + "." + VideoColumns.PATH);
        mVideoProjectionMap.put(VideoColumns.EXTENSION, VideoColumns.TABLE + "." + VideoColumns.EXTENSION);
        mVideoProjectionMap.put(VideoColumns.STORAGE, VideoColumns.TABLE + "." + VideoColumns.STORAGE);
        mVideoProjectionMap.put(VideoColumns.SYNC, VideoColumns.TABLE + "." + VideoColumns.SYNC);
        mVideoProjectionMap.put("favorite_id", FavoriteColumns.TABLE + "." + FavoriteColumns._ID + " AS "+"favorite_id");
        mVideoProjectionMap.put(VideoColumns.PLAY_DURATION, VideoColumns.TABLE + "." + VideoColumns.PLAY_DURATION);
        
        mApkProjectionMap = new HashMap<String,String>();
        mApkProjectionMap.put(ApkColumns._ID, ApkColumns.TABLE + "." + ApkColumns._ID);
        mApkProjectionMap.put(ApkColumns.DATE, ApkColumns.TABLE + "." + ApkColumns.DATE);
        mApkProjectionMap.put(ApkColumns.TYPE, ApkColumns.TABLE + "." + ApkColumns.TYPE);
        mApkProjectionMap.put(ApkColumns.SIZE, ApkColumns.TABLE + "." + ApkColumns.SIZE);
        mApkProjectionMap.put(ApkColumns.NAME, ApkColumns.TABLE + "." + ApkColumns.NAME);
        mApkProjectionMap.put(ApkColumns.PATH, ApkColumns.TABLE + "." + ApkColumns.PATH);
        mApkProjectionMap.put(ApkColumns.EXTENSION, ApkColumns.TABLE + "." + ApkColumns.EXTENSION);
        mApkProjectionMap.put(ApkColumns.STORAGE, ApkColumns.TABLE + "." + ApkColumns.STORAGE);
        mApkProjectionMap.put(ApkColumns.SYNC, ApkColumns.TABLE + "." + ApkColumns.SYNC);
        mApkProjectionMap.put("favorite_id", FavoriteColumns.TABLE + "." + FavoriteColumns._ID + " AS "+"favorite_id");

        mDocumentProjectionMap = new HashMap<String,String>();
        mDocumentProjectionMap.put(DocumentColumns._ID, DocumentColumns.TABLE + "." + DocumentColumns._ID);
        mDocumentProjectionMap.put(DocumentColumns.DATE, DocumentColumns.TABLE + "." + DocumentColumns.DATE);
        mDocumentProjectionMap.put(DocumentColumns.TYPE, DocumentColumns.TABLE + "." + DocumentColumns.TYPE);
        mDocumentProjectionMap.put(DocumentColumns.SIZE, DocumentColumns.TABLE + "." + DocumentColumns.SIZE);
        mDocumentProjectionMap.put(DocumentColumns.NAME, DocumentColumns.TABLE + "." + DocumentColumns.NAME);
        mDocumentProjectionMap.put(DocumentColumns.PATH, DocumentColumns.TABLE + "." + DocumentColumns.PATH);
        mDocumentProjectionMap.put(DocumentColumns.STORAGE, DocumentColumns.TABLE + "." + DocumentColumns.STORAGE);
        mDocumentProjectionMap.put(DocumentColumns.EXTENSION, DocumentColumns.TABLE + "." + DocumentColumns.EXTENSION);
        mDocumentProjectionMap.put(DocumentColumns.SYNC, DocumentColumns.TABLE + "." + DocumentColumns.SYNC);
        mDocumentProjectionMap.put("favorite_id", FavoriteColumns.TABLE + "." + FavoriteColumns._ID + " AS "+"favorite_id");

        mZipProjectionMap = new HashMap<String,String>();
        mZipProjectionMap.put(ZipColumns._ID, ZipColumns.TABLE + "." + ZipColumns._ID);
        mZipProjectionMap.put(ZipColumns.DATE, ZipColumns.TABLE + "." + ZipColumns.DATE);
        mZipProjectionMap.put(ZipColumns.TYPE, ZipColumns.TABLE + "." + ZipColumns.TYPE);
        mZipProjectionMap.put(ZipColumns.SIZE, ZipColumns.TABLE + "." + ZipColumns.SIZE);
        mZipProjectionMap.put(ZipColumns.NAME, ZipColumns.TABLE + "." + ZipColumns.NAME);
        mZipProjectionMap.put(ZipColumns.PATH, ZipColumns.TABLE + "." + ZipColumns.PATH);
        mZipProjectionMap.put(ZipColumns.EXTENSION, ZipColumns.TABLE + "." + ZipColumns.EXTENSION);
        mZipProjectionMap.put(ZipColumns.STORAGE, ZipColumns.TABLE + "." + ZipColumns.STORAGE);
        mZipProjectionMap.put(ZipColumns.SYNC, ZipColumns.TABLE + "." + ZipColumns.SYNC);
        mZipProjectionMap.put("favorite_id", FavoriteColumns.TABLE + "." + FavoriteColumns._ID + " AS "+"favorite_id");

        mFavoriteProjectionMap = new HashMap<String,String>();
        mFavoriteProjectionMap.put(FavoriteColumns._ID, FavoriteColumns._ID);
        mFavoriteProjectionMap.put(FavoriteColumns.DATE, FavoriteColumns.DATE);
        mFavoriteProjectionMap.put(FavoriteColumns.TYPE, FavoriteColumns.TYPE);
        mFavoriteProjectionMap.put(FavoriteColumns.SIZE, FavoriteColumns.SIZE);
        mFavoriteProjectionMap.put(FavoriteColumns.PATH, FavoriteColumns.PATH);
        mFavoriteProjectionMap.put(FavoriteColumns.NAME, FavoriteColumns.NAME);
        mFavoriteProjectionMap.put(FavoriteColumns.EXTENSION, FavoriteColumns.EXTENSION);
        mFavoriteProjectionMap.put(FavoriteColumns.STORAGE, FavoriteColumns.STORAGE);
        mFavoriteProjectionMap.put(FavoriteColumns.IS_DIRECTORY, FavoriteColumns.IS_DIRECTORY);
        mFavoriteProjectionMap.put(FavoriteColumns.SYNC, FavoriteColumns.SYNC);

        mCategoryProjectionMap = new HashMap<String,String>();
        mCategoryProjectionMap.put(CategoryColumns._ID, CategoryColumns._ID);
        mCategoryProjectionMap.put(CategoryColumns.CATEGORY, CategoryColumns.CATEGORY);
        mCategoryProjectionMap.put(CategoryColumns.SIZE, CategoryColumns.SIZE);
        mCategoryProjectionMap.put(CategoryColumns.NUMBER, CategoryColumns.NUMBER);
        mCategoryProjectionMap.put(CategoryColumns.STORAGE, CategoryColumns.STORAGE);

        mSelectedProjectionMap = new HashMap<String,String>();
        mSelectedProjectionMap.put(SelectedColumns._ID, SelectedColumns._ID);
        mSelectedProjectionMap.put(SelectedColumns.DATE, SelectedColumns.DATE);
        mSelectedProjectionMap.put(SelectedColumns.TYPE, SelectedColumns.TYPE);
        mSelectedProjectionMap.put(SelectedColumns.SIZE, SelectedColumns.SIZE);
        mSelectedProjectionMap.put(SelectedColumns.NAME, SelectedColumns.NAME);
        mSelectedProjectionMap.put(SelectedColumns.PATH, SelectedColumns.PATH);
        mSelectedProjectionMap.put(SelectedColumns.EXTENSION, SelectedColumns.EXTENSION);
        mSelectedProjectionMap.put(SelectedColumns.STORAGE, SelectedColumns.STORAGE);
        mSelectedProjectionMap.put(SelectedColumns.SYNC, SelectedColumns.SYNC);
        mSelectedProjectionMap.put(SelectedColumns.URL, SelectedColumns.URL);
        mSelectedProjectionMap.put(SelectedColumns.SERVER_NAME, SelectedColumns.SERVER_NAME);
        mSelectedProjectionMap.put(SelectedColumns.PACKAGE, SelectedColumns.PACKAGE);
        mSelectedProjectionMap.put(SelectedColumns.VERSION, SelectedColumns.VERSION);
        mSelectedProjectionMap.put(SelectedColumns.VERSION_NAME, SelectedColumns.VERSION_NAME);
        mSelectedProjectionMap.put(SelectedColumns.SERVER_DATE, SelectedColumns.SERVER_DATE);
        mSelectedProjectionMap.put(SelectedColumns.DESCRIPTION, SelectedColumns.DESCRIPTION);
        mSelectedProjectionMap.put(SelectedColumns.APP_CATEGORY, SelectedColumns.APP_CATEGORY);
        mSelectedProjectionMap.put(SelectedColumns.ICON, SelectedColumns.ICON);
        mSelectedProjectionMap.put(SelectedColumns.PHOTO, SelectedColumns.PHOTO);
        
        mCloudProjectionMap = new HashMap<String, String>();
        mCloudProjectionMap.put(CloudBoxColumns._ID, CloudBoxColumns._ID);
        mCloudProjectionMap.put(CloudBoxColumns.DATE, CloudBoxColumns.DATE);
        mCloudProjectionMap.put(CloudBoxColumns.TYPE, CloudBoxColumns.TYPE);
        mCloudProjectionMap.put(CloudBoxColumns.SIZE, CloudBoxColumns.SIZE);
        mCloudProjectionMap.put(CloudBoxColumns.NAME, CloudBoxColumns.NAME);
        mCloudProjectionMap.put(CloudBoxColumns.PATH, CloudBoxColumns.PATH);
        mCloudProjectionMap.put(CloudBoxColumns.EXTENSION, CloudBoxColumns.EXTENSION);
        mCloudProjectionMap.put(CloudBoxColumns.STORAGE, CloudBoxColumns.STORAGE);
        mCloudProjectionMap.put(CloudBoxColumns.SYNC, CloudBoxColumns.SYNC);
        mCloudProjectionMap.put(CloudBoxColumns.PARENT_FOLDER, CloudBoxColumns.PARENT_FOLDER);
        mCloudProjectionMap.put(CloudBoxColumns.IS_FOLDER, CloudBoxColumns.IS_FOLDER);
        mCloudProjectionMap.put(CloudBoxColumns.HASH, CloudBoxColumns.HASH);
        mCloudProjectionMap.put(CloudBoxColumns.LOCAL_FILE, CloudBoxColumns.LOCAL_FILE);
        mCloudProjectionMap.put(CloudBoxColumns.ICON_DATA, CloudBoxColumns.ICON_DATA);
        
        mPreferenceProjectionMap = new HashMap<String,String>();
        mPreferenceProjectionMap.put(PreferenceColumns.NAME, PreferenceColumns.NAME);
        mPreferenceProjectionMap.put(PreferenceColumns.VALUE, PreferenceColumns.VALUE);
        
        mMatchProjectionMap = new HashMap<String, String>();
        mMatchProjectionMap.put(MatchColumns.EXTENSION, MatchColumns.EXTENSION);
        mMatchProjectionMap.put(MatchColumns.CATEGORY, MatchColumns.CATEGORY);
        mMatchProjectionMap.put(MatchColumns.APP, MatchColumns.APP);
        mMatchProjectionMap.put(MatchColumns.DATE, MatchColumns.DATE);
        
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
        int category = -1;
        switch (URI_MATCHER.match(uri)) {
//        	case FILES:
//        		tablename = FileColumns.TABLE;
//        		type = FileCategoryHelper.TYPE_FILE;
//        		count = db.delete(FileColumns.TABLE, where, whereArgs);
//        		break;
//        	case ID:
//        		tablename = FileColumns.TABLE;
//        		type = FileCategoryHelper.TYPE_FILE;
//        		where = "_id=" + uri.getPathSegments().get(1)
//                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
//        		count = db.delete(FileColumns.TABLE, where, whereArgs);
//        		break;
        	case IMAGES:
        		tablename = ImageColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_IMAGE;
        		count = db.delete(ImageColumns.TABLE, where, whereArgs);
        		break;
        	case IMAGE_ID:
        		tablename = ImageColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_IMAGE;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(ImageColumns.TABLE, where, whereArgs);
        		break;
        	case AUDIOS:
        		tablename = AudioColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_AUDIO;
        		count = db.delete(AudioColumns.TABLE, where, whereArgs);
        		break;
        	case AUDIO_ID:
        		tablename = AudioColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_AUDIO;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(AudioColumns.TABLE, where, whereArgs);
        		break;
        	case VIDEOS:
        		tablename = VideoColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_VIDEO;
        		count = db.delete(VideoColumns.TABLE, where, whereArgs);
        		break;
        	case VIDEO_ID:
        		tablename = VideoColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_VIDEO;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(VideoColumns.TABLE, where, whereArgs);
        		break;
        	case APKS:
        		tablename = ApkColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_APK;
        		count = db.delete(ApkColumns.TABLE, where, whereArgs);
        		break;
        	case APK_ID:
        		tablename = ApkColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_APK;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(ApkColumns.TABLE, where, whereArgs);
        		break;
        	case DOCUMENTS:
        		tablename = DocumentColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_DOCUMENT;
        		count = db.delete(DocumentColumns.TABLE, where, whereArgs);
        		break;
        	case DOCUMENT_ID:
        		tablename = DocumentColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_DOCUMENT;
        		where = "_id=" + uri.getPathSegments().get(1)
                + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : "");
        		count = db.delete(DocumentColumns.TABLE, where, whereArgs);
        		break;
        	case ZIPS:
        		tablename = ZipColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_ZIP;
        		count = db.delete(ZipColumns.TABLE, where, whereArgs);
        		break;
        	case ZIP_ID:
        		tablename = ZipColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_ZIP;
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
        	case MATCHES:
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
        if(count>0 && category!=-1){
        	updateCategoryData(db,tablename, category);
        }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return count;
	}

	@Override
	public String getType(Uri uri) {
        return null;
//		// TODO Auto-generated method stub
//		switch(URI_MATCHER.match(uri))
//		{
//		case FILES:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.file";
//		case ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.file";
//		case IMAGES:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.image";
//		case IMAGE_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.image";
//		case AUDIOS:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.audio";
//		case AUDIO_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.audio";
//		case VIDEOS:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.video";
//		case VIDEO_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.video";
//		case APKS:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.apk";
//		case APK_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.apk";
//		case DOCUMENTS:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.document";
//		case DOCUMENT_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.document";
//		case ZIPS:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.zip";
//		case ZIP_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.zip";
//		case CATEGORIES:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.category";
//		case CATEGORY_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.category";
//		case FAVORITES:
//			return "vnd.android.cursor.dir/vnd.hufeng.filemanager.favorite";
//		case FAVORITE_ID:
//			return "vnd.android.cursor.item/vnd.hufeng.filemanager.favorite";
//        case SELECTEDS:
//            return "vnd.android.cursor.dir/vnd.hufeng.filemanager.selected";
//        case SELECTED_ID:
//            return "vnd.android.cursor.item/vnd.hufeng.filemanager.selected";
//        case CLOUDS:
//            return "vnd.android.cursor.dir/vnd.hufeng.filemanager.cloud";
//        case CLOUD_ID:
//            return "vnd.android.cursor.item/vnd.hufeng.filemanager.cloud";
////		case PREFERENCE_ID:
////			return "vnd.android.cursor.item/vnd.hufeng.filemanager.preference";
//		default:
//            throw new IllegalArgumentException("Unknown uri " + uri);
//		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
        LogUtil.i(LOG_TAG, "insert " + uri + " " + values);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = 0;
        String tablename = null;
        int category = -1;
        switch(URI_MATCHER.match(uri)){
//	        case FILES:
//                if (!values.containsKey(FileColumns.SYNC)) {
//                    values.put(FileColumns.SYNC , 0);
//                }
//	        	tablename = FileColumns.TABLE;
//	        	type = FileCategoryHelper.TYPE_FILE;
//	        	rowId = db.insert(FileColumns.TABLE, null, values);
//	        	break;
	        case IMAGES:
                if (!values.containsKey(FileColumns.SYNC)) {
                    values.put(FileColumns.SYNC , 0);
                }
                tablename = ImageColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_IMAGE;
	        	rowId = db.insert(ImageColumns.TABLE, null, values);
	        	break;
	        case AUDIOS:
                if (!values.containsKey(FileColumns.SYNC)) {
                    values.put(FileColumns.SYNC , 0);
                }
                tablename = AudioColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_AUDIO;
	        	rowId = db.insert(AudioColumns.TABLE, null, values);
	        	break;
	        case VIDEOS:
                if (!values.containsKey(FileColumns.SYNC)) {
                    values.put(FileColumns.SYNC , 0);
                }
                tablename = VideoColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_VIDEO;
	        	rowId = db.insert(VideoColumns.TABLE, null, values);
	        	break;
	        case APKS:
                if (!values.containsKey(FileColumns.SYNC)) {
                    values.put(FileColumns.SYNC , 0);
                }
                tablename = ApkColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_APK;
	        	rowId = db.insert(ApkColumns.TABLE, null, values);
	        	break;
	        case CATEGORIES:
	        	rowId = db.insert(CategoryColumns.TABLE, null, values);
	        	break;
	        case DOCUMENTS:
                if (!values.containsKey(FileColumns.SYNC)) {
                    values.put(FileColumns.SYNC , 0);
                }
                tablename = DocumentColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_DOCUMENT;
	        	rowId = db.insert(DocumentColumns.TABLE, null, values);
	        	break;
	        case ZIPS:
                if (!values.containsKey(FileColumns.SYNC)) {
                    values.put(FileColumns.SYNC , 0);
                }
                tablename = ZipColumns.TABLE;
                category = FileCategoryHelper.CATEGORY_TYPE_ZIP;
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
	        case MATCHES:
	        	db.insert(MatchColumns.TABLE, null, values);
	        	return null;
	        default:
	        	throw new SQLException("Failed to insert row into " + uri);	
        }
        if (rowId > 0) {
        	if(category!=-1){
                updateCategoryData(db,tablename,category);
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

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String orderBy;
        switch (URI_MATCHER.match(uri)) {
//            case FILES:
//            	qb.setTables(FileColumns.TABLE);
//            	qb.setProjectionMap(mFileProjectionMap);
//                if (TextUtils.isEmpty(sortOrder)) {
//                    orderBy = FileColumns.DEFAULT_SORT_ORDER;
//                } else {
//                    orderBy = sortOrder;
//                }
//            	break;
//            case ID:
//            	qb.setTables(FileColumns.TABLE);
//            	qb.setProjectionMap(mFileProjectionMap);
//            	qb.appendWhere(FileColumns._ID + "=" + uri.getPathSegments().get(1));
//                if (TextUtils.isEmpty(sortOrder)) {
//                    orderBy = FileColumns.DEFAULT_SORT_ORDER;
//                } else {
//                    orderBy = sortOrder;
//                }
//            	break;
            case IMAGES:
            	qb.setTables(ImageColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + ImageColumns.TABLE + "." + ImageColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mImageProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ImageColumns.TABLE + "." + ImageColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = ImageColumns.TABLE + "." + sortOrder;
                }
            	break;
            case IMAGE_ID:
            	qb.setTables(ImageColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + ImageColumns.TABLE + "." + ImageColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mImageProjectionMap);
            	qb.appendWhere(ImageColumns.TABLE + "." + ImageColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ImageColumns.TABLE + "." + ImageColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = ImageColumns.TABLE + "." + sortOrder;
                }
            	break;
            case AUDIOS:
            	qb.setTables(AudioColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + AudioColumns.TABLE + "." + AudioColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mAudioProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = AudioColumns.TABLE + "." + AudioColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = AudioColumns.TABLE + "." + sortOrder;
                }
            	break;
            case AUDIO_ID:
            	qb.setTables(AudioColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + AudioColumns.TABLE + "." + AudioColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mAudioProjectionMap);
            	qb.appendWhere(AudioColumns.TABLE + "." + AudioColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = AudioColumns.TABLE + "." + AudioColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = AudioColumns.TABLE + "." + sortOrder;
                }
            	break;
            case VIDEOS:
            	qb.setTables(VideoColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + VideoColumns.TABLE + "." + VideoColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mVideoProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = VideoColumns.TABLE + "." + VideoColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = VideoColumns.TABLE + "." + sortOrder;
                }
            	break;
            case VIDEO_ID:
            	qb.setTables(VideoColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + VideoColumns.TABLE + "." + VideoColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mVideoProjectionMap);
            	qb.appendWhere(VideoColumns.TABLE + "." + VideoColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = VideoColumns.TABLE + "." + VideoColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = VideoColumns.TABLE + "." + sortOrder;
                }
            	break;
            case APKS:
            	qb.setTables(ApkColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + ApkColumns.TABLE + "." + ApkColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mApkProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ApkColumns.TABLE + "." + ApkColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = ApkColumns.TABLE + "." + sortOrder;
                }
            	break;
            case APK_ID:
            	qb.setTables(ApkColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + ApkColumns.TABLE + "." + ApkColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
            	qb.setProjectionMap(mApkProjectionMap);
            	qb.appendWhere(ApkColumns.TABLE + "." + ApkColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ApkColumns.TABLE + "." + ApkColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = ApkColumns.TABLE + "." + sortOrder;
                }
            	break;
            case DOCUMENTS:
                qb.setTables(DocumentColumns.TABLE+ " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + DocumentColumns.TABLE + "." + DocumentColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
                qb.setProjectionMap(mDocumentProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = DocumentColumns.TABLE + "." + DocumentColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = DocumentColumns.TABLE + "." + sortOrder;
                }
                break;
            case DOCUMENT_ID:
                qb.setTables(DocumentColumns.TABLE+ " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + DocumentColumns.TABLE + "." + DocumentColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
                qb.setProjectionMap(mDocumentProjectionMap);
                qb.appendWhere(DocumentColumns.TABLE + "." + DocumentColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = DocumentColumns.TABLE + "." + DocumentColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = DocumentColumns.TABLE + "." + sortOrder;
                }
                break;
            case ZIPS:
                qb.setTables(ZipColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + ZipColumns.TABLE + "." + ZipColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
                qb.setProjectionMap(mZipProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ZipColumns.TABLE + "." + ZipColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = ZipColumns.TABLE + "." + sortOrder;
                }
                break;
            case ZIP_ID:
                qb.setTables(ZipColumns.TABLE + " LEFT OUTER JOIN "+FavoriteColumns.TABLE
                        + " ON " + ZipColumns.TABLE + "." + ZipColumns.PATH
                        + " = " + FavoriteColumns.TABLE + "." + FavoriteColumns.PATH);
                qb.setProjectionMap(mZipProjectionMap);
                qb.appendWhere(ZipColumns.TABLE + "." + ZipColumns._ID + "=" + uri.getPathSegments().get(1));
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = ZipColumns.TABLE + "." + ZipColumns.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = ZipColumns.TABLE + "." + sortOrder;
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
            case CATEGORIES:
            	qb.setTables(CategoryColumns.TABLE);
            	qb.setProjectionMap(mCategoryProjectionMap);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = CategoryColumns.DEFAULT_SORT_ORDER;
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
            case MATCHES:
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
        int category = -1;
        switch (URI_MATCHER.match(uri)) {
//	        case FILES:
//                values.put(FileColumns.SYNC, 0);
//	            count = db.update(FileColumns.TABLE, values, selection,
//	                    selectionArgs);
//	            break;
//	        case ID:
//                values.put(FileColumns.SYNC, 0);
//	            selection = "_id=" + uri.getPathSegments().get(1)
//	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
//	            count = db.update(FileColumns.TABLE, values, selection,
//	                    selectionArgs);
//	            break;
	        case IMAGES:
                category = FileCategoryHelper.CATEGORY_TYPE_IMAGE;
                values.put(FileColumns.SYNC, 0);
	            count = db.update(ImageColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case IMAGE_ID:
                category = FileCategoryHelper.CATEGORY_TYPE_IMAGE;
                values.put(FileColumns.SYNC, 0);
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(ImageColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case AUDIOS:
                category = FileCategoryHelper.CATEGORY_TYPE_AUDIO;
                values.put(FileColumns.SYNC, 0);
	            count = db.update(AudioColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case AUDIO_ID:
                category = FileCategoryHelper.CATEGORY_TYPE_AUDIO;
                values.put(FileColumns.SYNC, 0);
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(AudioColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case VIDEOS:
                category = FileCategoryHelper.CATEGORY_TYPE_VIDEO;
                values.put(FileColumns.SYNC, 0);
	            count = db.update(VideoColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case VIDEO_ID:
                category = FileCategoryHelper.CATEGORY_TYPE_VIDEO;
                values.put(FileColumns.SYNC, 0);
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(VideoColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case APKS:
                category = FileCategoryHelper.CATEGORY_TYPE_APK;
                values.put(FileColumns.SYNC, 0);
	            count = db.update(ApkColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case APK_ID:
                category = FileCategoryHelper.CATEGORY_TYPE_APK;
                values.put(FileColumns.SYNC, 0);
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(ApkColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case DOCUMENTS:
                category = FileCategoryHelper.CATEGORY_TYPE_DOCUMENT;
                values.put(FileColumns.SYNC, 0);
	            count = db.update(DocumentColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case DOCUMENT_ID:
                category = FileCategoryHelper.CATEGORY_TYPE_DOCUMENT;
                values.put(FileColumns.SYNC, 0);
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(DocumentColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case ZIPS:
                category = FileCategoryHelper.CATEGORY_TYPE_ZIP;
                values.put(FileColumns.SYNC, 0);
	            count = db.update(ZipColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case ZIP_ID:
                category = FileCategoryHelper.CATEGORY_TYPE_ZIP;
                values.put(FileColumns.SYNC, 0);
	            selection = "_id=" + uri.getPathSegments().get(1)
	                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
	            count = db.update(ZipColumns.TABLE, values, selection,
	                    selectionArgs);
	            break;
	        case CATEGORIES:
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
	        case MATCHES:
	        	if (selectionArgs == null) {
                    LogUtil.e(LOG_TAG, "selectinArgs is null");
                    throw new NullPointerException("selectionArgs could not be null ");
                }

                count = db.update(MatchColumns.TABLE, values, selection, selectionArgs);
                if (count <=0) {
                    values.put(MatchColumns.EXTENSION, selectionArgs[0]);
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
//        case FILES:
//        	tablename = FileColumns.TABLE;
//        	type = FileCategoryHelper.TYPE_FILE;
//        	break;
        case IMAGES:
        	tablename = ImageColumns.TABLE;
        	type = FileCategoryHelper.CATEGORY_TYPE_IMAGE;
        	break;
        case AUDIOS:
        	tablename = AudioColumns.TABLE;
        	type = FileCategoryHelper.CATEGORY_TYPE_AUDIO;
        	break;
        case VIDEOS:
        	tablename = VideoColumns.TABLE;
        	type = FileCategoryHelper.CATEGORY_TYPE_VIDEO;
        	break;
        case APKS:
        	tablename = ApkColumns.TABLE;
        	type = FileCategoryHelper.CATEGORY_TYPE_APK;
        	break;
        case CATEGORIES:
        	tablename = CategoryColumns.TABLE;
        	break;
        case DOCUMENTS:
        	tablename = DocumentColumns.TABLE;
        	type = FileCategoryHelper.CATEGORY_TYPE_DOCUMENT;
        	break;
        case ZIPS:
        	tablename = ZipColumns.TABLE;
        	type = FileCategoryHelper.CATEGORY_TYPE_ZIP;
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
		
        String[] projection = new String[] { "count(*) as count", "sum("+FileColumns.SIZE+") as size", FileColumns.STORAGE + " as storage"  };
        Cursor cursor = db.query(tablename, projection, 
        		/*FileColumns.TYPE+"=?", 
        		new String[]{""+type},*/
        		null,
        		null,
        		FileColumns.STORAGE, 
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
		        cvs.put(CategoryColumns.NUMBER, count);
		        cvs.put(CategoryColumns.SIZE, size);
		        int row = db.update(CategoryColumns.TABLE, cvs, 
		        		CategoryColumns.CATEGORY+"=? AND "+CategoryColumns.STORAGE+"=?",new String[]{type+"", storage});
		        long id = 0;
		        if(row<1){
		        	cvs.put(CategoryColumns.CATEGORY, type);
		        	cvs.put(CategoryColumns.STORAGE, storage);
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
            buffer.append(CategoryColumns.CATEGORY).append("=").append(type);
            if (stors.size() > 0) {
                buffer.append(" AND ").append(CategoryColumns.STORAGE).append(" NOT IN (");
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
            cvs.put(CategoryColumns.NUMBER, 0);
            cvs.put(CategoryColumns.SIZE, 0);
            int row = db.update(CategoryColumns.TABLE, cvs, buffer.toString(),null);
            if (row > 0) {
                flag = true;
            }
        }
        if(flag)
            getContext().getContentResolver().notifyChange(CategoryColumns.CONTENT_URI, null);
	}

}
