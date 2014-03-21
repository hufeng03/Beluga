package com.hufeng.playimage;

import android.os.Handler;

/**
 * Created by feng on 14-2-3.
 */
public class ImageLoadTaskFactory {

    public static BaseImageLoadTask newImageLoadTask(Handler handler, String uri) {
        if (uri.startsWith(ImageLoader.KANBOX_PREFIX) || uri.startsWith(ImageLoader.KANBOX_THUMBNAIL_PREFIX)) {
            return new KanboxImageLoadTask(handler, uri);
        } else {
            return new LocalImageLoadTask(handler, uri);
        }
    }

}
