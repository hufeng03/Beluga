package com.hufeng.filemanager.safebox;

import android.graphics.drawable.Drawable;

/**
 * Created by feng on 13-10-3.
 */
public class SafeCategoryEntry {
    public String name;
    public Drawable icon;
    public int category;

    public SafeCategoryEntry(String category_name, Drawable category_icon, int category_type) {
        name = category_name;
        icon = category_icon;
        category = category_type;
    }
}
