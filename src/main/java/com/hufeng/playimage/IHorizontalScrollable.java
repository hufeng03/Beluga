package com.hufeng.playimage;

/**
 * Created by feng on 14-1-19.
 */
import android.view.View;

public interface IHorizontalScrollable {
    boolean canScroll(View v, int dx, int x, int y);
}
