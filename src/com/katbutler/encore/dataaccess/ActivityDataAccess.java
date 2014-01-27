package com.katbutler.encore.dataaccess;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.Activity;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.User;
import com.katbutler.encore.xml.XmlParser;

public class ActivityDataAccess extends DataAccess {
	
	private static ActivityDataAccess instance = null;
	
	public static ActivityDataAccess getInstance() {
		if(instance == null) {
			instance = new ActivityDataAccess();
		}
		return instance;
	}
	
	/**
	 * REST function to get all the activities for a user
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void getActivitiesForUser(final User user, final AsyncCallback<List<Activity>> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "getActivitiesForUser?id=%d", user.getUserID()));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the Event XML
							XmlParser<Activity> xmlParser = new XmlParser<Activity>(Activity.class);
							List<Activity> activities = xmlParser.parseXmlDocumentList(urlConnection.getInputStream());

							System.out.println("Number of Activities: " + activities.size());

							callback.onSuccess(activities);

						} else if (statusCode == EncoreError.ERROR_CODE) { // Status Code 207 is an EncoreError
							// Parse the EncoreError
							XmlParser<EncoreError> xmlParser = new XmlParser<EncoreError>(EncoreError.class);
							EncoreError error = xmlParser.parseXmlDocument(urlConnection.getInputStream());

							System.err.println(error.getErrorMessage());

							callback.onFailure(error);
						}

					} catch (XmlPullParserException e) {
						callback.onFailure(new EncoreError("Could not parse response"));
						e.printStackTrace();
					} catch (InstantiationException e) {
						callback.onFailure(new EncoreError("Could not parse response"));
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						callback.onFailure(new EncoreError("Could not parse response"));
						e.printStackTrace();
					} catch (ParseException e) {
						callback.onFailure(new EncoreError("Could not parse Date " + e.getMessage()));
						e.printStackTrace();
					}

				} catch (MalformedURLException e) {
					callback.onFailure(new EncoreError("Could not connect to server."));
					e.printStackTrace();
				} catch (ProtocolException e) {
					callback.onFailure(new EncoreError("Could not connect to server."));
					e.printStackTrace();
				} catch (IOException e) {
					callback.onFailure(new EncoreError("Could not connect to server."));
					e.printStackTrace();
				} finally {
					if (urlConnection != null)
						urlConnection.disconnect();
				}
			}
		}).start();

	}
	
	
}
