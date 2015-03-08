package com.hufeng.filemanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.loader.FileCursorLoader;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;
import com.hufeng.filemanager.utils.LogUtil;

public class FileGrouperFragment extends FileRecyclerFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        BelugaEntryViewHolder.EntryClickListener {

    private static final String TAG = FileGrouperFragment.class.getSimpleName();

    public static final String FILE_GROUPER_ARGUMENT_CATEGORY = "file_grouper_argument_category";
    public static final String FILE_GROUPER_ARGUMENT_PICK = "file_grouper_argument_pick";

    private BelugaCursorRecyclerAdapter mAdapter;

    private int mCategory;

    private static final int LOADER_ID = 1;


    public static FileGrouperFragment newCategoryGrouperInstance(int category) {
        FileGrouperFragment fragment = new FileGrouperFragment();
        Bundle data = new Bundle();
        data.putInt(FileGrouperFragment.FILE_GROUPER_ARGUMENT_CATEGORY, category);
        fragment.setArguments(data);
        return fragment;
    }

    public static FileGrouperFragment newSelectionGrouper(int category) {
        FileGrouperFragment fragment = new FileGrouperFragment();
        Bundle data = new Bundle();
        data.putInt(FileGrouperFragment.FILE_GROUPER_ARGUMENT_CATEGORY, category);
        data.putBoolean(FILE_GROUPER_ARGUMENT_PICK, true);
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
        LogUtil.i(TAG, "FileGrouperFragment onCreate");
        Bundle data = getArguments();
        if (data != null) {
            mCategory = data.getInt(FILE_GROUPER_ARGUMENT_CATEGORY);
        }
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        String empty_text;
        switch(mCategory){
            case FileCategoryHelper.CATEGORY_TYPE_APK:
                empty_text = getResources().getString(R.string.empty_apk);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                empty_text = getResources().getString(R.string.empty_audio);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                empty_text = getResources().getString(R.string.empty_image);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                empty_text = getResources().getString(R.string.empty_video);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                empty_text = getResources().getString(R.string.empty_document);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                empty_text = getResources().getString(R.string.empty_zip);
                break;
            default:
                empty_text = getResources().getString(R.string.empty_file);
                break;
        }

        setEmptyText(empty_text);

        mAdapter = new BelugaCursorRecyclerAdapter(getActivity(), null, BelugaDisplayMode.LIST,/*R.layout.file_list_row,*/
                new BelugaEntryViewHolder.Builder() {
                    @Override
                    public BelugaEntryViewHolder createViewHolder(ViewGroup parent, int type) {
                        if (type == BelugaDisplayMode.GRID.ordinal()) {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_grid_row, parent, false);
                            return new FileEntryGridViewHolder(view, getActionController(), FileGrouperFragment.this);
                        } else {
                            View view = LayoutInflater.from(parent.getContext())
                                    .inflate( R.layout.file_list_row, parent, false);
                            return new FileEntryListViewHolder(view, getActionController(), FileGrouperFragment.this);
                        }
                    }
                });
        setRecyclerAdapter(mAdapter);

        if (FileCategoryHelper.CATEGORY_TYPE_VIDEO == mCategory ||
                FileCategoryHelper.CATEGORY_TYPE_IMAGE == mCategory ||
                FileCategoryHelper.CATEGORY_TYPE_AUDIO == mCategory ||
                FileCategoryHelper.CATEGORY_TYPE_APK == mCategory) {
            mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mRootView.getWidth() > 0 && mRootView.getHeight() > 0) {
                        switchDisplay();
                        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

        setEmptyViewShown(false);
        setListShownNoAnimation(false);
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getUserVisibleHint()) {
            inflater.inflate(R.menu.file_grouper_fragment_menu, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!getUserVisibleHint()) {
            return;
        }
        final MenuItem displayMenu = menu.findItem(R.id.menu_grouper_display);
        final MenuItem sortMenu = menu.findItem(R.id.menu_grouper_sort);

//        final Fragment parentFragment = getParentFragment();
        boolean isFragmentVisible = getUserVisibleHint();
//        if(parentFragment != null && (parentFragment instanceof FileTabFragment)) {
//            isFragmentVisible = parentFragment.getUserVisibleHint();
//        }
        final Activity parentActivity = getActivity();
        boolean isSearchMode = false;
        if (parentActivity != null && (parentActivity instanceof BelugaDrawerActivity)) {
            isSearchMode = ((BelugaDrawerActivity)getActivity()).isSearchMode();
        }

        final boolean menuVisible = isFragmentVisible && !isSearchMode;
        displayMenu.setVisible(menuVisible);
        sortMenu.setVisible(menuVisible);

        displayMenu.setIcon(getDisplayMode() == BelugaDisplayMode.LIST ?
                R.drawable.ic_action_view_as_grid : R.drawable.ic_action_view_as_list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!getUserVisibleHint()) {
            return false;
        }
        switch(item.getItemId()){
            case R.id.menu_grouper_display:
                switchDisplay();
                getActivity().supportInvalidateOptionsMenu();
                return true;
            case R.id.menu_grouper_sort:
                BelugaDialogFragment.showSortDialog(getActivity(), mCategory);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public FileEntry[] getAllFiles() {
        return mAdapter.getAll();
    }

    @Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        return new FileCursorLoader(getActivity(), mCategory, mSearchString);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        Log.i(TAG, "onCreateFinished");
		mAdapter.swapCursor(arg1);
        final boolean empty = arg1 == null || arg1.getCount() == 0;
        setRecyclerViewShown(true);
        setEmptyViewShown(empty);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
        Log.i(TAG, "onCreateReset");
		mAdapter.swapCursor(null);
	}


    @Override
    public void onEntryClickedToOpen(View view, BelugaEntry entry) {
        FileEntry fileEntry = (FileEntry)entry;
        if (fileEntry.isDirectory) {
            BusProvider.getInstance().post(new FolderOpenEvent(System.currentTimeMillis(), fileEntry));
        } else {
            BelugaActionDelegate.view(view.getContext(), fileEntry);
        }
    }
}
