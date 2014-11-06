package com.geojir;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AroundActivity extends ParentMenuActivity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	
	GoogleMap mMap;

    LocationClient mLocationClient;
    
    LocationListener mLocationListener;
    
    LocationRequest mLocationRequest;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);
		
	    if (mMap == null) {
	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        // Check if we were successful in obtaining the map.
	        if (mMap != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	        	mMap.setPadding(10, 10, 10, 10);

	            mMap.addMarker(new MarkerOptions().position(new LatLng(43.600, 3.883)).title("Ici"));
	            mMap.addMarker(new MarkerOptions().position(new LatLng(43.8961, 3.7369)).title("Là"));
	            
				UiSettings settings = mMap.getUiSettings();
				
				mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(43.8961, 3.7369), 13.5f, 30f, 112.5f))); // zoom, tilt, bearing
				mMap.setTrafficEnabled(true);
				settings.setAllGesturesEnabled(true);
				settings.setCompassEnabled(true);
				settings.setMyLocationButtonEnabled(true);
				settings.setRotateGesturesEnabled(true);
				settings.setScrollGesturesEnabled(true);
				settings.setTiltGesturesEnabled(true);
				settings.setZoomControlsEnabled(true);
				settings.setZoomGesturesEnabled(true);
	        }
	    } 
	    mLocationClient = new LocationClient(this, this, this);
	}

	 /* Called when the Activity becomes visible.    */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
//        mLocationRequest.PRIORITY_HIGH_ACCURACY;
        mLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener);
    }
    
    /* Called when the Activity is no longer visible.  */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
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
