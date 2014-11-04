package com.geojir.medias;

import android.media.MediaRecorder;

import com.geojir.Constants;

public class Sound extends RecordableMedia
{
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
