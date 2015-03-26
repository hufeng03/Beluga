package com.belugamobile.filemanager.dialog;

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

import com.belugamobile.filemanager.BelugaArrayRecyclerAdapter;
import com.belugamobile.filemanager.BelugaDisplayMode;
import com.belugamobile.filemanager.BelugaEntryViewHolder;
import com.belugamobile.filemanager.FileEntrySimpleListViewHolder;
import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaSortHelper;
import com.belugamobile.filemanager.helper.BelugaTimeHelper;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.translator.Translator;
import com.belugamobile.filemanager.translator.TranslatorManager;
import com.belugamobile.filemanager.translator.TranslatorViewHolder;
import com.belugamobile.filemanager.utils.FileUtil;
import com.belugamobile.filemanager.utils.MimeUtil;

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
    public static final int EXTRACT_ARCHIVE_DIALOG = 8;
    public static final int CREATE_ARCHIVE_DIALOG = 9;

    public static final int APP_SORT_DIALOG = 10;

    public static final int PROGRESS_DIALOG = 11;

    public static final int CHANGE_LOG_DIALOG = 20;
    public static final int TRANSLATION_CONTRIBUTION_DIALOG = 21;

    public static abstract interface OnDialogDoneInterface {
        public abstract void onDialogOK(int dialogId, String folder, BelugaFileEntry... entries);
        public abstract void onDialogCancel(int dialoId, String folder, BelugaFileEntry... entries);
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

    public static DialogFragment showTranslationContributionDialog(FragmentActivity activity) {
        Bundle data = new Bundle();
        DialogFragment dialog = BelugaDialogFragment.newInstance(TRANSLATION_CONTRIBUTION_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

    public static DialogFragment showChangeLogDialog(FragmentActivity activity) {
        Bundle data = new Bundle();
        DialogFragment dialog = BelugaDialogFragment.newInstance(CHANGE_LOG_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

    public static DialogFragment showSortDialog(FragmentActivity activity, int category) {
        Bundle data = new Bundle();
        data.putInt(CATEGORY_DATA, category);
        DialogFragment dialog = BelugaDialogFragment.newInstance(SORT_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;

    }

    public static DialogFragment showAppSortDialog(FragmentActivity activity) {
        Bundle data = new Bundle();
        data.putInt(CATEGORY_DATA, FileCategoryHelper.CATEGORY_TYPE_APP);
        DialogFragment dialog = BelugaDialogFragment.newInstance(SORT_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

	public static DialogFragment showRenameDialog(FragmentActivity activity, BelugaFileEntry entry){
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
	
	public static DialogFragment showDetailsDialog(FragmentActivity activity, BelugaFileEntry entry){
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

    public static DialogFragment showCopyPasteDialog(FragmentActivity activity, String path, BelugaFileEntry... entries) {
        Bundle data = new Bundle();
        data.putString(FOLDER_PATH_DATA, path);
        data.putParcelableArray(FILE_ARRAY_DATA, entries);
        DialogFragment dialog = BelugaDialogFragment.newInstance(COPY_PASTE_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

    public static DialogFragment showCutPasteDialog(FragmentActivity activity, String path, BelugaFileEntry... entries) {
        Bundle data = new Bundle();
        data.putString(FOLDER_PATH_DATA, path);
        data.putParcelableArray(FILE_ARRAY_DATA, entries);
        DialogFragment dialog = BelugaDialogFragment.newInstance(CUT_PASTE_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

    public static DialogFragment showExtractArchiveDialog(FragmentActivity activity, String path, BelugaFileEntry... entries) {
        Bundle data = new Bundle();
        data.putString(FOLDER_PATH_DATA, path);
        data.putParcelableArray(FILE_ARRAY_DATA, entries);
        DialogFragment dialog = BelugaDialogFragment.newInstance(EXTRACT_ARCHIVE_DIALOG, data);
        dialog.show(activity.getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
        return dialog;
    }

    public static DialogFragment showCreateArchiveDialog(FragmentActivity activity, String path, BelugaFileEntry... entries) {
        Bundle data = new Bundle();
        data.putString(FOLDER_PATH_DATA, path);
        data.putParcelableArray(FILE_ARRAY_DATA, entries);
        DialogFragment dialog = BelugaDialogFragment.newInstance(CREATE_ARCHIVE_DIALOG, data);
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

	
	public static DialogFragment showDeleteDialog(FragmentActivity activity, BelugaFileEntry... paths){
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
        case TRANSLATION_CONTRIBUTION_DIALOG: {
            dialog = buildTranslationContributionDialog();
            break;
        }
        case CHANGE_LOG_DIALOG: {
            View view = View.inflate(getActivity(), R.layout.changelog_dialog, null);
            dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.changelog_dialog_title)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .create();
            break;
        }
        case SORT_DIALOG: {
            View view = View.inflate(getActivity(), R.layout.beluga_sort_dialog, null);
            final RadioButton sortByDate = (RadioButton)view.findViewById(R.id.radio_sort_by_date);
            final RadioButton sortByName = (RadioButton)view.findViewById(R.id.radio_sort_by_name);
            final RadioButton sortBySize = (RadioButton)view.findViewById(R.id.radio_sort_by_size);
            final RadioButton sortByExtension = (RadioButton)view.findViewById(R.id.radio_sort_by_extension);
            final int category = getArguments().getInt(CATEGORY_DATA);
            if (category == FileCategoryHelper.CATEGORY_TYPE_APK || category == FileCategoryHelper.CATEGORY_TYPE_APP) {
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
            .setTitle(R.string.create_folder_dialog_title)
            .setView(contents)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	String name = edit.getText().toString();
                    	File file = new File(path, name);
                    	if(file.exists() && file.isDirectory())
                    	{
                    		Toast.makeText(getActivity(), R.string.folder_already_exist, Toast.LENGTH_SHORT).show();
                    	}
                    	else
                    	{
                            if (file.mkdirs()) {
                                Toast.makeText(getActivity(), R.string.create_folder_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.create_folder_fail, Toast.LENGTH_SHORT).show();
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
			View contents = View.inflate(getActivity(), R.layout.beluga_detail_dialog, null);
			final BelugaFileEntry entry = getArguments().getParcelable(FILE_DATA);
			final TextView content = (TextView)contents.findViewById(R.id.detail_dialog_content);
            String html_string;
            if (entry.isDirectory) {
                String dir_content = null;
                if (entry.childFileCount == 0 && entry.childFolderCount == 0) {
                   dir_content = getString(R.string.empty_folder);
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
                        dir_content += getString(R.string.single_folder, entry.childFolderCount);
                    } else if (entry.childFolderCount > 1) {
                        dir_content += getString(R.string.multiple_folders, entry.childFolderCount);
                    }
                }

                html_string = getString(R.string.details_content_folder,
                        entry.name,
                        entry.parentPath,
                        dir_content,
                        BelugaTimeHelper.getDateString(entry.lastModified));
            } else {
                html_string = getString(R.string.details_content_file,
                        entry.name,
                        entry.parentPath,
                        FileUtil.normalize(entry.size),
                        BelugaTimeHelper.getDateString(entry.lastModified));
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
            final BelugaFileEntry entry = getArguments().getParcelable(FILE_DATA);
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
                final String path = getArguments().getString(FOLDER_PATH_DATA);
                final String title = getString(R.string.cut_paste_confirm_title);
                final String info = getString(R.string.cut_paste_confirm_text, path);
                dialog = buildOperationDialog(title, info);
                break;
            }
            case COPY_PASTE_DIALOG: {
                final String path = getArguments().getString(FOLDER_PATH_DATA);
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
            case EXTRACT_ARCHIVE_DIALOG:
            {
                final String path = getArguments().getString(FOLDER_PATH_DATA);
                final String title = getString(R.string.extract_archive_confirm_dialog_title);
                final String info = getString(R.string.extract_archive_confirm_text, path);
                dialog = buildOperationDialog(title, info);
                break;
            }
            case CREATE_ARCHIVE_DIALOG:
            {
                final String path = getArguments().getString(FOLDER_PATH_DATA);
                final String title = getString(R.string.create_archive_confirm_dialog_title);
                final String info = getString(R.string.create_archive_confirm_text, path);
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

    private Dialog buildTranslationContributionDialog() {

        View contents = View.inflate(getActivity(), R.layout.translation_contribution_dialog, null);
        final RecyclerView list = (RecyclerView)contents.findViewById(R.id.translation_contributor_list);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        final Translator[] allTranslators = TranslatorManager.getAll();
        final int content_height = getResources().getDimensionPixelSize(R.dimen.translator_list_row_height)*allTranslators.length;
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<TranslatorViewHolder>() {
            @Override
            public int getItemCount() {
                return allTranslators.length;
            }

            @Override
            public TranslatorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                int height = parent.getHeight();
                if (height > 0 && height > content_height) {
                    parent.getLayoutParams().height = content_height;
                    parent.invalidate();
                }
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translator_list_row, parent, false);
                return new TranslatorViewHolder(view);
            }

            @Override
            public void onBindViewHolder(TranslatorViewHolder holder, int position) {
                holder.bindTranslator(allTranslators[position]);
            }
        };
        list.setAdapter(adapter);


        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.translation_contribution_dialog_title)
                .setView(contents)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create();
    }

    private Dialog buildOperationDialog(String title, String info){
        View contents = View.inflate(getActivity(), R.layout.beluga_delete_dialog, null);

        final BelugaFileEntry[] entries = (BelugaFileEntry[])getArguments().getParcelableArray(FILE_ARRAY_DATA);
        final TextView text = (TextView)contents.findViewById(R.id.delete_dialog_content);
        final RecyclerView list = (RecyclerView)contents.findViewById(R.id.delete_dialog_file_list);
        text.setText(info);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        final int content_height = getResources().getDimensionPixelSize(R.dimen.simple_file_list_row_height)*entries.length;
        BelugaArrayRecyclerAdapter<BelugaFileEntry> adapter = new BelugaArrayRecyclerAdapter<BelugaFileEntry>(
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
                                    (BelugaFileEntry[]) getArguments().getParcelableArray(FILE_ARRAY_DATA));
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
                                    (BelugaFileEntry[]) getArguments().getParcelableArray(FILE_ARRAY_DATA));
                        }
                    }
                })
                .setCancelable(false)
                .create();
    }
}
