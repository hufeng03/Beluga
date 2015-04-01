package com.belugamobile.filemanager.browser;

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

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.mp3.Mp3ReadId3v2;
import com.belugamobile.filemanager.utils.DrawableUtil;
import com.belugamobile.filemanager.utils.ImageUtil;
import com.belugamobile.filemanager.utils.LogUtil;
import com.belugamobile.filemanager.utils.MimeUtil;

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
        if (packageInfo != null) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            appInfo.sourceDir = path;
            appInfo.publicSourceDir = path;
            Drawable icon = appInfo.loadIcon(context.getPackageManager());
            return DrawableUtil.getBitmapFromDrawable(icon);
        } else {
            return getApkThumbnailByReflection(path);
        }
    }

    public static Bitmap getApkThumbnailByReflection(String apkPath) {
        final Context context = FileManager.getAppContext();
        File apkFile = new File(apkPath);
        if (!apkFile.exists() || !apkPath.toLowerCase().endsWith(".apk")) {
            return null;
        }
        String PATH_PackageParser = "android.content.pm.PackageParser";
        String PATH_AssetManager = "android.content.res.AssetManager";
        try {
            //反射得到pkgParserCls对象并实例化,有参数
            Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
            Class<?>[] typeArgs = {String.class};
            Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
            Object[] valueArgs = {apkPath};
            Object pkgParser = pkgParserCt.newInstance(valueArgs);

            //从pkgParserCls类得到parsePackage方法
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();//这个是与显示有关的, 这边使用默认
            typeArgs = new Class<?>[]{File.class, String.class,
                    DisplayMetrics.class, int.class};
            Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod("parsePackage", typeArgs);

            valueArgs = new Object[]{new File(apkPath), apkPath, metrics, 0};
            //执行pkgParser_parsePackageMtd方法并返回
            Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser, valueArgs);
            //从返回的对象得到名为"applicationInfo"的字段对象
            if (pkgParserPkg == null) {
                return null;
            }
            Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
                    "applicationInfo");

            //从对象"pkgParserPkg"得到字段"appInfoFld"的值
            if (appInfoFld.get(pkgParserPkg) == null) {
                return null;
            }
            ApplicationInfo info = (ApplicationInfo) appInfoFld
                    .get(pkgParserPkg);
            //反射得到assetMagCls对象并实例化,无参
            Class<?> assetMagCls = Class.forName(PATH_AssetManager);
            Object assetMag = assetMagCls.newInstance();
            //从assetMagCls类得到addAssetPath方法
            typeArgs = new Class[1];
            typeArgs[0] = String.class;
            Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
                    "addAssetPath", typeArgs);
            valueArgs = new Object[1];
            valueArgs[0] = apkPath;
            //执行assetMag_addAssetPathMtd方法
            assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
            //得到Resources对象并实例化,有参数
            Resources res = context.getResources();
            typeArgs = new Class[3];
            typeArgs[0] = assetMag.getClass();
            typeArgs[1] = res.getDisplayMetrics().getClass();
            typeArgs[2] = res.getConfiguration().getClass();
            Constructor<Resources> resCt = Resources.class
                    .getConstructor(typeArgs);
            valueArgs = new Object[3];
            valueArgs[0] = assetMag;
            valueArgs[1] = res.getDisplayMetrics();
            valueArgs[2] = res.getConfiguration();
            res = (Resources) resCt.newInstance(valueArgs);
            // 读取apk文件的信息

            if (info != null) {
                if (info.icon != 0) {// 图片存在，则读取相关信息
                    Drawable icon = res.getDrawable(info.icon);// 图标
                    return DrawableUtil.getBitmapFromDrawable(icon);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        return bitmap;
    }

    private static Bitmap getImageThumbnailFromFile(String path, String mimeType) {
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

    private static Bitmap getAudioThumbnailFromFile(String path, String mimeType) {
        Bitmap bm = null;

        if ("audio/mpeg".equals(mimeType) || "audio/mp3".equals(mimeType)) {
            try {
                Mp3ReadId3v2 mp3Id3v2 = new Mp3ReadId3v2(new FileInputStream(path));
                mp3Id3v2.readId3v2(1024 * 100);
                if (mp3Id3v2.getImg() != null) {
                    String path_base64 = Base64.encodeToString(path.getBytes(), Base64.DEFAULT);
                    int len = path_base64.length();
                    int i = len - 1;
                    StringBuilder builder = new StringBuilder();
                    while (i >= 0) {
                        char c = path_base64.charAt(i);
                        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                            builder.append(c);
                        }
                        i--;
                    }
                    if (builder.length() == 0) {
                        return null;
                    }
                    String name = builder.toString() + ".jpg";
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

    private static long getDbId(String path, int cate) throws Exception {
        String volumeName = "external";
        Uri uri = null;
        if (cate == FileCategoryHelper.CATEGORY_TYPE_VIDEO) {
            uri = MediaStore.Video.Media.getContentUri(volumeName);
        } else if (cate == FileCategoryHelper.CATEGORY_TYPE_IMAGE) {
            uri = MediaStore.Images.Media.getContentUri(volumeName);
        } else if (cate == FileCategoryHelper.CATEGORY_TYPE_AUDIO) {
            uri = MediaStore.Audio.Media.getContentUri(volumeName);
        } else {

        }

        String selection = MediaStore.Files.FileColumns.DATA + "=?";
        ;
        String[] selectionArgs = new String[]{
                path
        };

        String[] columns = new String[]{
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
            if (c != null) {
                c.close();
            }
        }
        if (!found) {
            throw new Exception("Entry not found in MediaDatabase");
        }
        return id;
    }
}
