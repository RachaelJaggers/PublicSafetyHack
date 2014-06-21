package com.att.publicsafetyhack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords. TODO: remove after
	 * connecting to a real authentication system.
	 */
	//private static final String[] DUMMY_CREDENTIALS = new String[] { "user:password" };

	/**
	 * The default values to populate the username field with.
	 */
	public static final String EXTRA_TEXT = "com.example.android.authenticatordemo.extra.TEXT";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	//database object
	JSONDatabase db;
	//preferences object
	Preferences settings;
	
	// Values for username and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUserView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		//grab the database object
		db = new JSONDatabase(this);
		//grab the preferences object
		settings = new Preferences(this);
		
		if(settings.getUsername() != null)
		{
			if(settings.firstTimeLogin()==true)
			{
				allowFirstTimeUserAccess();	//redirect user to select location
			} else {
				allowUserAccess();	//redirect user to main activity
			}	
		}
		
		setContentView(R.layout.activity_login);
		
		
		
		// Set up the login form.
		mUsername = getIntent().getStringExtra(EXTRA_TEXT);
		mUserView = (EditText) findViewById(R.id.prompt_username);
		mUserView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.prompt_password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		
		
		findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	
	//redirects to the main activity
	private void allowUserAccess()
	{
		//allow user access to the app
		Toast.makeText(LoginActivity.this, "Hey " + db.getUserWithUsername(settings.getUsername()).getName() +" has successfully logged in!", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
		finish();	//kill the task so back button will not make it come back
	}

	//redirects to the location selection activity
	private void allowFirstTimeUserAccess()
	{
		//allow user access to the app
		Toast.makeText(LoginActivity.this, db.getUserWithUsername(settings.getUsername()).getName() +" has successfully logged in!!", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(i);
		finish();	//kill the task so back button will not make it come back
	}

	/**
	 * Attempts to sign in to the account specified by the login form. If there are form errors
	 * (invalid email, missing fields, etc.), the errors are presented and no actual login attempt
	 * is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUserView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUserView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid ATTUID.
		if (TextUtils.isEmpty(mUsername)) {
			mUserView.setError(getString(R.string.error_field_required));
			focusView = mUserView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login task used to authenticate the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			
			//check the database for username and password input
			return db.validateUser(mUsername, mPassword);
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			
			if (success) {	// if the username and password are correct
					
				//save the current username in the preferences class
				settings.setUsername(mUsername);
				allowFirstTimeUserAccess();
				
			} else { // if the username and password are incorrect
							
				Toast.makeText(LoginActivity.this, "Failure to Login!!", Toast.LENGTH_SHORT).show();
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();

			}
		}
		


		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}