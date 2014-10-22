package com.hufeng.filemanager;

/**
 * Created by feng on 14-5-6.
 */
public class SearchFragment extends FileGridFragment {




    @Override
    public String getParentFile() {
        return null;
    }

    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public void reloadFiles() {

    }
}
