package com.geojir;

import static com.geojir.Constants.REQUEST_TAKE_PHOTO;

import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.geojir.view.CustomImageView;
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
	@InjectViews({ R.id.imagePhotos, R.id.imageMicro })
	List<ImageView> mediasIcons;
	@InjectViews({ R.id.photoFrame, R.id.audioFrame })
	List<View> mediasLayout;

	@InjectView(R.id.loadingCapture)
	CustomImageView loadingCapture;
	@InjectView(R.id.captureRootLayout)
	LinearLayout rootLayout;

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
	
	protected Boolean savePermission = false;
	protected Boolean onSave = false;
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
		
		// Hide loading bar
		loadingCapture.setVisibility(View.GONE);
		
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
		
		// Create tab menu images
		menu.addAll(mediasIcons, mediasLayout);

		// Initialize Audio button
		changeAudioButtonState();
		
		// Update screen
		changeCaptureType();
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
				createPhoto(photoRestore);
			// restore audio
			String audioRestore = savedInstanceState
					.getString(AUDIO_ON_RESTORE);
			if (audioRestore != null && !audioRestore.isEmpty())
				createSound(audioRestore);

			// Restore current media
			currentMedia = savedInstanceState.getString(MEDIA_ON_RESTORE);
			// Restore comment
			editComment.setText(savedInstanceState
					.getString(COMMENT_ON_RESTORE));
			// Restore current monochrome
			filterMonochrome.setChecked(savedInstanceState
					.getBoolean(FILTER_ON_RESTORE));
		}
		else
		{
			photo = null;
			sound = null;
			editComment.setText("");
			filterMonochrome.setChecked(false);
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

	// Save current media when click on actionBar icon
	public void saveMedia()
	{
		class SaveAsynchrone extends AsyncTask<Object, Object, Object>
		{

			// Before execute async task
			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
				// Clear display
				savePermission = false;
				clearFocus();
				
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
				Media mediaTemp = getCurrentMedia();
				
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
		
		// Display loading
		loadingCapture.setVisibility(View.VISIBLE);
		// Mask everything else
		rootLayout.setVisibility(View.GONE);
		
		// Launch async media save
		SaveAsynchrone tacheAsynchrone = new SaveAsynchrone();
		tacheAsynchrone.execute();

	}

	protected Media getCurrentMedia()
	{
		// Get current media
		Media mediaTemp = null;
		if (currentMedia == Constants.TYPE_IMAGE)
			mediaTemp = photo;
		else if (currentMedia == Constants.TYPE_AUDIO)
			mediaTemp = sound;
		
		return mediaTemp;
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
		createSound("");
	}

	protected void createSound(String path)
	{
		sound = new Sound(path);
		createSoundObserver();
	}

	protected void createPhoto()
	{
		createPhoto("");
	}

	protected void createPhoto(String restoreString)
	{
		photo = new Photo(restoreString);
		captureImageView.load();
		captureImageView.blackAndWhiteMode(filterMonochrome.isChecked());
		
		// User can save Photo
		savePermission = true;
	}

	// Change text and avaibility of audio button and display toast
	protected void changeAudioButtonState()
	{
		// User can't save Sound in play or record state
		savePermission = false;
		
		if (sound == null)
		{
			playAudioButton.setEnabled(false);
			recordAudioButton.setEnabled(true);
			recordAudioButton.setText(R.string.start_audio_record_button_text);
			
			return;
		}

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
			
			// User can save Sound
			savePermission = true;
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
		// Change menu
		int index_temp = mediasList.indexOf(currentMedia);
		menu.activeTab(mediasIcons.get(index_temp));
		
		// Check media existence
		Media curMedia = getCurrentMedia();
		Boolean bool = curMedia != null;
		
		// Active save button only if media exists
		savePermission = bool;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.capture, menu);
	    
	    return true;
	}
	
	@Override
	// Save with actionBar button
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.actionBarSave)
		{
			if (savePermission)
				saveMedia();
			else
			{
				// If no media to save, create alert user
				Builder builder = new Builder(CaptureActivity.this);
				builder.setMessage(R.string.dialog_no_media)
					.setTitle(R.string.dialog_error)
					.setNeutralButton(R.string.dialog_ok, new OnClickListener()
					{	
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							// if this button is clicked, just close
							// the dialog box and do nothing
							dialog.cancel();
						}
					});
				// display alert
				builder.create().show();
			}
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}	
	
	// When an another app send result
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
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
