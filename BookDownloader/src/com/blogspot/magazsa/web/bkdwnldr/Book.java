package com.blogspot.magazsa.web.bkdwnldr;

public class Book {
	private String id;
	private String title;
	private String asciiTitle;
	
	public Book(String id, String title, String asciiTitle) {
		this.id = id;
		this.title = title;
		this.asciiTitle = asciiTitle;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setAsciiTitle(String asciiTitle) {
		this.asciiTitle = asciiTitle;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAsciiTitle() {
		return asciiTitle;
	}
	
}