package com.geojir;

import static com.geojir.Constants.*;

import java.io.File;

import android.content.Context;
import android.os.Environment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ListMediaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_media);
		
		File file = getFilesDir();
		File file2 = Constants.initConstants();
		
		Log.i("Test", "test variable : " + PATH_GEOJIR);
		
		//lecture du fichier de liste des medias
		
		
		//si fichier inexistant, on le crée
		
		//on met le contenu dans un array
		
		// on récupère les 10 dernières entrées 
		
		//on affiche les 10 dernières entrées dans un label.
	}
}
