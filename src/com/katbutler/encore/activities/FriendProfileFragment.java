package com.katbutler.encore.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katbutler.encore.R;
import com.katbutler.encore.activities.EventsFragment.EventsAdapter;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.EventDataAccess;
import com.katbutler.encore.dataaccess.UserDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.User;
import com.katbutler.encore.util.DownloadImageTask;

public class FriendProfileFragment extends Fragment {
	
	
	public FriendProfileFragment() {
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
		View rootView = inflater.inflate(R.layout.friend_profile, container, false);

		return rootView;
	}
	
	private EventsAdapter adapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListView eventsList = (ListView) getView().findViewById(R.id.listView);
		
		if (adapter == null)
			adapter = new EventsAdapter(getActivity());

		eventsList.setAdapter(adapter);
		
		User user = (User)getArguments().getParcelable("User");
		
		EventDataAccess.getInstance().getEventsForUser(
				user, 
				new AsyncCallback<List<Event>>() {

					@Override
					public void onSuccess(final List<Event> result) {
						FriendProfileFragment.this.getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								adapter.upDateEntries(result);
							}
						});
						
					}

					@Override
					public void onFailure(EncoreError error) {
						System.out.println(error.getErrorMessage());
					}
				});
		
		if (user == null)
			throw new IllegalStateException(getClass().getSimpleName() + " should not be started without a User in its bundle.");
		
		ImageView profilePic = (ImageView) getView().findViewById(R.id.profilePic);
		TextView nameText = (TextView) getView().findViewById(R.id.fullNameText);
		TextView bioText = (TextView) getView().findViewById(R.id.bioText);
		
		nameText.setText(user.getFullName());
		
		if (user.getPicURL() != null)
			new DownloadImageTask(profilePic, true).execute(SessionManager.getInstance().getEncoreServerUrl() + user.getPicURL());
		
		if (user.getBio() != null)
			bioText.setText(user.getBio());
		
		setCounts();
		
		
	}
	
	public void setCounts() {
		User user = (User)getArguments().getParcelable("User"); //get current selected user
		
		final TextView friendsCount = (TextView) getView().findViewById(R.id.friendsCount);
		final TextView eventsCount = (TextView) getView().findViewById(R.id.eventsCount);
		
		EventDataAccess.getInstance().getEventsForUser(
				user, 
				new AsyncCallback<List<Event>>() {

					@Override
					public void onSuccess(final List<Event> result) {
						
						FriendProfileFragment.this.getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								int eventCount = result.size();
								eventsCount.setText("(" + eventCount + ")");
							}
						});
					}

					@Override
					public void onFailure(EncoreError error) {
						System.out.println(error.getErrorMessage());
					}
				});
		
		UserDataAccess.getInstance().getFollowersForUser(
				user, 
				new AsyncCallback<List<User>>() {

					@Override
					public void onSuccess(final List<User> result) {
							FriendProfileFragment.this.getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								int userCount = result.size();
								friendsCount.setText("(" + userCount + ")");
							}
						});
					}

					@Override
					public void onFailure(EncoreError error) {
						System.out.println(error.getErrorMessage());
					}
				});
		
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
			RelativeLayout itemView;
			if (convertView == null) {
				itemView = (RelativeLayout) mLayoutInflater.inflate(R.layout.event_cell, parent, false);

			} else {
				itemView = (RelativeLayout) convertView;
			}

			TextView eventTitleText = (TextView) itemView.findViewById(R.id.event_name_text);
			TextView eventDescText = (TextView) itemView.findViewById(R.id.event_desc_text);
			TextView dateText = (TextView) itemView.findViewById(R.id.dateText);
			
			Event event = mEntries.get(position);
			itemView.setTag(event);

			String description = event.getDescription();
			eventDescText.setText(description);
			
			Date eventDate = event.getEventDate();
			SimpleDateFormat df = new SimpleDateFormat("MMM d, yy");
			dateText.setText(df.format(eventDate));
			
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

