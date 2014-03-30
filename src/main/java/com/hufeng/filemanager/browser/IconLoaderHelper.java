package com.hufeng.filemanager.browser;

import android.content.Context;
import android.text.TextUtils;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.storage.StorageManager;

import java.io.File;
import java.util.HashMap;

public class IconLoaderHelper {
	
    private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();
    private static HashMap<String, Integer> fileExtToIconSquares = new HashMap<String, Integer>();
	
    static {
        addItem(new String[] {
            "mp3"
        }, R.drawable.file_icon_mp3, R.drawable.file_icon_mp3_square);
        addItem(new String[] {
            "wma"
        }, R.drawable.file_icon_wma, R.drawable.file_icon_wma_square);
        addItem(new String[] {
            "wav"
        }, R.drawable.file_icon_wav, R.drawable.file_icon_wav_square);
        addItem(new String[] {
            "mid"
        }, R.drawable.file_icon_mid, R.drawable.file_icon_mid_square);
        addItem(new String[] {
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3g2", "3gpp2", "asf",
        }, R.drawable.file_icon_video, R.drawable.file_icon_video_square);
        addItem(new String[] {
                "jpg", "jpeg", "gif", "png", "bmp", "wbmp"
        }, R.drawable.file_icon_picture, R.drawable.file_icon_picture_square);
        addItem(new String[] {
                "txt", "log", "xml", "ini", "lrc"
        }, R.drawable.file_icon_txt, R.drawable.file_icon_txt_square);
        addItem(new String[] {
                "doc", "ppt", "docx", "pptx", "xsl", "xslx",
        }, R.drawable.file_icon_office, R.drawable.file_icon_office_square);
        addItem(new String[] {
            "pdf"
        }, R.drawable.file_icon_pdf, R.drawable.file_icon_pdf_square);
        addItem(new String[] {
            "zip"
        }, R.drawable.file_icon_zip, R.drawable.file_icon_zip_square);
        addItem(new String[] {
            "rar"
        }, R.drawable.file_icon_rar, R.drawable.file_icon_rar_square);
        addItem(new String[] {
                "apk"
            }, R.drawable.file_icon_apk, R.drawable.file_icon_apk_square);
    }
    
    private static void addItem(String[] exts, int resId, int resSquareId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
                fileExtToIconSquares.put(ext.toLowerCase(), resSquareId);
            }
        }
    }
    
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    public static int getFileIcon(int type) {
            int id = R.drawable.file_icon_default;
            switch(type)
            {
                case FileUtils.FILE_TYPE_APK:
                    id = R.drawable.file_icon_apk;
                    break;
                case FileUtils.FILE_TYPE_IMAGE:
                    id = R.drawable.file_icon_picture;
                    break;
                case FileUtils.FILE_TYPE_VIDEO:
                    id = R.drawable.file_icon_video;
                    break;
                case FileUtils.FILE_TYPE_AUDIO:
                    id = R.drawable.file_icon_music;
                    break;
                default:
                    id= R.drawable.file_icon_default;
            }
            return id;
    }

    public static int getFileIcon(Context context, String path) {
        return getFileIcon(context, path, false);
    }

    public static int getFileIcon(Context context, String path, boolean with_square) {
        if (path.endsWith("/") || new File(path).isDirectory()) {
            if (path.equals("/")) {
                return with_square?R.drawable.file_icon_phone_square:R.drawable.phone;
            } else if (StorageManager.getInstance(context).isStorage(path)) {
                if (StorageManager.getInstance(context).isExternalStorage(path)) {
                    return with_square?R.drawable.file_icon_sdcard_square:R.drawable.sdcard;
                } else if (StorageManager.getInstance(context).isInternalStorage(path)) {
                    return with_square?R.drawable.file_icon_phone_square:R.drawable.phone;
                } else {
                    return with_square?R.drawable.file_icon_sdcard_square:R.drawable.sdcard;
                }
            } else {
                return with_square?R.drawable.file_icon_folder_square:R.drawable.file_icon_folder;
            }
        } else {
            String ext = getExtFromFilename(path);
            if(TextUtils.isEmpty(ext))
            {
                return with_square?R.drawable.file_icon_default_square:R.drawable.file_icon_default;
            }
            Integer i = with_square?fileExtToIconSquares.get(ext.toLowerCase()):fileExtToIcons.get(ext.toLowerCase());
            if (i != null) {
                return i.intValue();
            } else {
                int type = FileUtils.getFileType(new File(path));
                int id;
                switch(type)
                {
                case FileUtils.FILE_TYPE_IMAGE:
                    id = with_square?R.drawable.file_icon_picture_square:R.drawable.file_icon_picture;
                    break;
                case FileUtils.FILE_TYPE_VIDEO:
                    id = with_square?R.drawable.file_icon_video_square:R.drawable.file_icon_video;
                    break;
                case FileUtils.FILE_TYPE_AUDIO:
                    id = with_square?R.drawable.file_icon_music_square:R.drawable.file_icon_music;
                    break;
                default:
                    id= with_square?R.drawable.file_icon_default_square:R.drawable.file_icon_default;
                }
                return id;
            }
        }
    }

}
