package com.belugamobile.filemanager;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.belugamobile.filemanager.root.BelugaRootManager;


/**
 * Created by Feng Hu on 15-02-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaSettingActivity extends BelugaBaseActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beluga_setting_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        BelugaSettingFragment mPrefsFragment = new BelugaSettingFragment();
        mFragmentTransaction.replace(R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }


    public static class BelugaSettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.beluga_setting_preferences);
            Preference rootExplorerPreference = findPreference(PreferenceKeys.ROOT_EXPLORER_ENABLE);
            rootExplorerPreference.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (((CheckBoxPreference)preference).isChecked()) {
                BelugaRootManager.getInstance().init(getActivity());
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
