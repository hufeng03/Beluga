package com.hufeng.filemanager;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.hufeng.filemanager.browser.FileUtils;


public class SafeFileActivity extends BaseActivity{
	public static final String SAFE_FILE_CATEGORY = "safe_file_category";
	public static final int SAFE_FILE_CATEGORY_IMAGE = 1;
	public static final int SAFE_FILE_CATEGORY_VIDEO = 2;
	private int mCategory = FileUtils.FILE_TYPE_IMAGE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_simple_fragment);
		
//		ActionBar bar = getSupportActionBar();
//		bar.show();
		mCategory = getIntent().getIntExtra(SafeFileActivity.SAFE_FILE_CATEGORY, FileUtils.FILE_TYPE_IMAGE);
		
		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		SafeBoxGrouperFragment fragment = (SafeBoxGrouperFragment) fm
				.findFragmentByTag(SafeBoxGrouperFragment.class.getSimpleName());
		if (fragment == null) {
			fragment = new SafeBoxGrouperFragment();
//			ft.add(R.id.fragment_container, fragment,
//					WineRecordingFragment.class.getSimpleName());
			fragment.setSafeCategory(mCategory);
			ft.replace(R.id.fragment_container, fragment,
					SafeBoxGrouperFragment.class.getSimpleName());
		} else {
			if (fragment.isDetached()) {
				fragment.setSafeCategory(mCategory);
				ft.attach(fragment);
			}
		}
		ft.commit();
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		LogUtil.i(LOG_TAG, "onCreateOptionsMenu");
//		return super.onCreateOptionsMenu(menu);
//		if(mFirst){
		super.onCreateOptionsMenu(menu);
			menu.add("");
//			mFirst = false;
//		}
		return true;
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
	
	public void addSafe() {
		final FragmentManager fm = getSupportFragmentManager();
		final FragmentTransaction ft = fm.beginTransaction();
		CategoryTabFragment fragment = (CategoryTabFragment) fm
				.findFragmentByTag(CategoryTabFragment.class.getSimpleName());
		if (fragment == null) {
			fragment = new CategoryTabFragment();
//			ft.add(R.id.fragment_container, fragment,
//					WineRecordingFragment.class.getSimpleName());
		} else {
			if (fragment.isDetached()) {
				ft.attach(fragment);
			}
		}
		ft.replace(R.id.fragment_container, fragment,
				CategoryTabFragment.class.getSimpleName());
		ft.commit();
	}
	
	
}
