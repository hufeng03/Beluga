package com.hufeng.playimage;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * Created by feng on 14-2-3.
 */
public class KanboxImageLoadTask extends BaseImageLoadTask{

    public KanboxImageLoadTask(Handler handler, String uri) {
        super(handler, uri);
    }

    @Override
    protected Bitmap load(String uri) {
    //    android.os.Debug.waitForDebugger();
        boolean thumbnail = false;
        if (uri.startsWith(ImageLoader.KANBOX_THUMBNAIL_PREFIX)) {
            thumbnail = true;
        }
        if (thumbnail) {
            uri = uri.substring(ImageLoader.KANBOX_THUMBNAIL_PREFIX.length());
            Bitmap bm = KanboxImageUtils.loadThumbnailFromDb(uri);
            if (bm == null) {
                bm = KanboxImageUtils.downloadThumbnailFromServer(uri);
                if (bm != null) {
                    KanboxImageUtils.saveThumbnailIntoDb(uri, bm);
                }
            }
            return bm;
        } else {
            return null;
        }
    }


}
