package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileEntryFactory;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.data.FileListLoader;
import com.hufeng.filemanager.services.IUiImpl;
import com.hufeng.filemanager.services.UiServiceHelper;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.ui.FileArrayAdapter;
import com.hufeng.filemanager.ui.FileGridAdapterListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileBrowserFragment extends FileGridFragment implements LoaderManager.LoaderCallbacks<List<FileEntry>>,
        IUiImpl.UiCallback,
        FileGridAdapterListener{

    private static final String LOG_TAG = FileBrowserFragment.class.getSimpleName();

    private FileArrayAdapter mAdapter;

    private BROWSER_TYPE mBrowserType = BROWSER_TYPE.DEVICE;
    private String mRootDir; //if mBrowserType == BROWSER_TYPE.DEVICE
    private List<String> mInitDirList;
    private boolean mWorkWithTree;
    private String mSelectedDir = null;
    private int mSelectedPostion = -1;

    private static final int LOADER_ID_BROWSER_FILES = 201;

    public static final String ARGUMENT_BROWSER_TYPE = "browser_type";
    public static final String ARGUMENT_INIT_ROOT_DIR = "root_dir";
    public static final String ARGUMENT_INIT_DIR_LIST = "init_dir_list";

    public enum BROWSER_TYPE {
        DEVICE, FAVORITE, DOWNLOAD;

        public static BROWSER_TYPE valueOf(int value) {
            switch (value) {
                case 0:
                    return DEVICE;
                case 1:
                    return FAVORITE;
                case 2:
                    return DOWNLOAD;
                default:
                    return null;
            }
        }
    }

    public FileBrowserFragment(){
        super();
        mMenuId = R.menu.file_browser_fragment_menu;
        mCategory = FileUtils.FILE_TYPE_FILE;
    }

    private WeakReference<FileBrowserFragmentListener> mWeakListener = null;

    public static interface FileBrowserFragmentListener {
        public void onFileBrowserItemClick(View v, FileEntry entry);
        public void onFileBrowserItemSelect(View v, FileEntry entry);
        public void onFileBrowserItemClose(FileEntry entry);
    }

    public void setListener(FileBrowserFragmentListener listener) {
        mWeakListener = new WeakReference<FileBrowserFragmentListener>(listener);
    }

    public void workWithTree(boolean tree) {
        mWorkWithTree = tree;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "FileBrowserFragment onCreate  with menuId = "+mMenuId);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mBrowserType = BROWSER_TYPE.valueOf(arguments.getInt(ARGUMENT_BROWSER_TYPE));
            mRootDir = arguments.getString(ARGUMENT_INIT_ROOT_DIR);
            mInitDirList = arguments.getStringArrayList(ARGUMENT_INIT_DIR_LIST);
        }
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
        Log.i(LOG_TAG, "FileBrowserFragment onViewCreated");
        String empty_text = "";
        switch (mBrowserType) {
            case FAVORITE:
                empty_text = getResources().getString(R.string.empty_favorite);
                break;
            case DOWNLOAD:
                empty_text = getResources().getString(R.string.empty_download);
                break;
            case DEVICE:
                empty_text = getResources().getString(R.string.empty_device);
                break;
        }
        setEmptyText(empty_text);
        mAdapter = new FileArrayAdapter(getSherlockActivity(), getFileOperation());
        mAdapter.setFileGridAdapterListener(this);
        setGridAdapter(mAdapter);
        setGridShownNoAnimation(false);
        this.registerForContextMenu(getGridView());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    @Override
    public void onResume() {
        super.onResume();
        UiServiceHelper.getInstance().addCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UiServiceHelper.getInstance().removeCallback(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.unregisterForContextMenu(getGridView());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "FileBrowserFragment onActivityCreated");

        getLoaderManager().initLoader(LOADER_ID_BROWSER_FILES, null, this);
    }

    public boolean onBackPressed() {
        if (mRootDir != null) {
            if (mRootDir.equals("/")) {
                mInitDirList = null;
                mRootDir = null;
                showDir(null);
                return true;
            }
            String parent = new File(mRootDir).getParent();
            if(parent==null || parent.equals(mRootDir) || !new File(parent).canRead()) {
                return false;
            }
            if (mWeakListener != null) {
                FileBrowserFragmentListener listener = mWeakListener.get();
                if (listener != null) {
                    listener.onFileBrowserItemClose(new FileEntry(mRootDir));
                }
            }
            if( (BROWSER_TYPE.DEVICE == mBrowserType && StorageManager.getInstance(getActivity()).isStorage(mRootDir))
                || (mInitDirList != null && mInitDirList.contains(mRootDir)) ) {
                    mInitDirList = null;
                showDir(null);
            } else {
                showDir(parent);
            }
            return true;
        } else {
            if(getFileOperation().isMovingOrCopying()) {
                Toast.makeText(getActivity(), R.string.please_select_copy_or_cancel, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(getFileOperation().isMovingOrCopying()) {
            menu.clear();
            inflater.inflate(R.menu.file_browser_fragment_paste_menu,menu);
        } else {
            super.onCreateOptionsMenu(menu, inflater);

            MenuItem item_back = menu.findItem(R.id.menu_back);
            MenuItem item_create = menu.findItem(R.id.menu_create);

            if(mRootDir == null && BROWSER_TYPE.DEVICE == mBrowserType) {
                item_back.setVisible(false);
            } else {
                item_back.setVisible(true);
            }

            if(mRootDir == null) {
                item_create.setVisible(false);
            } else {
                item_create.setVisible(true);
            }
        }
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        FileEntry entry = (FileEntry)g.getAdapter().getItem(position);
        Log.i(LOG_TAG, "onItemClick " + entry.toString());

        if (mWeakListener != null) {
            FileBrowserFragmentListener listener = mWeakListener.get();
            if (listener != null) {
               ImageView v_img =  (ImageView)v.findViewById(R.id.icon);
               listener.onFileBrowserItemClick(v_img, FileEntryFactory.makeFileObject(entry.path));
            }
        }
    }

    @Override
    public void onGridItemSelect(GridView g, View v, int position, long id) {
        FileEntry entry = (FileEntry)g.getAdapter().getItem(position);
        Log.i(LOG_TAG, "onItemSelect " + entry.toString());

        if (mWeakListener != null) {
            FileBrowserFragmentListener listener = mWeakListener.get();
            if (listener != null) {
                ImageView v_img =  (ImageView)v.findViewById(R.id.icon);
                listener.onFileBrowserItemSelect(v_img, FileEntryFactory.makeFileObject(entry.path));
            }
        }
    }

    @Override
    public String getSearchString() {
        return mSearchString;
    }

    @Override
    public void reloadFiles() {
        Log.i(LOG_TAG, "reloadFiles");
        getLoaderManager().restartLoader(LOADER_ID_BROWSER_FILES, null, this);
    }

    @Override
    public String getParentFile() {
        return mRootDir;
    }

    @Override
    public void refreshUI() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public String[] getAllFiles() {
        return mAdapter.getAllFiles();
    }

//    public void showRootDirs(){
//        mRootDir = null;
//        mSelectedDir = null;
//        reloadFiles();
//        getSherlockActivity().invalidateOptionsMenu();
//    }

    public void showDir(String path) {
        if (mRootDir != null && (path == null || mRootDir.startsWith(path))) {
            // if we are backing from child directory,
            // I have to scroll list view to correct place
            mSelectedDir = mRootDir;
        } else {
            mSelectedDir = null;
        }
        mRootDir = path;
        reloadFiles();
        getSherlockActivity().invalidateOptionsMenu();
    }

    @Override
	public Loader<List<FileEntry>> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "FileBrowserFragment onCreateLoader " + arg0);
        if(arg0 ==  LOADER_ID_BROWSER_FILES) {
            return new FileListLoader(getActivity(), mBrowserType, mRootDir, mInitDirList, mSearchString);
        } else {
            return null;
        }
	}

	@Override
	public void onLoadFinished(Loader<List<FileEntry>> arg0,
			List<FileEntry> arg1) {
        Log.i(LOG_TAG, "onLoadFinished with length =  " + (arg1 == null ? 0 : arg1.size()));

        UiServiceHelper.getInstance().clearMonitor();

        boolean flag_init = false;
        if (mInitDirList == null) {
            mInitDirList = new ArrayList<String>();
            flag_init = true;
        }

        Iterator<FileEntry> iterator =  arg1.iterator();
        int pos = 0;
        while (iterator.hasNext()) {
            FileEntry entry = iterator.next();
            UiServiceHelper.getInstance().addMonitor(entry.path);
            if (flag_init) {
                mInitDirList.add(entry.path);
            }
            if (mSelectedPostion == -1 && mSelectedDir != null && entry.path.equals(mSelectedDir)) {
                mSelectedPostion = pos;
            }
            pos++;
        }

        if (!TextUtils.isEmpty(mRootDir)) {
            UiServiceHelper.getInstance().addMonitor(mRootDir);
        }

//        mAdapter.setData(arg1);
        mAdapter.addAll(arg1);
        if ( mSelectedPostion >5 ) {
            getGridView().setSelection(mSelectedPostion);
        }

        mSelectedDir = null;
        mSelectedPostion = -1;
        setGridShown(true);
        if (mWorkWithTree && BROWSER_TYPE.DEVICE == mBrowserType && mRootDir == null) {
            setRootShown(false);
        } else {
            setRootShown(true);
        }
	}

	@Override
	public void onLoaderReset(Loader<List<FileEntry>> arg0) {
        mAdapter.clear();
	}


    @Override
    public void scanStarted() {

    }

    @Override
    public void scanCompleted() {

    }

    @Override
    public void changeMonitored(String dir) {
        //reloadFiles();
        mSelectedPostion = getGridView().getFirstVisiblePosition();
        reloadFiles();
    }
}
