package com.geojir;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;

import com.geojir.view.CustomImageView;
import com.squareup.picasso.Picasso;

public class PopupImageViewActivity extends Activity
{
	protected String filePath = "";
	protected Boolean filter = false;
	protected CustomImageView imageView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popup_image);
		
		// Get imageView
		imageView = (CustomImageView) findViewById(R.id.popupImageView);
		
		// Save file path
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		filePath = extras.getString("imagePath");
		// Save filter Monochrome
		filter = extras.getBoolean("imageFilter", false);
		
		// Restore path if needed
		restoreState(savedInstanceState);
		
		// Load image
		load();
	}
	
	// Close activity on click, slide, etc...
	@Override
	public boolean dispatchTouchEvent(MotionEvent e)
	{
	    finish();
	    return super.dispatchTouchEvent(e);
	}
	
	// Restore medias
	protected void restoreState(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
			filePath = savedInstanceState.getString("imagePath");
	}
	
	// Check path format like *://*
	protected void checkFilePath()
	{
		// mathces need to be complete
		if (!filePath.matches("(.+?)(://)(.+?)"))
		{
			if (filePath.startsWith("//"))
				filePath = "file:"+filePath;
			else if (filePath.startsWith("/"))
				filePath = "file:/"+filePath;
			else
				filePath = "file://"+filePath;
		}
	}
	
	protected void load()
	{
		checkFilePath();
		// Get screen size
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);
		
		// Apply filter
		imageView.blackAndWhiteMode(filter);
		
		// If orientation landscape, reverse size
		// Strange, needed to be verified on other phone
		if (getResources().getConfiguration().orientation % 2 == 0)
			size = new Point(size.y, size.x);
		
		// Load image with Picasso
		Picasso.with(this).load(filePath)
			// Resize on screen size with margin
			.resize(size.x - 60, size.y - 60)
			.centerInside()
			.placeholder(R.drawable.loading)
			.into(imageView);
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		// Save the current image path
		savedInstanceState.putString("imagePath", filePath);
	}
}
