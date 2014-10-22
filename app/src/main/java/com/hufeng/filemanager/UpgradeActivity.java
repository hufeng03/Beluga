package com.hufeng.filemanager;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UpgradeActivity extends ListActivity{



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		ArrayList<Map<String, String>> list = buildData();
	    String[] from = { "key", "value" };
	    int[] to = { R.id.key, R.id.value };
	    SimpleAdapter adapter = new SimpleAdapter(this, list,
	        R.layout.list_item_key_value, from, to);
	    setListAdapter(adapter);
		
	}
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}
	
	private ArrayList<Map<String, String>> buildData() {
		String versionName = "unknown";
		try {
			String name = getPackageName();
			versionName = ""+(getPackageManager().getPackageInfo(name, 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    list.add(putData(getResources().getString(R.string.current_version_title), versionName));
	    list.add(putData(getResources().getString(R.string.new_version_title), "3.0"));
	    return list;
	  }

	  private HashMap<String, String> putData(String name, String purpose) {
	    HashMap<String, String> item = new HashMap<String, String>();
	    item.put("key", name);
	    item.put("value", purpose);
	    return item;
	  }
	  
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
	        case android.R.id.home:
	            // This is called when the Home (Up) button is pressed
	            // in the Action Bar.
//	            Intent parentActivityIntent = new Intent(this, SettingsActivity.class);
//	            parentActivityIntent.addFlags(
//	                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
//	                    Intent.FLAG_ACTIVITY_NEW_TASK);
//	            startActivity(parentActivityIntent);
//	            finish();
	        	onBackPressed();
	            return true;
		    }
		    return super.onOptionsItemSelected(item);
		}
	
}
