package com.geojir;

import java.io.File;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

public class Constants {
	
	//Directories URL
    public static String PATH_GEOJIR = "GeoJIR";
    public static String PATH_VIDEO = PATH_GEOJIR + "/" + Environment.DIRECTORY_MOVIES;
    public static String PATH_AUDIO = PATH_GEOJIR + "/" + Environment.DIRECTORY_MUSIC;
    public static String PATH_IMAGE = PATH_GEOJIR + "/" + Environment.DIRECTORY_PICTURES;
 
    private static final String[] listPath = {
    											PATH_GEOJIR,
    											PATH_VIDEO, 
    											PATH_AUDIO, 
    											PATH_IMAGE
    										};
	//Types of medias
    public static final String TYPE_VIDEO = Environment.DIRECTORY_MOVIES;
    public static final String TYPE_AUDIO = Environment.DIRECTORY_MUSIC;
    public static final String TYPE_IMAGE = Environment.DIRECTORY_PICTURES;
	
	//Extension of medias
	public static final String EXT_VIDEO = ".mp4";
	public static final String EXT_AUDIO = ".amr";
	public static final String EXT_IMAGE = ".jpg";
	
	//Intent Photo
	public static final int REQUEST_CODE = 436;
	public static final int REQUEST_TAKE_PHOTO = 437;
	
	//Divers
	public static final int NB_CAR_COMMENT = 140;
	
	//Preferences
	public static final String PREF_ACCOUNT = "pref_account";
	public static final String PREF_ACCOUNT_NAME = "pref_account_name";
	public static final String PREF_ACCOUNT_EMAIL = "pref_account_email";

	//BDD
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "GeoJIR.db";
	
	//Google Map
	public static final float GM_MPL_LATITUDE = 43.600f;
	public static final float GM_MPL_LONGITUDE = 3.883f;
	public static float GM_LATITUDE = GM_MPL_LATITUDE;
	public static float GM_LONGITUDE = GM_MPL_LONGITUDE;
	public static final int GM_DEFAULT_ZOOM = 12;
	public static final float GM_DEFAULT_DISTANCE = 10.0f;
	

	/************************ METHODS ******************/
	/*
	 */


	// Checks if external storage is available for read and write
	private static boolean isExternalStorageWritable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		return false;
	}
	
	public static void initConstants(Context context)
	{
		// Get the directory for the user's public pictures directory. 
		if(isExternalStorageWritable())
		{
		    //we'll create all directories if anyone are not created
	    	for(int i=0; i<listPath.length; i++) 
	    	{
	    		File file = new File(Environment.getExternalStoragePublicDirectory(listPath[i]), "" );
	    		if(!file.exists()) 
			    {
		    	    if(!file.mkdirs())
		    	        Log.e("ExternalStorage "+listPath[i], "Directory not created ");
		    	    else
		    	        Log.e("ExternalStorage "+listPath[i], "Directory created ");
			    }
	    	}
		}
		else
		{
		    //créer le path interne
			File file = new File(context.getFilesDir(), PATH_GEOJIR );
		    
		    if (!file.mkdirs()) {
		        Log.e(PATH_GEOJIR, "Directory not created");
		    }
		}
	}
}
