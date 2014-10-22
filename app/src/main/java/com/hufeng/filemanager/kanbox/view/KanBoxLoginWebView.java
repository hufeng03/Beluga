package com.hufeng.filemanager.kanbox.view;

import android.content.Context;
import android.os.Handler;
import android.webkit.WebView;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxLoginWebView extends WebView {

    private static final String TAG = KanBoxLoginWebView.class.getSimpleName();

    private Handler mHandler;

    public KanBoxLoginWebView(Context context) {
        super(context);
    }

}
