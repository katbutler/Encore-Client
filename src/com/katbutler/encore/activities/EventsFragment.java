package com.katbutler.encore.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katbutler.encore.R;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.EventDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.User;

/**
 * A Fragment for the tab view to show all the current users friends.
 */
public class EventsFragment extends ListFragment {

	public EventsFragment() {
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
		View rootView = inflater.inflate(R.layout.events, container, false);

		return rootView;
	}
	
	private EventsAdapter adapter;
	
	/** 
	 * onActivityCreated is called third after the view has been created.
	 * This is where we run getView().findViewById() and attach
	 * handlers to our view
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (adapter == null)
			adapter = new EventsAdapter(getActivity());

		setListAdapter(adapter);
		
		EventDataAccess.getInstance().getEventsForUser(
				SessionManager.getInstance().getCurrentUser(), 
				new AsyncCallback<List<Event>>() {

					@Override
					public void onSuccess(final List<Event> result) {
						EventsFragment.this.getActivity().runOnUiThread(new Runnable() {
							
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
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Fragment fragment;
		Event event = (Event) v.getTag(); //getting the user that was clicked from the listview
		
		if (event != null) {
			fragment = new EventFragment();
			Bundle args = new Bundle();
			args.putParcelable("Event", event);
			fragment.setArguments(args); //now this fragment can get the user that was selected
			
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.eventslayout, fragment); //id of events.xml layout
			ft.addToBackStack(null);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
			
		}
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
			
			Date eventDate = event.getEventDate();
			SimpleDateFormat df = new SimpleDateFormat("MMM d, yy");
			dateText.setText(df.format(eventDate));

			String description = event.getDescription();
			eventDescText.setText(description);
			
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


