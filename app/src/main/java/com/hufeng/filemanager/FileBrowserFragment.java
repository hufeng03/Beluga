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
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.hufeng.filemanager.browser.BelugaSorter;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.InfoLoader;
import com.hufeng.filemanager.data.FileBrowserLoader;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;
import com.hufeng.filemanager.services.IUiImpl;
import com.hufeng.filemanager.services.UiCallServiceHelper;
import com.hufeng.filemanager.ui.FileGridAdapterListener;
import com.hufeng.filemanager.ui.BelugaActionController;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileBrowserFragment extends FileRecyclerFragment implements LoaderManager.LoaderCallbacks<List<FileEntry>>,
        BelugaEntryViewHolder.EntryClickListener,
        FileGridAdapterListener{

    private static final String TAG = FileBrowserFragment.class.getSimpleName();

    BelugaArrayRecyclerAdapter<FileEntry, FileEntryListViewHolder> mAdapter;


    private String mRootDir;
    private String mSelectedDir = null;
    private int mSelectedPostion = -1;

    private static final int LOADER_ID = 1;

    public static final String ARGUMENT_BROWSER_ROOT_FOLDER = "browser_root_folder";
    public static final String ARGUMENT_BROWSER_ROOT_FILE_LIST = "browser_root_file_list";

    private static final String SAVE_INSTANCE_KEY_ROOT_FOLDER = "rootFolder";


    private BelugaFolderObserver mFolderObserver;

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
    public void reportNotExistFile() {
        //TODO: recover this
//        mSelectedPostion = getRecyclerView().getFirstVisiblePosition();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
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
            mFolderObserver = new BelugaFolderObserver(mRootDir);
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

    private static final String HANDLER_MESSAGE_FILE_ENTRY_KEY = "fileEntry";
    private static final String HANDLER_MESSAGE_POSITION_KEY = "position";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    FileEntry oldEntry = msg.getData().getParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY);
                    mAdapter.remove(oldEntry);
                    getActionController().removeSelection(oldEntry);
                    break;
                case 1:
                    FileEntry newEntry = msg.getData().getParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY);
                    int pos = msg.getData().getInt(HANDLER_MESSAGE_POSITION_KEY, 0);
                    mAdapter.add(newEntry, pos);
                    getLayoutManager().scrollToPosition(pos);
                    break;
            }
        }
    };

    private static final long FILE_OBSERVER_EVENT_DELAY = 1000;

    private class BelugaFolderObserver extends FileObserver {
        private String mFolderPath;
        private Handler mObserverHandler;
        private HandlerThread mObserverHandlerThread;

        public BelugaFolderObserver(String path) {
            super(path, DELETE | CREATE | MOVED_FROM | MOVED_TO);
            mObserverHandlerThread = new HandlerThread("TEST");
            mObserverHandlerThread.start();
            mObserverHandler = new Handler(mObserverHandlerThread.getLooper());
            mFolderPath = path;
        }

        @Override
        public void stopWatching() {
            super.stopWatching();
            mObserverHandlerThread.quit();
        }

        @Override
        public void onEvent(int event, final String name) {
            final boolean moved_from = (event & MOVED_FROM) !=0;
            final boolean moved_to = (event & MOVED_TO) !=0;
            final boolean delete = (event & DELETE) !=0;
            final boolean create = (event & CREATE) !=0;

            if (Looper.myLooper() == Looper.getMainLooper()) {
                Log.i(TAG, "main thread");
            } else {
                Log.i(TAG, "not main thread");
            }

            if ((delete || moved_from) && !new File(mFolderPath, name).exists()) {
                mObserverHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FileEntry oldEntry = new FileEntry(new File(mFolderPath, name));
                        Message message = mHandler.obtainMessage(0);
                        Bundle data = new Bundle();
                        data.putParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY, oldEntry);
                        message.setData(data);
                        mHandler.sendMessage(message);
                    }
                }, FILE_OBSERVER_EVENT_DELAY);
            }

            if ((create || moved_to) && new File(mFolderPath, name).exists()) {

                mObserverHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BelugaSorter.SORTER sorter = BelugaSorter.getFileSorter(getActivity(), CategorySelectEvent.CategoryType.NONE);
                        Comparator<BelugaSortableInterface> comparator = BelugaSorter.getComparator(sorter.field, sorter.order);
                        FileEntry newEntry = new FileEntry(new File(mFolderPath, name));
                        int pos = 0;
                        for (FileEntry entry : mAdapter.getAll()) {
                            if (comparator.compare(entry, newEntry) >= 0) {
                                break;
                            }
                            pos++;
                        }
                        Message message = mHandler.obtainMessage(1);
                        Bundle data = new Bundle();
                        data.putParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY, newEntry);
                        data.putInt(HANDLER_MESSAGE_POSITION_KEY, pos);
                        message.setData(data);
                        mHandler.sendMessage(message);
                    }
                }, FILE_OBSERVER_EVENT_DELAY);
            }

            boolean access = (event & ACCESS) != 0;
            boolean modify = (event & MODIFY) !=0;
            boolean attrib = (event & ATTRIB) !=0;
            boolean close_write = (event & CLOSE_WRITE) !=0;
            boolean close_nowrite = (event & CLOSE_NOWRITE) !=0;
            boolean open = (event & OPEN) !=0;
            boolean delete_self = (event & DELETE_SELF) !=0;
            boolean move_self = (event & MOVE_SELF) !=0;
            Log.i("BelugaFileObserver", name+" "+event+" "+" "+new File(mFolderPath, name).length()+" "
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
