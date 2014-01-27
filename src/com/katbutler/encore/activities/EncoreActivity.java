package com.katbutler.encore.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.katbutler.encore.R;
import com.katbutler.encore.activities.EventsFragment.EventsAdapter;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.DataAccess;
import com.katbutler.encore.dataaccess.EventDataAccess;
import com.katbutler.encore.dataaccess.UserDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.User;

public class EncoreActivity extends FragmentActivity implements ActionBar.TabListener {
	Context context;
	CheckedTextView tempCheck;
	Event selectedEvent;
	EditText email;
	EditText searchField;
	ListView listView;
	ImageView searchBtn;
	SessionManager sessionManager = SessionManager.getInstance();
	User curUser = sessionManager.getCurrentUser();
	
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /**
     * Create the activity. Sets up an {@link ActionBar} with tabs, and then configures the
     * {@link ViewPager} contained inside R.layout.activity_main.
     *
     * <p>A {@link SectionsPagerAdapter} will be instantiated to hold the different pages of
     * fragments that are to be displayed. A
     * {@link android.support.v4.view.ViewPager.SimpleOnPageChangeListener} will also be configured
     * to receive callbacks when the user swipes between pages in the ViewPager.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // Load the UI from res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        
        // Set up the action bar. The navigation mode is set to NAVIGATION_MODE_TABS, which will
        // cause the ActionBar to render a set of tabs. Note that these tabs are *not* rendered
        // by the ViewPager; additional logic is lower in this file to synchronize the ViewPager
        // state with the tab state. (See mViewPager.setOnPageChangeListener() and onTabSelected().)
        // BEGIN_INCLUDE (set_navigation_mode)
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // END_INCLUDE (set_navigation_mode)

        // BEGIN_INCLUDE (setup_view_pager)
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // END_INCLUDE (setup_view_pager)

        // When swiping between different sections, select the corresponding tab. We can also use
        // ActionBar.Tab#select() to do this if we have a reference to the Tab.
        // BEGIN_INCLUDE (page_change_listener)
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        // END_INCLUDE (page_change_listener)
        
        // BEGIN_INCLUDE (add_tabs)
        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter. Also
            // specify this Activity object, which implements the TabListener interface, as the
            // callback (listener) for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        // END_INCLUDE (add_tabs)
    }
    
    /*
     * Fix so user can't go back 
     * to login page
     */
//    @Override
//    public void onBackPressed() {
//    }

    /**
     * Update {@link ViewPager} after a tab has been selected in the ActionBar.
     *
     * @param tab Tab that was selected.
     * @param fragmentTransaction A {@link FragmentTransaction} for queuing fragment operations to
     *                            execute once this method returns. This FragmentTransaction does
     *                            not support being added to the back stack.
     */
    // BEGIN_INCLUDE (on_tab_selected)
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, tell the ViewPager to switch to the corresponding page.
        mViewPager.setCurrentItem(tab.getPosition());
    }
    // END_INCLUDE (on_tab_selected)

    /**
     * Unused. Required for {@link ActionBar.TabListener}.
     */
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * Unused. Required for {@link ActionBar.TabListener}.
     */
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    // BEGIN_INCLUDE (fragment_pager_adapter)
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages. This provides the data for the {@link ViewPager}.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
    // END_INCLUDE (fragment_pager_adapter)

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // BEGIN_INCLUDE (fragment_pager_adapter_getitem)
        /**
         * Get fragment corresponding to a specific position. This will be used to populate the
         * contents of the {@link ViewPager}.
         *
         * @param position Position to fetch fragment for.
         * @return Fragment for specified position.
         */
        @Override
        public Fragment getItem(int position) {
        	Fragment fragment = null;
        	
        	switch (position) {
			case 0: 
	            fragment = new ProfileFragment();
				break;
				
			case 1:  
				fragment = new FeedFragment();
				break;
				
			case 2:  
				fragment = new FriendsFragment();
				break;
				
			case 3: 
				fragment = new EventsFragment();
				break;

			default:
				throw new UnsupportedOperationException("There is no fragment to load for position + " + position);
			}

            return fragment;
        }
        // END_INCLUDE (fragment_pager_adapter_getitem)

        // BEGIN_INCLUDE (fragment_pager_adapter_getcount)
        /**
         * Get number of pages the {@link ViewPager} should render.
         *
         * @return Number of fragments to be rendered as pages.
         */
        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
        // END_INCLUDE (fragment_pager_adapter_getcount)

        // BEGIN_INCLUDE (fragment_pager_adapter_getpagetitle)
        /**
         * Get title for each of the pages. This will be displayed on each of the tabs.
         *
         * @param position Page to fetch title for.
         * @return Title for specified page.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.profile_title).toUpperCase(l);
                case 1:
                    return getString(R.string.feed_title).toUpperCase(l);
                case 2:
                    return getString(R.string.friends_title).toUpperCase(l);
                case 3:
                    return getString(R.string.events_title).toUpperCase(l);
            }
            return null;
        }
        // END_INCLUDE (fragment_pager_adapter_getpagetitle)
    }
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_logout:
	      SharedPreferences settings = getSharedPreferences(SessionManager.SESSION_DATA, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.remove("session");
	      
	      // Commit the edits!
	      editor.commit();
	      
	      
	      Intent myIntent = new Intent(EncoreActivity.this, LoginActivity.class);
	      myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	      EncoreActivity.this.startActivity(myIntent);
	      
	      break;
	    
	    case R.id.action_follow:
	      followNewUser();
	      break;
	      
	    case R.id.action_search:
	      searchForEvent();
	      break;

	    default:
	      break;
	    }

	    return true;
	} 
	
	private EventsAdapter adapter;
	
	public void searchForEvent() {
		LayoutInflater li = LayoutInflater.from(this);
		final View search = li.inflate(R.layout.search_dialog, null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(search);
		
		searchField = (EditText) search.findViewById(R.id.searchField);
		listView = (ListView) search.findViewById(R.id.listView);
		searchBtn = (ImageView) search.findViewById(R.id.searchBtn);
		
		if (adapter == null)
			adapter = new EventsAdapter(this);

		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
				selectedEvent = (Event) view.getTag();
				if(tempCheck != null) {
					tempCheck.setChecked(false);
				}
				CheckedTextView curCheckbox = (CheckedTextView) view.findViewById(R.id.event_name_text);
				tempCheck = curCheckbox;
				tempCheck.setChecked(true);
				
			}
		});
		
		EventDataAccess.getInstance().searchEventsTitle("", new AsyncCallback<List<Event>>() {

			@Override
			public void onSuccess(final List<Event> result) {
				EncoreActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						adapter.upDateEntries(result);
					}
				});
			}

			@Override
			public void onFailure(EncoreError error) {
				// TODO Auto-generated method stub
				
			}
		});
		
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String searchText = searchField.getText().toString();
				EventDataAccess.getInstance().searchEventsTitle(searchText, new AsyncCallback<List<Event>>() {

					@Override
					public void onSuccess(final List<Event> result) {
						EncoreActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adapter.upDateEntries(result);
							}
						});
					}

					@Override
					public void onFailure(EncoreError error) {
//						Toast.makeText(context, error.getErrorMessage(), Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		
		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Attend!", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//add event to current user
						
						EventDataAccess.getInstance().addEventToUser(curUser.getUserID(), selectedEvent.getEventID(), new AsyncCallback<Event>() {

							@Override
							public void onSuccess(Event event) {
								EncoreActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										System.out.println(selectedEvent.getTitle());
										Toast.makeText(EncoreActivity.this, "See you at " + selectedEvent.getTitle() + "!", Toast.LENGTH_LONG).show();
									}
								});
								
							}

							@Override
							public void onFailure(final EncoreError error) {
								EncoreActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(EncoreActivity.this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
									}
								});
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
	
	public void followNewUser() {
		LayoutInflater li = LayoutInflater.from(this);
		View addFollower = li.inflate(R.layout.addfollower_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(addFollower);
		
		email = (EditText) addFollower.findViewById(R.id.emailField);
		email.setText("yourfriend@email.com");
		
		// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							final String emailText = email.getText().toString();
								
							UserDataAccess.getInstance().getUser(emailText, new AsyncCallback<User>() {

								@Override
								public void onSuccess(User result) {
									EncoreActivity.this.runOnUiThread(new Runnable() {
										@Override
										public void run() {
											//ADD NEW FOLLOWER
											UserDataAccess.getInstance().followNewUser(curUser, emailText, new AsyncCallback<User>() {
												@Override
												public void onSuccess(final User result) {
													EncoreActivity.this.runOnUiThread(new Runnable() {
														
														@Override
														public void run() {
															Toast.makeText(EncoreActivity.this, "Successfully followed " + result.getFirstName(), Toast.LENGTH_LONG).show();
														}
													});
												}

												@Override
												public void onFailure(final EncoreError error) {
													EncoreActivity.this.runOnUiThread(new Runnable() {
														
														@Override
														public void run() {
															Toast.makeText(EncoreActivity.this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
														}
													});
												}
											});
										}
									});
								}

								@Override
								public void onFailure(EncoreError error) {
									Intent i = new Intent(Intent.ACTION_SEND);
							    	i.setType("message/rfc822");
							    	i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailText});
							    	i.putExtra(Intent.EXTRA_SUBJECT, "Try out Encore!");
							    	i.putExtra(Intent.EXTRA_TEXT   , "Hey! Your friend, " + curUser.getFirstName() + " wants to connect with you on Encore.");
							    	try {
							    	    startActivity(Intent.createChooser(i, "Send mail..."));
							    	} catch (android.content.ActivityNotFoundException ex) {
							    	    Toast.makeText(EncoreActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
							    	}
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
	
	
	public class EventsAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater mLayoutInflater;

		private List<Event> mEntries = new ArrayList<Event>();

		public EventsAdapter(Context context) {
			mContext = context;
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mEntries.size();
		}

		@Override
		public Object getItem(int position) {
			return mEntries.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout itemView;
			if (convertView == null) {
				itemView = (LinearLayout) mLayoutInflater.inflate(R.layout.search_event_cell, parent, false);

			} else {
				itemView = (LinearLayout) convertView;
			}

			CheckedTextView eventTitleText = (CheckedTextView) itemView.findViewById(R.id.event_name_text);
			
			Event event = mEntries.get(position);
			itemView.setTag(event);

			StringBuilder nameStringBuilder = new StringBuilder();
			if (event.getTitle() != null) {
				nameStringBuilder.append(event.getTitle());
			}
			
			eventTitleText.setText(nameStringBuilder.toString());

			return itemView;
		}

		public void upDateEntries(List<Event> entries) {
			mEntries = entries;
			notifyDataSetChanged();
		}
	}

}
