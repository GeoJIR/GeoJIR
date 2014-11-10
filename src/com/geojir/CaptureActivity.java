package com.geojir;

import static com.geojir.Constants.REQUEST_TAKE_PHOTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import com.geojir.medias.Media;
import com.geojir.medias.Photo;
import com.geojir.medias.RecordableMedia;
import com.geojir.medias.Sound;
import com.geojir.menus.TabImageMenu;
import com.geojir.override.OneLineArrayList;
import com.geojir.view.CaptureImageView;
import com.google.android.gms.maps.model.LatLng;

import android.app.PendingIntent;

public class CaptureActivity extends ParentMenuActivity implements LocationListener
{
	// Butterknife injectViews
	@InjectView(R.id.captureImageView)
	CaptureImageView captureImageView;
	@InjectView(R.id.filterMonochrome)
	CheckBox filterMonochrome;
	@InjectView(R.id.playAudioButton)
	Button playAudioButton;
	@InjectView(R.id.recordAudioButton)
	Button recordAudioButton;
	@InjectView(R.id.commentText)
	EditText editComment;
	@InjectView(R.id.saveMediaButton)
	Button saveMediaButton;
	@InjectViews({ R.id.imagePhotos, R.id.imageMicro })
	List<ImageView> mediasIcons;
	@InjectViews({ R.id.photoFrame, R.id.audioFrame })
	List<View> mediasLayout;

	@InjectView(R.id.progressBar)
	ProgressBar progressBar;

	// List of media
	protected OneLineArrayList<String> mediasList = new OneLineArrayList<String>()
			.put(Constants.TYPE_IMAGE).put(Constants.TYPE_AUDIO);

	// ////// WARNING ////////
	// Order and size of mediasLayout, mediasIcons and mediasList have to be
	// coherent

	// Memory of current media
	protected String currentMedia = Constants.TYPE_IMAGE;

	// Save instance constants
	protected final static String PHOTO_ON_RESTORE = "photoOnRestore";
	protected final static String AUDIO_ON_RESTORE = "audioOnRestore";
	protected final static String MEDIA_ON_RESTORE = Constants.TYPE_IMAGE;

	// Media variable
	protected Sound sound;
	protected Photo photo;

	protected TabImageMenu menu = new TabImageMenu();
	
	//Localization
	LocationManager locationManager;
	String locationProvider;

	// shared preferences
	SharedPreferences preferences;
	public static Context contextOfApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// CreateView
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// Inject ButterKnife Views
		ButterKnife.inject(this);
		
		preferences = getSharedPreferences(Constants.PREF_LOCATION,	Context.MODE_PRIVATE);
		
		restoreState(savedInstanceState);
		
		//initialize the location manager
		this.initializeLocationManager();
		
		saveMediaButton.requestFocus();

		// Create tab menu images
		menu.addAll(mediasIcons, mediasLayout);
		// Update screen
		changeCaptureType();

		// Initialize Audio button
		if (sound == null)
			createSound();
		if (new File(sound.getPath()).exists())
			playAudioButton.setEnabled(true);
		else
			playAudioButton.setEnabled(false);
		
		// Hide loading bar
		progressBar.setVisibility(View.INVISIBLE);
	}

	// Restore medias
	protected void restoreState(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			// Restore photo
			String photoRestore = savedInstanceState
					.getString(PHOTO_ON_RESTORE);
			if (photoRestore != null && !photoRestore.isEmpty())
			{
				createPhoto(photoRestore);
			}
			// restore audio
			String audioRestore = savedInstanceState
					.getString(AUDIO_ON_RESTORE);
			if (audioRestore != null && !audioRestore.isEmpty())
			{
				createSound();
				sound.restore(audioRestore);
				createSoundObserver();
			}

			// Restore current media
			currentMedia = savedInstanceState.getString(MEDIA_ON_RESTORE);
		}
	}

	// Create observer to detected sound state change
	protected void createSoundObserver()
	{
		Observable.create(sound).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<String>()
				{
					@Override
					public void call(String status)
					{
						changeAudioButtonState();
					}
				});
	}

	// Change current media when click on media icon
	@OnClick(R.id.saveMediaButton)
	public void clickOnSaveMedia(View view)
	{
		class SaveAsynchrone extends AsyncTask<Object, Object, Object>
		{

			// Before execute async task
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				progressBar.setVisibility(View.VISIBLE);
				toast(R.string.start_save_media_toast);
			}

			// After execute async task
			@Override
			protected void onPostExecute(Object result)
			{
				super.onPostExecute(result);
				
				// Reload view
				onCreate(null);
				toast(R.string.stop_save_media_toast);
			}

			@Override
			protected Object doInBackground(Object... params)
			{
				// Get current media
				Media mediaTemp = null;
				if (currentMedia == Constants.TYPE_IMAGE)
					mediaTemp = photo;
				else if (currentMedia == Constants.TYPE_AUDIO)
					mediaTemp = sound;
				
				// Save current media
				if (mediaTemp != null)
				{
					try
					{
						mediaTemp.save(editComment.getText().toString());
					}
					catch (InstantiationException | IllegalAccessException
							| IOException e)
					{
						e.printStackTrace();
					}
				}
				
				return null;
			}
		}
		
		// Lauch async media save
		SaveAsynchrone tacheAsynchrone = new SaveAsynchrone();
		tacheAsynchrone.execute();

	}

	// Change current media when click on media icon
	@OnClick(R.id.filterMonochrome)
	public void clickOnMonochromeFilter(View view)
	{
		captureImageView.blackAndWhiteMode(filterMonochrome.isChecked());
	}

	// Change current media when click on media icon
	@OnClick({ R.id.imagePhotos, R.id.imageMicro })
	public void clickIconForChangeMedia(ImageView view)
	{
		int index_temp = mediasIcons.indexOf(view);
		if (sound != null)
			sound.stop();
		currentMedia = mediasList.get(index_temp);
		changeCaptureType();
	}

	@OnClick(R.id.recordAudioButton)
	public void clickOnAudioRecord(View view)
	{
		// Create sound if not exist
		if (sound == null)
			createSound();
		// Active/Stop record
		if (sound.getState() != RecordableMedia.RECORD_STATE)
			sound.record();
		else
			sound.stop();
	}

	protected void createSound()
	{
		sound = new Sound();
		createSoundObserver();
	}

	protected void createPhoto()
	{
		createPhoto("");
	}

	protected void createPhoto(String restoreString)
	{
		photo = new Photo();
		if (restoreString != "")
			photo.restore(restoreString);
		captureImageView.load();
		captureImageView.blackAndWhiteMode(filterMonochrome.isChecked());
	}

	// Change text and avaibility of audio button and display toast
	protected void changeAudioButtonState()
	{
		if (sound == null)
			return;

		// Start record
		if (sound.getState() == RecordableMedia.RECORD_STATE)
		{
			playAudioButton.setEnabled(false);
			recordAudioButton.setText(R.string.stop_audio_record_button_text);
			toast(R.string.start_audio_record_toast);
		}
		// Start play
		if (sound.getState() == RecordableMedia.PLAY_STATE)
		{
			recordAudioButton.setEnabled(false);
			playAudioButton.setText(R.string.stop_audio_play_button_text);
			toast(R.string.start_audio_play_toast);
		}
		// Stop
		if (sound.getState() == RecordableMedia.STOP_STATE)
		{
			// stop record
			if (!playAudioButton.isEnabled())
			{
				playAudioButton.setEnabled(true);
				recordAudioButton
						.setText(R.string.start_audio_record_button_text);
				toast(R.string.stop_audio_record_toast);
			}
			// Stop playing
			else if (!recordAudioButton.isEnabled())
			{
				recordAudioButton.setEnabled(true);
				playAudioButton.setText(R.string.start_audio_play_button_text);
				toast(R.string.stop_audio_play_toast);
			}
		}
	}

	@OnClick(R.id.playAudioButton)
	public void clickOnAudioPlay(View view)
	{
		// Only if a sound exist
		if (sound == null)
			return;

		if (sound.getState() != RecordableMedia.PLAY_STATE)
		{
			try
			{
				sound.play();
			} catch (IllegalArgumentException | SecurityException
					| IllegalStateException | IOException e)
			{
			}
		} else
			sound.stop();
	}

	// Display only current media
	protected void changeCaptureType()
	{
		int index_temp = mediasList.indexOf(currentMedia);
		menu.activeTab(mediasIcons.get(index_temp));
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		// reload photo if captureImageView exists
		if (captureImageView != null)
			createPhoto();
	}

	// When an another app send result
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TAKE_PHOTO)
			createPhoto();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		// if sound exist, stop this
		if (sound != null)
			sound.stop();
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(MEDIA_ON_RESTORE, currentMedia);
		// Save the user's current medias
		if (photo != null)
			savedInstanceState.putString(PHOTO_ON_RESTORE, photo.getPath());
		if (sound != null)
			savedInstanceState.putString(AUDIO_ON_RESTORE, sound.getPath());
	}

	//-------------------------------------------
	// Summary: initialize location manager
	//-------------------------------------------
	private void initializeLocationManager() {
		//get the location manager
		this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		//define the location manager criteria
        Criteria criteres = new Criteria();
        criteres.setAccuracy(Criteria.ACCURACY_COARSE);
        criteres.setPowerRequirement(Criteria.POWER_LOW);
        criteres.setAltitudeRequired(false);
        criteres.setCostAllowed(true);

        //do not work correctly on network......
//        String bestLocationProvider = this.locationManager.getBestProvider(criteres, true);
//        this.locationManager.requestLocationUpdates(bestLocationProvider, 10000, 5.0f, this);
 
        //works with GPS.....
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5.0f, this);
        
    	this.locationProvider = locationManager.getBestProvider(criteres, false);
        
		Location location = locationManager.getLastKnownLocation(locationProvider);

		//initialize the location
		if(location != null) {
			onLocationChanged(location);
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		// TODO Auto-generated method stub
		LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
		
        //Utilisé en débug pour vérifier les valeur de géolocalisation
//        String mLatAndLongStr = String.format("Lat:%.2f - Long:%.2f", myLocation.latitude,myLocation.longitude);
//        Toast.makeText(CaptureActivity.this, "Location update: " + mLatAndLongStr, Toast.LENGTH_LONG).show();

        //Update preferences location
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(Constants.PREF_LOCATION_LATITUDE,(float)myLocation.latitude);
        editor.putFloat(Constants.PREF_LOCATION_LONGITUDE,(float)myLocation.longitude);
        editor.commit();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub
		
	}

}
