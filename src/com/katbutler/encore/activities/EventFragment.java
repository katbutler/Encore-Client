package com.katbutler.encore.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.katbutler.encore.R;
import com.katbutler.encore.activities.FriendProfileFragment.EventsAdapter;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.EventDataAccess;
import com.katbutler.encore.dataaccess.UserDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.User;
import com.katbutler.encore.util.DownloadImageTask;
import com.katbutler.encore.util.MyLocation;
import com.katbutler.encore.util.MyLocation.LocationResult;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EventFragment extends Fragment {
	public EventFragment() {
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
		View rootView = inflater.inflate(R.layout.event, container, false);

		return rootView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		User curUser = SessionManager.getInstance().getCurrentUser();
		
		TextView eventTitle = (TextView) getView().findViewById(R.id.eventTitleTextView);
		TextView description = (TextView) getView().findViewById(R.id.eventDescriptionTextView);
		Button checkInBtn = (Button) getView().findViewById(R.id.checkInBtn);
		
		final Event event = (Event)getArguments().getParcelable("Event");
		
		Double eventLat = event.getEventLatitude();
		Double eventLong = event.getEventLongitude();
		
		Date eventDate = event.getEventDate();
		Date dateCreated = event.getDateCreated();
		Date dateUpdated = event.getDateUpdated();
		
		eventTitle.setText(event.getTitle());
		description.setText(event.getDescription());
		
		final Date todaysDate = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy");
		
		final String todaysDateString = df.format(todaysDate);
		final String eventDateString = df.format(event.getEventDate());
		
		checkInBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.out.println(todaysDateString + "\n" + eventDateString);
				
				if(todaysDateString.equals(eventDateString)) {
					MyLocation myLocation = new MyLocation();
					myLocation.getLocation(EventFragment.this.getActivity(), new LocationResult() {
						
						@Override
						public void gotLocation(Location location) {
							// check if user and event have matching locations
							if (event == null)
								System.out.println("My Event is null");
							Double distance = distance(event.getEventLatitude(), event.getEventLongitude(), location.getLatitude(), location.getLongitude());
							if (Math.abs(distance) <= 500) { //if you are within 500m of the event
								Toast.makeText(EventFragment.this.getActivity(), "Welcome to " + event.getTitle(), Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(EventFragment.this.getActivity(), "Sorry, you aren't at " + event.getTitle() + " yet!", Toast.LENGTH_LONG).show();
							}
						}
					});
				} else {
					Toast.makeText(EventFragment.this.getActivity(), event.getTitle() + " hasn't started yet! Come back when you're at the event!", Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}
	
	double distance(double lat1, double lng1, double lat2, double lng2) {
		double d2r = Math.PI / 180;
		double distance = 0;

		try{
		    double dlong = (lng2 - lng1) * d2r;
		    double dlat = (lat2 - lat1) * d2r;
		    double a =
		        Math.pow(Math.sin(dlat / 2.0), 2)
		            + Math.cos(lat1 * d2r)
		            * Math.cos(lat2 * d2r)
		            * Math.pow(Math.sin(dlong / 2.0), 2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		    double d = 6367 * c;

		    return d;

		} catch(Exception e){
		    e.printStackTrace();
		}
		
		return Double.MIN_VALUE;
	}
	
	//TODO: Might need an adapter here...
}

