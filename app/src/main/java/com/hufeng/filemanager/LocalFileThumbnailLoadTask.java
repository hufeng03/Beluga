package com.hufeng.filemanager;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.hufeng.filemanager.browser.IconUtil;
import com.hufeng.filemanager.utils.MimeUtil;

/**
 * Created by Feng Hu on 15-02-04.
 * <p/>
 * TODO: Add a class header comment.
 */
public class LocalFileThumbnailLoadTask extends ImageLoadTask{

    public LocalFileThumbnailLoadTask(Handler handler, String url) {
        super(handler, url);
    }

    @Override
    protected Bitmap load(String url) {
        String mimeType = MimeUtil.getMimeType(url);
        Bitmap bm;
        if (mimeType.startsWith("video/")) {
            bm = IconUtil.getVideoThumbnail(url, mimeType);
        } else if (mimeType.startsWith("image/")) {
            bm = IconUtil.getImageThumbnail(url, mimeType);
        } else if (mimeType.startsWith("audio/")) {
            bm = IconUtil.getAudioThumbnail(url, mimeType);
        } else if (mimeType.equals("application/vnd.android.package-archive")) {
            bm = IconUtil.getApkThumbnail(url);
        } else {
            bm = null;
        }
        return bm;
    }
}
