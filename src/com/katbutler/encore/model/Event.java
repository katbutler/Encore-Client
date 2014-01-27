package com.katbutler.encore.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.katbutler.encore.xml.XmlDocument;
import com.katbutler.encore.xml.XmlElement;

@XmlDocument(listRootName="Events", rootName="Event")
public class Event implements Parcelable {
	
	@XmlElement(name="ID")
	private int eventID;
	
	@XmlElement(name="Title")
	private String title;
	
	@XmlElement(name="DateCreated")
	private Date dateCreated;
	
	@XmlElement(name="DateUpdated")
	private Date dateUpdated;
	
	@XmlElement(name="EventDate")
	private Date eventDate;
	
	@XmlElement(name="Description")
	private String description;
	
	@XmlElement(name="EventLatitude")
	double eventLatitude;
	
	@XmlElement(name="EventLongitude")
	double eventLongitude;
	
	private Set<User> users = new HashSet<User>(0);
	
	public Event() {
		
	}
	
	public int getEventID() {
		return eventID;
	}
	
	public void setEventID(int eventID) {
		this.eventID = eventID;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public Date getDateUpdated() {
		return dateUpdated;
	}
	
	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	
	public Date getEventDate() {
		return eventDate;
	}
	
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Double getEventLatitude() {
		return eventLatitude;
	}

	public void setEventLatitude(Double eventLatitude) {
		this.eventLatitude = eventLatitude;
	}

	public Double getEventLongitude() {
		return eventLongitude;
	}

	public void setEventLongitude(Double eventLongitude) {
		this.eventLongitude = eventLongitude;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EventId: "+ getEventID() + "\n");
		sb.append("Title: "+ getTitle() + "\n");
		sb.append("DateCreated: "+ getDateCreated() + "\n");
		sb.append("DateUpdated: "+ getDateUpdated() + "\n");
		sb.append("EventDate: "+ getEventDate() + "\n");
		sb.append("Description: "+ getDescription() + "\n");
		
		return sb.toString();
	}


	/* -- Parcelable Implementation -- */
	
	public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
		public Event createFromParcel(Parcel in) {
			return new Event(in);
		}

		public Event[] newArray(int size) {
			return new Event[size];
		}
	};

	private Event(Parcel in) {
		eventID = in.readInt();
		title = readStringFromParcel(in);
		description = readStringFromParcel(in);
		eventLatitude = in.readDouble();
		eventLongitude = in.readDouble();
		eventDate = new Date(in.readLong());
		dateCreated = new Date(in.readLong());
		dateUpdated = new Date(in.readLong());
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(getEventID());
		writeStringToParcel(out, title);
		writeStringToParcel(out, description);
		out.writeDouble(eventLatitude);
		out.writeDouble(eventLongitude);
		out.writeLong(eventDate.getTime());
		out.writeLong(dateCreated.getTime());
		out.writeLong(dateUpdated.getTime());
	}
	
	/**
	 * Used to write a string that might be null
	 * @param p
	 * @param s
	 */
	private void writeStringToParcel(Parcel p, String s) {
	    p.writeByte((byte)(s != null ? 1 : 0));
	    if (s != null)
	    	p.writeString(s);
	}

	/**
	 * Used to read a string that might be null
	 * @param p
	 * @return
	 */
	private String readStringFromParcel(Parcel p) {
	    boolean isPresent = p.readByte() == (byte)1;
	    return isPresent ? p.readString() : null;
	}
	
	
	
}
