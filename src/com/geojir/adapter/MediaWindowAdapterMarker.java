package com.geojir.adapter;
import android.content.Context;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.geojir.medias.MediaMarkerManager;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

// Custom adapter for Google marker
public class MediaWindowAdapterMarker implements InfoWindowAdapter
{	
	protected Marker markerShowingInfoWindow;
	protected Context popupContext;
	protected SimpleCursorAdapter cursor;
	
	public MediaWindowAdapterMarker(Context context, SimpleCursorAdapter cursorAdapter)
	{
		popupContext = context;
		cursor = cursorAdapter;
		
	}
	
	// Custom markers' window's content 
	@Override
	public View getInfoContents(Marker marker)
	{
		MediaMarkerManager manager = MediaMarkerManager.getManagerFromMarker(marker);
		
		if (manager == null)
			return null;
		else
			return manager.getView(popupContext);
	}
	
	@Override
	public View getInfoWindow(Marker marker)
	{
		return null;
	}
}
