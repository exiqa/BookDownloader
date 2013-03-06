package com.blogspot.magazsa.web.bkdwnldr;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultCaret;

public class BookDownloaderGUI extends JFrame {

	private static final long serialVersionUID = -1090770460719632795L;

	private BookDownloader downloader;
	private File downloadDir;
	
	private List<Author> authors;
	private List<Book> books;

	private DefaultListModel<String> authorListModel;
	private DefaultListModel<String> booksListModel;
	
	private JList<String> authorList;
	private JList<String> booksList;

	private JTextArea logger;

	private JButton findButton;
	private JButton showButton;
	private JButton downloadButton;
	private JButton downloadAllButton;

	public BookDownloaderGUI() {
		setTitle("Book Downloader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		downloader = new BookDownloader();
		downloadDir = new File(downloader.getDownloadDirPath());
		if(!downloadDir.exists()) {
			downloadDir.mkdir();
		}
		
		logger = new JTextArea(3, 35);
		logger.setFont(new Font("Monospaced", Font.PLAIN, 11));
		logger.setEditable(false);
		logger.setMargin(new Insets(2, 5, 2, 5));
		DefaultCaret caret = (DefaultCaret) logger.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		authorListModel = new DefaultListModel<>();
		booksListModel = new DefaultListModel<>();

		authorList = new JList<>(authorListModel);
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		authorList.setVisibleRowCount(15);
		authorList.setFont(new Font("Serif", Font.ITALIC, 14));
		JScrollPane authorsListScrollPane = new JScrollPane(authorList);
		authorsListScrollPane.setPreferredSize(new Dimension(250, 250));
		authorsListScrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Автор"));

		booksList = new JList<>(booksListModel);
		booksList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		booksList.setVisibleRowCount(15);
		booksList.setFont(new Font("Serif", Font.ITALIC, 14));
		JScrollPane booksListScrollPane = new JScrollPane(booksList);
		booksListScrollPane.setPreferredSize(new Dimension(400, 250));
		booksListScrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Книги"));

		// center panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(authorsListScrollPane, BorderLayout.WEST);
		centerPanel.add(booksListScrollPane, BorderLayout.CENTER);

		// top panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 4, 0, 4));
		topPanel.add(new JLabel("Поиск по автору: "), BorderLayout.WEST);

		// down panel
		JPanel downPanel = new JPanel();
		downPanel.setLayout(new BorderLayout());

		// buttons panel
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		final JTextField searchField = new JTextField(25);
		searchField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				searchField.setText("");
			}
		});
		topPanel.add(searchField, BorderLayout.CENTER);

		findButton = new JButton("Найти");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				authorListModel.removeAllElements();
				booksListModel.removeAllElements();
				String name = searchField.getText();
				if (!"".equals(name)) {
					findAuthors(name);
				}
			}
		});
		topPanel.add(findButton, BorderLayout.EAST);

		showButton = new JButton("Показать");
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				booksListModel.removeAllElements();
				Author author = authors.get(authorList.getSelectedIndex());
				showBooks(author);
			}
		});
		buttonsPanel.add(showButton);

		downloadButton = new JButton("Скачать");
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (books == null) {
					JOptionPane.showMessageDialog(BookDownloaderGUI.this,
							"Список книг пуст!");
					return;
				}
				int[] booksIndices = booksList.getSelectedIndices();
				if (booksIndices.length == 0) {
					JOptionPane.showMessageDialog(BookDownloaderGUI.this,
							"Не выбрано ни одной книги!");
					return;
				}
				List<Book> booksToDownload = new ArrayList<>();
				for (int i = 0; i < booksIndices.length; i++) {
					booksToDownload.add(books.get(i));
				}
				downloadFiles(booksToDownload);
				booksList.clearSelection();
			}
		});
		buttonsPanel.add(downloadButton);

		downloadAllButton = new JButton("Скачать все");
		downloadAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (books != null) {
					downloadAllFiles(books);
				} else {
					JOptionPane.showMessageDialog(BookDownloaderGUI.this,
							"Список книг пуст!");
				}
			}
		});
		buttonsPanel.add(downloadAllButton);

		downPanel.add(buttonsPanel, BorderLayout.WEST);
		downPanel.add(new JScrollPane(logger,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		// adding components to panel
		// content panel
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setSize(new Dimension(700, 500));
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.add(topPanel, BorderLayout.NORTH);
		content.add(centerPanel, BorderLayout.CENTER);
		content.add(downPanel, BorderLayout.SOUTH);

		add(content, BorderLayout.CENTER);
		// setLocationRelativeTo(null);
		pack();
	}

	private void findAuthors(final String authorName) {
		new SwingWorker<List<Author>, Void>() {
			protected List<Author> doInBackground() throws Exception {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				findButton.setEnabled(false);
				logger.append("Подождите.....идет поиск автора\n");
				return downloader.getAuthorsByName(authorName);
			};

			protected void done() {
				try {
					authors = get();
					if (authors.size() > 0) {
						logger.append("Поиск завершен.\n");
					} else {
						logger.append("Ничего не найдено.\n");
					}
					for (Author author : authors) {
						authorListModel.addElement(author.getName());
					}
					setCursor(null);
					findButton.setEnabled(true);
				} catch (Exception e) {
					logger.append("*error: " + e.getMessage());
				}
			};

		}.execute();
	}

	private void showBooks(final Author author) {
		new SwingWorker<List<Book>, Void>() {
			protected List<Book> doInBackground() throws Exception {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				showButton.setEnabled(false);
				logger.append("Подождите.....идет поиск книг\n");
				return downloader.getBooksByAuthor(author);
			}

			protected void done() {
				try {
					books = get();
					for (Book book : books) {
						booksListModel.addElement(book.getTitle());
					}
				} catch (Exception e) {
					logger.append("*error: " + e.getMessage());
				}
				setCursor(null);
				showButton.setEnabled(true);
			};

		}.execute();
	}

	private void downloadFiles(final List<Book> books) {
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				downloadButton.setEnabled(false);
				downloadAllButton.setEnabled(false);
				File authorDir = createAuthorDir(authorList.getSelectedValue());
				for (Book book : books) {
					logger.append("Загрузка книги..." + book.getTitle() + "...");
					downloader.download(authorDir, book);
					logger.append("done!\n");
				}
				return null;
			};

			protected void done() {
				downloadButton.setEnabled(true);
				downloadAllButton.setEnabled(true);
			};

		}.execute();
	}

	private void downloadAllFiles(final List<Book> books) {
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				downloadButton.setEnabled(false);
				downloadAllButton.setEnabled(false);
				File authorDir = createAuthorDir(authorList.getSelectedValue());
				for (Book book : books) {
					logger.append("Загрузка книги..." + book.getTitle() + "...");
					downloader.download(authorDir, book);
					logger.append("done!\n");
				}
				return null;
			};

			protected void done() {
				downloadButton.setEnabled(true);
				downloadAllButton.setEnabled(true);
				logger.append("Загрузка завершена!\n");
			};

		}.execute();
	}
	
	private File createAuthorDir(String authorName) {
		File authorDir = new File(downloader.getDownloadDirPath(), authorName);
		if (!authorDir.exists()) {
			authorDir.mkdir();
		}
		return authorDir;
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new BookDownloaderGUI().setVisible(true);
			}
		});

	}

}
