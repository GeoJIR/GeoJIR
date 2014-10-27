package com.geojir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class Constants {
	
    //Document file
    public static final String FILE_MEDIA_LIST_NAME = "geojir_media_list.csv";

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
	public static final String EXT_AUDIO = "3gp";
	public static final String EXT_IMAGE = "jpg";
	
	//Intent Photo
	public static final int REQUEST_CODE = 1888;
	
	//Divers
	public static final int NB_LIST_LAST_MEDIA = 10;
	public static final int NB_CAR_COMMENT = 140;
	
	//Tests
	private static final String TEXT_CSV = ""+
	"Music/music001.3gp;Super morceau\n"+
	"Music/music002.3gp;Bon morceau\n"+
	"Video/video001.mp4;Super film\n"+
	"Music/music004.3gp;Morceau extraordinaire\n"+
	"Video/video002.3gp;Video int�ressante\n"+
	"Video/video003.3gp;Super morceau";

	//Methods
	// Checks if external storage is available for read and write
	private static boolean isExternalStorageWritable() 
	{
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
	
	public static void initConstants() throws IOException
	{
		// Get the directory for the user's public pictures directory. 
//		File file = Environment.getRootDirectory();
//		toto = getExternalFilesDir("");
		
		
		
		//v�rification d'existence des sous r�p de stockage (video, audio, image et documents) 
		if(extStorage())
		{
		    //look for main directory
			File file = new File(Environment.getExternalStoragePublicDirectory(PATH_GEOJIR), "" );
		    
		    if(!file.exists()) 
		    {
			    //Main directory not created, we'll create  all directories
		    	for(int i=0; i<listPath.length; i++) 
		    	{
		    		file = new File(Environment.getExternalStoragePublicDirectory(listPath[i]), "" );
		    	    if(!file.mkdirs())
		    	        Log.e("ExternalStorage "+listPath[i], "Directory not created ");
		    	}
		    }
		    
	    	// TODO : if file listmedia not exists, we'll create it
		    file = new File(Environment.getExternalStoragePublicDirectory(PATH_GEOJIR), FILE_MEDIA_LIST_NAME );
		    
		    if(!file.exists()) 
		    {
    	        Log.i(FILE_MEDIA_LIST_NAME, "File not exists ");
		    	try {
					if(!file.createNewFile())
					    Log.e(FILE_MEDIA_LIST_NAME, "File not created ");
					else
					{
					    Log.i(FILE_MEDIA_LIST_NAME, "File created ");
						
						FileWriter writer = new FileWriter(file); 
						// Writes the content to the file
						writer.write(TEXT_CSV); 
						writer.flush();
						writer.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		    else
		    {
		    	//on va lire le fichier
		    }
		}
		else
		{
		    File filer = new File(Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_MOVIES), PATH_GEOJIR );
		    
		    if (!filer.mkdirs()) {
		        Log.e(PATH_GEOJIR, "Directory not created");
		    }
		}
	}	
}
