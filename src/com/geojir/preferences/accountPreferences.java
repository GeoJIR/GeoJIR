package com.geojir.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.geojir.Constants;

public class accountPreferences
{
	// shared preferences
	protected SharedPreferences preferences;
	protected SharedPreferences.Editor prefsEditor;
	 
 	/**
 	 * @param context
 	 */
 	public accountPreferences(Context context)
	{
		this.preferences = context.getSharedPreferences(Constants.PREF_ACCOUNT,Context.MODE_PRIVATE);
		this.prefsEditor = preferences.edit();
	}
	
	// SET preferences element
	/**
	 * @param value
	 */
	public void setAccountName(String value)
	{
		prefsEditor.putString(Constants.PREF_ACCOUNT_NAME, value).commit();
	}

	/**
	 * @param value
	 */
	public void setAccountEmail(String value)
	{
		prefsEditor.putString(Constants.PREF_ACCOUNT_EMAIL, value).commit();
	}

	// GET preferences element
	/**
	 * @return
	 */
	public String getAccountName()
	{
		return preferences.getString(Constants.PREF_ACCOUNT_NAME, null);
	}

	/**
	 * @return
	 */
	public String getAccountEmail()
	{
		return preferences.getString(Constants.PREF_ACCOUNT_EMAIL, null);
	}

}
