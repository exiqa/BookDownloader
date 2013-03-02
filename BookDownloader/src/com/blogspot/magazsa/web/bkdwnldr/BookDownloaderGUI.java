package com.blogspot.magazsa.web.bkdwnldr;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class BookDownloaderGUI extends JFrame {

	private static final long serialVersionUID = -1090770460719632795L;

	private BookDownloader downloader = new BookDownloader();

	private List<Author> authors;
	private List<Book> books;

	public BookDownloaderGUI() {
		setTitle("Book Downloader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final DefaultListModel<String> authorListModel = new DefaultListModel<>();
		final DefaultListModel<String> booksListModel = new DefaultListModel<>();

		final JList<String> authorList = new JList<>(authorListModel);
		authorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		authorList.setVisibleRowCount(15);
		authorList.setFont(new Font("Serif", Font.ITALIC, 14));
		JScrollPane authorsListScrollPane = new JScrollPane(authorList);

		final JList<String> booksList = new JList<>(booksListModel);
		booksList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		booksList.setVisibleRowCount(15);
		booksList.setFont(new Font("Serif", Font.ITALIC, 14));
		JScrollPane booksListScrollPane = new JScrollPane(booksList);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		final JTextField searchField = new JTextField("Введите имя автора", 20);

		JButton findButton = new JButton("Найти");
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				authorListModel.removeAllElements();
				booksListModel.removeAllElements();
				String name = searchField.getText();
				authorListModel.removeAllElements();
				if (!"".equals(name)) {
					authors = downloader.getAuthorsByName(name);
					for (Author author : authors) {
						authorListModel.addElement(author.getName());
					}
				}
			}
		});

		JButton showButton = new JButton("Показать");
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

		JButton downloadButton = new JButton("Скачать");
		downloadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (books == null) {
					JOptionPane.showMessageDialog(BookDownloaderGUI.this,
							"Список книг пуст!");
					return;
				}
				int[] booksIndices = booksList.getSelectedIndices();
				if (booksIndices.length== 0) {
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

		JButton downloadAllButton = new JButton("Скачать все");
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
		panel.add(new JLabel("Поиск по автору:"), new GridBagConstraints(0, 0,
				2, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(searchField, new GridBagConstraints(2, 0, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		panel.add(findButton, new GridBagConstraints(4, 0, 1, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));
		panel.add(new JLabel("Автор:"), new GridBagConstraints(0, 1, 3, 1, 0,
				0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		panel.add(new JLabel("Книги:"), new GridBagConstraints(3, 1, 2, 1, 0,
				0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		panel.add(authorsListScrollPane, new GridBagConstraints(0, 2, 3, 1, 0,
				0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(booksListScrollPane, new GridBagConstraints(3, 2, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		panel.add(showButton, new GridBagConstraints(0, 3, 2, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		panel.add(downloadButton, new GridBagConstraints(2, 3, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		panel.add(downloadAllButton, new GridBagConstraints(3, 3, 2, 1, 0, 0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 5, 5), 0, 0));

		add(panel);
		// setLocationRelativeTo(null);
		// setSize(new Dimension(700, 500));
		pack();
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
