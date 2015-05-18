package com.belugamobile.filemanager;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.belugamobile.filemanager.app.AppManagerFragment;

import refactor.com.android.contacts.common.util.ViewUtil;

/**
 * Created by Feng on 2015-05-10.
 */
public class WebServerActivity  extends BelugaBaseActionBarActivity {

    private BelugaNavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_server_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationDrawerFragment = (BelugaNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(drawerLayout, toolbar, new BelugaNavigationDrawerFragment.DrawerItem[]{
                new BelugaNavigationDrawerFragment.DrawerItem(R.id.drawer_item_my_files, getResources().getDrawable(R.drawable.ic_files_24dp), getString(R.string.my_files), false),
                new BelugaNavigationDrawerFragment.DrawerItem(R.id.drawer_item_my_apps, getResources().getDrawable(R.drawable.ic_apps_24dp), getString(R.string.my_apps), false),
                new BelugaNavigationDrawerFragment.DrawerItem(R.id.drawer_item_my_servers, getResources().getDrawable(R.drawable.ic_apps_24dp), getString(R.string.my_servers), true),
                new BelugaNavigationDrawerFragment.DrawerItem(R.id.drawer_item_settings, getResources().getDrawable(R.drawable.ic_settings_24dp), getString(R.string.settings_label), false),
                new BelugaNavigationDrawerFragment.DrawerItem(R.id.drawer_item_about, getResources().getDrawable(R.drawable.ic_info_24dp), getString(R.string.about_label), false),
                new BelugaNavigationDrawerFragment.DrawerItem(R.id.drawer_item_help_and_feedback, getResources().getDrawable(R.drawable.ic_help_24dp), getString(R.string.help_and_feedback), false)
        }, R.id.drawer_item_my_servers);

        showWebServerFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Add shadow under toolbar
            ViewUtil.addRectangularOutlineProvider(findViewById(R.id.toolbar_parent), getResources());
        }
    }

    private void showWebServerFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final String tag = "WebServerFragment";
        WebServerFragment fragment = (WebServerFragment) fm.findFragmentByTag(tag);
        if(fragment == null) {
            fragment = new WebServerFragment();
            ft.replace(R.id.fragment_container, fragment, tag);
        } else {
            if (fragment.isDetached()) {
                ft.attach(fragment);
            }
        }
        ft.commit();
    }
}
