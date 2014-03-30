package com.hufeng.filemanager.settings;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DefaultViewPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

    Preference mVideoPreference;
    Preference mAudioPreference;
    Preference mImagePreference;
    int mRes;

    @Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	
	  mRes=
	      getActivity().getResources()
	                   .getIdentifier(getArguments().getString("resource"),
	                                  "xml",
	                                  getActivity().getPackageName());
	
        init();

	}

    private void init(){
        addPreferencesFromResource(mRes);

        mVideoPreference = findPreference("VIDEO_DEFAULT_VIEW");
        mVideoPreference.setOnPreferenceChangeListener(this);

        mAudioPreference = findPreference("AUDIO_DEFAULT_VIEW");
        mAudioPreference.setOnPreferenceChangeListener(this);

        mImagePreference = findPreference("IMAGE_DEFAULT_VIEW");
        mImagePreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setPreferenceScreen(null);
        init();
        return true;
    }


    //    private Handler handler = new Handler();

    @Override
    public void onResume() {
        super.onResume();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Preference somePreference = findPreference("TEST_DEFAULT_VIEW");
//                PreferenceScreen preferenceScreen = getPreferenceScreen();
//                preferenceScreen.removePreference(somePreference);
//            }
//        },2000);

    }
}

