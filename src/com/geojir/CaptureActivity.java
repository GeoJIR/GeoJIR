package com.geojir;

import static com.geojir.Constants.REQUEST_TAKE_PHOTO;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.ButterKnife.Setter;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import com.geojir.medias.Sound;
import com.geojir.view.CaptureImageView;

public class CaptureActivity extends ParentMenuActivity
{
	// Context memory for use in Medias class
	public static Context CONTEXT;

	// Butterknife injectViews
	@InjectView(R.id.captureImageView)
	CaptureImageView captureImageView;
	@InjectView(R.id.playbutton)
	Button playButton;
	@InjectView(R.id.recordbutton)
	Button recordButton;
	@InjectViews(
	{ R.id.captureImageView, R.id.mediaController })
	List<View> mediasLayout;
	@InjectViews(
	{ R.id.imagePhotos, R.id.imageMicro })
	List<ImageView> mediasIcons;
	
	// List of media
	protected Hashtable<Integer, String> mediasList = new Hashtable<Integer, String>();
	// Memory of current media
	protected String media = Constants.TYPE_IMAGE;
	
	// Save instance constants
	final String photoOnRestore = "photoOnRestore";
	final String audioOnRestore = "audioOnRestore";
	final String audioOnRestoreURI = "audioOnRestoreURI";
	final String mediaOnRestore = media;
	
	// Butterknife Setter
	// Set visibility of media recording layout
	static final Setter<View, Boolean> VISIBILITY = new Setter<View, Boolean>()
	{
		@Override
		public void set(View view, Boolean value, int index)
		{
			if (value)
				view.setVisibility(View.VISIBLE);
			else
				view.setVisibility(View.INVISIBLE);

			view.setEnabled(value);
		}
	};
	// Set alpha of media icon
	static final Setter<View, Boolean> ENABLED = new Setter<View, Boolean>()
	{
		@Override
		public void set(View view, Boolean value, int index)
		{
			if (value)
				view.setAlpha((float) 1);
			else
				view.setAlpha((float) .3);
		}
	};

	// chemin du fichier son
	// private static String mFileName = null;

	boolean boolAudioRecording = true;
	boolean boolAudioPlaying = true;
	boolean boolAudioExist = false;
	protected Sound sound;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Create medias list possibility
		if (mediasList.isEmpty())
		{
			mediasList.put(R.id.imagePhotos, Constants.TYPE_IMAGE);
			mediasList.put(R.id.imageMicro, Constants.TYPE_AUDIO);
		}
		// CreateView
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// Inject ButterKnife Views
		ButterKnife.inject(this);

		if (savedInstanceState != null)
		{
			if (savedInstanceState.getString(photoOnRestore) != null && !savedInstanceState.getString(photoOnRestore).isEmpty())
				captureImageView.restore(savedInstanceState.getString(photoOnRestore));
			
			boolAudioExist = savedInstanceState.getBoolean(audioOnRestore);
			if (boolAudioExist)
				sound = new Sound(savedInstanceState.getString(audioOnRestoreURI));

			media = savedInstanceState.getString(mediaOnRestore);
		}
		
		// Save Context for Media class
		CONTEXT = getApplicationContext();

		// Update screen
		changeCaptureType();

		// photo
		captureImageView.onClickEvent(this);

		// Audio
		if (sound != null && sound.getFile().exists()
				&& !sound.getFile().getPath().isEmpty())
			playButton.setEnabled(true);
		else
			playButton.setEnabled(false);
	}

	// Change current media when click on media icon
	@OnClick(
	{ R.id.imagePhotos, R.id.imageMicro })
	public void clickIconForChangeMedia(View view)
	{
		media = mediasList.get(view.getId());
		changeCaptureType();
	}
	
	@OnClick(R.id.recordbutton)
	public void clickOnAudioRecord(View view)
	{
		if (boolAudioRecording)
			startAudioRecording();
		else
			stopAudioRecording();
		
		boolAudioRecording = !boolAudioRecording;
	}

	protected void startAudioRecording()
	{
		// Enable play button
		playButton.setEnabled(false);
		recordButton.setText(R.string.stop_record);
		
		Toast.makeText(getApplicationContext(), R.string.stop_record,
				Toast.LENGTH_SHORT).show();

		sound = new Sound();
		boolAudioExist = true;
	}

	private void stopAudioRecording()
	{
		if (sound != null)
			sound.stop();

		// on reactive le boutton pour jouer le son audio
		playButton.setEnabled(true);
		recordButton.setText(R.string.start_audio_record);

		Toast.makeText(getApplicationContext(),
				R.string.end_ok_record, Toast.LENGTH_SHORT)
				.show();
	}

	@OnClick(R.id.playbutton)
	public void clickOnAudioPlay(View view)
	{
		if (boolAudioPlaying)
		{
			try
			{
				startPlaying();
			} catch (IllegalArgumentException | SecurityException
					| IllegalStateException | IOException e)
			{
				e.printStackTrace();
			}
		} else
			stopPlaying();

		boolAudioPlaying = !boolAudioPlaying;
	}

	protected void startPlaying() throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException
	{
		// on desactive le boutton d'enregistrement
		recordButton.setEnabled(false);
		playButton.setText(R.string.end_play);
		sound.play();

		// message pour dire que lon joue le son audio
		Toast.makeText(getApplicationContext(), R.string.start_audio_play_message,
				Toast.LENGTH_SHORT).show();
	}

	private void stopPlaying()
	{
		if (sound != null)
			sound.stop();

		// on reactive le bouton pour enregistrer le son audio
		recordButton.setEnabled(true);
		playButton.setText(R.string.start_audio_play);

		Toast.makeText(getApplicationContext(), R.string.stop_audio_play_message,
				Toast.LENGTH_SHORT).show();
	}

	// Display only current media
	protected void changeCaptureType()
	{
		// Mask all medias layout
		ButterKnife.apply(mediasLayout, VISIBILITY, false);
		// Disable all media icons
		ButterKnife.apply(mediasIcons, ENABLED, false);

		// Active and display element depending current media
		if (media == Constants.TYPE_IMAGE)
		{
			VISIBILITY.set(ButterKnife.findById(this, R.id.captureImageView),
					true, 0);
			ENABLED.set(ButterKnife.findById(this, R.id.imagePhotos), true, 0);
		}
		if (media == Constants.TYPE_AUDIO)
		{
			VISIBILITY.set(ButterKnife.findById(this, R.id.mediaController),
					true, 0);
			ENABLED.set(ButterKnife.findById(this, R.id.imageMicro), true, 0);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		// on teste si il y a une photo deja présente
		if (captureImageView != null)
			captureImageView.load();
		if (boolAudioExist)
		{
			stopAudioRecording();
			stopPlaying();
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_TAKE_PHOTO)
		{
			captureImageView.load();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString(mediaOnRestore, media);
		// Save the user's current game state
		savedInstanceState.putString(photoOnRestore, captureImageView.getPath());
		savedInstanceState.putBoolean(audioOnRestore, boolAudioExist);
		if (sound != null && sound.getFile() != null)
			savedInstanceState.putString(audioOnRestoreURI, sound.getFile().getPath());
	}

}
