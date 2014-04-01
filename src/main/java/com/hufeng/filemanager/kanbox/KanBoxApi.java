package com.hufeng.filemanager.kanbox;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hufeng.filemanager.BuildConfig;
import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.TimeUtil;
import com.kanbox.api.Kanbox;
import com.kanbox.api.KanboxAsyncTask;
import com.kanbox.api.KanboxException;
import com.kanbox.api.KanboxHttp;
import com.kanbox.api.PushSharePreference;
import com.kanbox.api.RequestListener;
import com.kanbox.api.Token;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxApi implements RequestListener{
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static HashMap<String, Integer> mDownloadingProgress = new HashMap<String, Integer>();

    public static HashMap<String, Integer> mUploadingProgress = new HashMap<String, Integer>();

    public static LinkedBlockingQueue<KanboxAsyncTask> mDownloadingTasks = new LinkedBlockingQueue<KanboxAsyncTask>();

    public static LinkedBlockingQueue<KanboxAsyncTask> mUploadingTasks = new LinkedBlockingQueue<KanboxAsyncTask>();

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

    public enum TOKEN_STATUS {
        VALID, OBSOLETE, NONE;
    }

    public void getToken(String code) {
        try {
            Token.getInstance().getToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET, code, KanBoxConfig.GET_TOKEN_REDIRECT_URI, this);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void clearToken() {
        PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), PUSH_SHAREPREFERENCE_NAME);
        sPreference.clear();
        Token.clear();
        CookieSyncManager.createInstance(FileManager.getAppContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    public void saveToken(Token token) {
        PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), PUSH_SHAREPREFERENCE_NAME);
        sPreference.saveStringValueToSharePreferences("accecc_token", token.getAcceccToken());
        sPreference.saveStringValueToSharePreferences("refresh_token", token.getRefreshToken());
        Log.i(TAG, "save token expires "+ TimeUtil.getDateString(token.getExpiresTill())+"|"+token.getExpires());
        sPreference.saveLongValueToSharePreferences("expriesTill", token.getExpiresTill());
        sPreference.saveLongValueToSharePreferences("expries", token.getExpires());
    }

    public TOKEN_STATUS getTokenStatus() {
        if(!TextUtils.isEmpty(Token.getInstance().getAcceccToken()) && !TextUtils.isEmpty(Token.getInstance().getRefreshToken())) {
            long expirestill = Token.getInstance().getExpiresTill();
            long current = System.currentTimeMillis();
            if(current<expirestill) {
                return TOKEN_STATUS.VALID;
            } else {
                return TOKEN_STATUS.OBSOLETE;
            }
        } else {
            PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), PUSH_SHAREPREFERENCE_NAME);
            if(sPreference.contains("accecc_token") && sPreference.contains("refresh_token")){
                long current = System.currentTimeMillis();
                long expiresTill = sPreference.getLongValueByKey("expriesTill");
                Token.getInstance().setAcceccToken(sPreference.getStringValueByKey("accecc_token"));
                Token.getInstance().setRefreshToken(sPreference.getStringValueByKey("refresh_token"));
                Token.getInstance().setExpires(sPreference.getLongValueByKey("expries"));
                Token.getInstance().setExpiresTill(expiresTill);
                Log.i(TAG, "token expires at " + TimeUtil.getDateString(expiresTill) + ", current is " + TimeUtil.getDateString(current));
                if(current<expiresTill) {
                    return TOKEN_STATUS.VALID;
                } else {
                    return TOKEN_STATUS.OBSOLETE;
                }
            } else {
                return TOKEN_STATUS.NONE;
            }
        }
    }

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
            case OP_GET_TOKEN:
            case OP_REFRESH_TOKEN:
                try {
                    Token.getInstance().parseToken(response);
                    KanBoxApi.getInstance().saveToken(Token.getInstance());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case OP_GET_ACCCOUNT_INFO:
                break;
            case OP_GET_FILELIST:
                KanBoxResponseHandler.handleHttpResult_GetFileList(response);
                break;
            case OP_DOWNLOAD:
                mDownloadingProgress.remove(path);
                removeDownloadingTask(path);
                PushSharePreference sPreference2 = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                if ("pause".equals(response)) {
                    //say something
                } else {
                    String local_path2 = sPreference2.getStringValueByKey(path);

                    if(!TextUtils.isEmpty(local_path2)) {
                        ContentValues cv2 = new ContentValues();
                        cv2.put(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD, local_path2);
                        FileManager.getAppContext().getContentResolver().update(DataStructures.CloudBoxColumns.CONTENT_URI, cv2,
                                DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",new String[]{path});
                        FileAction.add(local_path2);
                    }
                }
                sPreference2.removeSharePreferences(path);
                break;
            case OP_MAKE_DIR:
                ContentValues cv = new ContentValues();

                cv.put(DataStructures.CloudBoxColumns.FILE_PATH_FIELD, path);
                cv.put(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD, 1);
                cv.put(DataStructures.CloudBoxColumns.FILE_NAME_FIELD, new File(path).getName());
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
                removeUploadingTask(path);
                PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                String remote_path = preference.getStringValueByKey(path);
                if(!TextUtils.isEmpty(remote_path)) {
                    KanBoxFileEntry entry = new KanBoxFileEntry(path);
                    entry.path = remote_path;
                    String parent2 = new File(remote_path).getParent();
                    if(!parent2.endsWith("/")) {
                       parent2+="/";
                    }
                    entry.parent_path = parent2;
                    entry.local_file_path = path;
                    entry.is_directory = false;
                    ContentValues cv2 = KanBoxResponseHandler.buildFullContentValueFromKanBoxFileEntry(entry);
                    Uri uri = FileManager.getAppContext().getContentResolver().insert(DataStructures.CloudBoxColumns.CONTENT_URI, cv2);
                }
                break;
        }
        if (listener != null) {
            listener.onKanBoxApiSuccess(operationType, path, response);
        }
    }

    @Override
    public void onError(String path, KanboxException error, int operationType) {
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
                if(getTokenStatus()== KanBoxApi.TOKEN_STATUS.OBSOLETE) {
                    refreshToken();
                }
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
                removeUploadingTask(path);
                break;
        }
        if(DEBUG) {
            error.printStackTrace();
            Log.i(TAG, "failed of operation "+operationType +" with error: "+error.getStatusCode());
        }
        if (listener != null) {
            listener.onKanBoxApiFailed(operationType, path);
        }
    }

    @Override
    public void downloadProgress(String path, long progress) {
        if(progress<5) {
            progress = 5;
        }
        mDownloadingProgress.put(path, (int)progress);
        KanBoxApiListener listener = getListener();
        if (listener != null) {
            listener.onKanBoxApiProgress(OP_DOWNLOAD, path, (int) progress);
        }
    }

    @Override
    public void uploadProgress(String path, long progress) {
        LogUtil.i(TAG,"progress of uploading "+ path +" is "+progress);
        if(progress<5) {
            progress = 5;
        }
        mUploadingProgress.put(path, (int)progress);
        KanBoxApiListener listener = getListener();
        if (listener != null) {
            listener.onKanBoxApiProgress(OP_UPLOAD, path, (int) progress);
        }
    }

    public void refreshToken() {
        try {
            Token.getInstance().refreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET, this);
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
            httpMethod = KanboxHttp.doGet(getFileListUrl + path.substring(0,path.length()-1), params, token);
        } else {
            httpMethod = KanboxHttp.doGet(getFileListUrl + path, params, token);
        }

        new KanboxAsyncTask(null, null, httpMethod, this, RequestListener.OP_GET_FILELIST).serialExecute();
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
        mDownloadingProgress.put(remote_path, 5);
        PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        preference.saveStringValueToSharePreferences(remote_path, local_path);
        new File(local_path).getParentFile().mkdirs();
        KanboxAsyncTask task = Kanbox.getInstance().download(remote_path, local_path, Token.getInstance(), this);
        mDownloadingTasks.add(task);
    }

    public void removeDownloadingTask(String path) {
        Iterator<KanboxAsyncTask> iter = mDownloadingTasks.iterator();
        while(iter.hasNext()) {
            KanboxAsyncTask task = iter.next();
            if (task!=null && task.getPath().equals(path)) {
                mDownloadingTasks.remove(task);
                break;
            }
        }
    }

    public void removeUploadingTask(String path) {
        Iterator<KanboxAsyncTask> iter = mUploadingTasks.iterator();
        while(iter.hasNext()) {
            KanboxAsyncTask task = iter.next();
            if (task != null && task.getPath().equals(path)) {
                mUploadingTasks.remove(task);
                break;
            }
        }
    }


    public void pauseDownloadFile(String path) {
        mDownloadingProgress.remove(path);
        KanboxAsyncTask downloadingTask = null;
        Iterator<KanboxAsyncTask> iter = mDownloadingTasks.iterator();
        while(iter.hasNext()) {
            KanboxAsyncTask task = iter.next();
            if (task.getPath().equals(path)) {
                downloadingTask = task;
                mDownloadingTasks.remove(task);
                break;
            }
        }
        if (downloadingTask != null) {
            downloadingTask.pause();
        }
    }


    public void pauseUploadFile(String path) {
        mUploadingProgress.remove(path);
        KanboxAsyncTask uploadingTask = null;
        Iterator<KanboxAsyncTask> iter = mUploadingTasks.iterator();
        while(iter.hasNext()) {
            KanboxAsyncTask task = iter.next();
            if (task.getPath().equals(path)) {
                uploadingTask = task;
                mUploadingTasks.remove(task);
                break;
            }
        }
        if (uploadingTask != null) {
            uploadingTask.pause();
        }
    }


    public void downloadThumbnail(String remote_path) {
        Kanbox.getInstance().getThumbnail(remote_path, Token.getInstance(), this);
    }

    public static boolean isDownloading(String path) {
        return mDownloadingProgress.containsKey(path);
    }

    public static int getDownloadingProgress(String path) {
        return mDownloadingProgress.get(path);
    }

    public static boolean isUploading(String path) {
        return mUploadingProgress.containsKey(path);
    }

    public static int getUploadingingProgress(String path) {
        return mUploadingProgress.get(path);
    }

    public List<FileEntry> getUploadingFiles() {
        List<FileEntry> uploadingFiles = new ArrayList<FileEntry>();
        Iterator<KanboxAsyncTask> iter = mUploadingTasks.iterator();
        while(iter.hasNext()) {
            KanboxAsyncTask task = iter.next();
            if (task != null) {
                String path = task.getPath();
                if (!TextUtils.isEmpty(path)) {
                    uploadingFiles.add(new FileEntry(path));
                }
            }
        }
        return uploadingFiles;
    }


    public void uploadFile(String localPath, String root) {
        mUploadingProgress.put(localPath, 5);
        String name = new File(localPath).getName();
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String destPath = new File(root, name).getAbsolutePath();
        PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        preference.saveStringValueToSharePreferences(localPath, destPath);
        KanboxAsyncTask task = null;
        try{
            task = Kanbox.getInstance().upload(localPath, destPath, Token.getInstance(), this);
        }catch (IOException e) {
            e.printStackTrace();
        }
        if (task != null) {
            mUploadingTasks.add(task);
        }
    }

    public boolean uploadFileSynchronized(String localPath, String root, Token token) {
        String uploadUrl = "https://api-upload.kanbox.com/0/upload";
        HttpPost httpMethod = null;
        String name = new File(localPath).getName();
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String destPath = new File(root, name).getAbsolutePath();
        InputStream is = null;
        try {
            httpMethod = KanboxHttp.doPost(uploadUrl + destPath, null, token);
            is = new FileInputStream(localPath);
            httpMethod.setEntity(new InputStreamEntity(is, is.available()));
            HttpClient sHttpClient = KanboxAsyncTask.createHttpClient();
            HttpResponse sHttpResponse = sHttpClient.execute(httpMethod);
            int statusCode = sHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String strResult = EntityUtils.toString(sHttpResponse.getEntity());
                KanBoxFileEntry entry = new KanBoxFileEntry(localPath);
                entry.path = destPath;
                entry.parent_path = root;
                entry.local_file_path = localPath;
                entry.is_directory = false;
                ContentValues cv = KanBoxResponseHandler.buildFullContentValueFromKanBoxFileEntry(entry);
                Uri uri = FileManager.getAppContext().getContentResolver().insert(DataStructures.CloudBoxColumns.CONTENT_URI, cv);
                return true;
            } else {
                LogUtil.i(TAG, "Kanbox upload return "+statusCode);
                //mException = new KanboxException(statusCode);
                return false;
            }
        } catch (ClientProtocolException e) {
            //mException = new KanboxException(e);
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            //mException = new KanboxException(e);
            e.printStackTrace();
            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        new KanboxAsyncTask(destPath, httpMethod, listener, RequestListener.OP_UPLOAD).execute();
    }
}
