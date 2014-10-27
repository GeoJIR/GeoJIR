package com.geojir;

import static com.geojir.Constants.*;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ListMediaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);
		
//		File file = getFilesDir();
		try {
			Constants.initConstants();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("Test", "test variable : " + PATH_GEOJIR);
		
		//lecture du fichier de liste des medias
		
		
		//si fichier inexistant, on le cree
		
		//on met le contenu dans un array
		
		// on r�cup�re les 10 dernieres entrees 
		
		//on affiche les 10 dernieres entrees dans un label.
	}
}
