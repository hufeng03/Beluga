package com.hufeng.playimage;

import android.content.Context;
import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskManager {

    static final String TAG = "TaskManager";

    private final ThreadPoolExecutor mNetworkPoolExecutor;
    private final ThreadPoolExecutor mDiskPoolExecutor;
    private final ConcurrentHashMap<String, WeakReference<BaseImageLoadTask>> mLoadingUris;

    Handler mHandler;

    public TaskManager(Handler handler) {
        mHandler = handler;
        mNetworkPoolExecutor = ThreadPoolFactory.newNetworkPoolExecutor();
        mDiskPoolExecutor = ThreadPoolFactory.newDiskPoolExecutor();
        mLoadingUris = new ConcurrentHashMap<String, WeakReference<BaseImageLoadTask>>();
    }

    private boolean isUriLoading(String uri) {
        if (!mLoadingUris.containsKey(uri)) {
            return false;
        }

        final WeakReference<BaseImageLoadTask> taskReference = mLoadingUris
                .get(uri);
        final BaseImageLoadTask task = taskReference.get();
        if (task == null || task.isFinished()) {
            return false;
        }

        return true;
    }


    public void startLoad(Context context, String uri) {
        if (isUriLoading(uri)) {
            return;
        }

        BaseImageLoadTask task = null;
        task = new LocalImageLoadTask(mHandler, uri);
        if (task instanceof LocalImageLoadTask) {
            mDiskPoolExecutor.execute(task);
        } else {
            mNetworkPoolExecutor.execute(task);
        }
        mLoadingUris.put(uri, new WeakReference<BaseImageLoadTask>(task));
    }

    public void cancelAllTask() {
        Collection<WeakReference<BaseImageLoadTask>> tasksReference = mLoadingUris
                .values();
        for (WeakReference<BaseImageLoadTask> taskReference : tasksReference) {
            BaseImageLoadTask task = taskReference.get();
            if (task != null) {
                task.cancel();
            }
        }
    }

}

