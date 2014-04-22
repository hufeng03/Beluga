package com.hufeng.filemanager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hufeng.filemanager.browser.FileUtils;

public class SafeCategoryActivity extends BaseActivity implements OnClickListener{

	private TextView mCategoryPicture;
	private TextView mCategoryVideo;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.activity_safe_category);
		
		mCategoryPicture= (TextView)findViewById(R.id.safe_category_picture);
		mCategoryVideo = (TextView)findViewById(R.id.safe_category_video);
		mCategoryPicture.setOnClickListener(this);
		mCategoryVideo.setOnClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
            // This is called when the Home (Up) button is pressed
            // in the Action Bar.
//            Intent parentActivityIntent = new Intent(this, SettingsActivity.class);
//            parentActivityIntent.addFlags(
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(parentActivityIntent);
//            finish();
        	onBackPressed();
            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.safe_category_picture:
			showSafeFiles(FileUtils.FILE_TYPE_IMAGE);
			break;
		case R.id.safe_category_video:
			showSafeFiles(FileUtils.FILE_TYPE_VIDEO);
			break;
		}
	}
	
	private void showSafeFiles(int category){
		Intent intent = new Intent(SafeCategoryActivity.this, SafeFileActivity.class);
		intent.putExtra(SafeFileActivity.SAFE_FILE_CATEGORY, category);
		startActivity(intent);
	}
	
}
