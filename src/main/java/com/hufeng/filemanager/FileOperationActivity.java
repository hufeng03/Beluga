package com.hufeng.filemanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.dialog.FmDialogFragment;
import com.hufeng.filemanager.ui.FileOperation;
import com.hufeng.playimage.AnimatedImageView;
import com.hufeng.playimage.AnimatorViewProvider;
import com.kanbox.api.Token;

import java.io.File;

/**
 * Created by feng on 13-10-4.
 */
public abstract class FileOperationActivity extends BaseActivity implements
        ActionMode.Callback, FileOperation.FileOperationProvider,
        FmDialogFragment.OnDialogDoneListener, AnimatorViewProvider{

    private ActionMode mActionMode = null;

    public FileOperation mGlobalFileOperation;

//    private ProgressDialog mProgressDialog;
//    PowerManager.WakeLock mWakeLock;

    public ActionMode getActionMode() {
        return mActionMode;
    }

    protected FileOperation getFileOperation(){
    	return mGlobalFileOperation;
    }

    public FileOperation getGlobalFileOperation(){
        return mGlobalFileOperation;
    }

    protected View mBlackCover;
    protected View mRootView;
    protected AnimatedImageView mAnimatedImageView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        FragmentManager fm = getSupportFragmentManager();
        mGlobalFileOperation = (FileOperation) fm.findFragmentByTag("GlobalFileOperation");

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mGlobalFileOperation == null) {
//            mGlobalFileOperation = new FileOperation();
            mGlobalFileOperation = FileOperation.newInstance(FileOperation.OPERATION_MODE.NORMAL.ordinal());
            fm.beginTransaction().add(mGlobalFileOperation, "GlobalFileOperation").commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void enableImageAnimatorView(ViewGroup root) {
        mRootView = root;
        View animatorView = LayoutInflater.from(this).inflate(R.layout.layout_image_detail, root);
    }


    @Override
    public AnimatedImageView getAnimatedImageView() {
        if (mAnimatedImageView == null) {
            mAnimatedImageView = (AnimatedImageView) findViewById(R.id.animated_image);
        }
        return mAnimatedImageView;
    }

    @Override
    public View getRootView() {
        return ((ViewGroup)mRootView).getChildAt(0);
    }

    @Override
    public View getCoverView() {
        if (mBlackCover == null) {
            mBlackCover = mRootView.findViewById(R.id.black);
        }
        return mBlackCover;
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        switch (getFileOperation().getOperationMode()) {
            case SELECT:
                inflater.inflate(R.menu.file_operation_selection_menu, menu);
                break;
            case ADD_SAFE:
                inflater.inflate(R.menu.file_operation_add_safe_menu, menu);
                break;
            case ADD_CLOUD:
                inflater.inflate(R.menu.file_operation_add_cloud_menu, menu);
                break;
            case NORMAL:
            default:
                inflater.inflate(R.menu.file_operation_menu, menu);
                break;
        }

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        int selectedNum = getFileOperation().getFileSelectedSize();

        if (selectedNum == 0) {
            mode.finish();
            mActionMode = null;
        } else {
            mode.setTitle(getString(R.string.multi_select_title, selectedNum));
            if (selectedNum == 1) {
                menu.setGroupVisible(R.id.file_operation_single, true);

                String path = getFileOperation().getSingleSelectedFile();

                int type = FileUtils.getFileType(path);
                MenuItem item1 = menu.findItem(R.id.file_operation_setaswallpaper);
                MenuItem item2 = menu.findItem(R.id.file_operation_setasringtone);
                switch (type) {
                    case FileUtils.FILE_TYPE_IMAGE:
                        if (item1 != null) item1.setVisible(true);
                        if (item2 != null) item2.setVisible(false);
                        break;
                    case FileUtils.FILE_TYPE_AUDIO:
                        if (item1 != null) item1.setVisible(false);
                        if (item2 != null) item2.setVisible(true);
                        break;
                    default:
                        if (item1 != null) item1.setVisible(false);
                        if (item2 != null) item2.setVisible(false);
                        break;
                }

                MenuItem item3 = menu.findItem(R.id.file_operation_addcloud2);
                if (item3 != null) {
                    File file = new File(path);
                    if(!file.isDirectory() && file.canRead() && Constants.SHOW_KANBOX_UPLOAD_ACTION) {
                        item3.setVisible(true);
                    } else {
                        item3.setVisible(false);
                    }
                }
            } else {
                menu.setGroupVisible(R.id.file_operation_single, false);
                MenuItem item1 = menu.findItem(R.id.file_operation_selectall);
                if (item1 != null) item1.setVisible(!getFileOperation().isFileAllSelected());
//                MenuItem item2 = menu.findItem(R.id.file_operation_addcloud);
//                if (item2 != null) item2.setVisible(false);
            }

            if ( getFileOperation().isSelectedAllFavorite() ){
                MenuItem item1 = menu.findItem(R.id.file_operation_removefavorite);
                if (item1 != null) item1.setVisible(true);
                MenuItem item2 = menu.findItem(R.id.file_operation_addfavorite);
                if (item2 != null) item2.setVisible(false);
            } else if ( getFileOperation().isSelectedAllNotFavorite()) {
                MenuItem item1 = menu.findItem(R.id.file_operation_removefavorite);
                if (item1 != null) item1.setVisible(false);
                MenuItem item2 = menu.findItem(R.id.file_operation_addfavorite);
                if (item2 != null) item2.setVisible(true);
            } else {
                MenuItem item1 = menu.findItem(R.id.file_operation_removefavorite);
                if (item1 != null) item1.setVisible(true);
                MenuItem item2 = menu.findItem(R.id.file_operation_addfavorite);
                if (item2 != null) item2.setVisible(true);
            }

        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.file_operation_delete:
                getFileOperation().onOperationDelete(this);
                break;
            case R.id.file_operation_copy:
                getFileOperation().onOperationCopy(this);
                break;
            case R.id.file_operation_move:
                getFileOperation().onOperationMove(this);
                break;
            case R.id.file_operation_cancel:
                getFileOperation().onOperationSelectCancel(this);
                break;
            case R.id.file_operation_selectall:
                getFileOperation().onOperationSelectAll(this);
                break;
            case R.id.file_operation_send:
                getFileOperation().onOperationSend(this);
                break;
            case R.id.file_operation_rename:
                getFileOperation().onOperationRename(this);
                break;
            case R.id.file_operation_viewdetail:
                getFileOperation().onOperationViewDetail(this);
                break;
            case R.id.file_operation_setasringtone:
                getFileOperation().onOperationSetAsRingtone(this);
                break;
            case R.id.file_operation_setaswallpaper:
                getFileOperation().onOperationSetAsWallpaper(this);
                break;
            case R.id.file_operation_addfavorite:
                getFileOperation().onOperationAddFavorite(this);
                break;
            case R.id.file_operation_removefavorite:
                getFileOperation().onOperationRemoveFavorite(this);
                break;
            case R.id.file_operation_addsafe:
                getFileOperation().onOperationAddToSafe(this);
                break;
            case R.id.file_operation_addcloud2:
            case R.id.file_operation_addcloud:
                if (TextUtils.isEmpty(Token.getInstance().getAccessToken())) {
                    getFileOperation().clearOperationFiles();
                    refreshActionMode();
                    Toast.makeText(this, R.string.please_login_kanbox, Toast.LENGTH_SHORT).show();
                    if (R.id.file_operation_addcloud2 == item.getItemId()) {
                        if (this instanceof FileManagerTabActivity) {
                            ((FileManagerTabActivity) this).gotoCloud();
                        }
                    }
                } else {
                    getFileOperation().onOperationAddToCloud(this);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (!isFinishing()) {
            getFileOperation().clearOperationFiles();
            mActionMode = null;
            refreshFiles();
            refreshUI();
        }
    }

    @Override
    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
        switch(dialog_id) {
            case FmDialogFragment.DELETE_DIALOG:
                getFileOperation().onOperationDeleteConfirm();
                break;
            case FmDialogFragment.SELECT_SEND_APP_DIALOG:
                getFileOperation().onOperationSendSelected(this, ((String[]) param)[0], ((String[]) param)[1]);
                break;
            case FmDialogFragment.NEW_DIRECTORY_DIALOG:
                getFileOperation().onOperationCreateConfirm(this, (String) param);
                break;
            case FmDialogFragment.RENAME_DIALOG:
                getFileOperation().onOperationRenameConfirm(this, ((String[]) param)[1]);
                break;
            case FmDialogFragment.ADD_TO_CLOUD_DIALOG:
                getFileOperation().onOperationAddToCloudConfirm(this);
                break;
        }
    }

    public void refreshUI() {
        refreshActionMode();
    }

    private boolean refreshActionMode() {
        int selectedNum = getFileOperation().getFileSelectedSize();

        if (mActionMode == null && selectedNum > 0) {
            mActionMode = startActionMode(this);
        } else if (mActionMode !=null && selectedNum == 0) {
            mActionMode.finish();
            return false;
        } else if (selectedNum > 0) {
            mActionMode.invalidate();
        }
        return true;
    }

    @Override
    public FragmentManager getHostFragmentManager() {
        return getSupportFragmentManager();
    }
}
