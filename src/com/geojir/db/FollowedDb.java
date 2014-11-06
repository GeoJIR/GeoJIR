package com.geojir.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.geojir.db.FollowerContract.FollowDb;

public class FollowedDb extends SQLiteOpenHelper
{

	public FollowedDb(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, name, factory, version);
	}

	private static final String FOLLOWED_TABLE_CREATE = "CREATE TABLE "
			+ FollowDb.FOLLOWED_TABLE_NAME + " (" +
			// KEY_ID + " INTEGER PRIMARY KEY," +
			FollowDb.FOLLOWED_IDENTIFIER + " TEXT)" + ";";

	private static final String SQL_SELECT_ENTRIES = "SELECT * FROM "
			+ FollowDb.FOLLOWED_TABLE_NAME;

	private static final String SQL_COUNT_TABLE = "SELECT COUNT(*) FROM "
			+ FollowDb.FOLLOWED_TABLE_NAME;

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(FOLLOWED_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.delete(FollowDb.FOLLOWED_TABLE_NAME, null, null);
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
	
	
	/**
	 * @param db
	 */
	public void test(SQLiteDatabase db)
	{
		// puis on en rajoute X pour les test
		for (int i = 0; i < 10; i++)
		{
			// on ajoute un entrée dans la BDD
			addEntry(i, db);
		}
		
		// on récupère le nombre d'entrées dans la base
		//int nbEntries = countEntries(db);
	}
}
