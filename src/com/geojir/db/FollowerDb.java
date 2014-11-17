package com.geojir.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.geojir.db.FollowerContract.FollowDb;

public class FollowerDb extends SQLiteOpenHelper
{

	private static final int DATABASE_VERSION = 1;

	public FollowerDb(Context context)
	{
		super(context,  com.geojir.Constants.DATABASE_NAME, null,  com.geojir.Constants.DATABASE_VERSION);
	}
	
	public FollowerDb(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, name, factory, version);
	}

	private static final String FOLLOWER_TABLE_CREATE = "CREATE TABLE "
			+ FollowDb.FOLLOWER_TABLE_NAME + " (" +
			// KEY_ID + " INTEGER PRIMARY KEY," +
			FollowDb.FOLLOWER_IDENTIFIER + " TEXT)" + ";";

	private static final String SQL_SELECT_ENTRIES = "SELECT * FROM "
			+ FollowDb.FOLLOWER_TABLE_NAME;

	private static final String SQL_COUNT_TABLE = "SELECT COUNT(*) FROM "
			+ FollowDb.FOLLOWER_TABLE_NAME;

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(FOLLOWER_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.delete(FollowDb.FOLLOWER_TABLE_NAME, null, null);
		onCreate(db);
	}

	/**
	 * Adding a new entry is based on auto-increment
	 * 
	 * @param ident
	 * @param db
	 */
	private void addEntry(Integer ident, SQLiteDatabase db)
	{
		ContentValues values = new ContentValues();
		values.put(FollowDb.FOLLOWER_IDENTIFIER, ident);
		db.insert(FollowDb.FOLLOWER_TABLE_NAME, null, values);
	}
	
	/**
	 * @param db
	 * @return
	 */
	public ArrayList<Integer> getAllFollower()
	{
		SQLiteDatabase db = this.getWritableDatabase();

		// Test nb entries
		//test(db);

		ArrayList<Integer> mediaList = buildData(db);

		return mediaList;
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
		// TODO changez, ne pas tout recuperer pour une seule entrée
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
	private ArrayList<Integer> buildData(SQLiteDatabase db)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();

		Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);

		if (cursor.moveToFirst())
		{
			do
			{
				list.add(cursor.getInt(0));
			} while (cursor.moveToNext());
		}

		db.close();

		return list;
	}
	
}
