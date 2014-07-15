package com.hufeng.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.browser.ImageEntry;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.ui.FileOperation;
import com.hufeng.playimage.AnimatorViewProvider;
import com.hufeng.playimage.ImageScaleAnimationController;

public abstract class FileTabFragment extends BaseFragment implements
        FileOperation.FileOperationProvider,
        FileGrouperFragment.FileGrouperCallbacks,
        FileBrowserFragment.FileBrowserCallbacks{

	private final String LOG_TAG = ((Object)this).getClass().getSimpleName();

    private AnimatorViewProvider mAnimatorViewProvider;
    private ImageScaleAnimationController mImageScaleAnimationController;

    public FileGridFragment mCurrentChildFragment;

    public FileOperation getFileOperation() {
        if (mCurrentChildFragment != null) {
            return (mCurrentChildFragment).getFileOperation();
        } else {
            Activity act = getActivity();
            if (act != null && act instanceof FileOperationActivity) {
                return ((FileOperationActivity) act).getGlobalFileOperation();
            }
        }
        return null;
    }

    public void setAnimatorViewProvider(AnimatorViewProvider provider) {

        mAnimatorViewProvider = provider;
    }

    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Constants.RETAIN_FRAGMENT) {
			setRetainInstance(true);
		}
//		setHasOptionsMenu(true);
	}

    @Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		FragmentActivity act = getActivity();
        if (mAnimatorViewProvider == null && act instanceof AnimatorViewProvider) {
            setAnimatorViewProvider((AnimatorViewProvider)getActivity());
        }
        if (mAnimatorViewProvider != null) {
            mImageScaleAnimationController = new ImageScaleAnimationController(act.getSupportFragmentManager(), mAnimatorViewProvider);
        }
	}

    @Override
    public boolean onBackPressed() {
        if(closeImage()) {
            return true;
        } else if (mCurrentChildFragment != null && mCurrentChildFragment.onBackPressed()) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void onFileBrowserItemClick(View v, FileEntry entry) {
        if (isSelectingFile()) {
            selectFile((ImageView) v, entry);
        } else {
            if (entry.isDirectory()) {
                if(mCurrentChildFragment!=null) {
                    if (!TextUtils.isEmpty(mCurrentChildFragment.mSearchString)) {
                        mCurrentChildFragment.mSearchString = null;
                    }
                }
                showFile(entry.path);
            } else {
                if (FileUtils.FILE_TYPE_IMAGE == entry.type) {
                    viewImage((ImageView) v, (ImageEntry) entry, FileUtils.FILE_TYPE_IMAGE);
                } else {
                    FileAction.viewFile(getActivity(), entry.path);
                }
                showFile(entry.path);
            }
        }
    }

    @Override
    public void onFileBrowserItemClose(FileEntry entry) {
        closeFile(entry.path);
    }


    @Override
    public void onFileBrowserItemSelect(View v, FileEntry entry) {
        selectFile((ImageView) v, entry);
    }

    @Override
    public void onFileGrouperItemClick(View v, FileEntry entry) {
        if (isSelectingFile()) {
            selectFile((ImageView) v, entry);
        } else {
            if (FileUtils.FILE_TYPE_IMAGE == entry.type) {
                //view image
                viewImage((ImageView) v, (ImageEntry) entry, FileUtils.FILE_TYPE_IMAGE);
            } else {
                viewFile(entry);
            }
        }
    }

    @Override
    public void onFileGrouperItemSelect(View v, FileEntry entry) {
        selectFile((ImageView) v, entry);
    }

    protected abstract void showFile(String path);
    protected abstract void closeFile(String path);

    protected void viewImage(ImageView v, ImageEntry entry, int type) {
        if (!entry.exist) {
            removeFile(entry.path);
            return;
        }

        if(Constants.PRODUCT_FLAVOR_NAME.equals("google")) {
            if (mImageScaleAnimationController != null)
                mImageScaleAnimationController.animateImageView(v, entry, type);
        } else {
            FileAction.viewFile(getActivity(), entry.path);
        }

    }

    protected boolean closeImage() {
        if (mImageScaleAnimationController != null) {
            return mImageScaleAnimationController.animateImageViewRevert();
        } else {
            return false;
        }
    }

    protected void viewFile(FileEntry entry) {
        if (!entry.exist) {
            removeFile(entry.path);
            return;
        }
        FileAction.viewFile(getActivity(), entry.path);
    }

    protected void selectFile(ImageView v, FileEntry entry) {
        if (!entry.exist) {
            removeFile(entry.path);
            return;
        }
        if (StorageManager.getInstance(getActivity()).isStorage(entry.path)) {
            return;
        }
        if (getFileOperation() != null) {
            getFileOperation().toggleSelection(entry.path);
        }
    }

    protected boolean isSelectingFile() {
        return (getFileOperation() != null && getFileOperation().getFileSelectedSize() > 0 );
    }

    private void removeFile(String path) {
        if (getFileOperation() != null && getFileOperation().isSelected(path)) {
            getFileOperation().toggleSelection(path);
        }
        FileAction.delete(path);
        if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.file_is_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public FragmentManager getHostFragmentManager() {
        return getChildFragmentManager();
    }

//    @Override
//    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
//        switch(dialog_id) {
//            case FmDialogFragment.DELETE_DIALOG:
//                getFileOperation().onOperationDeleteConfirm();
//                break;
//            case FmDialogFragment.SELECT_SEND_APP_DIALOG:
//                getFileOperation().onOperationSendSelected(getActivity(), ((String[]) param)[0], ((String[]) param)[1]);
//                break;
//            case FmDialogFragment.NEW_DIRECTORY_DIALOG:
//                getFileOperation().onOperationCreateConfirm(getActivity(), (String) param);
//                break;
//            case FmDialogFragment.RENAME_DIALOG:
//                getFileOperation().onOperationRenameConfirm(getActivity(), ((String[]) param)[1]);
//                break;
//            case FmDialogFragment.ADD_TO_CLOUD_DIALOG:
//                getFileOperation().onOperationAddToCloudConfirm(getActivity());
//                if(getActivity() instanceof FileManagerTabActivity) {
//                    ((FileManagerTabActivity)getActivity()).gotoCloud();
//                }
//                break;
//        }
//    }

    public void clearSelection() {
        if(mCurrentChildFragment!=null) {
            if (!TextUtils.isEmpty(mCurrentChildFragment.mSearchString) ){
                mCurrentChildFragment.mSearchString = null;
                mCurrentChildFragment.reloadFiles();
            }
        }
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() != null) {
            getActivity().supportInvalidateOptionsMenu();
        }
    }
}
