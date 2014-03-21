package com.hufeng.filemanager;

import android.widget.ListAdapter;

/**
 * Created by feng on 13-9-10.
 */
public interface GridAdapter extends ListAdapter {
    abstract void changeDisplayMode(GridFragment.DISPLAY_MODE mode);
    abstract GridFragment.DISPLAY_MODE getDisplayMode();
}
