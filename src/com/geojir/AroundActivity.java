package com.geojir;

import java.util.ArrayList;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.geojir.db.ListMediaDb;
import com.geojir.db.MediaContentProvider;
import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.view.CustomImageView;
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

	// shared preferences
	protected SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_around);

		restoreState(savedInstanceState);

		initMap(savedInstanceState);

		if (mMap != null)
		{
			mMap.setOnMyLocationChangeListener(myLocationChangeListener);

			// recuperation de donnée dans la BDD // database instantiate
			ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());
			/*
			 * // last X entries values = new ArrayList<Map<String, String>>();
			 * Observable.create(listeMedia).map(new Func1<Map<String, String>,
			 * Map<String, String>>() {
			 * 
			 * @Override public Map<String, String> call(Map<String, String>
			 * item) { values.add(item); return item; }
			 * }).subscribeOn(Schedulers
			 * .io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new
			 * Action1<Map<String, String>>() {
			 * 
			 * @Override public void call(Map<String, String> item) {
			 * markerMedia(); } });
			 */
		} else
		{
			Toast.makeText(getApplicationContext(), R.string.GM_NotReached,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 
	 */
	public void markerMedia()
	{
		for (int i = 1; i < 10; i++)
		{ // traitement de creation et d'ajout des marker
			LatLng point = new LatLng(43.6109200 + i / 4, 3.8772300 + i / 4);
			drawMarker(point, String.valueOf(i));
			// add point in pointsList
			pointsList.add(point);
		}
	}

	// Draw a marker at the "point"
	private void drawMarker(LatLng point, String remark)
	{
		// Creating an instance of MarkerOptions
		MarkerOptions markerOptions = new MarkerOptions();

		// Setting latitude, longitude and title for the marker
		markerOptions.position(point).title(remark);

		// Adding marker on the Google Map
		mMap.addMarker(markerOptions);

	}

	private void initMap(Bundle savedInstanceState)
	{
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		mMap.setMyLocationEnabled(true);

		if (savedInstanceState == null)
		{
			LatLng myLatLng = new LatLng(Constants.GM_LATITUDE,
					Constants.GM_LONGITUDE);
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng,
					currentZoom));
			Toast.makeText(getApplicationContext(), R.string.GM_WaitLocation,
					Toast.LENGTH_SHORT).show();
		}
	}

	private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener()
	{
		@Override
		public void onMyLocationChange(Location location)
		{
			LatLng myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			// Pour les tests
			// String mLatAndLongStr = String.format("Lat:%.2f - Long:%.2f",
			// myLocation.latitude,myLocation.longitude);
			// Toast.makeText(AroundActivity.this, "Location update: " +
			// mLatAndLongStr, Toast.LENGTH_LONG).show();

			// markerLocation not null => marker has been already placed
			if (markerLocation != null)
			{
				if (mMap.getCameraPosition().zoom != currentZoom)
				{
					// get zoom level
					currentZoom = mMap.getCameraPosition().zoom;
				}

				// if difference between new location and last location is less
				// than GM_DEFAULT_DISTANCE meters
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
				if (currentZoom == com.geojir.Constants.GM_DEFAULT_ZOOM)
				{
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							myLocation, currentZoom));
					markerMedia();
				} else
				{
					CameraPosition myCameraPosition = new CameraPosition(
							myLocation, currentZoom, currentTilt,
							currentBearing);
					mMap.moveCamera(CameraUpdateFactory
							.newCameraPosition(myCameraPosition));
				}
			} else
			{
				Toast.makeText(getApplicationContext(), R.string.GM_NotReached,
						Toast.LENGTH_SHORT).show();
			}
			// save (new) location in mylastlocation variable
			mylastlocation = location;
		}
	};

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
			cursorAdapter = new SimpleCursorAdapter(this,
					R.layout.list_item,
					cursor,
					new String[] { MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN, MediasDb.FILTER_COLUMN },
					new int[] {R.id.imageIcon, R.id.remark}
					, 0
			);
			
			// Convert String to image for ImageView
			cursorAdapter.setViewBinder(new ViewBinder()
			{
				@Override
				public boolean setViewValue(View view, Cursor cursor,
						int columnIndex)
				{
					if (view instanceof CustomImageView)
					{
						CustomImageView imageView = (CustomImageView) view;
						
						// Path of media
						String path = cursor.getString(columnIndex);
						// display image depend on path and file existence
						imageView.setImagePath(path);
						
						int filterInt = cursor.getColumnIndex(MediasDb.FILTER_COLUMN);
						if (cursor.getInt(filterInt) == 1)
							imageView.blackAndWhiteMode(true);
						
						return true;
					}
					return false;
				}
				
			});
		}
	
	// CONTENT PROVIDER
	private void displayContentProvider()
	{
		String columns[] = new String[] { MediasDb._ID,
				MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN,
				MediasDb.FILTER_COLUMN };
		Uri mContacts = MediaContentProvider.CONTENT_URI;
		Cursor cur = getContentResolver().query(mContacts, columns, null, null,
				null);

		createAdapter(cur);

	}
}
