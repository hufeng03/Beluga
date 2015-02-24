package com.hufeng.playimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.textservice.TextInfo;
import android.webkit.MimeTypeMap;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.IconLoaderHelper;
import com.hufeng.filemanager.utils.MimeUtil;

import java.io.File;

public class BelugaLazyLoadImageView extends BaseLazyLoadImageView {

    private int mDefaultResource;

    private String mMimeType;

    public BelugaLazyLoadImageView(Context context) {
        super(context);
    }

    public BelugaLazyLoadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void useDefaultResource() {
        if (currentUri!=null && targetUri!= null && currentUri.equals(targetUri)) {
            return;
        }
        setScaleType(ScaleType.CENTER_INSIDE);
        if (mDefaultResource == 0) {
            setImageDrawable(new ColorDrawable(0));
        } else {
            setImageResource(mDefaultResource);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm, String url) {
        if (mMimeType.startsWith("application/")) {
            setScaleType(ScaleType.CENTER_INSIDE);
        } else {
            setScaleType(ScaleType.CENTER_CROP);
        }
        super.setImageBitmap(bm, url);
    }

    //    public void requestDisplayKanbox(String path, boolean directory) {
//        int type = directory?FileUtils.FILE_TYPE_DIRECTORY:FileUtils.getFileType(path);
//        if (directory || type!=FileUtils.FILE_TYPE_IMAGE) {
//            currentUri = path;
//            targetUri = path;
//            useDefaultResource();
//            return;
//        }
//        String uri = ImageLoader.KANBOX_PREFIX + path;
//        super.requestDisplayImage(uri);
//    }
//
//    public void requestDisplayKanboxThumbnail(String path, boolean directory) {
//        int type = directory?FileUtils.FILE_TYPE_DIRECTORY:FileUtils.getFileType(path);
//        if (directory || type!=FileUtils.FILE_TYPE_IMAGE) {
//            currentUri = path;
//            targetUri = path;
//            useDefaultResource();
//            return;
//        }
//        String uri = ImageLoader.KANBOX_THUMBNAIL_PREFIX + path;
//        super.requestDisplayImage(uri);
//    }

//    public void requestDisplayLocalThumbnail(String path) {
//        int type = FileUtils.getFileType(path);
//        if (type != FileUtils.FILE_TYPE_IMAGE && type != FileUtils.FILE_TYPE_APK
//                && type != FileUtils.FILE_TYPE_AUDIO && type != FileUtils.FILE_TYPE_VIDEO) {
//            currentUri = path;
//            targetUri = path;
//            useDefaultResource();
//            return;
//        }
//        String uri = ImageLoader.THUMBNAIL_PREFIX + path;
//        super.requestDisplayImage(uri);
//    }
//
//    public void requestDisplayLocalImage(String path) {
//        int type = FileUtils.getFileType(path);
//        setDefaultResource(IconLoaderHelper.getFileIcon(type));
//        if (type != FileUtils.FILE_TYPE_IMAGE && type != FileUtils.FILE_TYPE_APK
//                && type != FileUtils.FILE_TYPE_AUDIO && type != FileUtils.FILE_TYPE_VIDEO) {
//            currentUri = path;
//            targetUri = path;
//            useDefaultResource();
//            return;
//        }
//        super.requestDisplayImage(path);
//    }

    @Override
    public void requestDisplayImage(String uri) {
        boolean needToLoad = false;
        if (new File(uri).isDirectory()) {
            setDefaultResource(R.drawable.file_icon_folder);
            setMimeType(DocumentsContract.Document.MIME_TYPE_DIR);
        } else {
            setDefaultResource(IconLoaderHelper.getFileIcon(getContext(), uri));
            setMimeType(MimeUtil.getMimeType(uri));
            if (!TextUtils.isEmpty(mMimeType) && (
                    mMimeType.startsWith("image/") ||
                    mMimeType.startsWith("audio/") ||
                    mMimeType.startsWith("video/") ||
                    mMimeType.equals("application/vnd.android.package-archive")
            )) {
                needToLoad = true;
            }
        }
        if (needToLoad) {
            super.requestDisplayImage(uri);
        } else {
            targetUri = uri;
            useDefaultResource();
            currentUri = uri;
        }
    }

    public void setDefaultResource(int defaultResource) {
        mDefaultResource = defaultResource;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

//    @Override
//    public boolean setImageBitmapIfNeeds(Bitmap bm, String uri) {
//        boolean flag = false;
//        //if we got thumbnail bitmap, but target is original and current is none, then set thumbnail for temporary usage
//        if (currentUri != uri && currentUri!=targetUri && uri.equals(targetUri)) {
//            setImageBitmap(bm);
//            flag = true;
//        }
//        return flag || super.setImageBitmapIfNeeds(bm, uri);
//    }

//    /**
//     * this function is for test only, do not call it in release code
//     * @param path
//     */
//    public void requestDisplayLocalThumbnailOnUIThread(String path) {
//        int type = FileUtils.getFileType(path);
//        setDefaultResource(IconLoaderHelper.getFileIcon(type));
//        if (type != FileUtils.FILE_TYPE_IMAGE && type != FileUtils.FILE_TYPE_APK
//                && type != FileUtils.FILE_TYPE_AUDIO && type != FileUtils.FILE_TYPE_VIDEO) {
//            currentUri = path;
//            targetUri = path;
//            useDefaultResource();
//            return;
//        }
//        setImageBitmap(IconUtil.getImageThumbnail(path));
//    }


}
