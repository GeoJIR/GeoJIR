package com.geojir.view;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.geojir.Constants;
import com.geojir.ParentMenuActivity;
import com.geojir.R;
import com.geojir.medias.Media;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * 
 * Extends of Image view for :
 * 		- Monochrome filter
 * 		- auto start Animate Drawable
 * 		- Load Picasso image depend on media given
 * 			(load image for image, load defaut icon for other)
 *
 */
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
	
	// Override for autostart animated drawable
	@Override
	public void setImageDrawable(Drawable drawable)
	{
		super.setImageDrawable(drawable);
		
		// If animatable, start it
		if (drawable != null && drawable instanceof Animatable)
			((Animatable) drawable).start();
		
		// If multiple layout drawable
		if (drawable != null && drawable instanceof LayerDrawable)
		{
			LayerDrawable layerDrawable = (LayerDrawable) drawable;
			// Check all child for animate it if needed
			for (int i=0; i<layerDrawable.getNumberOfLayers(); i++)
			{
				Drawable child = layerDrawable.getDrawable(i);
				if (child instanceof Animatable)
					((Animatable) child).start();
			}
		}
	}

	public void setImagePath(String path, Boolean monochrome)
	{
		fileMediaPath = path;
		File file = new File(fileMediaPath);
		
		// display image if exist
		if (path.endsWith(Constants.EXT_IMAGE) && file.exists())
			this.loadImageFile();
		else if (path.endsWith(Constants.EXT_AUDIO) && file.exists())
			// else display default
			this.setImageResource(R.drawable.ic_music);
		else
			// else display default
			this.setImageResource(R.drawable.ic_medias);
		
		blackAndWhiteMode(monochrome);
	}
	
	// Display image file on this
	protected void loadImageFile()
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
		
		String pathPicasso = fileMediaPath;
		// Check path format like *://*
		// matches need to be complete
//		if (!pathPicasso.matches("(.+?)(://)(.+?)"))
//		{
//			if (pathPicasso.startsWith("//"))
//				pathPicasso = "file:"+pathPicasso;
//			else if (pathPicasso.startsWith("/"))
//				pathPicasso = "file:/"+pathPicasso;
//			else
//				pathPicasso = "file://"+pathPicasso;
//		}
		
		RequestCreator request = Picasso.with(ParentMenuActivity.CONTEXT).load(new File(pathPicasso));
		if (!useCache())
			request.skipMemoryCache();
		
		request.resize(targetW, targetH)
			.centerInside()
			.placeholder(R.drawable.loading)
			.into(this);
	}
	
	protected Boolean useCache()
	{
		return true;
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
		if (fileMediaPath != "")
			Media.launch(fileMediaPath, isMonochrome);
	}
}
