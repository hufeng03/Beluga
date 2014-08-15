package com.hufeng.filemanager.browser;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.mp3.Mp3ReadId3v2;
import com.hufeng.filemanager.utils.ImageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by feng on 13-10-7.
 */
public class IconUtil {

    private static final String TAG = IconUtil.class.getSimpleName();

    private static final int MINI_KIND = 1;

    public static Bitmap getImageThumbnail(String path){
        long id = getDbId(path, FileUtils.FILE_TYPE_IMAGE);
        return getImageThumbnail(path, id);
    }

    public static Bitmap getVideoThumbnail(String path) {
//        android.os.Debug.waitForDebugger();
        long id = getDbId(path, FileUtils.FILE_TYPE_VIDEO);
//        LogUtil.i(TAG, "video_"+path+" id in db is " + id);
        return getVideoThumbnail(path, id);
    }

    public static Bitmap getAudioThumbnail(String path){
        long id = getDbId(path, FileUtils.FILE_TYPE_AUDIO);
        return getAudioThumbnail(path, id);
    }

    public static Bitmap getImageThumbnail(String path, long id) {
        Bitmap bm = null;
        //Bitmap bitmap = null;
        Bitmap bitmap = getImageThumbnailFromDatabase(id);
        if (bitmap == null) {
            bitmap = getImageThumbnailFromFile(path);
        }

        if(bitmap!=null)
        {
            int degree = ImageUtil.getImageRotateDegree(path);
            if(degree!=0)
            {
                Matrix mat = new Matrix();
                mat.postRotate(degree);
                bm  = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),mat,true);
                if(bm!=null && !bm.equals(bitmap))
                {
                    bitmap.recycle();
                }
                else
                {
                    bm = bitmap;
                }
            }
            else
            {
                bm = bitmap;
            }
        }
        return bm;
    }
    public static Bitmap getVideoThumbnail(String path, long id) {
        Bitmap bm = getVideoThumbnailFromDatabase(id);
        if (bm == null) {
            bm = getVideoThumbnailFromFile(path);
        }
        return bm;

    }
    public static Bitmap getAudioThumbnail(String path, long id) {
        Bitmap bm = getAudioThumbnailFromDatabase(id);
        if(bm==null)
        {
            bm = getAudioThumbnailFromFile(path);
        }
        return bm;
    }


    private static Bitmap getImageThumbnailFromDatabase(long id) {
        return MediaStore.Images.Thumbnails.getThumbnail(FileManager.getAppContext().getContentResolver(), id, MINI_KIND, null);
    }

    private static Bitmap getVideoThumbnailFromDatabase(long id) {
        return MediaStore.Video.Thumbnails.getThumbnail(FileManager.getAppContext().getContentResolver(), id, MINI_KIND, null);
    }

    private static Bitmap getAudioThumbnailFromDatabase(long id) {
        return null;
    }

    private static Bitmap getVideoThumbnailFromFile(String path) {
        Bitmap bitmap =  ThumbnailUtils.createVideoThumbnail(path, MINI_KIND);
//        LogUtil.i(TAG, "video_"+path+" create thumbnail from file " + bitmap);
        return bitmap;
    }

    private static Bitmap getImageThumbnailFromFile(String path)
    {
        Bitmap bm = ImageUtil.loadBitmapWithSizeLimitation(FileManager.getAppContext(),
                512 * 512, Uri.fromFile(new File(path)));

        if(bm==null)
            return null;
        return bm;
    }

    private static Bitmap getAudioThumbnailFromFile(String path){
        Bitmap bm = null;

        if("mp3".equalsIgnoreCase(IconLoaderHelper.getExtFromFilename(path)))
        {
            try {
                Mp3ReadId3v2 mp3Id3v2 = new Mp3ReadId3v2(new FileInputStream(path));
                mp3Id3v2.readId3v2(1024 * 100);
                if (mp3Id3v2.getImg() != null) {
//                    String name = Base64.encodeToString(path.getBytes(), Base64.DEFAULT);
                    int len = path.length();
                    int i = len-1;
                    StringBuilder builder = new StringBuilder();
                    while (i >= 0) {
                        char c = path.charAt(i);
                        if ( (c >='a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >='0' && c <='9')) {
                            builder.append(c);
                        }
                        i--;
                    }
                    if (builder.length() == 0) {
                        return null;
                    }
                    String name = "album_icon_"+builder.toString()+".jpg";
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
                    //						if(tmpBm!=null)
                    //						{
                    //							AudioUtil.insertAlbumArt(path, imgFile.getPath());
                    //						}
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return bm;
    }

    private static long getDbId(String path, int cate) {
        String volumeName = "external";
        Uri uri = null;
        if(cate==FileUtils.FILE_TYPE_VIDEO)
        {
            uri = MediaStore.Video.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_IMAGE)
        {
            uri = MediaStore.Images.Media.getContentUri(volumeName);
        }
        else if(cate==FileUtils.FILE_TYPE_AUDIO)
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
}
