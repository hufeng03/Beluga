package com.hufeng.filemanager.utils;

import android.widget.AbsListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by feng on 2014-03-16.
 */
public class GridViewUtils {

    public static void stopScroll(AbsListView view)
    {
        if (view == null) {
            return;
        }
        try
        {
            Field field = android.widget.AbsListView.class.getDeclaredField("mFlingRunnable");
            field.setAccessible(true);
            Object flingRunnable = field.get(view);
            if (flingRunnable != null)
            {
                Method method = Class.forName("android.widget.AbsListView$FlingRunnable").getDeclaredMethod("endFling");
                method.setAccessible(true);
                method.invoke(flingRunnable);
            }
        }
        catch (Exception e) {}
    }
}
