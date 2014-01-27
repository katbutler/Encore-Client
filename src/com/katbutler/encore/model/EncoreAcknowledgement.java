package com.katbutler.encore.model;

import com.katbutler.encore.xml.XmlDocument;
import com.katbutler.encore.xml.XmlElement;


@XmlDocument(listRootName="EncoreAcknowledgements", rootName="EncoreAcknowledgement")
public class EncoreAcknowledgement {
	
	public static final int ERROR_CODE = 207;
	
	@XmlElement(name="StatusCode")
	private Integer statusCode;
	
	@XmlElement(name="StatusMessage")
	private String statusMessage;
	
	public EncoreAcknowledgement() {
		
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	

}
