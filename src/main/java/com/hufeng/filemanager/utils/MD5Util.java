package com.hufeng.filemanager.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by feng on 2/24/2014.
 */
public class MD5Util {
    private static final String TAG = MD5Util.class.getSimpleName();

    public static String getMD5HexForFile( String filename )
    {
        final int BUFFER_SIZE = 8192;
        byte[] buf = new byte[BUFFER_SIZE];
        int length;
        try {
            FileInputStream fis = new FileInputStream( filename );
            BufferedInputStream bis = new BufferedInputStream(fis);
            MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            while( (length = bis.read(buf)) != -1 ) {
                md.update(buf, 0, length);
            }
            bis.close();

            byte[] array = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            Log.i(TAG, "md5sum: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.i(TAG, "" + e);
        }
        return "md5bad";
    }

    public static String getMD5HexForString( String str )
    {
        try {
            MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.digest(str.getBytes());
            byte[] array = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            Log.i(TAG, "md5sum: " + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            Log.i(TAG, "" + e);
        }
        return "md5bad";
    }

}
