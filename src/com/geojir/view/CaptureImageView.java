package com.geojir.view;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;

import com.geojir.Constants;
import com.geojir.R;
import com.geojir.medias.Photo;

public class CaptureImageView extends CustomImageView
{
	// Constructors required
	public CaptureImageView(Context context)
	{
		super(context);
		onClickEvent();
	}
	
	public CaptureImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		onClickEvent();
	}

	public CaptureImageView(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		onClickEvent();
	}

	// Constructor available until android-21 
	@TargetApi(Build.VERSION_CODES.L)
	public CaptureImageView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		onClickEvent();
	}
	
	// Configure click event
	public void onClickEvent()
	{
		// Create final variable to use in sub-function
		final Activity finalActivity = (Activity) getContext();
		
		this.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// Create camera intent
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

				if (cameraIntent.resolveActivity(finalActivity.getPackageManager()) != null)
				{
					try
					{
						// Create photo and path where save the bitmap
						Photo photo_temp = new Photo();
						cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
								Uri.fromFile(new File(photo_temp.getPath())));
						// Wait activity receive result
						finalActivity.startActivityForResult(cameraIntent, Constants.REQUEST_TAKE_PHOTO);
					}
					catch (Exception e)
					{
						// Error occurred while creating the File
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	// Display image file on this
	private void display(File file)
	{
		setImagePath(file.getPath());
	}
	
	// load temporary image
	public void load()
	{
		load (new Photo().getPath());
	}
	
	// load image with path
	public void load(String photoRestoreURI)
	{
		File file = new File(photoRestoreURI);
		// Load and display file image if exist
		if (file.exists())
		{
			if (file.getPath() == new Photo().getPath())
				file.deleteOnExit();
			display(file);
		}
		// Display default icon otherwise
		else
			this.setImageResource(R.drawable.ic_medias);
	}
}
