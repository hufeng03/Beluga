package com.hufeng.filemanager.browser;

import android.text.TextUtils;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.mp3.Mp3ReadId3v2;
import com.hufeng.filemanager.storage.StorageManager;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by feng on 2014-04-02.
 */
public class InfoUtil {

    public static String getFileInfo(String path, int type) {
        String info;
        switch (type) {
            case FileUtils.FILE_TYPE_DIRECTORY: {
                int count = getDirectoryChildrenCount(new File(path));
                if (!StorageManager.getInstance(FileManager.getAppContext()).isStorage(path)) {
                    String date = FileUtils.getFileDate(new File(path));
                    info = "(" + count + ") " + date;
                } else {
                    info = path;
                }
                break;
            }
            case FileUtils.FILE_TYPE_AUDIO: {
                String song = getAudioInfoFromFile(path);
                String size = FileUtils.getFileSize(new File(path));
                String date = FileUtils.getFileDate(new File(path));
                if (TextUtils.isEmpty(song)) {
                    info = size + " " + date;
                } else {
                    info = song + " " + size + " " + date;
                }
                break;
            }
            case FileUtils.FILE_TYPE_APK:
            default: {
                String size = FileUtils.getFileSize(new File(path));
                String date = FileUtils.getFileDate(new File(path));
                info = size + " " + date;
                break;
            }
        }
        return info;
    }

    private static int getDirectoryChildrenCount(File file)
    {
        if(file.isDirectory())
        {
            File[] files = file.listFiles();
            if(files!=null)
                return files.length;
            else
                return 0;
        }
        else
        {
            return 0;
        }
    }

    private static String getAudioInfoFromFile(String path)
    {
        String info = null;
        if("mp3".equalsIgnoreCase(IconLoaderHelper.getExtFromFilename(path)))
        {
            try {

                Mp3ReadId3v2 mp3Id3v2 = new Mp3ReadId3v2(new FileInputStream(path));
                mp3Id3v2.readId3v2(1024 * 100);
                String special = mp3Id3v2.getSpecial();
                String author = mp3Id3v2.getAuthor();
                if(!TextUtils.isEmpty(special) && !TextUtils.isEmpty(author))
                {
                    info = special+" "+author;
                }
                else if(!TextUtils.isEmpty(special))
                {
                    info = special;
                }
                else if(!TextUtils.isEmpty(author))
                {
                    info = author;
                }
                else
                {
                    info = null;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return info;
    }

}
