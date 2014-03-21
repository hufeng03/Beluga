package com.hufeng.playimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.browser.IconUtil;

public class MyLazyLoadImageView extends BaseLazyLoadImageView {

    private int mDefaultResource;
    private int mDisplayMode;

    public MyLazyLoadImageView(Context context) {
        super(context);
    }

    public MyLazyLoadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void useDefaultBitmap() {
        if (currentUri!=null && targetUri!= null && currentUri.equals(ImageLoader.THUMBNAIL_PREFIX+targetUri)) {
            return;
        }
        if (mDefaultResource == 0) {
            setImageDrawable(new ColorDrawable(0));
        } else {
            setImageResource(mDefaultResource);
        }
    }

    public void requestDisplayKanbox(String path, boolean directory) {
        int type = directory?FileUtils.FILE_TYPE_DIRECTORY:FileUtils.getFileType(path);
        if (directory || type!=FileUtils.FILE_TYPE_IMAGE) {
            currentUri = path;
            targetUri = path;
            useDefaultBitmap();
            return;
        }
        String uri = ImageLoader.KANBOX_PREFIX + path;
        super.requestDisplayImage(uri);
    }

    public void requestDisplayKanboxThumbnail(String path, boolean directory) {
        int type = directory?FileUtils.FILE_TYPE_DIRECTORY:FileUtils.getFileType(path);
        if (directory || type!=FileUtils.FILE_TYPE_IMAGE) {
            currentUri = path;
            targetUri = path;
            useDefaultBitmap();
            return;
        }
        String uri = ImageLoader.KANBOX_THUMBNAIL_PREFIX + path;
        super.requestDisplayImage(uri);
    }

    public void requestDisplayLocalThumbnail(String path) {
        int type = FileUtils.getFileType(path);
        if (type != FileUtils.FILE_TYPE_IMAGE && type != FileUtils.FILE_TYPE_APK
                && type != FileUtils.FILE_TYPE_AUDIO && type != FileUtils.FILE_TYPE_VIDEO) {
            currentUri = path;
            targetUri = path;
            useDefaultBitmap();
            return;
        }
        String uri = ImageLoader.THUMBNAIL_PREFIX + path;
        super.requestDisplayImage(uri);
    }

    public void requestDisplayLocalImage(String path) {
        int type = FileUtils.getFileType(path);
        setDefaultResource(IconLoaderHelper.getFileIcon(type));
        if (type != FileUtils.FILE_TYPE_IMAGE && type != FileUtils.FILE_TYPE_APK
                && type != FileUtils.FILE_TYPE_AUDIO && type != FileUtils.FILE_TYPE_VIDEO) {
            currentUri = path;
            targetUri = path;
            useDefaultBitmap();
            return;
        }
        super.requestDisplayImage(path);
    }

    @Override
    public void requestDisplayImage(String uri) {
        if (TextUtils.isEmpty(uri)) {
            currentUri = null;
            targetUri = null;
            useDefaultBitmap();
            return;
        }
        super.requestDisplayImage(uri);
    }

    public void setDefaultResource(int defaultResource) {
        mDefaultResource = defaultResource;
    }

    @Override
    public boolean setImageBitmapIfNeeds(Bitmap bm, String uri) {
        boolean flag = false;
        //if we got thumbnail bitmap, but target is original and current is none, then set thumbnail for temporary usage
        if (currentUri != uri && currentUri!=targetUri && uri.equals(ImageLoader.THUMBNAIL_PREFIX + targetUri)) {
            setImageBitmap(bm);
            flag = true;
        }
        return flag || super.setImageBitmapIfNeeds(bm, uri);
    }

    /**
     * this function is for test only, do not call it in release code
     * @param path
     */
    public void requestDisplayLocalThumbnailOnUIThread(String path) {
        int type = FileUtils.getFileType(path);
        setDefaultResource(IconLoaderHelper.getFileIcon(type));
        if (type != FileUtils.FILE_TYPE_IMAGE && type != FileUtils.FILE_TYPE_APK
                && type != FileUtils.FILE_TYPE_AUDIO && type != FileUtils.FILE_TYPE_VIDEO) {
            currentUri = path;
            targetUri = path;
            useDefaultBitmap();
            return;
        }
        setImageBitmap(IconUtil.getImageThumbnail(path));
    }


}
