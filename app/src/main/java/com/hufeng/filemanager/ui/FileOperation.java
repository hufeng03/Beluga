package com.hufeng.filemanager.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.hufeng.filemanager.BaseFragment;
import com.hufeng.filemanager.FileManagerTabActivity;
import com.hufeng.filemanager.FileOperationActivity;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.dialog.FmDialogFragment;
import com.hufeng.filemanager.kanbox.KanBoxApi;
import com.hufeng.filemanager.utils.IntentUtils;

import java.io.File;
import java.util.ArrayList;

//import com.umeng.analytics.MobclickAgent;

public class FileOperation extends BaseFragment {

    public static final String TAG = "FileOperation";
    public static final String FILE_OPERATION_MODE_ARGUMENT = "file_operation_argument";

    private FileOperationActivity mOperationActivity;
//    private WeakReference<FileOperationProvider> mWeakProvider;

    private OPERATION_MODE mOperationMode = OPERATION_MODE.NORMAL;

    public enum OPERATION_MODE {
        NORMAL, SELECT, ADD_SAFE, ADD_CLOUD;

        public static OPERATION_MODE valueOf(int value) {
            switch (value) {
                case 0:
                    return NORMAL;
                case 1:
                    return SELECT;
                case 2:
                    return ADD_SAFE;
                case 3:
                    return ADD_CLOUD;
                default:
                    return null;
            }
        }
    }

    public OPERATION_MODE getOperationMode() {
        return mOperationMode;
    }

    private ArrayList<String> mOperationPaths = new ArrayList<String>();
    private ArrayList<String> mCopyPaths = new ArrayList<String>();
    private ArrayList<String> mMovePaths = new ArrayList<String>();
//    private ArrayList<String> mUploadPaths = new ArrayList<String>();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOperationActivity = (FileOperationActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOperationActivity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mode = getArguments().getInt(FILE_OPERATION_MODE_ARGUMENT);
        mOperationMode = OPERATION_MODE.valueOf(mode);
        if (mOperationMode == OPERATION_MODE.ADD_CLOUD || mOperationMode == OPERATION_MODE.ADD_SAFE || mOperationMode == OPERATION_MODE.SELECT) {
            setRetainInstance(false);
        } else {
            setRetainInstance(true);
        }
    }


    public static FileOperation newInstance(int mode) {
        FileOperation operation = new FileOperation();
        Bundle bundle = new Bundle();
        bundle.putInt(FILE_OPERATION_MODE_ARGUMENT, mode);
        operation.setArguments(bundle);
        return operation;
    }

    public void setOperationMode (OPERATION_MODE mode) {
        mOperationMode = mode;
    }

    public boolean isSelected(String path) {
        return mOperationPaths.contains(path);
    }
    
    public boolean isMoving(String path)
    {
    	return mMovePaths.contains(path);
    }
    
    public boolean isCopying(String path)
    {
    	return mCopyPaths.contains(path);
    }
    
    public boolean isMovingOrCopying()
    {
    	if(mMovePaths.size()!=0 || mCopyPaths.size()!=0)
    		return true;
    	else
    		return false;
    }

    
    public interface FileOperationProvider {
        public void refreshFiles();
        public String[] getAllFiles();
        public String getParentFile();
        public FragmentManager getHostFragmentManager();
    }

//    public void setFileOperationProvider(FileOperationProvider provider) {
//        mWeakProvider = new WeakReference<FileOperationProvider>(provider);
//    }

    private final FileOperationProvider getFileOperationProvider() {
        FileOperationProvider provider = null;
//        if (mWeakProvider != null && (provider = mWeakProvider.get()) != null) {
//
//        } else
        if (mOperationActivity != null) {
            provider = mOperationActivity;
        }
        return provider;
    }

    public void setSelection(String path) {
        if (mOperationPaths.contains(path)) {
            mOperationPaths.remove(path);
        } else {
            mOperationPaths.clear();
            mOperationPaths.add(path);
        }
        refresh();
    }

    public void toggleSelection(String path) {
        if (mOperationPaths.contains(path)) {
            mOperationPaths.remove(path);
        } else {
            mOperationPaths.add(path);
        }

        refresh();
    }

    public void setMoveFiles(String[] files) {
        mOperationPaths.clear();
        mMovePaths.clear();
    	for(String path:files)
    		mMovePaths.add(path);
    }

    public void setCopyFiles(String[] files) {
        mOperationPaths.clear();
        mCopyPaths.clear();
        for(String path:files)
            mCopyPaths.add(path);
    }

//    public void setUploadFiles(String[] files) {
//        mOperationPaths.clear();
//        mUploadPaths.clear();
//        for(String path:files)
//            mUploadPaths.add(path);
//    }
	
	public void clearOperationFiles()
	{
    	mOperationPaths.clear();
        mCopyPaths.clear();
        mMovePaths.clear();
//        mUploadPaths.clear();
	}

    public void onOperationCreateConfirm(Context context, final String name) {
        FileOperationProvider provider = getFileOperationProvider();
        if (provider != null) {
            if (new File(provider.getParentFile(), name).mkdirs()) {
                Toast.makeText(context, R.string.create_directory_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.create_directory_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

	public void onOperationPasteConfirm(Context context) {
		final boolean copying = !mCopyPaths.isEmpty();
		final boolean moving = !mMovePaths.isEmpty();
        FileOperationProvider provider = getFileOperationProvider();
        String parent = provider.getParentFile();
        if (TextUtils.isEmpty(parent)) {
            Toast.makeText(context, R.string.cannot_paste_here, Toast.LENGTH_SHORT).show();
            return;
        } else if (!new File(parent).isDirectory()) {
            Toast.makeText(context, R.string.cannot_paste_here, Toast.LENGTH_SHORT).show();
            return;
        } else if (!new File(parent).canWrite()) {
            Toast.makeText(context, R.string.cannot_paste_here, Toast.LENGTH_SHORT).show();
            return;
        }
        if (copying) {
            FileOperationTask task = new FileCopyTask(mOperationActivity, mCopyPaths.toArray(new String[mCopyPaths.size()]));
            task.executeSerial(provider.getParentFile());
            mCopyPaths.clear();
        }

        if (moving) {
            String uploading_path = null;
            int uploading_count = 0;
            ArrayList<String> deleted = new ArrayList<String>();
            for (String path:mMovePaths) {
                if (KanBoxApi.isUploading(path)){
//                    mMovePaths.remove(path);
                    deleted.add(path);
                    uploading_count++;
                    if (uploading_path == null) {
                        uploading_path = path;
                    }
                }
            }

            if (uploading_count != 0) {
                for (String path:deleted) {
                    mMovePaths.remove(path);
                }
                Toast.makeText(context, getString(R.string.cannot_move_uploading, uploading_path, uploading_count), Toast.LENGTH_SHORT).show();
            }
            if (mMovePaths.size() != 0) {
                FileOperationTask task = new FileMoveTask(mOperationActivity, mMovePaths.toArray(new String[mMovePaths.size()]));
                task.executeSerial(provider.getParentFile());
                mMovePaths.clear();
            }
        }

        refresh();

	}
	
	public void onOperationPasteCancel(Context context){
        Intent intent = new Intent(FileManagerTabActivity.ACTION_CANCEL_PASTE_FILES);
        intent.setClassName(context.getPackageName(), FileManagerTabActivity.class.getName());
        context.startActivity(intent);
        mCopyPaths.clear();
        mMovePaths.clear();
        mOperationPaths.clear();
        refresh();
	}
	
	public void onOperationSendSelected(Context context, String app, String name)
	{
        if (mOperationPaths.size() > 0) {
            Intent intent = FileAction.buildSendFile(context, mOperationPaths.toArray(new String[mOperationPaths.size()]));
            if (intent != null) {
                if (app != null && name != null) {
                    //intent.setPackage(app);
                    intent.setComponent(new ComponentName(app, name));
                } else if (app != null) {
                    intent.setPackage(app);
                }
                context.startActivity(intent);
                mOperationPaths.clear();
                refresh();
            }
        }
    }

    public void onOperationAddToCloudConfirm(Context context) {
        String root = null;
        FileOperationProvider provider = getFileOperationProvider();
        if (provider != null && OPERATION_MODE.ADD_CLOUD == mOperationMode) {
            root = provider.getParentFile();
        }
        if(TextUtils.isEmpty(root)) {
//            return;
            root="/";
        }

        for(String path : mOperationPaths) {
            KanBoxApi.getInstance().uploadFile(path, root);
        }
        mOperationPaths.clear();
        refresh();

//        FileOperationTask task = new FileAddToCloudTask(mOperationActivity, mOperationPaths.toArray(new String[mOperationPaths.size()]));
//        task.executeSerial(root);
//        mOperationPaths.clear();
//        refresh();
    }

    public void onOperationAddToSafeConfirm(Context context) {
        FileAddToSafeTask task = new FileAddToSafeTask(mOperationActivity, mOperationPaths.toArray(new String[mOperationPaths.size()]));
        task.executeSerial();
        mOperationPaths.clear();
        refresh();
    }

	public void onOperationDeleteConfirm(Context context)
	{

        String uploading_path = null;
        int uploading_count = 0;

        ArrayList<String> deleted = new ArrayList<String>();
        for (String path:mOperationPaths) {
            if (KanBoxApi.isUploading(path)){
//                    mMovePaths.remove(path);
                deleted.add(path);
                uploading_count++;
                if (uploading_path == null) {
                    uploading_path = path;
                }
            }
        }

        if (uploading_count != 0) {
            for (String path:deleted) {
                mOperationPaths.remove(path);
            }
            Toast.makeText(context, getString(R.string.cannot_move_uploading, uploading_path, uploading_count), Toast.LENGTH_SHORT).show();
        }

        if (mOperationPaths.size() > 0) {
            FileDeleteTask task = new FileDeleteTask(mOperationActivity, mOperationPaths.toArray(new String[mOperationPaths.size()]));
            task.executeSerial();
        }

        mOperationPaths.clear();

        refresh();
	}
	
	
	public void onOperationRenameConfirm(Context context, String new_name){
        if (mOperationPaths.size() == 1) {
            File old_file = new File(mOperationPaths.get(0));
            String dir_str = old_file.getParent();
            File new_file = new File(dir_str, new_name);

            if(new_file.exists() && ((new_file.isDirectory() && old_file.isDirectory()) || (new_file.isFile() && old_file.isFile()))) {
                Toast.makeText(context, R.string.rename_same_name_string, Toast.LENGTH_SHORT).show();
            }
            else {
                FileAction.renameFile(context, old_file, new_file);
                mOperationPaths.clear();
                refresh();
            }
        }
	}

    public void onOperationCreate(Context context) {
        FileOperationProvider provider = getFileOperationProvider();
        if (provider != null) {
            String root = provider.getParentFile();
            FmDialogFragment.showCreateDirectoryDialog(provider.getHostFragmentManager(), root);
        }
    }
	
	public void onOperationDelete(Context context){
        FileOperationProvider provider = getFileOperationProvider();
    	if(mOperationPaths!=null && mOperationPaths.size()>0 ) {
    		FmDialogFragment.showDeleteDialog(provider.getHostFragmentManager(), mOperationPaths.size(), mOperationPaths.get(0));
        }
	}
	
	public void onOperationMove(Context context){
        FileOperationProvider provider = getFileOperationProvider();
    	if(mOperationPaths.size()>0)
    	{
	    	Intent intent = new Intent(FileManagerTabActivity.ACTION_MOVE_FILES);
	    	intent.setClassName(context.getPackageName(), FileManagerTabActivity.class.getName());
	    	intent.putExtra("files", mOperationPaths.toArray(new String[mOperationPaths.size()]));
	    	context.startActivity(intent);
            mOperationPaths.clear();
            refresh();
    	}
	}
	
	public void onOperationCopy(Context context){
    	if(mOperationPaths.size()>0)
    	{
	    	Intent intent = new Intent(FileManagerTabActivity.ACTION_COPY_FILES);
	    	intent.setClassName(context.getPackageName(), FileManagerTabActivity.class.getName());
	    	intent.putExtra("files", mOperationPaths.toArray(new String[mOperationPaths.size()]));
	    	context.startActivity(intent);
            mOperationPaths.clear();
            refresh();
    	}

	}
	
	public void onOperationSelectCancel(Context context){
    	mOperationPaths.clear();
        refresh();
	}

    public void onOperationSelectOK(Context context) {
        if (mOperationPaths.size() > 0) {
//            Intent intent = FileAction.buildSendFile(context, mOperationPaths.toArray(new String[mOperationPaths.size()]));
            Intent intent = new Intent();
            if (mOperationPaths.size() == 1) {
                intent.setData(Uri.fromFile(new File(mOperationPaths.get(0))));
            }
//            else {
//                intent.setData();
//            }
            if (intent != null) {
//                context.startActivity(intent);
                mOperationActivity.setResult(Activity.RESULT_OK, intent);
                mOperationActivity.finish();
            }
        }
    }


	public void onOperationSelectAll(Context context){
        FileOperationProvider provider = getFileOperationProvider();
        String[] all = provider.getAllFiles();
        if (all != null) {
            for(String path:all) {
                if (!mOperationPaths.contains(path))
                    mOperationPaths.add(path);
            }
            refresh();
        }
	}
	
	public void onOperationAddFavorite(Context context){
        FileOperationProvider provider = getFileOperationProvider();
		for(String path:mOperationPaths)
    		FileAction.addToFavorite(path);
    	mOperationPaths.clear();
        refresh();
    	Toast.makeText(context, R.string.add_favorite_success, Toast.LENGTH_SHORT).show();
	}

    public void onOperationRemoveFavorite(Context context){
        FileOperationProvider provider = getFileOperationProvider();
        for(String path:mOperationPaths)
            FileAction.removeFromFavorite(path);
        mOperationPaths.clear();
        refresh();
        Toast.makeText(context, R.string.remove_favorite_success, Toast.LENGTH_SHORT).show();
    }

    public void onOperationAddToSafe(Context context) {
        FileOperationProvider provider = getFileOperationProvider();
        if(mOperationPaths!=null && mOperationPaths.size()>0 ) {
            FmDialogFragment.showAddToSafeDialog(provider.getHostFragmentManager(), mOperationPaths.size(), mOperationPaths.get(0));
        }
    }

    public void onOperationAddToCloud(Context context) {
        FileOperationProvider provider = getFileOperationProvider();
        int directory_count = 0;
        String directory_path = null;
        for(String path:mOperationPaths) {
            if (new File(path).isDirectory()) {
                mOperationPaths.remove(path);
                if (directory_path == null) {
                    directory_path = path;
                }
                directory_count ++;
            }
        }
        if(mOperationPaths!=null && mOperationPaths.size()>0 ) {
            FmDialogFragment.showAddToCloudDialog(provider.getHostFragmentManager(), mOperationPaths.size(), mOperationPaths.get(0), directory_count, directory_path);
        }
    }

	public void onOperationSend(Context context){
		boolean flag = true;
		boolean flag_directory = false;
		if(mOperationPaths!=null && mOperationPaths.size()==1)
		{
			File file = new File(mOperationPaths.get(0));
			if(file.isFile())
			{
				int type = FileUtils.getFileType(file);
				if(type == FileUtils.FILE_TYPE_DOCUMENT || type == FileUtils.FILE_TYPE_AUDIO || type==FileUtils.FILE_TYPE_IMAGE || type==FileUtils.FILE_TYPE_VIDEO)
				{
					flag = false;
				}
			}
		}
		if(mOperationPaths!=null)
		{
			for(String path:mOperationPaths)
			{
				File file = new File(path);
				if(file.isDirectory())
				{
					flag_directory = true;
				}
			}
		}
		
		Intent intent = FileAction.buildSendFile(context, mOperationPaths.toArray(new String[mOperationPaths.size()]));
		ResolveInfo[] apps = IntentUtils.queryAvailableApps(intent);
        apps = IntentUtils.sort(apps);

		if (apps==null || apps.length==0) {
            if(flag_directory) {
                Toast.makeText(context, R.string.directory_cannot_send, Toast.LENGTH_SHORT).show();
            } else if (!flag_directory) {
				Toast.makeText(context, R.string.no_send_app_available, Toast.LENGTH_SHORT).show();
            }
		} else if (apps.length==1) {
			onOperationSendSelected(context, apps[0].activityInfo.packageName, apps[0].activityInfo.name);
		} else {
//            context.startActivity(intent);
//            context.startActivity(IntentUtils.buildChooserIntent(context.getString(R.string.select_send_app_dialog_title), intent, apps));
	    	FmDialogFragment.showSelectSendAppDialog(getFileOperationProvider().getHostFragmentManager(), apps);
		}
	}
	
	public void onOperationSetAsWallpaper(Context context) {
        if (mOperationPaths.size() == 1 ) {
            FileAction.setAsWallpaper(mOperationPaths.get(0));
            mOperationPaths.clear();
            refresh();
        } else {
        }
	}
	
	public void onOperationSetAsRingtone(Context context) {
        if (mOperationPaths.size() == 1 ) {
            FileAction.setAsRingTone(mOperationPaths.get(0));
            mOperationPaths.clear();
            refresh();
        } else {
        }
	}
	
	public void onOperationRename(Context context) {
        FileOperationProvider provider = getFileOperationProvider();
        if (mOperationPaths.size() == 1 ) {
		    FmDialogFragment.showRenameDialog(provider.getHostFragmentManager(), mOperationPaths.get(0));
        }
	}
	
	public void onOperationViewDetail(Context context) {
        FileOperationProvider provider = getFileOperationProvider();
        if (mOperationPaths.size() == 1 ) {
    		FmDialogFragment.showDetailDialog(provider.getHostFragmentManager(), mOperationPaths.get(0));
        }
	}	
	
	public int getFileSelectedSize(){
		if(mOperationPaths!=null)
			return mOperationPaths.size();
		else
			return 0;
	}

    public String getSingleSelectedFile() {
        return mOperationPaths.get(0);
    }

    public boolean isSelectedAllFavorite() {
        return FileAction.isAllFavorite(mOperationPaths.toArray(new String[mOperationPaths.size()]));
    }

    public boolean isSelectedAllCanNotWrite() {
        boolean flag = true;
        for (String file:mOperationPaths) {
            if (new File(file).canWrite()) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    public boolean isSelectedAllNotFavorite() {
        return FileAction.isAllNotFavorite(mOperationPaths.toArray(new String[mOperationPaths.size()]));
    }
	
	public boolean isFileAllSelected(){
        FileOperationProvider provider = getFileOperationProvider();
		if( provider == null ) {
			return true;
		} else {
            if (provider != null) {
                String[] files = provider.getAllFiles();
                if( files == null ) {
                    return true;
                } else {
                    boolean result = true;
                    for( String file : files ) {
                        if( ! mOperationPaths.contains(file) ){
                            result = false;
                        }
                    }
                    return result;
                }
            } else {
                return false;
            }
		}
	}

    private final void refresh() {
        if (mOperationActivity != null) {
            getFileOperationProvider().refreshFiles();
            mOperationActivity.refreshUI();
        }
    }

}
