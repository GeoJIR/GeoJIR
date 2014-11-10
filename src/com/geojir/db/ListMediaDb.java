package com.geojir.db;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.geojir.Constants;
import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.memory.DBMemory;

//Database for list media
public class ListMediaDb extends SQLiteOpenHelper implements Observable.OnSubscribe<Cursor>
{
	protected Subscriber<? super Cursor> subscriber;
	public static final String CURSOR_MEMORY = "ListMediaDbCursorMemory";
	
	protected SQLiteDatabase db;
	
	private static final int DATABASE_VERSION = 1;
	private static final String LISTMEDIA_TABLE_NAME = "ListMedia";
	private static final int NB_LIST_LAST_MEDIA = 10;
	
	private static final String LISTMEDIA_SELECT_ENTRIES = "SELECT * FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	private static final String LISTMEDIA_DELETE_ENTRIES = "DELETE FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	private static final String LISTMEDIA_COUNT_TABLE = "SELECT COUNT(*) FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	private static final String LISTMEDIA_DROP_TABLE = "DROP TABLE "
			+ LISTMEDIA_TABLE_NAME;
	
	private static final String LISTMEDIA_TABLE_CREATE = "CREATE TABLE "
			+ MediasDb.TABLE_NAME 
			+ " (" + MediasDb._ID + " INTEGER PRIMARY KEY," 
			+ MediasDb.FILE_NAME_COLUMN + " TEXT, "
			+ MediasDb.REMARK_COLUMN + " TEXT, " 
			+ MediasDb.FILTER_COLUMN + " NUMERIC," 
			+ MediasDb.LATITUDE_COLUMN + " REAL," 
			+ MediasDb.LONGITUDE_COLUMN + " REAL" 
			+ ");";
	
	private static final String SQL_SELECT_ENTRIES = "SELECT * FROM "
			+ MediasDb.TABLE_NAME;

	private static final String SQL_COUNT_TABLE = "SELECT COUNT(*) FROM "
			+ MediasDb.TABLE_NAME;

	public ListMediaDb(Context context)
	{
		super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase newDb)
	{
		db = newDb;
		db.execSQL(LISTMEDIA_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase newDb, int oldVersion, int newVersion)
	{
		db = newDb;
		db.execSQL(LISTMEDIA_DROP_TABLE);
		onCreate(db);
	}

	public void getCursorMedias()
	{
		Cursor cursor = DBMemory.getCursor(CURSOR_MEMORY);
		if (cursor == null)
		{
			db = DBMemory.setDb(CURSOR_MEMORY, this);
			
			cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);
			
			DBMemory.setCursor(CURSOR_MEMORY, cursor);
		}
		
		if (subscriber != null)
			subscriber.onNext(cursor);
	}	

	public void addMedia(String pathFileName, String remark, Boolean filter)
	{
		DBMemory.closeDb(CURSOR_MEMORY);
		db = DBMemory.setDb(CURSOR_MEMORY, this);

		// return datebase's count entries
		int nbEntries = countEntries();

		if (nbEntries == NB_LIST_LAST_MEDIA || nbEntries < NB_LIST_LAST_MEDIA)
		{
			if (nbEntries == NB_LIST_LAST_MEDIA)
			{
				deleteFirstEntry();
			}
		} else
		{
			// Error : more than NB_LIST_LAST_MEDIA entries
			// deleting all entries
			db.execSQL(LISTMEDIA_DELETE_ENTRIES);
		}
		
		// add a new entry then delete the first entry of the database
		addEntry(pathFileName, remark, filter);

		DBMemory.closeDb(CURSOR_MEMORY);
	}

	private void deleteFirstEntry()
	{
		Cursor cursor = db.rawQuery(LISTMEDIA_SELECT_ENTRIES, null);
		if (cursor.moveToFirst())
		{
			String whereClause = "'" + MediasDb._ID + "'=?";
			String[] whereArgs = new String[] { String.valueOf(cursor.getInt(0)) };
			db.delete(MediasDb.TABLE_NAME, whereClause, whereArgs);
		}
		cursor.close();
	}

	private void addEntry(String pathFileName, String remark, Boolean filter)
	{
		ContentValues values = new ContentValues();
		
		// values.put(MediasDb._ID, id);
		// TODO : .... resolve next problem
//		float savedLatitude = preferences.getFloat(Constants.PREF_LOCATION_LATITUDE,0.0f);
//		float savedLongitude = preferences.getFloat(Constants.PREF_LOCATION_LONGITUDE,0.0f);
		float savedLatitude = 43.0f;
		float savedLongitude = 3.5f;

		values.put(MediasDb.FILE_NAME_COLUMN, pathFileName);
		values.put(MediasDb.REMARK_COLUMN, remark);
		values.put(MediasDb.FILTER_COLUMN, filter);
		values.put(MediasDb.LATITUDE_COLUMN, savedLatitude);
		values.put(MediasDb.LONGITUDE_COLUMN, savedLongitude);
		db.insert(MediasDb.TABLE_NAME, null, values);
	}

	private int countEntries()
	{
		Cursor cursorCount = db.rawQuery(LISTMEDIA_COUNT_TABLE, null);
		cursorCount.moveToFirst();
		int nbEntries = cursorCount.getInt(0);
		cursorCount.close();
		
		return nbEntries;
	}
	
	@Override
	public void call(Subscriber<? super Cursor> newSubscriber)
	{
		subscriber = newSubscriber;
		getCursorMedias();
	}
}
