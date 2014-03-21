package com.hufeng.smsbackup;

import java.util.ArrayList;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.utils.TimeUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SmsListAdapter extends BaseAdapter{

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<SmsItem> mSmsArray;
	
	
	public SmsListAdapter(Context context, ArrayList<SmsItem> array)
	{
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mSmsArray = array;
	}
	
	public void setSmsArray(ArrayList<SmsItem> array)
	{
		mSmsArray = array;
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mSmsArray==null)
			return 0;
		else
			return mSmsArray.size();
	}



	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(mSmsArray==null || position>=mSmsArray.size())
			return null;
		else
			return mSmsArray.get(position);
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
            convertView = mInflater.inflate(R.layout.sms_list_row, null);
            holder = new ViewHolder();
            holder.addressandtype =(TextView)convertView.findViewById(R.id.sms_list_row_type_and_address);
            holder.date = (TextView)convertView.findViewById(R.id.sms_list_row_date);
            holder.body = (TextView)convertView.findViewById(R.id.sms_list_row_body);
            convertView.setTag(holder);
        }
        
        SmsItem item = mSmsArray.get(position);
        String address = item.getAddress();
        if(item.getType()=="1")
        {
        	holder.addressandtype.setText(mContext.getString(R.string.sms_to)+" "+address);
        }
        else
        {
        	holder.addressandtype.setText(mContext.getString(R.string.sms_from)+" "+address);
        }
        holder.body.setText(item.getBody());
        holder.date.setText(TimeUtil.getFormatDateTimeStr(Long.parseLong(item.getDate())));
        return convertView;
	}
	
	
	private class ViewHolder
	{
		private TextView addressandtype;
		private TextView date;
		private TextView body;
	}	

}
