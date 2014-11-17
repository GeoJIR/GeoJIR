package com.geojir.db;

import rx.Observable;
import rx.Subscriber;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.SimpleCursorAdapter;

import com.geojir.Constants;
import com.geojir.db.ListMediaContract.MediasDb;
import com.geojir.memory.DBMemory;

//Database for list media
public class ListMediaDb extends SQLiteOpenHelper implements
		Observable.OnSubscribe<Cursor>
{
	protected Subscriber<? super Cursor> subscriber;
	public static final String CURSOR_MEMORY = "ListMediaDbCursorMemory";

	protected SQLiteDatabase db;

	public static final int NB_LIST_LAST_MEDIA = 10;

	private static final String LISTMEDIA_TABLE_CREATE = "CREATE TABLE "
			+ MediasDb.TABLE_NAME + " (" + MediasDb._ID
			+ " INTEGER PRIMARY KEY," + MediasDb.FILE_NAME_COLUMN + " TEXT, "
			+ MediasDb.REMARK_COLUMN + " TEXT, " + MediasDb.FILTER_COLUMN
			+ " NUMERIC," + MediasDb.LATITUDE_COLUMN + " REAL,"
			+ MediasDb.LONGITUDE_COLUMN + " REAL" + ");";

	private static final String LISTMEDIA_SELECT_ENTRIES = "SELECT * FROM "
			+ MediasDb.TABLE_NAME;

	private static final String LISTMEDIA_DROP_TABLE = "DROP TABLE "
			+ MediasDb.TABLE_NAME;
	
	protected SimpleCursorAdapter cursorAdapter;
	

	public ListMediaDb(Context context)
	{
		super(context, Constants.DATABASE_NAME, null,
				Constants.DATABASE_VERSION);
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

			cursor = db.rawQuery(LISTMEDIA_SELECT_ENTRIES, null);

			DBMemory.setCursor(CURSOR_MEMORY, cursor);
		}

		if (subscriber != null)
			subscriber.onNext(cursor);
	}

	public void addMedia(String pathFileName, String remark, Boolean filter)
	{
		DBMemory.closeDb(CURSOR_MEMORY);
		db = DBMemory.setDb(CURSOR_MEMORY, this);

		// add a new entry then delete the first entry of the database
		addEntry(pathFileName, remark, filter);

		DBMemory.closeDb(CURSOR_MEMORY);
		
		
		
	}

	private void addEntry(String pathFileName, String remark, Boolean filter)
	{
		ContentValues values = new ContentValues();

		// values.put(MediasDb._ID, id);
		values.put(MediasDb.FILE_NAME_COLUMN, pathFileName);
		values.put(MediasDb.REMARK_COLUMN, remark);
		values.put(MediasDb.FILTER_COLUMN, filter);
		values.put(MediasDb.LATITUDE_COLUMN, Constants.GM_LATITUDE);
		values.put(MediasDb.LONGITUDE_COLUMN, Constants.GM_LONGITUDE);
		db.insert(MediasDb.TABLE_NAME, null, values);
	}

	@Override
	public void call(Subscriber<? super Cursor> newSubscriber)
	{
		subscriber = newSubscriber;
		getCursorMedias();
	}
}
