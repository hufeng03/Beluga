package com.belugamobile.filemanager.ui;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.belugamobile.filemanager.BelugaActionDelegate;
import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.dialog.BelugaDialogFragment;

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

    private OPERATION_MODE mOperationMode = OPERATION_MODE.NORMAL;

    public enum OPERATION_MODE {
        NORMAL, PICK, COPY_PASTE, CUT_PASTE, EXTRACT_ARCHIVE, CREATE_ARCHIVE;
    }

    boolean mActionModeShowing;

    public OPERATION_MODE getOperationMode() {
        return mOperationMode;
    }

    public interface BelugaActionControllerHostInterface {
        public BelugaFileEntry[] getAllEntries();
        public void invalidate();
    }

    BelugaActionControllerHostInterface mHost;

    private class SelectionDataWrapper extends HashSet<BelugaFileEntry> {
        public void clear() {
            if (size() > 0) {
                super.clear();
                invalidate();
            }
        }

        @Override
        public boolean add(BelugaFileEntry entry) {
            boolean result = super.add(entry);
            if (result) {
                invalidate();
            }
            return result;
        }

        @Override
        public boolean remove(Object entry) {
            boolean result = super.remove(entry);
            if (result) {
                invalidate();
            }
            return result;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            boolean result = super.removeAll(collection);
            if (result) {
                invalidate();
            }
            return result;
        }

        public BelugaFileEntry[] getAll() {
            return super.toArray(new BelugaFileEntry[size()]);
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
            case NORMAL:
            default:
                inflater.inflate(R.menu.beluga_context_action_menu, menu);
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
            menu.setGroupVisible(R.id.file_operation_single, selectedNum == 1);

            MenuItem item = menu.findItem(R.id.file_operation_selectall);
            if (item != null) item.setVisible(true);
//                if (isSelectedNoneWritable()) {
//                    MenuItem item_delete = menu.findItem(R.id.file_operation_delete);
//                    if (item_delete != null) {
//                        item_delete.setVisible(false);
//                    }
//                    MenuItem item_move = menu.findItem(R.id.file_operation_move);
//                    if (item_move != null) {
//                        item_move.setVisible(false);
//                    }
//                }
            if (isSelectedAllFavorite()){
                MenuItem item1 = menu.findItem(R.id.file_operation_removefavorite);
                if (item1 != null) item1.setVisible(true);
                MenuItem item2 = menu.findItem(R.id.file_operation_addfavorite);
                if (item2 != null) item2.setVisible(false);
            } else {
                MenuItem item1 = menu.findItem(R.id.file_operation_removefavorite);
                if (item1 != null) item1.setVisible(false);
                MenuItem item2 = menu.findItem(R.id.file_operation_addfavorite);
                if (item2 != null) item2.setVisible(true);
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
                performFavorite(mOperationPaths.getAll());
                break;
            case R.id.file_operation_removefavorite:
                performUndoFavorite(mOperationPaths.getAll());
                break;
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

    // TODO: implement this
    public boolean isFolderWholeSelected(String path) {
        for (BelugaFileEntry entry : mOperationPaths) {
            if (entry.isDirectory && entry.path.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEntrySelected(BelugaFileEntry entry) {
        return mOperationPaths.contains(entry);
    }

    public void setEntrySelection(boolean selected, BelugaFileEntry... entries) {
        if (selected) {
            mOperationPaths.addAll(Arrays.asList(entries));
        } else {
            mOperationPaths.removeAll(Arrays.asList(entries));
        }
    }

    public void toggleEntrySelection(BelugaFileEntry entry) {
        if (mOperationPaths.contains(entry)) {
            mOperationPaths.remove(entry);
        } else {
            mOperationPaths.add(entry);
        }
    }

    public void validateAllSelection() {
        List<BelugaFileEntry> deletedEntries = new ArrayList<BelugaFileEntry>();
        for (BelugaFileEntry entry : mOperationPaths) {
            if (!entry.checkExistance()) {
                deletedEntries.add(entry);
            }
        }
        if (deletedEntries.size() > 0) {
            mOperationPaths.removeAll(deletedEntries);
        }
    }

    public BelugaFileEntry[] getAllActionFiles() {
        return mOperationPaths.getAll();
    }

    public void performExtractArchive(String folder, BelugaFileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaExtractArchiveAsyncTask(getActivity().getApplicationContext(), this, folder);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void performCreateArchive(String folder, BelugaFileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaCreateArchiveAsyncTask(getActivity().getApplicationContext(), this, folder);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void performCutPaste(String folder, BelugaFileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaCutPasteAsyncTask(getActivity().getApplicationContext(), this, folder);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void performCopyPaste(String folder, BelugaFileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaCopyPasteAsyncTask(getActivity().getApplicationContext(), this, folder);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void performRename(String newName, BelugaFileEntry entry) {
        BelugaRenameAsyncTask renameTask = new BelugaRenameAsyncTask(getActivity().getApplicationContext(), this);
        renameTask.setNewName(newName);
        mActionAsyncTask = renameTask;
        mActionAsyncTask.executeParallel(entry);
    }

	public void performDeletion(BelugaFileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaDeleteAsyncTask(getActivity().getApplicationContext(), this);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void performCreateFolder(String folder) {
        if (!TextUtils.isEmpty(folder)) {
            mActionAsyncTask = new BelugaCreateFolderAsyncTask(getActivity().getApplicationContext(), this, folder);
            mActionAsyncTask.executeParallel();
        }
    }

    public void performFavorite(BelugaFileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaFavoriteAsyncTask(getActivity().getApplicationContext(), this);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void performUndoFavorite(BelugaFileEntry... entries) {
        if (entries.length > 0) {
            mActionAsyncTask = new BelugaUndoFavoriteAsyncTask(getActivity().getApplicationContext(), this);
            mActionAsyncTask.executeParallel(entries);
        }
    }

    public void onOperationSelectOK() {
        BelugaFileEntry[] entries = mOperationPaths.getAll();
        if(entries != null && entries.length > 0) {
            Intent intent = new Intent();
            if (entries.length == 1) {
                intent.setData(Uri.fromFile(new File(entries[0].path)));
            } else {
                ArrayList<Uri> uris = new ArrayList<Uri>();
                for (BelugaFileEntry entry : entries) {
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
            BelugaFileEntry[] all = mHost.getAllEntries();
            if (all != null && all.length > 0) {
                mOperationPaths.addAll(Arrays.asList(all));
            }
        }
	}

    public void performSearch(String searchString) {
        mActionAsyncTask = new BelugaSearchAsyncTask(getActivity().getApplicationContext(), this, searchString);
        mActionAsyncTask.executeParallel();
    }

	public int getFileSelectedSize(){
		return mOperationPaths.size();
	}

    // TODO: Need to refactor this
    private boolean isSelectedAllFavorite() {
        BelugaFileEntry[] entries = mOperationPaths.getAll();
        for (BelugaFileEntry entry : entries) {
            if (!entry.isFavorite) {
                return false;
            }
        }
        return true;
    }

    private boolean isSelectedNoneWritable() {
        boolean noWritable = true;
        BelugaFileEntry[] entries = mOperationPaths.getAll();
        if (entries != null) {
            for (BelugaFileEntry entry : entries) {
                if (entry.isWritable) {
                    noWritable = false;
                    break;
                }
            }
        }
        return noWritable;
    }
	
	public boolean isFileAllSelected(){
        BelugaFileEntry[] files = null;
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
            int size = getFileSelectedSize();
            if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(message)) {
                BelugaDialogFragment.showProgressDialog(getActivity(), title, message, size);
            }
        }
    }

    @Override
    public void onAsyncTaskProgressUpdated(BelugaFileEntry... entries) {
        final int oldLeftSize = getFileSelectedSize();

        for (BelugaFileEntry entry : entries) {
            if (mOperationPaths.contains(entry)) {
                //TODO: Tell UI to refresh
            }
        }

        mOperationPaths.removeAll(Arrays.asList(entries));
        final int newLeftSize = getFileSelectedSize();
        if (oldLeftSize - newLeftSize > 0) {
            DialogFragment fragment = (DialogFragment) (getFragmentManager().findFragmentByTag(BelugaDialogFragment.PROGRESS_DIALOG_FRAGMENT_TAG));
            if (fragment != null) {
                ProgressDialog dialog = (ProgressDialog)fragment.getDialog();
                int max = dialog.getMax();
                if (max > 0) {
                    int progress = dialog.getProgress();
                    int newProgress = max - newLeftSize;
                    if (newProgress != progress) {
                        dialog.setProgress(newProgress);
                    }
                }
            }
        }
    }

    @Override
    public void onAsyncTaskCompleted(boolean result) {
        mOperationPaths.clear();
        if (getActivity() != null) {
            DialogFragment fragment = (DialogFragment) (getFragmentManager().findFragmentByTag(BelugaDialogFragment.PROGRESS_DIALOG_FRAGMENT_TAG));
            if (fragment != null) {
                fragment.dismissAllowingStateLoss();
            }
            final String toastText = mActionAsyncTask.getCompleteToastContent(getActivity(), result);
            if (!TextUtils.isEmpty(toastText)) {
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
