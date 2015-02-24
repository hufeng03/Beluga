package com.hufeng.filemanager;

import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Feng Hu on 15-02-04.
 * <p/>
 * TODO: Add a class header comment.
 */
public class TaskManager {

    private static final int CORE_POOL_SIZE = 3;
    private static final int MAXIMUM_POOL_SIZE = 5;
    private static final int KEEP_ALIVE = 60;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "ImageLoader #" + mCount.getAndIncrement());
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    };

    private static final PriorityBlockingQueue<Runnable> sLoadingQueue = new PriorityBlockingQueue<Runnable>(
            1024);

    private final ThreadPoolExecutor mLoadingExcutor;
    private final ConcurrentHashMap<String, WeakReference<ImageLoadTask>> mLoadingUrls;

    Handler mHandler;


    public TaskManager(Handler handler) {
        mHandler = handler;
        mLoadingExcutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
                sLoadingQueue,
                sThreadFactory);
        mLoadingUrls = new ConcurrentHashMap<String, WeakReference<ImageLoadTask>>();
    }

    private boolean isUrlLoading(String url) {
        if (!mLoadingUrls.containsKey(url)) {
            return false;
        }

        final WeakReference<ImageLoadTask> taskWeakReference = mLoadingUrls.get(url);
        final ImageLoadTask task = taskWeakReference.get();
        if (task == null) {
            return false;
        } else if (task.isFinished()) {
            mLoadingUrls.remove(url);
            return false;
        } else {
            return true;
        }
    }

    public void startLoad(String url){
        if (isUrlLoading(url)) {
            return;
        }

        ImageLoadTask task = new LocalFileThumbnailLoadTask(mHandler, url);
        mLoadingExcutor.execute(task);
        mLoadingUrls.put(url, new WeakReference<ImageLoadTask>(task));
    }

    public void cancelAllTask() {
        Collection<WeakReference<ImageLoadTask>> tasksReference = mLoadingUrls
                .values();
        for (WeakReference<ImageLoadTask> taskReference : tasksReference) {
            ImageLoadTask task = taskReference.get();
            if (task != null) {
                task.cancel();
            }
        }
        mLoadingUrls.clear();
    }
}
