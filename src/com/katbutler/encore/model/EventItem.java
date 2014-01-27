package com.katbutler.encore.model;

public class EventItem {
	private int eventItemID;
	private int event;
	private String name;
	private String description;
	private String picURL;
	
	
	public int getEventItemID() {
		return eventItemID;
	}
	
	public void setEventItemID(int eventItemID) {
		this.eventItemID = eventItemID;
	}
	
	public int getEvent() {
		return event;
	}
	
	public void setEvent(int eventId) {
		this.event = eventId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getPicURL() {
		return picURL;
	}
	
	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}
	
	
}
