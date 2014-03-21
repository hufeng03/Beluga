package com.hufeng.backup;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.utils.TimeUtil;

import java.io.File;
import java.util.ArrayList;

//import com.hufeng.filemanager.SettingsDetailActivity;
//import com.hufeng.filemanager.browser.ExtensionAdapter.ViewHolder;

public class BackupAdapter extends BaseAdapter{
	
	private ArrayList<String> mItems;
	private LayoutInflater mInflater;
	private Context mContext;
	private Handler mHandler;
	private ArrayList<String> mSelected = new ArrayList<String>();
 	
	public BackupAdapter(Context context, ArrayList<String> filename, Handler handler)
	{
		mContext = context;
		mItems = filename;
		mHandler = handler;
		mInflater = LayoutInflater.from(context);
	}
	
	public ArrayList<String> getSelectedItems(){
		return mSelected;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mItems==null)
			return 0;
		else		
			return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(mItems==null)
			return null;
		else
			return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        ViewHolder holder = null;
        if(convertView!=null)
        	holder = (ViewHolder) convertView.getTag();

        if (holder==null) {
			convertView = mInflater.inflate(R.layout.backup_history_list_item, null);
	        holder = new ViewHolder();
	        holder.name = (TextView)convertView.findViewById(R.id.backup_history_list_item_name);
	        holder.desc = (TextView)convertView.findViewById(R.id.backup_history_list_item_desc);
			holder.check = (CheckBox)convertView.findViewById(R.id.backup_history_list_item_check);
			holder.delete = (ImageButton)convertView.findViewById(R.id.backup_history_list_item_delete);
			convertView.setTag(holder);
        }
        holder.path = mItems.get(position);
        File file = new File(holder.path);
		holder.name.setText(file.getName());
		holder.desc.setText(mContext.getString(R.string.backup_time)+" "+ TimeUtil.getRegularDateTimeStr(BackupUtil.getBackupDateFromFileName(file.getName())));
		holder.check.setOnCheckedChangeListener(null);
		if(mSelected!=null && mSelected.contains(holder.path))
		{
			holder.check.setChecked(true);
		}
		else
		{
			holder.check.setChecked(false);
		}
		holder.check.setOnCheckedChangeListener(holder);
		holder.delete.setOnClickListener(holder);
		return convertView;
	}
	
    public final class ViewHolder implements OnCheckedChangeListener, OnClickListener {
    	public ImageButton delete;
        public TextView name;
        public TextView desc;
        public String path;
        public CheckBox check;


		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			if(isChecked)
			{
				if(!mSelected.contains(path))
				{
					mSelected.add(path);
				}
			}
			else
			{
				mSelected.remove(path);
			}
		}


		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new File(path).delete();
			mSelected.remove(path);
			mItems.remove(path);
			notifyDataSetChanged();
//			Toast.makeText(mContext, R.string.delete_one_history_item, Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessage(BackupUtil.MESSAGE_DELETE_ONE_HISTORY_ITEM);
		}

    }

	


}
