package com.hufeng.filemanager.safe;

import java.util.ArrayList;

import com.hufeng.filemanager.app.AppInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SafeGridAdapter extends BaseAdapter{
	
	private ArrayList<SafeInfo> mSafeInfos;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public SafeGridAdapter(Context context, ArrayList<SafeInfo> safes) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mSafeInfos = safes;
	}

    public void setSafes(ArrayList<SafeInfo> safes)
    {
    	mSafeInfos = safes;
    }
    
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void destroy(){
		
	}

}
