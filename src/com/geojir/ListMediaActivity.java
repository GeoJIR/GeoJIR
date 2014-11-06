package com.geojir;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.db.ListMediaDb;
import com.geojir.db.MediaContentProvider;

public class ListMediaActivity extends ParentMenuActivity
{
	@InjectView(R.id.listViewMedias)
	protected ListView vue;
	protected SimpleCursorAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);
		ButterKnife.inject(this);

		/*
		// database instantiate
		ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());

		// Create observation of sql request
		Observable.create(listeMedia).observeOn(AndroidSchedulers.mainThread())
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
		*/
		
		displayContentProvider();
	}

	// Create custom adapter
	protected void createAdapter(Cursor cursor)
	{
		// Display image and comment
		cursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item,
				cursor, new String[] { MediasDb.FILE_NAME_COLUMN,
						MediasDb.REMARK_COLUMN }, new int[] { R.id.imageIcon,
						R.id.remark }, 0);

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
						// ///////////////////////////////////////
						// MEGA Boilerplate parce que pas le temp

						// Get the dimensions of the View
						int targetW = imageView.getWidth();
						int targetH = imageView.getHeight();
						if (targetW < 1)
							targetW = 1;
						if (targetH < 1)
							targetH = 1;

						// Get the dimensions of the bitmap
						BitmapFactory.Options bmOptions = new BitmapFactory.Options();
						bmOptions.inJustDecodeBounds = true;
						BitmapFactory.decodeFile(file.getPath(), bmOptions);
						int photoW = bmOptions.outWidth;
						int photoH = bmOptions.outHeight;

						// Determine how much to scale down the image
						int scaleFactor = Math.min(photoW / targetW * 3, photoH
								/ targetH * 3);

						// Decode the image file into a Bitmap sized to fill the
						// View
						bmOptions.inJustDecodeBounds = false;
						bmOptions.inSampleSize = scaleFactor;

						// Load resized image
						Bitmap bitmap = BitmapFactory.decodeFile(
								file.getPath(), bmOptions);
						imageView.setImageBitmap(bitmap);
						// ///////////////////////////////////////
					} else
						// else display default
						imageView.setImageResource(R.drawable.ic_medias);

					return true;
				}

				return false;
			}

		});
	}

	protected void displayList()
	{
		// Clear old items
		vue.setAdapter(null);
		// Display new item list
		vue.setAdapter(cursorAdapter);
	}

	// CONTEN PROVIDER
	private void displayContentProvider()
	{
		String columns[] = new String[] { MediasDb._ID, MediasDb.FILE_NAME_COLUMN,
				MediasDb.REMARK_COLUMN };
		Uri mContacts = MediaContentProvider.CONTENT_URI;
		Cursor cur = managedQuery(mContacts, columns, null, null, null);
		Toast.makeText(ListMediaActivity.this,
				cur.getCount() + "", Toast.LENGTH_LONG).show();

		if (cur.moveToFirst())
		{
			String name = null;
			do
			{
				name = cur.getString(cur.getColumnIndex(MediasDb._ID)) + " "
						+ cur.getString(cur.getColumnIndex(MediasDb.FILE_NAME_COLUMN))
						+ " "
						+ cur.getString(cur.getColumnIndex(MediasDb.REMARK_COLUMN));
				Toast.makeText(this, name + " ", Toast.LENGTH_LONG).show();
			} while (cur.moveToNext());
		}

	}

	private void insertRecords()
	{
		ContentValues contact = new ContentValues();
		contact.put(MediasDb.FILE_NAME_COLUMN, "Android");
		contact.put(MediasDb.REMARK_COLUMN,
				"Introduction à la programmation sous Android");
		getContentResolver().insert(MediaContentProvider.CONTENT_URI, contact);

		contact.clear();
		contact.put(MediasDb.FILE_NAME_COLUMN, "Java");
		contact.put(MediasDb.REMARK_COLUMN, "Introduction à la programmation Java");
		getContentResolver().insert(MediaContentProvider.CONTENT_URI, contact);

		contact.clear();
		contact.put(MediasDb.FILE_NAME_COLUMN, "Iphone");
		contact.put(MediasDb.REMARK_COLUMN, "Introduction à l'objectif C");
		getContentResolver().insert(MediaContentProvider.CONTENT_URI, contact);
	}
}
