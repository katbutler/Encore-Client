package com.katbutler.encore.dataaccess;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.User;
import com.katbutler.encore.xml.XmlParser;

public class AuthenticationDataAccess extends DataAccess {

	private static AuthenticationDataAccess instance = null;
	
	public static AuthenticationDataAccess getInstance() {
		if (instance == null)
			instance = new AuthenticationDataAccess();
		
		return instance;
	}
	
	/**
	 * REST function to login to the EncoreServer.
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void login(final String email, final String password, final AsyncCallback<User> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "login?email=%s&password=%s", email, password));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the User XML
							XmlParser<User> xmlParser = new XmlParser<User>(User.class);
							User user = xmlParser.parseXmlDocument(urlConnection.getInputStream());

							System.out.println(user);

							// store current logged in user in the singleton
							SessionManager.getInstance().setCurrentUser(user);

							callback.onSuccess(user);

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
	 * REST function to sign up for the Encore service.
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void signUp(final String email, final String password, final String fName, final String lName, final AsyncCallback<User> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "addUser?email=%s&fname=%s&lname=%s&password=%s", email, fName, lName, password));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the User XML
							XmlParser<User> xmlParser = new XmlParser<User>(User.class);
							User user = xmlParser.parseXmlDocument(urlConnection.getInputStream());

							System.out.println(user);

							// store current logged in user in the singleton
							SessionManager.getInstance().setCurrentUser(user);

							callback.onSuccess(user);

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
