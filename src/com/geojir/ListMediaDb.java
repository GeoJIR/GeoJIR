package com.geojir;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

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
          "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
          KEY_ID + " INTEGER, " +
          KEY_FILE_NAME + " TEXT, " +
          KEY_REMARK + " TEXT);";

   private static final String SQL_SELECT_ENTRIES =
          "SELECT * FROM " + LISTMEDIA_TABLE_NAME;

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
		// TODO Auto-generated method stub
	       db.delete(LISTMEDIA_TABLE_NAME, null, null);
	       onCreate(db);
	}
	
	/**
	 * @param db
	 * @return
	 */
	public String[][] getAllMedias() {
//		   List<String> mediaList = new ArrayList<String>();
		SQLiteDatabase db = this.getWritableDatabase();

	    //pour les tests
//		Cursor cursorDrop = db.rawQuery(SQL_DELETE_TABLE, null);
//	    cursorDrop.close();
	    
		//on regarde s'il n'y a pas plus d'entrées que prévu
		testNbEntries(db);
		
	    Cursor cursor = db.rawQuery(SQL_SELECT_ENTRIES, null);
	 
	    String[][] mediaList = new String[10][2];
	    
		int i = 0;
	    if (cursor.moveToFirst()) {
	        do {
	        	mediaList[i][0] = cursor.getString(1);
	        	mediaList[i][1] = cursor.getString(2);
	        	i++;
	        } while (cursor.moveToNext());
	    }
	    
 	    db.close();
	    
	    // return contact list
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
	    	//pour les tests
	    	int lastentry = lastEntry(db);
	    	lastentry += 1;
	    	addMedia(lastentry, "NomFichier"+lastentry, "Remarque"+lastentry, db);
	    }
	    else if(nbEntries > NB_LIST_LAST_MEDIA )
	    {
	    	//on a plus de X entrées, on les supprime toutes
		    db.delete(LISTMEDIA_TABLE_NAME, null, null);
	    	
			//puis on en rajoute X pour les test
	    	for(int i=0; i<10; i++)
			{
	    		addMedia(i, "NomFichier"+i, "Remarque"+i, db);
			}
	    }
	}

	// Gets the data repository in write mode
	/**
	 * @param fileName
	 * @param Remark
	 * @param db
	 */
	private void addMedia(int key, String fileName, String Remark, SQLiteDatabase db)
	{
	    //on récupère le nombre d'entrées dans la base
	    int nbEntries = countEntries(db);
	    
	    if(nbEntries == NB_LIST_LAST_MEDIA || nbEntries < NB_LIST_LAST_MEDIA)
	    {
		    //on a le nombre d'entrée, on va supprimer la première
	    	if(nbEntries == NB_LIST_LAST_MEDIA)
		    {
		    	deleteFirstEntry(db);
		    }
	    	
	    	//on ajoute la nouvelle entrée
		    addEntry(key, fileName, Remark, db);
	    }
	    else
	    {
	    	//on a plus de X entrées, on les supprime toutes
		    db.delete(LISTMEDIA_TABLE_NAME, null, null);
	    	
			//puis on en rajoute X pour les tests
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
			int key = cursor.getPosition();
        	int value0 = cursor.getInt(0);
        	String value1 = cursor.getString(1);
        	String value2 = cursor.getString(2);
        	
			int toto = 2;
//			db.delete(LISTMEDIA_TABLE_NAME, KEY_ID+"="+key+" AND "+KEY_FILE_NAME+"="+value1+" AND "+KEY_REMARK+"="+value2, null);
			db.delete(LISTMEDIA_TABLE_NAME, KEY_ID+"="+key, null);
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
	    int lastEntry = cursorLast.getPosition();
	    cursorLast.close();
		return lastEntry;
	}
}
