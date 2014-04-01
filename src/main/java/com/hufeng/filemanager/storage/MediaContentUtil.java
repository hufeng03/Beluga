package com.hufeng.filemanager.storage;

import android.provider.MediaStore;

/**
 * Created by feng on 2014-03-30.
 */
public class MediaContentUtil {

    public static String EXTERNAL_IMAGE_URI;
    public static String EXTERNAL_AUDIO_URI;
    public static String EXTERNAL_VIDEO_URI;
    public static String EXTERNAL_FILE_URI;

    //image
    static {
        String volumeName = "external";
        EXTERNAL_IMAGE_URI = MediaStore.Images.Media.getContentUri(volumeName).toString();
        EXTERNAL_AUDIO_URI = MediaStore.Audio.Media.getContentUri(volumeName).toString();
        EXTERNAL_VIDEO_URI = MediaStore.Video.Media.getContentUri(volumeName).toString();
        EXTERNAL_FILE_URI = MediaStore.Files.getContentUri(volumeName).toString();
    }
}
