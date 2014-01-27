package com.katbutler.encore.dataaccess;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.katbutler.encore.client.SessionManager;
import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.User;
import com.katbutler.encore.xml.XmlParser;

public class UserDataAccess extends DataAccess {

	private static UserDataAccess instance = null;
	
	public static UserDataAccess getInstance() {
		if (instance == null)
			instance = new UserDataAccess();
		
		return instance;
	}
	
	/**
	 * REST function to add a new follower
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void followNewUser(final User curUser, final String userEmailToFollow, final AsyncCallback<User> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "followNewUser?id=%d&newUserEmail=%s", curUser.getUserID(), userEmailToFollow));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the User XML
							XmlParser<User> xmlParser = new XmlParser<User>(User.class);
							User user = xmlParser.parseXmlDocument(urlConnection.getInputStream());

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
	 * REST function to add a picture url to a User
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void addPicUrlToUser(final String email, final String picUrl, final AsyncCallback<User> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "addPicUrlToUser?email=%s&picurl=%s", email, picUrl));
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
				} catch (ParseException e) {
					callback.onFailure(new EncoreError("Could not parse Date " + e.getMessage()));
					e.printStackTrace();
				} finally {
					if (urlConnection != null)
						urlConnection.disconnect();
				}
			}
		}).start();

	}
	
	/**
	 * REST function to update a Users infor
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void updateUser(final String email, final String fName, final String lName, final String bio, final String picUrl, final AsyncCallback<User> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					String encodedBio = URLEncoder.encode(bio, "UTF-8");
					
					String urlStr = String.format("updateUser?email=%s&fname=%s&lname=%s&bio=%s&picurl=%s", email, fName, lName, encodedBio, picUrl);
					
					System.out.println("URL: " + urlStr);
					
					url = new URL(SessionManager.getInstance().getEncoreServerUrl() + urlStr);
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

	
	/**
	 * REST function to get all the followers for a user
	 * @param email
	 * @param password
	 * @param callback
	 */
	public void getFollowersForUser(final User user, final AsyncCallback<List<User>> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "getFollowersForUser?id=%d", user.getUserID()));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the User XML
							XmlParser<User> xmlParser = new XmlParser<User>(User.class);
							List<User> users = xmlParser.parseXmlDocumentList(urlConnection.getInputStream());

							System.out.println("Number of Followers: " + users.size());

							callback.onSuccess(users);

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

	public void getUser(final String email, final AsyncCallback<User> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "getUser?email=%s", email));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the User XML
							XmlParser<User> xmlParser = new XmlParser<User>(User.class);
							User user = xmlParser.parseXmlDocument(urlConnection.getInputStream());

							System.out.println("Number of Followers: " + user);

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

	public void setHomeLocation(final User user, final double latitude, final double longitude, final AsyncCallback<User> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				HttpURLConnection urlConnection = null;
				try {
					url = new URL(String.format(SessionManager.getInstance().getEncoreServerUrl() + "setHomeLocationForUser?id=%d&lat=%s&lng=%s", user.getUserID(), latitude, longitude));
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");

					int statusCode = urlConnection.getResponseCode();
					System.out.println("Status Code: " + statusCode);

					try {
						if (statusCode == 200) {
							// Parse the User XML
							XmlParser<User> xmlParser = new XmlParser<User>(User.class);
							User user = xmlParser.parseXmlDocument(urlConnection.getInputStream());

							System.out.println("Number of Followers: " + user);

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
