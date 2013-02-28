package com.blogspot.magazsa.web.bkdwnldr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This program provides easy way to download books from http://many-books.org
 * web-site.
 * 
 * This program is exclusively for educational purposes. All rights belong to
 * their respective owners.
 * 
 * @author Magaz Serhii
 * 
 */
public class BookDownloader {

	private static final String PATH = "http://www.many-books.org";
	private static final String DOWNLOAD_PATH = PATH + "/download";

	private String downloadsDir = "/Downloads";

	public BookDownloader() {
	}

	/**
	 * Downloads and saves file from remote url
	 * 
	 * @param url
	 * @param file
	 */
	public void download(URL url, File file) {
		BufferedInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new BufferedInputStream(url.openStream());
			out = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = in.read(buffer, 0, 1024)) != -1) {
				out.write(buffer, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setDownloadsDir(String downloadsDir) {
		this.downloadsDir = downloadsDir;
	}

	public String getLinkByFirstLetter(String letter) {
		Document page;
		Connection con = Jsoup.connect(PATH);
		con.timeout(6000);
		try {
			page = con.get();
			Elements links = page.select("a[href*=alpha]");
			for (Element link : links) {
				if (letter.equalsIgnoreCase(link.text())) {
					return link.attr("href");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, String> getAuthorsByName(String name) {
		String firstLetterLink = getLinkByFirstLetter(String.valueOf(name.charAt(0)));
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(name.substring(0, 1).toUpperCase()).append(name.substring(1).toLowerCase());
		name = nameBuilder.toString().trim();
		Map<String, String> authors = new HashMap<>();
		Document page;
		Connection con = Jsoup.connect(PATH + firstLetterLink);
		con.timeout(6000);
		try {
			page = con.get();
			Elements links = page.select("a[href*=auth]");
			for (Element link : links) {
				String linkText = link.text();
				if (linkText.indexOf(name) != -1) {
					authors.put(linkText, link.attr("href"));
				}
			}
			return authors;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
	
	public Map<String, String> getBookLinksByAuthor(String authorLink) {
		Map<String, String> books = new HashMap<>();
		Document page;
		Connection con = Jsoup.connect(PATH + authorLink);
		con.timeout(6000);
		try {
			System.out.println(PATH + authorLink);
			page = con.get();
			Elements links = page.select("a[href*=auth]");
			for (Element link : links) {
				System.out.println(link);
				books.put(link.text(), link.attr("href"));
			}
			return books;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}
}
