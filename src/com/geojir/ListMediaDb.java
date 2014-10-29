package com.geojir;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Database for list media
public class ListMediaDb extends SQLiteOpenHelper {

  private static final int DATABASE_VERSION = 1;
  private static final String LISTMEDIA_TABLE_NAME = "ListMedia";
  private static final int NB_LIST_LAST_MEDIA = 10;

  private static final String KEY_ID = "id";
  private static final String KEY_FILE_NAME = "Filename";
  private static final String KEY_REMARK = "Remark";
  
  private static final String LISTMEDIA_TABLE_CREATE =
          "CREATE TABLE " + LISTMEDIA_TABLE_NAME + " (" +
//          "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
          KEY_ID + " INTEGER, " +
          KEY_FILE_NAME + " TEXT, " +
          KEY_REMARK + " TEXT);";

  private static final String SQL_SELECT_ENTRIES =
          "SELECT * FROM " + LISTMEDIA_TABLE_NAME;

  private static final String SQL_DELETE_ENTRIES =
          "DELETE FROM " + LISTMEDIA_TABLE_NAME;

  private static final String SQL_DELETE_TABLE =
	  		"DROP TABLE IF EXISTS " + LISTMEDIA_TABLE_NAME;
	    
  private static final String SQL_COUNT_TABLE =
	  		"SELECT COUNT(*) FROM " + LISTMEDIA_TABLE_NAME;
	    
  public ListMediaDb(Context context) {
      super(context, LISTMEDIA_TABLE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
      db.execSQL(LISTMEDIA_TABLE_CREATE);
  }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	       db.delete(LISTMEDIA_TABLE_NAME, null, null);
	       onCreate(db);
	}
	
	/**
	 * @param db
	 * @return
	 */
	public ArrayList<Map<String, String>> getAllMedias() {
		SQLiteDatabase db = this.getWritableDatabase();

	    //pour les tests
//		Cursor cursorDrop = db.rawQuery(SQL_DELETE_TABLE, null);
//		Cursor cursorDrop = db.rawQuery(SQL_DELETE_ENTRIES, null);
//	    cursorDrop.close();
	    
		//Test nb entries
		testNbEntries(db);
		
		ArrayList<Map<String, String>> mediaList = buildData(db);
		
	    return mediaList;
	}
	
	/**
	 * @param db
	 */
	public void testNbEntries(SQLiteDatabase db)
	{
	    //on récupère le nombre d'entrées dans la base
	    int nbEntries = countEntries(db);
	    
	    if(nbEntries == NB_LIST_LAST_MEDIA )
	    {
	    	//BOF : pour les tests
	    	int lastentry = lastEntry(db);
	    	lastentry += 1;
	    	addMedia(lastentry, "NomFichier"+lastentry, "Remarque"+lastentry, db);
	    	//EOF : pour les tests
	    }
	    else 
	    {
	    	if(nbEntries > NB_LIST_LAST_MEDIA )
	    	{
		    	//on a plus de X entrées, on les supprime toutes
			    db.delete(LISTMEDIA_TABLE_NAME, null, null);
	    	}
	    	if(!(nbEntries < NB_LIST_LAST_MEDIA && nbEntries > 0))
	    	{
				//puis on en rajoute X pour les test
		    	for(int i=0; i<10; i++)
				{
		    		addMedia(i, "NomFichier"+i, "Remarque"+i, db);
//		    		addMedia(i, Constants.PATH_IMAGE+"/picture01.jpg", "Remarque"+i, db);
				}
	    	}
	    }
	}

	/**
	 * @param key
	 * @param fileName
	 * @param Remark
	 * @param db
	 */
	private void addMedia(int key, String fileName, String Remark, SQLiteDatabase db)
	{
	    //return datebase's count entries 
	    int nbEntries = countEntries(db);
	    
	    if(nbEntries == NB_LIST_LAST_MEDIA || nbEntries < NB_LIST_LAST_MEDIA)
	    {
		    //add a new entry then delete the first entry of the database
		    addEntry(key, fileName, Remark, db);
	    	if(nbEntries == NB_LIST_LAST_MEDIA)
		    {
		    	deleteFirstEntry(db);
		    }
	    }
	    else
	    {
	    	//Error : more than NB_LIST_LAST_MEDIA entries
	    	//deleting all entries
		    db.delete(LISTMEDIA_TABLE_NAME, null, null);
	    	
			//then add tests entries
	    	for(int i=0; i<10; i++)
			{
				addEntry(i, "NomFichier"+i, "Remarque"+i, db);
			}
	    }
	}

	/**
	 * @param db
	 */
	private void deleteFirstEntry(SQLiteDatabase db) {
		Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);
		if (cursor.moveToFirst()) {
//			int key = cursor.getPosition();
			int key = cursor.getInt(0);
			
        	int value0 = cursor.getInt(0);
        	String value1 = cursor.getString(1);
        	String value2 = cursor.getString(2);
        	
			int toto = 2;
			
//			db.beginTransaction();
			db.delete(LISTMEDIA_TABLE_NAME, KEY_ID+" = "+key, null);
//			db.setTransactionSuccessful();
//			db.endTransaction();
			
			int titi = 3;
		}
		cursor.close();
	}

	/**
	 * @param fileName
	 * @param Remark
	 * @param db
	 */
	private void addEntry(int key, String fileName, String Remark, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put(KEY_ID, key);
		values.put(KEY_FILE_NAME, fileName);
		values.put(KEY_REMARK, Remark);
		db.insert(LISTMEDIA_TABLE_NAME, null, values);
	}

	/**
	 * @param db
	 * @return
	 */
	private int countEntries(SQLiteDatabase db) {
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
	private int lastEntry(SQLiteDatabase db) {
		Cursor cursorLast = db.rawQuery(SQL_SELECT_ENTRIES, null);
		cursorLast.moveToLast();
	    int lastEntry = cursorLast.getInt(0);
	    cursorLast.close();
		return lastEntry;
	}

   private ArrayList<Map<String, String>> buildData(SQLiteDatabase db) {
        
	   ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	   
	    Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);
		 
	    if (cursor.moveToFirst()) {
	        do {
	        	list.add(putData(cursor.getString(1), cursor.getString(2)));
	        } while (cursor.moveToNext());
	    }
	    
 	    db.close();

        return list;
    }

    private HashMap<String, String> putData(String pathFileName, String remark) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("pathFileName", pathFileName);
        item.put("remark", remark);
        return item;
    }
}
