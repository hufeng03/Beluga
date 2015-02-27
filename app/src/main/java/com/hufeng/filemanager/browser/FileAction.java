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

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.helper.FileCategoryHelper;
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
import com.hufeng.filemanager.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileAction {
	
	private static final String LOG_TAG = "FileAction";

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
    
    public static boolean copyFile(File file, File plasPath){
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
    	int cate = FileCategoryHelper.getFileCategoryForFile(path);
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileCategoryHelper.CATEGORY_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_AUDIO)
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
    	int cate = FileCategoryHelper.getFileCategoryForFile(path);
        String volumeName = "external";
        Uri uri = null;
        if(cate == FileCategoryHelper.CATEGORY_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_AUDIO)
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
        try {
            FileManager.getAppContext().getContentResolver().delete(uri, selection, selectionArgs);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public static void deleteFileInDatabase(File file)
    {
    	if(file.isDirectory())
    		return;
    	int category = FileCategoryHelper.getFileCategoryForFile(file.getAbsolutePath());
    	Uri uri = null;
    	switch(category)
    	{
    	case FileCategoryHelper.CATEGORY_TYPE_APK:
    		uri = DataStructures.ApkColumns.CONTENT_URI;
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
    		uri = DataStructures.AudioColumns.CONTENT_URI;
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
    		uri = DataStructures.DocumentColumns.CONTENT_URI;
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
    		uri = DataStructures.ImageColumns.CONTENT_URI;
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
    		uri = DataStructures.VideoColumns.CONTENT_URI;
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_ZIP:
    		uri = DataStructures.ZipColumns.CONTENT_URI;
    		break;
    	default:
    		uri = DataStructures.FileColumns.CONTENT_URI;
    		break;
    	}
    	if(uri!=null)
    	{
    		String path = file.getPath();
            int favorite_count = 0;
            int count =0;
            try {
                favorite_count = FileManager.getAppContext().getContentResolver().delete(FavoriteColumns.CONTENT_URI, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{path});
                count = FileManager.getAppContext().getContentResolver().delete(uri, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{path});
            } catch (Exception e){
                e.printStackTrace();
            }
    		if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "delete "+path+" return "+count+","+favorite_count);
    	}
    	
    	deleteFileInMediaDb(file.getAbsolutePath());
    }
    
    public static void addFileInDatabase(File file)
    {
    	if(file.isDirectory())
    		return;
    	int category = FileCategoryHelper.getFileCategoryForFile(file.getAbsolutePath());

        if (category == FileCategoryHelper.CATEGORY_TYPE_IMAGE) {
            long size = file.length();
            if (size < 30720) {
                String filter = FileManager.getPreference(FileManager.IMAGE_FILTER_SMALL, "1");
                if (filter.equals("1")) {
                    if (LogUtil.IDBG) {
                        LogUtil.i(LOG_TAG, "image filter small "+size+" of "+file.getAbsolutePath());
                    }
                    return;
                }
            }
        } else if (category == FileCategoryHelper.CATEGORY_TYPE_AUDIO) {
            long size = file.length();
            if (file.length() < 102400) {
                String filter = FileManager.getPreference(FileManager.AUDIO_FILTER_SMALL, "1");
                if (filter.equals("1")) {
                    if (LogUtil.IDBG) {
                        LogUtil.i(LOG_TAG, "audio filter small "+size+" of "+file.getAbsolutePath());
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
    	case FileCategoryHelper.CATEGORY_TYPE_APK:
    		uri = DataStructures.ApkColumns.CONTENT_URI;
    		new ApkObject(path).toContentValues(values);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
    		uri = DataStructures.AudioColumns.CONTENT_URI;
    		new AudioObject(path).toContentValues(values);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
    		uri = DataStructures.DocumentColumns.CONTENT_URI;
    		new DocumentObject(path).toContentValues(values);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
    		uri = DataStructures.ImageColumns.CONTENT_URI;
    		new ImageObject(path).toContentValues(values);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
    		uri = DataStructures.VideoColumns.CONTENT_URI;
    		new VideoObject(path).toContentValues(values);
    		break;
    	case FileCategoryHelper.FILE_TYPE_ZIP:
    		uri = DataStructures.ZipColumns.CONTENT_URI;
    		new ZipObject(path).toContentValues(values);
    		break;
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
    
    public static void moveDirInDatabase(File olddir, File newdir)
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
    
    private static int updateInDb(String old_path, String new_path, int cate){
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileCategoryHelper.CATEGORY_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_VIDEO)
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
        String name = null;
        if (!TextUtils.isEmpty(new_path)) {
            int i = new_path.lastIndexOf("/");
            if (i>0) {
                name = new_path.substring(i+1);
            }
        }
        if (!TextUtils.isEmpty(name)) {
            cvs.put(MediaStore.Files.FileColumns.DISPLAY_NAME, name);
        }
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
        if(cate==FileCategoryHelper.CATEGORY_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_AUDIO)
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
        if(cate==FileCategoryHelper.CATEGORY_TYPE_VIDEO)
        {
        	uri = Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_IMAGE)
        {
        	uri = Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_AUDIO)
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
    
    public static void updateFileInDatabase(File oldfile, File newfile)
    {
    	if(oldfile.isDirectory())
    		return;
    	int category = FileCategoryHelper.getFileCategoryForFile(oldfile.getAbsolutePath());
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
    	case FileCategoryHelper.CATEGORY_TYPE_APK:
    		uri = DataStructures.ApkColumns.CONTENT_URI;
    		new ApkObject(newpath).toContentValues(values);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
    		uri = DataStructures.AudioColumns.CONTENT_URI;
    		new AudioObject(newpath).toContentValues(values);
    		update_count = updateInDb(oldpath, newpath, FileCategoryHelper.CATEGORY_TYPE_AUDIO);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
    		uri = DataStructures.DocumentColumns.CONTENT_URI;
    		new DocumentObject(newpath).toContentValues(values);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
    		uri = DataStructures.ImageColumns.CONTENT_URI;
    		new ImageObject(newpath).toContentValues(values);
    		update_count = updateInDb(oldpath, newpath, FileCategoryHelper.CATEGORY_TYPE_IMAGE);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
    		uri = DataStructures.VideoColumns.CONTENT_URI;
    		new VideoObject(newpath).toContentValues(values);
    		update_count = updateInDb(oldpath, newpath, FileCategoryHelper.CATEGORY_TYPE_VIDEO);
    		break;
    	case FileCategoryHelper.CATEGORY_TYPE_ZIP:
    		uri = DataStructures.ZipColumns.CONTENT_URI;
    		new ZipObject(newpath).toContentValues(values);
    		break;
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
    
    public static boolean removeFromFavorite(String path)
    {
    	ContentValues values = new ContentValues();
    	FileObject fo = new FileObject(path);
		fo.toContentValues(values);

    	Uri uri = FavoriteColumns.CONTENT_URI;
		int count = 0;
        try {
            count = FileManager.getAppContext().getContentResolver().delete(uri, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count > 0;
    }

    public static boolean isFavorite(String path) {
        Cursor cursor = null;
        try {
            cursor = FileManager.getAppContext().getContentResolver().query(FavoriteColumns.CONTENT_URI,
                    null,
                    FavoriteColumns.FILE_PATH_FIELD+"=?",
                    new String[]{path},
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                return true;
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

    
    public static boolean addToFavorite(String path)
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
		if(count <= 0)
		{
            try {
                FileManager.getAppContext().getContentResolver().insert(uri, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
			if(LogUtil.IDBG) LogUtil.i(LOG_TAG, "insert "+path+" return "+count);
		}
        return count > 0;
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

    
}
