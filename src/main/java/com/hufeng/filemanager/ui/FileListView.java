package com.hufeng.filemanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;

public class FileListView extends ListView{

	@Override
	protected ContextMenuInfo getContextMenuInfo() {
		// TODO Auto-generated method stub
		ContextMenuInfo cmi = super.getContextMenuInfo();
		return cmi;
	}

	public FileListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public FileListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FileListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

}
