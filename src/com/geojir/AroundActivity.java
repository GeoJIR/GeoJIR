package com.geojir;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.geojir.db.ListMediaDb;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class AroundActivity extends ParentMenuActivity  {
	
	protected GoogleMap mMap;
	protected Location location;
	protected LatLng myLocation;
	protected CameraUpdate cameraUpdate;
	protected ArrayList<Map<String, String>> values;
	protected Marker marker;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		// on va trouver notre location pour pouvoir se centrer dessus
		mMap.setMyLocationEnabled(true);
		Location location = mMap.getMyLocation();

		LatLng myLocation = null;
		if (location != null)
		{
			myLocation = new LatLng(location.getLatitude(),
			location.getLongitude());
			
		
			//on ajuste la camera a notre position
			if (mMap != null)
			{
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				new LatLng(myLocation.latitude, myLocation.longitude), com.geojir.Constants.GM_DEFAULT_ZOOM);
				mMap.animateCamera(cameraUpdate);
				// Changed my mind, not draggable after all
				marker.setDraggable(false);
				// Properties are: Position (required), Anchor, Alpha, Title,
				// Snippet, Icon (use BitmapDescriptor), Draggable, Visible, Flat or
				// Billboard orientation, Rotation
		
				MarkerOptions markerOption = new MarkerOptions().position(new LatLng(
						marker.getPosition().latitude, marker.getPosition().longitude));
		
				// markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.currentlocation_icon));
		
				Marker currentMarker = mMap.addMarker(markerOption);
		
				// recuperation de donnée dans la BDD
				// database instantiate
				ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());
		
				// last X entries
				values = new ArrayList<Map<String, String>>();
		
				/*
				Observable.create(listeMedia).map(new Func1<Map<String, String>, Map<String, String>>()
						{
							@Override
							public Map<String, String> call(Map<String, String> item)
							{
								values.add(item);
								return item;
							}
						}).subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Action1<Map<String, String>>()
						{
							@Override
							public void call(Map<String, String> item)
							{
								markerMedia();
							}
						});
		
			 	*/
			}
			else
			{
				Toast.makeText(getApplicationContext(), R.string.GM_NoPosition, Toast.LENGTH_SHORT).show();

				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				new LatLng(com.geojir.Constants.GM_MPL_LATITUDE, com.geojir.Constants.GM_MPL_LONGITUDE), com.geojir.Constants.GM_DEFAULT_ZOOM);
				mMap.animateCamera(cameraUpdate);
			}
			//
		}
		else
		{
			Locale current = getResources().getConfiguration().locale;
			
			
			Toast.makeText(getApplicationContext(), R.string.GM_NotReached, Toast.LENGTH_SHORT).show();
		}



	}

	/**
	 * 
	 */
	public void markerMedia()
	{
		if (values != null)
		{
			int i = 1;
			for (Map<String, String> v : values)
			{
				// traitement de creation et d'ajout des marker
				Marker marker = mMap.addMarker(new MarkerOptions().position(
						new LatLng(43.6109200 + i, 3.8772300 + 1)).title(
						v.get("remark")));

				MarkerOptions markerOption = new MarkerOptions()
						.position(new LatLng(marker.getPosition().latitude,
								marker.getPosition().longitude));

				mMap.addMarker(markerOption);

				i++;
			}

		}
	}
}
