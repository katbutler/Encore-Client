package com.katbutler.encore.model;

import com.katbutler.encore.xml.XmlDocument;
import com.katbutler.encore.xml.XmlElement;


@XmlDocument(listRootName="Files", rootName="File")
public class EncoreImageFile {
	
	@XmlElement(name="FilePath")
	private String filePath;
	
	@XmlElement(name="Size1")
	private Long size1;
	
	@XmlElement(name="Size2")
	private Long size2;
	
	public EncoreImageFile() {
		
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public Long getSize1() {
		return size1;
	}

	public void setSize1(Long size1) {
		this.size1 = size1;
	}

	public Long getSize2() {
		return size2;
	}

	public void setSize2(Long size2) {
		this.size2 = size2;
	}



}
