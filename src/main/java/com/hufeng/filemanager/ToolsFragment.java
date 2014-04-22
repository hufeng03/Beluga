package com.hufeng.filemanager;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hufeng.filemanager.tools.Tool;
import com.hufeng.filemanager.tools.ToolsGridAdapter;
import com.hufeng.filemanager.tools.ToolsListAdapter;
import com.hufeng.filemanager.tools.ToolsManager;
import com.hufeng.filemanager.utils.LogUtil;

public class ToolsFragment extends BaseFragment implements OnClickListener, OnItemClickListener{

	private static final String LOG_TAG = ToolsFragment.class.getSimpleName();
	
	private LinearLayout mRootView;
	private GridView mGridView;
	private ListView mListView;
	private ToolsListAdapter mListAdapter;
	private ToolsGridAdapter mGridAdapter;
	private int mDisplayMode = 0; //0: list, 1: grid
	
	private View mAdView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(Constants.RETAIN_FRAGMENT){
			setRetainInstance(true);
		}
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = (LinearLayout)inflater.inflate(R.layout.tools_tab, container, false);
		return mRootView;
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews();

        initListeners();

    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        if (Constants.SHOW_AD) {
            mAdView = AdmobDelegate.showAd(getActivity(), mRootView);
        }
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (Constants.SHOW_AD) {
            if(mAdView!=null){
                AdmobDelegate.distroyAd(mAdView);
            }
        }
    }
	
//	public void refreshAd() {
//		if(mAdView!=null){
//			if(System.currentTimeMillis()-mLastRefreshTime>1000*10){
//				mAdView.loadAd(new AdRequest());
//				mLastRefreshTime = System.currentTimeMillis();
//			}
//		}
//	}
	
	private void findViews(){
		if(mRootView!=null){
			mListView = (ListView)mRootView.findViewById(R.id.tools_list);
			mGridView = (GridView)mRootView.findViewById(R.id.tools_grid);
            mListAdapter = new ToolsListAdapter(getActivity());
            mGridAdapter = new ToolsGridAdapter(getActivity());
			mListView.setAdapter(mListAdapter);
			mGridView.setAdapter(mGridAdapter);
			mListView.setOnItemClickListener(this);
			mGridView.setOnItemClickListener(this);
			Tool[] tools = ToolsManager.getAllTools(getActivity());
			mListAdapter.setData(tools);
			mGridAdapter.setData(tools);
			if(mDisplayMode == 0){
				mListView.setVisibility(View.VISIBLE);
				mGridView.setVisibility(View.GONE);
			}else{
				mListView.setVisibility(View.GONE);
				mGridView.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void initListeners(){
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		menu.clear();
//		boolean visible = getMenuVisibility();
//		LogUtil.i(LOG_TAG, "onCreateOptionsMenu with "+menu.size()+" "+visible);
		inflater.inflate(R.menu.tools_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onDestroyOptionsMenu() {
		LogUtil.i(LOG_TAG, "onDestroyOptionsMenu");
		super.onDestroyOptionsMenu();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_display:
			{
				if(mDisplayMode == 0) {
					mDisplayMode = 1;
					int pos = mListView.getFirstVisiblePosition();
					mListView.setVisibility(View.GONE);
					mGridView.setVisibility(View.VISIBLE);
					mGridView.setSelection(pos);
				} else {
					mDisplayMode = 0;
					int pos = mGridView.getFirstVisiblePosition();
					mListView.setVisibility(View.VISIBLE);
					mGridView.setVisibility(View.GONE);
					mListView.setSelection(pos);
				}
				getActivity().invalidateOptionsMenu();
				break;
			}
			case R.id.menu_search:
			{
				break;
			}
			case R.id.menu_sort:
			{
				break;
			}
			case R.id.menu_create:
			{
				break;
			}
		}
		return true;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		LogUtil.i(LOG_TAG, "onPrepareOptionsMenu");
		super.onPrepareOptionsMenu(menu);
		if( mDisplayMode == 0 ) {
        	MenuItem item = menu.findItem(R.id.menu_display);
        	if(item!=null)
        		item.setIcon(R.drawable.ic_menu_display_as_grid_holo_light);
        }else{
        	MenuItem item = menu.findItem(R.id.menu_display);
        	if(item!=null)
        		item.setIcon(R.drawable.ic_menu_display_as_list_holo_light);
        }
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Tool tool = (Tool)parent.getAdapter().getItem(position);
		if(tool != null){
			String activity_name = tool.activity_name;
			String package_name = tool.package_name;
			if(!TextUtils.isEmpty(activity_name) && !TextUtils.isEmpty(activity_name)){
//				if(ToolsManager.RECOMMEND.equals(activity_name)){
//					AppConnect.getInstance(getSherlockActivity()).showOffers(getSherlockActivity());
//				}else{
					Intent intent = new Intent();
	//				intent.setClassName(package_name, package_name+activity_name);
					ComponentName cn = new ComponentName(package_name, activity_name);
					intent.setComponent(cn);
					getActivity().startActivity(intent);
//				}
			}
		}
	}
	
}
