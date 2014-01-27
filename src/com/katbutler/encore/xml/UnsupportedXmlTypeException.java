package com.katbutler.encore.xml;

/**
 * Exception Type used whenever an Unsupported Xml Type is parsed. 
 * 
 * @author Kat Butler
 *
 */
public class UnsupportedXmlTypeException extends Exception {
	private static final long serialVersionUID = 8401283106905327565L;
	
	
	/**
	 * Create UnsupportedXmlTypeException with a String message
	 * @param message The message to display
	 */
	public UnsupportedXmlTypeException(String message) {
		super(message);
	}
	
	/**
	 * Create UnsupportedXmlTypeException that specifies the type that could not be parsed
	 * @param type The unsupported Class type
	 */
	public UnsupportedXmlTypeException(Class<?> type) {
		super("Type: " + type.toString());
	}

	
}
