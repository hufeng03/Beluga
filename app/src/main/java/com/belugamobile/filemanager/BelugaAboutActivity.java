package com.belugamobile.filemanager;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.belugamobile.filemanager.dialog.BelugaDialogFragment;

/**
 * Created by Feng Hu on 15-02-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaAboutActivity extends BelugaBaseActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beluga_about_activity);

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
            addPreferencesFromResource(R.xml.beluga_about_preferences);
            Preference versionPreference = findPreference(PreferenceKeys.PACKAGE_VERSION_NAME);
            versionPreference.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(PreferenceKeys.PACKAGE_VERSION_NAME, "1.0.0"));
            versionPreference.setOnPreferenceClickListener(this);

            Preference translationPreference = findPreference(PreferenceKeys.TRANSLATION_CONTRIBUTION);
            translationPreference.setSummary(getResources().getString(R.string.translation_summary, 3, 4));
            translationPreference.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (PreferenceKeys.PACKAGE_VERSION_NAME.equals(preference.getKey())) {
                BelugaDialogFragment.showChangeLogDialog((FragmentActivity)getActivity());
                return true;
            } else if (PreferenceKeys.TRANSLATION_CONTRIBUTION.equals(preference.getKey())) {
                BelugaDialogFragment.showTranslationContributionDialog((FragmentActivity)getActivity());
                return true;
            } else {
                return false;
            }
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
