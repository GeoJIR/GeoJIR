package com.geojir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Database for list media
public class ListMediaDb extends SQLiteOpenHelper
{
	
	// DATABASE
	protected SQLiteDatabase db;
	
	private static final int DATABASE_VERSION = 1;
	private static final String LISTMEDIA_TABLE_NAME = "ListMedia";
	private static final int NB_LIST_LAST_MEDIA = 10;
	
//	private static final String _ID = "_id";
	private static final String _ID = "id";
	private static final String KEY_FILE_NAME = "Filename";
	private static final String KEY_REMARK = "Remark";
	private static final String KEY_FILTER = "Filter";
	
	private static final String LISTMEDIA_TABLE_CREATE = "CREATE TABLE "
			+ LISTMEDIA_TABLE_NAME + " (" 
			+ _ID +" integer primary key , "
			+ KEY_FILE_NAME + " TEXT, "
			+ KEY_REMARK + " TEXT, " 
			+ KEY_FILTER + " NUMERIC)" + ";";
	
	private static final String LISTMEDIA_SELECT_ENTRIES = "SELECT * FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	private static final String LISTMEDIA_DELETE_ENTRIES = "DELETE FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	private static final String LISTMEDIA_COUNT_TABLE = "SELECT COUNT(*) FROM "
			+ LISTMEDIA_TABLE_NAME;
	
	private static final String LISTMEDIA_DROP_TABLE = "DROP TABLE "
			+ LISTMEDIA_TABLE_NAME;
	
	public ListMediaDb(Context context)
	{
		super(context, LISTMEDIA_TABLE_NAME, null, DATABASE_VERSION);
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
		db.execSQL(LISTMEDIA_DROP_TABLE);
		onCreate(db);
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
		
		closeDb();
	}
	
	/**
	 * @param db
	 */
	private void deleteFirstEntry()
	{
		Cursor cursor = db.rawQuery(LISTMEDIA_SELECT_ENTRIES, null);
		if (cursor.moveToFirst())
		{
			int key = cursor.getInt(0);
			db.delete(LISTMEDIA_TABLE_NAME, _ID +" = " + key, null);
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
		Cursor cursorCount = db.rawQuery(LISTMEDIA_COUNT_TABLE, null);
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
		
		Cursor cursor = db.rawQuery(LISTMEDIA_SELECT_ENTRIES, null);
		
		if (cursor.moveToFirst())
		{
			do
			{
				list.add(putData(cursor.getString(1), cursor.getString(2)));
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
	private HashMap<String, String> putData(String pathFileName, String remark)
	{
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("pathFileName", pathFileName);
		item.put("remark", remark);
		return item;
	}
}
