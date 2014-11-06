package com.geojir.medias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;

import com.geojir.Constants;
import com.geojir.ListMediaDb;
import com.geojir.ParentMenuActivity;
import com.geojir.interfaces.IFiles;
import com.geojir.override.OneLineArrayList;

/**
 * @author HumanBooster
 *
 */
/**
 * @author HumanBooster
 *
 */
public abstract class Media implements IFiles
{
	// Comment of the media
	public String comment = "";
	
	protected File file;
	
	public Media()
	{
		// create master folder if not exist
		Constants.initConstants(ParentMenuActivity.CONTEXT);
	}
	
	// Create File with unique path
	protected void createFile()
	{
		// create master folder if not exist
		Constants.initConstants(ParentMenuActivity.CONTEXT);
		
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		String fileName = getType() + "_" + timeStamp;
		
		// Create file on memory
		File storageDir = Environment
				.getExternalStoragePublicDirectory(getDir());
		try
		{
			file = File.createTempFile(fileName, getExt(), storageDir);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// Create real file from temp file
	protected void copyFile() throws IOException
	{
		File file_temp  = new File(getTempPath());
	    InputStream in = new FileInputStream(file_temp);
	    OutputStream out = new FileOutputStream(file);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0)
	    {
	        out.write(buf, 0, len);
	    }
	    
	    // Close and delete
	    in.close();
	    out.close();
	}
	
	// Return current path or tempPath if media not save
	public String getPath()
	{
		if (file != null)
			return file.getPath();
		else
			return getTempPath();
	}
	
	// Create temp path depend on media
	protected String getTempPath()
	{
		File storageDir = Environment
				.getExternalStoragePublicDirectory(getDir());
		String retour = storageDir.getAbsolutePath() + "/temp" + getExt();
		
		return retour;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	// Create file from temp file and save comment
	public void save(String commentary) throws IOException, InstantiationException, IllegalAccessException
	{
		if (file == null)
		{
			createFile();
			copyFile();
		}
		
		comment = commentary;
		deleteTempFile();
		callToDataBase();
	}
	
	// Call save to Database
	protected void callToDataBase()
	{
		ListMediaDb listMedia = new ListMediaDb(ParentMenuActivity.CONTEXT);
		listMedia.addMedia(getPath(), comment, false);
	}
	
	// Restore media if needed (screen rotate)
	public void restore(String restoreURI)
	{
		// create master folder if not exist
		Constants.initConstants(ParentMenuActivity.CONTEXT);
		if (restoreURI != getTempPath())
			file = new File(restoreURI);
	}
	
	// Delete temp file of each media
	public static void deleteTempFile() throws InstantiationException, IllegalAccessException
	{
		// Create list of media with temp file
		OneLineArrayList<Class<? extends Media>> classList
			= new OneLineArrayList<Class<? extends Media>>()
				.put(Photo.class)
				.put(Sound.class);
		
		// Delete temp file
		for (int i=0; i<classList.size(); i++)
		{
			Media mediaTemp = (Media) classList.get(i).newInstance();
			File delFile = new File(mediaTemp.getTempPath());
			if (delFile.exists())
				delFile.delete();
		}
	}
}
