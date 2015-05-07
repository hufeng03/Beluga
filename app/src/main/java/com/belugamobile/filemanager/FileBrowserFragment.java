package com.belugamobile.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaSortHelper;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.loader.FileBrowserLoader;
import com.belugamobile.filemanager.dialog.BelugaDialogFragment;
import com.belugamobile.filemanager.ui.BelugaActionController;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class FileBrowserFragment extends FileRecyclerFragment implements LoaderManager.LoaderCallbacks<List<BelugaFileEntry>>,
        BelugaEntryViewHolder.EntryClickListener{

    private static final String TAG = FileBrowserFragment.class.getSimpleName();

    BelugaArrayRecyclerAdapter<BelugaFileEntry> mAdapter;

    private String mRootFolder;
    private String mCurrentFolder;
    private String mLastFolder;

    private static final int LOADER_ID = 1;

    public static final String ARGUMENT_BROWSER_ROOT_FOLDER = "browser_root_folder";
    public static final String ARGUMENT_BROWSER_CURRENT_FOLDER = "browser_current_folder";

    public static FileBrowserFragment newRootFolderBrowser(String rootFolder, String currentFolder) {
        if (TextUtils.isEmpty(rootFolder)) {
            return null;
        }
        FileBrowserFragment fragment = new FileBrowserFragment();
        Bundle data = new Bundle();
        data.putString(ARGUMENT_BROWSER_ROOT_FOLDER, rootFolder);
        data.putString(ARGUMENT_BROWSER_CURRENT_FOLDER, currentFolder);
        fragment.setArguments(data);
        return fragment;
    }

    public void setRootAndCurrentFolder(String root, String current) {
        boolean oldEmpty = TextUtils.isEmpty(mRootFolder);
        boolean newEmpty = TextUtils.isEmpty(root);
        if ((oldEmpty && !newEmpty) || (!oldEmpty && newEmpty) || (!oldEmpty && !newEmpty && !mRootFolder.equals(root))) {
            mRootFolder = root;
        }

        if (TextUtils.isEmpty(current)) {
            mCurrentFolder = mRootFolder;
        } else {
            mCurrentFolder = current;
        }
        if (isResumed()) {
            showDir(mCurrentFolder);
        } else {

        }
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
        if (arguments != null) {
            mRootFolder = arguments.getString(ARGUMENT_BROWSER_ROOT_FOLDER);
            mCurrentFolder = arguments.getString(ARGUMENT_BROWSER_CURRENT_FOLDER);
        }
        if (TextUtils.isEmpty(mCurrentFolder)) {
            mCurrentFolder = mRootFolder;
        }
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

        enableFloatingActionButton();
        mFab.hide(false);

        setEmptyText(mCurrentFolder);

        mAdapter = new BelugaArrayRecyclerAdapter<BelugaFileEntry>(
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
        setRecyclerViewShownNoAnimation(false);

        switch (getActionController().getOperationMode()) {
            case NORMAL:
                setFloatingActionBarImage(R.drawable.ic_action_add);
                break;
            case PICK:
                break;
            case COPY_PASTE:
            case CUT_PASTE:
                setFloatingActionBarImage(R.drawable.ic_action_paste);
                break;
            case EXTRACT_ARCHIVE:
                setFloatingActionBarImage(R.drawable.ic_action_archive);
                break;
            case CREATE_ARCHIVE:
                setFloatingActionBarImage(R.drawable.ic_action_archive);
                break;
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
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getArguments().putString(ARGUMENT_BROWSER_ROOT_FOLDER, mRootFolder);
        getArguments().putString(ARGUMENT_BROWSER_CURRENT_FOLDER, mCurrentFolder);
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
    // !!! This is not fragment life cycle callback function
    @Override
    protected void onCreate() {
        BelugaActionController.OPERATION_MODE operationMode = getActionController().getOperationMode();
        if (operationMode == BelugaActionController.OPERATION_MODE.COPY_PASTE) {
            BelugaDialogFragment.showCopyPasteDialog(getActivity(), mCurrentFolder, getActionController().getAllActionFiles());
        } else if (operationMode == BelugaActionController.OPERATION_MODE.CUT_PASTE) {
            BelugaDialogFragment.showCutPasteDialog(getActivity(), mCurrentFolder, getActionController().getAllActionFiles());
        } else if (operationMode == BelugaActionController.OPERATION_MODE.EXTRACT_ARCHIVE) {
            BelugaDialogFragment.showExtractArchiveDialog(getActivity(), mCurrentFolder, getActionController().getAllActionFiles());
        } else if (operationMode == BelugaActionController.OPERATION_MODE.CREATE_ARCHIVE) {
            BelugaDialogFragment.showCreateArchiveDialog(getActivity(), mCurrentFolder, getActionController().getAllActionFiles());
        } else {
            BelugaDialogFragment.showCreateFolderDialog(getActivity(), mCurrentFolder);
        }
    }


    private boolean isActionMode() {
        final BelugaActionController.OPERATION_MODE mode = getActionController().getOperationMode();
        return mode != BelugaActionController.OPERATION_MODE.NORMAL;
    }

    @Override
    public boolean onBackPressed() {
        if (!TextUtils.isEmpty(mCurrentFolder)) {
            if (!TextUtils.isEmpty(mRootFolder)) {
                if (mCurrentFolder.equals(mRootFolder)) {
                    return false;
                }
            }

            String parent = new File(mCurrentFolder).getParent();
            if (!TextUtils.isEmpty(parent)) {
                showDir(parent);
                BusProvider.getInstance().post(new FolderShowEvent(System.currentTimeMillis(), parent, mRootFolder));
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getUserVisibleHint()) {
            inflater.inflate(R.menu.file_browser_fragment_menu, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!getUserVisibleHint()) {
            return;
        }
        final MenuItem displayMenu = menu.findItem(R.id.menu_browser_display);
        final MenuItem sortMenu = menu.findItem(R.id.menu_browser_sort);
        final MenuItem upMenu = menu.findItem(R.id.menu_up);
        final MenuItem createMenu = menu.findItem(R.id.menu_create);

        boolean isFragmentVisible = getUserVisibleHint();
        final Activity parentActivity = getActivity();
        boolean isSearchMode = false;
        if (parentActivity != null && (parentActivity instanceof BelugaDrawerActivity)) {
            isSearchMode = ((BelugaDrawerActivity)getActivity()).isSearchMode();
        }

        final boolean menuVisible = isFragmentVisible && !isSearchMode;

        if (displayMenu != null) displayMenu.setVisible(menuVisible);
        if (sortMenu != null) sortMenu.setVisible(menuVisible);
        if (upMenu != null) upMenu.setVisible(menuVisible && !TextUtils.isEmpty(mRootFolder));
        if (createMenu != null) createMenu.setVisible(menuVisible && !TextUtils.isEmpty(mRootFolder) && isActionMode());

        if (displayMenu != null) displayMenu.setIcon(getDisplayMode() == BelugaDisplayMode.LIST ?
                R.drawable.ic_action_view_as_grid : R.drawable.ic_action_view_as_list);

        if (!TextUtils.isEmpty(mCurrentFolder) /*&& new File(mCurrentFolder).canWrite()*/) {
            mFab.show();
        } else {
            mFab.hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!getUserVisibleHint()) {
            return false;
        }
        switch(item.getItemId()){
            case R.id.menu_browser_display:
                switchDisplay();
                getActivity().supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_browser_sort:
                BelugaDialogFragment.showSortDialog(getActivity(), FileCategoryHelper.CATEGORY_TYPE_UNKNOW);
                return true;
            case R.id.menu_up:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_create:
                BelugaDialogFragment.showCreateFolderDialog(getActivity(), mCurrentFolder);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void refreshUI() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (isActionMode() && getActionController().getFileSelectedSize() == 0) {
            mFab.hide(true);
            disableFloatingActionButton();
        }
    }

    @Override
    public BelugaFileEntry[] getAllFiles() {
        return mAdapter.getAll();
    }

    public void showDir(String path) {
        mLastFolder = mCurrentFolder;
        mCurrentFolder = path;
        setEmptyText(mCurrentFolder);
        getLoaderManager().restartLoader(LOADER_ID, null, this);
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
	public Loader<List<BelugaFileEntry>> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        if(arg0 ==  LOADER_ID) {
            return new FileBrowserLoader(getActivity(), mCurrentFolder, true);
        } else {
            return null;
        }
	}

	@Override
	public void onLoadFinished(Loader<List<BelugaFileEntry>> arg0,
			List<BelugaFileEntry> arg1) {
        Log.i(TAG, "onLoadFinished");
        int pos = -1;
        boolean flag = false;
        if (!TextUtils.isEmpty(mLastFolder)) {
            Iterator<BelugaFileEntry> iterator =  arg1.iterator();
            while (iterator.hasNext()) {
                pos++;
                BelugaFileEntry entry = iterator.next();
                if (entry.path.equals(mLastFolder)){
                    flag = true;
                    break;
                }
            }
        }

//        if (!TextUtils.isEmpty(mCurrentFolder)) {
//            mObserverHandlerThread = new HandlerThread("BelugaFolderObserver");
//            mObserverHandlerThread.start();
//            mObserverHandler = new ObserverHandler(mObserverHandlerThread.getLooper());
//            mFolderObserver = new BelugaFolderObserver(mCurrentFolder, mObserverHandler, 500);
//            mFolderObserver.startWatching();
//        }

        mAdapter.setData(arg1);
        //TODO: recover this
        if ( flag ) {
            getLayoutManager().scrollToPosition(pos);
        }

        mLastFolder = null;
        setRecyclerViewShown(true);
        setEmptyViewShown(arg1.size() == 0);
	}

	@Override
	public void onLoaderReset(Loader<List<BelugaFileEntry>> arg0) {
        Log.i(TAG, "onCreateReset");
        mAdapter.clear();

//        if (mFolderObserver != null) {
//            mFolderObserver.stopWatching();
//            mFolderObserver = null;
//        }
//
//        if (mObserverHandlerThread != null) {
//            mObserverHandlerThread.quit();
//            mObserverHandlerThread = null;
//            mObserverHandler = null;
//        }
	}

    @Override
    public void onEntryClickedToOpen(View view, BelugaEntry entry) {
        BelugaFileEntry belugaFileEntry = (BelugaFileEntry)entry;
        if (belugaFileEntry.isDirectory) {
            BusProvider.getInstance().post(new FolderShowEvent(System.currentTimeMillis(), belugaFileEntry.path, mRootFolder));
            showDir(belugaFileEntry.path);
        } else if (belugaFileEntry.type == FileCategoryHelper.FILE_TYPE_ZIP) {
            BusProvider.getInstance().post(new ZipSelectEvent(System.currentTimeMillis(), ((BelugaFileEntry) entry).path));
        } else {
            BelugaActionDelegate.view(getActivity(), belugaFileEntry);
        }
    }

    @Subscribe
    public void onFolderCreated(FileCreateEvent event) {
        if (event.path.startsWith(mCurrentFolder)) {
            BelugaFileEntry newEntry = new BelugaFileEntry(event.path);
            Comparator<BelugaSortableInterface> comparator = BelugaSortHelper.getComparator(FileCategoryHelper.CATEGORY_TYPE_UNKNOW);

            if (!mAdapter.contains(newEntry)) {
                int pos = 0;
                for (BelugaFileEntry entry : mAdapter.getAll()) {
                    if (comparator.compare(entry, newEntry) >= 0) {
                        break;
                    }
                    pos++;
                }
                Log.i(TAG, "add folder in " + pos + " : " + newEntry.path);
                getLayoutManager().scrollToPosition(pos);
                mAdapter.add(newEntry, pos);
                setEmptyViewShown(mAdapter.getItemCount() == 0);
            }
        }
    }

    @Subscribe
    public void onFolderDeleted(FileDeleteEvent event) {
        if (event.path.startsWith(mCurrentFolder)) {
            BelugaFileEntry oldEntry = new BelugaFileEntry(event.path);

            if (mAdapter.contains(oldEntry)) {
                mAdapter.remove(oldEntry);
                if (getActionController() != null) {
                    getActionController().setEntrySelection(false, oldEntry);
                }
                setEmptyViewShown(mAdapter.getItemCount() == 0);
            }
        }
    }

//    private HandlerThread mObserverHandlerThread;
//    private BelugaFolderObserver mFolderObserver;
//    private ObserverHandler mObserverHandler;
    private static final String HANDLER_MESSAGE_FILE_ENTRY_KEY = "fileEntry";
    private static final String HANDLER_MESSAGE_POSITION_KEY = "position";
//
//    private class ObserverHandler extends Handler {
//
//        public ObserverHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BelugaFolderObserver.MESSAGE_TYPE_FILE_DISAPPEAR:
//                {
//                    String path = msg.getData().getString(BelugaFolderObserver.HANDLER_MESSAGE_FILE_PATH_KEY);
//                    if (TextUtils.isEmpty(path) || new File(path).exists()) {
//                        return;
//                    }
//                    BelugaFileEntry oldEntry = new BelugaFileEntry(path);
//                    Message newMessage = mUIThreadHandler.obtainMessage(BelugaFolderObserver.MESSAGE_TYPE_FILE_DISAPPEAR);
//                    Bundle data = new Bundle();
//                    data.putParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY, oldEntry);
//                    newMessage.setData(data);
//                    newMessage.sendToTarget();
//                }
//                    break;
//                case BelugaFolderObserver.MESSAGE_TYPE_FILE_APPEAR:
//                {
//                    String path = msg.getData().getString(BelugaFolderObserver.HANDLER_MESSAGE_FILE_PATH_KEY);
//                    if (TextUtils.isEmpty(path) || !new File(path).exists()) {
//                        return;
//                    }
//                    BelugaFileEntry newEntry = new BelugaFileEntry(path);
//                    Comparator<BelugaSortableInterface> comparator = BelugaSortHelper.getComparator(FileCategoryHelper.CATEGORY_TYPE_UNKNOW);
//                    int pos = 0;
//                    for (BelugaFileEntry entry : mAdapter.getAll()) {
//                        if (comparator.compare(entry, newEntry) >= 0) {
//                            break;
//                        }
//                        pos++;
//                    }
//                    Message newMessage = mUIThreadHandler.obtainMessage(BelugaFolderObserver.MESSAGE_TYPE_FILE_APPEAR);
//                    Bundle data = new Bundle();
//                    data.putParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY, newEntry);
//                    data.putInt(HANDLER_MESSAGE_POSITION_KEY, pos);
//                    newMessage.setData(data);
//                    newMessage.sendToTarget();
//                }
//                    break;
//            }
//        }
//    }
//
    private Handler mUIThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BelugaFolderObserver.MESSAGE_TYPE_FILE_DISAPPEAR:
                {
                    BelugaFileEntry oldEntry = msg.getData().getParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY);
                    mAdapter.remove(oldEntry);
                    if (getActionController() != null) {
                        getActionController().setEntrySelection(false, oldEntry);
                    }
                    setEmptyViewShown(mAdapter.getItemCount() == 0);
                }
                    break;
                case BelugaFolderObserver.MESSAGE_TYPE_FILE_APPEAR:
                {
                    BelugaFileEntry newEntry = msg.getData().getParcelable(HANDLER_MESSAGE_FILE_ENTRY_KEY);
                    int pos = msg.getData().getInt(HANDLER_MESSAGE_POSITION_KEY, 0);
                    mAdapter.add(newEntry, pos);
                    getLayoutManager().scrollToPosition(pos);
                    setEmptyViewShown(mAdapter.getItemCount() == 0);
                }
                    break;
            }
        }
    };

}
