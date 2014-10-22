package com.hufeng.filemanager.resource;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by feng on 13-9-29.
 */
public class ResourceIconDownloader extends AsyncTask<Void, Void, byte[]> {

    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 5000;
    private static final int MAX_RECONNECTION_COUNT = 2;

    private ResourceIconLoader mLoader = null;

    private String mUrl = null;

    public ResourceIconDownloader(ResourceIconLoader loader, String path) {
        mLoader = loader;
        mUrl = path;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected byte[] doInBackground(Void... params) {
        return download_icon(mUrl, 1);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        mLoader.iconDownloaded(mUrl, bytes);
    }

    private byte[] download_icon(String image_url, int count) {
        ByteArrayOutputStream bos = null;

        try {
            URL url = new URL(image_url);

            /* Open a connection to that URL. */

            HttpURLConnection ucon = null;
            try{
                ucon = (HttpURLConnection)url.openConnection();
            }catch(IOException e){
                return null;
            }

            ucon.setConnectTimeout(CONNECTION_TIMEOUT);
            ucon.setReadTimeout(SOCKET_TIMEOUT);
            /*
             * Define InputStreams to read from the URLConnection.
             */
            InputStream is = ucon.getInputStream();

            Bitmap bm = BitmapFactory.decodeStream(is);

            byte[] data = null;
            if(bm!=null) {
                bos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                data = bos.toByteArray();
            }
            return data;

//            bis = new BufferedInputStream(ucon.getInputStream());
//
//            /*
//             * Read bytes to the Buffer until there is nothing more to read(-1).
//             */
//            byteos = new ByteArrayOutputStream();
//
//            byte[] buf = new byte[1024];
//            int len = 0;
//            while ((len = bis.read(buf)) > 0) {
//                byteos.write(buf, 0, len);
//            }
//            ucon.disconnect();
//            bis.close();
//            byte[]content=byteos.toByteArray();
//            byteos.close();
//            /* Convert the Bytes read to a String. */
//            File dir = new File(path);
//            if(!dir.exists()){
//                dir.mkdirs();
//            }
//            File file = new File(path,filename);
//            fos = new FileOutputStream(file);
//            fos.write(content);
        } catch (IOException e) {
            if (count < MAX_RECONNECTION_COUNT) {
                if(bos!=null)
                    try{
                        bos.close();
                    }catch(Exception e1){

                    }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                count++;
                return download_icon(image_url, count);
            }else{
               return null;
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (count < MAX_RECONNECTION_COUNT) {
                if(bos!=null)
                    try{
                        bos.close();
                    }catch(Exception e1){

                    }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                count++;
                return download_icon(image_url, count);
            }else{
                return null;
            }
        } finally{
            if(bos!=null)
                try{
                    bos.close();
                }catch(Exception e){

                }
        }
    }

}
