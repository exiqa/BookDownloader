package com.blogspot.magazsa.web.bkdwnldr;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
		}
	}

}
