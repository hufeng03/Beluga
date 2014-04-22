package com.hufeng.filemanager.kanbox;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.hufeng.filemanager.FileGridFragment;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.dialog.FmDialogFragment;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.ui.FileViewHolder;
import com.kanbox.api.PushSharePreference;
import com.kanbox.api.RequestListener;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by feng on 13-11-21.
 */
public class KanBoxBrowserFragment extends FileGridFragment implements
        KanBoxApi.KanBoxApiListener, LoaderManager.LoaderCallbacks<Cursor>,
        FmDialogFragment.OnDialogDoneListener, FileViewHolder.FileViewClicked {
    private static final String TAG = KanBoxTabFragment.class.getSimpleName();

    public String mRootDir = "/";

    public KanBoxBrowserFragment() {
        mMenuId = R.menu.kanbox_browser_fragment_menu;
        mCategory = FileUtils.FILE_TYPE_CLOUD;
    }

    private boolean mLoading = false;

    private static final int LOADER_ID_KANBOX = 311;

    private KanBoxBrowserAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    private static final int MESSAGE_SET_EMPTY_TEXT = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SET_EMPTY_TEXT:
                    if(isAdded()) {
                        setEmptyText(getResources().getString(R.string.kanbox_empty_file));
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        enableSwipe();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KanBoxApi.getInstance().registerKanBoxApiListener(this);
        mAdapter = new KanBoxBrowserAdapter(getActivity(), null, this);
        setGridAdapter(mAdapter);
        getGridView().setOnItemLongClickListener(null);
        registerForContextMenu(getGridView());
        setEmptyText("");
        PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        mRootDir = sPreference.getStringValueByKey("kanbox_browser_root_dir");
        if(TextUtils.isEmpty(mRootDir)) {
            mRootDir = "/";
        }

        if(true){
            mLoading = true;
            setEmptyText(getResources().getString(R.string.kanbox_getting_file_list));
            KanBoxApi.getInstance().getFileList(mRootDir);
        }
    }


    @Override
    public void onDestroyView() {
        KanBoxApi.getInstance().unRegisterKanBoxApiListener(this);
        unregisterForContextMenu(getGridView());
        PushSharePreference sPreference = new PushSharePreference(FileManager.getAppContext(), KanBoxApi.PUSH_SHAREPREFERENCE_NAME);
        sPreference.saveStringValueToSharePreferences("kanbox_browser_root_dir", mRootDir);
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if( getLoaderManager().getLoader(LOADER_ID_KANBOX)==null)	{
            getLoaderManager().initLoader(LOADER_ID_KANBOX, null, this);
        }else{
            getLoaderManager().restartLoader(LOADER_ID_KANBOX, null, this);
        }
    }

    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        super.onGridItemClick(g, v, position, id);
        clickGridItem(position);
    }

    @Override
    public void onFileStatusClicked(String path, long position) {
        clickGridItem(position);
    }

    private void clickGridItem(long position) {
        Cursor cursor = (Cursor)getGridAdapter().getItem((int)position);
        int folder = cursor.getInt(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD_INDEX);
        String path = cursor.getString(DataStructures.CloudBoxColumns.FILE_PATH_FIELD_INDEX);
        if (folder == 1) {
            if(!path.endsWith("/")) {
                mRootDir = path + "/";
            } else {
                mRootDir = path;
            }
            refresh();
        } else {
            if(KanBoxApi.isDownloading(path)) {
                //we need to pause downloading here
                KanBoxApi.getInstance().pauseDownloadFile(path);
                mAdapter.notifyDataSetChanged();
                return;
            }
            //download/view cloud file
            String local_path=cursor.getString(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD_INDEX);
            if(!TextUtils.isEmpty(local_path) && new File(local_path).exists()) {
                FileAction.viewFile(getActivity(),local_path);
            } else {
                FmDialogFragment.showDownloadFromCloudConfirmDialog(getChildFragmentManager(), path);
            }
        }
    }

    @Override
    public void onGridItemSelect(GridView g, View v, int position, long id) {
        super.onGridItemSelect(g, v, position, id);
    }

    @Override
    public void onKanBoxApiSuccess(int op_type, String path, String response) {
        mAdapter.notifyDataSetChanged();
        setGridShown(true);
        String tip = null;
        if(mProgressDialog!=null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
        switch(op_type) {
            case KanBoxApi.OP_UPLOAD:
                if ("pause".equals(response)) {
//                    tip = getString(R.string.upload_paused, new File(path).getName());
                } else {
                    tip = getString(R.string.upload_success, new File(path).getName());
                }
                refreshUploadStatus();
                break;
            case KanBoxApi.OP_MOVE:
                tip = getString(R.string.move_success, path);
                break;
            case KanBoxApi.OP_DELETE:
                tip = getString(R.string.delete_success, path);
                break;
            case KanBoxApi.OP_MAKE_DIR:
                tip = getString(R.string.create_success, path);
                break;
            case KanBoxApi.OP_DOWNLOAD:
                if("pause".equals(response)) {
//                    tip = getString(R.string.download_paused, path);
                } else {
                    tip = getString(R.string.download_success, path);
                }
                break;
            case KanBoxApi.OP_GET_FILELIST:
                completeRefresh();
                break;
        }
        if(!TextUtils.isEmpty(tip) && getActivity()!=null) {
            Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onKanBoxApiFailed(int op_type, String path) {
        mAdapter.notifyDataSetChanged();
//        setGridShown(true);
        String tip = null;
        if(mProgressDialog!=null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
        switch(op_type) {
            case KanBoxApi.OP_UPLOAD:
                tip = getString(R.string.upload_failed, path);
                refreshUploadStatus();
                break;
            case KanBoxApi.OP_MOVE:
                tip = getString(R.string.move_failed, path);
                break;
            case KanBoxApi.OP_DELETE:
                tip = getString(R.string.delete_failed, path);
                break;
            case KanBoxApi.OP_MAKE_DIR:
                tip = getString(R.string.create_failed, path);
                break;
            case KanBoxApi.OP_DOWNLOAD:
                tip = getString(R.string.download_failed, path);
                break;
            case KanBoxApi.OP_GET_FILELIST:
                tip = getString(R.string.kanbox_refresh_file_list_error);
                completeRefresh();
                break;
            case KanBoxApi.OP_REFRESH_TOKEN:
                tip = getString(R.string.kanbox_refresh_token_failed);
                break;
        }
        if(!TextUtils.isEmpty(tip) && getActivity()!=null) {
            Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onKanBoxApiProgress(int op_type, String path, int progress) {
        if (op_type == RequestListener.OP_DOWNLOAD) {
            mAdapter.notifyDataSetChanged();
        } else if (op_type == RequestListener.OP_UPLOAD) {
            refreshUploadStatus();
        }
    }

    private void refreshUploadStatus() {
        if (mListener != null && mListener.get() != null) {
            mListener.get().onUploadFileRefresh();
        }
    }

    public boolean onBackPressed() {
        if(TextUtils.isEmpty(mRootDir) || "/".equals(mRootDir)) {
            return false;
        } else {
            String parent = new File(mRootDir).getParent();
            if (parent.endsWith("/")) {
                mRootDir = parent;
            } else {
                mRootDir = parent + "/";
            }
//            setGridShown(true);
//            hideSwipeProgress();
            completeRefresh();
            reloadFiles();
            if ("/".equals(mRootDir)) {
                getActivity().supportInvalidateOptionsMenu();
            }
            return true;
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sort_constraint = null;
        FileSorter.SORTER sorter = FileSorter.getFileSorter(getActivity(), FileUtils.FILE_TYPE_CLOUD);
        switch(sorter.field){
            case NAME:
                sort_constraint = DataStructures.FileColumns.FILE_NAME_FIELD;
                break;
            case DATE:
                sort_constraint = DataStructures.FileColumns.FILE_DATE_FIELD;
                break;
            case SIZE:
                sort_constraint = DataStructures.FileColumns.FILE_SIZE_FIELD;
                break;
            case EXTENSION:
                sort_constraint = DataStructures.FileColumns.FILE_EXTENSION_FIELD;
                break;
        }
        if(!TextUtils.isEmpty(sort_constraint)){
            if(sorter.order == FileSorter.SORT_ORDER.ASC){
                sort_constraint += " ASC";
            }else{
                sort_constraint += " DESC";
            }
        }
        return new CursorLoader(getActivity(), DataStructures.CloudBoxColumns.CONTENT_URI,
                DataStructures.CloudBoxColumns.CLOUD_BOX_PROJECTION, DataStructures.CloudBoxColumns.PARENT_FOLDER_FIELD+"=?",
                new String[]{mRootDir},
                sort_constraint);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        if((cursor==null || cursor.getCount()==0) && !mLoading) {
            mHandler.sendEmptyMessageDelayed(MESSAGE_SET_EMPTY_TEXT, 500);
        }
        setGridShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

//    MenuItem mRefreshItem;
//    Animation mRefreshAnimation;
//    ImageView mRefreshView;
//    boolean mRefreshing = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        completeRefresh();
//        mRefreshItem = null;
        super.onCreateOptionsMenu(menu, inflater);

        if (!mMenuCreated) {
            return;
        }
        MenuItem item_back = menu.findItem(R.id.menu_back);
        MenuItem item_logout = menu.findItem(R.id.menu_cloud_logout);
        if(mRootDir == null || "/".equals(mRootDir)) {
            if (item_back != null) {
                item_back.setVisible(false);
            }
            if (item_logout != null) {
                item_logout.setVisible(true);
            }
        } else {
            if (item_back != null) {
                item_back.setVisible(true);
            }
            if (item_logout != null) {
                item_logout.setVisible(false);
            }
        }

//        mRefreshItem = menu.findItem(R.id.menu_cloud_refresh);
//        if(mRefreshItem!=null)
//            LogUtil.i(TAG, "new refresh item is " + mRefreshItem);
//        if(mRefreshItem!=null && mRefreshItem.isVisible()){
//            if(mLoading) {
//                LogUtil.i(TAG, "set refresh menu item to scanning");
//                refresh();
//            }else{
//                LogUtil.i(TAG, "set refresh menu item to complete scanning");
//                completeRefresh();
//            }
//        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!mMenuCreated) {
            return false;
        }
        switch(item.getItemId()) {
//            case R.id.menu_cloud_upload_confirm:
//                Fragment fragment = getParentFragment();
//                if(fragment instanceof KanBoxTabFragment) {
//                    FileOperation operation = ((KanBoxTabFragment) fragment).getFileOperation();
//                    String[] files = operation.getUploadingFiles();
//                    if(files!=null && files.length>0) {
//                        mProgressDialog = new ProgressDialog(getActivity());
//                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
//                        mProgressDialog.setTitle(R.string.progress_add_to_cloud_title);//设置标题
//                        mProgressDialog.setMessage(getString(R.string.progress_add_to_cloud_content));
//                        mProgressDialog.setIndeterminate(false);//设置进度条是否为不明确
//                        mProgressDialog.setCancelable(true);//设置进度条是否可以按退回键取消
//                        mProgressDialog.setCanceledOnTouchOutside(false);
//                        mProgressDialog.show();
//                        KanBoxApi.getInstance().uploadFile(files[0],getParentFile());
//                    }
//                }
//                return true;
            case R.id.menu_cloud_logout:
                FmDialogFragment.showCloudLogoutDialog(getChildFragmentManager());
                return true;
            case R.id.menu_cloud_create:
                FmDialogFragment.showCreateCloudDirectoryDialog(getChildFragmentManager(), getParentFile());
                return true;
            case R.id.menu_cloud_add_image:
                addFileIntoCloud(FileUtils.FILE_TYPE_IMAGE);
                return true;
            case R.id.menu_cloud_add_audio:
                addFileIntoCloud(FileUtils.FILE_TYPE_AUDIO);
                return true;
            case R.id.menu_cloud_add_video:
                addFileIntoCloud(FileUtils.FILE_TYPE_VIDEO);
                return true;
            case R.id.menu_cloud_add_apk:
                addFileIntoCloud(FileUtils.FILE_TYPE_APK);
                return true;
            case R.id.menu_cloud_add_zip:
                addFileIntoCloud(FileUtils.FILE_TYPE_ZIP);
                return true;
            case R.id.menu_cloud_add_document:
                addFileIntoCloud(FileUtils.FILE_TYPE_DOCUMENT);
                return true;
//            case R.id.menu_cloud_refresh:
//                mLoading = true;
//                setEmptyText(getResources().getString(R.string.kanbox_getting_file_list));
//                KanBoxApi.getInstance().getFileList(mRootDir);
//                refresh();
//                return true;
//            case R.id.menu_cloud_add_by_directory:
//                addFileIntoCloud(FileUtils.FILE_TYPE_ALL);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void addFileIntoCloud(int type) {
        if (mListener != null) {
            KanBoxBrowserListener listener = mListener.get();
            if (listener != null) {
                listener.onAddFileIntoCloud(type);
            }
        }
    }

    public interface KanBoxBrowserListener {
        public void onImageFileClicked(ImageView view, String path);
        public void onAddFileIntoCloud(int category);
        public void onLogoutCloud();
        public void onUploadFileRefresh();
    }

    public WeakReference<KanBoxBrowserListener> mListener;

    public void setKanBoxBrowserListener(KanBoxBrowserListener listener) {
        mListener = new WeakReference<KanBoxBrowserListener>(listener);
    }

    @Override
    public void reloadFiles() {
        getLoaderManager().restartLoader(LOADER_ID_KANBOX, null, this);
    }

    @Override
    public String getParentFile() {
        return mRootDir;
    }

    @Override
    public String[] getAllFiles() {
        return mAdapter.getAllFiles();
    }

    @Override
    public void refreshUI() {
        super.refreshUI();
    }

    public void showRootDirs(){
        if(!"/".equals(mRootDir)) {
            mRootDir = "/";
            reloadFiles();
            getActivity().supportInvalidateOptionsMenu();
        }
    }

    @Override
    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
        switch(dialog_id) {
            case FmDialogFragment.CLOUD_LOGOUT_DIALOG:
//                FileManager.getAppContext().getContentResolver().delete(DataStructures.CloudBoxColumns.CONTENT_URI, null, null);
                KanBoxApi.getInstance().logOut();
                if (mListener != null) {
                    KanBoxBrowserListener listener = mListener.get();
                    if (listener != null) {
                        listener.onLogoutCloud();
                    }
                }
                break;
            case FmDialogFragment.NEW_CLOUD_DIRECTORY_DIALOG:
                String new_dir = (String)param;
                KanBoxApi.getInstance().makeDir(new_dir);
                break;
            case FmDialogFragment.DOWNLOAD_FROM_CLOUD_CONFIRM_DIALOG:
                String remote_path = (String)param;
                downloadCloudFile(remote_path);
                break;
            case FmDialogFragment.CLOUD_RENAME_DIALOG:
                String old_name = ((String[])param)[0];
                String new_name = ((String[])param)[1];
                String new_path = new File(mRootDir, new_name).getAbsolutePath();
                KanBoxApi.getInstance().moveFile(old_name, new_path);
                break;
        }
    }

    private void downloadCloudFile(String remote_path) {
        long db_size = -1;
        long db_id = -1;
        Cursor cursor = null;
        try{
            cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                    new String[]{DataStructures.CloudBoxColumns._ID, DataStructures.CloudBoxColumns.FILE_SIZE_FIELD, DataStructures.CloudBoxColumns.HASH_FIELD}, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
                    new String[]{remote_path}, null);
            if (cursor!=null && cursor.moveToNext()) {
                db_id = cursor.getInt(0);
                db_size = cursor.getLong(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }

        StorageManager manager = StorageManager.getInstance(getActivity());
        String[] storages = manager.getMountedStorages();
        String local_path = null;
        boolean flag_has_local = false;
        if(storages!=null) {
            int size = storages.length;
            int idx = 0;
            idx = remote_path.lastIndexOf("/");
            String name = remote_path.substring(idx+1);
            String dir = remote_path.substring(0,idx+1);
            idx = 0;
            while(idx < size){
                String stor = storages[idx];
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String account_email = preferences.getString("KanBox_Account_Email", "").trim();
                File kanbox_dir = new File(stor, KanBoxConfig.LOCAL_STORAGE_DIRECTORY+File.separator+account_email);
                File kanbox_file = new File(kanbox_dir.getAbsolutePath()+dir, name);
                if(kanbox_file.exists() && kanbox_file.length() == db_size) {
                    local_path = new File(kanbox_dir.getAbsolutePath()+dir, name).getAbsolutePath();
                    flag_has_local = true;
                    break;
                } else {
                    if (TextUtils.isEmpty(local_path)) {
                        local_path = new File(kanbox_dir.getAbsolutePath()+dir, name).getAbsolutePath();
                    }
                }
                idx++;
            }
        }
        if (flag_has_local) {
            ContentValues cv = new ContentValues();
            cv.put(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD, local_path);
            FileManager.getAppContext().getContentResolver().update(Uri.withAppendedPath(DataStructures.CloudBoxColumns.CONTENT_URI, ""+db_id),
                    cv, null, null);
            mAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), getString(R.string.download_success, remote_path), Toast.LENGTH_SHORT).show();
        } else {
            if (local_path != null) {
                KanBoxApi.getInstance().downloadFile(remote_path, local_path);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

//    private void downloadCloudFile(String remote_path) {
//        StorageManager manager = StorageManager.getInstance(getActivity());
//        String[] storages = manager.getMountedStorages();
//        String local_path = null;
//        if(storages!=null) {
//            int size = storages.length;
//            int idx = 0;
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            String account_email = preferences.getString("KanBox_Account_Email", "").trim();
//            while(idx<size){
//                String stor = storages[idx];
//                File kanbox_dir = new File(stor, KanBoxConfig.LOCAL_STORAGE_DIRECTORY+File.separator+account_email);
//                if(local_path==null || new File(kanbox_dir.getAbsolutePath()+remote_path).exists()) {
//                    local_path = kanbox_dir.getAbsolutePath()+remote_path;
//                }
//                idx++;
//            }
//        }
//        boolean flag_has_local = false;
//        if(new File(local_path).exists()){
//            long size = new File(local_path).length();
//            Cursor cursor = null;
//            try{
//                cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
//                        new String[]{DataStructures.CloudBoxColumns._ID,DataStructures.CloudBoxColumns.FILE_SIZE_FIELD, DataStructures.CloudBoxColumns.HASH_FIELD}, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
//                        new String[]{remote_path}, null);
//                if (cursor!=null && cursor.moveToNext()) {
//                    int db_id = cursor.getInt(0);
//                    long db_size = cursor.getLong(1);
//                    String hash = cursor.getString(2);
//                    if(db_size == size) {
//                        flag_has_local = true;
//                        ContentValues cv = new ContentValues();
//                        cv.put(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD, local_path);
//                        FileManager.getAppContext().getContentResolver().update(Uri.withAppendedPath(DataStructures.CloudBoxColumns.CONTENT_URI, ""+db_id),
//                                cv, null, null);
//                        mAdapter.notifyDataSetChanged();
//                        Toast.makeText(getActivity(), getString(R.string.download_success, remote_path), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (cursor!=null) {
//                    cursor.close();
//                }
//            }
//        }
//        if (!flag_has_local) {
//            KanBoxApi.getInstance().downloadFile(remote_path, local_path);
//            mAdapter.notifyDataSetChanged();
////            Toast.makeText(getActivity(), getString(R.string.download_start, remote_path), Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        android.view.MenuInflater inflater = getActivity().getMenuInflater();
        FileViewHolder viewHolder = (FileViewHolder)((AdapterView.AdapterContextMenuInfo)menuInfo).targetView.getTag();
        String path = viewHolder.path;


        if(TextUtils.isEmpty(path)){
            return;
        }

        inflater.inflate(R.menu.cloud_file_menu, menu);

        boolean has_local = false;
        Cursor cursor = null;
        try{
            cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                    new String[]{DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD}, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
                    new String[]{path}, null);
            if (cursor!=null && cursor.moveToNext()) {
                String local_file = cursor.getString(0);
                if (!TextUtils.isEmpty(local_file) && !new File(local_file).isDirectory()) {
                    has_local = true;
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }

        if(has_local) {
            menu.findItem(R.id.menu_cloud_download).setVisible(false);
        } else {
            menu.findItem(R.id.menu_cloud_delete_local).setVisible(false);
            menu.findItem(R.id.menu_cloud_share).setVisible(false);
        }

        return;
        //super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
        Log.i(TAG, "onContextItemSelected");
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        FileViewHolder view = (FileViewHolder) info.targetView.getTag();
        String path = view.path;
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        switch(item.getItemId()) {
            case R.id.menu_cloud_rename:
                FmDialogFragment.showCloudRenameDialog(getChildFragmentManager(), path);
                break;
            case R.id.menu_cloud_download:
                downloadCloudFile(path);
                break;
            case R.id.menu_cloud_delete_local:
                deleteLocal(path);
                break;
            case R.id.menu_cloud_delete_server:
                KanBoxApi.getInstance().deleteFile(path);
                break;
            case R.id.menu_cloud_detail:
                FmDialogFragment.showCloudDetailDialog(getChildFragmentManager(),path);
                break;
            case R.id.menu_cloud_share:
                shareLocal(path);
                break;
        }
        return true;
    }

    private void shareLocal(String remote_path) {
        Cursor cursor = null;
        try{
            cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                    new String[]{DataStructures.CloudBoxColumns._ID, DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD}, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
                    new String[]{remote_path}, null);
            if (cursor!=null && cursor.moveToNext()) {
                String local_file = cursor.getString(1);
                if (!TextUtils.isEmpty(local_file)) {
                    FileAction.shareFile(getActivity(), local_file);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
    }

    private void deleteLocal(String remote_path) {
        Cursor cursor = null;
        try{
            cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                    new String[]{DataStructures.CloudBoxColumns._ID, DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD}, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
                    new String[]{remote_path}, null);
            if (cursor!=null && cursor.moveToNext()) {
                int db_id = cursor.getInt(0);
                String local_file = cursor.getString(1);
                if (!TextUtils.isEmpty(local_file)) {
                    if (FileAction.delete(local_file)){
                        ContentValues cv = new ContentValues();
                        cv.put(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD, "");
                        FileManager.getAppContext().getContentResolver().update(Uri.withAppendedPath(DataStructures.CloudBoxColumns.CONTENT_URI, ""+db_id),
                                cv, null, null);
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
        Toast.makeText(getActivity(), R.string.kanbox_delete_local_failed, Toast.LENGTH_SHORT).show();
    }


//    private void refresh(){
//        if(!mRefreshing) {
//            if(mRefreshItem!=null && mRefreshItem.isVisible()){
//                if(mRefreshView == null){
//                    LayoutInflater inflater = LayoutInflater.from(getSherlockActivity());
//                    mRefreshView = (ImageView) inflater.inflate(R.layout.refresh_action_view, null);
//                }
//                if(mRefreshAnimation == null) {
//                    mRefreshAnimation = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.clockwise_rotate);
//                    mRefreshAnimation.setRepeatCount(Animation.INFINITE);
//                }
//                if(mRefreshView.getAnimation()==null){
//                    mRefreshView.startAnimation(mRefreshAnimation);
//                }
//
//                mRefreshItem.setActionView(mRefreshView);
//                mRefreshing = true;
//            }
//        }
 //       showSwipeProgress();
 //   }

//    public void completeRefresh() {
//        hideSwipeProgress();
//    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLoading) {
            showSwipeProgress();
        } else {
            hideSwipeProgress();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideSwipeProgress();
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        refresh();
    }

    private void refresh() {
        mLoading = true;
        reloadFiles();
        setEmptyText(getResources().getString(R.string.kanbox_getting_file_list));
        KanBoxApi.getInstance().getFileList(mRootDir);
        showSwipeProgress();
    }

    private void completeRefresh() {
        mLoading = false;
        setEmptyText(getResources().getString(R.string.kanbox_empty_file));
        hideSwipeProgress();
    }
}
