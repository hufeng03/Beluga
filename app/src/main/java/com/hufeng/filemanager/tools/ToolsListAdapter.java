package com.hufeng.filemanager.tools;

import java.util.ArrayList;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.ui.FileViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ToolsListAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<Tool> mTools = new ArrayList<Tool>();
	
	public ToolsListAdapter(Context context){
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	public void setData(Tool[] tools){
		mTools.clear();
		for (Tool tool : tools) {
			mTools.add(tool);
		}
	}

	@Override
	public int getCount() {
		if(mTools != null)
			return mTools.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if(mTools != null && position>=0 && position<mTools.size())
			return mTools.get(position);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
        ToolViewHolder holder = null;
        if(convertView!=null)
        	holder = (ToolViewHolder) convertView.getTag();

        if (holder==null) {
            convertView = mInflater.inflate(R.layout.tool_list_item, null);
            holder = new ToolViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.tool_icon);
            holder.name = (TextView) convertView.findViewById(R.id.tool_name);
            holder.description = (TextView) convertView.findViewById(R.id.tool_description);
            convertView.setTag(holder);
        }
        Tool tool = mTools.get(position);
        holder.icon.setImageResource(tool.icon);
        holder.name.setText(tool.name);
        holder.description.setText(tool.description);
        return convertView;
	}
	
	public class ToolViewHolder{
		public ImageView icon;
		public TextView name;
		public TextView description;
	}

}
