package com.geojir;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;


public class ListMediaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);

		// on charge 10 entrées
		ListMediaDb listeMedia = new ListMediaDb(getApplicationContext());
		
		// on récupère les 10 dernières entrées
		String liste;
		liste = listeMedia.getAllMedias();
		
		//on affiche les 10 dernières entrées dans un label.
        TextView tm = (TextView) findViewById(R.id.textViewListMedia);
        tm.setText(liste);
	}
}


