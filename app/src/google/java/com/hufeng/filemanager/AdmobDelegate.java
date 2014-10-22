package com.hufeng.filemanager;

import android.app.Activity;
import android.location.Location;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.hufeng.filemanager.ad.LocationUtil;

public final class AdmobDelegate {
    public static final View showAd(Activity context, LinearLayout layout){
        if(Constants.SHOW_AD){
            AdView view = new AdView(context, AdSize.BANNER, "a15188f10d51d0e");
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            layout.addView(view, layoutParams);
            AdRequest request = new AdRequest();
            Location loc = LocationUtil.getLocation(context);
            if (loc != null) {
                request.setLocation(loc);
            }
            view.loadAd(request);
            return view;
        } else {
            return null;
        }
    }

    public static final void distroyAd(View view) {
        ((AdView)view).destroy();
    }
}