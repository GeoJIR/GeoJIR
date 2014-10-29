package com.geojir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AccountActivity extends ParentMenuActivity {

	// champ usernam
	private EditText username;
	// champ email
	private EditText mail;
	// boutton d'enregistrement
	private Button saveButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		username = (EditText) findViewById(R.id.acountcreation_email);
		mail = (EditText) findViewById(R.id.acountcreation_password);

		//on ajoute un listener au boutton de la vue
		saveButton = (Button) findViewById(R.id.acountcreation_connect);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//enregsitrement dans les préférence des infos de l'utilisateur

			}
		});
		
		//charger les infos des préférences dans les champs si elles existent déjà
	}
}
