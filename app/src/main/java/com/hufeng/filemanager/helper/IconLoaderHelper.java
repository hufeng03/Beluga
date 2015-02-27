package com.hufeng.filemanager.helper;

import android.content.Context;
import android.text.TextUtils;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.mount.MountPointManager;
import com.hufeng.filemanager.utils.MimeUtil;

import java.io.File;
import java.util.HashMap;

public class IconLoaderHelper {
	
    private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();
	
    static {
        addItem(new String[] {
            "mp3"
        }, R.drawable.file_icon_mp3);
        addItem(new String[] {
            "wma"
        }, R.drawable.file_icon_wma);
        addItem(new String[] {
            "wav"
        }, R.drawable.file_icon_wav);
        addItem(new String[] {
            "mid"
        }, R.drawable.file_icon_mid);
        addItem(new String[] {
                "mp4", "wmv", "mpeg", "m4v", "3gp", "3g2", "3gpp2", "asf",
        }, R.drawable.file_icon_video);
        addItem(new String[] {
                "jpg", "jpeg", "gif", "png", "bmp", "wbmp"
        }, R.drawable.file_icon_picture);
        addItem(new String[] {
                "txt", "log", "xml", "ini", "lrc"
        }, R.drawable.file_icon_txt);
        addItem(new String[] {
                "doc", "ppt", "docx", "pptx", "xsl", "xslx",
        }, R.drawable.file_icon_office);
        addItem(new String[] {
            "pdf"
        }, R.drawable.file_icon_pdf);
        addItem(new String[] {
            "zip"
        }, R.drawable.file_icon_zip);
        addItem(new String[] {
            "rar"
        }, R.drawable.file_icon_rar);
        addItem(new String[] {
                "apk"
            }, R.drawable.file_icon_apk);
    }
    
    private static void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }


    public static int getFileIcon(Context context, String path) {
        if (path.endsWith("/") || new File(path).isDirectory()) {
            if (path.equals("/")) {
                return R.drawable.ic_action_phone_android;
            } else if (MountPointManager.getInstance().isExternalMountPath(path)) {
                return R.drawable.ic_action_sd_card;
            } else if (MountPointManager.getInstance().isInternalMountPath(path)) {
                return R.drawable.ic_action_sd_card;
            }
            else {
                return R.drawable.ic_action_phone_android;
            }
        } else {
            String ext = MimeUtil.getExtension(path);
            if(TextUtils.isEmpty(ext))
            {
                return R.drawable.file_icon_default;
            }
            Integer i = fileExtToIcons.get(ext.toLowerCase());
            if (i != null) {
                return i.intValue();
            } else {
                int type = FileCategoryHelper.getFileCategoryForFile(path);
                int id;
                switch(type)
                {
                case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                    id = R.drawable.file_category_icon_image;
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                    id = R.drawable.file_category_icon_video;
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                    id = R.drawable.file_category_icon_audio;
                    break;
                default:
                    id= R.drawable.file_icon_default;
                }
                return id;
            }
        }
    }

}
