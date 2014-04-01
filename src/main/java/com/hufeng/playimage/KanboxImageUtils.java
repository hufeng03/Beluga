package com.hufeng.playimage;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.provider.DataStructures;
import com.kanbox.api.KanboxAsyncTask;
import com.kanbox.api.Token;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by feng on 14-2-3.
 */
public class KanboxImageUtils {

    private static final String[] COLUMNS = new String[] { DataStructures.CloudBoxColumns.FILE_PATH_FIELD,
            DataStructures.CloudBoxColumns.ICON_DATA_FIELD};

    private static final String TAG = KanboxImageUtils.class.getSimpleName();

    public static Bitmap loadThumbnailFromDb(String uri) {
        Bitmap bm = null;
        Cursor cursor = null;
        try {
            cursor = FileManager.getAppContext().getContentResolver()
                    .query(DataStructures.CloudBoxColumns.CONTENT_URI,
                            COLUMNS,
                            DataStructures.CloudBoxColumns.FILE_PATH_FIELD + "=?",
                            new String[]{uri}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    byte[] bytes = cursor.getBlob(1);
                    if (bytes!=null  && bytes.length>10) {
                        Log.i(TAG, "load from db success " + path + " " + bytes.length + " ");
                        try {
                            bm = BitmapFactory.decodeByteArray(bytes, 0,
                                    bytes.length, null);
                        } catch (OutOfMemoryError e) {

                        } catch (Exception e) {

                        }
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bm;
    }

    public static int saveThumbnailIntoDb(String uri, Bitmap bm) {
        byte[] data = null;
        ByteArrayOutputStream bos = null;
        Log.i(TAG, "decode as bitmap");
        bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
        data = bos.toByteArray();
        ContentValues values = new ContentValues();
        values.put(DataStructures.CloudBoxColumns.ICON_DATA_FIELD, data);
        int result = FileManager.getAppContext().getContentResolver().update(DataStructures.CloudBoxColumns.CONTENT_URI, values,
                DataStructures.CloudBoxColumns.FILE_PATH_FIELD + "=?", new String[]{uri});
        return result;
    }

    public static Bitmap downloadThumbnailFromServer(String path) {
        Bitmap bm = null;

        String encoded_path = null;
        try {
            encoded_path = URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encoded_path = path;
            e.printStackTrace();
        }
        StringBuffer thumbnailUrl = new StringBuffer( "https://api.kanbox.com/0/thumbnail");
        thumbnailUrl.append(encoded_path).append("?size=small");

        HttpGet httpRequest = new HttpGet(thumbnailUrl.toString());
        Token token = Token.getInstance();
        if(token != null) {
            httpRequest.setHeader("Authorization", "Bearer " + token.getAcceccToken());
        }


        HttpClient sHttpClient = KanboxAsyncTask.createHttpClient();
        Log.i(TAG, "get thumbnail from " + thumbnailUrl + " for " + path);
        try {
            HttpResponse sHttpResponse = sHttpClient.execute(httpRequest);
            int statusCode = sHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = sHttpResponse.getEntity();
                long length = entity.getContentLength();
                Log.i(TAG, "return thumbnail length "+length);
                InputStream is = entity.getContent();
                bm = BitmapFactory.decodeStream(is);


            } else {
                Log.i(TAG, "getthumbnail return statuscode="+statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.i(TAG, "getthumbnail return error");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "getthumbnail return error");
        } finally {

        }
        return bm;
    }

}
