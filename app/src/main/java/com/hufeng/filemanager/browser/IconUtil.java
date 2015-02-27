package com.hufeng.filemanager.browser;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.mp3.Mp3ReadId3v2;
import com.hufeng.filemanager.utils.ImageUtil;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.MimeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by feng on 13-10-7.
 */
public class IconUtil {

    private static final String TAG = IconUtil.class.getSimpleName();


    public static Bitmap getApkThumbnail(String path) {
        final Context context = FileManager.getAppContext();
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        appInfo.sourceDir = path;
        appInfo.publicSourceDir = path;
        Drawable icon = appInfo.loadIcon(context.getPackageManager());
        Bitmap bmpIcon = ((BitmapDrawable) icon).getBitmap();
        return bmpIcon;
    }

    public static Bitmap getImageThumbnail(String path, String mimeType) {
        Bitmap bitmap = null;
        try {
            long id = getDbId(path, FileCategoryHelper.CATEGORY_TYPE_IMAGE);
            bitmap = getImageThumbnailFromDatabase(id);
        } catch (Exception e) {
          e.printStackTrace();
        }

        if (bitmap == null) {
            bitmap = getImageThumbnailFromFile(path, mimeType);
        }
        return bitmap;
    }
    public static Bitmap getVideoThumbnail(String path, String mimeType) {
        Bitmap bitmap = null;
        try {
            long id = getDbId(path, FileCategoryHelper.CATEGORY_TYPE_VIDEO);
            bitmap = getVideoThumbnailFromDatabase(id);
        } catch (Exception e) {

        }
        if (bitmap == null) {
            bitmap = getVideoThumbnailFromFile(path, mimeType);
        }
        return bitmap;

    }
    public static Bitmap getAudioThumbnail(String path, String mimeType) {
        Bitmap bitmap = null;
        try {
            long id = getDbId(path, FileCategoryHelper.CATEGORY_TYPE_AUDIO);
            bitmap = getAudioThumbnailFromDatabase(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            bitmap = getAudioThumbnailFromFile(path, mimeType);
        }
        return bitmap;
    }


    private static Bitmap getImageThumbnailFromDatabase(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(FileManager.getAppContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
    }

    private static Bitmap getVideoThumbnailFromDatabase(long id) {
        return MediaStore.Video.Thumbnails.getThumbnail(FileManager.getAppContext().getContentResolver(), id, MediaStore.Video.Thumbnails.MINI_KIND, null);
    }

    private static Bitmap getAudioThumbnailFromDatabase(long id) {
        return null;
    }

    private static Bitmap getVideoThumbnailFromFile(String path, String mimeType) {
        Bitmap bitmap =  ThumbnailUtils.createVideoThumbnail(path,  MediaStore.Video.Thumbnails.MINI_KIND);
        return bitmap;
    }

    private static Bitmap getImageThumbnailFromFile(String path, String mimeType)
    {
        try {
            Method method = ThumbnailUtils.class.getMethod("createImageThumbnail", new Class[]{String.class, int.class});
            Bitmap bitmap = (Bitmap) method.invoke(null, new Object[]{path, new Integer(MediaStore.Images.Thumbnails.MINI_KIND)});
            if (bitmap != null) {
                return bitmap;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Bitmap getAudioThumbnailFromFile(String path, String mimeType){
        Bitmap bm = null;

        if("audio/mpeg".equals(mimeType) || "audio/mp3".equals(mimeType)) {
            try {
                Mp3ReadId3v2 mp3Id3v2 = new Mp3ReadId3v2(new FileInputStream(path));
                mp3Id3v2.readId3v2(1024 * 100);
                if (mp3Id3v2.getImg() != null) {
                    String path_base64 = Base64.encodeToString(path.getBytes(), Base64.DEFAULT);
                    int len = path_base64.length();
                    int i = len-1;
                    StringBuilder builder = new StringBuilder();
                    while (i >= 0) {
                        char c = path_base64.charAt(i);
                        if ( (c >='a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >='0' && c <='9')) {
                            builder.append(c);
                        }
                        i--;
                    }
                    if (builder.length() == 0) {
                        return null;
                    }
                    String name = builder.toString()+".jpg";
                    File imgFile = new File(FileManager.getAppContext().getExternalCacheDir().getAbsolutePath(),
									/* mp3Id3v2.getAuthor() +"_"+mp3Id3v2.getSpecial()*/ name);
                    if (!imgFile.exists()) {
                        imgFile.createNewFile();
                        FileOutputStream fout = new FileOutputStream(imgFile);
                        fout.write(mp3Id3v2.getImg());
                        fout.close();
                    }
                    bm = ImageUtil.loadBitmapWithSizeLimitation(FileManager.getAppContext(),
                            512 * 512, Uri.fromFile(imgFile));
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return bm;
    }

    private static long getDbId(String path, int cate) throws Exception{
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileCategoryHelper.CATEGORY_TYPE_VIDEO)
        {
            uri = MediaStore.Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_IMAGE)
        {
            uri = MediaStore.Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileCategoryHelper.CATEGORY_TYPE_AUDIO)
        {
            uri = MediaStore.Audio.Media.getContentUri(volumeName);
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

        long id = 0;
        Cursor c = null;
        boolean found = false;
        try {
            c = FileManager.getAppContext().getContentResolver()
                .query(uri, columns, selection, selectionArgs, null);
            if (c != null) {
                if (c.moveToNext()) {
                    id = c.getLong(0);
                    found = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c!= null) {
                c.close();
            }
        }
        if (!found) {
            throw new Exception("Entry not found in MediaDatabase");
        }
        return id;
    }
}
