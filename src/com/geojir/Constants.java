package com.geojir;

import java.io.File;
import java.lang.reflect.Array;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class Constants {
	
	//Directories URL
    public static String PATH_GEOJIR = "GeoJIR";
    public static String PATH_VIDEO = PATH_GEOJIR + "/" + Environment.DIRECTORY_MOVIES;
    public static String PATH_AUDIO = PATH_GEOJIR + "/" + Environment.DIRECTORY_MUSIC;
    public static String PATH_IMAGE = PATH_GEOJIR + "/" + Environment.DIRECTORY_PICTURES;
    public static String PATH_DOCUMENT = PATH_GEOJIR + "/" + "Documents";
 
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
	private static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}


	
	private static boolean extStorage()
	{
		if(isExternalStorageWritable())
		{
			return true;
		}
		return false;
	}
	
	public static File initConstants()
	{
		// Get the directory for the user's public pictures directory. 
//		File file = Environment.getRootDirectory();
		
		String filename = PATH_GEOJIR + ".";
		
		Array listPath String[] = {PATH_GEOJIR, PATH_VIDEO, PATH_AUDIO, PATH_IMAGE, PATH_DOCUMENT};
		
		
		//vérification d'existence des sous rép de stockage (video, audio, image et documents) 
		if(extStorage())
		{
		    File filer = new File(Environment.getExternalStoragePublicDirectory(
		    		PATH_GEOJIR), "" );
		    
		    if (!filer.mkdirs()) {
		        Log.e(PATH_GEOJIR, "Directory not created");
		    }
			return filer;
		}
		else
		{
		    File filer = new File(Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_MOVIES), filename );
		    
		    if (!filer.mkdirs()) {
		        Log.e(PATH_GEOJIR, "Directory not created");
		    }
		    return filer;
			
		}
	}	
}

