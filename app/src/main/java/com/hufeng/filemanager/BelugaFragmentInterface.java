package com.hufeng.filemanager;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.ui.BelugaActionController;

/**
 * Created by Feng Hu on 15-01-05.
 * <p/>
 * TODO: Add a class header comment.
 */
public interface BelugaFragmentInterface {
    public boolean onBackPressed();
    public BelugaActionController getActionController();

    public FileEntry[] getAllFiles();
//    public FileEntry getParentFile();
    public void refreshUI();
}
