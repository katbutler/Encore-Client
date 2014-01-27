package com.katbutler.encore.model;

import java.util.Date;

import com.katbutler.encore.model.common.ActivityType;
import com.katbutler.encore.xml.XmlDocument;
import com.katbutler.encore.xml.XmlElement;

@XmlDocument(listRootName="Activities", rootName="Activity")
public class Activity {
	
	@XmlElement(name="ActivityId")
	private int activityID;
	
	@XmlElement(name="ActivityDate")
	private Date date;
	
	@XmlElement(name="Description")
	private String description;

	@XmlElement(name="ActivityType")
	private String activityTypeStr = "";
	
	public Activity() {
	}
	
	public int getActivityID() {
		return activityID;
	}

	public void setActivityID(int activityID) {
		this.activityID = activityID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ActivityType getActivityType() {
		return ActivityType.valueForString(activityTypeStr);
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ActivityId: "+ getActivityID() + "\n");
		sb.append("Date: "+ getDate()+ "\n");
		sb.append("Description: "+ getDescription() + "\n");
		
		return sb.toString();
	}
	
}
