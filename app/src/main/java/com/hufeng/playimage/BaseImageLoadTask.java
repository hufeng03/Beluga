package com.hufeng.playimage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class BaseImageLoadTask implements Runnable {

    static final String TAG = "ImageLoadTask";

    public String targetUri;

    public static final int STATE_IDLE = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISH = 2;

    private int mState;
    private Handler mHandler;
    private boolean mIsCanncelled;

    public boolean isFinished() {
        return mState == STATE_FINISH;
    }

    public BaseImageLoadTask(Handler handler, String uri) {
        mState = STATE_IDLE;
        targetUri = uri;
        mHandler = handler;
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
            bitmap = load(targetUri);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, e.toString());
            System.gc();
        }

        if (bitmap != null && !mIsCanncelled) {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("uri", targetUri);
            msg.setData(bundle);
            msg.obj = bitmap;
            msg.what = ImageLoader.BITMAP_LOADED;
            mHandler.sendMessage(msg);
        } else if (bitmap != null && mIsCanncelled) {
            bitmap.recycle();
            bitmap = null;
        }
        mState = STATE_FINISH;
    }

    protected abstract Bitmap load(String url);
}

