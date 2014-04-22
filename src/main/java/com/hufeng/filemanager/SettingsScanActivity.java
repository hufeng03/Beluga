package com.hufeng.filemanager;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.hufeng.filemanager.services.UiServiceHelper;

public class SettingsScanActivity extends SettingsItemBaseActivity implements OnClickListener, OnCheckedChangeListener{

	@Override
	protected void setNewSkin(int skin) {
		// TODO Auto-generated method stub
//		mBack.setBackgroundResource(SkinManager.getBackButtonResourceId(skin));
//		mTopTab.setBackgroundResource(SkinManager.getTabBarResourceId(skin));
	}

//	Button mBack;
//	RelativeLayout mTopTab;
	
	public static final String AUDIO_FILTER_SMALL = "Audio_Filter_Small";
	public static final String IMAGE_FILTER_SMALL = "Image_Filter_Small";
	public static final String SCAN_HIDDEN_FILES = "Scan_Hidden_Files";
	public static final String SCAN_GAME_FILES = "Scan_Game_Files";
	
	private boolean mScanHiddenOrig;
	private boolean mScanGameOrig;
	private boolean mScanSmallIconOrig;
	private boolean mScanSmallAudioOrig;
	
	private boolean mScanHiddenNew;
	private boolean mScanGameNew;
	private boolean mScanSmallIconNew;
	private boolean mScanSmallAudioNew;
	
	private CheckBox mCheckScanGame;
	private CheckBox mCheckScanHidden;
	private CheckBox mCheckScanSmallIcon;
	private CheckBox mCheckScanSmallAudio;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings_scan_activity);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		
//		mTopTab = (RelativeLayout)findViewById(R.id.top_tab);
//		mBack = (Button)findViewById(R.id.back);
//		mBack.setOnClickListener(this);
		
		mCheckScanHidden = (CheckBox)findViewById(R.id.settings_scan_hidden_check);
		mCheckScanGame = (CheckBox)findViewById(R.id.settings_scan_game_check);
		mCheckScanSmallIcon = (CheckBox)findViewById(R.id.settings_scan_filter_small_image_check);
		mCheckScanSmallAudio = (CheckBox)findViewById(R.id.settings_scan_filter_small_audio_check);
		

		
		String filter = FileManager.getPreference(SCAN_HIDDEN_FILES, "0");
		if(filter.equals("0"))
		{
			mScanHiddenOrig = false;
		}
		else
		{
			mScanHiddenOrig = true;
		}
		mScanHiddenNew = mScanHiddenOrig;
		mCheckScanHidden.setChecked(mScanHiddenOrig);
		
		filter = FileManager.getPreference(SCAN_GAME_FILES, "0");
		if(filter.equals("0"))
		{
			mScanGameOrig = false;
		}
		else
		{
			mScanGameOrig = true;
		}
		mScanGameNew = mScanGameOrig;
		mCheckScanGame.setChecked(mScanGameOrig);
		
		filter = FileManager.getPreference(AUDIO_FILTER_SMALL, "1");
		if(filter.equals("0"))
		{
			mScanSmallAudioOrig = false;
		}
		else
		{
			mScanSmallAudioOrig = true;
		}
		mScanSmallAudioNew = mScanSmallAudioOrig;
		mCheckScanSmallAudio.setChecked(mScanSmallAudioOrig);
		
		filter = FileManager.getPreference(IMAGE_FILTER_SMALL, "1");
		if(filter.equals("0"))
		{
			mScanSmallIconOrig = false;
		}
		else
		{
			mScanSmallIconOrig = true;
		}
		mScanSmallIconNew = mScanSmallIconOrig;
		mCheckScanSmallIcon.setChecked(mScanSmallIconOrig);
		
		mCheckScanHidden.setOnCheckedChangeListener(this);
		mCheckScanSmallIcon.setOnCheckedChangeListener(this);
		mCheckScanSmallAudio.setOnCheckedChangeListener(this);
		mCheckScanGame.setOnCheckedChangeListener(this);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if((mScanSmallIconNew!=mScanSmallIconOrig) || (mScanSmallAudioNew!=mScanSmallAudioOrig) ||
				(mScanHiddenNew!=mScanHiddenOrig) || (mScanGameNew!=mScanGameOrig))
		{
//			Intent intent2 = new Intent();
//			intent2.setAction(IFileObserverServiceImpl.START_SCAN_FILE_ACTION);
//			FileManager.getAppContext().sendBroadcast(intent2);
            UiServiceHelper.getInstance().startScan();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch(buttonView.getId())
		{
		case R.id.settings_scan_hidden_check:
			if(isChecked)
			{
				FileManager.setPreference(SCAN_HIDDEN_FILES, "1");
				mScanHiddenNew = true;
			}
			else
			{
				FileManager.setPreference(SCAN_HIDDEN_FILES, "0");
				mScanHiddenNew = false;
			}
			break;
		case R.id.settings_scan_game_check:
			if(isChecked)
			{
				FileManager.setPreference(SCAN_GAME_FILES, "1");
				mScanGameNew = true;
			}
			else
			{
				FileManager.setPreference(SCAN_GAME_FILES, "0");
				mScanGameNew = false;
			}
			break;
		case R.id.settings_scan_filter_small_image_check:
			if(isChecked)
			{
				FileManager.setPreference(IMAGE_FILTER_SMALL, "1");
				mScanSmallIconNew = true;
			}
			else
			{
				FileManager.setPreference(IMAGE_FILTER_SMALL, "0");
				mScanSmallIconNew = false;
			}
			break;
		case R.id.settings_scan_filter_small_audio_check:
			if(isChecked)
			{
				FileManager.setPreference(AUDIO_FILTER_SMALL, "1");
				mScanSmallAudioNew = true;
			}
			else
			{
				FileManager.setPreference(AUDIO_FILTER_SMALL, "0");
				mScanSmallAudioNew = false;
			}
			break;
			
		}
	}

}
