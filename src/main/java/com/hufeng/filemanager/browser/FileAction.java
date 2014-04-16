package com.hufeng.filemanager.browser;

import android.app.WallpaperManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.FileManagerTabActivity;
import com.hufeng.filemanager.IntentData;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.SettingsScanActivity;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.provider.DataStructures.FavoriteColumns;
import com.hufeng.filemanager.provider.DataStructures.FileColumns;
import com.hufeng.filemanager.scan.ApkObject;
import com.hufeng.filemanager.scan.AudioObject;
import com.hufeng.filemanager.scan.DocumentObject;
import com.hufeng.filemanager.scan.FileObject;
import com.hufeng.filemanager.scan.ImageObject;
import com.hufeng.filemanager.scan.VideoObject;
import com.hufeng.filemanager.scan.ZipObject;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.FileUtil;
import com.hufeng.filemanager.utils.ImageUtil;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.PackageUtil;
import com.hufeng.filemanager.utils.TimeUtil;
import com.hufeng.safebox.CryptUtil;
import com.hufeng.safebox.SafeBoxConfig;
import com.hufeng.safebox.SafeDataStructs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileAction {
	
	private static final String LOG_TAG = "FileAction";

//    public static boolean delete(String dir, ArrayList<String> paths)
//    {
//    	boolean result = true;
//    	for(String path:paths)
//    	{
//    		if(!delete(dir,path))
//    			result = false;
//    	}
//    	return result;
//    }

//    public static boolean add_to_cloud(String[] paths, String root, Boolean cancel) {
//        boolean result = true;
//        for(String path:paths)
//        {
//            if(cancel)
//                return false;
//            if(!add_to_cloud(path, root, cancel))
//            {
//                result = false;
//            }
//        }
//
//        return result;
//    }

//    public static boolean add_to_cloud(String path, String root, Boolean cancel) {
//        boolean result;
//        result = KanBoxApi.getInstance().uploadFileSynchronized(path, root, Token.getInstance());
//        return result;
//    }

    public static boolean add_to_safe(String[] paths, Boolean cancel) {

        boolean result = true;
        for(String path:paths)
        {
            if(cancel)
                return false;
            if(!add_to_safe(path, cancel))
            {
                result = false;
            }
        }

        return result;
    }
    
    public static boolean delete(String[] paths, Boolean cancel)
    {
    	boolean result = true;
    	for(String path:paths)
    	{
    		if(cancel)
    			return false;
    		if(!delete(path, cancel))
    		{
    			result = false;
    		}
    	}
    	return result;
    }
    
//    private static boolean delete(String dir, String path)
//    {
//    		File delFile = new File(dir+File.separator+path);
//    		if(delFile.isDirectory())
//    			return deleteDir(delFile);
//    		else
//    		{
//    			boolean result = true;
//    			if(!delFile.delete())
//    				result = false;
//    			else
//    				deleteFileInDatabase(delFile);
//    			return result;
//    		}
//    }

    public static void addToSafeBox(String path) {
        if(new File(path).exists())
            moveToSafeBox(new File(path));
    }

    private static String moveToSafeBox(File nfile){
        boolean result = true;
        String device0 = StorageManager.getInstance(FileManager.getAppContext()).getStorageForPath(nfile.getAbsolutePath());
        String dir = device0 + File.separator + SafeBoxConfig.STORAGE_DIR;
        if(!new File(dir).exists()) {
            new File(dir).mkdirs();
        }
        String name = nfile.getName();
        String encoded_name = Base64.encodeToString(name.getBytes(), Base64.DEFAULT);
        encoded_name = encoded_name.trim();
        File encoded_file = new File(dir, encoded_name);
        int i = 1;
        while(encoded_file.exists()) {
            encoded_file = new File(dir, encoded_name+"("+(i++)+")");
        }

        copyFileToSafeInDatabase(nfile, encoded_file);

        nfile.renameTo(encoded_file);

        try {
            CryptUtil.encryptOneFile(encoded_file.getAbsolutePath());
            deleteFileInDatabase(nfile);
            result = true;
        }catch (Exception e) {
            result = false;
        }
        if(result) {
            return encoded_file.getAbsolutePath();
        } else {
            return null;
        }
    }

    public static String getTempSafeBoxFile(final String path){
        Cursor cursor = FileManager.getAppContext().getContentResolver().query(SafeDataStructs.SafeColumns.CONTENT_URI, SafeDataStructs.SafeColumns.SAFE_PROJECTION, SafeDataStructs.SafeColumns.SAFE_PATH+"=?", new String[]{path}, null);
        String temp_path = path;
        if (cursor!=null && cursor.moveToFirst()) {
            String original_path = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGINAL_PATH);
            String original_extension = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGIANL_EXTENSION);
            if (TextUtils.isEmpty(original_extension)) {
                int idx = original_path.lastIndexOf(".");
                if(idx>0){
                    original_extension = original_path.substring(idx+1);
                }
            }
            temp_path = path+"."+original_extension;
            new File(path).renameTo(new File(temp_path));
        }
        return temp_path;
    }

    private static boolean addToSafeBox_Dir(File safeDir, Boolean cancel){
        if(!safeDir.exists())
            return true;
        File[] f=safeDir.listFiles();//取得文件夹里面的路径
        if(f==null || f.length==0){
            return safeDir.delete();
        }
        else {
            boolean result = true;
            for(File nFile:f){
                if(cancel)
                    return false;
                if(nFile.isDirectory()){
                    if(!addToSafeBox_Dir(nFile, cancel))
                        result = false;
                }else {
                    String new_path = moveToSafeBox(nFile);
                    if(new_path == null) {
                        result = false;
                    } else {

                    }
                }
            }
            if(result) {
                result = safeDir.delete();
            }
            return result;
        }
    }

    private static boolean add_to_safe(String path, Boolean cancel) {
        Log.i(LOG_TAG, "add_to_safe " + (path == null ? "null" : path));
        File safeFile = new File(path);
        if(safeFile.isDirectory())
            return addToSafeBox_Dir(safeFile, cancel);
        else
        {
            boolean result = true;
            String new_path = null;
            if(safeFile.exists()){
                new_path  = moveToSafeBox(safeFile);
                if (new_path == null) {
                    result = false;
                }
            }
            return result;
        }
    }

    public static void scanSinglePath(String path) {
        if (!new File(path).exists()) {
            if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete single file: "+path);
            delete(path);
        } else {
            if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "add single file: "+path);
            add(path);
        }
    }

    public static boolean delete(String path) {
        Boolean cancel = false;
        if(delete(path, cancel)){
            return true;
        } else {
            return false;
        }
    }

    public static boolean add(String path) {
        Boolean cancel = false;
        if (add(path, cancel)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean add(String path, Boolean cancel)
    {
        if(TextUtils.isEmpty(path)) {
            return true;
        }
        File addFile = new File(path);
        if (!addFile.exists()) {
            return false;
        }
        if(addFile.isDirectory())
            return addDir(addFile, cancel);
        else
        {
            boolean result = true;
            addFileInDatabase(addFile);
            return result;
        }
    }

    private static boolean addDir(File addFile, Boolean cancel) {
        if(!addFile.exists())
            return true;
        File[] f=addFile.listFiles();//取得文件夹里面的路径
        if(f==null || f.length==0){
            return true;
        }
        else {
            boolean result = true;
            for(File nFile:f){
                if(cancel)
                    return false;
                if(nFile.isDirectory()){
                    if(!addDir(nFile, cancel))
                        result = false;
                }else {
                    addFileInDatabase(nFile);
                }
            }
            return result;
        }
    }

    private static boolean delete(String path, Boolean cancel)
    {
        if(TextUtils.isEmpty(path)) {
            return true;
        }
        File delFile = new File(path);
    	if(delFile.isDirectory())
    		return deleteDir(delFile, cancel);
    	else
        {
    		boolean result = true;
    		if(delFile.exists() && !delFile.delete()) {
    			result = false;
            } else {
    			deleteFileInDatabase(delFile);
    		}
    		return result;
    	}
    }
    
    private static boolean deleteDir(File delFile, Boolean cancel){
    	if(!delFile.exists())
    		return true;
        File[] f=delFile.listFiles();//取得文件夹里面的路径
        if(f==null || f.length==0){
        	return delFile.delete();
        }
        else {
        	boolean result = true;
                for(File nFile:f){
                		if(cancel)
                			return false;
                        if(nFile.isDirectory()){
                                if(!deleteDir(nFile, cancel)) 
                                	result = false;
                        }else {
                                if(!nFile.delete())
                                	result = false;
                                else
                                	deleteFileInDatabase(nFile);
                        }
                }
                result = delFile.delete();
                return result;
        }
    }
    
  //文件复制
    public static boolean copy(String[] paths, String target, Boolean cancel)
    {
    //	File targetDir = new File(target);
    	boolean result = true;
    	for(String path:paths)
    	{
    		File file = new File(path);
    		String name = path;
    		int idx = path.lastIndexOf("/");
    		if(idx>0)
    			name = path.substring(idx+1);
    		File targetFile = new File(target+File.separator+name);
    		
//    		if(file.getPath().equals(targetFile.getPath()))
//    		{
//    			continue;
//    		}
    		
    		int i = 1;
    		int pos = name.lastIndexOf(".");
    		while(targetFile.exists())
    		{
    			if(pos>0)
    			{
    				targetFile = new File(target+File.separator+name.substring(0,pos)+"("+i+")"+name.substring(pos));
    			}
    			else
    			{
    				targetFile = new File(target+File.separator+name+"("+i+")");
    			}
    			i++;
    		}
    		if(cancel)
    		{
    			return false;
    		}
    		if(file.isDirectory())
    		{
    			if(!copyDir(file, targetFile, cancel))
    				result = false;
    		}
    		else
    		{
    			if(!copyFile(file,targetFile))
    				result = false;
    			else
    				addFileInDatabase(targetFile);
    		}
    	}
    	return result;
    }
    
//    private static String getDeviceFromPath(String target)
//    {
//		int idx2 = target.indexOf("/mnt/");
//		String device = null;
//		if(idx2==0)
//		{
//			if(target.length()>5)
//			{
//    			device = target.substring(5);
//    			int idx3 = device.indexOf("/");
//    			if(idx3!=-1)
//    			{
//    				device = device.substring(0,idx3);
//    			}
//			}
//		}
//		else
//		{
//			if(target.length()>1)
//			{
//				device = target.substring(1);
//				int idx3 = device.indexOf("/");
//    			if(idx3!=-1)
//    			{
//    				device = device.substring(0,idx3);
//    			}
//			}
//		}
//		return device;
//    }
    //文件复制
    public static boolean move(String[] paths, String target, Boolean cancel)
    {
    //	File targetDir = new File(target);
    	boolean result = true;
    	for(String path:paths)
    	{
    		File file = new File(path);
    		String name = path;
    		int idx = path.lastIndexOf("/");
    		if(idx>0)
    			name = path.substring(idx+1);
    		File targetFile = new File(target+File.separator+name);
    		boolean flag_copy_delete = false;
    		if(file.getPath().equals(targetFile.getPath()))
    		{
    			continue;
    		}
            StorageManager stor = StorageManager.getInstance(FileManager.getAppContext());
    		String device0 = stor.getStorageForPath(path);
    		String device = stor.getStorageForPath(target);
    		if(!TextUtils.isEmpty(device) && !TextUtils.isEmpty(device0) && !device0.equals(device))
    		{
    			//not in same sdcard
    			flag_copy_delete = true;
    		}
    		
    		String targetFile_path = FileUtils.getFilename(new File(target, name).getAbsolutePath());
            targetFile = new File(targetFile_path);
//    		int i = 1;
//    		int pos = name.lastIndexOf(".");
//    		while(targetFile.exists())
//    		{
//    			if(pos>0)
//    			{
//    				targetFile = new File(target+File.separator+name.substring(0,pos)+"("+i+")"+name.substring(pos));
//    			}
//    			else
//    			{
//    				targetFile = new File(target+File.separator+name+"("+i+")");
//    			}
//    			i++;
//    		}
    		if(cancel)
    		{
    			return false;
    		}
    		if(file.isDirectory())
    		{
    			if(!flag_copy_delete)
    			{
	    			if(!file.renameTo(targetFile))
	    				result = false;
	    			else
	    				moveDirInDatabase(file, targetFile);
    			}
    			else
    			{
    				if(!moveDirByCopyAndDelete(file,targetFile,cancel))
    					result = false;
    			}
    		}
    		else
    		{
    			if(!flag_copy_delete)
    			{
	    			if(!file.renameTo(targetFile))
	    				result = false;
	    			else
	    				updateFileInDatabase(file, targetFile);
    			}
    			else
    			{
    				if(!copyFile(file,targetFile))
    					result = false;
    				else
    				{
    					file.delete();
    					updateFileInDatabase(file,targetFile);
    				}
    			}
    		}
    	}
    	return result;
    }
    
    //文件夹复制，包括文件夹里面的文件复制
    private static boolean moveDirByCopyAndDelete(File file, File plasPath, Boolean cancel){
            plasPath.mkdir();
            File[] f=file.listFiles();
            boolean result = true;
            if(f!=null)
            {
	            for(File newFile:f){
	            		if(cancel)
	            		{
	            			return false;
	            		}
	                    if(newFile.isDirectory()){
	                            File files=new File(file.getPath()+"/"+newFile.getName()) ;
	                            File plasPaths=new File(plasPath.getPath()+"/"+newFile.getName());
	                            if(!copyDir(files, plasPaths, cancel))
	                            	result = false;
	                    }else {
	                        String newPath=plasPath.getPath()+"/"+newFile.getName();
	                        File newPlasFile=new File(newPath);
	                        
	                        if(!copyFile(newFile,newPlasFile))
	        					result = false;
	        				else
	        				{
	        					newFile.delete();
	        					updateFileInDatabase(newFile,newPlasFile);
	        				}
	                    }
	            }  
	            file.delete();
            }
            return result;
    }
    
    private static boolean copyFile(File file, File plasPath){
            try {
                    FileInputStream fileInput = new FileInputStream(file);
                    BufferedInputStream inBuff=new BufferedInputStream(fileInput);
                    FileOutputStream fileOutput=new FileOutputStream(plasPath);
                    BufferedOutputStream outBuff=new BufferedOutputStream(fileOutput);
                    byte[] b=new byte[1025*5];
                    int len;
                    while((len=inBuff.read(b))!=-1){
                            outBuff.write(b, 0, len);
                    }
                    outBuff.flush();
                    inBuff.close();
                    outBuff.close();
                    fileOutput.close();
                    fileInput.close();
                    return true;
	    } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            return false;
	     } 
    }
    
    //文件夹复制，包括文件夹里面的文件复制
    private static boolean copyDir(File file, File plasPath, Boolean cancel){
            plasPath.mkdir();
            File[] f=file.listFiles();
            boolean result = true;
            if(f!=null)
            {
	            for(File newFile:f){
	            		if(cancel)
	            		{
	            			return false;
	            		}
	                    if(newFile.isDirectory()){
	                            File files=new File(file.getPath()+"/"+newFile.getName()) ;
	                            File plasPaths=new File(plasPath.getPath()+"/"+newFile.getName());
	                            if(!copyDir(files, plasPaths, cancel))
	                            	result = false;
	                    }else {
	                        String newPath=plasPath.getPath()+"/"+newFile.getName();
	                        File newPlasFile=new File(newPath);
	                        if(copyFile(newFile, newPlasFile))
	                        	addFileInDatabase(newPlasFile);
	                        else
	                        	result = false;
	                    }
	            }   
            }
            return result;
    }
    
    public static long getIdInMediaDb(String path) {
    	int cate = FileUtils.getFileType(new File(path));
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileUtils.FILE_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_AUDIO)
        {
        	uri = Audio.Media.getContentUri(volumeName);
        }
        else
        {
        	return -1;
        }
        
        String selection = android.provider.MediaStore.Files.FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[] {
            path
        };

        String[] columns = new String[] {
        		android.provider.MediaStore.Files.FileColumns._ID, android.provider.MediaStore.Files.FileColumns.DATA
        };


        Cursor c = FileManager.getAppContext().getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return 0;
        }
        long id = 0;
        if (c.moveToNext()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }
    
    public static void deleteFileInMediaDb(String path) {
    	int cate = FileUtils.getFileType(new File(path));
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileUtils.FILE_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_AUDIO)
        {
        	uri = Audio.Media.getContentUri(volumeName);
        }
        else
        {
        	return;
        }
        
        String selection = android.provider.MediaStore.Files.FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[] {
            path
        };

//        String[] columns = new String[] {
//                FileColumns._ID, FileColumns.DATA
//        };

        int count = FileManager.getAppContext().getContentResolver().delete(uri, selection, selectionArgs);

    }
    
    private static void deleteFileInDatabase(File file)
    {
    	if(file.isDirectory())
    		return;
    	int category = FileUtils.getFileType(file);
    	Uri uri = null;
    	switch(category)
    	{
    	case FileUtils.FILE_TYPE_APK:
    		uri = DataStructures.ApkColumns.CONTENT_URI;
    		break;
    	case FileUtils.FILE_TYPE_AUDIO:
    		uri = DataStructures.AudioColumns.CONTENT_URI;
    		break;
    	case FileUtils.FILE_TYPE_DOCUMENT:
    		uri = DataStructures.DocumentColumns.CONTENT_URI;
    		break;
    	case FileUtils.FILE_TYPE_IMAGE:
    		uri = DataStructures.ImageColumns.CONTENT_URI;
    		break;
    	case FileUtils.FILE_TYPE_VIDEO:
    		uri = DataStructures.VideoColumns.CONTENT_URI;
    		break;
    	case FileUtils.FILE_TYPE_ZIP:
    		uri = DataStructures.ZipColumns.CONTENT_URI;
    		break;
    	case FileUtils.FILE_TYPE_FILE:
    	default:
    		uri = DataStructures.FileColumns.CONTENT_URI;
    		break;
    	}
    	if(uri!=null)
    	{
    		String path = file.getPath();
            int favorite_count = FileManager.getAppContext().getContentResolver().delete(FavoriteColumns.CONTENT_URI, DataStructures.FileColumns.FILE_PATH_FIELD+"=?", new String[]{path});
            int count = FileManager.getAppContext().getContentResolver().delete(uri, DataStructures.FileColumns.FILE_PATH_FIELD+"=?", new String[]{path});
    		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete "+path+" return "+count+","+favorite_count);
    	}
    	
    	deleteFileInMediaDb(file.getAbsolutePath());
    }
    
    private static void addFileInDatabase(File file)
    {
    	if(file.isDirectory())
    		return;
    	int category = FileUtils.getFileType(file);

        if (category == FileUtils.FILE_TYPE_IMAGE) {
            long size = file.length();
            if (size < 30720) {
                String filter = FileManager.getPreference(SettingsScanActivity.IMAGE_FILTER_SMALL, "1");
                if (filter.equals("1")) {
                    if (LogUtil.IDBG) {
                        Log.i(LOG_TAG, "image filter small "+size+" of "+file.getAbsolutePath());
                    }
                    return;
                }
            }
        } else if (category == FileUtils.FILE_TYPE_AUDIO) {
            long size = file.length();
            if (file.length() < 102400) {
                String filter = FileManager.getPreference(SettingsScanActivity.AUDIO_FILTER_SMALL, "1");
                if (filter.equals("1")) {
                    if (LogUtil.IDBG) {
                        Log.i(LOG_TAG, "audio filter small "+size+" of "+file.getAbsolutePath());
                    }
                    return;
                }
            }
        }

    	Uri uri = null;
    	String path = file.getPath();
    	ContentValues values = new ContentValues();
    	switch(category)
    	{
    	case FileUtils.FILE_TYPE_APK:
    		uri = DataStructures.ApkColumns.CONTENT_URI;
    		new ApkObject(path).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_AUDIO:
    		uri = DataStructures.AudioColumns.CONTENT_URI;
    		new AudioObject(path).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_DOCUMENT:
    		uri = DataStructures.DocumentColumns.CONTENT_URI;
    		new DocumentObject(path).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_IMAGE:
    		uri = DataStructures.ImageColumns.CONTENT_URI;
    		new ImageObject(path).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_VIDEO:
    		uri = DataStructures.VideoColumns.CONTENT_URI;
    		new VideoObject(path).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_ZIP:
    		uri = DataStructures.ZipColumns.CONTENT_URI;
    		new ZipObject(path).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_FILE:
    	default:
    		uri = DataStructures.FileColumns.CONTENT_URI;
    		new FileObject(path).toContentValues(values);
    		break;
    	}
    	if(uri!=null)
    	{
    		Uri new_uri = null;
            try {
                new_uri = FileManager.getAppContext().getContentResolver().insert(uri, values);
            }catch (Exception e) {
                e.printStackTrace();
            }
    		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "insert "+path+" return "+new_uri);
    	}

//        MediaScannerConnection.scanFile(FileManager.getAppContext(),
//                new String[] { path }, null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                    }
//                });

    }
    
    private static void moveDirInDatabase(File olddir, File newdir)
    {
    	File[] files = olddir.listFiles();
    	if(files!=null)
    	{
	    	for(File file:files)
	    	{
	    		if(file.isDirectory())
	    		{
	    			moveDirInDatabase(file, new File(newdir.getPath(),file.getName()));
	    		}
	    		else
	    		{
	    			updateFileInDatabase(file, new File(newdir.getPath(),file.getName()));
	    		}
	    	}
    	}
    	
    }

    public static void copyFileToSafeInDatabase(File oldFile, File newFile) {

        int category = FileUtils.getFileType(oldFile);
//        FileEntry old_entry = new FileEntry(oldFile);
//        Log.i(LOG_TAG, "save new entry: "+new_entry);
        ContentValues cv = new ContentValues();
        Uri uri = null;
        switch(category)
        {
            case FileUtils.FILE_TYPE_APK:
                uri = DataStructures.ApkColumns.CONTENT_URI;
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                uri = DataStructures.AudioColumns.CONTENT_URI;
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                uri = DataStructures.DocumentColumns.CONTENT_URI;
                break;
            case FileUtils.FILE_TYPE_IMAGE:
                uri = DataStructures.ImageColumns.CONTENT_URI;
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                uri = DataStructures.VideoColumns.CONTENT_URI;
                break;
            case FileUtils.FILE_TYPE_ZIP:
                uri = DataStructures.ZipColumns.CONTENT_URI;
                break;
            case FileUtils.FILE_TYPE_FILE:
            default:
                uri = DataStructures.FileColumns.CONTENT_URI;
                break;
        }
        if(uri != null) {
            Cursor cursor = FileManager.getAppContext().getContentResolver().query(uri, FileColumns.FILE_PROJECTION, FileColumns.FILE_PATH_FIELD+"=?", new String[]{oldFile.getAbsolutePath()}, null);
            if (cursor!=null && cursor.moveToFirst()) {
                cv.put(SafeDataStructs.SafeColumns.ORIGINAL_PATH, cursor.getString(FileColumns.FILE_PATH_FIELD_INDEX));
                cv.put(SafeDataStructs.SafeColumns.ORIGINAL_DATE, cursor.getLong(FileColumns.FILE_DATE_FIELD_INDEX));
                cv.put(SafeDataStructs.SafeColumns.ORIGINAL_SIZE, cursor.getLong(FileColumns.FILE_SIZE_FIELD_INDEX));
                cv.put(SafeDataStructs.SafeColumns.ORIGINAL_EXTENSION, cursor.getString(FileColumns.FILE_EXTENSION_FIELD_INDEX));
            }
        }
        cv.put(SafeDataStructs.SafeColumns.CATEGORY, category);
        cv.put(SafeDataStructs.SafeColumns.SAFE_PATH, newFile.getAbsolutePath());
        cv.put(SafeDataStructs.SafeColumns.SAFE_ADD_DATE, System.currentTimeMillis());
        Bitmap bitmap = null;
        if(category == FileUtils.FILE_TYPE_IMAGE) {
            bitmap = IconUtil.getImageThumbnail(oldFile.getAbsolutePath());
        } else if(category == FileUtils.FILE_TYPE_AUDIO) {
            bitmap = IconUtil.getAudioThumbnail(oldFile.getAbsolutePath());
        } else if(category == FileUtils.FILE_TYPE_VIDEO) {
            bitmap = IconUtil.getVideoThumbnail(oldFile.getAbsolutePath());
        }

        if (bitmap != null) {
            byte[] data = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            data = bos.toByteArray();
            cv.put(SafeDataStructs.SafeColumns.THUMBNAIL, data);
        }
        try {
            FileManager.getAppContext().getContentResolver().insert(SafeDataStructs.SafeColumns.CONTENT_URI, cv);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static int updateInDb(String old_path, String new_path, int cate){
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileUtils.FILE_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_AUDIO)
        {
        	uri = Audio.Media.getContentUri(volumeName);
        }
        else
        {
        	
        }
        
        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[] {
            old_path
        };

//        String[] columns = new String[] {
//        		MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA
//        };

        ContentValues cvs = new ContentValues();
        cvs.put(MediaStore.Files.FileColumns.DATA, new_path);
        
        int count = 0;
        try {
            count = FileManager.getAppContext().getContentResolver()
                    .update(uri, cvs, selection, selectionArgs);
        }catch (Exception e) {
            e.printStackTrace();
        }
 
        return count;
    }
    
    private static int deleteInDb(String path, int cate){
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileUtils.FILE_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_AUDIO)
        {
        	uri = Audio.Media.getContentUri(volumeName);
        }
        else
        {
        	
        }
        
        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[] {
            path
        };

        String[] columns = new String[] {
        		MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA
        };

        int count = FileManager.getAppContext().getContentResolver()
                .delete(uri, selection, selectionArgs);
 
        return count;
    }
    
    private static long getDbId(String path, int cate) {
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileUtils.FILE_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_AUDIO)
        {
        	uri = Audio.Media.getContentUri(volumeName);
        }
        else
        {
        	
        }
        
        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[] {
            path
        };

        String[] columns = new String[] {
        		MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA
        };

        Cursor c = FileManager.getAppContext().getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
        if (c == null) {
            return 0;
        }
        long id = 0;
        if (c.moveToNext()) {
            id = c.getLong(0);
        }
        c.close();
        return id;
    }
    
    private static void updateFileInDatabase(File oldfile, File newfile)
    {
    	if(oldfile.isDirectory())
    		return;
    	int category = FileUtils.getFileType(oldfile);
    	Uri uri = null;
    	String oldpath = oldfile.getPath();
    	String newpath = newfile.getPath();
    	ContentValues values = new ContentValues();
        FileObject object = new FileObject(newpath);
        values.put(FavoriteColumns.FILE_NAME_FIELD, object.getName());
        values.put(FavoriteColumns.FILE_PATH_FIELD, object.getPath());
        values.put(FavoriteColumns.FILE_DATE_FIELD, object.getDate());
        values.put(FavoriteColumns.FILE_EXTENSION_FIELD, object.getExtension());
        int count = 0;
        try {
            count = FileManager.getAppContext().getContentResolver().update(FavoriteColumns.CONTENT_URI, values, FavoriteColumns.FILE_PATH_FIELD+"=?", new String[]{oldpath});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "update favorite from "+oldpath+" to "+newpath+" return "+count);

        values.clear();
        long update_count = 0;
    	switch(category)
    	{
    	case FileUtils.FILE_TYPE_APK:
    		uri = DataStructures.ApkColumns.CONTENT_URI;
    		new ApkObject(newpath).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_AUDIO:
    		uri = DataStructures.AudioColumns.CONTENT_URI;
    		new AudioObject(newpath).toContentValues(values);
    		update_count = updateInDb(oldpath, newpath, FileUtils.FILE_TYPE_AUDIO);
    		break;
    	case FileUtils.FILE_TYPE_DOCUMENT:
    		uri = DataStructures.DocumentColumns.CONTENT_URI;
    		new DocumentObject(newpath).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_IMAGE:
    		uri = DataStructures.ImageColumns.CONTENT_URI;
    		new ImageObject(newpath).toContentValues(values);
    		update_count = updateInDb(oldpath, newpath, FileUtils.FILE_TYPE_IMAGE);
    		break;
    	case FileUtils.FILE_TYPE_VIDEO:
    		uri = DataStructures.VideoColumns.CONTENT_URI;
    		new VideoObject(newpath).toContentValues(values);
    		update_count = updateInDb(oldpath, newpath, FileUtils.FILE_TYPE_VIDEO);
    		break;
    	case FileUtils.FILE_TYPE_ZIP:
    		uri = DataStructures.ZipColumns.CONTENT_URI;
    		new ZipObject(newpath).toContentValues(values);
    		break;
    	case FileUtils.FILE_TYPE_FILE:
    	default:
    		uri = DataStructures.FileColumns.CONTENT_URI;
    		new FileObject(newpath).toContentValues(values);
    		break;
    	}
    	if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "update file in media database: "+update_count);
    	if(uri!=null)
    	{
            try {
                count = FileManager.getAppContext().getContentResolver().update(uri, values, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{oldpath});
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "update "+oldpath+" to "+newpath+" return "+count);
    		if(count<=0)
    		{
                try {
                    FileManager.getAppContext().getContentResolver().insert(uri, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
    			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "insert "+newpath+" return "+count);
    		}
    //		deleteFileInMediaDb(oldfile.getAbsolutePath());
    	}


//        int count = FileManager.getAppContext().getContentResolver().delete(FavoriteColumns.CONTENT_URI, FavoriteColumns.FILE_PATH_FIELD+"=?", new String[]{oldpath});
//        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete favorite from "+oldpath+" to "+newpath+" return "+count);
//        if (count > 0) {
//            addToFavorite(newpath);
//        }

    }
    
    public static void removeFromFavorite(String path)
    {
    	ContentValues values = new ContentValues();
    	FileObject fo = new FileObject(path);
		fo.toContentValues(values);

    	Uri uri = FavoriteColumns.CONTENT_URI;
		int count = FileManager.getAppContext().getContentResolver().delete(uri, DataStructures.FileColumns.FILE_PATH_FIELD+"=?", new String[]{path});
		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "remove favorite "+path+" return "+count);
    }

    public static boolean isAllFavorite(String[] paths) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FavoriteColumns.FILE_PATH_FIELD + " IN(");
        for (int i = 0; i < paths.length; i++) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append('?');
        }
        stringBuilder.append(')');

        Cursor cursor = null;
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(FavoriteColumns.CONTENT_URI, new String[]{"count(*) as count"}, stringBuilder.toString(), paths, null);
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count == paths.length) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public static boolean isAllNotFavorite(String[] paths) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FavoriteColumns.FILE_PATH_FIELD + " IN(");
        for (int i = 0; i < paths.length; i++) {
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append('?');
        }
        stringBuilder.append(')');

        Cursor cursor = null;
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(FavoriteColumns.CONTENT_URI, new String[]{"count(*) as count"}, stringBuilder.toString(), paths, null);
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }
    
//    public static void addToSafe(String path)
//    {
//    	try {
//			CryptUtil.encryptOneFile(path);
//		//	FileAction.move(paths, target, cancel);
//
//    		String device0 = getDeviceFromPath(path);
//    		String device = getDeviceFromPath("/mnt/sdcard/");
//    		boolean flag_copy_delete = false;
//    		if(!TextUtils.isEmpty(device) && !TextUtils.isEmpty(device0) && !device0.equals(device))
//    		{
//    			//not in same sdcard
//    			flag_copy_delete = true;
//    		}
//    		if(flag_copy_delete){
//
//    		}
//    		else{
//
//    		}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
    
    public static void addToFavorite(String path)
    {
    	ContentValues values = new ContentValues();
    	FileObject fo = new FileObject(path);
		fo.toContentValues(values);
    	if(new File(path).isDirectory())
    	{
    		values.put(FavoriteColumns.IS_DIRECTORY_FIELD, 1);
    		values.put(FileColumns.FILE_SIZE_FIELD, 0);
    	}
    	else
    	{
    		values.put(FavoriteColumns.IS_DIRECTORY_FIELD, 0);
    	}
    	Uri uri = FavoriteColumns.CONTENT_URI;
		int count = 0;
        try {
            count = FileManager.getAppContext().getContentResolver().update(uri, values, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "update "+path+" return "+count);
		if(count<=0)
		{
            try {
                FileManager.getAppContext().getContentResolver().insert(uri, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "insert "+path+" return "+count);
		}
    }
    
    public static void setAsWallpaper(String  path)
    {
    	if(!TextUtils.isEmpty(path)){
    		File imageFile = new File(path);
    		Bitmap bitmap = ImageUtil.loadBitmapWithSizeLimitation(FileManager.getAppContext(), 500*500, Uri.fromFile(imageFile));

    	    System.out.println("Hi I am try to open Bit map");
    	    WallpaperManager wallpaperManager = WallpaperManager.getInstance(FileManager.getAppContext());
    	    //Drawable wallpaperDrawable = wallpaperManager.getDrawable();
    	    try {
				wallpaperManager.setBitmap(bitmap);
				Toast.makeText(FileManager.getAppContext(), R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
                e.printStackTrace();
            }
    	}
	    Toast.makeText(FileManager.getAppContext(), R.string.set_wallpaper_fail, Toast.LENGTH_SHORT).show();    	
    }
    
//    public static void setAsMultiSimRingTone(File soundFile, boolean first, boolean second)
//    {
//    	ContentValues values = new ContentValues();
//    	
//    	Uri newUri = null;
//    	String path = soundFile.getAbsolutePath();
//    	Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
//    	Cursor cursor = FileManager.getAppContext().getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { path },null);
//    	if (cursor!=null && cursor.moveToFirst() && cursor.getCount() > 0) {
//    		String _id = cursor.getString(0);
//    		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);//設置來電鈴聲為true
//    		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);//設置通知鈴聲為false
//    		values.put(MediaStore.Audio.Media.IS_ALARM, false);//設置鬧鐘鈴聲為false
//    		values.put(MediaStore.Audio.Media.IS_MUSIC, false);
//    		// 把需要設為鈴聲的歌曲更新鈴聲庫
//    		FileManager.getAppContext().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?",new String[] { path });
//    		newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
//    	}
//    	else
//    	{    	
//    	   values.put(MediaStore.MediaColumns.DATA, soundFile.getAbsolutePath());
//    	   values.put(MediaStore.MediaColumns.TITLE, "my ringtone");
//    	   values.put(MediaStore.MediaColumns.MIME_TYPE, FileUtils.getMIMEType(soundFile));
//    	   values.put(MediaStore.MediaColumns.SIZE, soundFile.length());
//    	   values.put(MediaStore.Audio.Media.ARTIST, "");
//    	   values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
//    	   values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
//    	   values.put(MediaStore.Audio.Media.IS_ALARM, true);
//    	   values.put(MediaStore.Audio.Media.IS_MUSIC, false);
//
//    	   newUri = FileManager.getAppContext().getContentResolver().insert(uri, values);
//    	}
//    	if(cursor!=null)
//    		cursor.close();
//
//    	try {
//    		boolean rst_1=false, rst_2=false;
//    		if(first)
//    		{
//    			rst_1 = MultiSimUtil.setActualDefaultRingtoneUri_Spreadtrum_Eton(FileManager.getAppContext(), RingtoneManager.TYPE_RINGTONE, newUri, 0);
//    		}
//    		if(second)
//    		{
//    			rst_2 = MultiSimUtil.setActualDefaultRingtoneUri_Spreadtrum_Eton(FileManager.getAppContext(), RingtoneManager.TYPE_RINGTONE, newUri, 1);
//    		}
//    		if(rst_1 || rst_2)
//    		{
//    			Toast.makeText(FileManager.getAppContext(), R.string.set_ringtone_success, Toast.LENGTH_SHORT).show();
//    		}
//    		else
//    		{
//        		if(first || second)
//        		{
//    	        	try {
//    	        		RingtoneManager.setActualDefaultRingtoneUri(FileManager.getAppContext(), RingtoneManager.TYPE_RINGTONE, newUri);
//    	        		Toast.makeText(FileManager.getAppContext(), R.string.set_ringtone_success, Toast.LENGTH_SHORT).show();
//    	        		return;
//    	        	} catch (Throwable t2) {
//    	        		t2.printStackTrace();
//    	        	}
//        		}
//            	Toast.makeText(FileManager.getAppContext(), R.string.set_ringtone_fail, Toast.LENGTH_SHORT).show();
//    		}
//    		return;
//    	} catch (Throwable t) {
//    		Toast.makeText(FileManager.getAppContext(), "throw"+t.toString(), Toast.LENGTH_SHORT).show();
//    		t.printStackTrace();
//    		if(first || second)
//    		{
//	        	try {
//	        		RingtoneManager.setActualDefaultRingtoneUri(FileManager.getAppContext(), RingtoneManager.TYPE_RINGTONE, newUri);
//	        		Toast.makeText(FileManager.getAppContext(), R.string.set_ringtone_success, Toast.LENGTH_SHORT).show();
//	        		return;
//	        	} catch (Throwable t2) {
//	        		t2.printStackTrace();
//	        	}
//    		}
//        	Toast.makeText(FileManager.getAppContext(), R.string.set_ringtone_fail, Toast.LENGTH_SHORT).show();
//    	}
//    }
    
    public static void setAsRingTone(String path)
    {
    	if(!TextUtils.isEmpty(path)) {
	    	File soundFile = new File(path);
	    	ContentValues values = new ContentValues();
	    	Uri newUri = null;
	    	Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
	    	Cursor cursor = FileManager.getAppContext().getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { path },null);
	    	if (cursor!=null && cursor.moveToFirst() && cursor.getCount() > 0) {
	    		String _id = cursor.getString(0);
	    		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);//設置來電鈴聲為true
	    		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);//設置通知鈴聲為false
	    		values.put(MediaStore.Audio.Media.IS_ALARM, false);//設置鬧鐘鈴聲為false
	    		values.put(MediaStore.Audio.Media.IS_MUSIC, false);
	    		// 把需要設為鈴聲的歌曲更新鈴聲庫
                try {
                    FileManager.getAppContext().getContentResolver().update(uri, values, MediaStore.MediaColumns.DATA + "=?", new String[]{path});
                }catch (Exception e) {
                    e.printStackTrace();
                }
                newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
	    	}
	    	else
	    	{    	
	    	   values.put(MediaStore.MediaColumns.DATA, path);
	    	   values.put(MediaStore.MediaColumns.TITLE, "my ringtone");
	    	   values.put(MediaStore.MediaColumns.MIME_TYPE, FileUtils.getMIMEType(soundFile));
	    	   values.put(MediaStore.MediaColumns.SIZE, soundFile.length());
	    	   values.put(MediaStore.Audio.Media.ARTIST, "");
	    	   values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
	    	   values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
	    	   values.put(MediaStore.Audio.Media.IS_ALARM, true);
	    	   values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                try {
                    newUri = FileManager.getAppContext().getContentResolver().insert(uri, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
	    	}
	    	if(cursor!=null)
	    		cursor.close();
	
	    	try {
	    		RingtoneManager.setActualDefaultRingtoneUri(FileManager.getAppContext(), RingtoneManager.TYPE_RINGTONE, newUri);
	    		Toast.makeText(FileManager.getAppContext(), R.string.set_ringtone_success, Toast.LENGTH_SHORT).show();
	    		return;
	    	} catch (Throwable t) {
	    		t.printStackTrace();
	    	}
	    	Toast.makeText(FileManager.getAppContext(), R.string.set_ringtone_fail, Toast.LENGTH_SHORT).show();
    	}
    }
    
//    public static void sendFile(Context context, File file)
//    {
//    	try{
//	    	Uri uri = Uri.fromFile(file);
//	    	Intent intent = new Intent(Intent.ACTION_SEND);
//	    	intent.setData(uri);
//	    	intent.setType((FileUtils.getMIMEType(file)));
//	    	intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//	    	context.startActivity(/*Intent.createChooser(*/intent/*, "Chooser")*/);
//    	}catch(Exception e)
//    	{
//    		Toast.makeText(FileManager.getAppContext(), R.string.no_activity_found, Toast.LENGTH_SHORT).show();
//    		e.printStackTrace();
//    	}
//    }
//    
//    public static void sendFile(Context context, String path, String pkg)
//    {
//    	try{
//    		File file = new File(path);
//	    	Uri uri = Uri.fromFile(file);
//	    	Intent intent = new Intent(Intent.ACTION_SEND);
//	    	intent.setData(uri);
//	    	String mimeType = FileUtils.getMIMEType(file);
//	    	if("application/vnd.android.package-archive".equals(mimeType)){
//        		mimeType = "application/zip";
//        	}
//	    	intent.setType(mimeType);
//	    	intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//	    	intent.setPackage(pkg);
//	    	context.startActivity(/*Intent.createChooser(*/intent/*, "Chooser")*/);
//    	}catch(Exception e)
//    	{
//    		Toast.makeText(FileManager.getAppContext(), R.string.no_activity_found, Toast.LENGTH_SHORT).show();
//    		e.printStackTrace();
//    	}
//    }

    public static void shareFile(Context context, String file) {
        String mimeType;

        File fileIn = new File(file);
        if (fileIn.isDirectory() || file.length() == 0) {
            Toast.makeText(context, R.string.can_not_share, Toast.LENGTH_SHORT).show();
            return;
        }
        mimeType = FileUtils.getMIMEType(fileIn);
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = "*/*";
        }
        Uri u = Uri.fromFile(fileIn);

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);

        if ("application/vnd.android.package-archive".equals(mimeType)) {
            mimeType = "application/zip";
        }
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, u);
        context.startActivity(intent);
    }
    
    public static Intent buildSendFile(Context context, String... files)
    {
        ArrayList<Uri> uris = new ArrayList<Uri>();

        String mimeType = "*/*";
        boolean flag = false;
        for (String file : files) {
            if (new File(file).isDirectory())
            {
            	flag = true;
                continue;
            }
            File fileIn = new File(file);
            mimeType = FileUtils.getMIMEType(fileIn);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }

        if (uris.size() == 0)
            return null;

        boolean multiple = (uris.size() > 1);
        Intent intent = new Intent(multiple ? android.content.Intent.ACTION_SEND_MULTIPLE
                : android.content.Intent.ACTION_SEND);

        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
        	if("application/vnd.android.package-archive".equals(mimeType)){
        		mimeType = "application/zip";
        	}
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        }
        
        return intent;
    }

    
    public static boolean renameFile(Context context, File file, File newPath)
    {
    	if(!file.renameTo(newPath))
    		return false;
    	updateFileInDatabase(file, newPath);
    	MediaScannerConnection.scanFile(FileManager.getAppContext(),
                new String[] { newPath.getAbsolutePath() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                if(LogUtil.IDBG) LogUtil.i("ExternalStorage", "Scanned " + path + ":");
                if(LogUtil.IDBG) LogUtil.i("ExternalStorage", "-> uri=" + uri);
            }
        });
    	return true;
    }
    
    public static boolean pickFile(Context context, File f){
    	if(!TextUtils.isEmpty(IntentData.action)
    			&& 
    			("android.intent.action.GET_CONTENT".equals(IntentData.action)
    			|| "android.intent.action.PICK".equals(IntentData.action)))
    	{
    		boolean flag_match = true;
    		Uri uri_base = null;
    		if(!TextUtils.isEmpty(IntentData.type) && IntentData.type.startsWith("image/"))
    		{
    			uri_base = Images.Media.getContentUri("external");
    			if(FileUtils.getFileType(f)!=FileUtils.FILE_TYPE_IMAGE)
    			{
    				flag_match = false;
    				Toast.makeText(context, R.string.select_image_file_not_match, Toast.LENGTH_SHORT).show();
    			}
    		}
    		else if(!TextUtils.isEmpty(IntentData.type) && IntentData.type.startsWith("audio/"))
    		{
    			uri_base = Audio.Media.getContentUri("external");
    			if(FileUtils.getFileType(f)!=FileUtils.FILE_TYPE_AUDIO)
    			{
    				flag_match = false;
    				Toast.makeText(context, R.string.select_audio_file_not_match, Toast.LENGTH_SHORT).show();	
    			}
    			else if(IntentData.type.equals("audio/mp3") && !f.getAbsolutePath().endsWith("mp3"))
    			{
    				flag_match = false;
    				Toast.makeText(context, R.string.select_file_not_supported_format, Toast.LENGTH_SHORT).show();
    			}
    		}
    		else if(!TextUtils.isEmpty(IntentData.type) && IntentData.type.startsWith("video/"))
    		{
    			uri_base = Video.Media.getContentUri("external");
    			if(FileUtils.getFileType(f)!=FileUtils.FILE_TYPE_VIDEO)
    			{
    				flag_match = false;
    				Toast.makeText(context, R.string.select_video_file_not_match, Toast.LENGTH_SHORT).show();
    			}
    		}
    		if(flag_match)
    		{
    			Uri uri = null;
    			if(uri_base!=null){
    				long id = getIdInMediaDb(f.getAbsolutePath());
    				if(id!=-1)
    				{
    					uri = Uri.withAppendedPath(uri_base, String.valueOf(id));
    				}
    			}
    			if(uri == null)
    				uri = Uri.fromFile(f);
//    			Intent intent = new Intent();
//    			intent.setData(uri);
//    			((Activity)context).setResult(Activity.RESULT_OK, intent);
//    			((Activity)context).finish();
    			
            	Intent intent = new Intent(context, FileManagerTabActivity.class);
    			intent.setData(uri);
    			intent.setAction("quit");
    			context.startActivity(intent);
    		}
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public static void viewFile(Context context, String file)
    {
    	boolean flag_anim = false;
        File f = new File(file);
		if (f.canRead()) {
	    	Uri uri = Uri.fromFile(f);
	    	
	    	Intent myIntent = new Intent();
	    	myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	
	    	boolean flag_content = false;
	    	
	    	int file_type = FileUtils.getFileType(f);
	    	
	    	switch(file_type)
	    	{
	    		case FileUtils.FILE_TYPE_DOCUMENT:
	    		{
	    	    	myIntent.setAction(android.content.Intent.ACTION_VIEW);
	    			if(viewDocumentFile(context, f))
	    				return;
	    		}
	    		case FileUtils.FILE_TYPE_ZIP:
	    		{
                    myIntent.setAction(Intent.ACTION_VIEW);
                    myIntent.setPackage(PackageUtil.getPackageName(context));
	    		}
	    		case FileUtils.FILE_TYPE_AUDIO:
	    		{
	    	    	myIntent.setAction(android.content.Intent.ACTION_VIEW);
	    			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());    		    			
	    			String music_app_pkg = sp.getString("AUDIO_DEFAULT_VIEW_APP", "");

		    		if(!TextUtils.isEmpty(music_app_pkg))	
		    		{
			    	       PackageInfo packageInfo = null;
			    	       try {
			    	           packageInfo = FileManager.getAppContext().getPackageManager().getPackageInfo(music_app_pkg, 0);
			    	       } catch (NameNotFoundException ex) {
			    	       }
			   		       if (packageInfo != null) {
			    		       	myIntent.setPackage(music_app_pkg);
			   		       }
		    		}
	    			break;
	    		}
	    		case FileUtils.FILE_TYPE_IMAGE:
	    		{
	    	    	myIntent.setAction(android.content.Intent.ACTION_VIEW);
	    			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());    		    			
	    			String image_app_pkg = sp.getString("IMAGE_DEFAULT_VIEW_APP", "");
	    			if(!TextUtils.isEmpty(image_app_pkg))	
	    			{
	    				PackageInfo packageInfo = null;
	    		        try {
	    		            packageInfo = FileManager.getAppContext().getPackageManager().getPackageInfo(image_app_pkg, 0);
	    		        } catch (NameNotFoundException ex) {
	    		        }
	    		        if (packageInfo != null) {
	    		        	myIntent.setPackage(image_app_pkg);
	    		        }
	    			}
	    			break;
	    		}
	    		case FileUtils.FILE_TYPE_VIDEO:
	    		{
                    myIntent.setAction(android.content.Intent.ACTION_VIEW);
	    			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());    		    			
	    			String video_app_pkg = sp.getString("VIDEO_DEFAULT_VIEW_APP", "");
	    			if(!TextUtils.isEmpty(video_app_pkg))
	    			{
	    				PackageInfo packageInfo = null;
	    		        try {
	    		            packageInfo = FileManager.getAppContext().getPackageManager().getPackageInfo(video_app_pkg, 0);
	    		        } catch (NameNotFoundException ex) {
	    		        }
	    		        if (packageInfo != null) {
	    		        	myIntent.setPackage(video_app_pkg);
	    		        }
	    			}
	    			break;
	    		}
	    		default:
	    	    	myIntent.setAction(android.content.Intent.ACTION_VIEW);
	    	}
	    	
	    	//String type = FileUtil.getMIMEType(f);
	    	String type = FileUtils.getMIMEType(f);
	    	
	    	if(type==null || "*/*".equals(type))
	    	{
	    		if(FileUtils.FILE_TYPE_VIDEO == file_type){
	    			type = "video/*";
	    		}else if(FileUtils.FILE_TYPE_AUDIO == file_type){
	    			type = "audio/*";
	    		}else if(FileUtils.FILE_TYPE_IMAGE == file_type){
	    			type = "image/*";
	    		}else{
	    			Toast.makeText(FileManager.getAppContext(), R.string.file_type_not_recognized, Toast.LENGTH_SHORT).show();
	    			return;
	    		}
	    	}
            myIntent.setDataAndType(Uri.fromFile(f), type);
            try {
                context.startActivity(myIntent);
            } catch (Exception e) {
                if (myIntent.getPackage() != null && myIntent.getType() != null) {
                    myIntent.setPackage(null);
                    try {
                        context.startActivity(myIntent);
                    } catch (Exception e2) {
                        Toast.makeText(FileManager.getAppContext(), R.string.no_activity_found, Toast.LENGTH_SHORT).show();
                        e2.printStackTrace();
                    }
                } else {
                    Toast.makeText(FileManager.getAppContext(), R.string.no_activity_found, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

        }
    }
    
    private static boolean viewDocumentFile(Context context, File f)
    {
    	Intent intent = FileIntent.getDocumentFileIntent(f);
    	if(intent!=null)
    	{
	    	try{
	    		context.startActivity(intent);
	    	}catch(Exception e)
	    	{
	    		if(intent.getPackage()!=null && intent.getType()!=null)
	    		{
	    			intent.setPackage(null);
	    			try{
	    				context.startActivity(intent);
	    			}catch(Exception e2)
	    			{
			    		Toast.makeText(FileManager.getAppContext(), R.string.no_activity_found, Toast.LENGTH_SHORT).show();
	    				e2.printStackTrace();
	    			}
	    		}
	    		else
	    		{	
		    		Toast.makeText(FileManager.getAppContext(), R.string.no_activity_found, Toast.LENGTH_SHORT).show();
	    			e.printStackTrace();
	    		}
	    	}finally
	    	{
	    		return true;
	    	}
    	}
    	else
    	{
    		return false;
    	}
    }

    public static String getCloudFileDetailInfo(String remote_path) {
        StringBuffer info = new StringBuffer();
        Cursor cursor = null;
        Context context = FileManager.getAppContext();
        try{
            cursor = context.getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                    DataStructures.CloudBoxColumns.CLOUD_BOX_PROJECTION, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
                    new String[]{remote_path}, null);
            if (cursor!=null && cursor.moveToNext()) {
                String path = cursor.getString(DataStructures.CloudBoxColumns.FILE_PATH_FIELD_INDEX);
                long size = cursor.getLong(DataStructures.CloudBoxColumns.FILE_SIZE_FIELD_INDEX);
                long date = cursor.getLong(DataStructures.CloudBoxColumns.FILE_DATE_FIELD_INDEX);
                int directory = cursor.getInt(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD_INDEX);
                int type = cursor.getInt(DataStructures.CloudBoxColumns.FILE_TYPE_FIELD_INDEX);
                String local_file = cursor.getString(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD_INDEX);
                info.append(context.getString(R.string.cloud_file_info_remote_location)).append(path).append('\n');
                info.append(context.getString(R.string.file_info_modified)).append(TimeUtil.getDateString(date)).append('\n');
                if(directory == 1) {
                    info.append(context.getString(R.string.file_info_kind)).append(context.getString(R.string.file_info_kind_directory)).append('\n');
                } else {
                    info.append(context.getString(R.string.file_info_size)).append(FileUtil.normalize(size)).append('\n');
                    info.append(context.getString(R.string.file_info_kind)).append(context.getString(R.string.file_info_kind_file)).append('\n');
                    if(!TextUtils.isEmpty(local_file)) {
                        info.append(context.getString(R.string.cloud_file_info_local_location)).append(local_file).append('\n');
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(cursor!=null) {
                cursor.close();
            }
        }
        return info.toString();
    }
    
    public static String getFileDetailInfo(String path)
    {
    	String rst = "";
    	File file = new File(path);
    	Context context = FileManager.getAppContext();
    	if(file.isDirectory())
    	{
    		rst += context.getString(R.string.file_info_kind)+""+context.getString(R.string.file_info_kind_directory)+"\n";
    	}
    	else
    	{
    		rst += context.getString(R.string.file_info_kind)+""+context.getString(R.string.file_info_kind_file)+"\n";
    	}
    	String yes = context.getString(R.string.file_info_yes);
    	String no = context.getString(R.string.file_info_no);
    	rst+=context.getString(R.string.file_info_location)+""+path+"\n";
    	rst+=context.getString(R.string.file_info_modified)+""+FileUtils.getFileDate(file)+"\n";
    	if(!file.isDirectory())
    	{
    		rst+=context.getString(R.string.file_info_size)+""+FileUtils.getFileSize(file)+"\n";
    	}
    	rst+=context.getString(R.string.file_info_canread)+""+(file.canRead()?yes:no)+"\n";
    	rst+=context.getString(R.string.file_info_canwrite)+""+(file.canWrite()?yes:no)+"\n";
    	rst+=context.getString(R.string.file_info_ishidden)+""+(file.isHidden()?yes:no)+"\n";
    	return rst;
    }
    
}
