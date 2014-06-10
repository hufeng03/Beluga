package com.hufeng.filemanager;

/**
 * Created by feng on 14-5-19.
 */
public class LabelItem extends DrawerItem{

    private String mLabelName;

    public LabelItem(String name) {
        mLabelName = name;
    }

    @Override
    void render(NavigationDrawerItem layout) {
        layout.setText(mLabelName);
        layout.setAsLabel(true, 0);
    }

    @Override
    void work(FileDrawerActivity activity) {

    }
}
