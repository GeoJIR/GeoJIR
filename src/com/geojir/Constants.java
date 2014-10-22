package com.geojir;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class Constants {
	
	//Directories URL
    public static final String PATH_GEOJIR = "";
    public static final String PATH_VIDEO = PATH_GEOJIR + "/" + Environment.DIRECTORY_MOVIES;
    public static final String PATH_AUDIO = PATH_GEOJIR + "/" + Environment.DIRECTORY_MUSIC;
    public static final String PATH_IMAGE = PATH_GEOJIR + "/" + Environment.DIRECTORY_PICTURES;
    public static final String PATH_DOCUMENT = PATH_GEOJIR + "/" + "Documents";
 
	//Types of medias
    public static final String TYPE_VIDEO = Environment.DIRECTORY_MOVIES;
    public static final String TYPE_AUDIO = Environment.DIRECTORY_MUSIC;
    public static final String TYPE_IMAGE = Environment.DIRECTORY_PICTURES;
	
	//Extension of medias
	public static final String EXT_VIDEO = "mp4";
	public static final String EXT_AUDIO = "3gp";
	public static final String EXT_IMAGE = "jpg";
	
	//Intent Photo
	public static final int REQUEST_CODE = 1888;
	
	//Divers
	public static final int NB_LIST_LAST_MEDIA = 10;
	public static final int NB_CAR_COMMENT = 140;
	
	//Methods
	// Checks if external storage is available for read and write
	private boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}


	
	private boolean extStorage()
	{
		if(this.isExtStorageWritable)
		{
			return true;
		}
		return false;
	}
	
	public File initConstants()
	{
		if(extStorage())
		{
		    // Get the directory for the user's public pictures directory. 
		    File file = Environment.getRootDirectory();
		    
		    if (!file.mkdirs()) {
		        Log.e("initConstants", "Directory not created");
		    }
		    return file;
		}
		else
		{
			
		}
	}

	/*	
	public File getAppStorageDir(Context context) {
	    // Get the directory for the app's private pictures directory. 
	    File file = context.getExternalFilesDir();
	    if (!file.mkdirs()) {
	        Log.e(LOG_TAG, "Directory not created");
	    }
	    return file;
	}
*/	
	
}

