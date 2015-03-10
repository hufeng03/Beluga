package com.hufeng.filemanager;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;
import com.hufeng.filemanager.intent.Constant;
import com.hufeng.filemanager.ui.BelugaActionController;

/**
 * Created by feng on 13-10-4.
 */
public abstract class BelugaActionControllerActivity extends BelugaBaseActionControllerActivity implements
        BelugaDialogFragment.OnDialogDoneInterface/*, AnimatorViewProvider*/{

    private static final String GLOBAL_ACTION_CONTROLLER_TAG = "GlobalActionController";

    public BelugaActionController mGlobalBelugaActionController;

    public final BelugaActionController getGlobalActionController(){
        return mGlobalBelugaActionController;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        FragmentManager fm = getSupportFragmentManager();
        mGlobalBelugaActionController = (BelugaActionController) fm.findFragmentByTag(GLOBAL_ACTION_CONTROLLER_TAG);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mGlobalBelugaActionController == null) {
            mGlobalBelugaActionController = BelugaActionController.newInstance(BelugaActionController.OPERATION_MODE.NORMAL);
            fm.beginTransaction().add(mGlobalBelugaActionController, GLOBAL_ACTION_CONTROLLER_TAG).commit();
        }
    }

    @Override
    public BelugaActionController getActionController(){
        return mGlobalBelugaActionController;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constant.REQUEST_CODE_PICK_FOLDER_TO_MOVE_FILE:
                getActionController().validateAllSelection();
                break;
            case Constant.REQUEST_CODE_PICK_FOLDER_TO_COPY_FILE:
                getActionController().validateAllSelection();
                break;
        }
    }

    @Override
    public void onDialogOK(int dialogId, String folder, BelugaFileEntry... entries) {
        switch(dialogId) {
            case BelugaDialogFragment.RENAME_DIALOG:
                getActionController().performRename(folder, entries[0]);
                break;
            case BelugaDialogFragment.DELETE_DIALOG:
                getActionController().performDeletion(entries);
                break;
            case BelugaDialogFragment.CUT_PASTE_DIALOG:
                getActionController().performCutPaste(folder, entries);
                break;
            case BelugaDialogFragment.COPY_PASTE_DIALOG:
                getActionController().performCopyPaste(folder, entries);
                break;
            case BelugaDialogFragment.EXTRACT_ARCHIVE_DIALOG:
                getActionController().performExtractArchive(folder, entries);
                break;
            case BelugaDialogFragment.CREATE_ARCHIVE_DIALOG:
                getActionController().performCreateArchive(folder, entries);
                break;
        }
    }



    @Override
    public void onDialogCancel(int dialogId, String folder, BelugaFileEntry... entries) {
        switch (dialogId) {
            case BelugaDialogFragment.RENAME_DIALOG:
                break;
            case BelugaDialogFragment.DELETE_DIALOG:
                break;
            case BelugaDialogFragment.CUT_PASTE_DIALOG:
                break;
            case BelugaDialogFragment.COPY_PASTE_DIALOG:
                break;
        }
    }
}
