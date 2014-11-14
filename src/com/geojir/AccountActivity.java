package com.geojir;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.geojir.preferences.accountPreferences;

public class AccountActivity extends ParentMenuActivity
{

	// champ usernam
	private EditText username;
	// champ email
	private EditText mail;
	// boutton d'enregistrement
	private Button saveButton;
	
	protected accountPreferences preferences;

	// shared preferences
	//protected SharedPreferences preferences;

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

//		preferences = getSharedPreferences(Constants.PREF_ACCOUNT, Context.MODE_PRIVATE);
		preferences = new accountPreferences(getApplicationContext());

		username = (EditText) findViewById(R.id.acountcreation_email);
		mail = (EditText) findViewById(R.id.acountcreation_password);

		username.setText(preferences.getAccountName());
		mail.setText(preferences.getAccountEmail());

		// Observable sur le bouton
		final String message = getString(R.string.infoSaved);

		final OnClickTest onSubscribe = new OnClickTest(message);

		final Observable<String> myObservable = Observable.create(onSubscribe);

		final Action1<String> onNextAction = new Action1<String>()
		{
			@Override
			public void call(String s)
			{
				toast(message);
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
				// enregistrement dans les préférences des infos de l'utilisateur
				//verify if one field is empty
				if( username.getText().toString().trim().isEmpty() ||  mail.getText().toString().trim().isEmpty() )
				{
					toast(R.string.oneFieldIsEmpty);
				}
				else
				{
					preferences.setAccountName(username.getText().toString().trim());
					preferences.setAccountEmail(mail.getText().toString().trim());
					
					// on informe l'observable
					onSubscribe.onClick();
				}
			}
		});
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
