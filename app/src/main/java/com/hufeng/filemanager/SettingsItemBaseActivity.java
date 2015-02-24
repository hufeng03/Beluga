package com.hufeng.filemanager;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;


public class SettingsItemBaseActivity extends BelugaBaseActionBarActivity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true); 
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
            // This is called when the Home (Up) button is pressed
            // in the Action Bar.
//            Intent parentActivityIntent = new Intent(this, SettingsActivity.class);
//            parentActivityIntent.addFlags(
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                    Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(parentActivityIntent);
//            finish();
        	onBackPressed();
            return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
}
