package com.hufeng.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.textservice.TextInfo;

import com.hufeng.filemanager.browser.BelugaSorter;
import com.hufeng.filemanager.browser.FileEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng Hu on 15-02-21.
 * <p/>
 * TODO: Add a class header comment.
 */
public class DownloadFolderObserver {

    final Loader mLoader;

    List<BelugaFolderDownloadObserver> mFolderObserverList = new ArrayList<BelugaFolderDownloadObserver>();
    public DownloadFolderObserver(Loader loader) {
        mLoader = loader;
    }

    public void register(String path) {
        if (!TextUtils.isEmpty(path)) {
            BelugaFolderDownloadObserver fileObserver = new BelugaFolderDownloadObserver(path);
            fileObserver.startWatching();
            mFolderObserverList.add(fileObserver);
        }
    }

    public void dismiss() {
        for (BelugaFolderDownloadObserver downloadObserver : mFolderObserverList) {
            downloadObserver.stopWatching();
        }
        mFolderObserverList.clear();
    }

    private class BelugaFolderDownloadObserver extends FileObserver {
        private String mFolderPath;

        public BelugaFolderDownloadObserver(String path) {
            super(path, DELETE | CREATE | MOVED_FROM | MOVED_TO);
            mFolderPath = path;
        }

        @Override
        public void onEvent(int event, String name) {
            if (TextUtils.isEmpty(name) || !name.toLowerCase().contains("download")) {
                return;
            }
            final boolean moved_from = (event & MOVED_FROM) !=0;
            final boolean moved_to = (event & MOVED_TO) !=0;
            final boolean delete = (event & DELETE) !=0;
            final boolean create = (event & CREATE) !=0;

            if ((delete || moved_from) && !new File(mFolderPath, name).exists()) {
                mLoader.onContentChanged();
            }

            if ((create || moved_to) && new File(mFolderPath, name).exists()) {
                mLoader.onContentChanged();
            }

            boolean access = (event & ACCESS) != 0;
            boolean modify = (event & MODIFY) !=0;
            boolean attrib = (event & ATTRIB) !=0;
            boolean close_write = (event & CLOSE_WRITE) !=0;
            boolean close_nowrite = (event & CLOSE_NOWRITE) !=0;
            boolean open = (event & OPEN) !=0;
            boolean delete_self = (event & DELETE_SELF) !=0;
            boolean move_self = (event & MOVE_SELF) !=0;
            Log.i("BelugaFileObserver", name+" "+event+" "
                            +"access("+access+")"
                            +"modify("+modify+")"
                            +"attrib("+attrib+")"
                            +"close_write("+close_write+")"
                            +"close_nowrite("+close_nowrite+")"
                            +"open("+open+")"
                            +"moved_from("+moved_from+")"
                            +"moved_to("+moved_to+")"
                            +"delete("+delete+")"
                            +"create("+create+")"
                            +"delete_self("+delete_self+")"
                            +"move_self("+move_self+")"
            );
        }
    }
}
