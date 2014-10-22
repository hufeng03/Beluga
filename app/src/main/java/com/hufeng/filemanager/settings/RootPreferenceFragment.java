package com.hufeng.filemanager.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.hufeng.filemanager.utils.LogUtil;

/**
 * Created by feng on 13-12-23.
 */
public class RootPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private static final String TAG = RootPreferenceFragment.class.getSimpleName();

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
        Preference myPref = findPreference(PreferenceKeys.SHOW_ROOT_DIR);
        if(myPref!=null){
            myPref.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if(!TextUtils.isEmpty(key)) {
            LogUtil.i(TAG, "onPreferenceClick " + key);
            if (PreferenceKeys.SHOW_ROOT_DIR.equals(key)) {
                Intent intent = new Intent("SHOW_ROOT_FILES_ACTION");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                return true;
            }
        }
        return false;
    }
}
