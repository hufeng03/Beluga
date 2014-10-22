package com.hufeng.filemanager.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.UmengDelegate;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class OthersPreferenceFragment extends PreferenceFragment implements OnPreferenceClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	
	  int res=
	      getActivity().getResources()
	                   .getIdentifier(getArguments().getString("resource"),
	                                  "xml",
	                                  getActivity().getPackageName());
	
	  addPreferencesFromResource(res);
	  
	  initClick();
	  
	}

	
	private void initClick() {
		Preference myPref = findPreference(PreferenceKeys.FEEDBACK);
		if(myPref!=null){
			myPref.setOnPreferenceClickListener(this);
		}
		
		Preference myPref2 = findPreference(PreferenceKeys.UPGRADE);
		if(myPref2!=null){
			myPref2.setOnPreferenceClickListener(this);
		}
		
		Preference myPref3 =  findPreference(PreferenceKeys.EMAIL);
		if(myPref3!=null)
			myPref3.setOnPreferenceClickListener(this);
		
		Preference myPref4 =  findPreference(PreferenceKeys.WEBSITE);
		if(myPref4!=null)
			myPref4.setOnPreferenceClickListener(this);
	}
	
    private void checkUpgrade(){
        if (Constants.USE_UMENG) {
            //UmengUpdateAgent.forceUpdate(getActivity());
            UmengDelegate.forceUpdate(getActivity());
        }
//    	UmengUpdateAgent.setUpdateAutoPopup(false);
//    	Toast.makeText(getActivity(), R.string.about_us_activity_upgrade_check, Toast.LENGTH_SHORT).show();
//    	UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//    	        @Override
//    	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
//    	            switch (updateStatus) {
//    	            case 0: // has update
//    	            	String new_version = updateInfo.version;
//    	            	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//    	            	sp.edit().putString("new_version", new_version).commit();
//    	                UmengUpdateAgent.showUpdateDialog(getActivity(), updateInfo);
//    	                break;
//    	            case 1: // has no update
//    	                Toast.makeText(getActivity(), R.string.about_us_activity_upgrade_no_need, Toast.LENGTH_SHORT)
//    	                        .show();
//    	                break;
//    	            case 2: // none wifi
//    	                Toast.makeText(getActivity(), R.string.about_us_activity_upgrade_no_wifi, Toast.LENGTH_SHORT)
//    	                        .show();
//    	                break;
//    	            case 3: // time out
//    	                Toast.makeText(getActivity(), R.string.about_us_activity_upgrade_time_out, Toast.LENGTH_SHORT)
//    	                        .show();
//    	                break;
//    	            }
//    	        }
//    	});
    }
    
	@Override
	public boolean onPreferenceClick(Preference preference) {
		String key = preference.getKey();
		boolean handled = false;
		if(!TextUtils.isEmpty(key)){
			if(PreferenceKeys.FEEDBACK.equals(key)){
                if (Constants.USE_UMENG) {
//                    FeedbackAgent agent = new FeedbackAgent(getActivity());
//                    agent.startFeedbackActivity();
                    UmengDelegate.launchFeedback(getActivity());
                }
				handled = true;
			} else if(PreferenceKeys.UPGRADE.equals(key)) {
				checkUpgrade();
				handled = true;
			} else if(PreferenceKeys.EMAIL.equals(key)) {
				Intent intent = new Intent(Intent.ACTION_SENDTO);
                String email = preference.getSummary().toString();
				intent.setData(Uri.parse("mailto:"+email));
				startActivity(intent);
				handled = true;
			} else if(PreferenceKeys.WEBSITE.equals(key)) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
                String web = preference.getSummary().toString();
				intent.setData(Uri.parse(web));
				startActivity(intent);
				handled = true;
			}
		}
		return handled;		
	}
}

