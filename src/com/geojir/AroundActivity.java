package com.geojir;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Bundle;


public class AroundActivity extends ParentMenuActivity  {
	
	GoogleMap mMap;
    Location location;
    LatLng myLocation;
    CameraUpdate cameraUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);
		
		// on va trouver notre location pour pouvoir se centrer dessus
		mMap.setMyLocationEnabled(true);
		Location location = mMap.getMyLocation();
		LatLng myLocation = null;
		if (location != null)
		{
			myLocation = new LatLng(location.getLatitude(),
			location.getLongitude());
		}

		//on ajuste la camera a notre position
		if (mMap != null)
		{
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
			new LatLng(43.6109200, 3.8772300), 10);
			mMap.animateCamera(cameraUpdate);
		}
	}
}
