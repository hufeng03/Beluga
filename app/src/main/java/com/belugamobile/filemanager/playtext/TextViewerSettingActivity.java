package com.belugamobile.filemanager.playtext;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.belugamobile.filemanager.BelugaBaseActionBarActivity;
import com.belugamobile.filemanager.R;

/**
 * Created by Feng on 2015-04-08.
 */
public class TextViewerSettingActivity extends BelugaBaseActionBarActivity{

    private static final String TAG = "TextViewerSettingAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_viewer_setting_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        TextViewerSettingFragment mPrefsFragment = new TextViewerSettingFragment();
        mFragmentTransaction.replace(R.id.content, mPrefsFragment);
        mFragmentTransaction.commit();
    }


    public static class TextViewerSettingFragment extends PreferenceFragment{

        private static final String TAG = "TextViewerSettingFrag";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.text_viewer_setting_preferences);
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
