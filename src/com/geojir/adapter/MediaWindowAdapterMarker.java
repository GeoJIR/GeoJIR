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
	
	// map of cached image
	// use to refresh marker info windows when image loaded
	protected HashMap<String, Boolean> cachedImage = new HashMap<String, Boolean>();
		
	public MediaWindowAdapterMarker(Context context, SimpleCursorAdapter cursorAdapter)
	{
		popupContext = context;
		cursor = cursorAdapter;
		
	}
	
	// recupere les infos lorsque l'on clique sur le marker sur la map
	@Override
	public View getInfoContents(Marker marker)
	{
		markerShowingInfoWindow = marker;
		
		LayoutInflater inflater = (LayoutInflater) popupContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// Getting view from the layout file info_window_layout
		final View popUp = inflater.inflate(R.layout.popup_gmap, null);
		
		
		if (cursor != null && !cursor.isEmpty()
				&& marker.getSnippet() != null && marker.getSnippet() != "")
		{
			if (cursor.getCursor() != null)
			{
				final Cursor cur = cursor.getCursor();
				
				int position = Integer.valueOf(marker.getSnippet());
				cur.moveToFirst();
				cur.moveToPosition(position - 1);
				
				TextView popUpTitle = (TextView) popUp
						.findViewById(R.id.remark_popup);
				final CustomImageView popUpImage = (CustomImageView) popUp
						.findViewById(R.id.image_popup);
				
				final TextView progressbar_popup = (TextView) popUp
						.findViewById(R.id.progressBarPopup);
				
				int remark_index = cur.getColumnIndex(MediasDb.REMARK_COLUMN);
				String remark = cur.getString(remark_index);
				
				if (remark != null && !remark.isEmpty())
					popUpTitle.setText(remark);
				else
					popUpTitle.setText("Aucun commentaire");
				
				int path_index = cur.getColumnIndex(MediasDb.FILE_NAME_COLUMN);
				String path = cur.getString(path_index);
				final File photo_marker = new File(path);
				
				EmptyCallback callback = new EmptyCallback() {
					
					@Override
					public void onSuccess()
					{
						super.onSuccess();
						
						if (cachedImage.get(photo_marker.getPath()) == null
								|| !cachedImage.get(photo_marker
										.getPath()))
						{
							cachedImage.put(photo_marker.getPath(),
									true);
							markerShowingInfoWindow
									.hideInfoWindow();
							markerShowingInfoWindow
									.showInfoWindow();
						}
						else
							cachedImage.put(photo_marker.getPath(),
									false);
						
						if (progressbar_popup != null)
						{
							progressbar_popup
									.setVisibility(View.GONE);
						}
						
						int filter_index = cur.getColumnIndex(MediasDb.FILTER_COLUMN); int filter =
						cur.getInt(filter_index);
								
						if (filter == 1) { popUpImage.blackAndWhiteMode(true); }
					}
					
					@Override
					public void onError()
					{
						super.onError();
						if (progressbar_popup != null)
						{
							progressbar_popup
									.setVisibility(View.GONE);
						}
					}
				};

				
				// Load the image thumbnail
				if (path != null && !path.isEmpty())
				{
					if (photo_marker.getPath().endsWith(Constants.EXT_IMAGE))
						Picasso.with(popupContext).load(photo_marker)
							.resize(200, 100).centerInside()
							.into(popUpImage, callback);
					else
						Picasso.with(popupContext).load(R.drawable.ic_music)
							.resize(200, 100).centerInside()
							.into(popUpImage, callback);
				}
			}
			
		}
		else
			return null;
		
		// Returning the view containing InfoWindow contents
		return popUp;
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
