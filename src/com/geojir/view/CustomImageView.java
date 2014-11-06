package com.geojir.view;

import java.io.File;
import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.geojir.Constants;
import com.geojir.R;
import com.geojir.medias.Sound;

public class CustomImageView extends ImageView
{
	protected String fileMediaPath;
	protected Boolean isMonochrome = false;

	// Constructors required
	public CustomImageView(Context context)
	{
		super(context);
	}
	
	public CustomImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public CustomImageView(Context context, AttributeSet attrs,
			int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	// Constructor available until android-21 
	@TargetApi(Build.VERSION_CODES.L)
	public CustomImageView(Context context, AttributeSet attrs,
			int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}
	
	public void setImagePath(String path)
	{
		setImagePath(path, false);
	}

	public void setImagePath(String path, Boolean monochrome)
	{
		fileMediaPath = path;
		File file = new File(path);
		// display image if exist
		if (path.endsWith(Constants.EXT_IMAGE) && file.exists())
			this.loadFile();
		else if (path.endsWith(Constants.EXT_AUDIO) && file.exists())
			// else display default
			this.setImageResource(R.drawable.ic_music);
		else
			// else display default
			this.setImageResource(R.drawable.ic_medias);
		
		blackAndWhiteMode(monochrome);
	}
	
	// Display image file on this
	protected void loadFile()
	{
		// get icon's dimension
		final int thumbnailSize = getResources().getDimensionPixelOffset(R.dimen.thumbnailSize);
		
		// Get the dimensions of the View
		int targetW = this.getWidth();
		int targetH = this.getHeight();
		if (targetW < 1)
			targetW = thumbnailSize;
		if (targetH < 1)
			targetH = thumbnailSize;

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileMediaPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		
		// Load resized image
		Bitmap bitmap = BitmapFactory.decodeFile(fileMediaPath, bmOptions);
		this.setImageBitmap(bitmap);
	}
		
	// Enable/disable black and white filter
	public void blackAndWhiteMode(Boolean enable)
	{
		isMonochrome = enable;
		if (enable)
		{
			float[] colorMatrix =
			{ 
			     0.33f, 0.33f, 0.33f, 0, 0, //red
			     0.33f, 0.33f, 0.33f, 0, 0, //green
			     0.33f, 0.33f, 0.33f, 0, 0, //blue
			     0, 0, 0, 1, 0    			//alpha    
			};
		     
		    ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
		    this.setColorFilter(colorFilter);  
		}
		else
			this.clearColorFilter();
	}

	public void playMedia()
	{
		File file = new File(fileMediaPath);
		if (fileMediaPath.endsWith(Constants.EXT_AUDIO) && file.exists())
		{
			Sound sound = new Sound();
			sound.restore(fileMediaPath);
			try
			{
				sound.play();
			}
			catch (IllegalArgumentException | SecurityException
					| IllegalStateException | IOException e)
			{
				e.printStackTrace();
			}
		}
		else
			blackAndWhiteMode(!isMonochrome);
	}
}
