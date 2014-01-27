package com.katbutler.encore.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * A Generic XML Parser that can parse any model class that uses the {@link XmlDocument} annotation.
 * You must also mark the fields within the model class witht eh {@link XmlElement} annotation.
 *  
 * @author Kat Butler
 *
 * @param <T>
 */
public class XmlParser<T> {
	
	private Class<T> klass;
	private String ns = null;
	private List<T> itemList;
	private String listRootTag;
	private String rootTag;
	
	public XmlParser(Class<T> klass) {
		this.klass = klass;
	}
	
	public T parseXmlDocument(InputStream in) throws XmlPullParserException, IOException, InstantiationException, IllegalAccessException, ParseException {
		XmlDocument xmlDocument = klass.getAnnotation(XmlDocument.class);
		if(xmlDocument != null) {
			ns = xmlDocument.namespace().equals("") ? null : xmlDocument.namespace(); // Does the namespace need to be null or is "" ok?
			listRootTag = xmlDocument.listRootName();
			rootTag = xmlDocument.rootName();
			
			try {
	        	System.out.println("IN XMLPARSER.PARSE");
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in, null);
	            parser.nextTag();
	            return readItem(parser);
	        } finally {
	            in.close();
	        }
			
		} else {
			throw new IllegalArgumentException("XmlParser can only Parse Classes with the XmlDocument annotation.");
		}
	}
	
	/**
	 * Parse an {@link XmlDocument} for a list of items
	 * @param in
	 * @param klass
	 * @return the list of items for this XML document
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public List<T> parseXmlDocumentList(InputStream in) throws XmlPullParserException, IOException, ParseException {
		XmlDocument xmlDocument = klass.getAnnotation(XmlDocument.class);
		if(xmlDocument != null) {
			ns = xmlDocument.namespace().equals("") ? null : xmlDocument.namespace(); // Does the namespace need to be null or is "" ok?
			listRootTag = xmlDocument.listRootName();
			rootTag = xmlDocument.rootName();
			
			
			try {
	        	System.out.println("IN XMLPARSER.PARSE");
	            XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in, null);
	            parser.nextTag();
	            return readFeed(parser);
	        } finally {
	            in.close();
	        }
			
		} else {
			throw new IllegalArgumentException("XmlParser can only Parse Classes with the XmlDocument annotation.");
		}
	}
    
	/**
	 * Read the XML Feed and create a list of the generic classes to return
	 * @param parser
	 * @param klass
	 * @return the list of generic classes
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ParseException 
	 */
    private List<T> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
    	itemList = new ArrayList<T>();
        
        System.out.println("IN READFEED METHOD");
        parser.require(XmlPullParser.START_TAG, ns, listRootTag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            // Starts by looking for the entry tag
            if (tag.equals(rootTag)) {
            	try {
					itemList.add(readItem(parser));
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } else if (tag.equals(listRootTag)) {
            	System.out.println("Hit List Root Tag. Move along");
            } else {
                skip(parser);
            }
        }  
        
        return itemList;
    }
    
      
    /**
     * Parses the contents of a XmlDocument item
     * @param parser
     * @param klass
     * @return the item that was parsed
     * @throws XmlPullParserException
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ParseException 
     */
    private T readItem(XmlPullParser parser) throws XmlPullParserException, IOException, InstantiationException, IllegalAccessException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, rootTag);
        
        T item = klass.newInstance();
        
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            
            String name = parser.getName(); // Name of Tag
            boolean tagHasField = false;
            
            // Loop through all the Classes fields for the corresponding tag
			for (Field field : klass.getDeclaredFields()) {
				Class<?> type = field.getType();
				// String fieldName = field.getName(); // Get the Name of the Classes Field
				
				// Need the annotation to check the tag name
				XmlElement xmlElement = field.getAnnotation(XmlElement.class);
				if (xmlElement != null) {
					
					
					// Find the corresponding field for this tag to set
					if (name.equals(xmlElement.name())) {
						try {
							
							parser.require(XmlPullParser.START_TAG, ns, xmlElement.name());
							String value = readText(parser);
							
							field.setAccessible(true);
							
							// Check the type of the field and set the value occordingly
							if (type.isAssignableFrom(String.class)) {
								field.set(item, value);
								tagHasField = true;
							} else if (type.isAssignableFrom(Date.class)) {
								//2013-12-07 13:38:37.0
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
								
								field.set(item, dateFormat.parse(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(Integer.class)) {
								field.setInt(item, Integer.parseInt(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(int.class)) {
								field.setInt(item, Integer.parseInt(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(Long.class)) {
								field.setLong(item, Long.parseLong(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(long.class)) {
								field.setLong(item, Long.parseLong(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(Double.class)) {
								field.setDouble(item, Double.parseDouble(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(double.class)) {
								field.setDouble(item, Double.parseDouble(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(Boolean.class)) {
								field.setBoolean(item, Boolean.parseBoolean(value));
								tagHasField = true;
							} else if (type.isAssignableFrom(boolean.class)) {
								field.setBoolean(item, Boolean.parseBoolean(value));
								tagHasField = true;
							} else {
								throw new UnsupportedXmlTypeException(type);
							}
							
							if (tagHasField) {
								parser.require(XmlPullParser.END_TAG, ns, xmlElement.name());
	//							break;
							}
	
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedXmlTypeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
						// We must skip the XML tag if the Model Class does not
						// have a corresponding field.
						if (!tagHasField) {
//							skip(parser);
						}
					} else {
						// field doesnt have XmlElement Annotation
					}
					
				} 
			}
            
        }
        
        return item;
    }

    /**
     * For the tags inner value, extracts their text values.
     * @param parser
     * @return the text between the tags
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    /**
     * Skip a Tag
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }

	
}
