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

	// URI of content provider, 
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.geojir.db.mediacontentprovider");
	
	// Mime of content provider, start is always the same
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
		long id = getId(uri);
		SQLiteDatabase db = database.getReadableDatabase();
		if (id < 0)
		{
			return db.query(MediasDb.TABLE_NAME, projection, selection,
					selectionArgs, null, null, sortOrder);
		} else
		{
			return db.query(MediasDb.TABLE_NAME, projection, MediasDb._ID + "="
					+ id, null, null, null, null);
		}

	}

	public Cursor query(Uri uri, String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit)
	{

		long id = getId(uri);
		SQLiteDatabase db = database.getReadableDatabase();
		if (id < 0)
		{
			return db.query(MediasDb.TABLE_NAME, columns, selection,
					selectionArgs, groupBy, having, orderBy, limit);

		} else
		{
			return db.query(MediasDb.TABLE_NAME, columns, MediasDb._ID + "="
					+ id, selectionArgs, groupBy, having, orderBy, limit);
		}
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
			long id = db.insertOrThrow(MediasDb.TABLE_NAME, null, values);

			if (id == -1)
			{
				throw new RuntimeException(String.format(
						"%s : Failed to insert [%s] for unknown reasons.",
						"MediaProvider", values, uri));
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
		long id = getId(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		try
		{
			if (id < 0)
				return db.delete(MediasDb.TABLE_NAME, selection, selectionArgs);
			else
				return db.delete(MediasDb.TABLE_NAME, MediasDb._ID + "=" + id,
						selectionArgs);
		} finally
		{
			db.close();
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		long id = getId(uri);
		SQLiteDatabase db = database.getWritableDatabase();

		try
		{
			if (id < 0)
				return db.update(MediasDb.TABLE_NAME, values, selection,
						selectionArgs);
			else
				return db.update(MediasDb.TABLE_NAME, values, MediasDb._ID
						+ "=" + id, null);
		} finally
		{
			db.close();
		}
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
