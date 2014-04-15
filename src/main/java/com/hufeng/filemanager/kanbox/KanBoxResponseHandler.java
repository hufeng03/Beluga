package com.hufeng.filemanager.kanbox;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.storage.StorageManager;
import com.kanbox.api.PushSharePreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by feng on 13-11-22.
 */
public class KanBoxResponseHandler {

    private static final String TAG = KanBoxResponseHandler.class.getSimpleName();

    public static void handleHttpResult_GetAccountInfo(final String response) {
        try {
            JSONObject sData = new JSONObject(response);
            String email = sData.getString("email");
            if (TextUtils.isEmpty(email)) {
                email = "";
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());
            String old_email = preferences.getString("KanBox_Account_Email", "");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("KanBox_Account_Email", email);
            editor.commit();
            if (TextUtils.isEmpty(old_email) || !old_email.equals(email)) {
                FileManager.getAppContext().getContentResolver().delete(DataStructures.CloudBoxColumns.CONTENT_URI, null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleHttpResult_GetFileList(String parent, final String response) {
        final String parent_path;
        if(parent != null && !parent.endsWith("/")) {
            parent_path = parent+"/";
        } else {
            parent_path = parent;
        }

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i(TAG, "getFileList return "+response);
                try {
                    JSONObject sData = new JSONObject(response);
                    String status = sData.getString("status");
                    if (status.equals("ok")) {
                        String hash = sData.getString("hash");
                        JSONArray array = sData.getJSONArray("contents");
                        int len = array.length();
                        if ( len > 0) {
//                            String parent_path = null;
                            int i = 0;
                            HashMap<String, KanBoxFileEntry> entries = new HashMap<String, KanBoxFileEntry>();
                            StringBuilder selection = new StringBuilder();
    //                      selection.append(DataStructures.CloudBoxColumns.FILE_PATH_FIELD + " IN ");
                            selection.append('(');
                            boolean isFirst = true;
                            while(i<len) {
                                JSONObject obj = array.getJSONObject(i);
                                KanBoxFileEntry entry = new KanBoxFileEntry(hash, obj);
                                Log.i(TAG, "kanboxfileentry "+i+": "+entry);
//                                if (i==0) {
//                                    parent_path = entry.parent_path;
//                                }
                                entries.put(entry.path, entry);
                                if (isFirst) {
                                    selection.append('\'');
                                    selection.append(entry.path.replace("'", "\""));
                                    selection.append('\'');
                                    isFirst = false;
                                } else {
                                    selection.append(',');
                                    selection.append('\'');
                                    selection.append(entry.path.replace("'", "\""));
                                    selection.append('\'');
                                }
                                i++;
                            }
                            selection.append(')');

                            Cursor cursor = null;
                            try {
                                cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                                        new String[]{DataStructures.CloudBoxColumns.FILE_PATH_FIELD,
                                                DataStructures.CloudBoxColumns.FILE_DATE_FIELD,
                                                DataStructures.CloudBoxColumns.FILE_SIZE_FIELD,
                                                DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD,
                                                DataStructures.CloudBoxColumns.ICON_DATA_FIELD,
                                                DataStructures.CloudBoxColumns.HASH_FIELD,
                                        }, DataStructures.CloudBoxColumns.FILE_PATH_FIELD + " IN "+selection.toString(), null, null);
                                if(cursor!=null) {
                                    while(cursor.moveToNext()) {
                                        String path = cursor.getString(0);
                                        KanBoxFileEntry entry = entries.get(path);
                                        long date = cursor.getLong(1);
                                        long size = cursor.getLong(2);
                                        String local_file = cursor.getString(3);
                                        byte[] icon_data = cursor.getBlob(4);
                                        String local_hash = cursor.getString(5);
                                        if(TextUtils.isEmpty(local_hash)) {
                                            entry.local_file_path = local_file;
                                            entry.icon_data = icon_data;
                                        } else if ( entry.lastModified == date && entry.size == size) {
                                            if (new File(local_file).exists() && new File(local_file).length() == size) {
                                                entry.local_file_path = local_file;
                                                entry.icon_data = icon_data;
                                            }
                                        }
                                    }
                                }
                            }catch(Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (cursor!=null) {
                                    cursor.close();
                                }
                            }

                            Iterator iter = entries.entrySet().iterator();
                            while (iter.hasNext()) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                KanBoxFileEntry item = (KanBoxFileEntry)entry.getValue();
                                if (TextUtils.isEmpty(item.local_file_path)) {
                                    //try to find one match
                                    item.local_file_path = tryToFindLocalFileMatch(item.path, item.size);
                                }
                            }

                            if(!TextUtils.isEmpty(parent_path)) {
                                int count = FileManager.getAppContext().getContentResolver().delete(DataStructures.CloudBoxColumns.CONTENT_URI, DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD+"=?",
                                        new String[]{parent_path});
                                Log.i(TAG, "delete cloudbox count " + count);
                            }

                            ContentValues[] cvs = new ContentValues[len];
                            iter = entries.entrySet().iterator();
                            i = 0;
                            while (iter.hasNext()) {
                                Map.Entry entry = (Map.Entry) iter.next();
                                Object key = entry.getKey();
                                Object val = entry.getValue();
                                cvs[i] = buildFullContentValueFromKanBoxFileEntry((KanBoxFileEntry)val);
                                i++;
                            }

                            FileManager.getAppContext().getContentResolver().bulkInsert(DataStructures.CloudBoxColumns.CONTENT_URI, cvs);

                            PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                            preference.saveStringValueToSharePreferences(parent_path, hash);

                        } else {
                            if(!TextUtils.isEmpty(parent_path)) {
                                int count = FileManager.getAppContext().getContentResolver().delete(DataStructures.CloudBoxColumns.CONTENT_URI, DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD + "=?", new String[]{parent_path});
                                Log.i(TAG, "delete cloudbox count " + count);
                                PushSharePreference preference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
                                preference.saveStringValueToSharePreferences(parent_path, hash);
                            }

                        }
                    } else if(status.equals("nochange")) {
                        //do nothing
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            task.execute();
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


    }

    public static ContentValues buildFullContentValueFromKanBoxFileEntry(KanBoxFileEntry entry) {
        ContentValues cv = new ContentValues();
        cv.put(DataStructures.CloudBoxColumns.FILE_NAME_FIELD, entry.name);
        cv.put(DataStructures.CloudBoxColumns.FILE_PATH_FIELD, entry.path);
        cv.put(DataStructures.CloudBoxColumns.FILE_DATE_FIELD, entry.lastModified);
        cv.put(DataStructures.CloudBoxColumns.FILE_SIZE_FIELD, entry.size);
        cv.put(DataStructures.CloudBoxColumns.FILE_TYPE_FIELD, entry.type);
        if(entry.is_directory) {
            cv.put(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD, 1);
        } else {
            cv.put(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD, 0);
        }
        if(entry.hash != null) {
            cv.put(DataStructures.CloudBoxColumns.HASH_FIELD, entry.hash);
        } else {
            cv.put(DataStructures.CloudBoxColumns.HASH_FIELD, "");
        }
        if(entry.parent_path != null) {
            cv.put(DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD, entry.parent_path);
        } else {
            cv.put(DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD, "");
        }
        if(entry.local_file_path != null) {
            cv.put(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD, entry.local_file_path);
        } else {
            cv.put(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD, "");
        }
        if(entry.icon_data != null && entry.icon_data.length>10) {
            cv.put(DataStructures.CloudBoxColumns.ICON_DATA_FIELD, entry.icon_data);
        } else {
            cv.put(DataStructures.CloudBoxColumns.ICON_DATA_FIELD, "");
        }
        return cv;
    }

    private static String tryToFindLocalFileMatch(String remote_path, long remote_size) {
        StorageManager manager = StorageManager.getInstance(FileManager.getAppContext());
        String[] storages = manager.getMountedStorages();
        String local_path = null;
        if(storages!=null) {
            int size = storages.length;
            int idx = 0;
            idx = remote_path.lastIndexOf("/");
            String name = remote_path.substring(idx+1);
            String dir = remote_path.substring(0,idx+1);
            idx = 0;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());
            String account_email = preferences.getString("KanBox_Account_Email", "").trim();
            while(idx < size){
                String stor = storages[idx];
                File kanbox_dir = new File(stor, KanBoxConfig.LOCAL_STORAGE_DIRECTORY+File.separator+account_email);
                File kanbox_file = new File(kanbox_dir.getAbsolutePath()+dir, name);
                if(kanbox_file.exists() && kanbox_file.length() == remote_size) {
                    local_path = new File(kanbox_dir.getAbsolutePath()+dir, name).getAbsolutePath();
                    break;
                }
                idx++;
            }
        }
        return local_path;
    }

}
