package com.geojir.medias;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import rx.Observable;
import rx.Subscriber;

// Class Media with play, stop and record action
public abstract class RecordableMedia extends Media implements Observable.OnSubscribe<String>
{
	// State constants
	public final static String EMPTY_STATE = "empty";
	public final static String RECORD_STATE = "record";
	public final static String STOP_STATE = "stop";
	public final static String PLAY_STATE = "play";
	
	protected MediaRecorder recorder;
	protected MediaPlayer player;	
	
	// Subscribe of state of media
	protected Subscriber<? super String> subscriber;
	protected String state = EMPTY_STATE;
	
	public String getState()
	{
		return state;
	}

	public RecordableMedia()
	{
		super();
		createRecorder();
	}
	
	// Recorder's creation depends on path of media
	protected void createRecorder()
	{
		recorder = new MediaRecorder();
		configureRecorder();
		recorder.setOutputFile(getPath());
		
		// Initialize state depending on file existence
		if (file != null)
			changeState(STOP_STATE);
		else
			changeState(EMPTY_STATE);
	}
	
	// Configuration depend on media, see child class
	protected void configureRecorder() {}
	
	// State change and call subscribe
	protected void changeState(String newState)
	{
		state = newState;
		if (subscriber != null)
			subscriber.onNext(state);
	}

	@Override
	public void call(Subscriber<? super String> newSubscriber)
	{
		// Save subscriber
		subscriber = newSubscriber;
	}
	
	// Save media flux
	public void record()
	{
		try
		{
			recorder.prepare();
			recorder.start();
			changeState(RECORD_STATE);
		}
		catch (IllegalStateException | IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	// restore RecordableMedia implies recreate recorder
	public void restore(String restoreURI)
	{
		super.restore(restoreURI);
		createRecorder();
	}
	
	// Stop play or record
	public void stop()
	{
		if (state == RECORD_STATE)
		{
			recorder.stop();
			recorder.release();
		}
		else if (state == PLAY_STATE)
		{
			player.release();
			player = null;
		}
		
		changeState(STOP_STATE);
	}
	
	// Play media
	public void play() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException
	{
		player = new MediaPlayer();
		player.setDataSource(getPath());
		player.prepare();
		player.start();
		
		changeState(PLAY_STATE);
		
		// Create listener when media complete
		player.setOnCompletionListener(new OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer mediaPlayer)
			{
				// stop media when player stop
				stop();
			}
		});

	}
}
