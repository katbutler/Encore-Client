package com.katbutler.encore.client;

import com.katbutler.encore.model.User;

public class SessionManager {

	public static String SESSION_DATA = "session_data";
	
	
	private static SessionManager instance = null;
	
	private final String encoreServerUrl = "http://192.168.0.11:8080/encore/";

	private User currentUser;
	
	public static SessionManager getInstance() {
		if(instance == null) {
			instance = new SessionManager();
		}
		
		return instance;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public String getEncoreServerUrl() {
		return encoreServerUrl;
	}
	
}
