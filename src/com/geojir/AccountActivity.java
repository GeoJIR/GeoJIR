package com.geojir;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AccountActivity extends ParentMenuActivity
{

	// champ usernam
	private EditText username;
	// champ email
	private EditText mail;
	// boutton d'enregistrement
	private Button saveButton;

	// shared preferences
	protected SharedPreferences preferences;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.geojir.ParentMenuActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);

		preferences = getSharedPreferences(Constants.PREF_ACCOUNT,
				Context.MODE_PRIVATE);

		username = (EditText) findViewById(R.id.acountcreation_email);
		mail = (EditText) findViewById(R.id.acountcreation_password);

		username.setText(getAccountName());
		mail.setText(getAccountEmail());

		// Observable sur le bouton
		final String message = getString(R.string.infoSaved);

		final OnClickTest onSubscribe = new OnClickTest(message);

		final Observable<String> myObservable = Observable.create(onSubscribe);

		final Action1<String> onNextAction = new Action1<String>()
		{
			@Override
			public void call(String s)
			{
				Toast.makeText(AccountActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		};

		myObservable.subscribe(onNextAction);

		// on ajoute un listener au boutton de la vue
		saveButton = (Button) findViewById(R.id.acountcreation_connect);

		saveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// enregistrement dans les préférences des infos de
				// l'utilisateur
				//verify if one field is empty
				if( username.getText().toString().trim().isEmpty() ||  mail.getText().toString().trim().isEmpty() )
				{
					Toast.makeText(AccountActivity.this, R.string.oneFieldIsEmpty,Toast.LENGTH_SHORT).show();
				}
				else
				{
					setAccountName(username.getText().toString().trim());
					setAccountEmail(mail.getText().toString().trim());
	
					// on informe l'observable
					onSubscribe.onClick();
				}
			}
		});

	}

	// SET preferences element
	public void setAccountName(String value)
	{
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_NAME, value);
		editor.commit();
	}

	public void setAccountEmail(String value)
	{
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_EMAIL, value);
		editor.commit();
	}

	public void setAccountFollowed(String value)
	{
		// TODO en fait...il faut gérer une liste multi selections => Bdd
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_FOLLOWED, value);
		editor.commit();
	}

	public void setAccountFollowers(String value)
	{
		// TODO en fait...il faut gérer une liste multi selections => Bdd
		Editor editor = preferences.edit();
		editor.putString(Constants.PREF_ACCOUNT_FOLLOWERS, value);
		editor.commit();
	}

	// GET preferences element
	public String getAccountName()
	{
		return preferences.getString(Constants.PREF_ACCOUNT_NAME, null);
	}

	public String getAccountEmail()
	{
		return preferences.getString(Constants.PREF_ACCOUNT_EMAIL, null);
	}

	public String getAccountFollowed()
	{
		return preferences.getString(Constants.PREF_ACCOUNT_FOLLOWED,
				Constants.PREF_DEFAULT_ACCOUNT_FOLLOWED);
	}

	public String getAccountFollowers()
	{
		return preferences.getString(Constants.PREF_ACCOUNT_FOLLOWERS,
				Constants.PREF_DEFAULT_ACCOUNT_FOLLOWERS);
	}

	/**
	 * 
	 * @author Igor
	 *
	 */
	private final class OnClickTest implements Observable.OnSubscribe<String>
	{
		private final String message;
		private Subscriber<? super String> subscriber;

		private OnClickTest(String message)
		{
			this.message = message;
		}

		@Override
		public void call(Subscriber<? super String> subscriber)
		{
			this.subscriber = subscriber;

		}

		public void onClick()
		{
			this.subscriber.onNext(message);
		}
	}
}
