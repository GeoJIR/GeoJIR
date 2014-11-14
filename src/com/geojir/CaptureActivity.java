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
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class CaptureActivity extends ParentMenuActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener
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
	protected final static String MEDIA_ON_RESTORE = "mediaOnRestore";
	protected final static String COMMENT_ON_RESTORE = "commentOnRestore";
	protected final static String FILTER_ON_RESTORE = "filterOnRestore";

	// Media variable
	protected Sound sound;
	protected Photo photo;

	protected TabImageMenu menu = new TabImageMenu();

	// Localization
	LocationRequest locationRequest;
	LocationListener locationListener;
	LocationClient mLocationClient;

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

		restoreState(savedInstanceState);

		// initialize the location manager
		locationRequest = LocationRequest.create();
		// Use highest accuracy
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the update interval in ms
		locationRequest.setInterval(Constants.GM_UPDATE_INTERVAL);
		// Set the fastest update interval in ms
		locationRequest.setFastestInterval(Constants.GM_FASTEST_INTERVAL);

		locationListener = new LocationListener()
		{

			@Override
			public void onLocationChanged(Location location)
			{
				LatLng myLocation = new LatLng(location.getLatitude(),
						location.getLongitude());

				// Used in debug to verify geolocalization values
				// String mLatAndLongStr = String.format("Lat:%.2f - Long:%.2f",
				// myLocation.latitude,myLocation.longitude);
				// Toast.makeText(CaptureActivity.this, "Location update: " +
				// mLatAndLongStr, Toast.LENGTH_LONG).show();

				Constants.GM_LATITUDE = (float) myLocation.latitude;
				Constants.GM_LONGITUDE = (float) myLocation.longitude;
			}
		};
		mLocationClient = new LocationClient(this, this, this);

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
		progressBar.setVisibility(View.GONE);
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
			// Restore comment
			editComment.setText(savedInstanceState
					.getString(COMMENT_ON_RESTORE));
			// Restore current monochrome
			filterMonochrome.setChecked(savedInstanceState
					.getBoolean(FILTER_ON_RESTORE));
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
				Boolean monochrome = currentMedia == Constants.TYPE_IMAGE
						&& filterMonochrome.isChecked();
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
						mediaTemp.save(editComment.getText().toString(),
								monochrome);
					} catch (InstantiationException | IllegalAccessException
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

		savedInstanceState.putString(COMMENT_ON_RESTORE, editComment.getText()
				.toString());
		savedInstanceState.putBoolean(FILTER_ON_RESTORE,
				filterMonochrome.isChecked());
	}

	/*
	 * Called by Google Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	public void onConnected(Bundle dataBundle)
	{
		mLocationClient.requestLocationUpdates(locationRequest,
				locationListener);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0)
	{
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
