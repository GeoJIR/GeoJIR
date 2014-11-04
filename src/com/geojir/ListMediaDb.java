package com.geojir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.geojir.ListMediaContract.MediasDb;

//Database for list media
public class ListMediaDb extends SQLiteOpenHelper
{

	// DATABASE
	private static final int DATABASE_VERSION = 1;
	// private static final String LISTMEDIA_TABLE_NAME = "ListMedia";
	private static final int NB_LIST_LAST_MEDIA = 10;

	/*
	 * private static final String KEY_ID = "_id"; private static final String
	 * KEY_FILE_NAME = "Filename"; private static final String KEY_REMARK =
	 * "Remark"; private static final String KEY_FILTER = "Filter";
	 */

	private static final String LISTMEDIA_TABLE_CREATE = "CREATE TABLE " + 
			MediasDb.TABLE_NAME + " (" + 
			MediasDb._ID + " INTEGER PRIMARY KEY," + 
			MediasDb.FILE_NAME_COLUMN + " TEXT, "+ 
			MediasDb.REMARK_COLUMN + " TEXT, " + 
			MediasDb.FILTER_COLUMN + " NUMERIC)" + ";";

	private static final String SQL_SELECT_ENTRIES = "SELECT * FROM "
			+ MediasDb.TABLE_NAME;

	/*
	 * Not use for now private static final String SQL_DELETE_ENTRIES =
	 * "DELETE FROM " + LISTMEDIA_TABLE_NAME;
	 * 
	 * private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " +
	 * LISTMEDIA_TABLE_NAME;
	 */

	private static final String SQL_COUNT_TABLE = "SELECT COUNT(*) FROM "
			+ MediasDb.TABLE_NAME;

	public ListMediaDb(Context context)
	{
		super(context, MediasDb.TABLE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(LISTMEDIA_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.delete(MediasDb.TABLE_NAME, null, null);
		onCreate(db);
	}

	/**
	 * @param db
	 * @return
	 */
	public ArrayList<Map<String, String>> getAllMedias()
	{
		SQLiteDatabase db = this.getWritableDatabase();

		// pour les tests
		// Cursor cursorDrop = db.rawQuery(SQL_DELETE_TABLE, null);
		// Cursor cursorDrop = db.rawQuery(SQL_DELETE_ENTRIES, null);
		// cursorDrop.close();

		// Test nb entries
		testNbEntries(db);

		ArrayList<Map<String, String>> mediaList = buildData(db);

		return mediaList;
	}

	/**
	 * @param db
	 */
	public void testNbEntries(SQLiteDatabase db)
	{
		// on récupère le nombre d'entrées dans la base
		int nbEntries = countEntries(db);
		Boolean filter = false;

		if (nbEntries == NB_LIST_LAST_MEDIA)
		{
			// BOF : pour les tests
			int lastentry = lastEntry(db);
			lastentry += 1;

			// addMedia(lastentry, "NomFichier"+lastentry, "Remarque"+lastentry,
			// filter, db);
			addMedia(Constants.PATH_IMAGE + "/picture01.jpg", "Remarque"
					+ lastentry, filter, db);
			// EOF : pour les tests
		} else
		{
			if (nbEntries > NB_LIST_LAST_MEDIA)
			{
				// on a plus de X entrées, on les supprime toutes
				db.delete(MediasDb.TABLE_NAME, null, null);
			}
			if (!(nbEntries < NB_LIST_LAST_MEDIA && nbEntries > 0))
			{
				// puis on en rajoute X pour les test
				for (int i = 0; i < 10; i++)
				{
					// addMedia(i, "NomFichier"+i, "Remarque"+i, filter, db);
					addMedia(Constants.PATH_IMAGE + "/picture01.jpg",
							"Remarque" + i, filter, db);
				}
			}
		}
	}

	/**
	 * @param pathFileName
	 * @param remark
	 * @param filter
	 * @param db
	 */
	private void addMedia(String pathFileName, String remark, Boolean filter,
			SQLiteDatabase db)
	{
		// return datebase's count entries
		int nbEntries = countEntries(db);

		if (nbEntries == NB_LIST_LAST_MEDIA || nbEntries < NB_LIST_LAST_MEDIA)
		{
			// add a new entry then delete the first entry of the database
			addEntry(pathFileName, remark, filter, db);
			if (nbEntries == NB_LIST_LAST_MEDIA)
			{
				deleteFirstEntry(db);
			}
		} else
		{
			// Error : more than NB_LIST_LAST_MEDIA entries
			// deleting all entries
			db.delete(MediasDb.TABLE_NAME, null, null);

			// then add tests entries
			for (int i = 0; i < 10; i++)
			{
				addEntry("NomFichier" + i, "Remarque" + i, filter, db);
			}
		}
	}

	/**
	 * @param db
	 */
	private void deleteFirstEntry(SQLiteDatabase db)
	{
		Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);
		if (cursor.moveToFirst())
		{
			int key = cursor.getInt(0);
			
			db.delete(MediasDb.TABLE_NAME, "'" + MediasDb._ID + "' = " + key, null);
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
	private void addEntry(String pathFileName, String remark, Boolean filter,
			SQLiteDatabase db)
	{
		ContentValues values = new ContentValues();

		// values.put(MediasDb._ID, id);

		values.put(MediasDb.FILE_NAME_COLUMN, pathFileName);
		values.put(MediasDb.REMARK_COLUMN, remark);
		values.put(MediasDb.FILTER_COLUMN, filter);
		db.insert(MediasDb.TABLE_NAME, null, values);
	}

	/**
	 * @param db
	 * @return
	 */
	private int countEntries(SQLiteDatabase db)
	{
		Cursor cursorCount = db.rawQuery(SQL_COUNT_TABLE, null);
		cursorCount.moveToFirst();
		int nbEntries = cursorCount.getInt(0);
		cursorCount.close();
		return nbEntries;
	}

	/**
	 * @param db
	 * @return
	 */
	private int lastEntry(SQLiteDatabase db)
	{
		Cursor cursorLast = db.rawQuery(SQL_SELECT_ENTRIES, null);
		cursorLast.moveToLast();
		int lastEntry = cursorLast.getInt(0);
		cursorLast.close();
		return lastEntry;
	}

	/**
	 * Fill the arraylist for the ListView
	 * 
	 * @param db
	 * @return
	 */
	private ArrayList<Map<String, String>> buildData(SQLiteDatabase db)
	{

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);

		if (cursor.moveToFirst())
		{
			do
			{
				list.add(putData(cursor.getString(0), cursor.getString(1)));
			} while (cursor.moveToNext());
		}

		db.close();

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
