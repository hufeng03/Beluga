package com.belugamobile.filemanager.services;

import android.os.Handler;

import com.belugamobile.filemanager.helper.FileCategoryHelper;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.fabric.sdk.android.services.concurrency.PriorityThreadPoolExecutor;

/**
 * Created by Feng on 2015-05-12.
 */
public class IWebServiceTaskManager {

    public static IWebServiceTaskManager instance;

    private final ThreadPoolExecutor mPoolExecutor;

    private final ConcurrentHashMap<String, WeakReference<IWebServiceTask>> mTasks;

    private final Handler mHandler;

    public synchronized static IWebServiceTaskManager getInstance(Handler handler) {
        if (instance == null) {
            instance = new IWebServiceTaskManager(handler);
        }
        return instance;
    }

    private IWebServiceTaskManager(Handler handler) {
        mHandler = handler;
        mPoolExecutor = ThreadPoolFactory.newThreadPoolExectutor();
        mTasks = new ConcurrentHashMap<String, WeakReference<IWebServiceTask>>();
    }


    public void process(String command) {

    }

    public void listCategory(int category) {

    }

    public void listFolder(String folder, int depth) {

    }

}


class ThreadPoolFactory {
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_ZIE = 5;
    private static final int KEEP_ALIVE = 60;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Loader #"+mCount.getAndIncrement());
            return t;
        }
    };

    private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingDeque<>(1024);

    public static ThreadPoolExecutor newThreadPoolExectutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_ZIE, KEEP_ALIVE,
                TimeUnit.SECONDS, sWorkQueue, sThreadFactory);
        return threadPoolExecutor;
    }

}