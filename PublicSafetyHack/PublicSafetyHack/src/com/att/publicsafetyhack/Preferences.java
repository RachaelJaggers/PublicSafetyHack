package com.att.publicsafetyhack;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 
 * @author za476x This class handles saving preferences permanently to the app
 */

public class Preferences {
	private Context context;
	private boolean firstTimeLogin = true;
	private String username = null; // this is used to store the username

	private SharedPreferences settings;
	public static final String PREFS_NAME = "AppPrefsFile";

	public Preferences(Context instance) {
		context = instance;

		updateValues();
	}

	private void updateValues() {
		// Restore preferences
		settings = context.getSharedPreferences(PREFS_NAME, 0);
		// get the boolean from the settings
		firstTimeLogin = settings.getBoolean("FirstTimeLogin", true); // if
																		// nothing
																		// is
																		// set,
																		// second
																		// argument
																		// is
																		// returned
		username = settings.getString("Username", null); // returns null if not
															// set
	}

	/**
	 * This is a getter method that returns true if it is the first time a user
	 * is logging in.
	 * 
	 * @return True if it is the users first time logging in.
	 */
	public boolean firstTimeLogin() {
		return firstTimeLogin;
	}

	/**
	 * Setting first time login to true will cause the user to be logged out
	 */
	public void setFirstTimeLogin(boolean value) {
		firstTimeLogin = value;
		// saving the value to shared preferences
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("FirstTimeLogin", value);

		// deleting username if setting the value is zero
		if (value == true) {
			username = null;
			editor.putString("Username", null);
		}

		// Commit the edits!
		editor.commit();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("Username", username);
		// Commit the edits!
		editor.commit();
	}

}
