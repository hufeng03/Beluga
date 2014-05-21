package com.hufeng.filemanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;

/**
 * Created by feng on 14-5-19.
 */
public class FileManagerDrawerActivity extends DrawerActivity
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
    public void showCategoryFragment(int category) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CategoryTabFragment.TAG);
        if (fragment == null) {
            fragment = CategoryTabFragment.newCategoryTabFragment(category);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment, CategoryTabFragment.TAG);
            transaction.commit();
        } else {
            ((CategoryTabFragment)fragment).setCategory(category);
        }
    }

    @Override
    public void showDirectoryFragment(String path) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DirectoryTabFragment.TAG);
        if (fragment == null) {
            fragment = DirectoryTabFragment.newDirectoryTabFragment(path);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment, null);
            transaction.commit();
        } else {
            ((DirectoryTabFragment)fragment).setInitPath(path);
        }
    }


    //    public void setMainFragment(String fragment_tag, int category) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.main_container, getOrCreateFragment(fragment_tag, category), null);
//        transaction.commit();
//    }
//
//    private Fragment getOrCreateFragment(String fragment_tag, int category) {
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragment_tag);
//        if (fragment == null) {
//            if (FileGrouperFragment.TAG.equals(fragment_tag)) {
//                fragment = FileGrouperFragment.newCategoryGrouperInstance(category);
//            } else if (FileBrowserFragment.TAG.equals(fragment_tag)) {
//                fragment = FileBrowserFragment.newStorageBrowser();
//            } else {
//
//            }
//        }
//        return fragment;
//    }


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
