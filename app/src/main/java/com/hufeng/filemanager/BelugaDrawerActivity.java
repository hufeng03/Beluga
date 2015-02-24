package com.hufeng.filemanager;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.hufeng.filemanager.apprate.AppRate;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.InfoLoader;
import com.hufeng.filemanager.ui.BelugaActionController;
import com.hufeng.filemanager.ui.FileViewPager;

import java.util.Locale;

import refactor.com.android.contacts.activities.ActionBarAdapter;
import refactor.com.android.contacts.common.list.ViewPagerTabs;
import refactor.com.android.contacts.common.util.ViewUtil;
import refactor.com.android.contacts.list.BelugaIntentResolver;
import refactor.com.android.contacts.list.BelugaRequest;


public class BelugaDrawerActivity extends BelugaActionControllerActivity implements ActionBarAdapter.Listener {

    private static final String LOG_TAG = BelugaDrawerActivity.class.getSimpleName();

    private ActionBarAdapter mActionBarAdapter;
    private ViewPagerTabs mViewPagerTabs;

    private FileViewPager mTabPager;
    private TabPagerAdapter mTabPagerAdapter;

    private CategoryTabFragment mCategoryTabFragment;
    private DeviceTabFragment mDeviceTabFragment;
    private SearchTabFragment mSearchTabFragment;

    private BelugaNavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * True if this activity instance is a re-created one.  i.e. set true after orientation change.
     * This is set in {@link #onCreate} for later use in {@link #onStart}.
     */
    private boolean mIsRecreatedInstance;
    /**
     * If {@link #configureFragments(boolean)} is already called.  Used to avoid calling it twice
     * in {@link #onStart}.
     * (This initialization only needs to be done once in onStart() when the Activity was just
     * created from scratch -- i.e. onCreate() was just called)
     */
    private boolean mFragmentInitialized;

    /**
     * This is to disable {@link #onOptionsItemSelected} when we trying to stop the activity.
     */
    private boolean mDisableOptionItemSelected;

    private long mLastBackPressTime = -1;

    private boolean mAppRateShown = false;

    private boolean mSelection = false;

    private Handler mHandler = new Handler();

    //	private boolean mFirst = true;
    AppRate mAppRate = null;

    private int mOldItemId = -1;

    private String[] mTabTitles;
    private final TabPagerListener mTabPagerListener = new TabPagerListener();
    private final TabClickListener mTabClickListener = new TabClickListener();

    private BelugaIntentResolver mIntentResolver;
    private BelugaRequest mRequest;

    public BelugaDrawerActivity() {
        mIntentResolver = new BelugaIntentResolver(this);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        if (!processIntent(false)) {
            finish();
            return;
        }

        mIsRecreatedInstance = (savedInstanceState != null);

        createViewsAndFragments(savedInstanceState);

//        getWindow().setBackgroundDrawable(null);


    }


    private void createViewsAndFragments(Bundle savedInstanceState) {
        setContentView(R.layout.beluga_activity);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Hide all tabs (the current tab will later be reshown once a tab is selected)
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        mTabTitles = new String[ActionBarAdapter.TabState.COUNT];
        mTabTitles[ActionBarAdapter.TabState.CATEGORY] = getString(R.string.category);
        mTabTitles[ActionBarAdapter.TabState.DEVICE] = getString(R.string.directory);
        mTabPager = (FileViewPager) findViewById(R.id.tab_pager);
        mTabPagerAdapter = new TabPagerAdapter();
        mTabPager.setAdapter(mTabPagerAdapter);
        mTabPager.setOnPageChangeListener(mTabPagerListener);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ViewPagerTabs portraitViewPagerTabs
                = (ViewPagerTabs) findViewById(R.id.lists_pager_header);
        ViewPagerTabs landscapeViewPagerTabs = null;
        if (portraitViewPagerTabs == null) {
            landscapeViewPagerTabs = (ViewPagerTabs) getLayoutInflater().inflate(
                    R.layout.beluga_activity_tabs_lands, toolbar, /* attachToRoot = */ false);
            mViewPagerTabs = landscapeViewPagerTabs;
        } else {
            mViewPagerTabs = portraitViewPagerTabs;
        }
        mViewPagerTabs.setViewPager(mTabPager);

        mViewPagerTabs.setOnTabClickListener(mTabClickListener);

        final String CATEGORY_TAB_FRAGMENT_TAG = "tab-pager-category";
        final String DEVICE_TAB_FRAGMENT_TAG = "tab-pager-device";
        final String SEARCH_TAB_FRAGMENT_TAG = "tab-pager-search";

        // Create the fragments and add as children of the view pager.
        // The pager adapter will only change the visibility; it'll never create/destroy
        // fragments.
        // However, if it's after screen rotation, the fragments have been re-created by
        // the fragment manager, so first see if there're already the target fragments
        // existing.
        mCategoryTabFragment = (CategoryTabFragment) fragmentManager.findFragmentByTag(CATEGORY_TAB_FRAGMENT_TAG);
        mDeviceTabFragment = (DeviceTabFragment) fragmentManager.findFragmentByTag(DEVICE_TAB_FRAGMENT_TAG);
        mSearchTabFragment = (SearchTabFragment) fragmentManager.findFragmentByTag(SEARCH_TAB_FRAGMENT_TAG);

        if (mCategoryTabFragment == null) {
            mCategoryTabFragment = new CategoryTabFragment();
            mDeviceTabFragment = new DeviceTabFragment();
            mSearchTabFragment = new SearchTabFragment();

            transaction.add(R.id.tab_pager, mCategoryTabFragment, CATEGORY_TAB_FRAGMENT_TAG);
            transaction.add(R.id.tab_pager, mDeviceTabFragment, DEVICE_TAB_FRAGMENT_TAG);
            transaction.add(R.id.tab_pager, mSearchTabFragment, SEARCH_TAB_FRAGMENT_TAG);
        }

        // Hide all fragments for now.  We adjust visibility when we get onSelectedTabChanged()
        // from ActionBarAdapter.
        transaction.hide(mCategoryTabFragment);
        transaction.hide(mDeviceTabFragment);
        transaction.hide(mSearchTabFragment);

        transaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();

        mNavigationDrawerFragment = (BelugaNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(drawerLayout, toolbar);

        mActionBarAdapter = new ActionBarAdapter(this, this, getSupportActionBar(),
                portraitViewPagerTabs, landscapeViewPagerTabs, toolbar, mNavigationDrawerFragment.getDrawerToggle());
        mActionBarAdapter.initialize(savedInstanceState, mRequest);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Add shadow under toolbar
            ViewUtil.addRectangularOutlineProvider(findViewById(R.id.toolbar_parent), getResources());
        }

        if (Constants.APP_RATE) {
            mAppRate = new AppRate(this);
            mAppRate.init();
        }

        supportInvalidateOptionsMenu();

        invalidate();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mNavigationDrawerFragment.getDrawerToggle().syncState();
    }

    @Override
    protected void onStart() {
        if (!mFragmentInitialized) {
            mFragmentInitialized = true;
            /* Configure fragments if we haven't.
             *
             * Note it's a one-shot initialization, so we want to do this in {@link #onCreate}.
             *
             * However, because this method may indirectly touch views in fragments but fragments
             * created in {@link #configureContentView} using a {@link FragmentTransaction} will NOT
             * have views until {@link Activity#onCreate} finishes (they would if they were inflated
             * from a layout), we need to do it here in {@link #onStart()}.
             *
             * (When {@link Fragment#onCreateView} is called is different in the former case and
             * in the latter case, unfortunately.)
             *
             * Also, we skip most of the work in it if the activity is a re-created one.
             * (so the argument.)
             */
            configureFragments(!mIsRecreatedInstance);
        }
        super.onStart();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        if (!processIntent(true)) {
            finish();
            return;
        }

        mActionBarAdapter.initialize(null, mRequest);

        configureFragments(true);
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InfoLoader.getInstance().resume();

        // Re-register the listener, which may have been cleared when onSaveInstanceState was
        // called.  See also: onSaveInstanceState
        mActionBarAdapter.setListener(this);
        mDisableOptionItemSelected = false;
        if (mTabPager != null) {
            mTabPager.setOnPageChangeListener(mTabPagerListener);
        }
        // Current tab may have changed since the last onSaveInstanceState().  Make sure
        // the actual contents match the tab.
        updateFragmentsVisibility();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InfoLoader.getInstance().pause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mActionBarAdapter.onSaveInstanceState(outState);

        // Clear the listener to make sure we don't get callbacks after onSaveInstanceState,
        // in order to avoid doing fragment transactions after it.
        // TODO Figure out a better way to deal with the issue.
        mDisableOptionItemSelected = true;
        mActionBarAdapter.setListener(null);
        if (mTabPager != null) {
            mTabPager.setOnPageChangeListener(null);
        }
    }

    @Override
    public void onDestroy() {
        // Some of variables will be null if this Activity redirects Intent.
        // See also onCreate() or other methods called during the Activity's initialization.
        if (mActionBarAdapter != null) {
            mActionBarAdapter.setListener(null);
        }
        super.onDestroy();
    }

//    private void setupPaneLayout(OverlappingPaneLayout paneLayout) {
//        // TODO: Remove the notion of a capturable view. The entire view be slideable, once
//        // the framework better supports nested scrolling.
//        paneLayout.setCapturableView(mViewPagerTabs);
//        paneLayout.openPane();
//        paneLayout.setPanelSlideCallbacks(mPanelSlideCallbacks);
//        paneLayout.setIntermediatePinnedOffset(
//                /*((HostInterface) getActivity()).getActionBarHeight()*/60);
//
////        LayoutTransition transition = paneLayout.getLayoutTransition();
////        // Turns on animations for all types of layout changes so that they occur for
////        // height changes.
////        transition.enableTransitionType(LayoutTransition.CHANGING);
//    }
//
//    private OverlappingPaneLayout.PanelSlideCallbacks mPanelSlideCallbacks = new OverlappingPaneLayout.PanelSlideCallbacks() {
//        @Override
//        public void onPanelSlide(View panel, float slideOffset) {
//            // For every 1 percent that the panel is slid upwards, clip 1 percent off the top
//            // edge of the shortcut card, to achieve the animated effect of the shortcut card
//            // being pushed out of view when the panel is slid upwards. slideOffset is 1 when
//            // the shortcut card is fully exposed, and 0 when completely hidden.
////            float ratioCardHidden = (1 - slideOffset);
////            if (mShortcutCardsListView.getChildCount() > 0) {
////                final SwipeableShortcutCard v =
////                        (SwipeableShortcutCard) mShortcutCardsListView.getChildAt(0);
////                v.clipCard(ratioCardHidden);
////            }
////
////            if (mActionBar != null) {
////                // Amount of available space that is not being hidden by the bottom pane
////                final int topPaneHeight = (int) (slideOffset * mShortcutCardsListView.getHeight());
////
////                final int availableActionBarHeight =
////                        Math.min(mActionBar.getHeight(), topPaneHeight);
////                ((HostInterface) getActivity()).setActionBarHideOffset(
////                        mActionBar.getHeight() - availableActionBarHeight);
////
////                if (!mActionBar.isShowing()) {
////                    mActionBar.show();
////                }
////            }
//        }
//
//        @Override
//        public void onPanelOpened(View panel) {
////            mIsPanelOpen = true;
//        }
//
//        @Override
//        public void onPanelClosed(View panel) {
////            mIsPanelOpen = false;
//        }
//
//        @Override
//        public void onPanelFlingReachesEdge(int velocityY) {
////            if (getCurrentListView() != null) {
////                getCurrentListView().fling(velocityY);
////            }
//        }
//
//        @Override
//        public boolean isScrollableChildUnscrolled() {
////            final AbsListView listView = getCurrentListView();
////            return listView != null && (listView.getChildCount() == 0
////                    || listView.getChildAt(0).getTop() == listView.getPaddingTop());
//            return false;
//        }
//    };
//
//    private AbsListView getCurrentListView() {
//        getCurrentFragment();
//    }


    private void configureFragments(boolean fromRequest) {
        if (fromRequest) {
            int actionCode = mRequest.getActionCode();
            boolean searchMode = mRequest.isSearchMode();
            final int tabToOpen;
            switch (actionCode) {
                case BelugaRequest.ACTION_PICK_DIRECTORY_TO_COPY_FILE:
                    tabToOpen = ActionBarAdapter.TabState.DEVICE;
//                    mDeviceTabFragment.getActionController().setCopyFiles(mRequest.getOperationFiles());
                    break;
                case BelugaRequest.ACTION_PICK_DIRECTORY_TO_MOVE_FILE:
                    tabToOpen = ActionBarAdapter.TabState.DEVICE;
//                    mDeviceTabFragment.getActionController().setMoveFiles(mRequest.getOperationFiles());
                    break;
//                case BelugaRequest.ACTION_PICK_KANBOX_DIRECTORY_TO_UPLOAD_FILE:
//                    tabToOpen = ActionBarAdapter.TabState.REMOTE;
//                    break;
                case BelugaRequest.ACTION_PICK_DIRECTORY_CANCEL:
                    tabToOpen = -1;
                    break;
                default:
                    tabToOpen = -1;
                    break;
            }

            if (tabToOpen != -1) {
                mActionBarAdapter.setCurrentTab(tabToOpen);
            }

            mActionBarAdapter.setSearchMode(searchMode);
        }
        supportInvalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //TODO: recover this
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.beluga_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem searchMenu = menu.findItem(R.id.menu_search);

        final boolean isSearchMode = mActionBarAdapter.isSearchMode();
        if (searchMenu != null) {
            searchMenu.setVisible(!isSearchMode);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDisableOptionItemSelected) {
            return false;
        }
        switch(item.getItemId()) {
            case R.id.menu_search:
                onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSearchRequested() {
        mActionBarAdapter.setSearchMode(true);
        return true;
    }

    public boolean isSearchMode() {
        return mActionBarAdapter.isSearchMode();
    }

    /**
     * Resolve the intent and initialize {@link #mRequest}, and launch another activity if redirect
     * is needed.
     *
     * @param forNewIntent set true if it's called from {@link #onNewIntent(Intent)}.
     * @return {@code true} if {@link BelugaDrawerActivity} should continue running.  {@code false}
     * if it shouldn't, in which case the caller should finish() itself and shouldn't do
     * farther initialization.
     */
    private boolean processIntent(boolean forNewIntent) {
        // Extract relevant information from the intent
        mRequest = mIntentResolver.resolveIntent(getIntent());
        if (!mRequest.isValid()) {
            setResult(RESULT_CANCELED);
            return false;
        }

        return true;
    }


    @Override
    public void onAction(int action) {
        switch (action) {
            case ActionBarAdapter.Listener.Action.START_SEARCH_MODE:
                // Tell the fragments that we're in the search mode
                configureFragments(false /* from request */);
                updateFragmentsVisibility();
                supportInvalidateOptionsMenu();
                break;
            case ActionBarAdapter.Listener.Action.STOP_SEARCH_MODE:
                setQueryTextToFragment("");
                updateFragmentsVisibility();
                supportInvalidateOptionsMenu();
                break;
            case ActionBarAdapter.Listener.Action.CHANGE_SEARCH_QUERY:
                final String queryString = mActionBarAdapter.getQueryString();
                setQueryTextToFragment(queryString);
                break;
            default:
                throw new IllegalStateException("Unknown ActionBarAdapter action: " + action);
        }
    }

    private void setQueryTextToFragment(String query) {
        mSearchTabFragment.setSearchString(query);
    }

    @Override
    public void onSelectedTabChanged() {
        updateFragmentsVisibility();
    }

    @Override
    public void onUpButtonPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mActionBarAdapter.isSearchMode()) {
            mActionBarAdapter.setSearchMode(false);
            return;
        }

        Fragment tabFragment = getCurrentFragment();
        if (tabFragment != null && tabFragment instanceof BelugaFragmentInterface) {
            if (((BelugaFragmentInterface)tabFragment).onBackPressed()) {
                return;
            }
        }

        if (getActionMode() != null) {
            super.onBackPressed();
            return;
        }

//        int tab = mActionBarAdapter.getCurrentTab();
//        if (tab != ActionBarAdapter.TabState.CATEGORY) {
//            mActionBarAdapter.setCurrentTab(ActionBarAdapter.TabState.CATEGORY);
//            return;
//        }

        super.onBackPressed();
    }

    /**
     * Updates the fragment/view visibility according to the current mode, such as
     * {@link ActionBarAdapter#isSearchMode()} and {@link ActionBarAdapter#getCurrentTab()}.
     */
    private void updateFragmentsVisibility() {
        int tab = mActionBarAdapter.getCurrentTab();

        if (mActionBarAdapter.isSearchMode()) {
            mTabPagerAdapter.setSearchMode(true);
        } else {
            // No smooth scrolling if quitting from the search mode.
            final boolean wasSearchMode = mTabPagerAdapter.isSearchMode();
            mTabPagerAdapter.setSearchMode(false);
            if (mTabPager.getCurrentItem() != tab) {
                mTabPager.setCurrentItem(tab, !wasSearchMode);
            }
        }
        supportInvalidateOptionsMenu();
//        showEmptyStateForTab(tab);
    }

//    private void showEmptyStateForTab(int tab) {
//        if (mContactsUnavailableFragment != null) {
//            switch (tab) {
//                case TabState.FAVORITES:
//                    mContactsUnavailableFragment.setMessageText(
//                            R.string.listTotalAllContactsZeroStarred, -1);
//                    break;
//                case TabState.ALL:
//                    mContactsUnavailableFragment.setMessageText(R.string.noContacts, -1);
//                    break;
//            }
//            // When using the mContactsUnavailableFragment the ViewPager doesn't contain two views.
//            // Therefore, we have to trick the ViewPagerTabs into thinking we have changed tabs
//            // when the mContactsUnavailableFragment changes. Otherwise the tab strip won't move.
//            mViewPagerTabs.onPageScrolled(tab, 0, 0);
//        }
//    }

    private class TabClickListener implements ViewPagerTabs.OnTabClickListener {
        @Override
        public void onTabReclicked(int position) {
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof FileTabFragment) {
                ((FileTabFragment)fragment).showInitialState();
            }
        }
    }

    private class TabPagerListener implements ViewPager.OnPageChangeListener {

        // This package-protected constructor is here because of a possible compiler bug.
        // PeopleActivity$1.class should be generated due to the private outer/inner class access
        // needed here.  But for some reason, PeopleActivity$1.class is missing.
        // Since $1 class is needed as a jvm work around to get access to the inner class,
        // changing the constructor to package-protected or public will solve the problem.
        // To verify whether $1 class is needed, javap PeopleActivity$TabPagerListener and look for
        // references to PeopleActivity$1.
        //
        // When the constructor is private and PeopleActivity$1.class is missing, proguard will
        // correctly catch this and throw warnings and error out the build on user/userdebug builds.
        //
        // All private inner classes below also need this fix.
        TabPagerListener() {}

        @Override
        public void onPageScrollStateChanged(int state) {
            if (!mTabPagerAdapter.isSearchMode()) {
                mViewPagerTabs.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (!mTabPagerAdapter.isSearchMode()) {
                mViewPagerTabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            // Make sure not in the search mode, in which case position != TabState.ordinal().
            if (!mTabPagerAdapter.isSearchMode()) {
                mActionBarAdapter.setCurrentTab(position, false);
                mViewPagerTabs.onPageSelected(position);
//                showEmptyStateForTab(position);
                supportInvalidateOptionsMenu();
            }
        }
    }

    /**
     * Adapter for the {@link ViewPager}.  Unlike {@link FragmentPagerAdapter},
     * {@link #instantiateItem} returns existing fragments, and {@link #instantiateItem}/
     * {@link #destroyItem} show/hide fragments instead of attaching/detaching.
     *
     * In search mode, we always show the "all" fragment, and disable the swipe.  We change the
     * number of items to 1 to disable the swipe.
     *
     * TODO figure out a more straight way to disable swipe.
     */
    private class TabPagerAdapter extends PagerAdapter {
        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;

        private boolean mTabPagerAdapterSearchMode;

        private Fragment mCurrentPrimaryItem;

        public TabPagerAdapter() {
            mFragmentManager = getSupportFragmentManager();
        }

        public boolean isSearchMode() {
            return mTabPagerAdapterSearchMode;
        }

        public void setSearchMode(boolean searchMode) {
            if (searchMode == mTabPagerAdapterSearchMode) {
                return;
            }
            mTabPagerAdapterSearchMode = searchMode;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabPagerAdapterSearchMode ? 1 : ActionBarAdapter.TabState.COUNT;
        }

        /** Gets called when the number of items changes. */
        @Override
        public int getItemPosition(Object object) {
            if (mTabPagerAdapterSearchMode) {
                if (object == /*mDeviceTabFragment*/mSearchTabFragment) {
                    return 0; // Only 1 page in search mode
                }
            } else {
                if (object == mCategoryTabFragment) {
                    return getTabPositionForTextDirection(ActionBarAdapter.TabState.CATEGORY);
                }
                if (object == mDeviceTabFragment) {
                    return getTabPositionForTextDirection(ActionBarAdapter.TabState.DEVICE);
                }
            }
            return POSITION_NONE;
        }

        @Override
        public void startUpdate(ViewGroup container) {
        }

        private Fragment getFragment(int position) {
            position = getTabPositionForTextDirection(position);
            if (mTabPagerAdapterSearchMode) {
                if (position != 0) {
                    // This has only been observed in monkey tests.
                    // Let's log this issue, but not crash
                    Log.w(LOG_TAG, "Request fragment at position=" + position + ", even though we " +
                            "are in search mode");
                }
                return/*mDeviceTabFragment*/mSearchTabFragment;
            } else {
                if (position == ActionBarAdapter.TabState.CATEGORY) {
                    return mCategoryTabFragment;
                } else if (position == ActionBarAdapter.TabState.DEVICE) {
                    return mDeviceTabFragment;
                } else {
                    throw new IllegalArgumentException("position: " + position);
                }
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            Fragment f = getFragment(position);
            mCurTransaction.show(f);

            // Non primary pages are not visible.
            f.setUserVisibleHint(f == mCurrentPrimaryItem);
            return f;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.hide((Fragment) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            if (mCurrentPrimaryItem != fragment) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setUserVisibleHint(false);
                }
                if (fragment != null) {
                    fragment.setUserVisibleHint(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }


    public Fragment getCurrentFragment() {
        int item = mTabPager.getCurrentItem();
        Fragment fragment = mTabPagerAdapter.getFragment(item);
        return fragment;
    }

    @Override
    public BelugaActionController getActionController() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BelugaFragmentInterface) {
            return ((BelugaFragmentInterface) fragment).getActionController();
        } else {
            return super.getActionController();
        }
    }


    @Override
    public FileEntry[] getAllEntries() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BelugaFragmentInterface) {
            return ((BelugaFragmentInterface) fragment).getAllFiles();
        } else {
            return null;
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof BelugaFragmentInterface) {
            ((BelugaFragmentInterface) fragment).refreshUI();
        }
    }
//
////    @Override
//    public void invalidate() {
////        super.invalidate();
//        if (getActionController() != null &&
//                (getActionController().getOperationMode() == FileOperationController.OPERATION_MODE.ADD_CLOUD
//              || getActionController().getOperationMode() == FileOperationController.OPERATION_MODE.COPY_PASTE
//              || getActionController().getOperationMode() == FileOperationController.OPERATION_MODE.CUT_PASTE)) {
//            mTabPager.setPagingEnabled(false);
//            mViewPagerTabs.setTabEnabled(false);
//        } else {
//            mTabPager.setPagingEnabled(getActionMode() == null);
//            mViewPagerTabs.setTabEnabled(getActionMode() == null);
//        }
//    }

    private boolean isRTL() {
        final Locale locale = Locale.getDefault();
        return TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL;
    }

    /**
     * Returns the tab position adjusted for the text direction.
     */
    private int getTabPositionForTextDirection(int position) {
        if (isRTL()) {
            return ActionBarAdapter.TabState.COUNT - 1 - position;
        }
        return position;
    }

    public void openMyFiles() {

    }

    public void openMyApps() {

    }
}