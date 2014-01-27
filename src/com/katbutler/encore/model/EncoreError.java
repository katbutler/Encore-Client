package com.katbutler.encore.model;

import com.katbutler.encore.xml.XmlDocument;
import com.katbutler.encore.xml.XmlElement;


@XmlDocument(listRootName="Errors", rootName="Error")
public class EncoreError {
	
	public static final int ERROR_CODE = 207;
	
	@XmlElement(name="ErrorMessage")
	private String errorMessage;
	
	public EncoreError() {
		
	}
	
	public EncoreError(String errorMessage) {
		setErrorMessage(errorMessage);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
