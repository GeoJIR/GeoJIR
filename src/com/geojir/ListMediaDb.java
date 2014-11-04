package com.geojir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Database for list media
public class ListMediaDb extends SQLiteOpenHelper implements Observable.OnSubscribe<Map<String, String>>
{
	protected Subscriber<? super Map<String, String>> subscriber;
	// DATABASE
	private static final int DATABASE_VERSION = 1;
	private static final String LISTMEDIA_DATABASE_NAME = "ListMedia.db";
	private static final String LISTMEDIA_TABLE_NAME = "ListMedia";
	private static final int NB_LIST_LAST_MEDIA = 10;
	
	private static final String KEY_ID = "id";
	private static final String KEY_FILE_NAME = "Filename";
	private static final String KEY_REMARK = "Remark";
	private static final String KEY_FILTER = "Filter";
	
	private static final String LISTMEDIA_TABLE_CREATE = "CREATE TABLE "
			+ LISTMEDIA_TABLE_NAME + " (" + KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_FILE_NAME + " TEXT, "
			+ KEY_REMARK + " TEXT, " + KEY_FILTER + " NUMERIC)" + ";";
	
	private static final String SQL_SELECT_ENTRIES = "SELECT * FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	protected SQLiteDatabase db;
	
	private static final String SQL_COUNT_TABLE = "SELECT COUNT(*) FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	public ListMediaDb(Context context)
	{
		super(context, LISTMEDIA_DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	protected void openDb()
	{
		db = this.getWritableDatabase();
	}
	
	protected void closeDb()
	{
		if (db != null)
			db.close();
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
		db.delete(LISTMEDIA_TABLE_NAME, null, null);
		// TODO drop table
		// onCreate(db);
	}
		
	/**
	 * @param pathFileName
	 * @param remark
	 * @param filter
	 * @param db
	 */
	public void addMedia(String pathFileName, String remark, Boolean filter)
	{
		openDb();
		// return datebase's count entries
		int nbEntries = countEntries();
		
		if (nbEntries == NB_LIST_LAST_MEDIA || nbEntries < NB_LIST_LAST_MEDIA)
		{
			// add a new entry then delete the first entry of the database
			addEntry(pathFileName, remark, filter);
			if (nbEntries == NB_LIST_LAST_MEDIA)
			{
				deleteFirstEntry();
			}
		} else
		{
			// Error : more than NB_LIST_LAST_MEDIA entries
			// deleting all entries
			db.delete(LISTMEDIA_TABLE_NAME, null, null);
			
			// then add tests entries
			for (int i = 0; i < 10; i++)
			{
				addEntry("NomFichier" + i, "Remarque" + i, filter);
			}
		}
		
		closeDb();
	}
	
	/**
	 * @param db
	 */
	private void deleteFirstEntry()
	{
		Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);
		if (cursor.moveToFirst())
		{
			String whereClause = KEY_ID + "=?";
			String[] whereArgs = new String[] { String.valueOf(cursor.getInt(0)) };
			db.delete(LISTMEDIA_TABLE_NAME, whereClause, whereArgs);
		}
		cursor.close();
	}
	
	/**
	 * Adding a new entry is based on auto-increment
	 * 
	 * @param fileName
	 * @param Remark
	 * @param db
	 */
	private void addEntry(String pathFileName, String remark, Boolean filter)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_FILE_NAME, pathFileName);
		values.put(KEY_REMARK, remark);
		values.put(KEY_FILTER, filter);
		db.insert(LISTMEDIA_TABLE_NAME, null, values);
	}
	
	/**
	 * @param db
	 * @return
	 */
	private int countEntries()
	{
		Cursor cursorCount = db.rawQuery(SQL_COUNT_TABLE, null);
		cursorCount.moveToFirst();
		int nbEntries = cursorCount.getInt(0);
		cursorCount.close();
		return nbEntries;
	}
	
	/**
	 * Fill the arraylist for the ListView
	 * 
	 * @param db
	 * @return
	 */
	public ArrayList<Map<String, String>> getAllMedias()
	{
		openDb();
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		
		Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);
		
		if (cursor.moveToFirst())
		{
			do
			{
				HashMap<String, String> item = createItem(cursor.getString(1), cursor.getString(2));
				list.add(item);
				if (subscriber != null)
					subscriber.onNext(item);
			}
			while (cursor.moveToNext());
		}
		
		closeDb();		
		return list;
	}
	
	/**
	 * Add 1 entry in the arraylist for the ListView
	 * 
	 * @param pathFileName
	 * @param remark
	 * @return
	 */
	private HashMap<String, String> createItem(String pathFileName, String remark)
	{
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("pathFileName", pathFileName);
		item.put("remark", remark);
		return item;
	}

	@Override
	public void call(Subscriber<? super Map<String, String>> newSubscriber)
	{
		subscriber = newSubscriber;
		getAllMedias();
	}

}
