package com.hufeng.filemanager.resource;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.data.DataStructures;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by feng on 13-9-24.
 */
public class ResourceListDownloader extends AsyncTask<Void, Void, List<ResourceEntry>> {

    private static final String LOG_TAG = ResourceListDownloader.class.getSimpleName();

    private static final String FETCH_GAME_LIST_LAST_TIME_KEY = "fecth_game_list_last_time";

    private Context mContext;

    private static final String HTTP_URL = "http://hufeng.info/fileapi/selected/";
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 5000;

    private boolean isChina = false;

    public ResourceListDownloader(Context context) {
        mContext = context;
    }

    public interface SelectedListDownloaderListener {
        public void onSelectedListFetched(List<ResourceEntry> games);
    }

    WeakReference<SelectedListDownloaderListener>  wListener;

    public void setGameListDownloaderListener(SelectedListDownloaderListener listener) {
        wListener = new WeakReference<SelectedListDownloaderListener>(listener);
    }

    private ResourceEntry buildGameEntry(JSONObject obj) {
        ResourceEntry entry = new ResourceEntry();
        Log.i(LOG_TAG, "download game entry json:"+obj);
        try {
            entry.resource_description = obj.getString("ds");
            entry.package_name = obj.getString("pk");
            entry.version_code = obj.getInt("vc");
            entry.version_name = obj.getString("vn");
            entry.resource_server_time = obj.getLong("da");
            entry.resource_icon_url = obj.getString("ic");
            entry.download_url = obj.getString("dl");
            entry.resource_category = obj.getInt("ca");
            entry.size = obj.getInt("sz")*1024;
            entry.name = entry.resource_name;
            entry.lastModified = entry.resource_server_time;
            entry.server_version_code = entry.version_code;
            entry.server_version_name = entry.version_name;
            if(isChina) {
                entry.resource_name = obj.getString("cn");
            } else {
                entry.resource_name = obj.getString("en");
            }

            if (entry.package_name != null) {
                try{
                    PackageInfo info = mContext.getPackageManager().getPackageInfo(entry.package_name, PackageManager.GET_UNINSTALLED_PACKAGES);
                    if(info!=null) {
                        entry.installed = true;
                        entry.installed_version_code = info.versionCode;
                        entry.installed_version_name = info.versionName;
                        if(entry.installed_version_code < entry.version_code) {
                            entry.app_upgrade = true;
                        } else {
                            entry.app_upgrade = false;
                        }
                    } else {
                        entry.installed = false;
                    }

                }catch(PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, "download game entry error: " + e.getMessage());
        }
        Log.i(LOG_TAG, "download game entry: " + entry);
        return entry;
    }

    private ContentValues[] buildSelectedContentValue(List<ResourceEntry> entrys) {
        ArrayList<ContentValues> cvs = new ArrayList<ContentValues>(entrys.size());
        ListIterator<ResourceEntry> iterator = entrys.listIterator();
        int idx = 0;
        while(iterator.hasNext()) {
            ResourceEntry entry = iterator.next();
            ContentValues cv = new ContentValues();
            cv.put(DataStructures.SelectedColumns.URL_FIELD, entry.download_url);
            cv.put(DataStructures.SelectedColumns.SERVER_NAME_FIELD, entry.resource_name);
            cv.put(DataStructures.SelectedColumns.DESCRIPTION_FIELD, entry.resource_description);
            cv.put(DataStructures.SelectedColumns.APP_CATEGORY_FIELD, entry.resource_category);
            cv.put(DataStructures.SelectedColumns.PACKAGE_FIELD, entry.package_name);
            cv.put(DataStructures.SelectedColumns.VERSION_FIELD, entry.version_code);
            cv.put(DataStructures.SelectedColumns.VERSION_NAME_FIELD, entry.version_name);
            cv.put(DataStructures.SelectedColumns.ICON_FIELD, entry.resource_icon_url);
            cv.put(DataStructures.SelectedColumns.SERVER_DATE_FIELD, entry.resource_server_time);
            cv.put(DataStructures.FileColumns.FILE_NAME_FIELD, entry.name);
            cv.put(DataStructures.FileColumns.FILE_DATE_FIELD, entry.lastModified);
            cv.put(DataStructures.FileColumns.FILE_SIZE_FIELD, entry.size);
            cvs.add(cv);
            idx++;
        }
        return cvs.toArray(new ContentValues[cvs.size()]);
    }

    @Override
    protected List<ResourceEntry> doInBackground(Void... voids) {
        Log.i(LOG_TAG, "doInBackground");
        long time = Long.parseLong(FileManager.getPreference(FETCH_GAME_LIST_LAST_TIME_KEY, "0"));
        if(Math.abs(System.currentTimeMillis()-time)<24*60*60*1000) {
            return null;
        }

        isChina = Locale.getDefault().getLanguage().contains("zh");
        HttpURLConnection ucon = null;
        InputStream ins;
        BufferedReader reader = null;
        try {
            URL http_url = new URL(HTTP_URL);
            ucon =  (HttpURLConnection)http_url.openConnection();
            ucon.setConnectTimeout(CONNECTION_TIMEOUT);
            ucon.setReadTimeout(SOCKET_TIMEOUT);
            if (isChina) {
                ucon.setRequestProperty("Accept-Language","zh-cn");
            }
            ucon.setRequestProperty("type","all");
            ucon.setRequestProperty("channel", Constants.PRODUCT_FLAVOR_NAME);
            TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            if (imei == null) imei = "";
            ucon.setRequestProperty("device", Build.VERSION.SDK_INT+"_"+imei);
            ins = ucon.getInputStream();
            reader = new BufferedReader(new InputStreamReader(ins));

            String line;
            StringBuffer buffer = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }


            try {
                JSONObject obj = new JSONObject(buffer.toString());
                if(obj.getString("status").equals("OK"))
                {
                    JSONArray rst = obj.getJSONArray("result");
                    Log.i(LOG_TAG, "result of gamelistdownloader is "+rst);
                    if (rst != null) {
                        int count = rst.length();
                        if (count>0) {
                            int idx = 0;
                            List<ResourceEntry> games = new ArrayList<ResourceEntry>(count);
                            while(idx<count) {
                                JSONObject json = rst.getJSONObject(idx);
                                ResourceEntry entry = buildGameEntry(json);
                                if(!entry.isInstalled() || entry.needAppUpgrade()) {
                                    games.add(entry);
//                                    Cursor cursor;
//                                    cursor = mContext.getContentResolver().query(DataStructures.SelectedColumns.CONTENT_URI,
//                                            DataStructures.SelectedColumns.SELECTED_PROJECTION,
//                                            DataStructures.SelectedColumns.URL_FIELD + "=? AND "+DataStructures.SelectedColumns.VERSION_FIELD_INDEX,
//                                            new String[]{entry.download_url}, null);
//                                    if(cursor!=null && cursor.moveToNext()) {
//                                        String path = cursor.getString(DataStructures.SelectedColumns.FILE_PATH_FIELD_INDEX);
//                                        if(TextUtils.isEmpty(path)) {
//                                            xxxx
//                                        }
//                                    }
                                }
                                idx++;
                            }
                            Collections.sort(games, FileSorter.getComparator(FileSorter.SORT_FIELD.DATE, FileSorter.SORT_ORDER.DESC));
                            mContext.getContentResolver().delete(DataStructures.SelectedColumns.CONTENT_URI, null, null);
                            ContentValues[] cvs = buildSelectedContentValue(games);
                            int insertNum = mContext.getContentResolver().bulkInsert(DataStructures.SelectedColumns.CONTENT_URI, cvs);
                            Log.i(LOG_TAG, "insert selected games: "+insertNum);
                            return games;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOG_TAG, e.getMessage());
        } finally {
            if(reader!=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(ucon!=null) {
                ucon.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<ResourceEntry> entries) {
        super.onPostExecute(entries);
        if(entries!=null && entries.size()>0) {
            FileManager.setPreference(FETCH_GAME_LIST_LAST_TIME_KEY, ""+System.currentTimeMillis());
        }
        if(wListener!=null) {
            SelectedListDownloaderListener listener = wListener.get();
            if(listener!=null) {
                listener.onSelectedListFetched(entries);
            }
        }
    }
}
