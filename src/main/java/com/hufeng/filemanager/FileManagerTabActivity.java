package com.hufeng.filemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.hufeng.filemanager.apprate.AppRate;
import com.hufeng.filemanager.dialog.FmDialogFragment;
import com.hufeng.filemanager.kanbox.KanBoxTabFragment;
import com.hufeng.filemanager.ui.FileOperation;
import com.hufeng.filemanager.ui.FileViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class FileManagerTabActivity extends FileOperationActivity{

	private static final String LOG_TAG = FileManagerTabActivity.class.getSimpleName();
	
	public static final String ACTION_MOVE_FILES = "action_move_files";
	public static final String ACTION_COPY_FILES = "action_copy_files";
    public static final String ACTION_UPLOAD_FILES = "action_upload_files";
	
	private static final int FRAGMENT_COUNT = 3;
    private static final int FRAGMENT_INDEX_SELECTED = -1;
    private static final int FRAGMENT_INDEX_CATEGORY = 0;
    private static final int FRAGMENT_INDEX_DEVICE = 1;
    private static final int FRAGMENT_INDEX_TOOLS = 2;
    private static final int FRAGMENT_INDEX_CLOUD = 2;

	private FileViewPager mViewPager;
	private TabAdapter mTabAdapter;
	
	private ActionBar mActionBar;
	
	private long mLastBackPressTime = -1;

    private boolean mAppRateShown = false;

    private boolean mSelection = false;

    private Handler mHandler = new Handler();
	
//	private boolean mFirst = true;
    AppRate mAppRate = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		checkUpgradeSlient();
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		if(savedInstanceState!=null)
//			mFirst = savedInstanceState.getBoolean("FIRST", true);
	    
		setContentView(R.layout.fragment_pager);

		mViewPager = (FileViewPager)findViewById(R.id.pager);
		
		mViewPager.setOffscreenPageLimit(FRAGMENT_COUNT-1);
				
		mActionBar = getSupportActionBar();

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);

//        final Tab selectedTab = getSupportActionBar().newTab().setText(
//                R.string.selected);
		final Tab categoryTab = getSupportActionBar().newTab().setText(
                R.string.category);
        final Tab directoryTab = getSupportActionBar().newTab().setText(
                R.string.directory);
        final Tab toolsTab = getSupportActionBar().newTab().setText(
                R.string.tools);
        final Tab kanboxTab = getSupportActionBar().newTab().setText(R.string.kanbox);
        
        mTabAdapter = new TabAdapter(this, mViewPager);

//        mTabAdapter.addTab(selectedTab,
//                SelectedFragment.class, null);
        mTabAdapter.addTab(categoryTab,
        		CategoryTabFragment.class, null);
        mTabAdapter.addTab(directoryTab,
        		DirectoryTabFragment.class, null);
        if(Constants.SHOW_KANBOX_CATEGORY) {
            mTabAdapter.addTab(kanboxTab,
                    KanBoxTabFragment.class, null);
        } else {
            mTabAdapter.addTab(toolsTab,
                    ToolsFragment.class, null);
        }

        mViewPager.setCurrentItem(0);

        handleIntent(getIntent());

        enableImageAnimatorView((ViewGroup)mViewPager.getRootView());

        if (Constants.APP_RATE) {
            mAppRate = new AppRate(this);
            mAppRate.init();
        }

	}

    @Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		outState.putInt("CATEGORY", mCategory);
//		outState.putBoolean(mFirst, true);
//		outState.putBoolean("FIRST", false);
	}
	
	@Override
	public void onNewIntent(Intent intent){
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		handleIntent(intent);
	}

    @Override
    protected void onResume() {
        super.onResume();
//        if(mViewPager.getCurrentItem()==FRAGMENT_INDEX_CATEGORY)
//            mFileOperation.setFileOperationProvider((CategoryTabFragment)getCurrentFragment());
//        else if(mViewPager.getCurrentItem()==FRAGMENT_INDEX_DEVICE)
//            mFileOperation.setFileOperationProvider((DirectoryTabFragment)getCurrentFragment());
//        else
//            mFileOperation.setFileOperationProvider(null);
        //mFileOperation.setSelection(mSelection);
//        mFileOperation.setOperationMode(FileOperation.OPERATION_MODE.SELECT);

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mFileOperation.setListener(null);
    }

    @Override
	public void onDestroy() {

        super.onDestroy();

	}
	
	@Override
	public void invalidateOptionsMenu(){
		super.invalidateOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("");
		return true;
	}
	
	private void handleIntent(Intent intent) {
        boolean flag = false;
//		if( Intent.ACTION_SEARCH.equals(intent.getAction()) ) {
//			String query = intent.getStringExtra(SearchManager.QUERY);
//		    doMySearch(query);
//            mViewPager.setCurrentItem(FRAGMENT_INDEX_DEVICE);
//            flag = true;
//		} else
        if( ACTION_MOVE_FILES.equals(intent.getAction()) ) {
			String[] files = intent.getStringArrayExtra("files");
            getFileOperation().setMoveFiles(files);
            mViewPager.setCurrentItem(FRAGMENT_INDEX_DEVICE);
		} else if( ACTION_COPY_FILES.equals(intent.getAction()) ) {
			String[] files = intent.getStringArrayExtra("files");
            getFileOperation().setCopyFiles(files);
            mViewPager.setCurrentItem(FRAGMENT_INDEX_DEVICE);
		}
//        else if( ACTION_UPLOAD_FILES.equals(intent.getAction())) {
//            String[] files = intent.getStringArrayExtra("files");
//            ((KanBoxTabFragment)mTabAdapter.getFragment(FRAGMENT_INDEX_TOOLS)).getFileOperation().setUploadFiles(files);
//        }
        refreshUI();
        invalidateOptionsMenu();
	}

    @Override
    public void onBackPressed() {
        BaseFragment fragment = (BaseFragment) mTabAdapter
                .getFragment(mViewPager.getCurrentItem());
        if (!fragment.onBackPressed()) {

            if(mViewPager.getPagingEnabled() && mViewPager.getCurrentItem() != FRAGMENT_INDEX_CATEGORY) {
                mViewPager.setCurrentItem(FRAGMENT_INDEX_CATEGORY);
                return;
            } else if (!mAppRateShown){
                if(mLastBackPressTime==-1 || System.currentTimeMillis()-mLastBackPressTime>2000)
                {
                    if(mAppRate != null && mAppRate.showIfNeeded()) {
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

    public void selectTabWithDelay(final int pos) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int count = mActionBar.getNavigationItemCount();
                if (pos < count)
                    mActionBar.setSelectedNavigationItem(pos);
            }
        });
    }


    public void refreshTabWithDelay() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                refreshFiles();
            }
        });
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between pages.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabsAdapter supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct paged in the ViewPager whenever the selected
     * tab changes.
     */
    public static class TabAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

		private final WeakReference<FileManagerTabActivity> mActivity;
        private final ActionBar mActionBar;
        private final FileViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;
            
            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }
        
//    	public Fragment findOrCreateFragment(Context mContext, String name, Bundle args) {  
//    		FragmentManager fm =  ((FileManagerTabActivity) mContext).getSupportFragmentManager(); 
//            Fragment fragment = fm.findFragmentByTag(name);  
//            if (fragment == null) {  
//            	FragmentTransaction ft = fm.beginTransaction();
//                fragment = Fragment.instantiate(mContext, name, args);  
//                ft.add(fragment, name);
//                ft.commit();
//            }  
////    		Fragment fragment = Fragment.instantiate(mContext, name, args);
//            return fragment;  
//        } 

        public TabAdapter(FileManagerTabActivity activity, FileViewPager pager) {
            super(activity.getSupportFragmentManager());
            mActivity = new WeakReference<FileManagerTabActivity>(activity);
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }
        
        public Fragment getFragment(int pos){
        	return mTabs.get(pos).fragment;
        }
        
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			Object obj = null;
			FileManagerTabActivity activity = mActivity.get();
			if (activity != null) {
				obj = activity.getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + position);
			}
			if (obj == null) {
				Log.i(LOG_TAG, "create new fragment with tag = " + "android:switcher:" + R.id.pager + ":" + position);
				obj =  super.instantiateItem(container, position);
			}else{
				Log.i(LOG_TAG, "use previous fragment with tag = "+"android:switcher:" + R.id.pager + ":" + position);
			}
			if (obj instanceof Fragment) {
	            TabInfo info = mTabs.get(position);
				info.fragment = (Fragment)obj;
				((Fragment) obj).setMenuVisibility(false);
//                if (mViewPager.getCurrentItem() == position) {
//                    if (obj instanceof FileOperation.FileOperationProvider)
//                        activity.getGlobalFileOperation().setFileOperationProvider((FileOperation.FileOperationProvider)obj);
//                }
	        }
	        return obj;
		}

        @Override
        public Fragment getItem(int position) {
            
        	SherlockFragmentActivity activity = mActivity.get();
            if(activity!=null){
	            TabInfo info = mTabs.get(position);
	            if(info.fragment==null)
	            	info.fragment = Fragment.instantiate(activity, info.clss.getName(), info.args);
	            return info.fragment;
            }else{
            	return null;
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        
        @Override
        public void onPageSelected(int position) {
        	Log.i(LOG_TAG, "page selected " + position);
            int tab_pos = mActionBar.getSelectedNavigationIndex();
            if (tab_pos != position) {
                int count = mActionBar.getNavigationItemCount();
                if(position<count)
                    mActionBar.setSelectedNavigationItem(position);
            }
            FileManagerTabActivity activity = mActivity.get();
            if(activity!=null) {
//                if(position == FRAGMENT_INDEX_CATEGORY){
//                    CategoryTabFragment fragment = ((CategoryTabFragment)getFragment(position));
//                    if(fragment!=null){
//                        activity.getFileOperation().setFileOperationProvider(fragment);
//                        fragment.refreshFiles();
//                    }
//                } else if(position==FRAGMENT_INDEX_DEVICE) {
//                    DirectoryTabFragment fragment = ((DirectoryTabFragment)getFragment(position));
//                    if(fragment!=null) {
//                        activity.getFileOperation().setFileOperationProvider(fragment);
//                        fragment.refreshFiles();
//                    }
//                } else {
//                    activity.getFileOperation().setFileOperationProvider(null);
//                }
//                Fragment fragment = getFragment(position);
//                if (fragment instanceof FileOperation.FileOperationProvider) {
//                    activity.getGlobalFileOperation().setFileOperationProvider((FileOperation.FileOperationProvider)fragment);
//                } else {
//                    activity.getGlobalFileOperation().setFileOperationProvider(null);
//                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	Log.i(LOG_TAG, "tab selected "+tab.getPosition());

//            mActionBar.selectTab(mActionBar.getTabAt(0));

        	if(mViewPager.getCurrentItem() != tab.getPosition()) {
                if(mViewPager.getPagingEnabled()) {
        		    mViewPager.setCurrentItem(tab.getPosition());
                    mActivity.get().refreshTabWithDelay();
                } else {
                    int position = mViewPager.getCurrentItem();
                    mActivity.get().selectTabWithDelay(position);
                    //mActionBar.selectTab(mActionBar.getTabAt(mViewPager.getCurrentItem()));
                    //mActionBar.setSelectedNavigationItem(mViewPager.getCurrentItem());
                }
        	}

//        	Object tag = tab.getTag();
//            for (int i=0; i<mTabs.size(); i++) {
//                if (mTabs.get(i) == tag) {
//                    mViewPager.setCurrentItem(i);
//                }
//            }
//            Activity activity = mActivity.get();
//            if(activity!=null && !tab.getText().equals(activity.getString(R.string.tab_sd))) {
//                ActionMode actionMode = ((FileManagerTabActivity) activity).getActionMode();
//                if (actionMode != null) {
//                    actionMode.finish();
//                }
//            }
//        	int position = tab.getPosition();
//        	if(position == FRAGMENT_INDEX_DEVICE) {
//        		DirectoryTabFragment fragment = ((DirectoryTabFragment)getFragment(position));
//        		if(fragment!=null) {
//        			fragment.refreshAd();
//        		}
//        	}
//        	else if(position == FRAGMENT_INDEX_TOOLS){
//        		ToolsFragment fragment = ((ToolsFragment)getFragment(position));
//        		if(fragment!=null) {
//        			fragment.refreshAd();
//        		}
//        	}
//        	if(position !=0){
//        		CategoryFragment fragment = ((CategoryFragment)getFragment(0));
//        		if(fragment!=null) {
//        			fragment.completeRefresh();
//        		}
//        	}
        }

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            Log.i(LOG_TAG, "tab unselected "+tab.getPosition());
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
            Log.i(LOG_TAG, "tab reselected "+tab.getPosition());
			//if(mReSelect){
				int position = tab.getPosition();
            	if(position==FRAGMENT_INDEX_CATEGORY){
            		CategoryTabFragment fragment = ((CategoryTabFragment)getFragment(position));
            		if(fragment!=null) {
            			fragment.showCategoryPanel();
            		}
            	}
            	else if(position==FRAGMENT_INDEX_DEVICE){
            		DirectoryTabFragment fragment = ((DirectoryTabFragment)getFragment(position));
            		if(fragment!=null) {
            			fragment.showBrowserRoot();
            		}
            	}
                else if(position==FRAGMENT_INDEX_TOOLS) {
                    Fragment fragment = getFragment(position);
                    if (fragment!=null && fragment instanceof KanBoxTabFragment) {
                        ((KanBoxTabFragment)fragment).showBrowserRoot();
                    }
                }
            	SherlockFragmentActivity activity = mActivity.get();
            	if( activity!=null ) activity.invalidateOptionsMenu();
//				Toast.makeText(mActivity.get(), "reselect tab", Toast.LENGTH_SHORT).show();
			}
    }
    
    private void checkUpgradeSlient(){
        if (Constants.USE_UMENG) {
    	    UmengDelegate.update(this);
        }

//    	UmengUpdateAgent.setUpdateAutoPopup(false);
////    	Toast.makeText(FileManagerTabActivity.this, R.string.about_us_activity_upgrade_check, Toast.LENGTH_SHORT).show();
//    	UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//    	        @Override
//    	        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
//    	            switch (updateStatus) {
//    	            case 0: // has update
//    	            	String new_version = updateInfo.version;
//    	            	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(FileManagerTabActivity.this);
//    	            	sp.edit().putString("new_version", new_version).commit();
////    	                UmengUpdateAgent.showUpdateDialog(FileManagerTabActivity.this, updateInfo);
//    	                break;
//    	            case 1: // has no update
////    	                Toast.makeText(FileManagerTabActivity.this, R.string.about_us_activity_upgrade_no_need, Toast.LENGTH_SHORT)
////    	                        .show();
//    	                break;
//    	            case 2: // none wifi
////    	                Toast.makeText(FileManagerTabActivity.this, R.string.about_us_activity_upgrade_no_wifi, Toast.LENGTH_SHORT)
////    	                        .show();
//    	                break;
//    	            case 3: // time out
////    	                Toast.makeText(FileManagerTabActivity.this, R.string.about_us_activity_upgrade_time_out, Toast.LENGTH_SHORT)
////    	                        .show();
//    	                break;
//    	            }
//    	        }
//    	});
    }

    public void gotoCloud() {
        mViewPager.setCurrentItem(FRAGMENT_INDEX_CLOUD);
//        mViewPager.setPagingEnabled(false);
    }

    public Fragment getCurrentFragment() {
        int item = mViewPager.getCurrentItem();
        Fragment fragment = mTabAdapter.getFragment(item);
        return fragment;
    }

    @Override
    protected FileOperation getFileOperation() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof FileTabFragment) {
            return ((FileTabFragment) fragment).getFileOperation();
        } else {
            return super.getFileOperation();
        }
    }
//
//    @Override
//    public void dismissCurrentFragment() {
//        onBackPressed();
//    }


    @Override
    public String getParentFile() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof FileTabFragment) {
            return ((FileTabFragment)fragment).getParentFile();
        } else {
            return null;
        }
//        int id = mViewPager.getCurrentItem();
//        if (id == FRAGMENT_INDEX_CATEGORY) {
//            return ((CategoryTabFragment)mTabAdapter.getFragment(FRAGMENT_INDEX_CATEGORY)).getParentFile();
//        } else if (id == FRAGMENT_INDEX_DEVICE) {
//            return ((DirectoryTabFragment)mTabAdapter.getFragment(FRAGMENT_INDEX_DEVICE)).getParentFile();
//        } else {
//            return null;
//        }
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
    public void refreshFiles() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof FileTabFragment) {
            ((FileTabFragment)fragment).refreshFiles();
        }
    }

    @Override
    public void refreshUI() {
        super.refreshUI();
//        if (getFileOperation().getOperationMode()== FileOperation.OPERATION_MODE.ADD_CLOUD || getFileOperation().isMovingOrCopying()) {
//            mViewPager.setPagingEnabled(false);
//        } else {
            if (getActionMode() != null) {
                mViewPager.setPagingEnabled(false);
            } else {
                mViewPager.setPagingEnabled(true);
            }
//        }
    }

    public void setPagingEnabled(boolean enabled) {
        mViewPager.setPagingEnabled(enabled);
    }


    @Override
    public void onDialogDone(DialogInterface dialog, int dialog_id, int button, Object param) {
        super.onDialogDone(dialog, dialog_id, button, param);
        if (dialog_id == FmDialogFragment.ADD_TO_CLOUD_DIALOG) {
            int item = mViewPager.getCurrentItem();
            if (item == FRAGMENT_INDEX_CLOUD) {
                onBackPressed();
            } else {
                gotoCloud();
            }
        }
    }
}