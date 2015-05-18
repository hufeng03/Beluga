package com.belugamobile.filemanager.services;

/**
 * Created by Feng on 2015-05-12.
 */
public abstract class IWebServiceTask implements Runnable{

    public static final int STATE_IDLE = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISH = 2;

    private int mState;
    private boolean mIsCancelled;


    public IWebServiceTask() {
        mState = STATE_IDLE;
    }

    public void cancel() {
        mIsCancelled = true;
    }

    public void run() {
        if (mIsCancelled) {
            mState = STATE_FINISH;
            return;
        }

        mState = STATE_RUNNING;


    }


    protected abstract void work(String command);

}
