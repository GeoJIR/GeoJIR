package com.geojir.db;

import rx.Subscriber;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.SimpleCursorAdapter;

import com.geojir.Constants;
import com.geojir.db.ListMediaContract.MediasDb;

//Database for list media
public class ListMediaDb extends SQLiteOpenHelper
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

}
