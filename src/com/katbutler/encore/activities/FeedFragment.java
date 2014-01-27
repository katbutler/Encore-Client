package com.katbutler.encore.activities;

import java.util.ArrayList;
import java.util.List;

import com.katbutler.encore.R;
import com.katbutler.encore.activities.EventsFragment.EventsAdapter;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.ActivityDataAccess;
import com.katbutler.encore.dataaccess.EventDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.Activity;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.common.ActivityType;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FeedFragment extends ListFragment {
	
	public FeedFragment() {
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
		View rootView = inflater.inflate(R.layout.feed, container, false);
		    
		return rootView;
	}
	
	private FeedAdapter adapter;
	
	/** 
	 * onActivityCreated is called third after the view has been created.
	 * This is where we run getView().findViewById() and attach
	 * handlers to our view
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (adapter == null)
			adapter = new FeedAdapter(getActivity());

		setListAdapter(adapter);
		
		ActivityDataAccess.getInstance().getActivitiesForUser(
				SessionManager.getInstance().getCurrentUser(), 
				new AsyncCallback<List<Activity>>() {

					@Override
					public void onSuccess(final List<Activity> result) {
						FeedFragment.this.getActivity().runOnUiThread(new Runnable() {
							
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
	}
	
	
	public class FeedAdapter extends BaseAdapter {
		
		private Context mContext;

		private LayoutInflater mLayoutInflater;
		
		private List<Activity> mEntries = new ArrayList<Activity>();
		
		public FeedAdapter(Context context) {
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
				itemView = (RelativeLayout) mLayoutInflater.inflate(R.layout.feed_cell, parent, false);

			} else {
				itemView = (RelativeLayout) convertView;
			}

			TextView activityText = (TextView) itemView.findViewById(R.id.activity_text);
			ImageView add_icon = (ImageView) itemView.findViewById(R.id.add_icon);
			
			Activity activity = mEntries.get(position);
			itemView.setTag(activity);

			String description = activity.getDescription();
			activityText.setText(description);
			
			if(activity.getActivityType() == ActivityType.ATTENDING_EVENT) {
				add_icon.setImageResource(R.drawable.eventadd);
			} else if(activity.getActivityType() == ActivityType.NEW_FOLLOWER) {
				add_icon.setImageResource(R.drawable.friendadd);
			} else {
				//default icon
			}
			
			return itemView;
		}
		
		public void upDateEntries(List<Activity> entries) {
			mEntries = entries;
			notifyDataSetChanged();
		}
		
	}
}
