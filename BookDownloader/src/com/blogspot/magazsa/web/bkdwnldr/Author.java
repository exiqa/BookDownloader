package com.blogspot.magazsa.web.bkdwnldr;


public class Author {
	
	private String id;
	private String name;
	private String asciiName;
	private String link;
	
	public Author(String id, String name, String asciiName, String link) {
		this.id = id;
		this.name = name;
		this.asciiName = asciiName;
		this.link = link;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAsciiName() {
		return asciiName;
	}

	public void setAsciiName(String asciiName) {
		this.asciiName = asciiName;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
