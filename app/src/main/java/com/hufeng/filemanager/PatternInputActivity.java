package com.hufeng.filemanager;

import android.os.Bundle;
import android.view.Window;

public class PatternInputActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pattern_input);

	}	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void setNewSkin(int skin) {
		// TODO Auto-generated method stub
		super.setNewSkin(skin);
	}

	
}