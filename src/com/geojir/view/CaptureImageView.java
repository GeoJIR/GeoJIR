package com.geojir.view;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.geojir.Constants;
import com.geojir.R;
import com.geojir.medias.Photo;

public class CaptureImageView extends ImageView
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
		// Get the dimensions of the View
		int targetW = this.getWidth();
		int targetH = this.getHeight();
		if (targetW < 1)
			targetW = 1;
		if (targetH < 1)
			targetH = 1;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file.getPath(), bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW * 3, photoH / targetH * 3);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		
		// Load resized image
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), bmOptions);
		this.setImageBitmap(bitmap);
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
	
	// Enable/disable black and white filter
	public void blackAndWhiteMode(Boolean enable)
	{
		if (enable)
		{
			float[] colorMatrix =
			{ 
			     0.33f, 0.33f, 0.33f, 0, 0, //red
			     0.33f, 0.33f, 0.33f, 0, 0, //green
			     0.33f, 0.33f, 0.33f, 0, 0, //blue
			     0, 0, 0, 1, 0    //alpha    
			};
		     
		    ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
		    this.setColorFilter(colorFilter);  
		}
		else
			this.clearColorFilter();
	}
}
