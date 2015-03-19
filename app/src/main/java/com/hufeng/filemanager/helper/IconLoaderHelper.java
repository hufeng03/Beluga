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
                "txt", "log", "xml", "ini", "lrc"
        }, R.drawable.ic_type_txt);
        addItem(new String[] {
                "xml",
        }, R.drawable.ic_type_xml);
        addItem(new String[] {
                "html", "htm"
        }, R.drawable.ic_type_htm);
        addItem(new String[] {
                "doc", "docx",
        }, R.drawable.ic_type_doc);
        addItem(new String[] {
                "ppt", "pptx",
        }, R.drawable.ic_type_ppt);
        addItem(new String[] {
                "xsl", "xslx",
        }, R.drawable.ic_type_xsl);
        addItem(new String[] {
            "pdf"
        }, R.drawable.ic_type_pdf);
        addItem(new String[] {
            "zip"
        }, R.drawable.ic_type_zip);
        addItem(new String[] {
            "rar"
        }, R.drawable.ic_type_rar);
        addItem(new String[] {
                "tar"
        }, R.drawable.ic_type_tar);
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
                return R.drawable.ic_sd_storage;
            } else if (MountPointManager.getInstance().isExternalMountPath(path)) {
                return R.drawable.ic_sd_storage;
            } else if (MountPointManager.getInstance().isInternalMountPath(path)) {
                return R.drawable.ic_sd_storage;
            }
            else {
                return R.drawable.ic_sd_storage;
            }
        } else {
            String ext = MimeUtil.getExtension(path);
            if(TextUtils.isEmpty(ext))
            {
                return R.drawable.ic_type_unknown;
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
                    id = R.drawable.ic_type_photo;
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                    id = R.drawable.ic_type_video;
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                    id = R.drawable.ic_type_audio;
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                    id = R.drawable.ic_type_zip_unknown;
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                    id = R.drawable.ic_type_doc_unknown;
                    break;
                case FileCategoryHelper.CATEGORY_TYPE_APK:
                    id = R.drawable.ic_type_apk;
                    break;
                default:
                    id= R.drawable.ic_type_unknown;
                }
                return id;
            }
        }
    }

}
