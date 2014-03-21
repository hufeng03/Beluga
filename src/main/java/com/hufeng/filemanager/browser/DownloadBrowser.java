package com.hufeng.filemanager.browser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.hufeng.filemanager.R;

public class DownloadBrowser {

	private Context mContext;
	private View mView;
	
    public DownloadBrowser(Context context) {
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.download_browser, null);
    }
}
