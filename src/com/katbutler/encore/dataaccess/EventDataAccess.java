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
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.Event;
import com.katbutler.encore.model.User;
import com.katbutler.encore.xml.XmlParser;

public class EventDataAccess extends DataAccess {
	
	private static EventDataAccess instance = null;
	
	public static EventDataAccess getInstance() {
		if(instance == null) {
			instance = new EventDataAccess();
		}
		return instance;
	}
	
	public void addEventToUser(final int userId, final int eventId, final AsyncCallback<Event> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "addEventForUser?userid=%d&id=%d", userId, eventId));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the Event XML
							XmlParser<Event> xmlParser = new XmlParser<Event>(Event.class);
							Event event = xmlParser.parseXmlDocument(urlConnection.getInputStream());

							callback.onSuccess(event);

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
	
	public void searchEventsTitle(final String searchString, final AsyncCallback<List<Event>> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "searchEventsTitle?search=%s", searchString));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the Event XML
							XmlParser<Event> xmlParser = new XmlParser<Event>(Event.class);
							List<Event> events = xmlParser.parseXmlDocumentList(urlConnection.getInputStream());

							System.out.println("Number of Events: " + events.size());

							callback.onSuccess(events);

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
	
	/**
	 * REST function to get all the events for a user
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void getEventsForUser(final User user, final AsyncCallback<List<Event>> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "getEventsForUser?id=%d", user.getUserID()));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the Event XML
							XmlParser<Event> xmlParser = new XmlParser<Event>(Event.class);
							List<Event> events = xmlParser.parseXmlDocumentList(urlConnection.getInputStream());

							System.out.println("Number of Events: " + events.size());

							callback.onSuccess(events);

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
