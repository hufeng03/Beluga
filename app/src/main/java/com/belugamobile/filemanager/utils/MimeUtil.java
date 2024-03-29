package com.belugamobile.filemanager.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

/**
 * Created by Feng Hu on 15-02-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class MimeUtil {

   public static String getExtension(String url) {
       String extension = MimeTypeMap.getFileExtensionFromUrl(url);
       // MimeTypeMap.getFileExtensionFromUrl can not handle filename with special characters
       // We handle this case by ourshelve
       if (TextUtils.isEmpty(extension)) {
           String processedUrl = url;
           int filenamePos = processedUrl.lastIndexOf("/");
           if (0 < filenamePos) {
               processedUrl = processedUrl.substring(filenamePos + 1);
           }
           int dotPos = processedUrl.lastIndexOf(".");
           if (0 < dotPos) {
               extension = processedUrl.substring(dotPos + 1);
           }
       }
       return extension;
   }

   public static String getMimeType(String url) {
       String extension = getExtension(url);
       String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
       return mimeType;
   }

    public static String getMimeTypeByExtension(String extension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

}
