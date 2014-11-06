package com.hufeng.filemanager.settings;



import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.services.UiCallServiceHelper;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScanPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    CheckBoxPreference pref_filter_audio, pref_filter_image, pref_scan_hidden, pref_scan_game;
    boolean changed_filter_audio, changed_filter_image, changed_scan_hidden, changed_scan_game;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

      SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
      editor.putBoolean(PreferenceKeys.FILTER_AUDIO, FileManager.getPreference(PreferenceKeys.FILTER_AUDIO, "1").equals("0"));
      editor.putBoolean(PreferenceKeys.FILTER_IMAGE, FileManager.getPreference(PreferenceKeys.FILTER_IMAGE, "1").equals("0"));
      editor.putBoolean(PreferenceKeys.SCAN_HIDDEN, FileManager.getPreference(PreferenceKeys.SCAN_HIDDEN, "0").equals("1"));
      editor.putBoolean(PreferenceKeys.SCAN_GAME, FileManager.getPreference(PreferenceKeys.SCAN_GAME, "0").equals("1"));
      editor.commit();

	  int res=
	      getActivity().getResources()
	                   .getIdentifier(getArguments().getString("resource"),
	                                  "xml",
	                                  getActivity().getPackageName());
	
	  addPreferencesFromResource(res);
      pref_filter_audio = (CheckBoxPreference)findPreference(PreferenceKeys.FILTER_AUDIO);
      pref_filter_image = (CheckBoxPreference)findPreference(PreferenceKeys.FILTER_IMAGE);
      pref_scan_hidden = (CheckBoxPreference)findPreference(PreferenceKeys.SCAN_HIDDEN);
      pref_scan_game = (CheckBoxPreference)findPreference(PreferenceKeys.SCAN_GAME);

      if (Constants.PRODUCT_FLAVOR_NAME.equals("fanzhuo")) {
          getPreferenceScreen().removePreference(pref_scan_hidden);
          getPreferenceScreen().removePreference(pref_scan_game);
      }

	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        changed_filter_audio = false;
        changed_filter_image = false;
        changed_scan_hidden = false;
        changed_scan_game = false;
        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        if(changed_filter_audio || changed_filter_image || changed_scan_hidden || changed_scan_game) {
            UiCallServiceHelper.getInstance().startScan();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PreferenceKeys.FILTER_AUDIO.equals(key)) {
            if(pref_filter_audio.isChecked()) {
                FileManager.setPreference(PreferenceKeys.FILTER_AUDIO, "0");
            } else {
                FileManager.setPreference(PreferenceKeys.FILTER_AUDIO, "1");
            }
            changed_filter_audio = !changed_filter_audio;
        } else if(PreferenceKeys.FILTER_IMAGE.equals(key)) {
            if(pref_filter_image.isChecked()) {
                FileManager.setPreference(PreferenceKeys.FILTER_IMAGE, "0");
            } else {
                FileManager.setPreference(PreferenceKeys.FILTER_IMAGE, "1");
            }
            changed_filter_image = !changed_filter_image;
        } else if(PreferenceKeys.SCAN_HIDDEN.equals(key)) {
            if(pref_scan_hidden.isChecked()) {
                FileManager.setPreference(PreferenceKeys.SCAN_HIDDEN, "1");
            } else {
                FileManager.setPreference(PreferenceKeys.SCAN_HIDDEN, "0");
            }
            changed_scan_hidden = !changed_scan_hidden;
        } else if(PreferenceKeys.SCAN_GAME.equals(key)) {
            if(pref_scan_game.isChecked()) {
                FileManager.setPreference(PreferenceKeys.SCAN_GAME, "1");
            } else {
                FileManager.setPreference(PreferenceKeys.SCAN_GAME, "0");
            }
            changed_scan_game = !changed_scan_game;
        }
    }
}

