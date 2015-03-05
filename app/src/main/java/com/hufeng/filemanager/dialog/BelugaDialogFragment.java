package com.hufeng.filemanager.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hufeng.filemanager.BelugaArrayRecyclerAdapter;
import com.hufeng.filemanager.BelugaDisplayMode;
import com.hufeng.filemanager.BelugaEntryViewHolder;
import com.hufeng.filemanager.CategorySelectEvent;
import com.hufeng.filemanager.FileEntrySimpleListViewHolder;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.helper.BelugaSortHelper;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.utils.FileUtil;
import com.hufeng.filemanager.utils.MimeUtil;
import com.hufeng.filemanager.utils.TimeUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class BelugaDialogFragment extends DialogFragment{

    private static final String TAG = "BelugaDialogFragment";

    private String CORRECT_FILENAME = "^[^:*?\"/,|<>]+$";
	
	private int mDialogId = -1;

    public static final String DIALOG_FRAGMENT_TAG = "dialog";

    public static final String PROGRESS_DIALOG_FRAGMENT_TAG = "progress_dialog";

    private static final String ARG_DIALOG_ID = "dialog_id";

    public static final String FOLDER_PATH_DATA = "folder";
    public static final String FILE_ARRAY_DATA = "files";
    public static final String FILE_DATA = "file";
    public static final String CATEGORY_DATA = "category";
	
	public static final int RENAME_DIALOG = 1;
	public static final int DETAIL_DIALOG = 2;
	public static final int DELETE_DIALOG = 3;
    public static final int SORT_DIALOG = 4;
	public static final int NEW_FOLDER_DIALOG = 5;
    public static final int COPY_PASTE_DIALOG = 6;
    public static final int CUT_PASTE_DIALOG = 7;

    public static final int APP_SORT_DIALOG = 8;

    public static final int PROGRESS_DIALOG = 9;


    public static abstract interface OnDialogDoneInterface {
        public abstract void onDialogOK(int dialogId, String folder, FileEntry... entries);
        public abstract void onDialogCancel(int dialoId, String folder, FileEntry... entries);
    }

    private OnDialogDoneInterface mListener;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static BelugaDialogFragment newInstance(int id, Bundle data) {
        BelugaDialogFragment f = new BelugaDialogFragment();

        data.putInt(ARG_DIALOG_ID, id);
        f.setArguments(data);

        return f;
    }


    public static DialogFragment showSortDialog(FragmentActivity activity, CategorySelectEvent.CategoryType category) {
        Bundle data = new Bundle();
        data.putString(CATEGORY_DATA, category.toString());
        DialogFragment dialog = BelugaDialogFragment.newInstance(SORT_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;

    }

    public static DialogFragment showAppSortDialog(FragmentActivity activity) {
        Bundle data = new Bundle();
        data.putString(CATEGORY_DATA, CategorySelectEvent.CategoryType.APP.toString());
        DialogFragment dialog = BelugaDialogFragment.newInstance(SORT_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

	public static DialogFragment showRenameDialog(FragmentActivity activity, FileEntry entry){
		Bundle data = new Bundle();
		data.putParcelable(FILE_DATA, entry);
        DialogFragment dialog = BelugaDialogFragment.newInstance(RENAME_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
	}

    public static DialogFragment showProgressDialog(FragmentActivity activity, String title, String message, int max){
        Bundle data = new Bundle();
        data.putString("title", title);
        data.putString("message", message);
        data.putInt("max", max);
        DialogFragment dialog = BelugaDialogFragment.newInstance(PROGRESS_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), PROGRESS_DIALOG_FRAGMENT_TAG);
        return dialog;
    }
	
	public static DialogFragment showDetailsDialog(FragmentActivity activity, FileEntry entry){
		Bundle data = new Bundle();
		data.putParcelable(FILE_DATA, entry);
        DialogFragment dialog = BelugaDialogFragment.newInstance(DETAIL_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
	}
	
	public static DialogFragment showCreateFolderDialog(FragmentActivity activity, String path){
		Bundle data = new Bundle();
		data.putString(FOLDER_PATH_DATA, path);
        DialogFragment dialog = BelugaDialogFragment.newInstance(NEW_FOLDER_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
	}

    public static DialogFragment showCopyPasteDialog(FragmentActivity activity, String path, FileEntry... entries) {
        Bundle data = new Bundle();
        data.putString(FOLDER_PATH_DATA, path);
        data.putParcelableArray(FILE_ARRAY_DATA, entries);
        DialogFragment dialog = BelugaDialogFragment.newInstance(COPY_PASTE_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

    public static DialogFragment showCutPasteDialog(FragmentActivity activity, String path, FileEntry... entries) {
        Bundle data = new Bundle();
        data.putString(FOLDER_PATH_DATA, path);
        data.putParcelableArray(FILE_ARRAY_DATA, entries);
        DialogFragment dialog = BelugaDialogFragment.newInstance(CUT_PASTE_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

//    public static DialogFragment showDownloadFromCloudConfirmDialog(FragmentManager fm, FileEntry remote_path){
//        Bundle data = new Bundle();
//        data.putParcelable("path", remote_path);
//        DialogFragment dialog = BelugaDialogFragment.newInstance(DOWNLOAD_FROM_CLOUD_CONFIRM_DIALOG, data);
//        dialog.show(fm, DIALOG_FRAGMENT_TAG);
//        return dialog;
//    }
//    public static DialogFragment showCreateCloudDirectoryDialog(FragmentManager fm, FileEntry entry){
//        Bundle data = new Bundle();
//        data.putParcelable("root", entry);
//        DialogFragment dialog = BelugaDialogFragment.newInstance(NEW_CLOUD_DIRECTORY_DIALOG, data);
//        dialog.show(fm, DIALOG_FRAGMENT_TAG);
//        return dialog;
//    }
//
//    public static DialogFragment showCloudLogoutDialog(FragmentManager fm){
//        Bundle data = new Bundle();
//        DialogFragment dialog = BelugaDialogFragment.newInstance(CLOUD_LOGOUT_DIALOG, data);
//        dialog.show(fm, DIALOG_FRAGMENT_TAG);
//        return dialog;
//    }

	
	public static DialogFragment showDeleteDialog(FragmentActivity activity, FileEntry... paths){
		Bundle data = new Bundle();
        data.putParcelableArray(FILE_ARRAY_DATA, paths);
        DialogFragment dialog = BelugaDialogFragment.newInstance(DELETE_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
	}



    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "onAttach");
        super.onAttach(activity);
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            if ((parentFragment instanceof OnDialogDoneInterface)) {
                mListener = ((OnDialogDoneInterface) parentFragment);
                return;
            }
        }

        if ((activity instanceof OnDialogDoneInterface)) {
            mListener = ((OnDialogDoneInterface) activity);
        }
    }

    @Override
    public void onDetach() {
        Log.i(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }



    @Override
	public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mDialogId = getArguments().getInt(ARG_DIALOG_ID);

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Translucent);
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.i(TAG, "onCreateDialog");

        Dialog dialog = null;
		switch(mDialogId)
		{
        case SORT_DIALOG: {
            View view = View.inflate(getActivity(), R.layout.sort_dialog, null);
            final RadioButton sortByDate = (RadioButton)view.findViewById(R.id.radio_sort_by_date);
            final RadioButton sortByName = (RadioButton)view.findViewById(R.id.radio_sort_by_name);
            final RadioButton sortBySize = (RadioButton)view.findViewById(R.id.radio_sort_by_size);
            final RadioButton sortByExtension = (RadioButton)view.findViewById(R.id.radio_sort_by_extension);
            final CategorySelectEvent.CategoryType category =
                    CategorySelectEvent.CategoryType.valueOf(getArguments().getString(CATEGORY_DATA));
            if (category == CategorySelectEvent.CategoryType.APP || category == CategorySelectEvent.CategoryType.APK) {
                sortByExtension.setVisibility(View.GONE);
            }
            BelugaSortHelper.SORTER sorter = BelugaSortHelper.getFileSorter(getActivity(), category);
            switch (sorter.field) {
                case DATE:
                    sortByDate.setChecked(true);
                    break;
                case NAME:
                    sortByName.setChecked(true);
                    break;
                case SIZE:
                    sortBySize.setChecked(true);
                    break;
                case EXTENSION:
                    sortByExtension.setChecked(true);
                    break;
            }

            Button.OnClickListener onClickListener = new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.radio_sort_by_date:
                            BelugaSortHelper.saveFileSorter(v.getContext(), category,
                                    new BelugaSortHelper.SORTER(BelugaSortHelper.SORT_FIELD.DATE, BelugaSortHelper.SORT_ORDER.DESC));
                            break;
                        case R.id.radio_sort_by_name:
                            BelugaSortHelper.saveFileSorter(v.getContext(), category,
                                    new BelugaSortHelper.SORTER(BelugaSortHelper.SORT_FIELD.NAME, BelugaSortHelper.SORT_ORDER.ASC));
                            break;
                        case R.id.radio_sort_by_size:
                            BelugaSortHelper.saveFileSorter(v.getContext(), category,
                                    new BelugaSortHelper.SORTER(BelugaSortHelper.SORT_FIELD.SIZE, BelugaSortHelper.SORT_ORDER.DESC));
                            break;
                        case R.id.radio_sort_by_extension:
                            BelugaSortHelper.saveFileSorter(v.getContext(), category,
                                    new BelugaSortHelper.SORTER(BelugaSortHelper.SORT_FIELD.EXTENSION, BelugaSortHelper.SORT_ORDER.ASC));
                            break;
                    }
                    dismiss();
                }
            };
            sortByDate.setOnClickListener(onClickListener);
            sortByName.setOnClickListener(onClickListener);
            sortBySize.setOnClickListener(onClickListener);
            sortByExtension.setOnClickListener(onClickListener);
            dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.sort_dialog_title)
                    .setView(view)
                    .setCancelable(false)
                    .create();
            break;
        }
		case NEW_FOLDER_DIALOG:
		{
            View contents = View.inflate(getActivity(), R.layout.new_directory_dialog, null);
            final String path = getArguments().getString(FOLDER_PATH_DATA);
            final EditText edit = (EditText)contents.findViewById(R.id.new_directory_dialog_edit);
            dialog = new AlertDialog.Builder(getActivity())
            .setTitle(R.string.new_directory_dialog_title)
            .setView(contents)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	String name = edit.getText().toString();
                    	File file = new File(path, name);
                    	if(file.exists() && file.isDirectory())
                    	{
                    		Toast.makeText(getActivity(), R.string.new_directory_alreay_exist, Toast.LENGTH_SHORT).show();
                    	}
                    	else
                    	{
                            if (file.mkdirs()) {
                                Toast.makeText(getActivity(), R.string.create_directory_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.create_directory_fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            })
            .setCancelable(false)
            .create();
            break;
		}
		case DETAIL_DIALOG:
		{
			View contents = View.inflate(getActivity(), R.layout.detail_dialog, null);
			final FileEntry entry = getArguments().getParcelable(FILE_DATA);
			final TextView content = (TextView)contents.findViewById(R.id.detail_dialog_content);
            String html_string;
            if (entry.isDirectory) {
                String dir_content = null;
                if (entry.childFileCount == 0 && entry.childFolderCount == 0) {
                   dir_content = getString(R.string.empty_dir);
                } else {
                    if (entry.childFileCount == 1) {
                        dir_content = getString(R.string.single_file, entry.childFileCount);
                    } else if (entry.childFileCount > 1) {
                        dir_content = getString(R.string.multiple_files, entry.childFileCount);
                    }

                    if (!TextUtils.isEmpty(dir_content) && entry.childFolderCount > 0) {
                        dir_content += ",";
                    }

                    if (entry.childFolderCount == 1) {
                        dir_content += getString(R.string.single_dir, entry.childFolderCount);
                    } else if (entry.childFolderCount > 1) {
                        dir_content += getString(R.string.multiple_dirs, entry.childFolderCount);
                    }
                }

                html_string = getString(R.string.details_content_dir,
                        entry.name,
                        entry.parentPath,
                        dir_content,
                        TimeUtil.getDateString(entry.lastModified));
            } else {
                html_string = getString(R.string.details_content_file,
                        entry.name,
                        entry.parentPath,
                        FileUtil.normalize(entry.size),
                        TimeUtil.getDateString(entry.lastModified));
            }


            String permission = null;
            String owner = null;
            BufferedWriter out;
            BufferedReader in;
            String[] splits = null;
            try {
                Process proc = Runtime.getRuntime().exec((entry.isDirectory?"ls -ld ":"ls -l ")+entry.path);
                out = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
                in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line = "";
                if((line = in.readLine()) != null) {
                    splits = line.split("\\s+");
                }
                proc.waitFor();
                in.close();
                out.close();
                if (splits != null) {
                    permission = splits[0].substring(1);
                    owner = splits[2]+":"+splits[1];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(permission) && !TextUtils.isEmpty(owner)) {
                html_string += getString(R.string.details_permission_owner, permission, owner);
            }

            content.setText(Html.fromHtml(html_string));

            dialog = new AlertDialog.Builder(getActivity())
			.setTitle(R.string.detail_dialog_title)
			.setView(contents)
			.setPositiveButton(android.R.string.ok,null)
			.setCancelable(true)
			.create();
            break;
		}
		case RENAME_DIALOG:
		{
            View contents = View.inflate(getActivity(), R.layout.rename_dialog, null);
            final FileEntry entry = getArguments().getParcelable(FILE_DATA);
            final EditText edit = (EditText)contents.findViewById(R.id.rename_dialog_edit);
            edit.setText(entry.name);
            dialog = new AlertDialog.Builder(getActivity())
            .setTitle(R.string.rename_dialog_title)
            .setView(contents)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, 
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	String new_name = edit.getText().toString();

                    	if(TextUtils.isEmpty(new_name))
                    	{
                    		Toast.makeText(getActivity(), R.string.rename_empty_string, Toast.LENGTH_SHORT).show();
                    		return;
                    	}

                    	if(!TextUtils.isEmpty(entry.extension) && TextUtils.isEmpty(MimeUtil.getExtension(new_name))){
                    		new_name += "." + entry.extension;
                    	}

                        String dir_str = entry.parentPath;
                        File new_file = new File(dir_str, new_name);

                        if(new_file.exists()) {
                            Toast.makeText(getActivity(), R.string.rename_same_name_string, Toast.LENGTH_SHORT).show();
                        } else {
                            if (mListener != null) {
                                mListener.onDialogOK(
                                        getArguments().getInt(ARG_DIALOG_ID),
                                        new_name,
                                        entry);
                            }
                        }
                    }
            })
            .setCancelable(false)
            .create();

            break;
		}
        case PROGRESS_DIALOG: {
            final String title = getArguments().getString("title");
            final String message = getArguments().getString("message");
            final int max = getArguments().getInt("max");
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            if (max > 0) {
                progressDialog.setMax(max);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            } else  {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            }
            progressDialog.setIndeterminate(false);
            dialog = progressDialog;
            break;
        }
            case CUT_PASTE_DIALOG: {
                String path = getArguments().getString(FOLDER_PATH_DATA);
                final String title = getString(R.string.cut_paste_confirm_title);
                final String info = getString(R.string.cut_paste_confirm_text, path);
                dialog = buildOperationDialog(title, info);
                break;
            }
            case COPY_PASTE_DIALOG: {
                String path = getArguments().getString(FOLDER_PATH_DATA);
                final String title = getString(R.string.copy_paste_confirm_title);
                final String info = getString(R.string.copy_paste_confirm_text, path);
                dialog = buildOperationDialog(title, info);
                break;
            }
            case DELETE_DIALOG:
            {
                final String title = getString(R.string.delete_confirm_dialog_title);
                final String info = getString(R.string.delete_confirm_text);
                dialog = buildOperationDialog(title, info);
                break;
            }
        default:
        	throw new IllegalArgumentException("unknown dialog id:"+mDialogId);
		}
        if (dialog != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            return dialog;
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
	}


    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        // This is a bug in support library
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    private Dialog buildOperationDialog(String title, String info){
        View contents = View.inflate(getActivity(), R.layout.delete_dialog, null);

        final FileEntry[] entries = (FileEntry[])getArguments().getParcelableArray(FILE_ARRAY_DATA);
        final TextView text = (TextView)contents.findViewById(R.id.delete_dialog_content);
        final RecyclerView list = (RecyclerView)contents.findViewById(R.id.delete_dialog_file_list);
        text.setText(info);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        final int content_height = getResources().getDimensionPixelSize(R.dimen.simple_file_list_row_height)*entries.length;
        BelugaArrayRecyclerAdapter<FileEntry, FileEntrySimpleListViewHolder> adapter = new BelugaArrayRecyclerAdapter<FileEntry, FileEntrySimpleListViewHolder>(
                getActivity(),
                BelugaDisplayMode.LIST,
                new BelugaEntryViewHolder.Builder() {
                    @Override
                    public BelugaEntryViewHolder createViewHolder(ViewGroup parent, int type) {
                        int height = parent.getHeight();
                        if (height > 0 && height > content_height) {
                            parent.getLayoutParams().height = content_height;
                            parent.invalidate();
                        }
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_file_list_row, parent, false);
                        return new FileEntrySimpleListViewHolder(view);
                    }
                });
        list.setAdapter(adapter);
        adapter.setData(Arrays.asList(entries));

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(contents)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onDialogOK(
                                    getArguments().getInt(ARG_DIALOG_ID),
                                    getArguments().getString(FOLDER_PATH_DATA),
                                    (FileEntry[]) getArguments().getParcelableArray(FILE_ARRAY_DATA));
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mListener != null) {
                            mListener.onDialogCancel(
                                    getArguments().getInt(ARG_DIALOG_ID),
                                    getArguments().getString(FOLDER_PATH_DATA),
                                    (FileEntry[]) getArguments().getParcelableArray(FILE_ARRAY_DATA));
                        }
                    }
                })
                .setCancelable(false)
                .create();
    }
}
