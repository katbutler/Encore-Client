package com.katbutler.encore.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.katbutler.encore.R;
import com.katbutler.encore.dataaccess.AuthenticationDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.User;


public class SignUpActivity extends Activity {
	
	
	
	private Button signupButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);	//Remove title bar
		
		setContentView(R.layout.sign_up);
		
		final EditText emailField = (EditText) findViewById(R.id.email);
		final EditText passwordField = (EditText) findViewById(R.id.password);
		final EditText fNameField = (EditText) findViewById(R.id.fname);
		final EditText lNameField = (EditText) findViewById(R.id.lname);
		signupButton = (Button) findViewById(R.id.signupButton);
	
		signupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addUser(emailField.getText().toString(), passwordField.getText().toString(), fNameField.getText().toString(), lNameField.getText().toString());
			}
		});
	}
	
	/**
	 * 
	 * @param email
	 * @param password
	 * @param name
	 */
	private void addUser(final String email, final String password, final String fName, final String lName) {
		final ProgressBar signUpProgress = (ProgressBar) findViewById(R.id.signUpProgress);
		signUpProgress.setVisibility(View.VISIBLE);
		signupButton.setEnabled(false);

		// Use the DataAccess to sign up for the Encore Service
		AuthenticationDataAccess.getInstance().signUp(email, password, fName, lName, new AsyncCallback<User>() {
			@Override
			public void onSuccess(User result) {
				//load the profile activity
				Intent myIntent = new Intent(SignUpActivity.this, EncoreActivity.class);
				SignUpActivity.this.startActivity(myIntent);
			}
			
			@Override
			public void onFailure(final EncoreError error) {
				// Display the Error in a Toast
				SignUpActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(SignUpActivity.this.getApplicationContext(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
						
						signUpProgress.setVisibility(View.INVISIBLE);
						signupButton.setEnabled(true);
					}
				});
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
