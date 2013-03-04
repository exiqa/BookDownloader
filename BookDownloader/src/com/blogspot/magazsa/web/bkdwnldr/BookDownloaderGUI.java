package com.blogspot.magazsa.web.bkdwnldr;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.List;

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

public class BookDownloaderGUI extends JFrame {

	private static final long serialVersionUID = -1090770460719632795L;

	private BookDownloader downloader;
	
	private List<Author> authors;
	private List<Book> books;
	
	private DefaultListModel<String> authorListModel;
	private DefaultListModel<String> booksListModel;
	
	private JTextArea logger;
	
	private JButton findButton;
	private JButton showButton;
	private JButton downloadButton;
	private JButton downloadAllButton;

	public BookDownloaderGUI() {
		setTitle("Book Downloader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		logger = new JTextArea(3, 25);
		
		try {
			downloader = new BookDownloader();
		} catch (IOException e) {
			logger.append("Невозможно создать папку для хранения файлов\n");
		}
		
		authorListModel = new DefaultListModel<>();
		booksListModel = new DefaultListModel<>();

		final JList<String> authorList = new JList<>(authorListModel);
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		authorList.setVisibleRowCount(15);
		authorList.setFont(new Font("Serif", Font.ITALIC, 14));
		JScrollPane authorsListScrollPane = new JScrollPane(authorList);
		authorsListScrollPane.setPreferredSize(new Dimension(250, 250));

		final JList<String> booksList = new JList<>(booksListModel);
		booksList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		booksList.setVisibleRowCount(15);
		booksList.setFont(new Font("Serif", Font.ITALIC, 14));
		JScrollPane booksListScrollPane = new JScrollPane(booksList);
		booksListScrollPane.setPreferredSize(new Dimension(450, 250));

		JPanel content = new JPanel();
		content.setLayout(new GridBagLayout());
		content.setSize(new Dimension(700, 500));

		final JTextField searchField = new JTextField(25);
		searchField.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				searchField.setText("");
			}
		});
		

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

		showButton = new JButton("Показать");
		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				booksListModel.removeAllElements();
				Author author = authors.get(authorList.getSelectedIndex());
				books = downloader.getBooksByAuthor(author);
				for (Book book : books) {
					booksListModel.addElement(book.getTitle());
				}
			}
		});

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
				for (int i = 0; i < booksIndices.length; i++) {
					downloader.download(books.get(i));
				}
				booksList.clearSelection();
			}
		});

		downloadAllButton = new JButton("Скачать все");
		downloadAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (books != null) {
					for (int i = 0; i < books.size(); i++) {
						downloader.download(books.get(i));
					}
				} else {
					JOptionPane.showMessageDialog(BookDownloaderGUI.this,
							"Список книг пуст!");
				}
			}
		});

		// adding components to panel
		content.add(new JLabel("Поиск по автору:"), new GridBagConstraints(0, 0,
				2, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		content.add(searchField, new GridBagConstraints(2, 0, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		content.add(findButton, new GridBagConstraints(4, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));
		content.add(new JLabel("Автор:"), new GridBagConstraints(0, 1, 3, 1, 0,
				0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		content.add(new JLabel("Книги:"), new GridBagConstraints(3, 1, 2, 1, 0,
				0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		content.add(authorsListScrollPane, new GridBagConstraints(0, 2, 3, 1, 0,
				0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		content.add(booksListScrollPane, new GridBagConstraints(3, 2, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		content.add(showButton, new GridBagConstraints(0, 3, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		content.add(downloadButton, new GridBagConstraints(2, 3, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		content.add(downloadAllButton, new GridBagConstraints(3, 3, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));
		content.add(new JScrollPane(logger), new GridBagConstraints(4, 3, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

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
						logger.append("ничего не найдено.\n");
					}
					for (Author author : authors) {
						authorListModel.addElement(author.getName());
					}
					setCursor(null);
					findButton.setEnabled(true);
				} catch (Exception e) {
					// TODO: handle exception
				}
			};
			
				
		}.execute();
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
