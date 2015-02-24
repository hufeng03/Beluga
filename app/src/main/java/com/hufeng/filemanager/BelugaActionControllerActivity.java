package com.hufeng.filemanager;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;
import com.hufeng.filemanager.intent.Constant;
import com.hufeng.filemanager.ui.BelugaActionController;

import java.util.ArrayList;

/**
 * Created by feng on 13-10-4.
 */
public abstract class BelugaActionControllerActivity extends BelugaBaseActionControllerActivity implements
        BelugaDialogFragment.OnDialogDoneInterface/*, AnimatorViewProvider*/{

    public static final int REQUEST_CODE_PICK_FILE_TO_MOVE_TO_SAFEBOX = 1;
    public static final int REQUEST_CODE_AUTHENTICATE_KANBOX = 2;

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
            mGlobalBelugaActionController = BelugaActionController.newInstance(BelugaActionController.OPERATION_MODE.SELECT);
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
                getActionController().validateSelection();
                break;
            case Constant.REQUEST_CODE_PICK_FOLDER_TO_COPY_FILE:
                getActionController().validateSelection();
                break;
        }

//        if (requestCode == REQUEST_CODE_PICK_FILE_TO_MOVE_TO_SAFEBOX) {
//            if (resultCode == RESULT_OK) {
//                Uri uri = data.getData();
//                String[] paths;
//                if (uri != null) {
//                    paths = new String[1];
//                    paths[0] = uri.getPath();
//                } else {
//                    ArrayList<Uri> uris = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//                    paths = new String[uris.size()];
//                    for (int i = 0; i < uris.size(); i++) {
//                        paths[i] = uris.get(i).getPath();
//                    }
//                }
//                getActionController().setSafeboxFiles(paths);
//                getActionController().onOperationAddToSafeConfirm();
//            }
//        }
    }

    @Override
    public void onDialogOK(int dialogId, String folder, FileEntry... entries) {
        switch(dialogId) {
            case BelugaDialogFragment.DELETE_DIALOG:
                getActionController().onOperationDeleteConfirm(entries);
                break;
            case BelugaDialogFragment.CUT_PASTE_DIALOG:
                getActionController().onOperationCutPasteConfirm(folder, entries);
                break;
            case BelugaDialogFragment.COPY_PASTE_DIALOG:
                getActionController().onOperationCopyPasteConfirm(folder, entries);
                break;
        }
    }



    @Override
    public void onDialogCancel(int dialogId, String folder, FileEntry... entries) {
        switch (dialogId) {
            case BelugaDialogFragment.DELETE_DIALOG:
                break;
            case BelugaDialogFragment.CUT_PASTE_DIALOG:
            case BelugaDialogFragment.COPY_PASTE_DIALOG:
//                finish();
                break;
        }
    }
}
