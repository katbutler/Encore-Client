package com.katbutler.encore.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.katbutler.encore.xml.XmlDocument;
import com.katbutler.encore.xml.XmlElement;

@XmlDocument(listRootName="Users", rootName="User")
public class User implements Parcelable {
	
	@XmlElement(name="UserID")
	int userID;
	
	@XmlElement(name="FirstName")
	String firstName;
	
	@XmlElement(name="LastName")
	String lastName;
	
	@XmlElement(name="Email")
	String email;

	@XmlElement(name="PictureUrl")
	String picURL;
	
	@XmlElement(name="Bio")
	String bio;
	
	@XmlElement(name="HomeLatitude")
	double homeLatitude;
	
	@XmlElement(name="HomeLongitude")
	double homeLongitude;
	
	//default constructor
	public User () {
		setUserID(-1);
		setFirstName(null);
		setLastName(null);
		setEmail(null);
//		setPassword(null);
		setPicURL(null);
		setBio(null);
	}
	
	public User (int id, String fN, String lN, String e, /*String pw, */String pic, String b) {
		setUserID(id);
		setFirstName(fN);
		setLastName(lN);
		setEmail(e);
//		setPassword(pw);
		setPicURL(pic);
		setBio(b);
	}
	
	
	/*
	 * Getters and Setters
	 */
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
	

	/**
	 * Use to get Full name using this users first and last names.
	 * @return
	 */
	public String getFullName() {
		StringBuilder nameStringBuilder = new StringBuilder();
		
		if (firstName != null) {
			nameStringBuilder.append(getFirstName() + " ");
		}
		
		if (lastName != null) {
			nameStringBuilder.append(getLastName());
		}
		return nameStringBuilder.toString();
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String fName) {
		this.firstName = fName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lName) {
		this.lastName = lName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

	public String getPicURL() {
		return picURL;
	}

	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
	
	public double getHomeLatitude() {
		return homeLatitude;
	}

	public void setHomeLatitude(double homeLatitude) {
		this.homeLatitude = homeLatitude;
	}

	public double getHomeLongitude() {
		return homeLongitude;
	}

	public void setHomeLongitude(double homeLongitude) {
		this.homeLongitude = homeLongitude;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserId: "+getUserID() + "\n");
		sb.append("First Name: "+getFirstName() + "\n");
		sb.append("Last Name: "+getLastName() + "\n");
		sb.append("Email: "+getEmail() + "\n");
		sb.append("Pic URL: "+getPicURL() + "\n");
		sb.append("Bio: "+getBio() + "\n");
		
		return sb.toString();
	}

	
	/* -- Parcelable Implementation -- */
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};

	private User(Parcel in) {
		userID = in.readInt();
		firstName = readStringFromParcel(in);
		lastName = readStringFromParcel(in);
		email = readStringFromParcel(in);
		picURL = readStringFromParcel(in);
		bio = readStringFromParcel(in);
		homeLatitude = in.readDouble();
		homeLongitude = in.readDouble();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(getUserID());
		writeStringToParcel(out, firstName);
		writeStringToParcel(out, lastName);
		writeStringToParcel(out, email);
		writeStringToParcel(out, picURL);
		writeStringToParcel(out, bio);
		out.writeDouble(homeLatitude);
		out.writeDouble(homeLongitude);
		
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
