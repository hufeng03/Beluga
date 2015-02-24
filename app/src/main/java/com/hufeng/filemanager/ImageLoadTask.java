package com.hufeng.filemanager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Comparator;

/**
 * Created by Feng Hu on 15-02-04.
 * <p/>
 * TODO: Add a class header comment.
 */
public abstract class ImageLoadTask implements Runnable, Comparable<ImageLoadTask>{

    static final String TAG = "ImageLoadTask";

    public String targetUrl;

    public static final int STATE_IDLE = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISH = 2;

    private int mState;
    private Handler mHandler;
    private boolean mIsCanncelled;

    private final long mTime;

    public boolean isFinished() {
        return mState == STATE_FINISH;
    }

    public ImageLoadTask(Handler handler, String url) {
        mState = STATE_IDLE;
        targetUrl = url;
        mHandler = handler;
        mTime = System.currentTimeMillis();
    }

    public void cancel() {
        mIsCanncelled = true;
    }

    public void run() {
        if (mIsCanncelled) {
            mState = STATE_FINISH;
            return;
        }

        mState = STATE_RUNNING;
        Bitmap bitmap = null;
        try {
            bitmap = load(targetUrl);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, e.toString());
            System.gc();
        }

        if (mIsCanncelled) {
            if (bitmap != null) {
                bitmap.recycle();
            }
        } else {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("url", targetUrl);
            msg.setData(bundle);
            msg.obj = bitmap;
            msg.what = BelugaImageLoader.BITMAP_LOADED;
            mHandler.sendMessage(msg);
        }

        mState = STATE_FINISH;
    }

    protected abstract Bitmap load(String url);

    @Override
    public int compareTo(ImageLoadTask another) {
        return (int)(another.mTime - mTime);
    }
}

