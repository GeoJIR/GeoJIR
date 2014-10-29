package com.geojir;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.geojir.Constants.*;

public class AccountActivity extends ParentMenuActivity {

	// champ usernam
	private EditText username;
	// champ email
	private EditText mail;
	// boutton d'enregistrement
	private Button saveButton;
	
	//shared preferences
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		preferences = getSharedPreferences(Constants.PREF_ACCOUNT, Context.MODE_PRIVATE);
		
		username = (EditText) findViewById(R.id.acountcreation_email);
		mail = (EditText) findViewById(R.id.acountcreation_password);
		
		username.setText(getAccountName(preferences));
		mail.setText(getAccountEmail(preferences));
		
		//on ajoute un listener au TextEdit du nom pour éventuellement le mettre à ""
		//et si le Text Edit du mail est à "" on lui remet la valeur par défaut
		username.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO : la mise à jour du TextEdit n'est pas intantanée
				if( mail.getText().toString().equals(""))
					mail.setText(Constants.PREF_DEFAULT_ACCOUNT_EMAIL);
				
				if( username.getText().toString().equals(Constants.PREF_DEFAULT_ACCOUNT_NAME))
					username.setText("");
			}
		});
		
		//on ajoute un listener au TextEdit du mail pour éventuellement le mettre à ""
		//et si le Text Edit du nom est à "" on lui remet la valeur par défaut
		mail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO : la mise à jour du TextEdit n'est pas intantanée
				if( username.getText().toString().equals(""))
					username.setText(Constants.PREF_DEFAULT_ACCOUNT_NAME);
				
				if( mail.getText().toString().equals(Constants.PREF_DEFAULT_ACCOUNT_EMAIL))
					mail.setText("");
			}
		});
		
		//on ajoute un listener au boutton de la vue
		saveButton = (Button) findViewById(R.id.acountcreation_connect);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//enregistrement dans les préférences des infos de l'utilisateur
				setAccountName(preferences, username.getText().toString());
				setAccountEmail(preferences, mail.getText().toString());
			}
		});
	}	

	//SET preferences element
	public void setAccountName(SharedPreferences preferences, String value) {
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_NAME, value);
		editor.commit();
	}
	
	public void setAccountEmail(SharedPreferences preferences, String value) {
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_EMAIL, value);
		editor.commit();
	}
	
	public void setAccountFollowed(SharedPreferences preferences, String value) {
		//en fait...il faut gérer une liste multi selections
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_FOLLOWED, value);
		editor.commit();
	}
	
	public void setAccountFollowers(SharedPreferences preferences, String value) {
		//en fait...il faut gérer une liste multi selections
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_FOLLOWERS, value);
		editor.commit();
	}
	
	//GET preferences element
	public String getAccountName(SharedPreferences preferences) {
		return preferences.getString(Constants.PREF_ACCOUNT_NAME, Constants.PREF_DEFAULT_ACCOUNT_NAME);
		
	}
	
	public String getAccountEmail(SharedPreferences preferences) {
		
		return preferences.getString(Constants.PREF_ACCOUNT_EMAIL, Constants.PREF_DEFAULT_ACCOUNT_EMAIL);
		
	}
	
	public String getAccountFollowed(SharedPreferences preferences) {
		
		return preferences.getString(Constants.PREF_ACCOUNT_FOLLOWED, Constants.PREF_DEFAULT_ACCOUNT_FOLLOWED);
		
	}
	
	public String getAccountFollowers(SharedPreferences preferences) {
		
		return preferences.getString(Constants.PREF_ACCOUNT_FOLLOWERS, Constants.PREF_DEFAULT_ACCOUNT_FOLLOWERS);
		
	}

}
