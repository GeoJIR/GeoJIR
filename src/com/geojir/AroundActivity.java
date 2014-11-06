package com.geojir;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AroundActivity extends ParentMenuActivity
{
	MapFragment mapFragment;
	GoogleMap map;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);
		
		mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		
		map = mapFragment.getMap();
		if (map != null)
		{
			map.addMarker(new MarkerOptions()
				.position(new LatLng(43.619273, 3.913608))
				.title("Hello world")
				.draggable(true)
			);
			
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					new LatLng(43.619273, 3.913608), 15);
			
			map.animateCamera(cameraUpdate);
		}
	}
}
