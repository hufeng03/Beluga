package com.hufeng.filemanager;

import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by Feng Hu on 15-02-26.
 * <p/>
 * TODO: Add a class header comment.
 */
public final class BelugaFolderObserver extends FileObserver {

    private static final String TAG = "BelugaFolderObserver";

    public static final String HANDLER_MESSAGE_FILE_PATH_KEY = "filePath";

    private long mSendMessageDelay = 0;

    private String mFolderPath;
    private Handler mObserverHandler;
    public BelugaFolderObserver(String path, Handler handler) {
        super(path, DELETE | CREATE | MOVED_FROM | MOVED_TO);
        mFolderPath = path;
        mObserverHandler = handler;
    }

    public BelugaFolderObserver(String path, Handler handler, long delay) {
        super(path, DELETE | CREATE | MOVED_FROM | MOVED_TO);
        mFolderPath = path;
        mObserverHandler = handler;
        mSendMessageDelay = delay;
    }

    @Override
    public void onEvent(int event, final String name) {

        if (TextUtils.isEmpty(name)) {
            return;
        }

        final boolean moved_from = (event & MOVED_FROM) !=0;
        final boolean moved_to = (event & MOVED_TO) !=0;
        final boolean delete = (event & DELETE) !=0;
        final boolean create = (event & CREATE) !=0;

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.i(TAG, "main thread");
        } else {
            Log.i(TAG, "not main thread");
        }

        if (delete || moved_from) {
            Message message = mObserverHandler.obtainMessage(0);
            Bundle data = new Bundle();
            data.putString(HANDLER_MESSAGE_FILE_PATH_KEY, new File(mFolderPath, name).getAbsolutePath());
            message.setData(data);
            mObserverHandler.sendMessageDelayed(message, mSendMessageDelay);
        }

        if (create || moved_to) {
            Message message = mObserverHandler.obtainMessage(1);
            Bundle data = new Bundle();
            data.putString(HANDLER_MESSAGE_FILE_PATH_KEY, new File(mFolderPath, name).getAbsolutePath());
            message.setData(data);
            mObserverHandler.sendMessageDelayed(message, mSendMessageDelay);
        }

        boolean access = (event & ACCESS) != 0;
        boolean modify = (event & MODIFY) !=0;
        boolean attrib = (event & ATTRIB) !=0;
        boolean close_write = (event & CLOSE_WRITE) !=0;
        boolean close_nowrite = (event & CLOSE_NOWRITE) !=0;
        boolean open = (event & OPEN) !=0;
        boolean delete_self = (event & DELETE_SELF) !=0;
        boolean move_self = (event & MOVE_SELF) !=0;
        Log.i("BelugaFileObserver", name+" "+event+" "+" "+new File(mFolderPath, name).length()+" "
                        +"access("+access+")"
                        +"modify("+modify+")"
                        +"attrib("+attrib+")"
                        +"close_write("+close_write+")"
                        +"close_nowrite("+close_nowrite+")"
                        +"open("+open+")"
                        +"moved_from("+moved_from+")"
                        +"moved_to("+moved_to+")"
                        +"delete("+delete+")"
                        +"create("+create+")"
                        +"delete_self("+delete_self+")"
                        +"move_self("+move_self+")"
        );
    }
}
