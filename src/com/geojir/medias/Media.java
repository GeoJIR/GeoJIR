package com.geojir.medias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.geojir.Constants;
import com.geojir.ParentMenuActivity;
import com.geojir.R;
import com.geojir.db.ListMediaDb;
import com.geojir.db.MediaContentProvider;
import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.interfaces.IFiles;
import com.geojir.override.OneLineArrayList;

public abstract class Media implements IFiles
{
	// Comment of the media
	public String comment = "";
	
	protected File file;
	
	public Media()
	{
		restore(new File(getTempPath()));
	}
	
	public Media(String path)
	{
		this(new File(path));
	}
	
	public Media(URI uri)
	{
		this(new File(uri));
	}
	
	public Media (File mediaFile)
	{
		restore(mediaFile);
	}
	
	// Create File with unique path
	protected String createFile()
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
		
		String retour = file.getPath();
		
		return retour;
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
		save(commentary, false, null);
	}
	
	// Create file from temp file and save comment
	public void save(String commentary, Boolean monochrome, ContentResolver cr) throws IOException, InstantiationException, IllegalAccessException
	{
		String path = null;
		
		if (file == null)
		{
			path = createFile();
			copyFile();
		}
		
		ContentValues values = new ContentValues(); 
        values.put(MediasDb.FILE_NAME_COLUMN, path); 
        values.put(MediasDb.REMARK_COLUMN, commentary);
        values.put(MediasDb.FILTER_COLUMN, monochrome);
        values.put(MediasDb.LATITUDE_COLUMN, Constants.GM_LATITUDE);
        values.put(MediasDb.LONGITUDE_COLUMN, Constants.GM_LONGITUDE);
        
		Uri mediaURI = MediaContentProvider.CONTENT_URI;
		cr.insert(mediaURI, values);
		
		comment = commentary;
		deleteTempFile();
	}
	
	// Call save to Database
	protected void callToDataBase(Boolean monochrome)
	{
		ListMediaDb listMedia = new ListMediaDb(ParentMenuActivity.CONTEXT);
	}
	
	// Restore media if needed (screen rotate)
	protected void restore(File mediaFile)
	{
		// create master folder if not exist
		Constants.initConstants(ParentMenuActivity.CONTEXT);
		if (mediaFile != null)
		if (!mediaFile.getPath().isEmpty() && !mediaFile.getPath().equals(getTempPath()))
			file = mediaFile;
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
	
	// Functions used to launch a media
	// 		- Play music if audio
	// 		- Display image if photo
	public static void launch(URI uri)
	{
		launch(uri.getPath());
	}

	public static void launch(File file)
	{
		launch(file.getPath());
	}

	public static void launch(String filePath)
	{
		launch(filePath, false);
	}

	public static void launch(URI uri, Boolean filterMonochrome)
	{
		launch(uri.getPath(), false);
	}

	public static void launch(File file, Boolean filterMonochrome)
	{
		launch(file.getPath(), false);
	}

	public static void launch(String filePath, Boolean filterMonochrome)
	{
		// Skip launch if media don't exist
		File file = new File(filePath);
		if (!file.exists())
		{
			Toast.makeText(ParentMenuActivity.CONTEXT,
					R.string.media_unknown, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (filePath.endsWith(Constants.EXT_AUDIO))
		{
			Sound sound = new Sound(filePath);
			try
			{
				sound.play();
			}
			catch (IllegalArgumentException | SecurityException
					| IllegalStateException | IOException e)
			{
				e.printStackTrace();
			}
		}
		if (filePath.endsWith(Constants.EXT_IMAGE))
		{
			Photo image = new Photo(filePath);
			image.display(filterMonochrome);
		}
	}
}
