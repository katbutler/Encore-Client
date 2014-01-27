package com.katbutler.encore.activities;

import java.util.ArrayList;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katbutler.encore.R;
import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.UserDataAccess;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.User;
import com.katbutler.encore.util.DownloadImageTask;

/**
 * A Fragment for the tab view to show all the current users friends.
 */
public class FriendsFragment extends ListFragment {

	public FriendsFragment() {
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.following_users_layout,
				container, false);

		return rootView;
	}

	
	private FollowersAdapter adapter;

	/**
	 * onActivityCreated is called third after the view has been created. This
	 * is where we run getView().findViewById() and attach handlers to our view
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (adapter == null)
			adapter = new FollowersAdapter(getActivity());

		setListAdapter(adapter);

		UserDataAccess.getInstance().getFollowersForUser(
				SessionManager.getInstance().getCurrentUser(),
				new AsyncCallback<List<User>>() {

					@Override
					public void onSuccess(final List<User> result) {
						FriendsFragment.this.getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								adapter.upDateEntries(result);
							}
						});
						
					}

					@Override
					public void onFailure(EncoreError error) {

					}
				});
		
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Fragment fragment;
		User user = (User) v.getTag(); //getting the user that was clicked from the listview
		
		if (user != null) {
			fragment = new FriendProfileFragment();
			Bundle args = new Bundle();
			args.putParcelable("User", user);
			fragment.setArguments(args); //now this fragment can get the user that was selected
			
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.friendslayout, fragment); //id of following_users_layout.xml
			ft.addToBackStack(null);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.commit();
			
		}
	}

	public class FollowersAdapter extends BaseAdapter {

		private Context mContext;

		private LayoutInflater mLayoutInflater;

		private List<User> mEntries = new ArrayList<User>();

		public FollowersAdapter(Context context) {
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
				itemView = (RelativeLayout) mLayoutInflater.inflate(R.layout.friend_cell, parent, false);

			} else {
				itemView = (RelativeLayout) convertView;
			}

			ImageView imageView = (ImageView) itemView.findViewById(R.id.icon);
			TextView nameText = (TextView) itemView.findViewById(R.id.name_text);
			TextView emailText = (TextView) itemView.findViewById(R.id.email_text);
			
			User user = mEntries.get(position);
			itemView.setTag(user);
			
			if (user.getPicURL() != null) {
				String imageUrl = SessionManager.getInstance().getEncoreServerUrl() + user.getPicURL();
			
				new DownloadImageTask(imageView, true).execute(imageUrl);
			}

			String email = user.getEmail();
			emailText.setText(email);
			
			StringBuilder nameStringBuilder = new StringBuilder();
			if (user.getFirstName() != null) {
				nameStringBuilder.append(user.getFirstName() + " ");
			}
			
			if (user.getLastName() != null) {
				nameStringBuilder.append(user.getLastName());
			}
			
			nameText.setText(nameStringBuilder.toString());

			return itemView;
		}

		public void upDateEntries(List<User> entries) {
			mEntries = entries;
			notifyDataSetChanged();
		}
	}

}
