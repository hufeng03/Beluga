package com.belugamobile.filemanager.services;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.mount.MountPoint;
import com.belugamobile.filemanager.mount.MountPointManager;
import com.belugamobile.filemanager.provider.DataStructures;
import com.belugamobile.filemanager.utils.LogUtil;
import com.belugamobile.filemanager.utils.MediaStoreUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IFileSyncServiceImpl extends IFileSyncService.Stub{
	
	private static final String LOG_TAG = IFileSyncServiceImpl.class.getSimpleName();
	
	private Context mContext;

    private boolean mFilterSmallIcon = false;
    private boolean mFilterSmallAudio = false;

    public boolean mIsScanning = false;

    private BroadcastReceiver mMediaReceiver;

    private Handler mMainThreadHandler = new MainThreadHandler();

    private class MainThreadHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            performScan();
        }
    }
	
	public IFileSyncServiceImpl(Context context) {
		mContext = context;
	}


    public void onCreate() {
        //Start first time scan
        mMainThreadHandler.sendEmptyMessageDelayed(0,1000);
        registerMediaReceiver();
    }


    public void onDestroy() {
        unregisterMediaReceiver();
    }


    private void registerMediaReceiver() {
        mMediaReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(intent.getAction())){
                    if(mMainThreadHandler.hasMessages(0))
                        mMainThreadHandler.removeMessages(0);
                    mMainThreadHandler.sendEmptyMessageDelayed(0,3000);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        filter.addDataScheme("file");
        mContext.registerReceiver(mMediaReceiver, filter);
    }

    private void unregisterMediaReceiver() {
        if (mMediaReceiver != null) {
            mContext.unregisterReceiver(mMediaReceiver);
            mMediaReceiver = null;
        }
    }

    @Override
    public void forceScan() throws RemoteException
	{
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "forceScan");

        performScan();

        return;
	}

    private void performScan() {
        if (!mIsScanning) {
            mIsScanning = true;
            new ScanTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class ScanTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            refreshDatabase();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mIsScanning = false;
        }
    }
	
	private void refreshDatabase()
	{
		ContentValues category_values = new ContentValues();
		category_values.put(DataStructures.CategoryColumns.SIZE, 0);
		category_values.put(DataStructures.CategoryColumns.NUMBER, 0);
		int count = 0;
        try {
            count = FileManager.getAppContext().getContentResolver().update(DataStructures.CategoryColumns.CONTENT_URI, category_values, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "update category size and number to 0, return "+count);
		if(count==0)
		{
			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "we will do insert");
			List<MountPoint> mps = MountPointManager.getInstance().getMountPoints();
			for(MountPoint mp: mps){
				category_values.put(DataStructures.CategoryColumns.STORAGE, mp.mPath);
				category_values.put(DataStructures.CategoryColumns.CATEGORY, FileCategoryHelper.CATEGORY_TYPE_ZIP);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY, FileCategoryHelper.CATEGORY_TYPE_APK);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY, FileCategoryHelper.CATEGORY_TYPE_DOCUMENT);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY, FileCategoryHelper.CATEGORY_TYPE_IMAGE);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY, FileCategoryHelper.CATEGORY_TYPE_AUDIO);
				FileManager.getAppContext().getContentResolver().insert(DataStructures.CategoryColumns.CONTENT_URI, category_values);
				category_values.put(DataStructures.CategoryColumns.CATEGORY, FileCategoryHelper.CATEGORY_TYPE_VIDEO);
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

//		count = FileManager.getAppContext().getContentResolver().delete(DataStructures.FileColumns.CONTENT_URI, selection_where, selection_arg);
//		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete all file: "+count);
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

        int[] counts = clearUnExistFileInDatabase(DataStructures.FavoriteColumns.CONTENT_URI);
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete not exist favorite: "+counts[0]+","+counts[1]);


        initDatabaseFromMediaStore();

        List<String> important_dirs = IFolderMonitorUtil.getAllImportantFolders();

        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "get import dirs: "+ (important_dirs == null ? 0 :important_dirs.size()));

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

        addFileIntoDatabase(DataStructures.ImageColumns.CONTENT_URI, FileCategoryHelper.CATEGORY_TYPE_IMAGE, save_unsyned_images);
        addFileIntoDatabase(DataStructures.AudioColumns.CONTENT_URI, FileCategoryHelper.CATEGORY_TYPE_AUDIO, save_unsyned_audios);
        addFileIntoDatabase(DataStructures.VideoColumns.CONTENT_URI, FileCategoryHelper.CATEGORY_TYPE_VIDEO, save_unsyned_videos);
        addFileIntoDatabase(DataStructures.ApkColumns.CONTENT_URI, FileCategoryHelper.CATEGORY_TYPE_APK, save_unsyned_apks);
        addFileIntoDatabase(DataStructures.DocumentColumns.CONTENT_URI, FileCategoryHelper.CATEGORY_TYPE_DOCUMENT, save_unsyned_documents);
        addFileIntoDatabase(DataStructures.ZipColumns.CONTENT_URI, FileCategoryHelper.CATEGORY_TYPE_ZIP, save_unsyned_zips);


	}

    private void scanImportantDirectory(List<String> dirs, ArrayList<String> images,
                                      ArrayList<String> audios, ArrayList<String> videos,
                                      ArrayList<String> apks, ArrayList<String> documents, ArrayList<String> zips) {

        boolean filter_small_image = (FileManager.getPreference(FileManager.IMAGE_FILTER_SMALL, "1") == "1");
        boolean filter_small_audio = (FileManager.getPreference(FileManager.AUDIO_FILTER_SMALL, "1") == "1");
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
                            int category = FileCategoryHelper.getFileCategoryForPath(child.getAbsolutePath());
                            String name = child.getAbsolutePath();
                            switch (category) {
                                case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                                    if (!images.contains(name)) {
                                        if (!filter_small_image || new File(name).length() > 30720) {
                                            images.add(name);
                                        }
                                    }
                                    break;
                                case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                                    if (!audios.contains(name)) {
                                        if (!filter_small_audio || new File(name).length() > 102400) {
                                            audios.add(name);
                                        }
                                    }
                                    break;
                                case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                                    if (!videos.contains(name)) {
                                        videos.add(name);
                                    }
                                    break;
                                case FileCategoryHelper.CATEGORY_TYPE_APK:
                                    if (!apks.contains(name)) {
                                        apks.add(name);
                                    }
                                    break;
                                case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                                    if (!documents.contains(name)) {
                                        documents.add(name);
                                    }
                                    break;
                                case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                                    if (!zips.contains(name)) {
                                        zips.add(name);
                                    }
                                    break;
                                default:
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
            cvs[i].put(DataStructures.FileColumns.SYNC, 0);
            BelugaFileEntry entry = BelugaProviderHelper.createFileEntryAccordingToCategory(
                    files.get(i), category);
            entry.fillContentValues(cvs[i]);
        }
        FileManager.getAppContext().getContentResolver().bulkInsert(uri, cvs);
    }

    private void filterAgainstDatabase(Uri uri, ArrayList<String> files) {
        if (files == null || files.size() == 0) {
            return;
        }
        StringBuilder selection = new StringBuilder();
        selection.append(DataStructures.FileColumns.PATH + " IN ");
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
            cursor = FileManager.getAppContext().getContentResolver().query(uri, new String[]{DataStructures.FileColumns.PATH}, selection.toString(), null, null);
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
        String[] projection = new String[]{DataStructures.FileColumns.PATH};
        String selection_where = DataStructures.FileColumns.SYNC + "==?";
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

    private int[] clearUnExistFileInDatabase(Uri uri) {
        int count = 0;
        String[] projection = new String[]{DataStructures.FileColumns.PATH};

        StringBuilder delete_selection = new StringBuilder();
        delete_selection.append(DataStructures.FileColumns.PATH + " IN ");
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

    private void copyMediaStoreToDatabase(Uri uri_mediastore, String[] projection_mediastore, String selection_mediastore,
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
                while(cursor.moveToNext()) {
                    ContentValues cv = new ContentValues();
                    String path = cursor.getString(0);
                    if (!new File(path).exists() || new File(path).isDirectory() || new File(path).length() == 0) {
                        continue;
                    }
                    cv.put(DataStructures.FileColumns.PATH, path);
                    cv.put(DataStructures.FileColumns.SIZE, cursor.getLong(1));
                    cv.put(DataStructures.FileColumns.DATE, cursor.getLong(2));
                    cv.put(DataStructures.FileColumns.SYNC, 1);
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
                        cv.put(DataStructures.FileColumns.NAME, name);
                        int i = name.lastIndexOf(".");
                        if(i>0) {
                            cv.put(DataStructures.FileColumns.EXTENSION, name.substring(i+1));
                        }
                    }
                    String storage_path = MountPointManager.getInstance().getRealMountPointPath(path);
                    cv.put(DataStructures.FileColumns.STORAGE, storage_path);
                    switch (category_type) {
                        case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                            cv.put(DataStructures.ImageColumns.IMAGE_WIDTH, cursor.getInt(4));
                            cv.put(DataStructures.ImageColumns.IMAGE_HEIGHT, cursor.getInt(5));
                            break;
                        case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                            cv.put(DataStructures.VideoColumns.PLAY_DURATION, cursor.getLong(4));
                            break;
                        case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                            cv.put(DataStructures.AudioColumns.PLAY_DURATION, cursor.getLong(4));
                            cv.put(DataStructures.AudioColumns.ALBUM, cursor.getString(5));
                            cv.put(DataStructures.AudioColumns.SINGER, cursor.getString(6));
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
        String filter = FileManager.getPreference(FileManager.IMAGE_FILTER_SMALL, "1");
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
        copyMediaStoreToDatabase(uri, projection, selection, uri_fm, FileCategoryHelper.CATEGORY_TYPE_IMAGE);


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
        copyMediaStoreToDatabase(uri, projection, selection, uri_fm, FileCategoryHelper.CATEGORY_TYPE_VIDEO);


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
        filter = FileManager.getPreference(FileManager.AUDIO_FILTER_SMALL, "1");
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
        copyMediaStoreToDatabase(uri, projection, selection, uri_fm, FileCategoryHelper.CATEGORY_TYPE_AUDIO);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            projection = new String[]{
                    MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.DATE_MODIFIED,
                    MediaStore.MediaColumns.DISPLAY_NAME
            };
            //apk
            uri = MediaStore.Files.getContentUri(volumeName);
            selection = MediaStoreUtil.buildSelectionByCategory(FileCategoryHelper.CATEGORY_TYPE_APK);
            uri_fm = DataStructures.ApkColumns.CONTENT_URI;
            copyMediaStoreToDatabase(uri, projection, selection, uri_fm, FileCategoryHelper.CATEGORY_TYPE_APK);

            //zip
            selection = MediaStoreUtil.buildSelectionByCategory(FileCategoryHelper.CATEGORY_TYPE_ZIP);
            uri_fm = DataStructures.ZipColumns.CONTENT_URI;
            copyMediaStoreToDatabase(uri, projection, selection, uri_fm, FileCategoryHelper.CATEGORY_TYPE_ZIP);


            //document
            selection = MediaStoreUtil.buildSelectionByCategory(FileCategoryHelper.CATEGORY_TYPE_DOCUMENT);
            uri_fm = DataStructures.DocumentColumns.CONTENT_URI;
            copyMediaStoreToDatabase(uri, projection, selection, uri_fm, FileCategoryHelper.CATEGORY_TYPE_DOCUMENT);
        }

    }

	@Override
	public boolean isScanning() throws RemoteException {
		return mIsScanning;
	}

}
