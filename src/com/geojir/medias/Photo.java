package com.geojir.medias;

import android.content.Intent;

import com.geojir.Constants;
import com.geojir.ParentMenuActivity;
import com.geojir.PopupImageViewActivity;

public class Photo extends Media
{
	public String getExt()
	{
		return Constants.EXT_IMAGE;
	}
	
	public String getType()
	{
		return Constants.TYPE_IMAGE;
	}
	
	public String getDir()
	{
		return Constants.PATH_IMAGE;
	}
	
	public void display()
	{
		display(false);
	}

	public void display(Boolean filterMonochrome)
	{
		if (file == null)
			return;
		
		Intent intent = new Intent(ParentMenuActivity.CONTEXT, PopupImageViewActivity.class);  
		intent.putExtra("imagePath", file.getPath());  
		intent.putExtra("imageFilter", filterMonochrome);  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
		ParentMenuActivity.CONTEXT.startActivity(intent);
	}
}
