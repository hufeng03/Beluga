package com.hufeng.playzip;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.hufeng.filemanager.TreeFragment;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.treeview.InMemoryTreeStateManager;
import com.hufeng.filemanager.treeview.TreeBuilder;
import com.hufeng.filemanager.treeview.TreeNodeInfo;
import com.hufeng.filemanager.treeview.TreeViewList;
import com.hufeng.filemanager.ui.FileTreeAdapter;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;

/**
 * Created by feng on 2014-03-24.
 */
public class ZipTreeFragment  extends TreeFragment implements LoaderManager.LoaderCallbacks<InMemoryTreeStateManager<String>> {

    private static final String TAG = ZipTreeFragment.class.getSimpleName();

    private static final String ARGUMNET_ZIP_FILE = "zip_file";

    private static final int LOADER_ID_ZIP_TREE = 502;

    private InMemoryTreeStateManager<String> mTreeManager;
    private FileTreeAdapter mAdapter;

    private String mZipFile;

    public static ZipTreeFragment newZipBrowser(String dir) {
        ZipTreeFragment fragment = new ZipTreeFragment();
        Bundle data = new Bundle();
        String[] files = FileUtils.getStorageDirs();
        if (!TextUtils.isEmpty(dir)) {
            data.putString(ARGUMNET_ZIP_FILE, dir);
        }
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mZipFile = getArguments().getString(ARGUMNET_ZIP_FILE);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_ZIP_TREE, null, this);
    }

    @Override
    public void onTreeItemClick(TreeViewList parent, View view, int position, long id) {
        String path = (String)parent.getAdapter().getItem(position);
        FileEntry entry = new FileEntry(path);
        boolean collasp = false;
        if (entry.isDirectory()) {
            final TreeNodeInfo<String> info = mTreeManager.getNodeInfo(path);
            if(info.isWithChildren()){
//                if(!info.isExpanded()){
//                    //expand it
//
//                }else{
//                    //collasp it
//                    collasp = true;
//                }
                mAdapter.handleItemClick(view, path);
            }else{
                //expand itself
                mAdapter.handleItemClick(view, path);
            }
        } else {
            //open file
            mAdapter.handleItemClick(view, path);
        }
    }

    @Override
    public Loader<InMemoryTreeStateManager<String>> onCreateLoader(int id, Bundle args) {
        return new ZipTreeLoader(getActivity(), mZipFile);
    }

    @Override
    public void onLoadFinished(Loader<InMemoryTreeStateManager<String>> loader, InMemoryTreeStateManager<String> data) {
        mTreeManager = data;
        mAdapter = new FileTreeAdapter(getSherlockActivity(), mTreeManager, 1);
        setTreeAdapter(mAdapter);
        setTreeShown(true);
    }

    @Override
    public void onLoaderReset(Loader<InMemoryTreeStateManager<String>> loader) {

    }

    public static class ZipTreeLoader extends AsyncTaskLoader<InMemoryTreeStateManager<String>> {

        InMemoryTreeStateManager<String> mTreeManager;
        String mZipFile;

        public ZipTreeLoader(Context context, String zip) {
            super(context);
            mZipFile = zip;
        }

        @Override
        public InMemoryTreeStateManager<String> loadInBackground() {
            ArrayList<FileEntry> files = new ArrayList<FileEntry>();
            try {
                //生成一个zip的文件
                ZipFile zipFile = new ZipFile(mZipFile);
                //遍历zipFile中所有的实体，并把他们解压出来
                for (@SuppressWarnings("unchecked")
                     Enumeration<ZipEntry> entries = zipFile.getEntries(); entries
                             .hasMoreElements(); ) {
                    ZipEntry entry = entries.nextElement();
                    //生成他们解压后的一个文件
                    String name = entry.getName();
                    FileEntry fe = new FileEntry(name);
                    fe.is_directory = entry.isDirectory();
                    fe.size = entry.getSize();
                    fe.lastModified = entry.getTime();
                    files.add(fe);
                }
            } catch (IOException e) {

            }
//            Collections.sort(files);
            FileSorter.SORTER sorter = FileSorter.getFileSorter(getContext(), FileUtils.FILE_TYPE_FILE);
            Collections.sort(files, new Comparator<FileEntry>() {
                @Override
                public int compare(FileEntry lhs, FileEntry rhs) {
                    if (lhs.path.startsWith(rhs.path)) {
                        return 1;
                    } else if (rhs.path.startsWith(lhs.path)) {
                        return -1;
                    } else {
                        return lhs.path.compareTo(rhs.path);
                    }
                }
            });

            InMemoryTreeStateManager<String> manager = new InMemoryTreeStateManager<String>();
            final TreeBuilder<String> treeBuilder = new TreeBuilder<String>(manager);
            //android.os.Debug.waitForDebugger();
            for (int i=0; i<files.size(); i++) {
                FileEntry entry = files.get(i);
                Log.i(TAG, "Unzip: "+entry.path+" "+entry.length()+" "+entry.lastModified);
                int len = entry.path.length();
                int depth = 0;
                for(int j=0; j < len; j++) {
                    if (j!=0 && j!=len-1 && entry.path.charAt(j)=='/') {
                        depth++;
                    }
                }
                if (entry.is_directory && !entry.path.endsWith("/")) {
                    treeBuilder.sequentiallyAddNextNode(entry.path+'/', depth);
                } else {
                    treeBuilder.sequentiallyAddNextNode(entry.path, depth);
                }
            }

            return manager;
        }

        @Override
        public void deliverResult(InMemoryTreeStateManager<String> data) {
            if (isReset()) {
                releaseResources(data);
            }

            InMemoryTreeStateManager<String> oldTreeManager = mTreeManager;
            mTreeManager = data;

            if (isStarted()) {
                super.deliverResult(data);
            }

            if (oldTreeManager != null && oldTreeManager != data) {
                releaseResources(oldTreeManager);
            }

            super.deliverResult(data);
        }

        @Override
        protected void onStartLoading() {
            if (mTreeManager != null) {
                deliverResult(mTreeManager);
            }

            if (takeContentChanged() || mTreeManager == null) {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        @Override
        protected void onReset() {
            onStopLoading();

            if (mTreeManager != null) {
                releaseResources(mTreeManager);
                mTreeManager = null;
            }
        }

        @Override
        public void onCanceled(InMemoryTreeStateManager<String> data) {
            super.onCanceled(data);
            releaseResources(data);
        }

        @Override
        public void forceLoad() {
            super.forceLoad();
        }

        private void releaseResources(InMemoryTreeStateManager<String> data) {
            // do nothing
            if (data != null) {
                data.clear();
            }
        }

    }
}
