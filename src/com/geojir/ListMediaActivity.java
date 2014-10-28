package com.geojir;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ListMediaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);
		
		// on charge 10 entrées
		ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());
		
		// on récupère les 10 dernières entrées
	    String[][] values = new String[10][2];
	    values = listeMedia.getAllMedias();
		
		/****************************************************************************************************/
		 // Get ListView object from xml
	    ListView vue = (ListView) findViewById(R.id.listViewMedias);
       
	    
	    ArrayList<Map<String, String>> list = buildData();
	    
	    String[] from = { "name", "purpose" };
/*	    
	    int[] to = { android.R.id.icon, android.R.id.remark };
	    
	    SimpleAdapter adapter = new SimpleAdapter(this, list,
	            android.R.layout.simple_list_item_2, from, to);
	        setListAdapter(adapter);
*/
/****************************************************************************************************/		
		//on affiche les 10 dernières entrées dans un label.
//        TextView tm = (TextView) findViewById(R.id.textViewListMedia);
//        tm.setText(liste);
	}
    private ArrayList<Map<String, String>> buildData() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(putData("Android", "Mobile"));
        list.add(putData("Windows7", "Windows7"));
        list.add(putData("iPhone", "iPhone"));
        return list;
    }

    private HashMap<String, String> putData(String name, String purpose) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", name);
        item.put("purpose", purpose);
        return item;
    }
}


