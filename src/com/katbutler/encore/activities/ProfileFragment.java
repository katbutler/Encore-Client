package com.katbutler.encore.activities;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.katbutler.encore.R;
import com.katbutler.encore.client.ImageUploader;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.EventDataAccess;
import com.katbutler.encore.dataaccess.UserDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.EncoreImageFile;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.User;
import com.katbutler.encore.util.DownloadImageTask;
import com.katbutler.encore.util.ImageUtils;
import com.katbutler.encore.util.MyLocation;
import com.katbutler.encore.util.MyLocation.LocationResult;

/**
 * A Fragment for the tab view to show the current users profile
 */
public class ProfileFragment extends Fragment {
	
	private static int CAMERA_SUCCESS = 7290;
	AlertDialog editColours;
	Context context;
	ImageView profPic = null;
	ImageView editBtn = null;
	ImageView myLocationBtn = null;
	TextView bioText = null;
	TextView fullNameText = null;
	TextView numFriends = null;
	TextView numEvents = null;
	private Uri picUri;

	public ProfileFragment() {
	}

	/** 
	 * onCreate is called first
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	/**
	 * onCreateView is called second. This is where we inflate our view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.profile, container, false);

		return rootView;
	}
	
	/** 
	 * onActivityCreated is called third after the view has been created.
	 * This is where we run getView().findViewById() and attach
	 * handlers to our view
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		
		bioText = (TextView) getView().findViewById(R.id.bioText);
		profPic = (ImageView) getView().findViewById(R.id.profilePic);
		editBtn = (ImageView) getView().findViewById(R.id.editColoursBtn);
		myLocationBtn = (ImageView) getView().findViewById(R.id.myLocationBtn);
		fullNameText = (TextView) getView().findViewById(R.id.fullNameText);
		
		fullNameText.setText(SessionManager.getInstance().getCurrentUser().getFullName());

		
		String email = SessionManager.getInstance().getCurrentUser().getEmail();
		// Set saved color scheme
		SharedPreferences settings = getActivity().getSharedPreferences("profile_color", 0);
	    int color = settings.getInt("color"+email, ORANGE);
	    
	    setProfileColor(color);
		
		if (SessionManager.getInstance().getCurrentUser().getBio() == null
				|| SessionManager.getInstance().getCurrentUser().getBio()
						.equals("")) {
			bioText.setText("<Click to Edit Bio>");
		} else {
			bioText.setText(SessionManager.getInstance().getCurrentUser()
					.getBio());
		}

		bioText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editBio();
			}
		});

		if (SessionManager.getInstance().getCurrentUser().getPicURL() != null) {
			// Download an Image from the server to use as the profile pic
			new DownloadImageTask(profPic, true)
					.execute(SessionManager.getInstance().getEncoreServerUrl()
							+ SessionManager.getInstance().getCurrentUser()
									.getPicURL());
		}

		profPic.setClickable(true); // user can edit profile pic
		profPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editProfilePic();
			}
		});
		
		editBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editColours();
			}
		});
		
		myLocationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder confirmGetLocation = new AlertDialog.Builder(context);
				
				// set dialog message
				confirmGetLocation
						.setTitle("Set your current location to home location?")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								findLocation(); 
							}
						})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

				AlertDialog confirmDialog = confirmGetLocation.create();
				confirmDialog.show();
				
			}
		});
		
		setCounts(); //set the number of users following and the number of events attended
	}
	
	public void setCounts() {
		numFriends = (TextView) getView().findViewById(R.id.followingCount);
		numEvents = (TextView) getView().findViewById(R.id.eventsCount);
		
		EventDataAccess.getInstance().getEventsForUser(
				SessionManager.getInstance().getCurrentUser(), 
				new AsyncCallback<List<Event>>() {

					@Override
					public void onSuccess(final List<Event> result) {
						
						ProfileFragment.this.getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								int eventCount = result.size();
								numEvents.setText("(" + eventCount + ")");
							}
						});
					}

					@Override
					public void onFailure(EncoreError error) {
						System.out.println(error.getErrorMessage());
					}
				});
		
		UserDataAccess.getInstance().getFollowersForUser(
				SessionManager.getInstance().getCurrentUser(), 
				new AsyncCallback<List<User>>() {

					@Override
					public void onSuccess(final List<User> result) {
							ProfileFragment.this.getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								int userCount = result.size();
								numFriends.setText("(" + userCount + ")");
							}
						});
					}

					@Override
					public void onFailure(EncoreError error) {
						System.out.println(error.getErrorMessage());
					}
				});
	}
	
	public void findLocation() {
		context = this.getActivity();
		
		LocationResult locationResult = new LocationResult(){
		    @Override
		    public void gotLocation(Location location){
		    	
		    	Toast.makeText(context, 
		    			"Set home location to:\n" +
		    			"Latitude: " + location.getLatitude() + "\n" +
		    			"Longitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
		    	
		    	UserDataAccess.getInstance().setHomeLocation(
		    		 SessionManager.getInstance().getCurrentUser(), 
					 location.getLatitude(), 
					 location.getLongitude(), 
					 new AsyncCallback<User>() {
						@Override
						public void onSuccess(User user) {
							SessionManager.getInstance().setCurrentUser(user);
						}

						@Override
						public void onFailure(final EncoreError error) {
							getActivity().runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
								}
							});
						}
					 });
		    	
		    }
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(context, locationResult);
	}
	
	public void editColours() {
		context = this.getActivity();
		
		
		final CharSequence[] items = {"Orange", "Yellow", "Blue"};
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Pick a color");
		builder.setSingleChoiceItems(items, -1, new android.content.DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	 setProfileColor(item);
		    }

		});
		AlertDialog alert = builder.create();
		//And if the line above didn't bring ur dialog up use this bellow also:
		alert.show();

	}

	private static String ORANGE_COLOR = "#EA6735";
	private static String YELLOW_COLOR = "#FFFF6A";
	private static String BLUE_COLOR = "#7AA8FF";
	
	private static final int ORANGE = 0;
	private static final int YELLOW = 1;
	private static final int BLUE = 2;

	
	private void setProfileColor(int item) {
		final RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.background);
		
		SharedPreferences settings = getActivity().getSharedPreferences("profile_color", 0);
	    Editor editor = settings.edit();
	    editor.putInt("color"+SessionManager.getInstance().getCurrentUser().getEmail(), item);
	    editor.commit();
		
		switch(item)
        {
            case ORANGE:
           	 layout.setBackgroundColor(Color.parseColor(ORANGE_COLOR));
           	 bioText.setTextColor(Color.parseColor("#383838"));
           	 fullNameText.setTextColor(Color.parseColor("#383838"));
                break;
            case YELLOW:
           	 layout.setBackgroundColor(Color.parseColor(YELLOW_COLOR));
           	 bioText.setTextColor(Color.parseColor("#750071"));
           	 fullNameText.setTextColor(Color.parseColor("#750071"));
                break;
            case BLUE:
           	 layout.setBackgroundColor(Color.parseColor(BLUE_COLOR));
           	 bioText.setTextColor(Color.parseColor("#FFFFFF"));
           	 fullNameText.setTextColor(Color.parseColor("#FFFFFF"));
                break;
            
        }
	}
	
	
	public void editBio() {
		context = this.getActivity();

		// get bio_dialog.xml
		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.bio_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText bioEditText = (EditText) promptsView
				.findViewById(R.id.bioEditText);
		bioEditText.setText(SessionManager.getInstance().getCurrentUser()
				.getBio());

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// get user input and set it to bio text in profile
						bioText.setText(bioEditText.getText());

						User user = SessionManager.getInstance()
								.getCurrentUser();
						user.setBio(bioEditText.getText().toString());
						UserDataAccess.getInstance().updateUser(
								user.getEmail(), user.getFirstName(),
								user.getLastName(), user.getBio(),
								user.getPicURL(), new AsyncCallback<User>() {

									@Override
									public void onSuccess(User user) {
										SessionManager.getInstance()
												.setCurrentUser(user);
										System.out.println("SUCCESS");
									}

									@Override
									public void onFailure(EncoreError error) {
										System.out.println(error
												.getErrorMessage());
									}
								});
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void editProfilePic() {
		// start camera and take pic
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		// getting pic Uri
		cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, picUri);

		startActivityForResult(cameraIntent, CAMERA_SUCCESS);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent cameraIntent) {
		if (requestCode == CAMERA_SUCCESS && cameraIntent != null) {
			if (cameraIntent.getExtras() == null) {
				System.out.println("Did not take a picture");
				return;
			}
			Bitmap thumbnail = (Bitmap) cameraIntent.getExtras().get("data");
			Bitmap thumbCropped = ImageUtils
					.createCroppedCircledBitmap(thumbnail);

			String downloadCacheTempDir = getActivity().getCacheDir().getAbsolutePath() + "/";
			
			try {
				String filename = downloadCacheTempDir + "EncoreImage"
						+ new Date().getTime() + ".png";
				FileOutputStream out = new FileOutputStream(filename);
				thumbnail.compress(Bitmap.CompressFormat.PNG, 90, out);
				out.close();

				System.out.println(filename);

				new ImageUploader().uploadImage(filename,
						new AsyncCallback<EncoreImageFile>() {
							@Override
							public void onSuccess(final EncoreImageFile result) {
								getActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												UserDataAccess.getInstance().addPicUrlToUser(
													SessionManager.getInstance().getCurrentUser().getEmail(), 
													result.getFilePath(), new AsyncCallback<User>() {
														@Override
														public void onSuccess(User user) {
															SessionManager.getInstance().setCurrentUser(user);
															System.out.println("Added Image Url to user");
														}

														@Override
														public void onFailure(EncoreError error) {
															System.err.println(error.getErrorMessage());
														}
													});
											}
										});
							}

							@Override
							public void onFailure(final EncoreError error) {
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
									}
								});
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}

			profPic.setImageBitmap(thumbCropped);

		} else {
			Toast.makeText(getActivity(), "Picture not taken", Toast.LENGTH_LONG).show();
		}
		super.onActivityResult(requestCode, resultCode, cameraIntent);
	}

}
