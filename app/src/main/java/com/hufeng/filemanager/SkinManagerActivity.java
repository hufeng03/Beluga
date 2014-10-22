package com.hufeng.filemanager;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.hufeng.filemanager.skin.SkinManager;
import com.hufeng.filemanager.utils.ChannelUtil;

public class SkinManagerActivity extends SettingsItemBaseActivity implements OnClickListener{

//	private int mSkin = SkinManager.SKIN_BLACK;
//	private Button mBack;
//	private RelativeLayout mTopTab;
	

	private CheckBox mCheckBlack = null;
	private CheckBox mCheckRed = null;
//	private ImageView mSkinRed = null;
//	private ImageView mSkinBlack = null;
	
	
	@Override
	protected void setNewSkin(int skin) {
		// TODO Auto-generated method stub
//		mBack.setBackgroundResource(SkinManager.getBackButtonResourceId(skin));
//		mTopTab.setBackgroundResource(SkinManager.getTabBarResourceId(skin));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.skin_manager_activity);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		
//		mTopTab = (RelativeLayout)findViewById(R.id.top_tab);
//		
//		mBack = (Button)findViewById(R.id.back);
//
//		mBack.setOnClickListener(this);	
		
		mCheckBlack = (CheckBox)findViewById(R.id.skin_black_check);
		mCheckRed = (CheckBox)findViewById(R.id.skin_red_check);
		
//		mSkinRed = (ImageView)findViewById(R.id.skin_red_icon);
//		mSkinBlack = (ImageView)findViewById(R.id.skin_black_icon);
		
		SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(SkinManagerActivity.this);
		int selection = sp.getInt(SkinManager.SKIN_SELECTION, 0);
		
		if(selection == SkinManager.SKIN_RED)
		{
			mCheckRed.setChecked(true);
		}
		else if(selection == SkinManager.SKIN_BLACK)
		{
			mCheckBlack.setChecked(true);
		}
		else
		{
			if(ChannelUtil.isDOOV_ROOMChannel(FileManager.getAppContext()))
			{
				mCheckRed.setChecked(true);
			}
			else
			{
				mCheckBlack.setChecked(true);
			}
		}
		
		mCheckRed.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(SkinManagerActivity.this);
					SharedPreferences.Editor edit = sp.edit();
					edit.putInt(SkinManager.SKIN_SELECTION, SkinManager.SKIN_RED);
					edit.commit();
					mCheckBlack.setChecked(false);
					setNewSkin(SkinManager.SKIN_RED);
				}
			}
			
		});
		
		mCheckBlack.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(SkinManagerActivity.this);
					SharedPreferences.Editor edit = sp.edit();
					edit.putInt(SkinManager.SKIN_SELECTION, SkinManager.SKIN_BLACK);
					edit.commit();
					mCheckRed.setChecked(false);
					setNewSkin(SkinManager.SKIN_BLACK);
					
				}
			}
			
		});

//		mList = (ListView)findViewById(R.id.skin_list);
//        List<Map<String, Object>> listdata = new ArrayList<Map<String,Object>>();
//    	       
//        Map<String,Object> map1 = new HashMap<String, Object>();
// 	    map1.put("name", this.getString(R.string.skin_red));
//	    map1.put("icon", R.drawable.doov_top_tab_bg);
//	    if(selection==SKIN_RED)
//	    {
//	    	map1.put("check", true);
//	    }
//	    else{
//	    	map1.put("check", false);
//	    }
//	    listdata.add(map1);
//	    
//	    Map<String,Object> map2 = new HashMap<String, Object>();
// 	    map2.put("name", this.getString(R.string.skin_black));
//	    map2.put("icon", R.drawable.top_tab_bg);
//	    
//	    if(selection==SKIN_BLACK)
//	    {
//	    	map1.put("check", true);
//	    }
//	    else{
//	    	map1.put("check", false);
//	    }
//	    listdata.add(map2);
//	    
//        SimpleAdapter mAdapter = new SimpleAdapter(SkinManagerActivity.this, listdata, 
//        		R.layout.skin_list_item,
//    			new String[]{"name","icon", "check"},
//    			new int[]{R.id.name, R.id.icon, R.id.check});
//        
//        mAdapter.setViewBinder(new ViewBinder()
//        {
//
//			@Override
//			public boolean setViewValue(View view, Object data,
//					String textRepresentation) {
//				// TODO Auto-generated method stub
//				if((data instanceof Boolean) && (view instanceof CheckBox))
//				{
//					if((Boolean)data)
//					{
//						((CheckBox)view).setChecked(true);
//						mCheckedBox = ((CheckBox)view);
//					}
//					else
//					{
//						((CheckBox)view).setChecked(false);
//					}
//					view.setClickable(false);
//					return true;
//				}
//				else
//				{
//					return false;
//				}
//			}
//        	
//        });
//        
//        mList.setAdapter(mAdapter);
//        
//        mList.setOnItemClickListener(new OnItemClickListener(){
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				switch(position)
//				{
//					case 0:
//					{
//						SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(SkinManagerActivity.this);
//						SharedPreferences.Editor edit = sp.edit();
//						edit.putInt(SKIN_SELECTION, SKIN_RED);
//						edit.commit();
//						((CheckBox)view.findViewById(R.id.check)).setChecked(true);
//						if(mCheckedBox!=null)
//							mCheckedBox.setChecked(false);
//						mCheckedBox = (CheckBox)view.findViewById(R.id.check);
//						break;
//					}
//					case 1:
//					{
//						SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(SkinManagerActivity.this);
//						SharedPreferences.Editor edit = sp.edit();
//						edit.putInt(SKIN_SELECTION, SKIN_BLACK);
//						edit.commit();
//						((CheckBox)view.findViewById(R.id.check)).setChecked(true);
//						if(mCheckedBox!=null)
//							mCheckedBox.setChecked(false);
//						mCheckedBox = (CheckBox)view.findViewById(R.id.check);
//						break;
//					}
//				}
//			}
//        	
//        });
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.back:
			finish();
			break;
		}
	}
	
//	private void setUI()
//	{
//		SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(this);
//		int skin = sp.getInt(SkinManager.SKIN_SELECTION, SkinManager.SKIN_UNDEFINED);
//		if(mSkin!=skin)
//		{
//			mBack.setBackgroundResource(SkinManager.getBackButtonResourceId(skin));
//			mTopTab.setBackgroundResource(SkinManager.getTabBarResourceId(skin));
//			mSkin = skin;
//		}
//	}

}
