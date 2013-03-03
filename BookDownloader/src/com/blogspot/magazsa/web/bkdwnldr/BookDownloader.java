package com.blogspot.magazsa.web.bkdwnldr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	private static final String DOWNLOAD_PATH = PATH + "/download/";

	private String downloadsDirPath = "Downloads/";

	private static final int TIMEOUT = 5000; // 5 seconds

	public BookDownloader() throws IOException {
		File downloadsDir = new File("./" + downloadsDirPath);
		if (!downloadsDir.exists()) {
			downloadsDir.mkdir();
		}
	}

	/**
	 * Downloads and saves book from remote url
	 * 
	 * @param book
	 *            - book to be downloaded
	 */
	public void download(Book book) {
		BufferedInputStream in = null;
		FileOutputStream out = null;
		URL url;
		try {
			url = new URL(DOWNLOAD_PATH + book.getId());
			in = new BufferedInputStream(url.openStream());
			out = new FileOutputStream(new File(downloadsDirPath
					+ book.getAsciiTitle() + ".zip"));
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

	/**
	 * Sets the directory to store downloaded books
	 * 
	 * @param downloadsDirPath
	 *            - directory name
	 */
	public void setDownloadsDirPath(String downloadsDirPath) {
		this.downloadsDirPath = downloadsDirPath;
	}

	/**
	 * Returns the link to page with authors, whose names starts with given
	 * letter
	 * 
	 * @param letter
	 *            - first letter of the author's name
	 * @return - link to page with authors
	 */
	public String getLinkByFirstLetter(String letter) {
		Document page;
		Connection connection = Jsoup.connect(PATH);
		connection.timeout(TIMEOUT);
		try {
			page = connection.get();
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
	
	/**
	 * Returns a list of authors whose names match the given name
	 * 
	 * @param name - given name
	 * @return - list of found authors  
	 */
	public List<Author> getAuthorsByName(String name) {
		String firstLetterLink = getLinkByFirstLetter(String.valueOf(name
				.charAt(0)));
		StringBuilder nameBuilder = new StringBuilder();
		nameBuilder.append(name.substring(0, 1).toUpperCase()).append(
				name.substring(1).toLowerCase());
		name = nameBuilder.toString().trim();
		List<Author> authors = new ArrayList<>();
		Document page;
		Connection connection = Jsoup.connect(PATH + firstLetterLink);
		connection.timeout(TIMEOUT);
		try {
			page = connection.get();
			Elements links = page.select("a[href*=auth]");
			for (Element link : links) {
				String linkText = link.text();
				if (linkText.startsWith(name)) {
					String href = link.attr("href");
					String[] tokens = href.split("/");
					String id = tokens[2];
					String asciiName = tokens[3];
					Author author = new Author(id, linkText, asciiName, href);
					authors.add(author);
				}
			}
			return authors;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
	/**
	 * Returns a list of all the books by given author
	 * 
	 * @param author - author, whose books should be found
	 * @return - list of all found books 
	 */
	public List<Book> getBooksByAuthor(Author author) {
		List<Book> books = new ArrayList<>();
		Document page;
		Connection connection = Jsoup.connect(PATH + author.getLink());
		connection.timeout(TIMEOUT);
		try {
			page = connection.get();
			Elements links = page
					.select("a[href*=auth/" + author.getId() + "]");
			for (Element link : links) {
				String title = link.text();
				String[] tokens = link.attr("href").split("/");
				String id = tokens[4];
				String asciiTitle = tokens[6];
				Book book = new Book(id, title, asciiTitle);
				books.add(book);
			}
			return books;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
	
}
