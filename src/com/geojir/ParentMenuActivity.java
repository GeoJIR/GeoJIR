package com.geojir;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class ParentMenuActivity extends Activity {
	
	private View lateral_menu;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_parent_menu);
    	
        lateral_menu = (View) findViewById(R.id.lateral_menu_left);
   		//lateral_menu.setVisibility(View.INVISIBLE);
        
   		ActionBar actionBar = getActionBar();
   		
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);
    }
    
    @Override
	public void setContentView(int activity) {
        
    	FrameLayout item = (FrameLayout) findViewById(R.id.content_frame);
    	View child = getLayoutInflater().inflate(activity, item);
    	//item.addView(child);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home)
		{
			Log.v("test", "click sur home");
	    	if (lateral_menu.getVisibility() == View.VISIBLE)
	    		lateral_menu.setVisibility(View.INVISIBLE);
	    	else
	    		lateral_menu.setVisibility(View.VISIBLE);
			
	    	return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
