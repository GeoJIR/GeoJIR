package com.geojir.medias;

import java.io.File;
import java.net.URI;

import android.media.MediaRecorder;

import com.geojir.Constants;

public class Sound extends RecordableMedia
{
	public Sound()
	{
		super();
	}
	
	public Sound(String path)
	{
		super(path);
	}
	
	public Sound(URI uri)
	{
		super(uri);
	}
	
	public Sound(File mediaFile)
	{
		super(mediaFile);
	}	
	
	// Configure recorder for Sound media
	protected void configureRecorder()
	{
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
	}
	
	public String getExt()
	{
		return Constants.EXT_AUDIO;
	}

	public String getType()
	{
		return Constants.TYPE_AUDIO;
	}
	
	public String getDir()
	{
		return Constants.PATH_AUDIO;
	}
}
