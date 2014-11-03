package com.geojir;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
//import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.geojir.medias.Media;

public class ParentMenuActivity extends Activity
{
	// Context memory for use in Medias class
	public static Context CONTEXT;
	public static Boolean firstLaunch = true;
	
	//@InjectView(R.id.drawer_layout)
	DrawerLayout drawerLayout;
	//@InjectView(R.id.lateral_menu_left)
	ViewGroup lateralMenuLeft;
	//@InjectView(R.id.content_frame)
	FrameLayout drawerContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// load layout drawer
		super.setContentView(R.layout.activity_parent_menu);
		
		// Save Context for Media class
		CONTEXT = getApplicationContext();
		// Delete temp file if exists on firstLaunch
		if (firstLaunch)
		{
			try
			{
				Media.deleteTempFile();
			}
			catch (InstantiationException | IllegalAccessException e) {}
			
			firstLaunch = false;
		}
		
		// Manual ButterKnife injections for skip child injections
		//ButterKnife.inject(this);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		lateralMenuLeft = (ViewGroup) findViewById(R.id.lateral_menu_left);
		drawerContent = (FrameLayout) findViewById(R.id.content_frame);
		
		// Active actionBar
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		
		// Create listener for all menu item
		for (int index = 0; index < lateralMenuLeft.getChildCount(); index++)
		{
			View item = lateralMenuLeft.getChildAt(index);
			item.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					clickOnMenu(v);
				}
			});
		}
	}
	
	// Function call after an item click
	protected void clickOnMenu(View item)
	{
		drawerLayout.closeDrawer(GravityCompat.START);

		Class<? extends ParentMenuActivity> startActivity = this.getClass();
		Class<? extends ParentMenuActivity> endActivity = this.getClass();
		
		// Switch on item
		switch (item.getId())
		{
			case R.id.drawable_capture:
				endActivity = CaptureActivity.class;
				break;
			case R.id.drawable_around:
				endActivity = AroundActivity.class;
				break;
			case R.id.drawable_follow:
				endActivity = FollowActivity.class;
				break;
			case R.id.drawable_historic:
				endActivity = ListMediaActivity.class;
				break;
			case R.id.drawable_account:
				endActivity = AccountActivity.class;
				break;
		}
		
		// Change activity only if different
		if (startActivity != endActivity)
		{
			Intent intent = new Intent(this, endActivity);
			startActivity(intent);
		}
	}

	@Override
	// setContent activity in content Frame
	public void setContentView(int activity)
	{
		getLayoutInflater().inflate(activity, drawerContent);
	}
	
	// force initial setContent 
	public void setContentView(int activity, Boolean original)
	{
		if (original)
			super.setContentView(activity);
		else
			setContentView(activity);
	}
	
	// SetContent and menu layout
	public void setContentView(int activity, int activity_menu)
	{
		this.setContentView(activity);
		
		// Inflate new menu
		ViewGroup parent = (ViewGroup) lateralMenuLeft.getParent();
		View lateral_temp = getLayoutInflater().inflate(activity_menu, parent, false);
		
		// Stop method if inflate fail
		if (!(lateral_temp instanceof ViewGroup))
			return;
		
		// Suppress old menu
		int index = parent.indexOfChild(lateralMenuLeft);
		parent.removeView(lateralMenuLeft);
		// Add new menu
		lateralMenuLeft = (ViewGroup) lateral_temp;
		parent.addView(lateralMenuLeft, index);
	}

	@Override
	// Open/Close drawer on click home
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == android.R.id.home)
		{
			if (drawerLayout.isDrawerOpen(GravityCompat.START))
				drawerLayout.closeDrawer(GravityCompat.START);
			else
				drawerLayout.openDrawer(GravityCompat.START);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
