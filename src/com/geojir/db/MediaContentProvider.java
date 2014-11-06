package com.geojir.db;

import com.geojir.db.ListMediaContract.MediasDb;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MediaContentProvider extends ContentProvider
{
	// database
	private ListMediaDb database;

	// URI de notre content provider, elle sera utilisé pour accéder au
	// ContentProvider
	public static final Uri CONTENT_URI = Uri.parse("content://com.geojir.db");
	// Le Mime de notre content provider, la premiére partie est toujours
	// identique
	public static final String CONTENT_PROVIDER_MIME = "vnd.android.cursor.item/vnd.com.geojir.db.MediasDb";

	@Override
	public boolean onCreate()
	{
		database = new ListMediaDb(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		return null;
	}

	@Override
	public String getType(Uri uri)
	{
		return CONTENT_PROVIDER_MIME;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		SQLiteDatabase db = database.getWritableDatabase();
		try
		{
			long id = db.insertOrThrow(
					MediasDb.TABLE_NAME, null,
					values);

			if (id == -1)
			{
				throw new RuntimeException(String.format(
						"%s : Failed to insert [%s] for unknown reasons.",
						"TutosAndroidProvider", values, uri));
			} else
			{
				return ContentUris.withAppendedId(uri, id);
			}

		} finally
		{
			db.close();
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		return 0;
	}

	private long getId(Uri uri)
	{
		String lastPathSegment = uri.getLastPathSegment();
		if (lastPathSegment != null)
		{
			try
			{
				return Long.parseLong(lastPathSegment);
			} catch (NumberFormatException e)
			{
				Log.e("MediaContentProvider", "Number Format Exception : " + e);
			}
		}
		return -1;
	}
}
