package com.hufeng.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileEntryFactory;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.data.FileListLoader;
import com.hufeng.filemanager.provider.UiProvider;
import com.hufeng.filemanager.services.IUiImpl;
import com.hufeng.filemanager.services.UiServiceHelper;
import com.hufeng.filemanager.ui.FileArrayAdapter;
import com.hufeng.filemanager.ui.FileGridAdapterListener;
import com.hufeng.filemanager.ui.FileOperation;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

public class FileBrowserFragment extends FileGridFragment implements LoaderManager.LoaderCallbacks<List<FileEntry>>,
        IUiImpl.UiCallback,
        FileGridAdapterListener{

    public  static final String TAG = "FileBrowserFragment";

    public static final String FILE_BROWSER_ARGUMENT_SELECT = "file_browser_argumnet_select";

    private FileArrayAdapter mAdapter;

    private String mRootDir;
    private boolean mWorkWithTree;
    private String mSelectedDir = null;
    private int mSelectedPostion = -1;

    private static final int LOADER_ID_BROWSER_FILES = 201;

    public static final String ARGUMENT_INIT_ROOT_DIR = "init_root_dir";
    public static final String ARGUMENT_INIT_DIR_LIST = "init_dir_list";


    public static FileBrowserFragment newDownloadBrowser() {
        FileBrowserFragment fragement = new FileBrowserFragment();
        Bundle data = new Bundle();
        String[] files = UiProvider.getDownloadDirs();
        if (files != null && files.length > 0) {
            data.putStringArray(ARGUMENT_INIT_DIR_LIST, files);
        }
        fragement.setArguments(data);
        return fragement;
    }

    public static FileBrowserFragment newFavoriteBrowser() {
        FileBrowserFragment fragement = new FileBrowserFragment();
        Bundle data = new Bundle();
        String[] files = UiProvider.getFavoriteFiles();
        if (files != null && files.length > 0) {
            data.putStringArray(ARGUMENT_INIT_DIR_LIST, files);
        }
        fragement.setArguments(data);
        return fragement;
    }

    public static FileBrowserFragment newStorageBrowser(String root) {
        FileBrowserFragment fragment = new FileBrowserFragment();
        Bundle data = new Bundle();
        String[] files = UiProvider.getStorageDirs();
        if (files != null && files.length > 0) {
            data.putStringArray(ARGUMENT_INIT_DIR_LIST, files);
        }
        if (!TextUtils.isEmpty(root)) {
            data.putString(ARGUMENT_INIT_ROOT_DIR, root);
        }
        fragment.setArguments(data);
        return fragment;
    }

    public static FileBrowserFragment newSelectionBrowser() {
        FileBrowserFragment fragment = new FileBrowserFragment();
        Bundle data = new Bundle();
        data.putBoolean(FILE_BROWSER_ARGUMENT_SELECT, true);
        String[] files = UiProvider.getStorageDirs();
        if (files != null && files.length > 0) {
            data.putStringArray(ARGUMENT_INIT_DIR_LIST, files);
        }
        fragment.setArguments(data);
        return fragment;
    }

    public void setInitDirs(String[] dirs) {
        getArguments().putStringArray(ARGUMENT_INIT_DIR_LIST, dirs);
        reloadFiles();
    }


    public FileBrowserFragment(){
        mMenuId = R.menu.file_browser_fragment_menu;
        mCategory = FileUtils.FILE_TYPE_FILE;
    }

    private FileBrowserCallbacks mCallbacks;

    public static interface FileBrowserCallbacks {
        public void onFileBrowserItemClick(View v, FileEntry entry);
        public void onFileBrowserItemSelect(View v, FileEntry entry);
        public void onFileBrowserItemClose(FileEntry entry);
        public void onFileBrowserDirShown(String path);
    }


    public void workWithTree(boolean tree) {
        mWorkWithTree = tree;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments().getBoolean(FILE_BROWSER_ARGUMENT_SELECT)) {
            FileOperation fileOperation = (FileOperation) getChildFragmentManager().findFragmentByTag("FileBrowser-FileOperation");
            if (fileOperation == null) {
                fileOperation = FileOperation.newInstance(FileOperation.OPERATION_MODE.SELECT.ordinal());
                getChildFragmentManager().beginTransaction().add(fileOperation, "FileBrowser-FileOperation").commit();
            }
            setFileOperation(fileOperation);
        }
        if (getParentFragment() != null) {
            try {
                mCallbacks = (FileBrowserCallbacks)getParentFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
            }
        } else {
            try {
                mCallbacks = (FileBrowserCallbacks) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "FileBrowserFragment onCreate  with menuId = "+mMenuId);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mRootDir = arguments.getString(ARGUMENT_INIT_ROOT_DIR);
        }

        if (getActivity() instanceof FileDrawerActivity) {
            mMenuId = R.menu.file_browser_fragment_menu_for_drawer;
        } else {
            mMenuId = R.menu.file_browser_fragment_menu;
        }
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
        LogUtil.i(TAG, "FileBrowserFragment onViewCreated");
        String empty_text = getResources().getString(R.string.empty_file);
//        switch (mBrowserType) {
//            case FAVORITE:
//                empty_text = getResources().getString(R.string.empty_favorite);
//                break;
//            case DOWNLOAD:
//                empty_text = getResources().getString(R.string.empty_download);
//                break;
//            case DEVICE:
//                empty_text = getResources().getString(R.string.empty_device);
//                break;
//        }
        setEmptyText(empty_text);
        mAdapter = new FileArrayAdapter(getActivity(), getFileOperation());
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
        LogUtil.i(TAG, "FileBrowserFragment onActivityCreated");

        getLoaderManager().initLoader(LOADER_ID_BROWSER_FILES, null, this);
    }

    public boolean onBackPressed() {
        if (mRootDir != null) {
                if (mCallbacks != null) {
                    mCallbacks.onFileBrowserItemClose(new FileEntry(mRootDir));
                }
            String[] initDirs = getArguments().getStringArray(ARGUMENT_INIT_DIR_LIST);
            if (initDirs != null) {
                boolean flag_child = false, flag_equal = false;
                for (String dir: initDirs) {
                    if (mRootDir.startsWith(dir)) {
                        if (mRootDir.equals(dir)) {
                            flag_equal = true;
                        } else {
                            flag_child = true;
                        }
                    }
                }
                if (flag_equal && !flag_child) {
                    showDir(null);
                    return true;
                }
            }

            String parent = new File(mRootDir).getParent();
            if (parent != null && new File(parent).canRead()) {
                showDir(parent);
                return true;
            }
        } else {
            if(getFileOperation().isMovingOrCopying()) {
                Toast.makeText(getActivity(), R.string.please_select_copy_or_cancel, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(getFileOperation()!=null && getFileOperation().isMovingOrCopying()) {
            menu.clear();
            mMenuCreated = true;
            inflater.inflate(R.menu.file_browser_fragment_paste_menu,menu);
            MenuItem item_paste = menu.findItem(R.id.menu_paste_confirm);
            if (item_paste != null)
            if (mRootDir == null) {
                item_paste.setEnabled(false);
            } else {
                boolean can_write = new File(mRootDir).canWrite();
                if (can_write && Constants.TRY_TO_TEST_WRITE) {
                    if(new File(mRootDir, ".test_writable").mkdir()){
                        new File(mRootDir, ".test_writable").delete();
                    } else {
                        can_write = false;
                    }
                }
                if (!can_write) {
                    item_paste.setEnabled(false);
                } else {
                    item_paste.setEnabled(true);
                }
            }
        } else {
            super.onCreateOptionsMenu(menu, inflater);

            if (!mMenuCreated)
                return;

            MenuItem item_back = menu.findItem(R.id.menu_back);
            MenuItem item_create = menu.findItem(R.id.menu_create);
            MenuItem item_search = menu.findItem(R.id.menu_search);

            if(item_back != null) {
                if (mRootDir == null) {
                    item_back.setVisible(false);
                } else {
                    item_back.setVisible(true);
                }
            }

            if (item_search != null) {
                if (mRootDir == null) {
                    item_search.setVisible(false);
                } else {
                    item_search.setVisible(true);
                }
            }

            if (item_create != null) {
                if (mRootDir == null) {
                    item_create.setVisible(false);
                } else {
                    boolean can_write =  new File(mRootDir).canWrite();
                    if (can_write && Constants.TRY_TO_TEST_WRITE) {
                        if(new File(mRootDir, ".test_writable").mkdir()){
                            new File(mRootDir, ".test_writable").delete();
                        } else {
                            can_write = false;
                        }
                    }
                    if (!can_write) {
                        item_create.setVisible(false);
                    } else {
                        item_create.setVisible(true);
                    }
                }
            }
        }
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        super.onGridItemSelect(g,v,position,id);
        FileEntry entry = (FileEntry)g.getAdapter().getItem(position);

        LogUtil.i(TAG, "onItemClick " + entry.toString());

            if (mCallbacks != null) {
               ImageView v_img =  (ImageView)v.findViewById(R.id.icon);
               if (mFileOperation != null && mFileOperation.getFileSelectedSize() > 0) {
                   mCallbacks.onFileBrowserItemSelect(v_img, FileEntryFactory.makeFileObject(entry.path));
               } else {
                   mCallbacks.onFileBrowserItemClick(v_img, FileEntryFactory.makeFileObject(entry.path));
               }
            }
    }

    @Override
    public void onGridItemSelect(GridView g, View v, int position, long id) {
        super.onGridItemSelect(g,v,position,id);
        FileEntry entry = (FileEntry)g.getAdapter().getItem(position);
        LogUtil.i(TAG, "onItemSelect " + entry.toString());

            if (mCallbacks != null) {
                ImageView v_img =  (ImageView)v.findViewById(R.id.icon);
                mCallbacks.onFileBrowserItemSelect(v_img, FileEntryFactory.makeFileObject(entry.path));
            }
    }

    @Override
    public String getSearchString() {
        return mSearchString;
    }

    @Override
    public void reportNotExistFile() {
        mSelectedPostion = getGridView().getFirstVisiblePosition();
        reloadFiles();
    }

    @Override
    public void reloadFiles() {
        LogUtil.i(TAG, "reloadFiles");
        getLoaderManager().restartLoader(LOADER_ID_BROWSER_FILES, null, this);
    }

    @Override
    public String getParentFile() {
        return mRootDir;
    }

    @Override
    public void refreshUI() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public String[] getAllFiles() {
        return mAdapter.getAllFiles();
    }

    public void showDir(String path) {
        String[] initDirs = getArguments().getStringArray(ARGUMENT_INIT_DIR_LIST);
        if (path != null && initDirs != null) {
            boolean flag = false;
            for (String dir : initDirs) {
                if (path.startsWith(dir)) {
                    flag = true;
                }
            }
            if (!flag)
                path = null;
        }
        if (mRootDir != null && (path == null || mRootDir.startsWith(path))) {
            // if we are backing from child directory,
            // I have to scroll list view to correct place
            mSelectedDir = mRootDir;
        } else {
            mSelectedDir = null;
        }
        mRootDir = path;
        reloadFiles();
        getActivity().supportInvalidateOptionsMenu();
            if (mCallbacks != null) {
                mCallbacks.onFileBrowserDirShown(mRootDir);
            }
    }

    @Override
	public Loader<List<FileEntry>> onCreateLoader(int arg0, Bundle arg1) {
        LogUtil.i(TAG, "FileBrowserFragment onCreateLoader " + arg0);
        if(arg0 ==  LOADER_ID_BROWSER_FILES) {
            String[] initDirs = getArguments().getStringArray(ARGUMENT_INIT_DIR_LIST);
            return new FileListLoader(getActivity(), mRootDir, initDirs, mSearchString, mWorkWithTree);
        } else {
            return null;
        }
	}

	@Override
	public void onLoadFinished(Loader<List<FileEntry>> arg0,
			List<FileEntry> arg1) {
        LogUtil.i(TAG, "onLoadFinished with length =  " + (arg1 == null ? 0 : arg1.size()));

        UiServiceHelper.getInstance().clearMonitor();


        Iterator<FileEntry> iterator =  arg1.iterator();
        int pos = 0;
        while (iterator.hasNext()) {
            FileEntry entry = iterator.next();
            UiServiceHelper.getInstance().addMonitor(entry.path);
            if (mSelectedPostion == -1 && mSelectedDir != null && entry.path.equals(mSelectedDir)) {
                mSelectedPostion = pos;
            }
            pos++;
        }

        if (!TextUtils.isEmpty(mRootDir)) {
            UiServiceHelper.getInstance().addMonitor(mRootDir);
        }

//        mAdapter.setData(arg1);
        mAdapter.clear();
        mAdapter.addAll(arg1);
        if ( mSelectedPostion >5 ) {
            getGridView().setSelection(mSelectedPostion);
        }

        mSelectedDir = null;
        mSelectedPostion = -1;
        setGridShown(true);
        if (mWorkWithTree && mRootDir == null) {
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
