package com.geojir;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.db.ListMediaDb;
import com.geojir.db.MediaContentProvider;
import com.geojir.medias.Callback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

public class AroundActivity extends ParentMenuActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener
{
	// Localization
	LocationRequest locationRequest;
	LocationListener locationListener;
	LocationClient mLocationClient;
	
	// Map
	protected GoogleMap mMap;
	protected Location mylastlocation = null;;
	protected CameraUpdate cameraUpdate;
	protected ArrayList<Map<String, String>> values;
	protected Marker markerLocation;
	protected boolean setFirstMarker = false;
	protected MapView mGoogleMapView;

	protected ArrayList<LatLng> pointsList = new ArrayList<LatLng>();
	protected SimpleCursorAdapter cursorAdapter;

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
		
		restoreState(savedInstanceState);
		
		// initialize the location manager
		locationRequest = LocationRequest.create();
		// Use highest accuracy
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		
		// Set the update interval in ms
		locationRequest.setInterval(Constants.GM_UPDATE_INTERVAL);
		// Set the fastest update interval in ms
		locationRequest.setFastestInterval(Constants.GM_FASTEST_INTERVAL);
		
		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location)
			{
				LatLng myLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				
				// Pour les tests
				// String mLatAndLongStr = String.format("Lat:%.2f - Long:%.2f",
				// myLocation.latitude,myLocation.longitude);
				// Toast.makeText(AroundActivity.this, "Location update: " +
				// mLatAndLongStr, Toast.LENGTH_LONG).show();
				
				// markerLocation not null => marker has been already placed
				if (markerLocation != null && mMap != null)
				{
					if (mMap.getCameraPosition().zoom != currentZoom)
					{
						// get zoom level
						currentZoom = mMap.getCameraPosition().zoom;
					}
					
					// if difference between new location and last location is
					// less than GM_DEFAULT_DISTANCE meters
					float distance = mylastlocation.distanceTo(location);
					if (mylastlocation != null
							&& distance > Constants.GM_DEFAULT_DISTANCE)
					{
						markerLocation.remove();
						markerLocation = mMap
								.addMarker(new MarkerOptions()
										.position(myLocation)
										.icon(BitmapDescriptorFactory
												.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
						// we move more than X meters => so we update static
						// variables
						Constants.GM_LATITUDE = (float) location.getLatitude();
						Constants.GM_LONGITUDE = (float) location
								.getLongitude();
						
					}
				} else
				{
					markerLocation = mMap
							.addMarker(new MarkerOptions()
									.position(myLocation)
									.icon(BitmapDescriptorFactory
											.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				}
				
				if (mMap != null)
				{
					if (firstZoom == false)
					{
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
								myLocation, currentZoom));
						markerMedia();
						firstZoom = true;
					}
				} else
				{
					Toast.makeText(getApplicationContext(),
							R.string.GM_NotReached, Toast.LENGTH_SHORT).show();
				}
				// save (new) location in mylastlocation variable
				mylastlocation = location;
			}
		};
		mLocationClient = new LocationClient(this, this, this);
		
		initMap();
	}
	
	protected void onResume()
	{
		super.onResume();
		
		if (mMap == null)
			initMap();
	}
	
	/**
	 * 
	 */
	public void markerMedia()
	{
		Cursor cur = cursorAdapter.getCursor();
		if (cur.getCount() != 0)
		{
			cur.moveToFirst();
			do
			{
				// on recupere les infos du medias pour créer son point
				// geolocaliser
				int lati_index = cur.getColumnIndex(MediasDb.LATITUDE_COLUMN);
				int longi_index = cur.getColumnIndex(MediasDb.LONGITUDE_COLUMN);
				float lati = cur.getFloat(lati_index);
				float longi = cur.getFloat(longi_index);

				int remark_index = cur.getColumnIndex(MediasDb.REMARK_COLUMN);
				String remark = cur.getString(remark_index);

				int path_index = cur.getColumnIndex(MediasDb.FILE_NAME_COLUMN);
				String path = cur.getString(path_index);

				LatLng point = new LatLng(lati + Math.random(), longi
						+ Math.random());
				//drawMarker(point, remark, path);
				drawMarker(point, remark);
				// add point in pointsList
				pointsList.add(point);

			} while (cur.moveToNext() != false);

		}

	}

	// Draw a marker at the "point"
//	private void drawMarker(LatLng point, String remark, String path)
	private void drawMarker(LatLng point, String remark)
	{
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude, longitude and title for the marker
		markerOptions.position(point).title(remark);
//				.icon(BitmapDescriptorFactory.fromPath(path));

		// Adding marker on the Google Map
		mMap.addMarker(markerOptions);

	}

	private void initMap()
	{
		if (mMap != null && firstZoom == false)
		{
			LatLng myLatLng = new LatLng(Constants.GM_LATITUDE,
					Constants.GM_LONGITUDE);
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
					currentZoom));
			firstZoom = true;
		}
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMyLocationEnabled(true);
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
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			mMap.moveCamera(CameraUpdateFactory
					.newCameraPosition(myCameraPosition));
			// Restore markers
			if (savedInstanceState.containsKey("points"))
			{
				pointsList = savedInstanceState
						.getParcelableArrayList("points");
				if (pointsList != null)
				{
					for (int i = 0; i < pointsList.size(); i++)
					{
						// TODO mettre le path en sauvegarde
						drawMarker(pointsList.get(i), String.valueOf(i));
					}
				}
			}

		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		// save camera position
		CameraPosition myCameraPosition = mMap.getCameraPosition();
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

		// save markers list
		savedInstanceState.putParcelableArrayList("points", pointsList);
	}

	// Create custom adapter
	protected void createAdapter(Cursor cursor)
	{
		// Display image and comment
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
				cursor, new String[] { MediasDb.FILE_NAME_COLUMN,
						MediasDb.REMARK_COLUMN, MediasDb.FILTER_COLUMN },
				new int[] { R.id.imageIcon, R.id.remark }, 0);

		// Convert String to image for ImageView
		/*
		 * cursorAdapter.setViewBinder(new ViewBinder() {
		 * 
		 * @Override public boolean setViewValue(View view, Cursor cursor, int
		 * columnIndex) { if (view instanceof CustomImageView) { CustomImageView
		 * imageView = (CustomImageView) view;
		 * 
		 * // Path of media String path = cursor.getString(columnIndex); //
		 * display image depend on path and file existence
		 * imageView.setImagePath(path);
		 * 
		 * int filterInt = cursor .getColumnIndex(MediasDb.FILTER_COLUMN); if
		 * (cursor.getInt(filterInt) == 1) imageView.blackAndWhiteMode(true);
		 * 
		 * return true; } return false; }
		 * 
		 * });
		 */
	}

	// CONTENT PROVIDER
	private void displayContentProvider()
	{
		String columns[] = new String[] { MediasDb._ID,
				MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN,
				MediasDb.FILTER_COLUMN, MediasDb.LATITUDE_COLUMN,
				MediasDb.LONGITUDE_COLUMN };
		Uri mContacts = MediaContentProvider.CONTENT_URI;
		Cursor cur = getContentResolver().query(mContacts, columns, null, null,
				"_id DESC LIMIT 2");

		createAdapter(cur);

	}

	
	/*
	//customisation du marker
	public class MediaWindowAdapterMarker implements InfoWindowAdapter
	{

		private Marker markerShowingInfoWindow;
		private Context mContext;

		public MediaWindowAdapterMarker(Context context)
		{
			mContext = context;
		}

		@Override
		public View getInfoContents(Marker marker)
		{

			markerShowingInfoWindow = marker;

			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Getting view from the layout file info_window_layout
			View popUp = inflater.inflate(R.layout.layout_popup, null);

			TextView popUpTitle = (TextView) popUp
					.findViewById(R.id.popup_title);
			TextView popUpContent = (TextView) popUp
					.findViewById(R.id.popup_content);
			ImageView popUpImage = (ImageView) popUp
					.findViewById(R.id.popup_image);

			popUpTitle.setText(marker.getTitle());
			popUpContent.setText(marker.getSnippet());

			// Load the image thumbnail
			final String imagePath = markers.get(marker.getId());
			ImageLoader imageLoader = ((AppConfig) mContext
					.getApplicationContext()).getImageLoader();
			imageLoader.loadBitmap(imagePath, popUpImage, 0, 0, onImageLoaded);

			// Returning the view containing InfoWindow contents
			return popUp;
		}

		@Override
		public View getInfoWindow(Marker marker)
		{

			return null;
		}

		/**
		 * This method is called after the bitmap has been loaded. It checks if
		 * the currently displayed info window is the same info window which has
		 * been saved. If it is, then refresh the window to display the newly
		 * loaded image.
		 */
	
	/*
		private Callback onImageLoaded = new Callback()
		{

			@Override
			public void execute(String result)
			{
				if (markerShowingInfoWindow != null
						&& markerShowingInfoWindow.isInfoWindowShown())
				{
					markerShowingInfoWindow.hideInfoWindow();
					markerShowingInfoWindow.showInfoWindow();
				}
			}
		};

	} */
		
	
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
}
