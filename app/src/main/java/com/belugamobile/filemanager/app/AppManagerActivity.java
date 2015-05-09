package com.belugamobile.filemanager.app;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.belugamobile.filemanager.BelugaBaseActionBarActivity;
import com.belugamobile.filemanager.BelugaNavigationDrawerFragment;
import com.belugamobile.filemanager.BelugaNavigationDrawerFragment.DrawerItem;
import com.belugamobile.filemanager.R;

import refactor.com.android.contacts.common.util.ViewUtil;

/**
 * Created by Feng Hu on 15-02-11.
 * <p/>
 * TODO: Add a class header comment.
 */
public class AppManagerActivity extends BelugaBaseActionBarActivity {

    private BelugaNavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_manager_activity);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_color_dark));
//        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationDrawerFragment = (BelugaNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(drawerLayout, toolbar, new DrawerItem[] {
                new DrawerItem(R.id.drawer_item_my_files, getResources().getDrawable(R.drawable.ic_files_24dp), getString(R.string.my_files), false),
                new DrawerItem(R.id.drawer_item_my_apps, getResources().getDrawable(R.drawable.ic_apps_24dp), getString(R.string.my_apps), true),
                new DrawerItem(R.id.drawer_item_settings, getResources().getDrawable(R.drawable.ic_settings_24dp), getString(R.string.settings_label), false),
                new DrawerItem(R.id.drawer_item_about, getResources().getDrawable(R.drawable.ic_info_24dp), getString(R.string.about_label), false),
                new DrawerItem(R.id.drawer_item_help_and_feedback, getResources().getDrawable(R.drawable.ic_help_24dp), getString(R.string.help_and_feedback), false)
        }, R.id.drawer_item_my_apps);

        showAppManagerFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Add shadow under toolbar
            ViewUtil.addRectangularOutlineProvider(findViewById(R.id.toolbar_parent), getResources());
        }

//        mNavigationDrawerFragment.getDrawerToggle().setDrawerIndicatorEnabled(false);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavigationDrawerFragment.getDrawerToggle().syncState();
    }

    private void showAppManagerFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "AppManagerFragment";
        AppManagerFragment fragment = (AppManagerFragment) fm.findFragmentByTag(tag);
        if(fragment == null) {
            fragment = new AppManagerFragment();
            ft.replace(R.id.fragment_container, fragment, tag);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
    }

}
