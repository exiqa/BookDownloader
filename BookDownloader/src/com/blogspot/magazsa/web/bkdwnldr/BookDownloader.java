package com.blogspot.magazsa.web.bkdwnldr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		Pattern pattern = Pattern
				.compile("(?m)(?i)(?u)(?s).*<a\\s+href\\s*=\\s*\"(.+?)\"\\s*>"
						+ letter.toUpperCase() + "</a>.*");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new URL(PATH).openStream()));
			String line;
			Matcher m;
			while ((line = reader.readLine()) != null) {
				m = pattern.matcher(line);
				if (m.matches()) {
					return PATH + m.group(1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
