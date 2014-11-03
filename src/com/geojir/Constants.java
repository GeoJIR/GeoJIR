package com.geojir;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

public class Constants extends Activity {
	
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
	public static final String EXT_VIDEO = "mp4";
	public static final String EXT_AUDIO = "amr";
	public static final String EXT_IMAGE = "jpg";
	
	//Intent Photo
	public static final int REQUEST_CODE = 436;
	public static final int REQUEST_TAKE_PHOTO = 437;
	
	//Divers
	public static final int NB_CAR_COMMENT = 140;
	
	//Preferences
	public static final String PREF_ACCOUNT = "pref_account";
	public static final String PREF_ACCOUNT_NAME = "pref_account_name";
	public static final String PREF_DEFAULT_ACCOUNT_NAME = "Votre nom";
	public static final String PREF_ACCOUNT_EMAIL = "pref_account_email";
	public static final String PREF_DEFAULT_ACCOUNT_EMAIL = "Votre email";
	public static final String PREF_ACCOUNT_FOLLOWED = "";
	public static final String PREF_DEFAULT_ACCOUNT_FOLLOWED = "";
	public static final String PREF_ACCOUNT_FOLLOWERS = "";
	public static final String PREF_DEFAULT_ACCOUNT_FOLLOWERS = "";

	/************************ METHODS ******************/
	/*
	 * Name [can edit] email address [can edit] Name and/or email address of
	 * people "followed" [can edit] Name and/or email address of "followers"
	 * [can NOT edit]
	 */

	// SET preferences element
	public void setAccountName(SharedPreferences preferences, String value)
	{

		((SharedPreferences) preferences).edit();
		((Editor) preferences).putString(PREF_ACCOUNT_NAME, value);
		((Editor) preferences).commit();
	}

	public void setAccountEmail(SharedPreferences preferences, String value)
	{

		((SharedPreferences) preferences).edit();
		((Editor) preferences).putString(PREF_ACCOUNT_EMAIL, value);
		((Editor) preferences).commit();
	}

	public void setAccountFollowed(SharedPreferences preferences, String value)
	{

		((SharedPreferences) preferences).edit();
		((Editor) preferences).putString(PREF_ACCOUNT_FOLLOWED, value);
		((Editor) preferences).commit();
	}

	public void setAccountFollowers(SharedPreferences preferences, String value)
	{

		((SharedPreferences) preferences).edit();
		((Editor) preferences).putString(PREF_ACCOUNT_FOLLOWERS, value);
		((Editor) preferences).commit();
	}

	// GET preferences element
	public String getAccountName(SharedPreferences preferences)
	{

		return preferences.getString(PREF_ACCOUNT_NAME,
				PREF_DEFAULT_ACCOUNT_NAME);

	}

	public String getAccountEmail(SharedPreferences preferences)
	{

		return preferences.getString(PREF_ACCOUNT_EMAIL,
				PREF_DEFAULT_ACCOUNT_EMAIL);

	}

	public String getAccountFollowed(SharedPreferences preferences)
	{

		return preferences.getString(PREF_ACCOUNT_FOLLOWED,
				PREF_DEFAULT_ACCOUNT_FOLLOWED);

	}

	public String getAccountFollowers(SharedPreferences preferences)
	{

		return preferences.getString(PREF_ACCOUNT_FOLLOWERS,
				PREF_DEFAULT_ACCOUNT_FOLLOWERS);

	}

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
