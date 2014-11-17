package com.geojir.medias;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import rx.Observable;
import rx.Subscriber;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;

import com.geojir.ParentMenuActivity;

// Class Media with play, stop and record action
public abstract class RecordableMedia extends Media implements Observable.OnSubscribe<String>
{
	// State constants
	public final static String EMPTY_STATE = "empty";
	public final static String RECORD_STATE = "record";
	public final static String STOP_STATE = "stop";
	public final static String PLAY_STATE = "play";
	
	protected MediaRecorder recorder;
	
	// Subscribe of state of media
	protected Subscriber<? super String> subscriber;
	protected String state = EMPTY_STATE;
	
	public RecordableMedia()
	{
		super();
	}
	
	public RecordableMedia(String path)
	{
		super(path);
	}
	
	public RecordableMedia(URI uri)
	{
		super(uri);
	}
	
	public RecordableMedia (File mediaFile)
	{
		super(mediaFile);
	}
	
	// Configuration depend on media, see child class
	protected void configureRecorder() {}
	
	public String getState()
	{
		return state;
	}
	
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
	protected void restore(File mediaFile)
	{
		super.restore(mediaFile);
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
			// Not use changeState for not calling subscriber
			// for not toasting
			state = STOP_STATE;
		else
			changeState(EMPTY_STATE);
	}
	
	// Stop play or record
	public void stop()
	{
		if (state == RECORD_STATE)
		{
			recorder.stop();
			recorder.release();
			// Generate file
			file = new File(getPath());
			changeState(STOP_STATE);
		}
		else if (state == PLAY_STATE)
		{
			changeState(STOP_STATE);
		}
	}
	
	// Play media
	public void play() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException
	{
		
		if (file == null)
			return;
		
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		intent.setDataAndType(Uri.fromFile(file), "audio/*");  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
		ParentMenuActivity.CONTEXT.startActivity(intent);
	}
}
