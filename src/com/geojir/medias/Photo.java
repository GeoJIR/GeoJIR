package com.geojir.medias;

import com.geojir.Constants;

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
}
