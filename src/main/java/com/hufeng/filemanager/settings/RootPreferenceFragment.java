package com.hufeng.filemanager.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by feng on 13-12-23.
 */
public class RootPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int res=
                getActivity().getResources()
                        .getIdentifier(getArguments().getString("resource"),
                                "xml",
                                getActivity().getPackageName());

        addPreferencesFromResource(res);

    }
}
