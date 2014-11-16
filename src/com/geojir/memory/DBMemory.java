package com.geojir.memory;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Class for database memory gestion
public class DBMemory
{
	protected static Map<String, Cursor> listCursor = new Hashtable<String, Cursor>();
	protected static Map<String, SQLiteDatabase> listDb = new Hashtable<String, SQLiteDatabase>();
	
	public static SQLiteDatabase getDb(String key)
	{
		if (listDb.containsKey(key))
			return listDb.get(key);
		else
			return null;
	}
	
	public static Cursor getCursor(String key)
	{
		if (listCursor.containsKey(key))
			return listCursor.get(key);
		else
			return null;
	}
	
	public static SQLiteDatabase setDb(String key,
			SQLiteOpenHelper db)
	{
		listDb.put(key, db.getWritableDatabase());
		return listDb.get(key);
	}

	public static Cursor setCursor(String key, Cursor cursor)
	{
		listCursor.put(key, cursor);
		return cursor;
	}
	
	public static void delete(String key)
	{
		if (listCursor.containsKey(key))
			listCursor.remove(key);
		
		if (listCursor.containsKey(key))
		{
			SQLiteDatabase db = listDb.get(key);
			db.close();
			listDb.remove(key);
		}
	}
	
	public static void clear()
	{
		clear("");
	}
	
	public static void clear(String keyRescue)
	{
		Set<String> keys = listDb.keySet();
		for (int i=0; i<keys.size(); i++)
		{
			Iterator<String> iterator = keys.iterator();
			while (iterator != null && iterator.hasNext())
			{
				String entry = iterator.next();
				if (entry != keyRescue)
					closeDb(entry);
			}
		}
		
	}
	
	public static void closeDb(String key)
	{
		listCursor.remove(key);
		
		SQLiteDatabase dbTemp = getDb(key);
		if (dbTemp != null && dbTemp.isOpen())
			dbTemp.close();
		listDb.remove(key);		
	}
	
	/*
	 
 	FUTURE CLASS FOR BD MOMERY GESTION
 	
 	
	public abstract class SQLiteMemoryHelper extends SQLiteOpenHelper
	{
		protected SQLiteDatabase db;
		
		public SQLiteMemoryHelper(Context context, String name,
				CursorFactory factory, int version)
		{
			super(context, name, factory, version);
		}
		
		public SQLiteMemoryHelper(Context context, String name,
				CursorFactory factory, int version,
				DatabaseErrorHandler errorHandler)
		{
			super(context, name, factory, version, errorHandler);
		}
		
		public void openDb()
		{
			if (db == null)
				db = getDB(getUniqueNameReference());
			if (db == null)
				createDB();
		}
		
		protected void createDB()
		{
			db = this.getWritableDatabase();
			setDB(getUniqueNameReference(), db);
		}

		public abstract String getUniqueNameReference();
		
		protected SQLiteDatabase getDB(String key)
		{
			return DBMemory.getDb(key);
		}
		
		protected void setDB(String key, SQLiteDatabase database)
		{
			DBMemory.setDb(key, database);
		}
	}
	*/

}
