package com.belugamobile.filemanager;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

public final class UmengDelegate {
    public final static void umengAnalysisResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public final static void umengAnalysisPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public final static void forceUpdate(Context context) {
        UmengUpdateAgent.forceUpdate(context);
    }

    public final static void launchFeedback(Context context) {
        FeedbackAgent agent = new FeedbackAgent(context);
        agent.startFeedbackActivity();
    }

    public final static void update(Context context) {
        UmengUpdateAgent.update(context);
    }
}
