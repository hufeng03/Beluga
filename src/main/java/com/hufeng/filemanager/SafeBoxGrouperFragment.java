package com.hufeng.filemanager;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.data.DataStructures;
import com.hufeng.filemanager.dialog.FmDialogFragment;
import com.hufeng.filemanager.safebox.CryptUtil;
import com.hufeng.filemanager.safebox.SafeBoxGrouperAdapter;
import com.hufeng.filemanager.safebox.SafeDataStructs;
import com.hufeng.filemanager.scan.ImageObject;
import com.hufeng.filemanager.scan.VideoObject;
import com.hufeng.filemanager.ui.FileGridAdapterListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SafeBoxGrouperFragment extends GridFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        FileGridAdapterListener,
        ActionMode.Callback,
        FmDialogFragment.OnDialogDoneListener{

    private static final String TAG = SafeBoxGrouperFragment.class.getSimpleName();

    private ArrayList<String> mOperationPaths = new ArrayList<String>();

    public static final int LOADER_ID_SAFE_BOX_CATEGORY_FILES = 203;

    public static final String ARGUMENT_SAFE_BOX_CATEGORY = "safe_box_detail_category";

	int mCategory;

    private SafeBoxGrouperAdapter mAdapter;

    private ActionMode mActionMode = null;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String empty_text;
        switch(mCategory){
            case FileUtils.FILE_TYPE_AUDIO:
                empty_text = getResources().getString(R.string.empty_secrect_audio);
                break;
            case FileUtils.FILE_TYPE_IMAGE:
                empty_text = getResources().getString(R.string.empty_secrect_image);
                break;
            case FileUtils.FILE_TYPE_VIDEO:
                empty_text = getResources().getString(R.string.empty_secrect_video);
                break;
            default:
                empty_text = "";
                break;
        }

        setEmptyText(empty_text);

        mAdapter = new SafeBoxGrouperAdapter(getSherlockActivity(),null, mOperationPaths);
        mAdapter.setFileGridAdapterListener(this);
        setGridAdapter(mAdapter);

        setGridShownNoAnimation(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        if( getLoaderManager().getLoader(LOADER_ID_SAFE_BOX_CATEGORY_FILES)==null)	{
            getLoaderManager().initLoader(LOADER_ID_SAFE_BOX_CATEGORY_FILES, null, this);
        }else{
            getLoaderManager().restartLoader(LOADER_ID_SAFE_BOX_CATEGORY_FILES, null, this);
        }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
        Bundle argus = getArguments();
        if (argus != null) {
            mCategory = argus.getInt(ARGUMENT_SAFE_BOX_CATEGORY);
        }
	}


    @Override
    public void onGridItemClick(GridView g, View v, int position, long id) {
        if (mOperationPaths.size() > 0) {
            onGridItemSelect(g,v,position,id);
            return;
        }
        Cursor cursor = (Cursor)g.getAdapter().getItem(position);
        String path = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_SAFE_PATH);
        int category = cursor.getInt(SafeDataStructs.SafeColumns.FIELD_INDEX_CATEGORY);
        String original_path = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGINAL_PATH);
        String original_extension = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGIANL_EXTENSION);
        if (TextUtils.isEmpty(original_extension)) {
            int idx = original_path.lastIndexOf(".");
            if(idx>0) {
                original_extension = original_path.substring(idx+1);
            }
        }
        String temp_path = path+"."+original_extension;
        if(FileUtils.FILE_TYPE_IMAGE == category) {
//            Intent intent = new Intent(getActivity(), PlayImageActivity.class);
//            intent.putExtra();
//            intent.putExtra();
//            intent.putExtra();
//            startActivity(intent);
        } else if(FileUtils.FILE_TYPE_AUDIO == category) {
//            Intent intent = new Intent(getActivity(), PlayAudioActivity.class);
//            intent.putExtra();
//            intent.putExtra();
//            intent.putExtra();
//            startActivity(intent, REQUEST_CODE_VIEW_SECRECTS);
        } else if(FileUtils.FILE_TYPE_VIDEO == category) {
//            Intent intent = new Intent(getActivity(), PlayVideoActivity.class);
//            intent.putExtra();
//            intent.putExtra();
//            intent.putExtra();
//            startActivity(intent);
        } else {

        }
    }


    @Override
    public void onGridItemSelect(GridView g, View v, int position, long id) {
        Cursor cursor = (Cursor)g.getAdapter().getItem(position);
        String path = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_SAFE_PATH);
        if(mOperationPaths.contains(path)) {
            mOperationPaths.remove(path);
        }
        else {
            mOperationPaths.add(path);
        }

        mAdapter.notifyDataSetChanged();

        if (getActivity()!=null && mActionMode == null) {
            mActionMode = ((SherlockFragmentActivity) getActivity())
                    .startActionMode(this);
        } else {
            mActionMode.invalidate();
        }
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.safe_file_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {

        switch(mCategory) {
            case FileUtils.FILE_TYPE_VIDEO:
                menu.findItem(R.id.menu_add).setIcon(R.drawable.safe_video_add);
                break;
            case FileUtils.FILE_TYPE_AUDIO:
                menu.findItem(R.id.menu_add).setIcon(R.drawable.safe_audio_add);
                break;
            case FileUtils.FILE_TYPE_IMAGE:
                menu.findItem(R.id.menu_add).setIcon(R.drawable.safe_image_add);
                break;
        }
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_add:
			addSafe();
			break;
		}
		return true;
	}


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.safe_box_operation_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        initMenuItemSelectAllOrCancel(menu);
        if (mode != null) {
            int selectedNum = getFileSelectedSize();
            mode.setTitle(getString(R.string.multi_select_title, selectedNum));
            if(selectedNum == 0){
                mode.finish();
            }
        }
        return true;
    }

    public int getFileSelectedSize(){
        if(mOperationPaths!=null)
            return mOperationPaths.size();
        else
            return 0;
    }

    private void initMenuItemSelectAllOrCancel(Menu menu) {
        MenuItem item = menu.findItem(R.id.file_operation_cancel);
        if(item != null) {
            item.setVisible(getFileSelectedSize()>0);
        }
        item = menu.findItem(R.id.file_operation_selectall);
        if(item != null) {
            item.setVisible(!isFileAllSelected());
        }
    }

    public boolean isFileAllSelected(){
        int size = mOperationPaths.size();
        int total = mAdapter.getCount();
        if(size>0 && size>=total) {
            return true;
        } else {
            return false;
        }
//        if( mOperationPerformListener == null ) {
//            return true;
//        } else {
//            if (mOperationPerformListener != null) {
//                String[] files = mOperationPerformListener.getAllFiles();
//                if( files == null ) {
//                    return true;
//                } else {
//                    boolean result = true;
//                    for( String file : files ) {
//                        if( ! mOperationPaths.contains(file) ){
//                            result = false;
//                        }
//                    }
//                    return result;
//                }
//            } else {
//                return false;
//            }
//        }
    }

    public void onOperationDelete(){
        FmDialogFragment.showDeleteFromSafeDialog(getChildFragmentManager(), mOperationPaths.size(), mOperationPaths.get(0));
    }

    public void onOperationCancel(){
        mOperationPaths.clear();
        mAdapter.notifyDataSetChanged();
        mActionMode.invalidate();
    }

    public void onOperationSelectAll(){
        int total = mAdapter.getCount();
        for (int position=0; position < total; position++) {
            Cursor cursor = (Cursor) mAdapter.getItem(position);
            String path = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_SAFE_PATH);
            if(!mOperationPaths.contains(path)) {
                mOperationPaths.add(path);
            }
        }
        mAdapter.notifyDataSetChanged();
        mActionMode.invalidate();
    }

    public void onOperationMove() {
        FmDialogFragment.showMoveFromSafeDialog(getChildFragmentManager(), mOperationPaths.size(), mOperationPaths.get(0));
    }

    @TargetApi(11)
    @Override
    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
        if (dialog_id == FmDialogFragment.MOVE_FROM_SAFE_DIALOG) {
            DecodeSafeBoxFile task = new DecodeSafeBoxFile(this);
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
                task.execute(mOperationPaths.toArray(new String[mOperationPaths.size()]));
            } else {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mOperationPaths.toArray(new String[mOperationPaths.size()]));
            }
        } else if(dialog_id == FmDialogFragment.DELETE_FROM_SAFE_DIALOG) {
            DeleteSafeBoxFile task = new DeleteSafeBoxFile(this);
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
                task.execute(mOperationPaths.toArray(new String[mOperationPaths.size()]));
            } else {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mOperationPaths.toArray(new String[mOperationPaths.size()]));
            }
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.file_operation_delete:
                onOperationDelete();
                break;
            case R.id.file_operation_cancel:
                onOperationCancel();
                mode.finish();
                break;
            case R.id.file_operation_selectall:
                onOperationSelectAll();
                break;
            case R.id.file_operation_move:
                onOperationMove();
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mActionMode = null;
        mOperationPaths.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = SafeDataStructs.SafeColumns.CONTENT_URI;

        if(baseUri!=null){
            return new CursorLoader(this.getSherlockActivity(), baseUri,
                    SafeDataStructs.SafeColumns.SAFE_PROJECTION, SafeDataStructs.SafeColumns.CATEGORY+"=?", new String[]{mCategory+""},
                    null);
        }
        else{
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        setGridShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    private void addSafe() {
		if (mListener != null) {
            mListener.onSafeDetailAdd(mCategory);
        }
	}
	
	public void setSafeCategory(int category) {
		mCategory = category;
	}

    public interface SafeBoxDetailListener {
        public void onSafeDetailAdd(int category);
    }

    public SafeBoxDetailListener mListener;

    public void setSafeBoxDetailListener(SafeBoxDetailListener listener) {
        mListener = listener;
    }

    static class DeleteSafeBoxFile extends AsyncTask<String[], Void, String[]> {

        WeakReference<SafeBoxGrouperFragment> mWeakFragment;

        public DeleteSafeBoxFile(SafeBoxGrouperFragment fragment) {
            mWeakFragment = new WeakReference<SafeBoxGrouperFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mWeakFragment!=null) {
                SafeBoxGrouperFragment fragment = mWeakFragment.get();
                if (fragment!=null) {
                    fragment.showProgressDialog();
                }
            }
        }

        @Override
        protected void onPostExecute(String[] deleted) {
            super.onPostExecute(deleted);
            if(mWeakFragment!=null) {
                SafeBoxGrouperFragment fragment = mWeakFragment.get();
                if (fragment!=null) {
                    fragment.dismissProgressDialog();
                    fragment.removeOperationFiles(deleted);
                }
            }
        }

        @Override
        protected String[] doInBackground(String[]... strings) {
            ArrayList<String> deletedPaths = new ArrayList<String>();
            String[] paths = strings[0];
            int size = paths.length;
            for (int i = 0; i<size; i++) {
                String safe_path = paths[i];
                try{
                    new File(safe_path).delete();
                    FileManager.getAppContext().getContentResolver().delete(SafeDataStructs.SafeColumns.CONTENT_URI,
                            SafeDataStructs.SafeColumns.SAFE_PATH+"=?",
                            new String[]{safe_path});
                    deletedPaths.add(safe_path);
                }catch(Exception e) {
                    e.printStackTrace();
                }finally{
                }
            }
            return deletedPaths.toArray(new String[deletedPaths.size()]);
        }


    }

    static class DecodeSafeBoxFile extends AsyncTask<String[], Void, String[]> {

        WeakReference<SafeBoxGrouperFragment> mWeakFragment;

        public DecodeSafeBoxFile(SafeBoxGrouperFragment fragment) {
            mWeakFragment = new WeakReference<SafeBoxGrouperFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(mWeakFragment!=null) {
                SafeBoxGrouperFragment fragment = mWeakFragment.get();
                if (fragment!=null) {
                    fragment.showProgressDialog();
                }
            }
        }

        @Override
        protected String[] doInBackground(String[]... strings) {
            ArrayList<String> deletedPaths = new ArrayList<String>();
            String[] paths = strings[0];
            int size = paths.length;
            for (int i = 0; i<size; i++) {
                String safe_path = paths[i];
                Cursor cursor = null;
                try{
                    cursor = FileManager.getAppContext().getContentResolver().query(SafeDataStructs.SafeColumns.CONTENT_URI,
                            SafeDataStructs.SafeColumns.SAFE_PROJECTION,
                            SafeDataStructs.SafeColumns.SAFE_PATH+"=?",
                            new String[]{safe_path}, null);
                    if (cursor!=null && cursor.moveToFirst()) {
                        String path = cursor.getString(SafeDataStructs.SafeColumns.FIELD_INDEX_ORIGINAL_PATH);
                        int category = cursor.getInt(SafeDataStructs.SafeColumns.FIELD_INDEX_CATEGORY);
                        new File(path).getParentFile().mkdirs();
                        CryptUtil.decryptOneFile(safe_path);
                        new File(safe_path).renameTo(new File(path));
                        Uri uri = null;
                        switch (category){
                            case FileUtils.FILE_TYPE_VIDEO:
                                VideoObject vo = new VideoObject(path);
                                Log.i(TAG, "New VideoObject is " + vo);
                                ContentValues vcv = new ContentValues();
                                vo.toContentValues(vcv);
                                uri = FileManager.getAppContext().getContentResolver().insert(DataStructures.VideoColumns.CONTENT_URI, vcv);
                                break;
                            case FileUtils.FILE_TYPE_IMAGE:
                                ImageObject io = new ImageObject(path);
                                Log.i(TAG, "New ImageObject is " + io);
                                ContentValues icv = new ContentValues();
                                io.toContentValues(icv);
                                uri = FileManager.getAppContext().getContentResolver().insert(DataStructures.ImageColumns.CONTENT_URI, icv);
                                break;
                            case FileUtils.FILE_TYPE_AUDIO:
                                ImageObject ao = new ImageObject(path);
                                ContentValues acv = new ContentValues();
                                ao.toContentValues(acv);
                                uri = FileManager.getAppContext().getContentResolver().insert(DataStructures.AudioColumns.CONTENT_URI, acv);
                                break;
                        }
                        if(uri!=null) {
                            Log.i(TAG, "inserted uri is " + uri );
                            FileManager.getAppContext().getContentResolver().delete(SafeDataStructs.SafeColumns.CONTENT_URI,
                                    SafeDataStructs.SafeColumns.SAFE_PATH+"=?",
                                    new String[]{safe_path});
                            deletedPaths.add(safe_path);
                            deletedPaths.add(path);
                        }else{
                            new File(path).renameTo(new File(safe_path));
                            CryptUtil.encryptOneFile(safe_path);
                        }
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    if(cursor != null){
                        cursor.close();
                    }
                }
            }

            return deletedPaths.toArray(new String[deletedPaths.size()]);
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if(mWeakFragment!=null) {
                SafeBoxGrouperFragment fragment = mWeakFragment.get();
                if (fragment!=null) {
                    fragment.dismissProgressDialog();
                    int size = result.length;
                    String[] decoded = new String[size/2];
                    String[] recovered = new String[size/2];
                    for(int i=0, j=0;i<size;i++){
                        if(i%2==0)  {
                            decoded[j] = result[i];
                        } else {
                            recovered[j] = result[i];
                            j++;
                        }
                    }
                    fragment.removeOperationFiles(decoded);
                    fragment.scanRecoveredFiles(recovered);
                }
            }

        }
    }

    private ProgressDialog mProgressDialog;
    PowerManager.WakeLock mWakeLock;

    private void showProgressDialog() {
        PowerManager pm = (PowerManager) FileManager.getAppContext().getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag");
        if(mWakeLock!=null)
            mWakeLock.acquire();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
        mProgressDialog.setTitle(R.string.progress_paste_title);//设置标题
        mProgressDialog.setMessage(getString(R.string.progress_paste_content));
        mProgressDialog.setIndeterminate(false);//设置进度条是否为不明确
        mProgressDialog.setCancelable(true);//设置进度条是否可以按退回键取消
        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
//
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                // TODO Auto-generated method stub
//                Field f;
//                try {
//                    f = cancel.getClass().getDeclaredField("value");
//                    f.setAccessible(true);
//                    try {
//                        f.set(cancel, new Boolean("true"));
//                    } catch (IllegalArgumentException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                } catch (NoSuchFieldException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//
//        });
        mProgressDialog.show();
    }

    private void dismissProgressDialog(){
        if(mProgressDialog!=null && mProgressDialog.isShowing())
            mProgressDialog.cancel();
        if(mWakeLock!=null)
            mWakeLock.release();
    }

    private void removeOperationFiles(String[] paths) {
        for(String path:paths){
            mOperationPaths.remove(path);
        }
        mActionMode.invalidate();
        mAdapter.notifyDataSetChanged();
    }

    private void scanRecoveredFiles(final String[] paths) {
        MediaScannerConnection.scanFile(getActivity().getApplicationContext(), paths, null, null);
    }

    @Override
    public String getSearchString() {
        return null;
    }

}
