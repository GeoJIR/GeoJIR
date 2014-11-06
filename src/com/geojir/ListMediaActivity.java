package com.geojir;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.geojir.ListMediaContract.MediasDb;

public class ListMediaActivity extends ParentMenuActivity
{
	@InjectView(R.id.emptyListTextView)
	protected TextView emptyListTextView;
	@InjectView(R.id.listViewMedias)
	protected ListView listView;
	protected SimpleCursorAdapter cursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);
		ButterKnife.inject(this);
		updateChildrenVisibility();
		
		// database instantiate
		ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());
		
		// Create observation of sql request
		Observable.create(listeMedia)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Action1<Cursor>()
			{
				@Override
				public void call(Cursor cursor)
				{
					// display results
					createAdapter(cursor);
					displayList();
				}
			});
			
			listeMedia.getCursorMedias();
	}
	
	// Create custom adapter
	protected void createAdapter(Cursor cursor)
	{
		// get icon's dimension
		final int thumbnailSize = getResources().getDimensionPixelOffset(R.dimen.thumbnailSize);
		
		// Display image and comment
		cursorAdapter = new SimpleCursorAdapter(this,
				R.layout.list_item,
				cursor,
				new String[] { MediasDb.FILE_NAME_COLUMN, MediasDb.REMARK_COLUMN },
				new int[] {R.id.imageIcon, R.id.remark}
				, 0
		);
		
		updateChildrenVisibility();
		
		// Convert String to image for ImageView
		cursorAdapter.setViewBinder(new ViewBinder()
		{
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex)
			{
				if (view instanceof ImageView)
				{
					ImageView imageView = (ImageView) view;
					
					// Path of media
					String path = cursor.getString(columnIndex);
					File file = new File(path);
					// display image if exist
					if (path.endsWith(Constants.EXT_IMAGE) && file.exists())
					{
						/////////////////////////////////////////
						// MEGA Boilerplate parce que pas le temp
						
						// Get the dimensions of the View
						int targetW = thumbnailSize;
						int targetH = thumbnailSize;
						
						// Get the dimensions of the bitmap
						BitmapFactory.Options bmOptions = new BitmapFactory.Options();
						bmOptions.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(file.getPath(), bmOptions);
						int photoW = bmOptions.outWidth;
						int photoH = bmOptions.outHeight;

						// Determine how much to scale down the image
						int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

						// Decode the image file into a Bitmap sized to fill the View
						bmOptions.inJustDecodeBounds = false;
						bmOptions.inSampleSize = scaleFactor;
						
						// Load resized image
						Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), bmOptions);
						imageView.setImageBitmap(bitmap);
						/////////////////////////////////////////
					}
					else if (path.endsWith(Constants.EXT_AUDIO) && file.exists())
						// else display default
						imageView.setImageResource(R.drawable.ic_music);
					else
						// else display default
						imageView.setImageResource(R.drawable.ic_medias);
					
					return true;
				}
				
				return false;
			}
			
		});
	}
	
	protected void updateChildrenVisibility()
	{
		if (cursorAdapter == null || cursorAdapter.isEmpty())
		{
			listView.setVisibility(View.INVISIBLE);
			emptyListTextView.setVisibility(View.VISIBLE);
		}
		else
		{
			listView.setVisibility(View.VISIBLE);
			emptyListTextView.setVisibility(View.INVISIBLE);
		}
	}

	protected void displayList()
	{
		// Clear old items
		listView.setAdapter(null);
		// Display new item list
		listView.setAdapter(cursorAdapter);
	}
}
