package com.hufeng.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.helper.BelugaSortHelper;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.data.FileBrowserLoader;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;
import com.hufeng.filemanager.ui.BelugaActionController;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FileBrowserFragment extends FileRecyclerFragment implements LoaderManager.LoaderCallbacks<List<FileEntry>>,
        BelugaEntryViewHolder.EntryClickListener{

    private static final String TAG = FileBrowserFragment.class.getSimpleName();

    BelugaArrayRecyclerAdapter<FileEntry, FileEntryListViewHolder> mAdapter;


    private String mRootDir;
    private String mSelectedDir = null;
    private int mSelectedPostion = -1;

    private static final int LOADER_ID = 1;

    public static final String ARGUMENT_BROWSER_ROOT_FOLDER = "browser_root_folder";
    public static final String ARGUMENT_BROWSER_ROOT_FILE_LIST = "browser_root_file_list";

    private static final String SAVE_INSTANCE_KEY_ROOT_FOLDER = "rootFolder";

    public static FileBrowserFragment newRootFolderBrowser(String rootFolder) {
        if (TextUtils.isEmpty(rootFolder)) {
            return null;
        }
        FileBrowserFragment fragment = new FileBrowserFragment();
        Bundle data = new Bundle();
        data.putString(ARGUMENT_BROWSER_ROOT_FOLDER, rootFolder);
        fragment.setArguments(data);
        return fragment;
    }

    public static FileBrowserFragment newRootFileListBrowser(String[] fileList) {
        if (fileList == null) {
            return null;
        }
        FileBrowserFragment fragment = new FileBrowserFragment();
        Bundle data = new Bundle();
        data.putStringArray(ARGUMENT_BROWSER_ROOT_FILE_LIST, fileList);
        fragment.setArguments(data);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (savedInstanceState == null) {
            if (arguments != null) {
                mRootDir = arguments.getString(ARGUMENT_BROWSER_ROOT_FOLDER);
            }
        } else {
            mRootDir = savedInstanceState.getString(SAVE_INSTANCE_KEY_ROOT_FOLDER, null);
        }
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

        enableFloatingActionButton();
        mFab.hide(false);

        final String empty_text = getResources().getString(R.string.empty_file);
        setEmptyText(empty_text);

        mAdapter = new BelugaArrayRecyclerAdapter<FileEntry, FileEntryListViewHolder>(
                getActivity(),
                BelugaDisplayMode.LIST,
                new BelugaEntryViewHolder.Builder() {
                    @Override
                    public BelugaEntryViewHolder createViewHolder(ViewGroup parent, int type) {

                        if (type == BelugaDisplayMode.GRID.ordinal()) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_grid_row, parent, false);
                            return new FileEntryGridViewHolder(view, getActionController(), FileBrowserFragment.this);
                        } else {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_list_row, parent, false);
                            return new FileEntryListViewHolder(view, getActionController(), FileBrowserFragment.this);
                        }

                    }
                });

        setRecyclerAdapter(mAdapter);

        setEmptyViewShown(false);
        setListShownNoAnimation(false);

        if (isPasteMode()) {
            setFloatingActionBarImage(R.drawable.ic_paste);
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mRootDir)) {
            outState.putString(SAVE_INSTANCE_KEY_ROOT_FOLDER, mRootDir);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    // Floating Action Bar Click Listener Callback
    @Override
    protected void onCreate() {
        BelugaActionController.OPERATION_MODE operationMode = getActionController().getOperationMode();
        if (operationMode == BelugaActionController.OPERATION_MODE.COPY_PASTE) {
            BelugaDialogFragment.showCopyPasteDialog(getActivity(), mRootDir, getActionController().getAllActionFiles());
        } else if (operationMode == BelugaActionController.OPERATION_MODE.CUT_PASTE) {
            BelugaDialogFragment.showCutPasteDialog(getActivity(), mRootDir, getActionController().getAllActionFiles());
        } else {
            BelugaDialogFragment.showCreateFolderDialog(getActivity(), mRootDir);
        }
    }


    private boolean isPasteMode() {
        final BelugaActionController.OPERATION_MODE mode = getActionController().getOperationMode();
        return mode == BelugaActionController.OPERATION_MODE.COPY_PASTE
                || mode == BelugaActionController.OPERATION_MODE.CUT_PASTE;
    }

    public boolean onBackPressed() {
        if (mRootDir != null) {
            String[] initDirs = getArguments().getStringArray(ARGUMENT_BROWSER_ROOT_FILE_LIST);
            if (initDirs != null) {
                for (String dir: initDirs) {
                    if (mRootDir.equals(dir)) {
                        showDir(null);
                        return true;
                    }
                }
            }

            String initDir = getArguments().getString(ARGUMENT_BROWSER_ROOT_FOLDER);
            if (!TextUtils.isEmpty(initDir)) {
                if (mRootDir.equals(initDir)) {
                    return false;
                }
            }

            String parent = new File(mRootDir).getParent();
            if (!TextUtils.isEmpty(parent) && new File(parent).canRead()) {
                showDir(parent);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.file_browser_fragment_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem displayMenu = menu.findItem(R.id.menu_browser_display);
        final MenuItem sortMenu = menu.findItem(R.id.menu_browser_sort);
        final MenuItem upMenu = menu.findItem(R.id.menu_up);

        final Fragment parentFragment = getParentFragment();
        boolean isFragmentVisible = true;
        if(parentFragment != null && (parentFragment instanceof FileTabFragment)) {
            isFragmentVisible = parentFragment.getUserVisibleHint();
        }
        final Activity parentActivity = getActivity();
        boolean isSearchMode = false;
        if (parentActivity != null && (parentActivity instanceof BelugaDrawerActivity)) {
            isSearchMode = ((BelugaDrawerActivity)getActivity()).isSearchMode();
        }

        final boolean menuVisible = isFragmentVisible && !isSearchMode;

        displayMenu.setVisible(menuVisible);
        sortMenu.setVisible(menuVisible);
        upMenu.setVisible(menuVisible && !TextUtils.isEmpty(mRootDir));

        displayMenu.setIcon(getDisplayMode() == BelugaDisplayMode.LIST ?
                R.drawable.ic_action_view_as_grid : R.drawable.ic_action_view_as_list);

        if (!TextUtils.isEmpty(mRootDir) && new File(mRootDir).canWrite()) {
            mFab.show();
        } else {
            mFab.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_browser_display:
                switchDisplay();
                getActivity().supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_browser_sort:
                BelugaDialogFragment.showSortDialog(getActivity(), CategorySelectEvent.CategoryType.NONE);
                return true;
            case R.id.menu_up:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getRootFolder() {
        return mRootDir;
    }

    @Override
    public void refreshUI() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (isPasteMode() && getActionController().getFileSelectedSize() == 0) {
            mFab.hide(true);
            disableFloatingActionButton();
        }
    }

    @Override
    public FileEntry[] getAllFiles() {
        return mAdapter.getAll();
    }

    public void showDir(String path) {
        mSelectedDir = mRootDir;
        mRootDir = path;
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
	public Loader<List<FileEntry>> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        if(arg0 ==  LOADER_ID) {
            String[] initDirs = getArguments().getStringArray(ARGUMENT_BROWSER_ROOT_FILE_LIST);
            return new FileBrowserLoader(getActivity(), mRootDir, initDirs, mSearchString);
        } else {
            return null;
        }
	}

	@Override
	public void onLoadFinished(Loader<List<FileEntry>> arg0,
			List<FileEntry> arg1) {
        Log.i(TAG, "onLoadFinished");
        Iterator<FileEntry> iterator =  arg1.iterator();
        int pos = 0;
        while (iterator.hasNext()) {
            FileEntry entry = iterator.next();
            if (mSelectedPostion == -1 && !TextUtils.isEmpty(mSelectedDir) && entry.path.equals(mSelectedDir)) {
                mSelectedPostion = pos;
            }
            pos++;
        }

        if (!TextUtils.isEmpty(mRootDir)) {
            mObserverHandlerThread = new HandlerThread("BelugaFolderObserver");
            mObserverHandlerThread.start();
            mObserverHandler = new ObserverHandler(mObserverHandlerThread.getLooper());
            mFolderObserver = new BelugaFolderObserver(mRootDir, mObserverHandler, 500);
            mFolderObserver.startWatching();
        }

        mAdapter.setData(arg1);
        //TODO: recover this
        if ( mSelectedPostion > 5 ) {
            getLayoutManager().scrollToPosition(mSelectedPostion);
        }

        mSelectedDir = null;
        mSelectedPostion = -1;
        setRecyclerViewShown(true);
        setEmptyViewShown(arg1.size()==0);
	}

	@Override
	public void onLoaderReset(Loader<List<FileEntry>> arg0) {
        Log.i(TAG, "onCreateReset");
        mAdapter.clear();

        if (mFolderObserver != null) {
            mFolderObserver.stopWatching();
            mFolderObserver = null;
        }

        if (mObserverHandlerThread != null) {
            mObserverHandlerThread.quit();
            mObserverHandlerThread = null;
            mObserverHandler = null;
        }
	}

    @Override
    public void onEntryClickedToOpen(View view, BelugaEntry entry) {
        FileEntry fileEntry = (FileEntry)entry;
        if (fileEntry.isDirectory) {
            showDir(fileEntry.path);
        } else {
            BelugaActionDelegate.view(getActivity(), fileEntry);
        }
    }

    private HandlerThread mObserverHandlerThread;
    private BelugaFolderObserver mFolderObserver;
    private ObserverHandler mObserverHandler;
    private static final String HANDLER_MESSAGE_FILE_ENTRY_KEY = "fileEntry";
    private static final String HANDLER_MESSAGE_POSITION_KEY = "position";

    private class ObserverHandler extends Handler {

        public ObserverHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                {
                    String path = msg.getData().getString(BelugaFolderObserver.HANDLER_MESSAGE_FILE_PATH_KEY);
                    if (TextUtils.isEmpty(path) || new File(path).exists()) {
                        return;
                    }
                    FileEntry oldEntry = new FileEntry(path);
                    msg.getData().putParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY, oldEntry);
                    msg.setTarget(mUIThreadHandler);
                    mUIThreadHandler.sendMessage(msg);
                }
                    break;
                case 1:
                {
                    String path = msg.getData().getString(BelugaFolderObserver.HANDLER_MESSAGE_FILE_PATH_KEY);
                    if (TextUtils.isEmpty(path) || !new File(path).exists()) {
                        return;
                    }
                    FileEntry newEntry = new FileEntry(path);
                    BelugaSortHelper.SORTER sorter = BelugaSortHelper.getFileSorter(getActivity(), CategorySelectEvent.CategoryType.NONE);
                    Comparator<BelugaSortableInterface> comparator = BelugaSortHelper.getComparator(sorter.field, sorter.order);
                    int pos = 0;
                    for (FileEntry entry : mAdapter.getAll()) {
                        if (comparator.compare(entry, newEntry) >= 0) {
                            break;
                        }
                        pos++;
                    }
                    msg.getData().putParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY, newEntry);
                    msg.getData().putInt(HANDLER_MESSAGE_POSITION_KEY, pos);
                    msg.setTarget(mUIThreadHandler);
                    mUIThreadHandler.sendMessage(msg);
                }
                    break;
            }
        }
    };

    private Handler mUIThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                {
                    FileEntry oldEntry = msg.getData().getParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY);
                    mAdapter.remove(oldEntry);
                    getActionController().removeSelection(oldEntry);
                }
                    break;
                case 1:
                {
                    FileEntry newEntry = msg.getData().getParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY);
                    int pos = msg.getData().getInt(HANDLER_MESSAGE_POSITION_KEY, 0);
                    mAdapter.add(newEntry, pos);
                    getLayoutManager().scrollToPosition(pos);
                }
                    break;
            }
        }
    };

}
