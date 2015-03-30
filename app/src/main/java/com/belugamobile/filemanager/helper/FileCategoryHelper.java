package com.belugamobile.filemanager.helper;

import android.mtp.MtpConstants;
import android.text.TextUtils;

import com.belugamobile.filemanager.utils.MimeUtil;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Feng Hu on 15-02-24.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileCategoryHelper {

    public static final int CATEGORY_TYPE_UNKNOW = 0;
    public static final int CATEGORY_TYPE_AUDIO = 1;
    public static final int CATEGORY_TYPE_VIDEO = 2;
    public static final int CATEGORY_TYPE_IMAGE = 3;
    public static final int CATEGORY_TYPE_APK = 4;
    public static final int CATEGORY_TYPE_DOCUMENT = 5;
    public static final int CATEGORY_TYPE_ZIP = 6;

    // This is not file type, but for convenient, we define as category
    public static final int CATEGORY_TYPE_APP = 100;
    public static final int CATEGORY_TYPE_FAVORITE = 101;
    public static final int CATEGORY_TYPE_DOWNLOAD = 102;

    // Audio file types
    public static final int FILE_TYPE_MP3     = 1;
    public static final int FILE_TYPE_M4A     = 2;
    public static final int FILE_TYPE_WAV     = 3;
    public static final int FILE_TYPE_AMR     = 4;
    public static final int FILE_TYPE_AWB     = 5;
    public static final int FILE_TYPE_WMA     = 6;
    public static final int FILE_TYPE_OGG     = 7;
    public static final int FILE_TYPE_AAC     = 8;
    public static final int FILE_TYPE_MKA     = 9;
    public static final int FILE_TYPE_FLAC    = 10;
    public static final int FILE_TYPE_MID     = 11;
    public static final int FILE_TYPE_SMF     = 12;
    public static final int FILE_TYPE_IMY     = 13;


    // Video file types
    public static final int FILE_TYPE_MP4     = 101;
    public static final int FILE_TYPE_M4V     = 102;
    public static final int FILE_TYPE_3GPP    = 103;
    public static final int FILE_TYPE_3GPP2   = 104;
    public static final int FILE_TYPE_WMV     = 105;
    public static final int FILE_TYPE_ASF     = 106;
    public static final int FILE_TYPE_MKV     = 107;
    public static final int FILE_TYPE_MP2TS   = 108;
    public static final int FILE_TYPE_AVI     = 109;
    public static final int FILE_TYPE_WEBM    = 110;
    public static final int FILE_TYPE_MP2PS   = 111;

    // Image file types
    public static final int FILE_TYPE_JPEG    = 201;
    public static final int FILE_TYPE_GIF     = 202;
    public static final int FILE_TYPE_PNG     = 203;
    public static final int FILE_TYPE_BMP     = 204;
    public static final int FILE_TYPE_WBMP    = 205;
    public static final int FILE_TYPE_WEBP    = 206;


    // Other popular file types
    public static final int FILE_TYPE_TEXT          = 300;
    public static final int FILE_TYPE_HTML          = 301;
    public static final int FILE_TYPE_PDF           = 302;
    public static final int FILE_TYPE_XML           = 303;
    public static final int FILE_TYPE_MS_WORD       = 304;
    public static final int FILE_TYPE_MS_EXCEL      = 305;
    public static final int FILE_TYPE_MS_POWERPOINT = 306;


    public static final int FILE_TYPE_ZIP           = 401;


    public static final int FILE_TYPE_APK           = 108;

    private static final HashMap<String, Integer> sExtensionToFileTypeMap
            = new HashMap<String, Integer>();

    private static final HashMap<String, Integer> sExtensionToCategoryTypeMap
            = new HashMap<String, Integer>();

    static {
        addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg", CATEGORY_TYPE_AUDIO);
        addFileType("MPGA", FILE_TYPE_MP3, "audio/mpeg", CATEGORY_TYPE_AUDIO);
        addFileType("M4A", FILE_TYPE_M4A, "audio/mp4", CATEGORY_TYPE_AUDIO);
        addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav", CATEGORY_TYPE_AUDIO);
        addFileType("AMR", FILE_TYPE_AMR, "audio/amr", CATEGORY_TYPE_AUDIO);
        addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb", CATEGORY_TYPE_AUDIO);
        addFileType("WMA", FILE_TYPE_WMA, "audio/x-ms-wma", CATEGORY_TYPE_AUDIO);
        addFileType("OGG", FILE_TYPE_OGG, "audio/ogg", CATEGORY_TYPE_AUDIO);
        addFileType("OGG", FILE_TYPE_OGG, "application/ogg", CATEGORY_TYPE_AUDIO);
        addFileType("OGA", FILE_TYPE_OGG, "application/ogg", CATEGORY_TYPE_AUDIO);
        addFileType("AAC", FILE_TYPE_AAC, "audio/aac", CATEGORY_TYPE_AUDIO);
        addFileType("AAC", FILE_TYPE_AAC, "audio/aac-adts", CATEGORY_TYPE_AUDIO);
        addFileType("MKA", FILE_TYPE_MKA, "audio/x-matroska", CATEGORY_TYPE_AUDIO);
        addFileType("FLAC", FILE_TYPE_FLAC, "audio/flac", CATEGORY_TYPE_AUDIO);
        addFileType("MID", FILE_TYPE_MID, "audio/midi", CATEGORY_TYPE_AUDIO);
        addFileType("MIDI", FILE_TYPE_MID, "audio/midi", CATEGORY_TYPE_AUDIO);
        addFileType("XMF", FILE_TYPE_MID, "audio/midi", CATEGORY_TYPE_AUDIO);
        addFileType("RTTTL", FILE_TYPE_MID, "audio/midi", CATEGORY_TYPE_AUDIO);
        addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi", CATEGORY_TYPE_AUDIO);
        addFileType("IMY", FILE_TYPE_IMY, "audio/imelody", CATEGORY_TYPE_AUDIO);
        addFileType("RTX", FILE_TYPE_MID, "audio/midi", CATEGORY_TYPE_AUDIO);
        addFileType("OTA", FILE_TYPE_MID, "audio/midi", CATEGORY_TYPE_AUDIO);
        addFileType("MXMF", FILE_TYPE_MID, "audio/midi", CATEGORY_TYPE_AUDIO);

        addFileType("MPEG", FILE_TYPE_MP4, "video/mpeg", CATEGORY_TYPE_VIDEO);
        addFileType("MPG", FILE_TYPE_MP4, "video/mpeg", CATEGORY_TYPE_VIDEO);
        addFileType("MP4", FILE_TYPE_MP4, "video/mp4", CATEGORY_TYPE_VIDEO);
        addFileType("M4V", FILE_TYPE_M4V, "video/mp4", CATEGORY_TYPE_VIDEO);
        addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp",  CATEGORY_TYPE_VIDEO);
        addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp", CATEGORY_TYPE_VIDEO);
        addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2", CATEGORY_TYPE_VIDEO);
        addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2", CATEGORY_TYPE_VIDEO);
        addFileType("MKV", FILE_TYPE_MKV, "video/x-matroska", CATEGORY_TYPE_VIDEO);
        addFileType("WEBM", FILE_TYPE_WEBM, "video/webm", CATEGORY_TYPE_VIDEO);
        addFileType("TS", FILE_TYPE_MP2TS, "video/mp2ts", CATEGORY_TYPE_VIDEO);
        addFileType("AVI", FILE_TYPE_AVI, "video/avi", CATEGORY_TYPE_VIDEO);
        addFileType("WMV", FILE_TYPE_WMV, "video/x-ms-wmv", CATEGORY_TYPE_VIDEO);
        addFileType("ASF", FILE_TYPE_ASF, "video/x-ms-asf", CATEGORY_TYPE_VIDEO);
        addFileType("MPG", FILE_TYPE_MP2PS, "video/mp2p", CATEGORY_TYPE_VIDEO);
        addFileType("MPEG", FILE_TYPE_MP2PS, "video/mp2p", CATEGORY_TYPE_VIDEO);

        addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg", CATEGORY_TYPE_IMAGE);
        addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg", CATEGORY_TYPE_IMAGE);
        addFileType("GIF", FILE_TYPE_GIF, "image/gif", CATEGORY_TYPE_IMAGE);
        addFileType("PNG", FILE_TYPE_PNG, "image/png", CATEGORY_TYPE_IMAGE);
        addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp", CATEGORY_TYPE_IMAGE);
        addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp", CATEGORY_TYPE_IMAGE);
        addFileType("WEBP", FILE_TYPE_WEBP, "image/webp", CATEGORY_TYPE_IMAGE);


        addFileType("TXT", FILE_TYPE_TEXT, "text/plain", CATEGORY_TYPE_DOCUMENT);
        addFileType("HTM", FILE_TYPE_HTML, "text/html", CATEGORY_TYPE_DOCUMENT);
        addFileType("HTML", FILE_TYPE_HTML, "text/html", CATEGORY_TYPE_DOCUMENT);
        addFileType("PDF", FILE_TYPE_PDF, "application/pdf", CATEGORY_TYPE_DOCUMENT);
        addFileType("DOC", FILE_TYPE_MS_WORD, "application/msword", CATEGORY_TYPE_DOCUMENT);
        addFileType("XLS", FILE_TYPE_MS_EXCEL, "application/vnd.ms-excel", CATEGORY_TYPE_DOCUMENT);
        addFileType("PPT", FILE_TYPE_MS_POWERPOINT, "application/mspowerpoint", CATEGORY_TYPE_DOCUMENT);

        addFileType("ZIP", FILE_TYPE_ZIP, "application/zip", CATEGORY_TYPE_ZIP);

        addFileType("APK", FILE_TYPE_APK, "application/vnd.android.package-archive", CATEGORY_TYPE_APK);
    }


    static void addFileType(String extension, int fileType, String mimeType, int categoryType) {
        sExtensionToFileTypeMap.put(extension, Integer.valueOf(fileType));
        sExtensionToCategoryTypeMap.put(extension, Integer.valueOf(categoryType));
    }

    public static int getFileTypeForExtension(String extension) {
        if (TextUtils.isEmpty(extension)) {
            return 0;
        } else {
            Integer val = sExtensionToFileTypeMap.get(extension.toUpperCase());
            if (val == null)
                return 0;
            return val.intValue();
        }
    }

    public static int getFileCategoryForPath(String path) {
        String extension = MimeUtil.getExtension(path);
        return getFileCategoryForExtension(extension);
    }

    public static int getFileCategoryForExtension(String extension) {
        if (TextUtils.isEmpty(extension)) {
            return 0;
        } else {
            Integer val = sExtensionToCategoryTypeMap.get(extension.toUpperCase());
            if (val == null) {
                String mimeType = MimeUtil.getMimeTypeByExtension(extension);
                if (!TextUtils.isEmpty(mimeType)) {
                    if (mimeType.startsWith("image/")) {
                        return CATEGORY_TYPE_IMAGE;
                    } else if (mimeType.startsWith("audio/")) {
                        return CATEGORY_TYPE_AUDIO;
                    } else if (mimeType.startsWith("video/")) {
                        return CATEGORY_TYPE_VIDEO;
                    } else if (mimeType.startsWith("text/")) {
                        return CATEGORY_TYPE_DOCUMENT;
                    }
                }
                return 0;
            }
            return val.intValue();
        }
    }

}
