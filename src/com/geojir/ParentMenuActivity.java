package com.geojir;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
//import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class ParentMenuActivity extends Activity
{
	private DrawerLayout drawer_layout;
	private ViewGroup lateral_menu_left;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_parent_menu);

		drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);

		lateral_menu_left = (LinearLayout) findViewById(R.id.lateral_menu_left);

		final ParentMenuActivity this_temp = this;
		
		for (int index = 0; index < lateral_menu_left.getChildCount(); index++)
		{
			View button = lateral_menu_left.getChildAt(index);
			button.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					this_temp.clickOnMenu(v);
				}
			});
		}
	}

	protected void clickOnMenu(View v)
	{
		drawer_layout.closeDrawer(GravityCompat.START);

		Class<? extends ParentMenuActivity> startActivity = this.getClass();
		Class<? extends ParentMenuActivity> endActivity = this.getClass();

		switch (v.getId())
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
			case R.id.drawable_account:
				endActivity = AccountActivity.class;
				break;
		}

		if (startActivity != endActivity)
		{
			Intent intent = new Intent(this, endActivity);
			startActivity(intent);
		}
	}

	@Override
	public void setContentView(int activity)
	{
		FrameLayout item = (FrameLayout) findViewById(R.id.content_frame);
		getLayoutInflater().inflate(activity, item);
	}

	/*
	public void setContentView(int activity, Boolean original)
	{
		super.setContentView(activity);
	}

	public void setContentView(int activity, int activity_menu)
	{
		this.setContentView(activity);

		ViewGroup parent = (ViewGroup) lateral_menu_left.getParent();
		View lateral_temp = getLayoutInflater().inflate(activity_menu, parent, false);
		
		if (!(lateral_temp instanceof ViewGroup))
			return;
		
		int index = parent.indexOfChild(lateral_menu_left);
		parent.removeView(lateral_menu_left);
		lateral_menu_left = (ViewGroup) lateral_temp;
		parent.addView(lateral_menu_left, index);
	}
	*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == android.R.id.home)
		{
			if (drawer_layout.isDrawerOpen(GravityCompat.START))
				drawer_layout.closeDrawer(GravityCompat.START);
			else
				drawer_layout.openDrawer(GravityCompat.START);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
