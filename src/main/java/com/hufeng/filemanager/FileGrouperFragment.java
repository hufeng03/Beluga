package com.hufeng.filemanager;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileEntryFactory;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.services.UiServiceHelper;
import com.hufeng.filemanager.ui.FileCursorAdapter;
import com.hufeng.filemanager.ui.FileGridAdapterListener;
import com.hufeng.filemanager.ui.FileOperation;

import java.lang.ref.WeakReference;

public class FileGrouperFragment extends FileGridFragment implements LoaderManager.LoaderCallbacks<Cursor>, FileGridAdapterListener{

    private static final String TAG = FileGrouperFragment.class.getSimpleName();

    public static final String FILE_GROUPER_ARGUMENT_CATEGORY = "file_grouper_argument_category";
//    public static final String FILE_GROUPER_ARGUMENT_SELECTION = "file_grouper_argument_selection";
    public static final String FILE_GROUPER_ARGUMENT_SAFE = "file_grouper_argument_safe";
    public static final String FILE_GROUPER_ARGUMENT_CLOUD = "file_grouper_argumnet_cloud";
    public static final String FILE_GROUPER_ARGUMENT_CLOUD_UPLOAD_PARENT = "file_grouper_argument_cloud_upload_parent";

    private FileCursorAdapter mAdapter;

    public FileGrouperFragment() {
        super();
        mMenuId = R.menu.file_grouper_fragment_menu;
    }

    private WeakReference<FileGrouperFragmentListener> mWeakListener = null;

    public static interface FileGrouperFragmentListener {
        public void onFileGrouperItemClick(View v, FileEntry entry);
        public void onFileGrouperItemSelect(View v, FileEntry entry);
    }

    public void setListener(FileGrouperFragmentListener listener) {
        mWeakListener = new WeakReference<FileGrouperFragmentListener>(listener);
    }

    public static FileGrouperFragment newCategoryGrouperInstance(int category) {
        FileGrouperFragment fragment = new FileGrouperFragment();
        Bundle data = new Bundle();
        data.putInt(FileGrouperFragment.FILE_GROUPER_ARGUMENT_CATEGORY, category);
        fragment.setArguments(data);
        return fragment;
    }

    public static FileGrouperFragment newSafeBoxAddSelectInstance(int category) {
        FileGrouperFragment fragment = new FileGrouperFragment();
        Bundle data = new Bundle();
        data.putInt(FileGrouperFragment.FILE_GROUPER_ARGUMENT_CATEGORY, category);
        data.putBoolean(FILE_GROUPER_ARGUMENT_SAFE, true);
        fragment.setArguments(data);
//        FileOperation fileOperation = new FileOperation();
//        fileOperation.setOperationMode(FileOperation.OPERATION_MODE.ADD_SAFE);
//        fragment.getChildFragmentManager().beginTransaction().add(fragment, "FileOperation").commit();
//        fragment.setFileOperation(fileOperation);
        return fragment;
    }

    public static FileGrouperFragment newCloudUploadSelectInstance(int category, String root) {
        FileGrouperFragment fragment = new FileGrouperFragment();
        Bundle data = new Bundle();
        data.putInt(FileGrouperFragment.FILE_GROUPER_ARGUMENT_CATEGORY, category);
        data.putBoolean(FILE_GROUPER_ARGUMENT_CLOUD, true);
        data.putString(FILE_GROUPER_ARGUMENT_CLOUD_UPLOAD_PARENT, root);
        fragment.setArguments(data);
//        FileOperation fileOperation = new FileOperation();
//        fileOperation.setOperationMode(FileOperation.OPERATION_MODE.ADD_CLOUD);
//        fragment.getChildFragmentManager().beginTransaction().add(fragment, "FileOperation").commit();
//        fragment.setFileOperation(fileOperation);
        return fragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments().getBoolean(FILE_GROUPER_ARGUMENT_CLOUD) || getArguments().getBoolean(FILE_GROUPER_ARGUMENT_SAFE)) {
//            FileOperation fileOperation = new FileOperation();
//            fileOperation.setOperationMode(FileOperation.OPERATION_MODE.ADD_CLOUD);

            FileOperation fileOperation = (FileOperation) getChildFragmentManager().findFragmentByTag("FileGrouper-FileOperation");
            if (fileOperation == null) {
                if (getArguments().getBoolean(FILE_GROUPER_ARGUMENT_SAFE)) {
                    fileOperation = FileOperation.newInstance(FileOperation.OPERATION_MODE.ADD_SAFE.ordinal());
                } else {
                    fileOperation = FileOperation.newInstance(FileOperation.OPERATION_MODE.ADD_CLOUD.ordinal());
                }
                getChildFragmentManager().beginTransaction().add(fileOperation, "FileGrouper-FileOperation").commit();
            }
            setFileOperation(fileOperation);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.i(TAG, "FileGrouperFragment onCreate  with menuId = "+mMenuId);
        Bundle data = getArguments();
        if (data == null) {
            mCategory = FileUtils.FILE_TYPE_IMAGE;
        } else {
            mCategory = data.getInt(FILE_GROUPER_ARGUMENT_CATEGORY);
        }
        if(getArguments().getBoolean(FILE_GROUPER_ARGUMENT_SAFE) || getArguments().getBoolean(FILE_GROUPER_ARGUMENT_CLOUD)) {
            mMenuId = R.menu.file_grouper_fragment_search_menu;
        }
	}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getFileOperation().getOperationMode() == FileOperation.OPERATION_MODE.ADD_CLOUD) {
            Activity act = getActivity();
            if (act != null && act instanceof FileManagerTabActivity) {
                ((FileManagerTabActivity)act).setPagingEnabled(false);
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        String empty_text;
        switch(mCategory){
            case FileUtils.FILE_TYPE_APK:
                empty_text = getResources().getString(R.string.empty_apk);
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                empty_text = getResources().getString(R.string.empty_audio);
                break;
            case FileUtils.FILE_TYPE_IMAGE:
                empty_text = getResources().getString(R.string.empty_image);
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                empty_text = getResources().getString(R.string.empty_video);
                break;
            case FileUtils.FILE_TYPE_DOCUMENT:
                empty_text = getResources().getString(R.string.empty_document);
                break;
            case FileUtils.FILE_TYPE_ZIP:
                empty_text = getResources().getString(R.string.empty_zip);
                break;
            default:
                empty_text = "";
                break;
        }

        setEmptyText(empty_text);

        mAdapter = new FileCursorAdapter(getSherlockActivity(),null, getFileOperation());
        mAdapter.setFileGridAdapterListener(this);
        setGridAdapter(mAdapter);
        if (FileUtils.FILE_TYPE_IMAGE == mCategory || FileUtils.FILE_TYPE_VIDEO == mCategory) {
            if (getDisplayMode() == DISPLAY_MODE.LIST) {
                switchDisplayMode();
            }
        } else {
            if (getDisplayMode() == DISPLAY_MODE.GRID) {
                switchDisplayMode();
            }
        }
        setGridShownNoAnimation(false);
//        registerForContextMenu(getGridView());
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LoaderIDs.getLoaderId(mCategory), null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getFileOperation().getOperationMode() == FileOperation.OPERATION_MODE.ADD_CLOUD) {
            Activity act = getActivity();
            if (act != null && act instanceof FileManagerTabActivity) {
                ((FileManagerTabActivity)act).setPagingEnabled(true);
            }
        }
//        unregisterForContextMenu(getGridView());
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        super.onGridItemClick(g,v,position,id);
        if (mWeakListener != null) {
            FileGrouperFragmentListener listener = mWeakListener.get();
            if (listener != null) {
               Cursor cursor =(Cursor)g.getItemAtPosition(position);
               //Cursor cursor = (Cursor)g.getAdapter().getItem(position);
               String path = cursor.getString(DataStructures.FileColumns.FILE_PATH_FIELD_INDEX);
               ImageView v_img =  (ImageView)v.findViewById(R.id.icon);
               listener.onFileGrouperItemClick(v_img, FileEntryFactory.makeFileObject(path));
            }
        }
    }

    @Override
    public void onGridItemSelect(GridView g, View v, int position, long id) {
        super.onGridItemSelect(g,v,position,id);
        if (mWeakListener != null) {
            FileGrouperFragmentListener listener = mWeakListener.get();
            if (listener != null) {
                Cursor cursor =(Cursor)g.getItemAtPosition(position);
                //Cursor cursor = (Cursor)g.getAdapter().getItem(position);
                String path = cursor.getString(DataStructures.FileColumns.FILE_PATH_FIELD_INDEX);
                ImageView v_img =  (ImageView)v.findViewById(R.id.icon);
                listener.onFileGrouperItemSelect(v_img, FileEntryFactory.makeFileObject(path));
            }
        }
    }

    @Override
    public String getSearchString() {
        return mSearchString;
    }

    @Override
    public void reportNotExistFile() {
//        reloadFiles();
        UiServiceHelper.getInstance().deleteUnexist(mCategory);
    }

    @Override
    public void reloadFiles() {
        getLoaderManager().restartLoader(LoaderIDs.getLoaderId(mCategory), null, this);
    }

    @Override
    public String getParentFile() {
        return getArguments().getString(FILE_GROUPER_ARGUMENT_CLOUD_UPLOAD_PARENT,"");
    }

    @Override
    public String[] getAllFiles() {
        return mAdapter.getAllFiles();
    }

    @Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return FileManagerLoaders.getCursorLoader(getActivity(), mCategory, mSearchString);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		mAdapter.changeCursor(arg1);
		setGridShown(true);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
		
	}
	
}
