package com.hufeng.smsbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.skin.SkinManager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class SmsBackupViewer extends Activity implements OnClickListener{
	
	private ListView mList;
	
	private String mFilePath;
	
	private Button mBack;
	private RelativeLayout mTopTab;
	
	private int mSkin = SkinManager.SKIN_BLACK;
	
//	private SmsListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sms_backup_viewer);
		mList = (ListView)findViewById(R.id.sms_backup_list);
		Uri uri =  getIntent().getData();
		if(uri!=null)
		{
			mFilePath = FileUtils.getPathFromContent(this, uri).getPath();
		}
		mTopTab = (RelativeLayout)findViewById(R.id.top_tab);
		mBack = (Button)findViewById(R.id.back);
		mBack.setOnClickListener(this);
		new LoadSmsBackUpFile().execute();
//		mAdapter = new SmsListAdapter();	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUI();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private class LoadSmsBackUpFile extends AsyncTask<Void, Void, Void>
	{

		private ArrayList<SmsItem> mSmsArray;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if(mSmsArray==null)
			{
				finish();
			}
			else
			{
				mList.setAdapter(new SmsListAdapter(SmsBackupViewer.this, mSmsArray));			
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			readFile();
			return null;
		}
		
		private void readFile()
		{
			XmlPullParser parser = Xml.newPullParser();
			
	        try {

	            FileInputStream fis = new FileInputStream(new File(mFilePath));

	            parser.setInput(fis, "UTF-8");

	            int event = parser.getEventType();
	            
	            boolean flag = true;
	            
	            int address_index = -1;
	            int type_index = -1;
	            int body_index = -1;
	            int date_index = -1;
	            
	            SmsItem item = null;

	            while (event != XmlPullParser.END_DOCUMENT) {

	                switch (event) {

	                case XmlPullParser.START_DOCUMENT:

	                	mSmsArray = new ArrayList<SmsItem>();

	                    break;

	 

	                case XmlPullParser.START_TAG: // 如果遇到开始标记，如<smsItems>,<smsItem>等

	                    if ("item".equals(parser.getName())) {
	                    	
	                    	item = new SmsItem();
	                        int count = parser.getAttributeCount();
	                        if(flag){                     
		                        for(int i=0;i<count;i++)
		                        {
		                        	String name = parser.getAttributeName(i);
		                        	if(SmsField.ADDRESS.equals(name))
		                        	{
		                        		address_index = i;
		                        	}
		                        	else if(SmsField.BODY.equals(name))
		                        	{
		                        		body_index = i;
		                        	}
		                        	else if(SmsField.DATE.equals(name))
		                        	{
		                        		date_index = i;
		                        	}
		                        	else if(SmsField.TYPE.equals(name))
		                        	{
		                        		type_index = i;
		                        	}
		                        }
		                        flag = false;
	                        }
//	                        for(int i=0;i<count;i++)
//	                        {
//	                        	String val = parser.getAttributeValue(i);
//	                        	values.put(columnNames[i], val);
//	                        }
	                        item.setDate(parser.getAttributeValue(date_index));
	                        item.setType(parser.getAttributeValue(type_index));
	                        item.setBody(parser.getAttributeValue(body_index));
	                        item.setAddress(parser.getAttributeValue(address_index));
	                    }

	                    break;

	                case XmlPullParser.END_TAG:// 结束标记,如</smsItems>,</smsItem>等

	                    if ("item".equals(parser.getName())) {

	                    	mSmsArray.add(item);

	                        item = null;

	                    }

	                    break;

	                }

	                event = parser.next();

	            }

	        } catch (FileNotFoundException e) {

	            // TODO Auto-generated catch block

	            e.printStackTrace();

	             

	        } catch (XmlPullParserException e) {

	            // TODO Auto-generated catch block


	            e.printStackTrace();       

	             

	        } catch (IOException e) {

	            // TODO Auto-generated catch block


	            e.printStackTrace();

	        }
		}
		
	}
	
	private void setUI()
	{
		SharedPreferences sp  = PreferenceManager.getDefaultSharedPreferences(this);
		int skin = sp.getInt(SkinManager.SKIN_SELECTION, SkinManager.SKIN_UNDEFINED);
		if(mSkin!=skin)
		{
			mBack.setBackgroundResource(SkinManager.getBackButtonResourceId(skin));
			mTopTab.setBackgroundResource(SkinManager.getTabBarResourceId(skin));
			mSkin = skin;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.back:
			finish();
		}
	}
	

}
