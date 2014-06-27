package com.hufeng.filemanager;

import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by feng on 2/24/2014.
 */
public class KanboxWebActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        WebView web = new WebView(this);
        setContentView(web);
        web.loadUrl("http://belugamobile.com/kanbox/");
    }
}
