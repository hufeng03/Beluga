package com.hufeng.filemanager.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.FileUtil;
import com.hufeng.filemanager.utils.NetworkUtil;
import com.hufeng.filemanager.utils.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.hufeng.filemanager.CategoryTab;

public class FmDialogFragment extends SherlockDialogFragment{
    
    private String CORRECT_FILENAME = "^[^*&%?!\\s]+$";
	
	private int mDialogId = -1;

    private static final String ARG_DIALOG_ID = "dialog_id";
	
	public static final int RENAME_DIALOG = 1;
	public static final int DETAIL_DIALOG = 2;
	public static final int DELETE_DIALOG = 3;
	public static final int SELECT_SEND_APP_DIALOG = 4;
	public static final int NEW_DIRECTORY_DIALOG = 5;
//	public static final int MULTISIM_RINGTONE_DIALOG = 6;
	public static final int IMPORT_CONTACT_DIALOG = 7;
    public static final int ADD_TO_SAFE_DIALOG = 8;
    public static final int MOVE_FROM_SAFE_DIALOG = 9;
    public static final int DELETE_FROM_SAFE_DIALOG = 10;
    public static final int NEW_CLOUD_DIRECTORY_DIALOG = 11;
    public static final int ADD_TO_CLOUD_DIALOG = 12;
    public static final int DOWNLOAD_FROM_CLOUD_CONFIRM_DIALOG = 13;
    public static final int CLOUD_DETAIL_DIALOG = 14;
    public static final int CLOUD_RENAME_DIALOG = 15;
    public static final int CLOUD_LOGOUT_DIALOG = 16;

    private OnDialogDoneListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment parentFragment = getParentFragment();
        if (parentFragment != null) {
            if ((parentFragment instanceof OnDialogDoneListener)) {
                mListener = ((OnDialogDoneListener) parentFragment);
                return;
            }
        }

        if ((activity instanceof OnDialogDoneListener)) {
            mListener = ((OnDialogDoneListener) activity);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static void showSelectSendAppDialog(FragmentManager fm, ResolveInfo[] apps){
		Bundle data = new Bundle();
		data.putParcelableArray("available_apps", apps);
		final FmDialogFragment dialog = FmDialogFragment.newInstance(SELECT_SEND_APP_DIALOG, data);
		dialog.show(fm, "FmDialogFragment");
	}
	
	public static void showSelectSendAppDialog(FragmentManager fm, ResolveInfo[] apps, String file){
		Bundle data = new Bundle();
		data.putParcelableArray("available_apps", apps);
		data.putString("file", file);
		final FmDialogFragment dialog = FmDialogFragment.newInstance(SELECT_SEND_APP_DIALOG, data);
		dialog.show(fm, "FmDialogFragment");
	}
	
//	public static void showMultiSimRingtoneDialog(FragmentManager fm, String path){
//		Bundle data = new Bundle();
//		data.putString("path", path);
//		final FmDialogFragment dialog = FmDialogFragment.newInstance(MULTISIM_RINGTONE_DIALOG, data);
//		dialog.show(fm, "FmDialogFragment");
//	}
	
	public static void showRenameDialog(FragmentManager fm, String path){
		Bundle data = new Bundle();
		data.putString("path", path);
		final FmDialogFragment dialog = FmDialogFragment.newInstance(RENAME_DIALOG, data);
		dialog.show(fm, "FmDialogFragment");
	}
	
	public static void showDetailDialog(FragmentManager fm, String path){
		Bundle data = new Bundle();
		data.putString("path", path);
		final FmDialogFragment dialog = FmDialogFragment.newInstance(DETAIL_DIALOG, data);
		dialog.show(fm, "FmDialogFragment");
	}
	
	public static void showCreateDirectoryDialog(FragmentManager fm, String path){
		Bundle data = new Bundle();
		data.putString("path", path);
		final FmDialogFragment dialog = FmDialogFragment.newInstance(NEW_DIRECTORY_DIALOG, data);
//        dialog.setTargetFragment(fragment, 0);
		dialog.show(fm, "FmDialogFragment");
	}

    public static void showDownloadFromCloudConfirmDialog(FragmentManager fm, String remote_path){
        Bundle data = new Bundle();
        data.putString("path", remote_path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(DOWNLOAD_FROM_CLOUD_CONFIRM_DIALOG, data);
        //dialog.setTargetFragment(fragment, 0);
        dialog.show(fm, "FmDialogFragment");
    }

    public static void showCreateCloudDirectoryDialog(FragmentManager fm, String path){
        Bundle data = new Bundle();
        data.putString("root", path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(NEW_CLOUD_DIRECTORY_DIALOG, data);
        //dialog.setTargetFragment(fragment, 0);
        dialog.show(fm, "FmDialogFragment");
    }

    public static void showCloudLogoutDialog(FragmentManager fm){
        Bundle data = new Bundle();
        final FmDialogFragment dialog = FmDialogFragment.newInstance(CLOUD_LOGOUT_DIALOG, data);
        //dialog.setTargetFragment(fragment, 0);
        dialog.show(fm, "FmDialogFragment");
    }
	
	public static void showImportContactDialog(FragmentManager fm, String path){
		Bundle data = new Bundle();
		data.putString("path", path);
		final FmDialogFragment dialog = FmDialogFragment.newInstance(IMPORT_CONTACT_DIALOG, data);
		dialog.show(fm, "FmDialogFragment");
	}
	
	public static void showDeleteDialog(FragmentManager fm, int size, String first_path){
		Bundle data = new Bundle();
		data.putInt("path_count", size);
		data.putString("path_first", first_path);
		final FmDialogFragment dialog = FmDialogFragment.newInstance(DELETE_DIALOG, data);
//        dialog.setTargetFragment(fragment, 0);
		dialog.show(fm, "FmDialogFragment");
	}

    public static void showAddToSafeDialog(FragmentManager fm, int size, String first_path){
        Bundle data = new Bundle();
        data.putInt("path_count", size);
        data.putString("path_first", first_path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(ADD_TO_SAFE_DIALOG, data);
//        dialog.setTargetFragment(fragment, 0);
        dialog.show(fm, "FmDialogFragment");
    }

    public static void showAddToCloudDialog(FragmentManager fm, int size, String first_path){
        Bundle data = new Bundle();
        data.putInt("path_count", size);
        data.putString("path_first", first_path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(ADD_TO_CLOUD_DIALOG, data);
        dialog.show(fm, "FmDialogFragment");
    }

    public static void showMoveFromSafeDialog(FragmentManager fm, int size, String first_path){
        Bundle data = new Bundle();
        data.putInt("path_count", size);
        data.putString("path_first", first_path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(MOVE_FROM_SAFE_DIALOG, data);
        dialog.show(fm, "FmDialogFragment");
    }

    public static void showDeleteFromSafeDialog(FragmentManager fm, int size, String first_path){
        Bundle data = new Bundle();
        data.putInt("path_count", size);
        data.putString("path_first", first_path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(DELETE_FROM_SAFE_DIALOG, data);
//        dialog.setTargetFragment(fragment, 0);
        dialog.show(fm, "FmDialogFragment");
    }

    public static void showCloudDetailDialog(FragmentManager fm, String path){
        Bundle data = new Bundle();
        data.putString("path", path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(CLOUD_DETAIL_DIALOG, data);
        dialog.show(fm, "FmDialogFragment");
    }

    public static void showCloudRenameDialog(FragmentManager fm, String path){
        Bundle data = new Bundle();
        data.putString("path", path);
        final FmDialogFragment dialog = FmDialogFragment.newInstance(CLOUD_RENAME_DIALOG, data);
        dialog.show(fm, "FmDialogFragment");
    }
	
	/**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static FmDialogFragment newInstance(int id, Bundle data) {
    	FmDialogFragment f = new FmDialogFragment();

        data.putInt(ARG_DIALOG_ID, id);
        f.setArguments(data);

        return f;
    }


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDialogId = getArguments().getInt("dialog_id");
		
		int style = SherlockDialogFragment.STYLE_NORMAL, theme = 0;
		
		switch(mDialogId){
		case IMPORT_CONTACT_DIALOG:
			break;
		case NEW_DIRECTORY_DIALOG:
			break;
        case NEW_CLOUD_DIRECTORY_DIALOG:
            break;
        case CLOUD_LOGOUT_DIALOG:
            break;
		case SELECT_SEND_APP_DIALOG:
			break;
		case DELETE_DIALOG:
			break;
		case DETAIL_DIALOG:
			break;
        case CLOUD_DETAIL_DIALOG:
            break;
        case CLOUD_RENAME_DIALOG:
            break;
		case RENAME_DIALOG:
			break;
        case ADD_TO_SAFE_DIALOG:
            break;
        case MOVE_FROM_SAFE_DIALOG:
            break;
        case DELETE_FROM_SAFE_DIALOG:
            break;
        case ADD_TO_CLOUD_DIALOG:
            break;
        case DOWNLOAD_FROM_CLOUD_CONFIRM_DIALOG:
            break;
		}
		
		setStyle(style, theme);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = null;
		switch(mDialogId) {
		case IMPORT_CONTACT_DIALOG:
			break;
		case NEW_DIRECTORY_DIALOG:
//			view = buildCreateDirectoryView();
			break;
        case CLOUD_LOGOUT_DIALOG:
            break;
        case NEW_CLOUD_DIRECTORY_DIALOG:
            break;
		case SELECT_SEND_APP_DIALOG:
			view = buildSendAppDialogView();
			break;
		case DELETE_DIALOG:
			break;
		case DETAIL_DIALOG:
			break;
        case CLOUD_DETAIL_DIALOG:
            break;
        case CLOUD_RENAME_DIALOG:
            break;
		case RENAME_DIALOG:
			break;
        case ADD_TO_SAFE_DIALOG:
            break;
        case MOVE_FROM_SAFE_DIALOG:
            break;
        case DELETE_FROM_SAFE_DIALOG:
            break;
        case ADD_TO_CLOUD_DIALOG:
            break;
        case DOWNLOAD_FROM_CLOUD_CONFIRM_DIALOG:
            break;
		}
		return view;
	}
	
//	private View buildCreateDirectoryView() {
//		View contents = View.inflate(getActivity(), R.layout.new_directory_dialog, null);
//		
//		return contents;
//	}
	
	private View buildSendAppDialogView(){
		View contents = View.inflate(getActivity(), R.layout.select_app_dialog, null);
        
        final ResolveInfo[] available_apps = (ResolveInfo[])getArguments().getParcelableArray("available_apps");
        
        final String single_file = getArguments().getString("file");
        
        final ListView list = (ListView)contents.findViewById(R.id.select_app_list);
        
        List<Map<String, Object>> listdata = new ArrayList<Map<String,Object>>();
    	
        PackageManager pm = FileManager.getAppContext().getPackageManager();
        
        for(ResolveInfo app:available_apps)
        {
        	Map<String,Object> map = new HashMap<String, Object>();
        	try {
        		String pkg = app.activityInfo.applicationInfo.packageName;
	        	String cls = app.activityInfo.name;
				ComponentName componentName = new ComponentName(pkg, cls);
				Drawable icon = pm.getActivityIcon(componentName);
				CharSequence name = app.activityInfo.loadLabel(pm);
				
				map.put("name", name);
	        	map.put("icon", icon);
	        	listdata.add(map);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    	
        SimpleAdapter mAdapter = new SimpleAdapter(getActivity(), listdata, 
        		R.layout.select_app_list_item,
    			new String[]{"name","icon"},
    			new int[]{R.id.name, R.id.icon});
        
        mAdapter.setViewBinder(new ViewBinder()
        {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// TODO Auto-generated method stub
				if(data instanceof Drawable)
				{
					((ImageView)view).setImageDrawable((Drawable)data);
					return true;
				}
				else
				{
					return false;
				}
			}

        });
        
        list.setAdapter(mAdapter);
        
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				getDialog().dismiss();
                if (mListener != null) {
                    mListener.onDialogDone(getDialog(), getArguments().getInt(ARG_DIALOG_ID), 0, new String[]{available_apps[arg2].activityInfo.packageName, available_apps[arg2].activityInfo.name});
                }
//				if(	single_file != null )
//					((FileOperationActivity)getActivity()).getFileOperation().onOperationSendSelected(available_apps[arg2].activityInfo.packageName, available_apps[arg2].activityInfo.name, single_file);
//				else
//					((FileOperationActivity)getActivity()).getFileOperation().onOperationSendSelected(available_apps[arg2].activityInfo.packageName, available_apps[arg2].activityInfo.name);
			}
        });
        return contents;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		switch(mDialogId)
		{
		case IMPORT_CONTACT_DIALOG:
		{
            final String path = getArguments().getString("path");
			View contents = View.inflate(getActivity(), R.layout.import_contact_dialog, null);
			TextView text = (TextView)contents.findViewById(R.id.import_contact_dialog_content);
			text.setText(getString(R.string.import_contact_dialog_content, path));
            return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.import_contact_dialog_title)
            .setView(contents)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, 
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
	    				intent.setDataAndType(Uri.fromFile(new File(path)),"text/x-vcard");
	    				getActivity().startActivity(intent);
                    }
            })
            .setCancelable(true)
            .create();
		}
        case DOWNLOAD_FROM_CLOUD_CONFIRM_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.download_from_cloud_confirm_dialog, null);
            final String remote_path = getArguments().getString("path");
            final String info = getString(R.string.download_from_cloud_confirm_dialog_text, remote_path);
            final boolean wifi = NetworkUtil.isWifiConnected(getActivity());
            final TextView text = (TextView)contents.findViewById(R.id.download_from_cloud_confirm_info);
            if (wifi) {
                text.setText(info);
            } else {
                text.setText(getString(R.string.wifi_not_available)+info);
            }
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.download_from_cloud_confirm_dialog_title)
                    .setView(contents)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (mListener != null)
                                        mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, remote_path);
                                }
                            })
                    .setCancelable(true)
                    .create();
        }
        case CLOUD_LOGOUT_DIALOG:{
            View contents = View.inflate(getActivity(), R.layout.cloud_logout_dialog, null);
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.cloud_logout_dialog_title)
                    .setView(contents)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (mListener != null)
                                        mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, null);
                                }
                            })
                    .setCancelable(true)
                    .create();

        }
        case NEW_CLOUD_DIRECTORY_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.new_directory_dialog, null);
            final String root = getArguments().getString("root");
            final EditText edit = (EditText)contents.findViewById(R.id.new_directory_dialog_edit);
            final TextView error = (TextView)contents.findViewById(R.id.error_name);
            if (error!= null) {
                edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null) {
                            String name = s.toString();
                            if (!TextUtils.isEmpty(name) && !name.matches(CORRECT_FILENAME)) {
                                error.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                        error.setVisibility(View.INVISIBLE);
                        return;
                    }
                });
            }
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.new_directory_dialog_title)
                    .setView(contents)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String name = edit.getText().toString();
                                    File dir = new File(root, name);
                                    if (mListener != null)
                                        mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, dir.getAbsolutePath());
                                }
                            })
                    .setCancelable(true)
                    .create();
        }
		case NEW_DIRECTORY_DIALOG:
		{
            View contents = View.inflate(getActivity(), R.layout.new_directory_dialog, null);
            final String parent_path = getArguments().getString("path");
            final EditText edit = (EditText)contents.findViewById(R.id.new_directory_dialog_edit);
            final TextView error = (TextView)contents.findViewById(R.id.error_name);
            if (error!=null) {
                edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null) {
                            String name = s.toString();
                            if (!TextUtils.isEmpty(name) && !name.matches(CORRECT_FILENAME)) {
                                error.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                        error.setVisibility(View.GONE);
                        return;
                    }
                });
            }
            return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.new_directory_dialog_title)
            .setView(contents)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	String name = edit.getText().toString();
                    	File file = new File(parent_path, name);
                    	if(file.exists() && file.isDirectory())
                    	{
                    		Toast.makeText(getActivity(), R.string.new_directory_alreay_exist, Toast.LENGTH_SHORT).show();
                    	}
                    	else
                    	{
//                            boolean result = file.mkdirs();
//                            if(result && mListener!=null)
//                                mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, file.getName());
//                            else
//                                mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, "");
                            mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, file.getName());
                        }
                    }
            })
            .setCancelable(true)
            .create();
		}
		case SELECT_SEND_APP_DIALOG:
		{
			Dialog dialog = super.onCreateDialog(savedInstanceState);
			dialog.setTitle(R.string.select_send_app_dialog_title);
			return dialog;
		}
		case DELETE_DIALOG:
		{
			View contents = View.inflate(getActivity(), R.layout.delete_dialog, null);
			final String path_first = getArguments().getString("path_first");
			final int path_count = getArguments().getInt("path_count");
			final String info = getString(R.string.delete_confirm_text,path_first, path_count);
			final TextView text = (TextView)contents.findViewById(R.id.delete_dialog_info);
			text.setText(info);
			return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.delete_confirm_dialog_title)
			.setView(contents)
			.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(mListener!=null) {
                        mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), which, null);
                    }
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.setCancelable(true)
			.create();
		}
        case ADD_TO_CLOUD_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.add_to_cloud_confirm_dialog, null);
            final String path_first = getArguments().getString("path_first");
            final int path_count = getArguments().getInt("path_count");
            final String info = getString(R.string.add_to_cloud_confirm_dialog_text,path_first, path_count);
            final TextView text = (TextView)contents.findViewById(R.id.add_to_cloud_confirm_dialog_info);
            text.setText(info);
            return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_to_cloud_confirm_dialog_title)
                .setView(contents)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener!=null) {
                            mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), which, null);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(true)
                .create();
        }
        case ADD_TO_SAFE_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.add_to_safe_confirm_dialog, null);
            final String path_first = getArguments().getString("path_first");
            final int path_count = getArguments().getInt("path_count");
            final String info = getString(R.string.add_to_safe_confirm_dialog_text,path_first, path_count);
            final TextView text = (TextView)contents.findViewById(R.id.add_to_safe_confirm_dialog_info);
            text.setText(info);
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.add_to_safe_confirm_dialog_title)
                    .setView(contents)
                    .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mListener!=null) {
                                mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), which, null);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(true)
                    .create();
        }
        case DELETE_FROM_SAFE_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.delete_from_safe_confirm_dialog, null);
            final String path_first = getArguments().getString("path_first");
            final int path_count = getArguments().getInt("path_count");
            final String info = getString(R.string.delete_from_safe_confirm_dialog_text,path_first, path_count);
            final TextView text = (TextView)contents.findViewById(R.id.delete_from_safe_confirm_dialog_info);
            text.setText(info);
            return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_from_safe_confirm_dialog_title)
                .setView(contents)
                .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                    if(mListener!=null) {
                        mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), which, null);
                    }
                   }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(true)
                .create();
            }
        case MOVE_FROM_SAFE_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.move_from_safe_confirm_dialog, null);
            final String path_first = getArguments().getString("path_first");
            final int path_count = getArguments().getInt("path_count");
            final String info = getString(R.string.move_from_safe_confirm_dialog_text,path_first, path_count);
            final TextView text = (TextView)contents.findViewById(R.id.move_from_safe_confirm_dialog_info);
            text.setText(info);
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.move_from_safe_confirm_dialog_title)
                    .setView(contents)
                    .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mListener!=null) {
                                mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), which, null);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(true)
                    .create();
        }
		case DETAIL_DIALOG:
		{
			View contents = View.inflate(getActivity(), R.layout.detail_dialog, null);
			final String path = getArguments().getString("path");
//			final String info = FileAction.getFileDetailInfo(path);
//			final TextView text = (TextView)contents.findViewById(R.id.detail_dialog_info);
            final TextView name = (TextView)contents.findViewById(R.id.file_name_content);
            final TextView location = (TextView)contents.findViewById(R.id.file_location_content);
            final TextView permission = (TextView)contents.findViewById(R.id.file_permissions_content);
            final TextView size = (TextView)contents.findViewById(R.id.file_size_content);
            final TextView timestamp = (TextView)contents.findViewById(R.id.file_timestamp_content);
            final TextView owner = (TextView)contents.findViewById(R.id.file_owner_content);
            final TextView group = (TextView)contents.findViewById(R.id.file_group_content);
            File entry = new File(path);
            if (entry.isDirectory()) {
                size.setVisibility(View.GONE);
                contents.findViewById(R.id.file_size_label).setVisibility(View.GONE);
            }
            name.setText(entry.getName());
            location.setText(entry.getAbsolutePath());
            timestamp.setText(TimeUtil.getDateString(entry.lastModified()));
            size.setText(FileUtils.getFileSize(entry));
			return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.detail_dialog_title)
			.setView(contents)
			.setPositiveButton(android.R.string.ok,null)
			.setCancelable(true)
			.create();
		}
        case CLOUD_DETAIL_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.detail_dialog, null);
            final String remote_path = getArguments().getString("path");
//            final String info = FileAction.getCloudFileDetailInfo(path);
//            final TextView text = (TextView)contents.findViewById(R.id.detail_dialog_info);
//            text.setText(info);
            final TextView name_view = (TextView)contents.findViewById(R.id.file_name_content);
            final TextView location_view = (TextView)contents.findViewById(R.id.file_location_content);
            final TextView permission_view = (TextView)contents.findViewById(R.id.file_permissions_content);
            final TextView size_view = (TextView)contents.findViewById(R.id.file_size_content);
            final TextView timestamp_view = (TextView)contents.findViewById(R.id.file_timestamp_content);
            final TextView owner_view = (TextView)contents.findViewById(R.id.file_owner_content);
            final TextView group_view = (TextView)contents.findViewById(R.id.file_group_content);
            Cursor cursor = null;
            Context context = FileManager.getAppContext();
            try{
                cursor = context.getContentResolver().query(DataStructures.CloudBoxColumns.CONTENT_URI,
                        DataStructures.CloudBoxColumns.CLOUD_BOX_PROJECTION, DataStructures.CloudBoxColumns.FILE_PATH_FIELD+"=?",
                        new String[]{remote_path}, null);
                if (cursor!=null && cursor.moveToNext()) {
                    String name = cursor.getString(DataStructures.CloudBoxColumns.FILE_NAME_FIELD_INDEX);
                    String path = cursor.getString(DataStructures.CloudBoxColumns.FILE_PATH_FIELD_INDEX);
                    long size = cursor.getLong(DataStructures.CloudBoxColumns.FILE_SIZE_FIELD_INDEX);
                    long date = cursor.getLong(DataStructures.CloudBoxColumns.FILE_DATE_FIELD_INDEX);
                    int directory = cursor.getInt(DataStructures.CloudBoxColumns.IS_FOLDER_FIELD_INDEX);
                    int type = cursor.getInt(DataStructures.CloudBoxColumns.FILE_TYPE_FIELD_INDEX);
                    String local_file = cursor.getString(DataStructures.CloudBoxColumns.LOCAL_FILE_FIELD_INDEX);
                    name_view.setText(name);
                    location_view.setText(path);
                    size_view.setText(FileUtil.normalize(size));
                    timestamp_view.setText(TimeUtil.getDateString(date));
//                    info.append(context.getString(R.string.cloud_file_info_remote_location)).append(path).append('\n');
//                    info.append(context.getString(R.string.file_info_modified)).append(TimeUtil.getDateString(date)).append('\n');
//                    if(directory == 1) {
//                        info.append(context.getString(R.string.file_info_kind)).append(context.getString(R.string.file_info_kind_directory)).append('\n');
//                    } else {
//                        info.append(context.getString(R.string.file_info_size)).append(FileUtil.normalize(size)).append('\n');
//                        info.append(context.getString(R.string.file_info_kind)).append(context.getString(R.string.file_info_kind_file)).append('\n');
//                        if(!TextUtils.isEmpty(local_file)) {
//                            info.append(context.getString(R.string.cloud_file_info_local_location)).append(local_file).append('\n');
//                        }
//                    }
                }
            }catch(Exception e) {
                e.printStackTrace();
            }finally {
                if(cursor!=null) {
                    cursor.close();
                }
            }
            return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.detail_dialog_title)
                .setView(contents)
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(true)
                .create();
        }
        case CLOUD_RENAME_DIALOG:
        {
            View contents = View.inflate(getActivity(), R.layout.rename_dialog, null);
            final String orig_path = getArguments().getString("path");
            final EditText edit = (EditText)contents.findViewById(R.id.rename_dialog_edit);
            final TextView error = (TextView)contents.findViewById(R.id.error_name);
            if (error != null) {
                edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null) {
                            String name = s.toString();
                            if (!TextUtils.isEmpty(name) && !name.matches(CORRECT_FILENAME)) {
                                error.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                        error.setVisibility(View.INVISIBLE);
                        return;
                    }
                });
            }
            int idx = orig_path.lastIndexOf(File.separator);
            if(idx>=0)
                edit.setText(orig_path.substring(idx+1));
            return new AlertDialog.Builder(getActivity())
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

                                    int idx = orig_path.lastIndexOf(".");
                                    if(new_name.lastIndexOf(".")==-1 && idx!=-1)
                                    {
                                        new_name +=orig_path.substring(idx);
                                    }

                                    if (mListener != null) {
                                        mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, new String[]{orig_path, new_name});
                                    }

                                }
                            })
                    .setCancelable(true)
                    .create();
        }
		case RENAME_DIALOG:
		{
            View contents = View.inflate(getActivity(), R.layout.rename_dialog, null);
            final String orig_path = getArguments().getString("path");
            final EditText edit = (EditText)contents.findViewById(R.id.rename_dialog_edit);
            final TextView error = (TextView)contents.findViewById(R.id.error_name);
            if (error!=null) {
                edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null) {
                            String name = s.toString();
                            if (!TextUtils.isEmpty(name) && !name.matches(CORRECT_FILENAME)) {
                                error.setVisibility(View.VISIBLE);
                                return;
                            }
                        }
                        error.setVisibility(View.INVISIBLE);
                        return;
                    }
                });
            }
            int idx = orig_path.lastIndexOf(File.separator);
            if(idx>=0)
            	edit.setText(orig_path.substring(idx+1));
            return new AlertDialog.Builder(getActivity())
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

                    	int idx = orig_path.lastIndexOf(".");
                    	if(new_name.lastIndexOf(".")==-1 && idx!=-1)
                    	{
                    		new_name += orig_path.substring(idx);
                    	}

                        if (mListener != null) {
                            mListener.onDialogDone(dialog, getArguments().getInt(ARG_DIALOG_ID), whichButton, new String[]{orig_path, new_name});
                        }

//                    	((FileOperationActivity)getActivity()).getFileOperation().onOperationRenameConfirm(orig_path, new_name);

                        
                    }
            })
            .setCancelable(true)
            .create();
		}
//        case MULTISIM_RINGTONE_DIALOG:
//        {
//        	
//            View contents = View.inflate(getActivity(), R.layout.multisim_ringtone_dialog, null);
//            final LinearLayout multisim_first = (LinearLayout)contents.findViewById(R.id.multisim_first);
//            final LinearLayout multisim_second = (LinearLayout)contents.findViewById(R.id.multisim_second);
//            final CheckBox multisim_first_check = (CheckBox)contents.findViewById(R.id.multisim_first_check);
//            final CheckBox multisim_second_check = (CheckBox)contents.findViewById(R.id.multisim_second_check);
//            final String ringtone_path = getArguments().getString("path");
//            multisim_first.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(multisim_first_check.isChecked())
//						multisim_first_check.setChecked(false);
//					else
//						multisim_first_check.setChecked(true);
//				}
//			});
//            multisim_second.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(multisim_second_check.isChecked())
//						multisim_second_check.setChecked(false);
//					else
//						multisim_second_check.setChecked(true);
//				}
//			});
//            return new AlertDialog.Builder(getActivity())
//            .setTitle(R.string.ringtone_dialog_title)
//            .setView(contents)
//            .setNegativeButton(android.R.string.cancel, null)
//            .setPositiveButton(android.R.string.ok,  new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	if(multisim_first_check.isChecked() && multisim_second_check.isChecked())
//                	{
//                		FileAction.setAsMultiSimRingTone(new File(ringtone_path),true,true);
//                	}
//                	else if(multisim_first_check.isChecked())
//                	{
//                		FileAction.setAsMultiSimRingTone(new File(ringtone_path),true,false);
//                	}
//                	else if(multisim_second_check.isChecked())
//                	{
//                		FileAction.setAsMultiSimRingTone(new File(ringtone_path),false,true);
//                	}
//                }})
//                .setCancelable(true).create();
//            		
////        	return new AlertDialog.Builder(this).setTitle(R.string.multisim_ringtone).setMultiChoiceItems(
////        		     new String[] {
////        		    		 CategoryTab.this.getString(R.string.multisim_first), 
////        		    		 CategoryTab.this.getString(R.string.multisim_second),  
////        		    		 }, new boolean[]{true,true}, null)
////        		     .setPositiveButton(android.R.string.ok, null)
////        		     .setNegativeButton(android.R.string.cancel, null).show();
//        }
        default:
        	throw new IllegalArgumentException("unknown dialog id:"+mDialogId);
		}
	}

    public static abstract interface OnDialogDoneListener
    {
        public abstract void onDialogDone(DialogInterface paramDialogInterface, int paramInt1,
                                          int paramInt2, Object param);
    }
}
