package com.geojir;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.geojir.preferences.accountPreferences;

public class AccountActivity extends ParentMenuActivity
{
	@InjectView(R.id.account_username)
	EditText username;
	@InjectView(R.id.account_email)
	EditText mail;
	
	protected accountPreferences preferences;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		ButterKnife.inject(this);
		
		// Rescue preferences values
		preferences = new accountPreferences(getApplicationContext());
		username.setText(preferences.getAccountName());
		mail.setText(preferences.getAccountEmail());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simple_save_menu, menu);
		
		return true;
	}
	
	@Override
	// Save with actionBar button
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.actionBarSave)
		{
			savePreference();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	// Save new preferences
	public void savePreference()
	{
		// Verify empty editText
		if (username.getText().toString().trim().isEmpty()
				|| mail.getText().toString().trim().isEmpty())
			toast(R.string.oneFieldIsEmpty);
		else
		{
			// Save
			preferences.setAccountName(username.getText().toString().trim());
			preferences.setAccountEmail(mail.getText().toString().trim());
			
			// User message
			toast(R.string.infoSaved);
		}
	}
	
}
