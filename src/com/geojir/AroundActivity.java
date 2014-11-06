package com.geojir;

import java.util.ArrayList;
import java.util.Map;

import android.location.Location;
import android.os.Bundle;

import com.geojir.db.ListMediaDb;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AroundActivity extends ParentMenuActivity implements GooglePlayServicesClient.ConnectionCallbacks, 
GooglePlayServicesClient.OnConnectionFailedListener
{
	private GoogleMap mMap;
	private ArrayList<Map<String, String>> values;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		Marker marker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(43.6109200, 3.8772300))
				.title(getString(R.string.geojir_here)).draggable(true));
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
		//

	}

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

	@Override
	public void onConnectionFailed(ConnectionResult arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected()
	{
		// TODO Auto-generated method stub
		
	}

}
