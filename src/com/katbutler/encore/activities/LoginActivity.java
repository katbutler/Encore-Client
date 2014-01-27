package com.katbutler.encore.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.katbutler.encore.R;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.AuthenticationDataAccess;
import com.katbutler.encore.dataaccess.UserDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.User;

public class LoginActivity extends Activity {

	private EditText emailField;
	private EditText passwordField;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);	//Remove title bar

		SharedPreferences settings = getSharedPreferences(SessionManager.SESSION_DATA, 0);
	    String session = settings.getString("session", "NONE");
	    
		setContentView(R.layout.login);
		
		emailField = (EditText) findViewById(R.id.emailField);
		passwordField = (EditText) findViewById(R.id.passwordField);
		final TextView logInText = (TextView) findViewById(R.id.logInText);
		final Button signupButton = (Button) findViewById(R.id.signupButton);
		final Button loginButton = (Button) findViewById(R.id.loginButton);
			
		if (session.equals("NONE")) {
	
			//animation of logo
			ImageView img_animation = (ImageView) findViewById(R.id.encoreLogo);
			 
	        TranslateAnimation animation = new TranslateAnimation(0.0f, 00f, 0.0f, -400.0f);  
	        animation.setDuration(2000);
	        animation.setFillAfter(true);   
	        animation.setStartOffset(1000);
	 
	        img_animation.startAnimation(animation); 
	       
	        animation.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationEnd(Animation animation) {
					Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
					animFadeIn.setDuration(1000);
					animFadeIn.setFillAfter(true);
					
					emailField.setAnimation(animFadeIn);
					passwordField.setAnimation(animFadeIn);
					logInText.setAnimation(animFadeIn);
					loginButton.setAnimation(animFadeIn);
					signupButton.setAnimation(animFadeIn);
					
					animFadeIn.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation animation) {
							emailField.setVisibility(View.VISIBLE);
							passwordField.setVisibility(View.VISIBLE);
							logInText.setVisibility(View.VISIBLE);
							loginButton.setVisibility(View.VISIBLE);
							signupButton.setVisibility(View.VISIBLE);
						}
						
						@Override
						public void onAnimationStart(Animation animation) {}
						
						@Override
						public void onAnimationRepeat(Animation animation) {}
						
					});
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationStart(Animation animation) {}
	        	
	        });
	        
			signupButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent myIntent = new Intent(LoginActivity.this, SignUpActivity.class);
					LoginActivity.this.startActivity(myIntent);
				}
			});
			
			loginButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					final ProgressBar signInProgress = (ProgressBar) findViewById(R.id.progressBar);
					LoginActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							signInProgress.setVisibility(View.VISIBLE);
	//						loginButton.setVisibility(View.INVISIBLE);
							loginButton.setAlpha(0); //fix for visibility issue
						}
					});
					
					AuthenticationDataAccess.getInstance().login(emailField.getText().toString(), passwordField.getText().toString(), new AsyncCallback<User>() {
						@Override
						public void onSuccess(User result) {
	//						LoginActivity.this.runOnUiThread(new Runnable() {
	//							@Override
	//							public void run() {
	//								signInProgress.setVisibility(View.INVISIBLE);
	//								loginButton.setVisibility(View.VISIBLE);
	//								loginButton.setAlpha(1);
	//							}
	//						});
							
							SharedPreferences settings = getSharedPreferences(SessionManager.SESSION_DATA, 0);
							SharedPreferences.Editor editor = settings.edit();
							editor.putString("session", result.getEmail());
							editor.commit();
							
							// load the profile activity
							Intent myIntent = new Intent(LoginActivity.this, EncoreActivity.class);
							LoginActivity.this.startActivity(myIntent);
						}
						
						@Override
						public void onFailure(final EncoreError error) {
							LoginActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									signInProgress.setVisibility(View.INVISIBLE);
									loginButton.setAlpha(1);
								}
							});
							
							// Display the Error in a Toast
							LoginActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(LoginActivity.this.getApplicationContext(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
								}
							});
						}
					});
				}
			});
		} else { // Already has session
			
			UserDataAccess.getInstance().getUser(session, new AsyncCallback<User>() {
				@Override
				public void onSuccess(User user) {
					SessionManager.getInstance().setCurrentUser(user);
					
					Intent encoreIntent = new Intent(LoginActivity.this, EncoreActivity.class);
					encoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					LoginActivity.this.startActivity(encoreIntent);
				}

				@Override
				public void onFailure(final EncoreError error) {
					LoginActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(LoginActivity.this.getApplicationContext(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			});
			
		}
		
	}

	@Override
	public void onBackPressed() {
	    moveTaskToBack(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
