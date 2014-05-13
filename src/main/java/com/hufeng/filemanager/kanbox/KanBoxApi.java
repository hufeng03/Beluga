package com.hufeng.filemanager.kanbox;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hufeng.filemanager.BuildConfig;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.LogUtil;
import com.kanbox.api.Kanbox;
import com.kanbox.api.KanboxAsyncTask;
import com.kanbox.api.KanboxException;
import com.kanbox.api.KanboxHttp;
import com.kanbox.api.PushSharePreference;
import com.kanbox.api.RequestListener;
import com.kanbox.api.Token;

import org.apache.http.client.methods.HttpRequestBase;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxApi implements RequestListener{
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static HashMap<String, Integer> mDownloadingProgress = new HashMap<String, Integer>();

    public static HashMap<String, Integer> mUploadingProgress = new HashMap<String, Integer>();

    public static ConcurrentHashMap<String, KanboxAsyncTask> mDownloadingTasks = new ConcurrentHashMap<String, KanboxAsyncTask>();

    public static ConcurrentHashMap<String, KanboxAsyncTask> mUploadingTasks = new ConcurrentHashMap<String, KanboxAsyncTask>();

    public static ConcurrentHashMap<String, KanboxAsyncTask> mUploadingFailedTasks = new ConcurrentHashMap<String, KanboxAsyncTask>();

    public static ConcurrentHashMap<String, KanboxAsyncTask> mUploadingSuccessTasks = new ConcurrentHashMap<String, KanboxAsyncTask>();

    public static final String PUSH_SHAREPREFERENCE_NAME = "oauth";

    private static final String TAG = KanBoxApi.class.getSimpleName();

    private static KanBoxApi instance;

    private KanBoxApi() {

    }

    public static KanBoxApi getInstance(){
        if(instance == null) {
            instance = new KanBoxApi();
        }
        return instance;
    }

    public void getToken() {
        try {
            Token.getInstance().getToken(this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void logOut() {
        PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), PUSH_SHAREPREFERENCE_NAME);
        sPreference.clear();
        Token.getInstance().clear();
        CookieSyncManager.createInstance(FileManager.getAppContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }


//    public TOKEN_STATUS getTokenStatus() {
//        if(!TextUtils.isEmpty(Token.getInstance().getAcceccToken()) && !TextUtils.isEmpty(Token.getInstance().getRefreshToken())) {
//            long expirestill = Token.getInstance().getExpiresTill();
//            long current = System.currentTimeMillis();
//            if(current<expirestill) {
//                return TOKEN_STATUS.VALID;
//            } else {
//                return TOKEN_STATUS.OBSOLETE;
//            }
//        } else {
//            PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), PUSH_SHAREPREFERENCE_NAME);
//            if(sPreference.contains("accecc_token") && sPreference.contains("refresh_token")){
//                long current = System.currentTimeMillis();
//                long expiresTill = sPreference.getLongValueByKey("expriesTill");
//                Token.getInstance().setAcceccToken(sPreference.getStringValueByKey("accecc_token"));
//                Token.getInstance().setRefreshToken(sPreference.getStringValueByKey("refresh_token"));
//                Token.getInstance().setExpires(sPreference.getLongValueByKey("expries"));
//                Token.getInstance().setExpiresTill(expiresTill);
//                Log.i(TAG, "token expires at " + TimeUtil.getDateString(expiresTill) + ", current is " + TimeUtil.getDateString(current));
//                if(current<expiresTill) {
//                    return TOKEN_STATUS.VALID;
//                } else {
//                    return TOKEN_STATUS.OBSOLETE;
//                }
//            } else {
//                return TOKEN_STATUS.NONE;
//            }
//        }
//    }

    public interface KanBoxApiListener {
        public void onKanBoxApiSuccess(int op_type, String path, String response);
        public void onKanBoxApiFailed(int op_type, String path);
        public void onKanBoxApiProgress(int op_type, String path, int progress);
    }

    private WeakReference<KanBoxApiListener> mWeakListener;

    public void registerKanBoxApiListener(KanBoxApiListener listener) {
        mWeakListener = new WeakReference<KanBoxApiListener>(listener);
    }

    public void unRegisterKanBoxApiListener(KanBoxApiListener listener) {
        if (listener == getListener()) {
            mWeakListener.clear();
        }
    }

    private KanBoxApiListener getListener() {
        return mWeakListener==null?null:mWeakListener.get();
    }

    @Override
    public void onComplete(String path, String response, int operationType) {
        Log.i(TAG, "onComplete "+path+" "+operationType+" "+response);
        KanBoxApiListener listener = getListener();
        switch (operationType) {
            case OP_GET_THUMBNAIL:
                if(!TextUtils.isEmpty(response)) {
                    ContentValues cv = new ContentValues();
                    cv.put(DataStructures.CloudBoxColumns.ICON_DATA_FIELD, response);
                    FileManager.getAppContext().getContentResolver().update(DataStructures.CloudBoxColumns.CONTENT_URI,
                            cv, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?", new String[]{path});
                }
                break;
            case OP_COPY:
                break;
            case OP_DELETE:
                //
                FileManager.getAppContext().getContentResolver().delete(DataStructures.CloudBoxColumns.CONTENT_URI,
                        DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?", new String[]{path});
                PushSharePreference sPreference0 = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                //TODO: 删除云端文件时候不删除本地文件
//                String local_path = sPreference0.getStringValueByKey(path);
//                if(!TextUtils.isEmpty(local_path)) {
//                    FileAction.delete(local_path);
//                }
                sPreference0.removeSharePreferences(path);
                break;
            case OP_MOVE:
                PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                String new_path = sPreference.getStringValueByKey(path);
                if(!TextUtils.isEmpty(new_path)) {
                    ContentValues cv2 = new ContentValues();
                    cv2.put(DataStructures.CloudBoxColumns.FILE_PATH_FIELD, new_path);
                    int idx = path.lastIndexOf("/");
                    if ( idx >= 0 ){
                        String name = new_path.substring(idx + 1);
                        String parent = new_path.substring(0,idx + 1);
                        cv2.put(DataStructures.CloudBoxColumns.FILE_NAME_FIELD, name);
                        cv2.put(DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD, parent);
                    }
                    FileManager.getAppContext().getContentResolver().update(DataStructures.CloudBoxColumns.CONTENT_URI, cv2,
                            DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",new String[]{path});
                }
                sPreference.removeSharePreferences(path);
                break;
//            case OP_GET_TOKEN:
//            case OP_REFRESH_TOKEN:
//                try {
//                    Log.i(TAG, "token return "+response);
//                    Token.getInstance().parseToken(response);
//                    KanBoxApi.getInstance().saveToken(Token.getInstance());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
            case OP_GET_ACCCOUNT_INFO:
                KanBoxResponseHandler.handleHttpResult_GetAccountInfo(response);
                break;
            case OP_GET_FILELIST:
                KanBoxResponseHandler.handleHttpResult_GetFileList(path, response);
                break;
            case OP_DOWNLOAD:
                mDownloadingProgress.remove(path);
                removeDownloadingTask(path);
                PushSharePreference sPreference2 = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                if ("pause".equals(response)) {
                    //say something
                } else {
                    final String local_path2 = sPreference2.getStringValueByKey(path);

                    if(!TextUtils.isEmpty(local_path2) && new File(local_path2).exists()) {
                        ContentValues cv2 = new ContentValues();
                        cv2.put(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD, local_path2);
                        FileManager.getAppContext().getContentResolver().update(DataStructures.CloudBoxColumns.CONTENT_URI, cv2,
                                DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",new String[]{path});
                        FileAction.add(local_path2);
//                        Log.i(TAG, "try to scan into mediadb "+local_path2);
                        MediaScannerConnection.scanFile(FileManager.getAppContext(),
                                new String[]{local_path2}, null, null
//                                new MediaScannerConnection.OnScanCompletedListener() {
//                                    public void onScanCompleted(String path, Uri uri) {
////                                        Log.i(TAG, "scanned into mediadb "+local_path2+"@@@"+uri);
//                                    }
//                                }
                        );
                    }
                }
                sPreference2.removeSharePreferences(path);
                break;
            case OP_MAKE_DIR:
                ContentValues cv = new ContentValues();

                cv.put(DataStructures.CloudBoxColumns.FILE_PATH_FIELD, path);
                cv.put(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD, 1);
                cv.put(DataStructures.CloudBoxColumns.FILE_NAME_FIELD, new File(path).getName());
                cv.put(DataStructures.CloudBoxColumns.FILE_DATE_FIELD, System.currentTimeMillis());
                String parent = new File(path).getParent();
                if(parent.endsWith("/")) {
                    cv.put(DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD, parent);
                } else {
                    cv.put(DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD, parent+"/");
                }
                FileManager.getAppContext().getContentResolver().insert(DataStructures.CloudBoxColumns.CONTENT_URI,cv);
                break;
            case OP_UPLOAD:
                mUploadingProgress.remove(path);
                PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                if ("pause".equals(response)) {
                    moveUploadingTasktoFailed(path);
                } else {
                    moveUploadingTasktoSuccess(path);
                    String remote_path = preference.getStringValueByKey(path);
                    if (!TextUtils.isEmpty(remote_path)) {
                        KanBoxFileEntry entry = new KanBoxFileEntry(path);
                        entry.path = remote_path;
                        String parent2 = new File(remote_path).getParent();
                        if (!parent2.endsWith("/")) {
                            parent2 += "/";
                        }
                        entry.parent_path = parent2;
                        entry.local_file_path = path;
                        entry.is_directory = false;
                        entry.lastModified = System.currentTimeMillis();
                        ContentValues cv2 = KanBoxResponseHandler.buildFullContentValueFromKanBoxFileEntry(entry);
                        Uri uri = FileManager.getAppContext().getContentResolver().insert(DataStructures.CloudBoxColumns.CONTENT_URI, cv2);
                    }
                }
                preference.removeSharePreferences(path);
                break;
        }
        if (listener != null) {
            listener.onKanBoxApiSuccess(operationType, path, response);
        }
    }

    @Override
    public void onError(String path, KanboxException error, int operationType) {
        if (DEBUG) {
            Log.i(TAG, "onError " + path + " " + operationType + " ");
        }
        KanBoxApiListener listener = getListener();
        switch (operationType) {
            case OP_GET_THUMBNAIL:
                break;
            case OP_COPY:
                break;
            case OP_DELETE:
                PushSharePreference sPreference0 = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                sPreference0.removeSharePreferences(path);
                break;
            case OP_MOVE:
                PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                sPreference.removeSharePreferences(path);
                break;
            case OP_GET_TOKEN:
                break;
            case OP_GET_ACCCOUNT_INFO:
                break;
            case OP_GET_FILELIST:
                break;
            case OP_REFRESH_TOKEN:
                break;
            case OP_DOWNLOAD:
                PushSharePreference sPreference2 = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                sPreference2.removeSharePreferences(path);
                mDownloadingProgress.remove(path);
                removeDownloadingTask(path);
                break;
            case OP_MAKE_DIR:
                break;
            case OP_UPLOAD:
                PushSharePreference sPreference3 = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                sPreference3.removeSharePreferences(path);
                mUploadingProgress.remove(path);
                moveUploadingTasktoFailed(path);
                break;
        }
        if (listener != null) {
            listener.onKanBoxApiFailed(operationType, path);
        }
    }

    @Override
    public void downloadProgress(String path, long progress) {
        mDownloadingProgress.put(path, (int)progress);
        LogUtil.i(TAG,"progress of downloading "+ path +" is "+progress);
        KanBoxApiListener listener = getListener();
        if (listener != null) {
            listener.onKanBoxApiProgress(OP_DOWNLOAD, path, (int) progress);
        }
    }

    @Override
    public void uploadProgress(String path, long progress) {
        LogUtil.i(TAG,"progress of uploading "+ path +" is "+progress);
        mUploadingProgress.put(path, (int)progress);
        KanBoxApiListener listener = getListener();
        if (listener != null) {
            listener.onKanBoxApiProgress(OP_UPLOAD, path, (int) progress);
        }
    }

    public void refreshToken() {
        try {
            Token.getInstance().refreshToken(this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
//            Toast.makeText(getActivity(), "操作失败\n\n" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void getAccountInfo() {
        Kanbox.getInstance().getAccountInfo(Token.getInstance(), this);
    }

    public void getFileList(String path) {
        Token token = Token.getInstance();
        String getFileListUrl = "https://api.kanbox.com/0/list";
        Map<String,String> params = new HashMap<String, String>();
//        params.put("hash",);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());
        String hash = null;
        if(path.endsWith("/")) {
            hash = preferences.getString(path,"");
        } else {
            hash = preferences.getString(path+"/","");
        }
        if(!TextUtils.isEmpty(hash)) {
            params.put("hash",hash);
        }
        HttpRequestBase httpMethod = null;
        if(!"/".equals(path)) {
            path = path.substring(0,path.length()-1);

        }
        httpMethod = KanboxHttp.doGet(getFileListUrl + Kanbox.encodePath(path), params/*, token*/);

        new KanboxAsyncTask(path, null, httpMethod, this, RequestListener.OP_GET_FILELIST, true).serialExecute();
    }

    public void makeDir(String new_dir) {
        Token token = Token.getInstance();
        Kanbox.getInstance().makeDir(token, new_dir, this);
    }

    public void moveFile(String old_path, String new_path) {
        PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        preference.saveStringValueToSharePreferences(old_path, new_path);
        Token token = Token.getInstance();
        Kanbox.getInstance().moveFile(token, old_path, new_path, this);
    }

    public void deleteFile(String path) {
        Cursor cursor = null;
        try{
            cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                    new String[]{DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD}, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
                    new String[]{path}, null);
            if (cursor!=null && cursor.moveToNext()) {
                String local_path = cursor.getString(0);
                PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                preference.saveStringValueToSharePreferences(path, local_path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }

        Token token = Token.getInstance();
        Kanbox.getInstance().deleteFile(token, path, this);
    }

    public void downloadFile(String remote_path, String local_path) {
//        File file = new File(remote_path);
//        String name = new File(remote_path).getName();
//        try {
//            name = URLEncoder.encode(name, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        remote_path = new File(file.getParentFile().getAbsolutePath(), name).getAbsolutePath();
        mDownloadingProgress.put(remote_path, 0);
        PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        preference.saveStringValueToSharePreferences(remote_path, local_path);
        new File(local_path).getParentFile().mkdirs();
        KanboxAsyncTask task = Kanbox.getInstance().download(remote_path, local_path, Token.getInstance(), this);
        mDownloadingTasks.put(remote_path, task);
    }

    public void removeDownloadingTask(String path) {
        mDownloadingTasks.remove(path);
    }

//    public void removeUploadingTask(String path) {
//        Iterator<KanboxAsyncTask> iter = mUploadingTasks.iterator();
//        while(iter.hasNext()) {
//            KanboxAsyncTask task = iter.next();
//            if (task != null && task.getPath().equals(path)) {
//                mUploadingTasks.remove(task);
//                break;
//            }
//        }
//    }

    public void moveUploadingTasktoSuccess(String path) {
        if (TextUtils.isEmpty(path)) return;
        KanboxAsyncTask task = mUploadingTasks.remove(path);
        if (task == null) return;
        task.mOperationTime = System.currentTimeMillis();
        mUploadingSuccessTasks.put(path, task);
        if (mUploadingTasks.size() == 0 && mUploadingFailedTasks.size() == 0) {
            mUploadingSuccessTasks.clear();
        }
    }

    public void moveUploadingTasktoFailed(String path) {
        if (TextUtils.isEmpty(path)) return;
        KanboxAsyncTask task = mUploadingTasks.remove(path);
        if (task == null) return;
        task.mOperationTime = System.currentTimeMillis();
        mUploadingFailedTasks.put(path, task);
    }


    public void pauseDownloadFile(String path) {
        mDownloadingProgress.remove(path);
        KanboxAsyncTask downloadingTask = mDownloadingTasks.get(path);
        if (downloadingTask != null && !downloadingTask.isCancelled()) {
            downloadingTask.cancel(true);
        }
    }


    public void pauseUploadFile(String path) {
        if (TextUtils.isEmpty(path)) return;
        mUploadingProgress.remove(path);
        KanboxAsyncTask uploadingTask = mUploadingTasks.get(path);
        if (uploadingTask != null && !uploadingTask.isCancelled()) {
            uploadingTask.cancel(true);
            uploadingTask.mOperationTime = System.currentTimeMillis();
        }
    }

    public static boolean isDownloading(String path) {
        return mDownloadingProgress.containsKey(path);
    }

    public static boolean isDownloadCancelling(String path) {
        KanboxAsyncTask task =  mDownloadingTasks.get(path);
        return (task != null && task.isCancelled());
    }

    public static boolean isDownloadWaiting(String path) {
        KanboxAsyncTask task =  mDownloadingTasks.get(path);
        return (task != null && !task.isCancelled() && !task.mStarted);
    }

    public static int getDownloadingProgress(String path) {
        Integer val =  mDownloadingProgress.get(path);
        return val == null ? 0 :val;
    }

    public static boolean isUploading(String path) {
        KanboxAsyncTask task =  mUploadingTasks.get(path);
        return (task !=null);
    }

    public static boolean isUploadWorking(String path) {
        KanboxAsyncTask task =  mUploadingTasks.get(path);
        return (task !=null && !task.isCancelled() && task.mStarted);
    }

    public static boolean isUploadCancelling(String path) {
        KanboxAsyncTask task =  mUploadingTasks.get(path);
        return (task != null && task.isCancelled());
    }

    public static boolean isUploadWaiting(String path) {
        KanboxAsyncTask task =  mUploadingTasks.get(path);
        return (task != null && !task.isCancelled() && !task.mStarted);
    }

    public static long getUploadingTime(String path) {
        KanboxAsyncTask task =  mUploadingTasks.get(path);
        return (task != null)? task.mOperationTime : 0;
    }

    public static long getUploadWaitingTime(String path) {
        KanboxAsyncTask task =  mUploadingTasks.get(path);
        return (task != null && !task.isCancelled() && !task.mStarted) ? task.mOperationTime : 0;
    }

    public static long getUploadCancellingTime(String path) {
        KanboxAsyncTask task =  mUploadingTasks.get(path);
        return (task != null && task.isCancelled())?task.mOperationTime : 0;
    }

    public static long getUploadCancelledTime(String path) {
        KanboxAsyncTask task =  mUploadingFailedTasks.get(path);
        return (task != null && task.isCancelled())?task.mOperationTime : 0;
    }

    public static boolean isUploadSuccess(String path) {
        return mUploadingSuccessTasks.containsKey(path);
    }

    public static long getUploadSuccessTime(String path) {
        KanboxAsyncTask task = mUploadingSuccessTasks.get(path);
        return (task != null)?task.mOperationTime : 0;
    }

    public static boolean isUploadFailed(String path) {
        return mUploadingFailedTasks.containsKey(path);
    }

    public static long getUploadFailedTime(String path) {
        KanboxAsyncTask task = mUploadingFailedTasks.get(path);
        return (task != null)?task.mOperationTime : 0;
    }

    public static int getUploadingingProgress(String path) {
        Integer val =  mUploadingProgress.get(path);
        return val == null ? 0 :val;
    }

    public List<FileEntry> getUploadingFiles() {
        List<FileEntry> uploadingFiles = new ArrayList<FileEntry>();
        Iterator<String> iter = mUploadingTasks.keySet().iterator();
        while(iter.hasNext()) {
            String path = iter.next();
                if (!TextUtils.isEmpty(path)) {
                    FileEntry entry = new FileEntry(path);

                    entry.lastModified = 0-getUploadingTime(path);
                    uploadingFiles.add(entry);
                }
        }
        return uploadingFiles;
    }

    public List<FileEntry> getUploadingSuccessFiles() {
        List<FileEntry> uploadingFiles = new ArrayList<FileEntry>();
        Iterator<String> iter = mUploadingSuccessTasks.keySet().iterator();
        while(iter.hasNext()) {
            String path = iter.next();
                if (!TextUtils.isEmpty(path)) {
                    FileEntry entry = new FileEntry(path);
                    entry.lastModified = getUploadSuccessTime(path);
                    uploadingFiles.add(entry);
                }
        }
        return uploadingFiles;
    }

    public List<FileEntry> getUploadingFailedFiles() {
        List<FileEntry> uploadingFiles = new ArrayList<FileEntry>();
        Iterator<String> iter = mUploadingFailedTasks.keySet().iterator();
        while(iter.hasNext()) {
            String path = iter.next();
                if (!TextUtils.isEmpty(path)) {
                    FileEntry entry = new FileEntry(path);
                    entry.lastModified = getUploadFailedTime(path);
                    uploadingFiles.add(entry);
                }
        }
        return uploadingFiles;
    }

    public void uploadFileAgain(String localPath) {
        KanboxAsyncTask task = mUploadingFailedTasks.get(localPath);
        String destPath = task.getDestPath();
//        if(destPath!=null) {
//            int idx = destPath.lastIndexOf("/");
//            destPath = destPath.substring(0,idx+1);
//        }
        mUploadingProgress.put(localPath, 0);
        PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        preference.saveStringValueToSharePreferences(localPath, destPath);
        KanboxAsyncTask new_task = null;
        try{
            new_task = Kanbox.getInstance().upload(localPath, destPath, Token.getInstance(), this);
        }catch (IOException e) {
            e.printStackTrace();
        }
        mUploadingSuccessTasks.remove(localPath);
        mUploadingFailedTasks.remove(localPath);
        if (new_task != null) {
            new_task.mOperationTime = System.currentTimeMillis();
            mUploadingTasks.put(localPath, new_task);
        }
    }


    public void uploadFile(String localPath, String root) {
        Log.i(TAG, "upload File from "+localPath+" to "+root);
        mUploadingProgress.put(localPath, 0);
        String name = new File(localPath).getName();
        String destPath = new File(root, name).getAbsolutePath();
        PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        preference.saveStringValueToSharePreferences(localPath, destPath);
        KanboxAsyncTask task = null;
        try{
            task = Kanbox.getInstance().upload(localPath, destPath, Token.getInstance(), this);
        }catch (IOException e) {
            e.printStackTrace();
        }
        mUploadingSuccessTasks.remove(localPath);
        mUploadingFailedTasks.remove(localPath);
        if (task != null) {
            task.mOperationTime = System.currentTimeMillis();
            mUploadingTasks.put(localPath, task);
        }
    }
}
