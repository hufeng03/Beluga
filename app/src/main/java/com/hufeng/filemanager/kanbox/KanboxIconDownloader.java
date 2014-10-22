package com.hufeng.filemanager.kanbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.hufeng.filemanager.utils.LogUtil;
import com.kanbox.api.Kanbox;
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

/**
 * Created by feng on 13-12-5.
 */
public class KanboxIconDownloader extends AsyncTask<Void, Void, byte[]> {

    private static final String TAG = KanboxIconDownloader.class.getSimpleName();

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 5000;
    private static final int MAX_RECONNECTION_COUNT = 2;

    private KanboxIconLoader mLoader = null;

    private String mUrl = null;

    public KanboxIconDownloader(KanboxIconLoader loader, String path) {
        mLoader = loader;
        mUrl = path;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected byte[] doInBackground(Void... params) {
        return download_icon(mUrl);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        mLoader.iconDownloaded(mUrl, bytes);
    }

    private byte[] download_icon(String path) {

        byte[] data = null;
        StringBuffer thumbnailUrl = new StringBuffer( "https://api.kanbox.com/0/thumbnail");
        String encoded_path = Kanbox.encodePath(path);
        thumbnailUrl.append(encoded_path).append("?size=small");

        HttpGet httpRequest = new HttpGet(thumbnailUrl.toString());
        Token token = Token.getInstance();
        if(token != null) {
            httpRequest.setHeader("Authorization", "Bearer " + token.getAccessToken());
        }


        HttpClient sHttpClient = KanboxAsyncTask.createHttpClient();
        ByteArrayOutputStream bos = null;
        LogUtil.i(TAG, "get thumbnail from " + thumbnailUrl + " for " + path);
        try {
            HttpResponse sHttpResponse = sHttpClient.execute(httpRequest);
            int statusCode = sHttpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = sHttpResponse.getEntity();
                long length = entity.getContentLength();
                LogUtil.i(TAG, "return thumbnail length "+length);
                InputStream is = entity.getContent();
                Bitmap bm = BitmapFactory.decodeStream(is);

                if(bm!=null) {
                    LogUtil.i(TAG, "decode as bitmap");
                    bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    data = bos.toByteArray();
                } else {
                    LogUtil.i(TAG, "cannot decode as bitmap");
                }
            } else {
                LogUtil.i(TAG, "getthumbnail return statuscode="+statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            LogUtil.i(TAG, "getthumbnail return error");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.i(TAG, "getthumbnail return error");
        } finally {
            if(bos!=null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;

    }

}
