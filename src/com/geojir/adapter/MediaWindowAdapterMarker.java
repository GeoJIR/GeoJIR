package com.geojir.adapter;
import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.geojir.Constants;
import com.geojir.R;
import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.medias.MediaMarkerManager;
import com.geojir.view.CustomImageView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback.EmptyCallback;
import com.squareup.picasso.Picasso;

// customisation du marker
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
	
	// recupere les infos lorsque l'on clique sur le marker sur la map
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
	
	/**
	 * This method is called after the bitmap has been loaded. It checks if the
	 * currently displayed info window is the same info window which has been
	 * saved. If it is, then refresh the window to display the newly loaded
	 * image.
	 */
	/*
	 * private Callback onImageLoaded = new Callback() {
	 * 
	 * @Override public void execute(String result) { if
	 * (markerShowingInfoWindow != null &&
	 * markerShowingInfoWindow.isInfoWindowShown()) {
	 * markerShowingInfoWindow.hideInfoWindow();
	 * markerShowingInfoWindow.showInfoWindow(); } } };
	 */
	
}
