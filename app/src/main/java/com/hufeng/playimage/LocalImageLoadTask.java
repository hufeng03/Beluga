package com.hufeng.playimage;


import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.IconUtil;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;

public class LocalImageLoadTask extends BaseImageLoadTask {

	public LocalImageLoadTask(Handler handler, String uri) {
		super(handler, uri);
	}

	@Override
	protected Bitmap load(String uri) {
		if (LogUtil.DDBG) {
			Log.d("ImageDiskLoadTask", "load resource from disk" + uri);
		}
        boolean thumbnail = false;
        if (uri.startsWith(ImageLoader.THUMBNAIL_PREFIX)) {
            thumbnail = true;
        }
        if (thumbnail) {
            uri = uri.substring(ImageLoader.THUMBNAIL_PREFIX.length());
            File file = new File(uri);
            Bitmap bitmap = null;
            int category = FileUtils.getFileType(file);
            switch (category) {
                case FileUtils.FILE_TYPE_IMAGE:
                    bitmap = IconUtil.getImageThumbnail(uri);
                    break;
                case FileUtils.FILE_TYPE_AUDIO:
                    bitmap = IconUtil.getAudioThumbnail(uri);
                    break;
                case FileUtils.FILE_TYPE_VIDEO:
                    bitmap = IconUtil.getVideoThumbnail(uri);
                    break;
                case FileUtils.FILE_TYPE_APK:
                    bitmap = FileUtils.getApkThumbnail(uri);
                    break;
                default:
                    break;
            }
            return bitmap;
        } else {
		    return DiskBitmapLoadHelper.decodeSampledBitmapFromFile(uri);
        }
	}

}
