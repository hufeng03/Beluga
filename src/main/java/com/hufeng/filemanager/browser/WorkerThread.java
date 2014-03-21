package com.hufeng.filemanager.browser;

import android.os.Process;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.utils.LogUtil;
import com.hufeng.filemanager.utils.OSUtil;

public class WorkerThread extends Thread {
    private static final String LOG_TAG = WorkerThread.class.getName();
    private boolean destroyed = false;
    private boolean paused = false;
    private int mThreadPriority = -1;
    final private Object lock = new Object();

    public WorkerThread() {
        this("worker-thread");
    }

    public WorkerThread(String name) {
        super(name);
    }
    
    public WorkerThread(int thread_priority){
    	super();
    	mThreadPriority = thread_priority;
    }

    public void destroySelf() {
        synchronized (lock) {
            this.destroyed = true;
        }
        resumeSelf();
    }

    public void pauseSelf() {
        this.paused = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isPaused() {
        return paused;
    }

    public void resumeSelf() {
        if (paused) {
            synchronized (lock) {
                lock.notify();
                this.paused = false;
            }
        }
    }

    protected void doPauseSelfIfNeeded() throws InterruptedException {
        synchronized (lock) {
            while (paused && !destroyed) {
            	if(LogUtil.WDBG)
            		LogUtil.w(LOG_TAG, "======>going to pause!");
                lock.wait();
                if(LogUtil.WDBG)
                	LogUtil.w(LOG_TAG, "======>resumed!");
            }
        }
    }

    @Override
    public void run() {
    	if(mThreadPriority!=-1)
    		Process.setThreadPriority(mThreadPriority);
    	else{
    		String name = OSUtil.getCurrProcessName(FileManager.getAppContext());
            String pkgName = FileManager.getAppContext().getPackageName();
            if (name.equalsIgnoreCase(pkgName)) {
            	Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
            }else{
          		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            }
    	}
          if(LogUtil.WDBG)
        	LogUtil.w(LOG_TAG, getName() + " thread running...");
        try {
            while (!destroyed) {
                doJob();
                doPauseSelfIfNeeded();
            }
        } catch (InterruptedException e) {
        	if(LogUtil.WDBG)
        		LogUtil.w(LOG_TAG, getName() + " thread interrupted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(LogUtil.WDBG)
        	LogUtil.w(LOG_TAG, getName() + " thread stoped.");
    }

    public void doJob() {
        // call pauseSelf() properly.
    }

}
