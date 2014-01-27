package com.katbutler.encore.model.common;

public enum ActivityType {
	NEW_FOLLOWER(" is now following "),
	ATTENDING_EVENT(" is planning to attend "),
	CHECKED_INTO_EVENT(" has just checked into ");
	
	String displayString;
	
	ActivityType(String dVal) {
		displayString = dVal;
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	public static ActivityType valueForString(String value) {
		for (ActivityType a : values()) {
			if (a.getDisplayString().equals(value) || a.toString().equals(value)) {
				return a;
			}
		}
		return null;
	}
}
