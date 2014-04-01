package com.hufeng.filemanager.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.SettingsScanActivity;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.MediaStoreUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class IFileSyncServiceImpl extends IFileSyncService.Stub{
	
	private static final String LOG_TAG = IFileSyncServiceImpl.class.getSimpleName();
	
	private Context mContext;

    private boolean mFilterSmallIcon = false;
    private boolean mFilterSmallAudio = false;

    public AtomicBoolean mIsScanning = new AtomicBoolean(false);
	
	public IFileSyncServiceImpl(Context context)
	{
		mContext = context;
	}

//    FileObserver[] mObserver = null;

    public void onCreate() {

    }

    public void onDestroy() {
//        stopWatching();
//        mObserver = null;
    }

    @Override
    public void startScan() throws RemoteException
	{
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "startScanFile");

        if (!mIsScanning.get()) {
            mIsScanning.set(true);
            ServiceUiHelper.getInstance().scanStarted();
            FileManager.setPreference(FileManager.FILEMANAGER_LAST_SCAN, System.currentTimeMillis()+"");

            new ScanTask().execute();
        } else {

        }
        return;
	}

    private class ScanTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            long start_time = System.currentTimeMillis();
            clearDatabase();
            long duration = System.currentTimeMillis() - start_time;
            if (duration < 3000) {
                try {
                    Thread.sleep(5000-duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            stopWatching();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsScanning.set(false);
            ServiceUiHelper.getInstance().scanCompleted();
//            startWatching();
        }


    }

//    private void startWatching() {
//        String[] files = StorageManager.getInstance(FileManager.getAppContext()).getMountedStorages();
//        if (files != null && files.length > 0) {
//            mObserver = new FileObserver[files.length];
//            for (int i=0;i<files.length;i++) {
//                if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "start watching file: "+files[i]);
//                mObserver[i] = new FileObserver(files[i], FileObserver.CREATE | FileObserver.DELETE | FileObserver.MOVED_FROM | FileObserver.MOVED_TO) {
//                    @Override
//                    public void onEvent(int event, String path) {
//                        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "onEvent "+event+", "+path);
//                        scanSinglePath(path);
//                    }
//                };
//            }
//        }
//    }

//    private void stopWatching() {
//        if (mObserver != null && mObserver.length > 0) {
//            for (FileObserver observer : mObserver) {
//                if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "stop watching file: "+observer.toString());
//                observer.stopWatching();
//            }
//        }
//        mObserver = null;
//    }
	
	private void clearDatabase()
	{
		ContentValues category_values = new ContentValues();
		category_values.put(DataStructures.CategoryColumns.SIZE_FIELD, 0);
		category_values.put(DataStructures.CategoryColumns.NUMBER_FIELD, 0);
		int count = FileManager.getAppContext().getContentResolver().update(DataStructures.CategoryColumns.CONTENT_URI, category_values, null, null);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "update category size and number to 0, return "+count);
		if(count==0)
		{
			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "we will do insert");
			String[] paths = StorageManager.getInstance(FileManager.getAppContext()).getAllStorages();
			for(String path:paths){
				category_values.put(DataStructures.CategoryColumns.STORAGE_FIELD, path);
				category_values.put(DataStructures.CategoryColumns.CATEGORY_FIELD, FileUtils.FILE_TYPE_ZIP);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY_FIELD, FileUtils.FILE_TYPE_APK);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY_FIELD, FileUtils.FILE_TYPE_DOCUMENT);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY_FIELD, FileUtils.FILE_TYPE_IMAGE);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY_FIELD, FileUtils.FILE_TYPE_AUDIO);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY_FIELD, FileUtils.FILE_TYPE_VIDEO);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY_FIELD, FileUtils.FILE_TYPE_FILE);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
			}
		}
        String selection_where = DataStructures.FileColumns.FILE_SYNC_FIELD + "==?";
        String[] selection_arg = new String[]{String.valueOf(1)};
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.FileColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete synced file file, return "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.ImageColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete synced image file, return "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.AudioColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete synced audio file, return "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.VideoColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete synced video file, return "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.DocumentColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete synced document file, return "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.ApkColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete synced apk file, return "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.ZipColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete synced zip file, return "+count);

        count = clearUnsyncedFileInDatabase(DataStructures.FileColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unsynced file file, return "+count);
        count = clearUnsyncedFileInDatabase(DataStructures.ImageColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unsynced image file, return "+count);
        count = clearUnsyncedFileInDatabase(DataStructures.AudioColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unsynced audio file, return "+count);
        count = clearUnsyncedFileInDatabase(DataStructures.VideoColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unsynced video file, return "+count);
        count = clearUnsyncedFileInDatabase(DataStructures.ApkColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unsynced apk file, return "+count);
        count = clearUnsyncedFileInDatabase(DataStructures.DocumentColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unsynced document file, return "+count);
        count = clearUnsyncedFileInDatabase(DataStructures.ZipColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unsynced zip file, return "+count);

        initDatabaseFromMediaStore();

        count = clearFileInDatabase(DataStructures.FavoriteColumns.CONTENT_URI, false);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete favorite file, return "+count);
	}

    private int clearUnsyncedFileInDatabase(Uri uri) {
        return clearFileInDatabase(uri, true);
    }

    private int clearFileInDatabase(Uri uri, boolean unsynced_only) {
        int count = 0;
        String[] projection = new String[]{DataStructures.FileColumns.FILE_PATH_FIELD};
        String selection_where = DataStructures.FileColumns.FILE_SYNC_FIELD + "!=?";
        String[] selection_arg = new String[]{String.valueOf(1)};

        StringBuilder delete_selection = new StringBuilder();
        delete_selection.append(DataStructures.FileColumns.FILE_PATH_FIELD + " IN ");
        delete_selection.append('(');
        boolean isFirst = true;
        Cursor cursor = null;
        try {
            if (unsynced_only) {
                cursor = FileManager.getAppContext().getContentResolver().query(uri, projection, selection_where, selection_arg, null );
            } else {
                cursor = FileManager.getAppContext().getContentResolver().query(uri, projection, null, null, null );
            }
            if (cursor != null) {
                while(cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    if (!new File(path).exists()) {
                        if (isFirst) {
                            delete_selection.append('\'');
                            delete_selection.append(path.replace("'", "\""));
                            delete_selection.append('\'');
                            isFirst = false;
                        } else {
                            delete_selection.append(',');
                            delete_selection.append('\'');
                            delete_selection.append(path.replace("'", "\""));
                            delete_selection.append('\'');
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!isFirst) {
            try {
                count = FileManager.getAppContext().getContentResolver().delete(uri, delete_selection.toString(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    private void copyMediastoreToDatabase(Uri uri_mediastore, String[] projection_mediastore, String selection_mediastore,
                                         Uri uri_database, int category_type) {
        Cursor cursor = null;
        ArrayList<ContentValues> cva = new ArrayList<ContentValues>();
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(
                    uri_mediastore,
                    projection_mediastore,
                    selection_mediastore,
                    null,
                    null
            );
            if (cursor!=null) {
                StorageManager stor = StorageManager.getInstance(mContext);
                while(cursor.moveToNext()) {
                    ContentValues cv = new ContentValues();
                    String path = cursor.getString(0);
                    if (!new File(path).exists() || new File(path).isDirectory()) {
                        continue;
                    }
                    cv.put(DataStructures.FileColumns.FILE_PATH_FIELD, path);
                    cv.put(DataStructures.FileColumns.FILE_SIZE_FIELD, cursor.getLong(1));
                    cv.put(DataStructures.FileColumns.FILE_DATE_FIELD, cursor.getLong(2));
                    cv.put(DataStructures.FileColumns.FILE_SYNC_FIELD, 1);
                    String name = cursor.getString(3);
                    if (TextUtils.isEmpty(name)) {
                        int i = path.lastIndexOf("/");
                        if (i>0) {
                            name = path.substring(i+1);
                        }
                    }
                    if(!TextUtils.isEmpty(name)) {
                        cv.put(DataStructures.FileColumns.FILE_NAME_FIELD, name);
                        int i = name.lastIndexOf(".");
                        if(i>0) {
                            cv.put(DataStructures.FileColumns.FILE_EXTENSION_FIELD, name.substring(i+1));
                        }
                    }
                    String storage_path = stor.getStorageForPath(path);
                    cv.put(DataStructures.FileColumns.FILE_STORAGE_FIELD, storage_path);
                    switch (category_type) {
                        case FileUtils.FILE_TYPE_IMAGE:
                            cv.put(DataStructures.ImageColumns.IMAGE_WIDTH_FIELD, cursor.getInt(4));
                            cv.put(DataStructures.ImageColumns.IMAGE_HEIGHT_FIELD, cursor.getInt(5));
                            break;
                        case FileUtils.FILE_TYPE_VIDEO:
                            cv.put(DataStructures.VideoColumns.PLAY_DURATION_FIELD, cursor.getLong(4));
                            break;
                        case FileUtils.FILE_TYPE_AUDIO:
                            cv.put(DataStructures.AudioColumns.PLAY_DURATION_FIELD, cursor.getLong(4));
                            cv.put(DataStructures.AudioColumns.ALBUM_FIELD, cursor.getString(5));
                            cv.put(DataStructures.AudioColumns.SINGER_FIELD, cursor.getString(6));
                            break;
                        default:
                            break;
                    }
                    cva.add(cv);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
        int size = cva.size();
        if ( size>0 ) {
            FileManager.getAppContext().getContentResolver().bulkInsert(uri_database, cva.toArray(new ContentValues[size]));
        }
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "initDataFromMediaStore category="+category_type+", count = "+size);

    }

    public void initDatabaseFromMediaStore(){
        String volumeName = "external";

        //image
        Uri uri = MediaStore.Images.Media.getContentUri(volumeName);
        String[] projection = new String[]{
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
        };
        String filter = FileManager.getPreference(SettingsScanActivity.IMAGE_FILTER_SMALL, "1");
        if(filter.equals("0"))
        {
            mFilterSmallIcon = false;
        }
        else
        {
            mFilterSmallIcon = true;
        }
        String selection = null;
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "filter small icon "+mFilterSmallIcon);
        if (mFilterSmallIcon) {
                    selection = MediaStore.MediaColumns.SIZE+" > 30720";
        }
        Uri uri_fm = DataStructures.ImageColumns.CONTENT_URI;
        copyMediastoreToDatabase(uri, projection, selection, uri_fm, FileUtils.FILE_TYPE_IMAGE);


        //video
        uri = MediaStore.Video.Media.getContentUri(volumeName);
        selection = null;
        projection = new String[]{
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DURATION,
        };
        uri_fm = DataStructures.VideoColumns.CONTENT_URI;
        copyMediastoreToDatabase(uri, projection, selection, uri_fm, FileUtils.FILE_TYPE_VIDEO);


        //audio
        uri = MediaStore.Audio.Media.getContentUri(volumeName);
        projection = new String[]{
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST
        };
        uri_fm = DataStructures.AudioColumns.CONTENT_URI;
        filter = FileManager.getPreference(SettingsScanActivity.AUDIO_FILTER_SMALL, "1");
        if(filter.equals("0"))
        {
            mFilterSmallAudio = false;
        }
        else
        {
            mFilterSmallAudio = true;
        }
        selection = null;
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "filter small audio "+mFilterSmallAudio);
        if (mFilterSmallAudio) {
            selection = MediaStore.MediaColumns.SIZE+" > 102400";
        }
        copyMediastoreToDatabase(uri, projection, selection, uri_fm, FileUtils.FILE_TYPE_AUDIO);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            projection = new String[]{
                    MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.DATE_MODIFIED,
                    MediaStore.MediaColumns.DISPLAY_NAME
            };
            //apk
            uri = MediaStore.Files.getContentUri(volumeName);
            selection = MediaStoreUtil.buildSelectionByCategory(FileUtils.FILE_TYPE_APK);
            uri_fm = DataStructures.ApkColumns.CONTENT_URI;
            copyMediastoreToDatabase(uri, projection, selection, uri_fm, FileUtils.FILE_TYPE_APK);

            //zip
            selection = MediaStoreUtil.buildSelectionByCategory(FileUtils.FILE_TYPE_ZIP);
            uri_fm = DataStructures.ZipColumns.CONTENT_URI;
            copyMediastoreToDatabase(uri, projection, selection, uri_fm, FileUtils.FILE_TYPE_ZIP);


            //document
            selection = MediaStoreUtil.buildSelectionByCategory(FileUtils.FILE_TYPE_DOCUMENT);
            uri_fm = DataStructures.DocumentColumns.CONTENT_URI;
            copyMediastoreToDatabase(uri, projection, selection, uri_fm, FileUtils.FILE_TYPE_DOCUMENT);
        }

    }

	@Override
	public boolean isScanning() throws RemoteException {
		return mIsScanning.get();
	}


}
