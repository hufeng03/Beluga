package com.hufeng.filemanager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hufeng.filemanager.apprate.AppRate;
import com.hufeng.filemanager.drawer.DrawItemManager;
import com.hufeng.filemanager.kanbox.KanBoxTabFragment;
import com.hufeng.nanohttpd.HTTPServerService;

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

    private AppRate mAppRate;

    private boolean mAppRateShown = false;

    private long mLastBackPressTime = -1;

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

        if (Constants.APP_RATE) {
            mAppRate = new AppRate(this);
            mAppRate.init();
        }
        if (HTTPServerService.INTENT_ACTION_HTTP_SERVICE_VIEW.equals(getIntent().getAction())) {
            mNavigationDrawerFragment.selectItem(10);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (HTTPServerService.INTENT_ACTION_HTTP_SERVICE_VIEW.equals(intent.getAction())) {
            mNavigationDrawerFragment.selectItem(10);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //if (mNavigationDrawerFragment != null) {
        DrawerItem item = DrawItemManager.getDrawerItemAtPosition(position);
        item.work(this);
    }

    @Override
    public void showCategoryFragment(int category, String name) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CategoryTabFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = CategoryTabFragment.newCategoryTabFragment(category);
            transaction.replace(R.id.main_container, fragment, CategoryTabFragment.TAG);
            transaction.commit();
        } else {
            ((CategoryTabFragment)fragment).setCategory(category);
            if (fragment.isDetached()) {
                transaction.attach(fragment);
                transaction.commit();
            }
        }
        mTitle = name;
    }

    @Override
    public void showDirectoryFragment(String path, String name) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DirectoryTabFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = DirectoryTabFragment.newDirectoryTabFragment(path);
            transaction.replace(R.id.main_container, fragment, DirectoryTabFragment.TAG);
            transaction.commit();
        } else {
            ((DirectoryTabFragment)fragment).setInitPath(path);
            if (fragment.isDetached()) {
                transaction.attach(fragment);
                transaction.commit();
            }
        }
        mTitle = name;
    }

    @Override
    public void showCloudFragment(int provider, String title) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(KanBoxTabFragment.TAG);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (fragment == null) {
            fragment = KanBoxTabFragment.newKanBoxTabFragment();
            transaction.replace(R.id.main_container, fragment, KanBoxTabFragment.TAG);
            transaction.commit();
        } else {
            if (fragment.isDetached()) {
                transaction.attach(fragment);
                transaction.commit();
            }
        }
        mTitle = title;
    }

    @Override
    public void showFragment(String title, Class<?> fragment_class, String tag, Object... params) {
        FragmentUtil.replaceFragment(getSupportFragmentManager(), R.id.main_container, fragment_class, tag, false, params);
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
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment)getCurrentFragment();
        if (fragment == null || !fragment.onBackPressed()) {
            if (mNavigationDrawerFragment != null && mNavigationDrawerFragment.goBackHomeIfNeeded()) {
                return;
            } else if (!mAppRateShown) {
                if (mLastBackPressTime == -1 || System.currentTimeMillis() - mLastBackPressTime > 2000) {
                    if (mAppRate != null && mAppRate.showIfNeeded()) {
                        mAppRateShown = true;
                    } else {
                        Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT).show();
                    }
                    mLastBackPressTime = System.currentTimeMillis();
                    return;
                }
            }
            super.onBackPressed();
        }
    }

    @Override
    public void refreshFiles() {
        Fragment fragment = getCurrentFragment();
        if (fragment!=null && fragment instanceof FileTabFragment) {
            ((FileTabFragment)fragment).refreshFiles();
        }
    }

    @Override
    public String[] getAllFiles() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof FileTabFragment) {
            return ((FileTabFragment)fragment).getAllFiles();
        } else {
            return null;
        }
    }

    @Override
    public String getParentFile() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof FileTabFragment) {
            return ((FileTabFragment)fragment).getParentFile();
        } else {
            return null;
        }
    }

    private Fragment getCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        return fragment;
    }
}
