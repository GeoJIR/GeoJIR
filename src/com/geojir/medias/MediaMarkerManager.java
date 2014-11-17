package com.geojir.medias;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.geojir.Constants;
import com.geojir.R;
import com.geojir.view.CustomImageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback.EmptyCallback;
import com.squareup.picasso.Picasso;

public class MediaMarkerManager
{
	protected static HashMap<Marker, MediaMarkerManager> list = new HashMap<Marker, MediaMarkerManager>();
	protected Marker marker;
	protected String filePath = "";
	protected String comment = "";
	protected Boolean filter = false;
	protected LatLng latlng;
	protected Boolean firstLoad = true;
	
	public MediaMarkerManager(String path, LatLng position, String remark)
	{
		this(path, position, remark, false);
	}
	
	public MediaMarkerManager(String path, LatLng position, String remark, Boolean filterMonochrome)
	{
		filePath = path;
		latlng = position;
		comment = remark;
		filter = filterMonochrome;
	}
	
	public void addToMap(GoogleMap map)
	{
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude, longitude and title for the marker
		markerOptions.position(latlng).title(comment);
		
		// Marker color depend on media
		float markerColor = BitmapDescriptorFactory.HUE_RED;
		
		if (filePath.endsWith(Constants.EXT_AUDIO))
			markerColor = BitmapDescriptorFactory.HUE_VIOLET;
		
		markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor));
		
		// Create marker
		marker = map.addMarker(markerOptions);
		list.put(marker,  this);
	}
	
	public void remove()
	{
		if (marker != null)
		{
			list.remove(marker);
			marker.remove();
			marker = null;
		}
	}
	
	public static MediaMarkerManager getManagerFromMarker(Marker marker)
	{
		if (list.containsKey(marker))
			return list.get(marker);
		else
			return null;
	}

	public View getView(Context context)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// Getting view from the layout file info_window_layout
		final View popUp = inflater.inflate(R.layout.popup_gmap, null);
		
		TextView popUpTitle = (TextView) popUp.findViewById(R.id.remark_popup);
		final CustomImageView popUpImage = (CustomImageView) popUp.findViewById(R.id.image_popup);
		
		final TextView progressbarPopup = (TextView) popUp.findViewById(R.id.progressBarPopup);
		
		if (!comment.isEmpty())
			popUpTitle.setText(comment);
		else
			popUpTitle.setText("Aucun commentaire");
		
		final File photo_marker = new File(filePath);
		// Create callback for image load (b&w and refresh)
		EmptyCallback callback = new EmptyCallback()
		{	
			@Override
			public void onSuccess()
			{
				super.onSuccess();
				
				popUpImage.blackAndWhiteMode(filter);
				
				if (firstLoad)
				{
					firstLoad = false;
					if (marker.isInfoWindowShown())
						marker.hideInfoWindow();
					marker.showInfoWindow();
				}
				else
					firstLoad = true;
				
				if (progressbarPopup != null)
					progressbarPopup.setVisibility(View.GONE);
			}
			
			@Override
			public void onError()
			{
				super.onError();
				if (progressbarPopup != null)
				{
					progressbarPopup.setText("Load error");
				}
			}
		};
		
		// Load the image thumbnail for audio (not image)
		if (!photo_marker.getPath().endsWith(Constants.EXT_IMAGE))
			Picasso.with(context).load(R.drawable.ic_music)
				.resize(200, 100).centerInside()
				.into(popUpImage, callback);
		else
		{
			//Load with Picasso
			Picasso.with(context).load(photo_marker)
				.resize(200, 100).centerInside()
				.placeholder(R.drawable.loading)
				.into(popUpImage, callback);
		}
		
		return popUp;
	}
	
	public void windowsClick()
	{
		if (filePath != "")
			Media.launch(filePath, filter);
	}
}
