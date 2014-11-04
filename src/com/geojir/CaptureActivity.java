package com.geojir;

import static com.geojir.Constants.REQUEST_TAKE_PHOTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import android.content.Intent;
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

public class CaptureActivity extends ParentMenuActivity
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// CreateView
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		// Inject ButterKnife Views
		ButterKnife.inject(this);

		restoreState(savedInstanceState);
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
		
		//on cache la barre de progression
		progressBar.setAlpha(0f);
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
		// On lance la tâche asynchrone
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

	// Shorts methods for toast
	protected void toast(int idString)
	{
		toast(getString(idString));
	}

	protected void toast(String message)
	{
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
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

	/**
	 **
	 * Tâche asynchrone à exécuter lors de l'appui sur le bouton Le premier
	 * paramètre de généricité (Void) représente le type de paramètre à passer
	 * dans la méthode doInBackground Le second paramètre de généricité
	 * (Integer) représente le type de paramètre à passer à la méthode
	 * onProgressUpdate Le troisième paramètre de généricité (Void) représente
	 * le type de paramètre à passer à la méthode onPostExecute
	 */
	private class SaveAsynchrone extends AsyncTask
	{

		// Méthode exécutée au début de l'execution de la tâche asynchrone
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			progressBar.setAlpha(1f);
			Toast.makeText(getApplicationContext(),
					"Début de l'enregistrement", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onProgressUpdate(Object... values)
		{
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			// Mise à jour de la ProgressBar
			//onProgressUpdateInt((int)values[0]);
			//progressBar.setProgress( values[0]);
		}
		
		

		// Méthode exécutée à la fin de l'execution de la tâche asynchrone
		@Override
		protected void onPostExecute(Object result)
		{
			
			super.onPostExecute(result);
			progressBar.setAlpha(0f);
			Toast.makeText(getApplicationContext(),
					"L'enregistrement est terminé", Toast.LENGTH_SHORT).show();
			
			//on recrée l'application pour vider tous les champs
			onCreate(null);
		}

		@Override
		protected Object doInBackground(Object... params)
		{
			Media mediaTemp = null;
			if (currentMedia == Constants.TYPE_IMAGE)
				mediaTemp = photo;
			else if (currentMedia == Constants.TYPE_AUDIO)
				mediaTemp = sound;

			if (mediaTemp != null)
			{
				try
				{
					mediaTemp.save(editComment.getText().toString());
				} catch (InstantiationException | IllegalAccessException
						| IOException e)
				{
					e.printStackTrace();
				}
			}

			int progress = 0;
			for (progress = 0; progress < 10; progress++)
			{
				for (int i = 0; i < 1000; i++)
				{
					// Ne fait rien mais fait juste passer du temps
				}

				// publishProgress met à jour l'interface en invoquant la
				// méthode onProgressUpdate
				progress++;
				publishProgress(progress);
			}

			return null;
		}
	}

}