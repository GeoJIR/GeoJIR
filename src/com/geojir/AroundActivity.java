package com.geojir;

import java.util.ArrayList;
import java.util.Map;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.geojir.adapter.MediaWindowAdapterMarker;
import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.db.MediaContentProvider;
import com.geojir.medias.MediaMarkerManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AroundActivity extends ParentMenuActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LoaderCallbacks<Cursor>
{
	// Localization
	LocationRequest locationRequest;
	LocationListener locationListener;
	LocationClient mLocationClient;
	
	// Map
	protected GoogleMap map;
	protected Location mylastlocation = null;
	protected CameraUpdate cameraUpdate;
	protected ArrayList<Map<String, String>> values;
	protected Marker markerLocation;
	protected boolean setFirstMarker = false;
	protected MapView mGoogleMapView;
	
	protected ArrayList<MediaMarkerManager> markerList = new ArrayList<MediaMarkerManager>();
	protected SimpleCursorAdapter cursorAdapter;
	
	TextView progressbar_popup;
	
	protected final static String ZOOM_ON_RESTORE = "zoomOnRestore";
	protected final static String TILT_ON_RESTORE = "tiltOnRestore";
	protected final static String BEARING_ON_RESTORE = "bearingOnRestore";
	protected final static String LATITUDE_ON_RESTORE = "latitudenOnRestore";
	protected final static String LONGITUDE_ON_RESTORE = "longitudenOnRestore";
	
	protected float currentZoom = com.geojir.Constants.GM_DEFAULT_ZOOM;
	protected float currentTilt;
	protected float currentBearing;
	
	private boolean firstZoom = false;
	
	// shared preferences
	protected SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);
		
		// test if Google Play Services is accessible
		boolean testPlayServices = testPlayServices();
		
		if (testPlayServices == true)
		{
			
			cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
					null, new String[] { MediasDb.FILE_NAME_COLUMN,
							MediasDb.REMARK_COLUMN, MediasDb.FILTER_COLUMN },
					new int[] { R.id.imageIcon, R.id.remark }, 0);
			
			// create content provider
			getLoaderManager().initLoader(0, null, this);
			// displayContentProvider();
			
			restoreState(savedInstanceState);
			
			// initialize the location manager
			locationRequest = LocationRequest.create();
			// Use highest accuracy
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			
			// Set the update interval in ms
			locationRequest.setInterval(Constants.GM_UPDATE_INTERVAL);
			// Set the fastest update interval in ms
			locationRequest.setFastestInterval(Constants.GM_FASTEST_INTERVAL);
			// Set the smallest displacement
			locationRequest
					.setSmallestDisplacement(Constants.GM_DEFAULT_DISTANCE);
			
			locationListener = new LocationListener() {
				@Override
				public void onLocationChanged(Location location)
				{
					LatLng myLocation = new LatLng(location.getLatitude(),
							location.getLongitude());
					
					if (map != null)
					{
						if (map.getCameraPosition().zoom != currentZoom)
							// get zoom level
							currentZoom = map.getCameraPosition().zoom;
						
						// Create marker of user position
						createMarkerPosition(myLocation);
						// Center on position
						zoomIfNotYet();
					} else
						toast(R.string.GM_NotReached);
					
					// save (new) location in mylastlocation variable
					mylastlocation = location;
				}
			};
			mLocationClient = new LocationClient(this, this, this);
			
			initMap();
		}
	}
	
	protected boolean testPlayServices()
	{
		int checkGooglePlayServices = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		
		if (checkGooglePlayServices != ConnectionResult.SUCCESS)
		{
			// google play services is missing!!!!
			/*
			 * Returns status code indicating whether there was an error. Can be
			 * one of following in ConnectionResult: SUCCESS, SERVICE_MISSING,
			 * SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED,
			 * SERVICE_INVALID.
			 */
			GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
					this, 1122).show();
			
			toast(R.string.GM_Google_Play_Services_not_installed);
			
			return false;
		}
		return true;
	}
	
	protected void createMarkerPosition(LatLng position)
	{
		if (markerLocation != null)
			markerLocation.remove();
		
		markerLocation = map.addMarker(new MarkerOptions().position(position)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		
	}
	
	protected void zoomIfNotYet()
	{
		if (markerLocation != null && firstZoom == false)
		{
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(
					markerLocation.getPosition(), currentZoom));
			firstZoom = true;
		}
	}
	
	protected void onResume()
	{
		super.onResume();
		
		if (map == null)
			initMap();
	}
	
	protected void clearMarker()
	{
		// Remove all marker
		for (int i = 0; i < markerList.size(); i++)
			markerList.get(i).remove();
		
		// Clear list
		markerList = new ArrayList<MediaMarkerManager>();
	}
	
	/**
	 * 
	 */
	public void createMarkerMedia()
	{
		if (map == null)
			return;
		
		if (!markerList.isEmpty())
			clearMarker();
		
		Cursor cur = cursorAdapter.getCursor();
		if (cur.getCount() != 0)
		{
			cur.moveToFirst();
			do
			{
				// Get path file
				String path = cur.getString(cur
						.getColumnIndex(MediasDb.FILE_NAME_COLUMN));
				// Get position
				float lat = cur.getFloat(cur
						.getColumnIndex(MediasDb.LATITUDE_COLUMN));
				float lng = cur.getFloat(cur
						.getColumnIndex(MediasDb.LONGITUDE_COLUMN));
				LatLng position = new LatLng(lat, lng);
				
				// Get comment
				String remark = cur.getString(cur
						.getColumnIndex(MediasDb.REMARK_COLUMN));
				// Get monochrome filter
				Boolean filter = cur.getInt(cur
						.getColumnIndex(MediasDb.FILTER_COLUMN)) == 1;
				
				MediaMarkerManager mediaMarker = new MediaMarkerManager(path,
						position, remark, filter);
				mediaMarker.addToMap(map);
				markerList.add(mediaMarker);
			} while (cur.moveToNext() != false);
			
		}
		
	}
	
	private void initMap()
	{
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setMyLocationEnabled(true);
		
		// Custom Markers' Window's layout
		map.setInfoWindowAdapter(new MediaWindowAdapterMarker(
				getApplicationContext(), cursorAdapter));
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			public void onInfoWindowClick(Marker marker)
			{
				MediaMarkerManager manager = MediaMarkerManager
						.getManagerFromMarker(marker);
				if (manager != null)
					manager.windowsClick();
			}
		});
		
		// Zoom on position if it's possible
		zoomIfNotYet();
	}
	
	protected void restoreState(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			// Restore Map camera position
			currentZoom = Float.parseFloat(savedInstanceState
					.getString(ZOOM_ON_RESTORE));
			currentTilt = Float.parseFloat(savedInstanceState
					.getString(TILT_ON_RESTORE));
			currentBearing = Float.parseFloat(savedInstanceState
					.getString(BEARING_ON_RESTORE));
			double latitudeRestore = Double.parseDouble(savedInstanceState
					.getString(LATITUDE_ON_RESTORE));
			double longitudeRestore = Double.parseDouble(savedInstanceState
					.getString(LONGITUDE_ON_RESTORE));
			LatLng myLocation = new LatLng(latitudeRestore, longitudeRestore);
			CameraPosition myCameraPosition = new CameraPosition(myLocation,
					currentZoom, currentTilt, currentBearing);
			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.moveCamera(CameraUpdateFactory
					.newCameraPosition(myCameraPosition));
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		// save camera position
		CameraPosition myCameraPosition = map.getCameraPosition();
		savedInstanceState.putString(ZOOM_ON_RESTORE,
				String.valueOf(myCameraPosition.zoom));
		savedInstanceState.putString(TILT_ON_RESTORE,
				String.valueOf(myCameraPosition.tilt));
		savedInstanceState.putString(BEARING_ON_RESTORE,
				String.valueOf(myCameraPosition.bearing));
		savedInstanceState.putString(LATITUDE_ON_RESTORE,
				String.valueOf(myCameraPosition.target.latitude));
		savedInstanceState.putString(LONGITUDE_ON_RESTORE,
				String.valueOf(myCameraPosition.target.longitude));
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0)
	{
	}
	
	@Override
	public void onConnected(Bundle arg0)
	{
		mLocationClient.requestLocationUpdates(locationRequest,
				locationListener);
	}
	
	@Override
	public void onDisconnected()
	{
	}
	
	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}
	
	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop()
	{
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args)
	{
		Uri CONTENT_URI = MediaContentProvider.CONTENT_URI;
		
		String columns[] = new String[] { MediasDb._ID,
				MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN,
				MediasDb.FILTER_COLUMN, MediasDb.LATITUDE_COLUMN,
				MediasDb.LONGITUDE_COLUMN };
		
		return new CursorLoader(this, CONTENT_URI, columns, null, null, null);
		
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	{
		this.cursorAdapter.swapCursor(data);
		// Create media marker
		createMarkerMedia();
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader)
	{
		// If the Cursor is being placed in a CursorAdapter, you should use the
		// swapCursor(null) method to remove any references it has to the
		// Loader's data.
		this.cursorAdapter.swapCursor(null);
	}
}
