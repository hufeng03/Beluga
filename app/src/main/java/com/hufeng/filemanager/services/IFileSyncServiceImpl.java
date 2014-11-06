package com.hufeng.filemanager.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.SettingsScanActivity;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.scan.ApkObject;
import com.hufeng.filemanager.scan.AudioObject;
import com.hufeng.filemanager.scan.DocumentObject;
import com.hufeng.filemanager.scan.ImageObject;
import com.hufeng.filemanager.scan.VideoObject;
import com.hufeng.filemanager.scan.ZipObject;
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

    private MyHandler mHandler;

    public class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            performScan();
        }
    }
	
	public IFileSyncServiceImpl(Context context) {
		mContext = context;
        HandlerThread thread = new HandlerThread("FileSynServiceImpl");
        thread.start();
        mHandler = new MyHandler(thread.getLooper());
	}


    public void onCreate() {
        mHandler.sendEmptyMessageDelayed(0,1000);
    }

    public void refresh() {
        if(mHandler.hasMessages(0))
            mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0,3000);
    }

    public void onDestroy() {
    }

    @Override
    public void startScan() throws RemoteException
	{
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "startScanFile");

        performScan();

        return;
	}

    @Override
    public void deleteUnexist(int type) throws RemoteException {
        if (!mIsScanning.get()) {
            mIsScanning.set(true);
            new DeleteUnexistTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, type);
        }
        return;
    }


    private void performScan() {
        if (!mIsScanning.get()) {
            mIsScanning.set(true);
            ServiceCallUiHelper.getInstance().scanStarted();
            FileManager.setPreference(FileManager.FILEMANAGER_LAST_SCAN, System.currentTimeMillis()+"");

            new ScanTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {

        }
    }

    private class DeleteUnexistTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            deleteUnexistFiles(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsScanning.set(false);
        }
    }

    private void deleteUnexistFiles(int type) {
        switch (type) {
            case FileUtils.FILE_TYPE_IMAGE:
                clearUnexistFileInDatabase(DataStructures.ImageColumns.CONTENT_URI);
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                clearUnexistFileInDatabase(DataStructures.AudioColumns.CONTENT_URI);
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                clearUnexistFileInDatabase(DataStructures.VideoColumns.CONTENT_URI);
                break;
            case FileUtils.FILE_TYPE_APK:
                clearUnexistFileInDatabase(DataStructures.ApkColumns.CONTENT_URI);
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                clearUnexistFileInDatabase(DataStructures.DocumentColumns.CONTENT_URI);
                break;
            case FileUtils.FILE_TYPE_ZIP:
                clearUnexistFileInDatabase(DataStructures.ZipColumns.CONTENT_URI);
                break;

        }
    }

    private class ScanTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
//            long start_time = System.currentTimeMillis();
            refreshDatabase();
//            long duration = System.currentTimeMillis() - start_time;
//            if (duration < 3000) {
//                try {
//                    Thread.sleep(5000-duration);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsScanning.set(false);
            ServiceCallUiHelper.getInstance().scanCompleted();
        }


    }
	
	private void refreshDatabase()
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

        ArrayList<String> save_unsyned_images = saveUnsyncedFileInDatabase(DataStructures.ImageColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "save unsynced image: "+(save_unsyned_images==null?"0":save_unsyned_images.size()));
        ArrayList<String> save_unsyned_audios = saveUnsyncedFileInDatabase(DataStructures.AudioColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "save unsynced audio: "+(save_unsyned_audios==null?"0":save_unsyned_audios.size()));
        ArrayList<String> save_unsyned_videos = saveUnsyncedFileInDatabase(DataStructures.VideoColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "save unsynced video: "+(save_unsyned_videos==null?"0":save_unsyned_videos.size()));
        ArrayList<String> save_unsyned_apks = saveUnsyncedFileInDatabase(DataStructures.ApkColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "save unsynced apk: "+(save_unsyned_apks==null?"0":save_unsyned_apks.size()));
        ArrayList<String> save_unsyned_documents = saveUnsyncedFileInDatabase(DataStructures.DocumentColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "save unsynced document: "+(save_unsyned_documents==null?"0":save_unsyned_documents.size()));
        ArrayList<String> save_unsyned_zips = saveUnsyncedFileInDatabase(DataStructures.ZipColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "save unsynced zip: "+(save_unsyned_zips==null?"0":save_unsyned_zips.size()));

        String selection_where = null;
        String[] selection_arg = null;

		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.FileColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all file: "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.ImageColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all image: "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.AudioColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all audio: "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.VideoColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all video: "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.DocumentColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all document: "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.ApkColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all apk: "+count);
		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.ZipColumns.CONTENT_URI, selection_where, selection_arg);
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all zip: "+count);

        int[] counts = clearUnexistFileInDatabase(DataStructures.FavoriteColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete unexist favorite: "+counts[0]+","+counts[1]);


        initDatabaseFromMediaStore();

        String[] important_dirs = ServiceUtil.getAllImportantDirectory();

        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "get import dirs: "+ (important_dirs == null ? 0 :important_dirs.length));

        scanImportantDirectory(important_dirs,
                save_unsyned_images,
                save_unsyned_audios,
                save_unsyned_videos,
                save_unsyned_apks,
                save_unsyned_documents,
                save_unsyned_zips);

        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "scan import files: "
                        + save_unsyned_images.size()+","+save_unsyned_audios.size()+","
                        + save_unsyned_videos.size()+","+save_unsyned_apks.size()+","
                        + save_unsyned_documents.size()+","+save_unsyned_zips.size()+",");

        filterAgainstDatabase(DataStructures.ImageColumns.CONTENT_URI, save_unsyned_images);
        filterAgainstDatabase(DataStructures.AudioColumns.CONTENT_URI, save_unsyned_audios);
        filterAgainstDatabase(DataStructures.VideoColumns.CONTENT_URI, save_unsyned_videos);
        filterAgainstDatabase(DataStructures.ApkColumns.CONTENT_URI, save_unsyned_apks);
        filterAgainstDatabase(DataStructures.DocumentColumns.CONTENT_URI, save_unsyned_documents);
        filterAgainstDatabase(DataStructures.ZipColumns.CONTENT_URI, save_unsyned_zips);

        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "filter import files against database: "
                + save_unsyned_images.size()+","+save_unsyned_audios.size()+","
                + save_unsyned_videos.size()+","+save_unsyned_apks.size()+","
                + save_unsyned_documents.size()+","+save_unsyned_zips.size()+",");

        addFileIntoDatabase(DataStructures.ImageColumns.CONTENT_URI, FileUtils.FILE_TYPE_IMAGE, save_unsyned_images);
        addFileIntoDatabase(DataStructures.AudioColumns.CONTENT_URI, FileUtils.FILE_TYPE_AUDIO, save_unsyned_audios);
        addFileIntoDatabase(DataStructures.VideoColumns.CONTENT_URI, FileUtils.FILE_TYPE_VIDEO, save_unsyned_videos);
        addFileIntoDatabase(DataStructures.ApkColumns.CONTENT_URI, FileUtils.FILE_TYPE_APK, save_unsyned_apks);
        addFileIntoDatabase(DataStructures.DocumentColumns.CONTENT_URI, FileUtils.FILE_TYPE_DOCUMENT, save_unsyned_documents);
        addFileIntoDatabase(DataStructures.ZipColumns.CONTENT_URI, FileUtils.FILE_TYPE_ZIP, save_unsyned_zips);


	}

    private void scanImportantDirectory(String[] dirs, ArrayList<String> images,
                                      ArrayList<String> audios, ArrayList<String> videos,
                                      ArrayList<String> apks, ArrayList<String> documents, ArrayList<String> zips) {

        boolean filter_small_image = (FileManager.getPreference(SettingsScanActivity.IMAGE_FILTER_SMALL, "1") == "1");
        boolean filter_small_audio = (FileManager.getPreference(SettingsScanActivity.AUDIO_FILTER_SMALL, "1") == "1");
        if (dirs == null) {
            return;
        }
        for (String dir : dirs) {
            if (new File(dir).exists()) {
                File[] childs = new File(dir).listFiles();
                if (childs != null && childs.length > 0) {
                    for (File child : childs) {
                        if (child.isDirectory()) {
                            continue;
                        } else if (child.isHidden()) {
                            continue;
                        } else if (child.length() == 0) {
                            continue;
                        } else {
                            int type = FileUtils.getFileType(child);
                            String name = child.getAbsolutePath();
                            switch (type) {
                                case FileUtils.FILE_TYPE_IMAGE:
                                    if (!images.contains(name)) {
                                        if (!filter_small_image || new File(name).length() > 30720) {
                                            images.add(name);
                                        }
                                    }
                                    break;
                                case FileUtils.FILE_TYPE_AUDIO:
                                    if (!audios.contains(name)) {
                                        if (!filter_small_audio || new File(name).length() > 102400) {
                                            audios.add(name);
                                        }
                                    }
                                    break;
                                case FileUtils.FILE_TYPE_VIDEO:
                                    if (!videos.contains(name)) {
                                        videos.add(name);
                                    }
                                    break;
                                case FileUtils.FILE_TYPE_APK:
                                    if (!apks.contains(name)) {
                                        apks.add(name);
                                    }
                                    break;
                                case FileUtils.FILE_TYPE_DOCUMENT:
                                    if (!documents.contains(name)) {
                                        documents.add(name);
                                    }
                                    break;
                                case FileUtils.FILE_TYPE_ZIP:
                                    if (!zips.contains(name)) {
                                        zips.add(name);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }

    }

    private void addFileIntoDatabase(Uri uri, int category, ArrayList<String> files) {
        if (files == null || files.size() == 0) return;
        ContentValues[] cvs = new ContentValues[files.size()];
        for (int i = 0; i < files.size(); i++) {
            cvs[i] = new ContentValues();
            cvs[i].put(DataStructures.FileColumns.FILE_SYNC_FIELD, 0);
            switch (category) {
                case FileUtils.FILE_TYPE_IMAGE:
                    new ImageObject(files.get(i)).toContentValues(cvs[i]);
                    break;
                case FileUtils.FILE_TYPE_AUDIO:
                    new AudioObject(files.get(i)).toContentValues(cvs[i]);
                    break;
                case FileUtils.FILE_TYPE_VIDEO:
                    new VideoObject(files.get(i)).toContentValues(cvs[i]);
                    break;
                case FileUtils.FILE_TYPE_APK:
                    new ApkObject(files.get(i)).toContentValues(cvs[i]);
                    break;
                case FileUtils.FILE_TYPE_DOCUMENT:
                    new DocumentObject(files.get(i)).toContentValues(cvs[i]);
                    break;
                case FileUtils.FILE_TYPE_ZIP:
                    new ZipObject(files.get(i)).toContentValues(cvs[i]);
                    break;
            }
        }
        FileManager.getAppContext().getContentResolver().bulkInsert(uri, cvs);
    }

    private void filterAgainstDatabase(Uri uri, ArrayList<String> files) {
        if (files == null || files.size() == 0) {
            return;
        }
        StringBuilder selection = new StringBuilder();
        selection.append(DataStructures.FileColumns.FILE_PATH_FIELD + " IN ");
        selection.append('(');
        int size = files.size();
        for(int i=0; i<size; i++)
        {
            if (i==0) {
                selection.append('\'');
                selection.append(files.get(i).replace("'", "\""));
                selection.append('\'');
            } else {
                selection.append(',');
                selection.append('\'');
                selection.append(files.get(i).replace("'", "\""));
                selection.append('\'');
            }
        }
        selection.append(')');

        Cursor cursor = null;
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(uri, new String[]{DataStructures.FileColumns.FILE_PATH_FIELD}, selection.toString(), null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    files.remove(path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private ArrayList<String> saveUnsyncedFileInDatabase(Uri uri) {

        Cursor cursor = null;
        String[] projection = new String[]{DataStructures.FileColumns.FILE_PATH_FIELD};
        String selection_where = DataStructures.FileColumns.FILE_SYNC_FIELD + "==?";
        String[] selection_arg = new String[]{String.valueOf(0)};
        ArrayList<String> unsynced_files = new ArrayList<String>();
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(uri, projection, selection_where, selection_arg, null);
            if (cursor != null) {
                while(cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    if (new File(path).exists()) {
                        unsynced_files.add(path);
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
//        return (unsynced_files==null || unsynced_files.size()==0) ? null : unsynced_files.toArray(new String[unsynced_files.size()]);
        return unsynced_files;
    }

    private int[] clearUnexistFileInDatabase(Uri uri) {
        int count = 0;
        String[] projection = new String[]{DataStructures.FileColumns.FILE_PATH_FIELD};

        StringBuilder delete_selection = new StringBuilder();
        delete_selection.append(DataStructures.FileColumns.FILE_PATH_FIELD + " IN ");
        delete_selection.append('(');
        boolean isFirst = true;
        Cursor cursor = null;
        int total_count = 0;
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(uri, projection, null, null, null );
            if (cursor != null) {
                total_count = cursor.getCount();
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
        delete_selection.append(')');
        if (!isFirst) {
            try {
                count = FileManager.getAppContext().getContentResolver().delete(uri, delete_selection.toString(), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new int[]{count, total_count-count};
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
                    if (!new File(path).exists() || new File(path).isDirectory() || new File(path).length() == 0) {
                        continue;
                    }
                    cv.put(DataStructures.FileColumns.FILE_PATH_FIELD, path);
                    cv.put(DataStructures.FileColumns.FILE_SIZE_FIELD, cursor.getLong(1));
                    cv.put(DataStructures.FileColumns.FILE_DATE_FIELD, cursor.getLong(2));
                    cv.put(DataStructures.FileColumns.FILE_SYNC_FIELD, 1);
                    String name = null;
                    if (!TextUtils.isEmpty(path)) {
                        int i = path.lastIndexOf("/");
                        if (i>0) {
                            name = path.substring(i+1);
                        }
                    }

                    if (TextUtils.isEmpty(name)) {
                        name = cursor.getString(3);
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
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "initDataFromMediaStore bulkinsert category="+category_type+", count = "+size);

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
