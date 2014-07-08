package com.hufeng.filemanager;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.hufeng.filemanager.kanbox.KanBoxTabFragment;

/**
 * Created by feng on 14-5-19.
 */
public class FileManagerDrawerActivity extends FileDrawerActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

//    /**
//     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
//     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        enableImageAnimatorView((ViewGroup)findViewById(R.id.main_container).getRootView());
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mNavigationDrawerFragment != null) {
            DrawerItem item = mNavigationDrawerFragment.getDrawerItem(position);
            item.work(this);
        }
    }

    @Override
    public void showCategoryFragment(int category, String name) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CategoryTabFragment.TAG);
        if (fragment == null) {
            fragment = CategoryTabFragment.newCategoryTabFragment(category);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment, CategoryTabFragment.TAG);
            transaction.commit();
        } else {
            ((CategoryTabFragment)fragment).setCategory(category);
        }
        mTitle = name;
    }

    @Override
    public void showDirectoryFragment(String path, String name) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DirectoryTabFragment.LOG_TAG);
        if (fragment == null) {
            fragment = DirectoryTabFragment.newDirectoryTabFragment(path);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment, DirectoryTabFragment.LOG_TAG);
            transaction.commit();
        } else {
            ((DirectoryTabFragment)fragment).setInitPath(path);
        }
        mTitle = name;
    }

    @Override
    public void showCloudFragment(int provider, String title) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(KanBoxTabFragment.TAG);
        if (fragment == null) {
            fragment = KanBoxTabFragment.newKanBoxTabFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment, KanBoxTabFragment.TAG);
            transaction.commit();
        }
        mTitle = title;
    }

    @Override
    public boolean isDrawerOpen() {
        return mNavigationDrawerFragment != null && mNavigationDrawerFragment.isDrawerOpen();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void refreshFiles() {

    }

    @Override
    public String[] getAllFiles() {
        return new String[0];
    }

    @Override
    public String getParentFile() {
        return null;
    }
}
