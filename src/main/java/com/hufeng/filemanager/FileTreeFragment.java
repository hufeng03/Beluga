package com.hufeng.filemanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.treeview.InMemoryTreeStateManager;
import com.hufeng.filemanager.treeview.TreeBuilder;
import com.hufeng.filemanager.treeview.TreeNodeInfo;
import com.hufeng.filemanager.treeview.TreeViewList;
import com.hufeng.filemanager.ui.FileTreeAdapter;

import java.io.File;
import java.io.FileFilter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileTreeFragment extends TreeFragment implements LoaderManager.LoaderCallbacks<InMemoryTreeStateManager<String>>{

    private static final String TAG = FileTreeFragment.class.getSimpleName();

    private static final int LOADER_ID_TREE_FILES = 201;

    public static final String ARGUMENT_INIT_ROOT_DIR = "root_dir";

    private WeakReference<FileTreeFragmentListener> mWeakListener = null;

    private InMemoryTreeStateManager<String> mTreeManager;

    public static interface FileTreeFragmentListener {
        public void onFileTreeItemClick(FileEntry entry);
    }

    public void setListener(FileTreeFragmentListener listener) {
        mWeakListener = new WeakReference<FileTreeFragmentListener>(listener);
    }

    String mRootDir = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mRootDir = arguments.getString(ARGUMENT_INIT_ROOT_DIR);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String empty_text = getResources().getString(R.string.empty_device);
        setEmptyText(empty_text);
        setTreeShownNoAnimation(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( getLoaderManager().getLoader(LOADER_ID_TREE_FILES)==null)	{
            getLoaderManager().initLoader(LOADER_ID_TREE_FILES, null, this);
        }else{
            getLoaderManager().restartLoader(LOADER_ID_TREE_FILES, null, this);
        }
    }

    @Override
    public void onTreeItemClick(TreeViewList parent, View view, int position, long id) {
        String path = (String)parent.getAdapter().getItem(position);
        FileEntry entry = new FileEntry(path);
        if (entry.isDirectory()) {
            final TreeNodeInfo<String> info = mTreeManager.getNodeInfo(path);
            if(info.isWithChildren()){
                if(!info.isExpanded()){
                    //expand it
                    mTreeManager.removeChildNodesRecursively(info.getId());
                    addChildrenAndGrandchildren(info);
                }else{
                    //collasp it
                }
                mAdapter.handleItemClick(view, path);
            }else{
                //add child and grandchild, expand itself
                addChildrenAndGrandchildren(info);
                mAdapter.handleItemClick(view, path);
            }
        } else {
            //will not happen, currently
        }

        if (mWeakListener != null) {
            FileTreeFragmentListener listener = mWeakListener.get();
            if (listener != null) {
                listener.onFileTreeItemClick(entry);
            }
        }
    }

    private void addChildrenAndGrandchildren(TreeNodeInfo<String> root) {
        String root_id = root.getId();
        int root_level = root.getLevel();
        File[] file_children = new File(root_id).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (file_children != null) {
            Arrays.sort(file_children, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            for(File child:file_children) {
                String child_id = child.getAbsolutePath();
                mTreeManager.addAfterChild(root_id, child_id, null);
                File[] file_grandchildren = new File(child_id).listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.isDirectory()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                if (file_grandchildren != null) {
                    Arrays.sort(file_grandchildren, new Comparator<File>() {
                        @Override
                        public int compare(File lhs, File rhs) {
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    for(File grandchild:file_grandchildren) {
                        String grandchild_id = grandchild.getAbsolutePath();
                        mTreeManager.addAfterChild(child_id, grandchild_id, null);
                    }
                }
            }
        }
    }

    public void closeDir(String path) {
        if (path == null) {
            mTreeManager.collapseChildren(null);
        } else {
            if (mTreeManager.isInTree(path)) {
                mTreeManager.collapseChildren(path);
            }
        }
    }

    public void showDir(String path) {
        if(mTreeManager == null) {
            return;
        }
        if(path==null){
            mTreeManager.collapseChildren(null);
        }else{
            if(mTreeManager.isInTree(path)){
                final TreeNodeInfo<String> info = mTreeManager.getNodeInfo(path);
                if(info==null || !info.isExpanded()){
                   // refresh(path);
                    mAdapter.handleItemClick(null, path);
                }else{
                    mTreeManager.collapseChildren(path);
                    mTreeManager.expandDirectChildren(path);
                }
            }else{
//				if(new File(path).getParent())
//                String root = StorageManager.getInstance(mContext).getStoragePath(path);
//                String parent = path;
//                while(!parent.equals(root)){
//                    if(mTreeManager.isInTree(parent)){
//                        break;
//                    }else{
//                        parent = new File(parent).getAbsolutePath();
//                    }
//                }
//                refresh(parent);
//                if(mTreeManager.isInTree(path)){
//                    mAdapter.handleItemClick(null, path);
//                }
            }
//            int pos = mFileTreeAdapter.getFilePosition(path);
//            if(pos!=-1)
//                mList.setSelection(pos);
        }
    }

    @Override
    public Loader<InMemoryTreeStateManager<String>> onCreateLoader(int i, Bundle bundle) {
        return new FileTreeLoader(getActivity(), mRootDir);
    }

    @Override
    public void onLoadFinished(Loader<InMemoryTreeStateManager<String>> Loader, InMemoryTreeStateManager<String> treeManager) {
        mTreeManager = treeManager;
        mAdapter = new FileTreeAdapter(getSherlockActivity(), mTreeManager, 1);
        setTreeAdapter(mAdapter);
        setTreeShown(true);
    }

    @Override
    public void onLoaderReset(Loader<InMemoryTreeStateManager<String>> listLoader) {
        //mAdapter.setData(null);
    }


    /**
     * A custom Loader that loads all of the installed applications.
     */
    public static class FileTreeLoader extends AsyncTaskLoader<InMemoryTreeStateManager<String>> {

        InMemoryTreeStateManager<String> mTreeManager;
        String mRoot;

        public FileTreeLoader(Context context, String root) {
            super(context);
            mRoot = root;
        }

        @Override
        public InMemoryTreeStateManager<String> loadInBackground() {
            ArrayList<String> treeFiles = new ArrayList<String>();
            ArrayList<Integer> treeDepth = new ArrayList<Integer>();
            String[] files = StorageManager.getInstance(getContext()).getMountedStorages();
            if(files!=null && files.length!=0){
                for(String file:files) {
                    File f = new File(file);
                    int depth = 0;
                    if(f.exists()) {
                        treeFiles.add(file);
                        treeDepth.add(depth);
                        addChildrenAndGrandChildren(treeFiles, treeDepth, file, depth);
                    }
                }
            }

            InMemoryTreeStateManager<String> manager = new InMemoryTreeStateManager<String>();
            final TreeBuilder<String> treeBuilder = new TreeBuilder<String>(manager);
            for (int i = 0; i < treeFiles.size(); i++) {
                String path = treeFiles.get(i);
                if(!manager.isInTree(path)){
                    treeBuilder.sequentiallyAddNextNode(/*(long) i*/path, /*DEMO_NODES[i]*/treeDepth.get(i));
                }
            }

            treeFiles.clear();
            treeDepth.clear();

            Log.i(TAG, "Root Dir is :" + mRoot);
            if(!TextUtils.isEmpty(mRoot) && new File(mRoot).exists()) {
                if (!manager.isInTree(mRoot)) {
                    String id = mRoot;
                    do {
                        id = new File(id).getParent();
                    } while (!manager.isInTree(id));

                    addTillRootRecursively(id, mRoot);
                }
                final int level = manager.getLevel(mRoot);
                Log.i(TAG, "Root Dir level is :" + level);
                final String[] hierarchy = new String[level+1];

                int currentLevel = level;
                String current = mRoot;
                String parent = manager.getParent(current);
                while (currentLevel >= 0) {
                    hierarchy[currentLevel--] = current;
                    current = parent;
                    parent = manager.getParent(parent);
                }

                while(currentLevel<level) {
                    current = hierarchy[++currentLevel];
                    manager.expandDirectChildren(current);
                    Log.i(TAG, "expand direct children:" + current);
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
            if (mTreeManager != null ) {
                deliverResult(mTreeManager);
            }

            if(takeContentChanged() || mTreeManager == null) {
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

        private void addChildrenAndGrandChildren(ArrayList<String> treeFiles, ArrayList<Integer> treeDepth, String file, int depth){
            File[] child_files = new File(file).listFiles();
            if(child_files == null)
                return;
            Arrays.sort(child_files, FileSorter.getFileComparator(FileSorter.SORT_FIELD.NAME, FileSorter.SORT_ORDER.ASC));
            int flag_child = -1;
            for(File child_file : child_files){
                if(!child_file.isDirectory()){
                    continue;
                }
                if(flag_child == -1){
                    if(treeFiles.contains(child_file))
                        flag_child = 1;
                    else
                        flag_child = 0;
                }
                if(flag_child == 0){
                    treeFiles.add(child_file.getAbsolutePath());
                    treeDepth.add(depth+1);
                }
                if(child_file.isDirectory()){
                    File[] grand_files = child_file.listFiles();
                    if(grand_files == null) {
                        continue;
                    }
                    boolean flag_grand = true;
                    for(File grand_file:grand_files) {
                        if(!grand_file.isDirectory()){
                            continue;
                        }
                        if(flag_grand){
                            if(treeFiles.contains(grand_file))
                                break;
                            flag_grand = false;
                        }
                        treeFiles.add(grand_file.getAbsolutePath());
                        treeDepth.add(depth+2);
                    }
                }
            }
        }

        private void addTillRootRecursively(String id, String root) {
            File[] file_children = new File(id).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            if (file_children != null) {
                Arrays.sort(file_children, new Comparator<File>() {
                    @Override
                    public int compare(File lhs, File rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });

                for(File child:file_children) {
                    String child_id = child.getAbsolutePath();
                    mTreeManager.addAfterChild(id, child_id, null);
                    if (mRoot.startsWith(child.getAbsolutePath())) {
                       addTillRootRecursively(child.getAbsolutePath(), root);
                    }
                }
            }
        }


    }

}