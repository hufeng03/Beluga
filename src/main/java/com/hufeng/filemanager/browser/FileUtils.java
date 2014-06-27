package com.hufeng.filemanager.browser;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.provider.DataStructures.MatchColumns;
import com.hufeng.filemanager.storage.MediaContentUtil;
import com.hufeng.filemanager.utils.FileUtil;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.MimeUtils;
import com.hufeng.filemanager.utils.TimeUtil;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static final int FILE_TYPE_ALL = -1;
    public static final int FILE_TYPE_FILE = 0;
    public static final int FILE_TYPE_AUDIO = 1;
    public static final int FILE_TYPE_VIDEO = 2;
    public static final int FILE_TYPE_IMAGE = 3;
    public static final int FILE_TYPE_APK = 4;
    public static final int FILE_TYPE_DOCUMENT = 5;
    public static final int FILE_TYPE_MEDIA = 6;
    public static final int FILE_TYPE_ZIP = 7;
    public static final int FILE_TYPE_FAVORITE = 101;
    public static final int FILE_TYPE_DOWNLOAD = 102;
    public static final int FILE_TYPE_APP = 103;
    public static final int FILE_TYPE_DIRECTORY = 104;
    public static final int FILE_TYPE_RESOURCE_GAME = 105;
    public static final int FILE_TYPE_RESOURCE_APP = 106;
    public static final int FILE_TYPE_RESOURCE_DOC = 107;
    public static final int FILE_TYPE_RESOURCE_ALL = 108;
    public static final int FILE_TYPE_CLOUD = 150;

    public static String getFileSize(File path) {
        if (path == null)
            return "";
        String ret = "";
        File f = path;
        long v = f.length();
        ret = normalize(v);
        return ret;
    }

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;

    private static String normalize(long data) {
        return FileUtil.normalize(data);
    }

    public static int getFileType(String path) {
        if(sFileMap==null)
        {
            initFileMap();
            buildFileMap();
        }

        int dotIndex = path.lastIndexOf(".");
        if (dotIndex < 0)
            return FILE_TYPE_FILE;

        String end = path.substring(dotIndex, path.length()).toLowerCase();
        if (TextUtils.isEmpty(end))
            return FILE_TYPE_FILE;

        Integer t = sFileMap.get(end);
        if (t != null)
            return t;
        return FILE_TYPE_FILE;
    }

    public static int getFileType(File file) {
    	if(file.isDirectory())
    	{
    		return FILE_TYPE_DIRECTORY;
    	}
    	
    	if(sFileMap==null)
    	{
        	initFileMap();
    		buildFileMap();
    	}
    	
        String name = file.getName();
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex < 0)
            return FILE_TYPE_FILE;

        String end = name.substring(dotIndex, name.length()).toLowerCase();
        if (TextUtils.isEmpty(end))
            return FILE_TYPE_FILE;

        Integer t = sFileMap.get(end);
        if (t != null)
            return t;
        return FILE_TYPE_FILE;
    }
    
    /* 判断文件MimeType的method */
    public static String getMIMEType(File f) 
    { 
        String filePath=f.getName();

        int dotPosition = filePath.lastIndexOf('.');
        if (dotPosition == -1)
        	return "*/*";

        String ext = filePath.substring(dotPosition + 1, filePath.length()).toLowerCase();
        
        if("gz".equals(ext) && filePath.endsWith(".tar.gz")){
        	ext = "tar.gz";
        }
        
        String mimeType = MimeUtils.guessMimeTypeFromExtension(ext);
//            if (ext.equals("mtz")) {
//                mimeType = "application/miui-mtz";
//            }

        return mimeType != null ? mimeType : "*/*";
    }
    
    public static String getFileDate(File file) {
    	long date = file.lastModified();
    	return TimeUtil.getDateString(date);
    }

    private static HashMap<String, Integer> sFileMap = null;
    
    public static void initFileMap(){
		String first_open = FileManager.getPreference("filemanager_first_open", "1");
		
		if("1".equals(first_open))
		{
	        FileManager.setCategoryMatch(".3gp", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".asf", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".avi", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".m4u", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".m4v", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".mov", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".mp4", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".mpe", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".mpeg", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".mpg", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".mpg4", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".rmvb", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".rm", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".wmv", FileUtils.FILE_TYPE_VIDEO);
	        FileManager.setCategoryMatch(".xv", FileUtils.FILE_TYPE_VIDEO);
            FileManager.setCategoryMatch(".flv", FileUtils.FILE_TYPE_VIDEO);

	        FileManager.setCategoryMatch(".m3u", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".m4a", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".m4b", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".m4p", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".mp2", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".mp3", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".mpga", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".amr", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".aac", FileUtils.FILE_TYPE_AUDIO);       
	        FileManager.setCategoryMatch(".ogg", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".wav", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".wma", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".ape", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".flac", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".imy", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".mmf", FileUtils.FILE_TYPE_AUDIO);
	        FileManager.setCategoryMatch(".3gpp", FileUtils.FILE_TYPE_AUDIO);
            FileManager.setCategoryMatch(".awb", FileUtils.FILE_TYPE_AUDIO);
            FileManager.setCategoryMatch(".mid", FileUtils.FILE_TYPE_AUDIO);

	        FileManager.setCategoryMatch(".apk", FileUtils.FILE_TYPE_APK);

	        FileManager.setCategoryMatch(".bmp", FileUtils.FILE_TYPE_IMAGE);
	        FileManager.setCategoryMatch(".gif", FileUtils.FILE_TYPE_IMAGE);
	        FileManager.setCategoryMatch(".jpeg", FileUtils.FILE_TYPE_IMAGE);
	        FileManager.setCategoryMatch(".jpg", FileUtils.FILE_TYPE_IMAGE);
	        FileManager.setCategoryMatch(".png", FileUtils.FILE_TYPE_IMAGE);
	        FileManager.setCategoryMatch(".tif", FileUtils.FILE_TYPE_IMAGE);
	        FileManager.setCategoryMatch(".mpo", FileUtils.FILE_TYPE_IMAGE);
            FileManager.setCategoryMatch(".wbmp", FileUtils.FILE_TYPE_IMAGE);
            FileManager.setCategoryMatch(".pcx", FileUtils.FILE_TYPE_IMAGE);
            FileManager.setCategoryMatch(".tga", FileUtils.FILE_TYPE_IMAGE);
            FileManager.setCategoryMatch(".wmf", FileUtils.FILE_TYPE_IMAGE);
	        
	        FileManager.setCategoryMatch(".txt", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".epub", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".chm", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".pdf", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".ps", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".doc", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".ppt", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".xls", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".docx", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".pptx", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".xlsx", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".html", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".htm", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".xhtml", FileUtils.FILE_TYPE_DOCUMENT);
	        FileManager.setCategoryMatch(".xml", FileUtils.FILE_TYPE_DOCUMENT);
	        
	        
	        FileManager.setCategoryMatch(".zip", FileUtils.FILE_TYPE_ZIP);
	        FileManager.setCategoryMatch(".rar", FileUtils.FILE_TYPE_ZIP);
	        FileManager.setCategoryMatch(".tar", FileUtils.FILE_TYPE_ZIP);
	        FileManager.setCategoryMatch(".z", FileUtils.FILE_TYPE_ZIP);
	        FileManager.setCategoryMatch(".7z", FileUtils.FILE_TYPE_ZIP);
	        FileManager.setCategoryMatch(".gz", FileUtils.FILE_TYPE_ZIP);
	        
			FileManager.setPreference(FileManager.FILEMANAGER_FIRST_OPEN, "0");
		}
    }
    
    public static void buildFileMap()
    {
    	if(sFileMap!=null)
    		sFileMap.clear();
		sFileMap = new HashMap<String, Integer>();
		
    	Cursor cursor = FileManager.getAppContext().getContentResolver()
    			.query(MatchColumns.CONTENT_URI, new String[] {
           MatchColumns.EXTENSION_FIELD, MatchColumns.CATEGORY_FIELD},null, null, null);
    	    	
    	if(cursor!=null)
    	{
        	if(LogUtil.IDBG) LogUtil.i(TAG, "build File Map with database record number is "+cursor.getCount());
        	if(cursor.getCount()==0)
        	{
        		LogUtil.e(TAG, "build File Map error because not match pair in database");
        	}
    		while(cursor.moveToNext())
    		{
    			String extension = cursor.getString(0);
    			int category = cursor.getInt(1);
    			if(LogUtil.IDBG) LogUtil.i(TAG, "build File Map with "+extension+" "+category);
    			sFileMap.put(extension, category);
    		}
    		cursor.close();
    	} 	
    	else
    	{
    		LogUtil.e(TAG, "build File Map error because not match pair in database");
    	}
    }

    public static String getUninstallApkLabel(Context context, String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        String PATH_AssetManager = "android.content.res.AssetManager";
        try {
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);
            LogUtil.d("ANDROID_LAB", "pkgParser:" + pkgParser.toString());
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                    "parsePackage", typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = 0;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                    valueArgs);
            Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "applicationInfo");
            ApplicationInfo info = (ApplicationInfo) appInfoFld
                    .get(pkgParserPkg);
            Class assetMagCls = Class.forName(PATH_AssetManager);
            Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
            Object assetMag = assetMagCt.newInstance((Object[]) null);
            typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
                    "addAssetPath", typeArgs);
            valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
            Resources res = context.getResources();// getResources();
            typeArgs = new Class[3];
            typeArgs[0] = assetMag.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor resCt = Resources.class.getConstructor(typeArgs);
            valueArgs = new Object[3];
            valueArgs[0] = assetMag;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            res = (Resources) resCt.newInstance(valueArgs);
            CharSequence label = null;
            if (info.labelRes != 0) {
                label = res.getText(info.labelRes);
            }
            LogUtil.d("ANDROID_LAB", "label=" + label);
            if (!TextUtils.isEmpty(label)) {
                PackageManager pm = context.getPackageManager();
                PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath,
                        PackageManager.GET_ACTIVITIES);
                if (packageInfo != null) {
                    return label.toString()+('['+packageInfo.versionName+']');
//                packageInfo.versionCode;// 版本码
                } else {
                    return label.toString();
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getUninstallAPKIcon(Context context, String apkPath) {
        String PATH_PackageParser = "android.content.pm.PackageParser";
        String PATH_AssetManager = "android.content.res.AssetManager";
        try {
            Class pkgParserCls = Class.forName(PATH_PackageParser);
            Class[] typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            Object pkgParser = pkgParserCt.newInstance(valueArgs);
            LogUtil.d("ANDROID_LAB", "pkgParser:" + pkgParser.toString());
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            typeArgs = new Class[4];
            typeArgs[0] = File.class;
            typeArgs[1] = String.class;
            typeArgs[2] = DisplayMetrics.class;
            typeArgs[3] = Integer.TYPE;
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
                    "parsePackage", typeArgs);
            valueArgs = new Object[4];
            valueArgs[0] = new File(apkPath);
            valueArgs[1] = apkPath;
            valueArgs[2] = metrics;
            valueArgs[3] = 0;
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
                    valueArgs);
            Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "applicationInfo");
            ApplicationInfo info = (ApplicationInfo) appInfoFld
                    .get(pkgParserPkg);
            Class assetMagCls = Class.forName(PATH_AssetManager);
            Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
            Object assetMag = assetMagCt.newInstance((Object[]) null);
            typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
                    "addAssetPath", typeArgs);
            valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
            Resources res = context.getResources();// getResources();
            typeArgs = new Class[3];
            typeArgs[0] = assetMag.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor resCt = Resources.class.getConstructor(typeArgs);
            valueArgs = new Object[3];
            valueArgs[0] = assetMag;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            res = (Resources) resCt.newInstance(valueArgs);
            CharSequence label = null;
            if (info.labelRes != 0) {
                label = res.getText(info.labelRes);
            }
            LogUtil.d("ANDROID_LAB", "label=" + label);
            if (info.icon != 0) {
                Bitmap icon = BitmapFactory.decodeResource(res, info.icon);
                return icon;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }


    public static Uri getPathFromMediaContent(Context context, Uri contentUri) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        Uri rstUri = contentUri;
        String uri_str = contentUri.toString();
        try{
            if (uri_str.startsWith(MediaContentUtil.EXTERNAL_IMAGE_URI)) {
                cursor = resolver.query(contentUri,
                        new String[] { MediaStore.Images.ImageColumns.DATA },
                        null, null, null);
                cursor.moveToFirst();
                rstUri = Uri.fromFile(new File(cursor.getString(0)));
            } else if (uri_str.startsWith(MediaContentUtil.EXTERNAL_VIDEO_URI)) {
                cursor = resolver.query(contentUri,
                        new String[] { MediaStore.Video.VideoColumns.DATA },
                        null, null, null);
                cursor.moveToFirst();
                rstUri = Uri.fromFile(new File(cursor.getString(0)));
            }	else if (uri_str.startsWith(MediaContentUtil.EXTERNAL_AUDIO_URI)) {
                cursor = resolver.query(contentUri,
                        new String[] { MediaStore.Audio.AudioColumns.DATA },
                        null, null, null);
                cursor.moveToFirst();
                rstUri = Uri.fromFile(new File(cursor.getString(0)));
            }   else if (uri_str.startsWith(MediaContentUtil.EXTERNAL_FILE_URI)) {
                cursor = resolver.query(contentUri,
                        new String[] { MediaStore.Files.FileColumns.DATA },
                        null, null, null);
                cursor.moveToFirst();
                rstUri = Uri.fromFile(new File(cursor.getString(0)));
            }
            else
            {
            	rstUri = contentUri;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return rstUri;
    }

    public static String getFilename(String path) {
        File file = new File(path);
        if (file.exists()) {
            String dir = file.getParent();
            String name = file.getName();
            int i = 1;
            int idx = name.lastIndexOf('.');
            if (idx>0) {
                String real_name = name.substring(0,idx);
                String extension = name.substring(idx);
                while(!file.exists()) {
                    file = new File(dir, real_name+"("+(i++)+")"+extension);
                }
            } else {
                while(!file.exists()) {
                    file = new File(dir, name+"("+(i++)+")");
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static boolean isDirWritable(String path) {
        boolean can_write = new File(path).canWrite();
        if (can_write && Constants.TRY_TO_TEST_WRITE) {
            if(new File(path, ".test_writable").mkdir()){
                new File(path, ".test_writable").delete();
            } else {
                can_write = false;
            }
        }
        return can_write;
    }
    
}
