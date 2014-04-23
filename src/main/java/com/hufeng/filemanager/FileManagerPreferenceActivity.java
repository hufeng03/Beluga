package com.hufeng.filemanager;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import com.hufeng.filemanager.root.RootHelper;

import java.util.List;

//import com.umeng.fb.UMFeedbackService;

public class FileManagerPreferenceActivity extends PreferenceActivity {

	private ActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		UMFeedbackService.enableNewReplyNotification(this, NotificationType.NotificationBar);
        
		mActionBar = getActionBar();
		mActionBar.setDisplayShowTitleEnabled(true); 
		mActionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);

        if (RootHelper.isRootedPhone()) {
            loadHeadersFromResource(R.xml.preference_headers_root, target);
        } else {
            loadHeadersFromResource(R.xml.preference_headers, target);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    //	private void initClick() {
//		Preference myPref =(Preference) findPreference(PreferenceKeys.FEEDBACK);
//		if(myPref!=null){
//			myPref.setOnPreferenceClickListener(this);
//		}
//
//		Preference myPref2 =(Preference) findPreference(PreferenceKeys.UPGRADE);
//		if(myPref2!=null){
//			myPref2.setOnPreferenceClickListener(this);
//		}
//
//		Preference myPref3 = (Preference) findPreference(PreferenceKeys.EMAIL);
//		if(myPref3!=null)
//			myPref3.setOnPreferenceClickListener(this);
//
//		Preference myPref4 = (Preference) findPreference(PreferenceKeys.WEBSITE);
//		if(myPref4!=null)
//			myPref4.setOnPreferenceClickListener(this);
//	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
            // This is called when the Home (Up) button is pressed
            // in the Action Bar.
//            Intent parentActivityIntent = new Intent(this, FileManagerTabActivity.class);
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
	
    private void checkUpgrade(){
        if (Constants.USE_UMENG) {
            UmengDelegate.forceUpdate(this);
        }

//    	UmengUpdateAgent.setUpdateAutoPopup(false);
//    	Toast.makeText(FileManagerPreferenceActivity.this, R.string.about_us_activity_upgrade_check, Toast.LENGTH_SHORT).show();
//    	UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//    	        @Override
//    	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
//    	            switch (updateStatus) {
//    	            case 0: // has update
//    	            	String new_version = updateInfo.version;
//    	            	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FileManagerPreferenceActivity.this);
//    	            	sp.edit().putString("new_version", new_version).commit();
//    	                UmengUpdateAgent.showUpdateDialog(FileManagerPreferenceActivity.this, updateInfo);
//    	                break;
//    	            case 1: // has no update
//    	                Toast.makeText(FileManagerPreferenceActivity.this, R.string.about_us_activity_upgrade_no_need, Toast.LENGTH_SHORT)
//    	                        .show();
//    	                break;
//    	            case 2: // none wifi
//    	                Toast.makeText(FileManagerPreferenceActivity.this, R.string.about_us_activity_upgrade_no_wifi, Toast.LENGTH_SHORT)
//    	                        .show();
//    	                break;
//    	            case 3: // time out
//    	                Toast.makeText(FileManagerPreferenceActivity.this, R.string.about_us_activity_upgrade_time_out, Toast.LENGTH_SHORT)
//    	                        .show();
//    	                break;
//    	            }
//    	        }
//    	});
    }

//	@Override
//	public boolean onPreferenceClick(Preference preference) {
//		String key = preference.getKey();
//		boolean handled = false;
//		if(!TextUtils.isEmpty(key)){
//			if(PreferenceKeys.FEEDBACK.equals(key)){
//                if (Constants.USE_UMENG) {
//                    UmengDelegate.launchFeedback(this);
//                }
//				handled = true;
//			} else if(PreferenceKeys.UPGRADE.equals(key)) {
//				checkUpgrade();
//				handled = true;
//			} else if(PreferenceKeys.EMAIL.equals(key)) {
//				Intent intent = new Intent(Intent.ACTION_SENDTO);
//                String email = preference.getSummary().toString();
//				intent.setData(Uri.parse("mailto:"+email));
//				startActivity(intent);
//				handled = true;
//			} else if(PreferenceKeys.WEBSITE.equals(key)) {
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//                String web = preference.getSummary().toString();
//				intent.setData(Uri.parse(web));
//				startActivity(intent);
//				handled = true;
//			}
//		}
//		return handled;
//	}
	
}
