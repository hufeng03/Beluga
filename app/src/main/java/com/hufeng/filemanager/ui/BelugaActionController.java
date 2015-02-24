package com.hufeng.filemanager.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.hufeng.filemanager.BelugaActionDelegate;
import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.dialog.BelugaDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class BelugaActionController extends Fragment implements ActionMode.Callback,
        BelugaActionAsyncTask.BelugaActionAsyncTaskCallbackDelegate {

    private static final String TAG = "BelugaActionController";
    private static final String FILE_OPERATION_MODE_ARGUMENT = "file_operation_argument";

    private OPERATION_MODE mOperationMode = OPERATION_MODE.SELECT;

    public enum OPERATION_MODE {
        SELECT, PICK, COPY_PASTE, CUT_PASTE;
    }

    boolean mActionModeShowing;

    public OPERATION_MODE getOperationMode() {
        return mOperationMode;
    }

    public interface BelugaActionControllerHostInterface {
        public FileEntry[] getAllEntries();
        public void invalidate();
    }

    BelugaActionControllerHostInterface mHost;

    private class SelectionDataWrapper extends HashSet<FileEntry> {
        public void clear() {
            if (size() > 0) {
                super.clear();
                invalidate();
            }
        }

        @Override
        public boolean add(FileEntry entry) {
            boolean result = super.add(entry);
            invalidate();
            return result;
        }

        @Override
        public boolean remove(Object entry) {
            boolean result = super.remove(entry);
            invalidate();
            return result;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean result = super.removeAll(collection);
            invalidate();
            return result;
        }

        public FileEntry[] getAll() {
            return super.toArray(new FileEntry[size()]);
        }
        
        public FileEntry getSingleOne() {
            final int size = size();
            if (size == 0) {
                return null;
            } else {
                return getAll()[0];
            } 
        }
    }

    private void invalidate() {
        if (mHost != null) {
            mHost.invalidate();
        }
    }

    SelectionDataWrapper mOperationPaths = new SelectionDataWrapper();
    BelugaActionAsyncTask mActionAsyncTask;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHost = (BelugaActionControllerHostInterface) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHost = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mode = getArguments().getString(FILE_OPERATION_MODE_ARGUMENT);
        if (!TextUtils.isEmpty(mode)) {
            mOperationMode = OPERATION_MODE.valueOf(mode);
        }
        setRetainInstance(true);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static BelugaActionController newInstance(OPERATION_MODE mode) {
        BelugaActionController operation = new BelugaActionController();
        Bundle bundle = new Bundle();
        bundle.putString(FILE_OPERATION_MODE_ARGUMENT, mode.toString());
        operation.setArguments(bundle);
        return operation;
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        switch (getOperationMode()) {
            case CUT_PASTE:
                break;
            case COPY_PASTE:
                break;
            case PICK:
                inflater.inflate(R.menu.file_operation_selection_menu, menu);
                break;
            case SELECT:
            default:
                inflater.inflate(R.menu.file_operation_menu, menu);
                break;
        }
        mActionModeShowing = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(R.color.primary_color_dark));
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        int selectedNum = getFileSelectedSize();
        int menuSize = menu.size();
        if (selectedNum == 0 || menuSize == 0) {
            mode.finish();
        } else {
            mode.setTitle(String.valueOf(selectedNum));
            if (selectedNum == 1) {
                menu.setGroupVisible(R.id.file_operation_single, true);

                FileEntry entry = getSingleSelectedFile();

                boolean can_write = entry.isWritable;
                if (can_write && Constants.TRY_TO_TEST_WRITE) {
                    if (entry.isDirectory) {
                        if (new File(entry.path, ".test_writable").mkdir()) {
                            new File(entry.path, ".test_writable").delete();
                        } else {
                            can_write = false;
                        }
                    } else {
                        if (new File(entry.path).renameTo(new File(entry.path+"_tmp"))) {
                            new File(entry.path+"_tmp").renameTo(new File(entry.path));
                        } else {
                            can_write = false;
                        }
                    }
                }
                if (!can_write) {
                    MenuItem item_delete = menu.findItem(R.id.file_operation_delete);
                    if (item_delete != null) {
                        item_delete.setVisible(false);
                    }
                    MenuItem item_rename = menu.findItem(R.id.file_operation_rename);
                    if (item_rename != null) {
                        item_rename.setVisible(false);
                    }
                    MenuItem item_move = menu.findItem(R.id.file_operation_move);
                    if (item_move != null) {
                        item_move.setVisible(false);
                    }
                }

            } else {
                menu.setGroupVisible(R.id.file_operation_single, false);
                MenuItem item1 = menu.findItem(R.id.file_operation_selectall);
                if (item1 != null) item1.setVisible(!isFileAllSelected());

                if (isSelectedAllCanNotWrite()) {
                    MenuItem item_delete = menu.findItem(R.id.file_operation_delete);
                    if (item_delete != null) {
                        item_delete.setVisible(false);
                    }
                    MenuItem item_move = menu.findItem(R.id.file_operation_move);
                    if (item_move != null) {
                        item_move.setVisible(false);
                    }
                }
            }

            if (isSelectedAllFavorite()){
                MenuItem item1 = menu.findItem(R.id.file_operation_removefavorite);
                if (item1 != null) item1.setVisible(true);
                MenuItem item2 = menu.findItem(R.id.file_operation_addfavorite);
                if (item2 != null) item2.setVisible(false);
            } else {
                MenuItem item2 = menu.findItem(R.id.file_operation_addfavorite);
                if (item2 != null) item2.setVisible(true);
                MenuItem item1 = menu.findItem(R.id.file_operation_removefavorite);
                if (item1 != null) item1.setVisible(false);
            }

        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        super.onDestroyOptionsMenu();
        mOperationPaths.clear();
        mActionModeShowing = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(getActivity().getResources().getColor(android.R.color.transparent));
        }
    }

    public boolean isActionModeShowing() {
        return mActionModeShowing;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.file_operation_delete:
                BelugaActionDelegate.delete(getActivity(), mOperationPaths.getAll());
                break;
            case R.id.file_operation_copy:
                BelugaActionDelegate.copy(getActivity(), mOperationPaths.getAll());
                break;
            case R.id.file_operation_move:
                BelugaActionDelegate.cut(getActivity(), mOperationPaths.getAll());
                break;
            case R.id.file_operation_selectok:
                onOperationSelectOK();
                break;
            case R.id.file_operation_selectall:
                onOperationSelectAll();
                break;
            case R.id.file_operation_send:
                BelugaActionDelegate.share(getActivity(), mOperationPaths.getAll());
                break;
            case R.id.file_operation_rename:
                BelugaActionDelegate.rename(getActivity(), mOperationPaths.getAll());
                break;
            case R.id.file_operation_details:
                BelugaActionDelegate.details(getActivity(), mOperationPaths.getAll());
                break;
            case R.id.file_operation_addfavorite:
                onOperationAddFavorite();
                break;
            case R.id.file_operation_removefavorite:
                onOperationRemoveFavorite();
                break;
//            case R.id.file_operation_addsafe:
//                onOperationAddToSafe();
//                break;
//            case R.id.file_operation_safe_delete:
//                onOperationSafeDelete();
//                break;
//            case R.id.file_operation_safe_move:
//                onOperationSafeMove();
//                break;
//            case R.id.file_operation_safe_selectall:
//                onOperationSelectAll();
//                break;
//            case R.id.file_operation_addcloud2:
//            case R.id.file_operation_addcloud:
//                if (TextUtils.isEmpty(Token.getInstance().getAccessToken())) {
//                    clearOperationFiles();
//                    refreshActionMode();
//                    Toast.makeText(this, R.string.please_login_kanbox, Toast.LENGTH_SHORT).show();
//                    if (R.id.file_operation_addcloud2 == item.getItemId()) {
//                        if (this instanceof FileManagerTabActivity) {
//                            ((FileManagerTabActivity) this).gotoCloud();
//                        }
//                    }
//                } else {
//                    onOperationAddToCloud();
//                }
//                break;
            default:
                break;
        }
        return true;
    }


    public void setOperationMode (OPERATION_MODE mode) {
        mOperationMode = mode;
        Bundle data = getArguments();
        if (data != null) {
            data.putString(FILE_OPERATION_MODE_ARGUMENT, mode.toString());
        }
    }

    public void addEntries(FileEntry... entries) {
        mOperationPaths.addAll(Arrays.asList(entries));
    }

    public boolean isSelected(FileEntry entry) {
        return mOperationPaths.contains(entry);
    }

    public void removeSelection(FileEntry entry) {
        if (mOperationPaths.contains(entry)) {
            mOperationPaths.remove(entry);
        }
    }

    public void toggleSelection(FileEntry entry) {
        if (mOperationMode != OPERATION_MODE.CUT_PASTE
                && mOperationMode != OPERATION_MODE.COPY_PASTE) {
            if (mOperationPaths.contains(entry)) {
                mOperationPaths.remove(entry);
            } else {
                mOperationPaths.add(entry);
            }
        }
    }

    public void validateSelection() {
        List<FileEntry> deletedEntreis = new ArrayList<FileEntry>();
        for (FileEntry entry : mOperationPaths) {
            if (!entry.checkExistance()) {
                deletedEntreis.add(entry);
            }
        }
        if (deletedEntreis.size() > 0) {
            mOperationPaths.removeAll(deletedEntreis);
        }
    }

    public FileEntry[] getAllActionFiles() {
        return mOperationPaths.getAll();
    }


    public void onOperationCutPasteConfirm(String folder, FileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaMoveAsyncTask(this, folder);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void onOperationCopyPasteConfirm(String folder, FileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaCopyAsyncTask(this, folder);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void onOperationPickConfirm(Context context) {
        getActivity().setResult(Activity.RESULT_OK);

    }

	public void onOperationDeleteConfirm(FileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaDeleteAsyncTask(this);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void onOperationSelectOK() {
        FileEntry[] entries = mOperationPaths.getAll();
        if(entries != null && entries.length > 0) {
            Intent intent = new Intent();
            if (entries.length == 1) {
                intent.setData(Uri.fromFile(new File(entries[0].path)));
            } else {
                ArrayList<Uri> uris = new ArrayList<Uri>();
                for (FileEntry entry : entries) {
                    uris.add(Uri.fromFile(new File(entry.path)));
                }
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            }
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

	public void onOperationSelectAll(){
        if (mHost != null) {
            FileEntry[] all = mHost.getAllEntries();
            if (all != null && all.length > 0) {
                mOperationPaths.addAll(Arrays.asList(all));
            }
        }
	}
	
	public void onOperationAddFavorite(){
        FileEntry[] entries = mOperationPaths.getAll();
        // TODO: modify to use batch processing
        boolean result = true;
        for (FileEntry entry : entries) {
            if (!FileAction.addToFavorite(entry.path)) {
                result = false;
            }
        }
        mOperationPaths.clear();
        if (result) {
            Toast.makeText(getActivity(), R.string.add_favorite_success, Toast.LENGTH_SHORT).show();
        }
    }

    public void onOperationRemoveFavorite(){
        FileEntry[] entries = mOperationPaths.getAll();
        // TODO: modify to use batch processing
        boolean result = true;
        for (FileEntry entry : entries) {
            if (!FileAction.removeFromFavorite(entry.path)) {
                result = false;
            }
        }
        mOperationPaths.clear();
        if (result) {
            Toast.makeText(getActivity(), R.string.remove_favorite_success, Toast.LENGTH_SHORT).show();
        }
    }

    public void onOperationSearchFile(String searchString) {
        mActionAsyncTask = new BelugaSearchAsyncTask(this, searchString);
        mActionAsyncTask.executeParallel();
    }

	public int getFileSelectedSize(){
		return mOperationPaths.size();
	}

    public FileEntry getSingleSelectedFile() {
        return mOperationPaths.getSingleOne();
    }

    public boolean isSelectedAllFavorite() {
        FileEntry[] entries = mOperationPaths.getAll();
        String[] paths = new String[entries.length];
        int i = 0;
        for (FileEntry entry : entries) {
            paths[i++] = entry.path;
        }
        return FileAction.isAllFavorite(paths);
    }

    public boolean isSelectedAllCanNotWrite() {
        boolean flag = true;
        FileEntry[] entries = mOperationPaths.getAll();
        if (entries != null) {
            for (FileEntry entry : entries) {
                if (entry.isWritable) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public boolean isSelectedAllNotFavorite() {
//        return FileAction.isAllNotFavorite(mOperationPaths.getAll());
        return false;
    }
	
	public boolean isFileAllSelected(){
        FileEntry[] files = null;
        if (mHost != null) {
            files = mHost.getAllEntries();
        }
        if( files == null || files.length == 0) {
            return true;
        } else {
            return mOperationPaths.containsAll(Arrays.asList(files));
        }
	}

    @Override
    public void onAsyncTaskStarted() {
        Context context = getActivity();
        if (context != null) {
            final String title = mActionAsyncTask.getProgressDialogTitle(context);
            final String message = mActionAsyncTask.getProgressDialogContent(context);
            BelugaDialogFragment.showProgressDialog(getActivity(), title, message);
        }
    }

    @Override
    public void onAsyncTaskProgressUpdated(FileEntry... progress) {

    }

    @Override
    public void onAsyncTaskCompleted(boolean result) {
        mOperationPaths.clear();
        if (getActivity() != null) {
            DialogFragment fragment = (DialogFragment) (getFragmentManager().findFragmentByTag(BelugaDialogFragment.PROGRESS_DIALOG_FRAGMENT_TAG));
            if (fragment != null) {
                fragment.dismiss();
            }
            final String toastText = mActionAsyncTask.getCompleteToastContent(getActivity(), result);
            if (!TextUtils.isEmpty(toastText)) {
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
