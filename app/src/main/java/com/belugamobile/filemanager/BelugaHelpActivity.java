package com.belugamobile.filemanager;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.belugamobile.filemanager.dialog.BelugaDialogFragment;
import com.belugamobile.filemanager.utils.PackageUtil;

/**
 * Created by Feng Hu on 15-02-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaHelpActivity extends BelugaBaseActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beluga_help_activity);

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


    public static class BelugaSettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.beluga_help_preferences);

            Preference marketPreference = findPreference(PreferenceKeys.MARKET_REVIEW_KEY);
            marketPreference.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (PreferenceKeys.MARKET_REVIEW_KEY.equals(preference.getKey())) {
                Intent markIntent = new Intent(Intent.ACTION_VIEW);
                markIntent.setData(Uri.parse("market://details?id="+ PackageUtil.getPackageName(getActivity())));
                try {
                    startActivity(markIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
