package com.geojir;

import java.util.ArrayList;
import java.util.Map;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AroundActivity extends ParentMenuActivity
{

	protected GoogleMap mMap;
	protected Location location;
	protected CameraUpdate cameraUpdate;
	protected ArrayList<Map<String, String>> values;
	protected Marker markerLocation;
	protected boolean setFirstMarker = false;
	protected MapView mGoogleMapView;
    protected ArrayList<LatLng> pointsList = new ArrayList<LatLng>();

	protected final static String ZOOM_ON_RESTORE = "zoomOnRestore";
	protected final static String TILT_ON_RESTORE = "tiltOnRestore";
	protected final static String BEARING_ON_RESTORE = "bearingOnRestore";
	protected final static String LATITUDE_ON_RESTORE = "latitudenOnRestore";
	protected final static String LONGITUDE_ON_RESTORE = "longitudenOnRestore";
	
	protected float currentZoom = com.geojir.Constants.GM_DEFAULT_ZOOM;
	protected float currentTilt;
	protected float currentBearing;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);

		restoreState(savedInstanceState);
		
//		markerLocation.setIcon(icon);	//set a particular icon location (color ?? else)
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMyLocationEnabled(true);

		if (mMap != null)
		{
			mMap.setOnMyLocationChangeListener(myLocationChangeListener);

			/*
			 * // recuperation de donnée dans la BDD // database instantiate
			 * ListMediaDb listeMedia = new
			 * ListMediaDb(getApplicationContext());
			 * 
			 * // last X entries values = new ArrayList<Map<String, String>>();
			 * Observable.create(listeMedia).map(new Func1<Map<String, String>,
			 * Map<String, String>>() {
			 * 
			 * @Override public Map<String, String> call(Map<String, String>
			 * item) { values.add(item); return item; }
			 * }).subscribeOn(Schedulers.io())
			 * .observeOn(AndroidSchedulers.mainThread()) .subscribe(new
			 * Action1<Map<String, String>>() {
			 * 
			 * @Override public void call(Map<String, String> item) {
			 * //markerMedia(); } });
			 */
		} 
		else
		{
			Toast.makeText(getApplicationContext(), R.string.GM_NotReached,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 */
	 public void markerMedia() { 
		 for (int i=1; i<10; i++) { // traitement de creation et d'ajout des marker 
			 LatLng point = new LatLng(43.6109200 + i/4, 3.8772300 + i/4);
			 drawMarker(point, String.valueOf(i));
	        //add point in pointsList
	        pointsList.add(point);
		 }
	 } 
	 

    // Draw a marker at the "point"
    private void drawMarker(LatLng point, String remark){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
 
        // Setting latitude and longitude for the marker
        markerOptions.position(point);
 
        // Setting a title for this marker
        markerOptions.title(remark);
 
        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
 
    }
	    
	private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener()
	{
		@Override
		public void onMyLocationChange(Location location)
		{
			LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());

			if (myLocation != null)
			{
				//delete last marker and memorize the level of the zoom
				if (setFirstMarker == true)
				{
					markerLocation.remove();
			        if (mMap.getCameraPosition().zoom != currentZoom)
			        {
			        	// get zoom level
			        	currentZoom = mMap.getCameraPosition().zoom;  
			        }
				} 
				else
				{
					setFirstMarker = true;
				}
				markerLocation = mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

				if (mMap != null)
				{
					if(currentZoom == com.geojir.Constants.GM_DEFAULT_ZOOM)
					{
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, currentZoom));
						markerMedia();
					}
			        else
			        {
						CameraPosition myCameraPosition = new CameraPosition(myLocation, currentZoom, currentTilt, currentBearing);
			 		    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
						mMap.moveCamera(CameraUpdateFactory.newCameraPosition(myCameraPosition));
			        }
				}
				else
				{
					Toast.makeText(getApplicationContext(), R.string.GM_NotReached, Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				// TODO : code mort ?????
				Toast.makeText(getApplicationContext(), R.string.GM_NoLocation,	Toast.LENGTH_SHORT).show();

			}

		}
	};
	
	protected void restoreState(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			// Restore Map camera position
			currentZoom = Float.parseFloat(savedInstanceState.getString(ZOOM_ON_RESTORE));
			currentTilt = Float.parseFloat(savedInstanceState.getString(TILT_ON_RESTORE));
			currentBearing = Float.parseFloat(savedInstanceState.getString(BEARING_ON_RESTORE));
			
			double latitudeRestore = Double.parseDouble(savedInstanceState.getString(LATITUDE_ON_RESTORE));
			double longitudeRestore = Double.parseDouble(savedInstanceState.getString(LONGITUDE_ON_RESTORE));
			LatLng myLocation = new LatLng(latitudeRestore, longitudeRestore);
			
			CameraPosition myCameraPosition = new CameraPosition(myLocation, currentZoom, currentTilt, currentBearing);
 		    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(myCameraPosition));
			
            //Restore markers
			if(savedInstanceState.containsKey("points")){
                pointsList = savedInstanceState.getParcelableArrayList("points");
                if(pointsList!=null){
                    for(int i=0;i<pointsList.size();i++){
                        drawMarker(pointsList.get(i), String.valueOf(i));
                    }
                }
            }

		}
		else
		{
			Toast.makeText(getApplicationContext(), R.string.GM_WaitLocation, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		//save camera position
		CameraPosition myCameraPosition = mMap.getCameraPosition();
		
		savedInstanceState.putString(ZOOM_ON_RESTORE, String.valueOf(myCameraPosition.zoom));
		savedInstanceState.putString(TILT_ON_RESTORE, String.valueOf(myCameraPosition.tilt));
		savedInstanceState.putString(BEARING_ON_RESTORE, String.valueOf(myCameraPosition.bearing));
		savedInstanceState.putString(LATITUDE_ON_RESTORE, String.valueOf(myCameraPosition.target.latitude));
		savedInstanceState.putString(LONGITUDE_ON_RESTORE, String.valueOf(myCameraPosition.target.longitude));
		
		//save markers list
		savedInstanceState.putParcelableArrayList("points", pointsList);
	}
}
