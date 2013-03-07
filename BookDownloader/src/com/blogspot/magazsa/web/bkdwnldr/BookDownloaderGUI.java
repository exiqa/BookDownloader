package com.blogspot.magazsa.web.bkdwnldr;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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

	private JMenuBar menuBar;

	private JProgressBar progressBar;

	public BookDownloaderGUI() {
		setTitle("Book Downloader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		downloader = new BookDownloader();
		downloadDir = new File(downloader.getDownloadDirPath());
		if (!downloadDir.exists()) {
			downloadDir.mkdir();
		}

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		JMenu programMenu = new JMenu("Опции");
		JMenuItem preferencesMenuItem = new JMenuItem("Настройки");
		preferencesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame preferences = new JFrame("Настройки");
				preferences.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				preferences.setLayout(new FlowLayout(FlowLayout.LEFT));
				preferences.add(new JLabel("Папка для загрузок: "));
				final JTextField path = new JTextField(downloadDir.getPath(),
						20);
				JButton browseButton = new JButton("Изменить");
				browseButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JFileChooser fc = new JFileChooser(downloadDir);
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fc.setApproveButtonText("OK");
						if (fc.showSaveDialog(BookDownloaderGUI.this) == JFileChooser.APPROVE_OPTION) {
							downloadDir = fc.getSelectedFile();
							path.setText(downloadDir.getAbsolutePath());
						}
					}
				});
				preferences.add(path);
				preferences.add(browseButton);
				preferences.pack();
				preferences.setLocationRelativeTo(BookDownloaderGUI.this);
				preferences.setVisible(true);
			}
		});
		programMenu.add(preferencesMenuItem);
		menuBar.add(programMenu);

		logger = new JTextArea(3, 35);
		logger.setFont(new Font("Monospaced", Font.PLAIN, 11));
		logger.setEditable(false);
		logger.setMargin(new Insets(2, 5, 2, 5));
		logger.setBackground(this.getBackground());
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
		topPanel.setBorder(BorderFactory.createEmptyBorder(5, 4, 5, 4));
		topPanel.add(new JLabel("Поиск по автору:  "), BorderLayout.WEST);

		// down panel
		JPanel downPanel = new JPanel();
		downPanel.setLayout(new BorderLayout());

		// buttons panel
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridBagLayout());

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
		buttonsPanel.add(showButton, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 0, 5, 5), 0, 0));

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
				for (int i : booksIndices) {
					booksToDownload.add(books.get(i));
				}
				new DownloadTask(booksToDownload).execute();
				booksList.clearSelection();
			}
		});
		buttonsPanel.add(downloadButton, new GridBagConstraints(1, 0, 1, 1, 0,
				0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));

		downloadAllButton = new JButton("Скачать все");
		downloadAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (books != null) {
					new DownloadTask(books).execute();
				} else {
					JOptionPane.showMessageDialog(BookDownloaderGUI.this,
							"Список книг пуст!");
				}
			}
		});
		buttonsPanel.add(downloadAllButton, new GridBagConstraints(2, 0, 1, 1,
				0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(5, 0, 5, 5), 0, 0));

		buttonsPanel.add(progressBar, new GridBagConstraints(0, 1, 3, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 5), 0, 0));

		downPanel.add(buttonsPanel, BorderLayout.WEST);
		downPanel.add(new JScrollPane(logger,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		// adding components to panel
		// content panel
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.add(topPanel, BorderLayout.NORTH);
		content.add(centerPanel, BorderLayout.CENTER);
		content.add(downPanel, BorderLayout.SOUTH);

		add(content, BorderLayout.CENTER);
		pack();
	}

	private void findAuthors(final String authorName) {
		new SwingWorker<List<Author>, Void>() {
			protected List<Author> doInBackground() throws Exception {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				findButton.setEnabled(false);
				logger.append("Подождите.....идет поиск автора\n");
				return downloader.getAuthorsByName(authorName);
			}

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
				} catch (Exception e) {
					logger.append("Проблемы с подключением к серверу. Попробуйте еще раз");
				} finally {
					setCursor(null);
					findButton.setEnabled(true);
				}
			}

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
					logger.append("Проблемы с подключением к серверу. Попробуйте еще раз\n");
				} finally {
					setCursor(null);
					showButton.setEnabled(true);
				}
			}

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

	private class DownloadTask extends SwingWorker<Void, Void> implements
			PropertyChangeListener {

		private List<Book> books;

		public DownloadTask(List<Book> books) {
			this.books = books;
			addPropertyChangeListener(this);
		}

		protected Void doInBackground() throws Exception {
			downloadButton.setEnabled(false);
			downloadAllButton.setEnabled(false);
			progressBar.setValue(0);
			File authorDir = createAuthorDir(authorList.getSelectedValue());
			int progressTick = 100 / books.size();
			int progress = 0;
			setProgress(progress);
			for (Book book : books) {
				logger.append("Загрузка книги..." + book.getTitle() + "...");
				downloader.download(authorDir, book);
				logger.append("done!\n");
				progress += progressTick;
				setProgress(progress);
			}
			return null;
		}

		protected void done() {
			downloadButton.setEnabled(true);
			downloadAllButton.setEnabled(true);
			logger.append("Загрузка завершена!\n");
			progressBar.setValue(100);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("progress" == evt.getPropertyName()) {
				Integer progress = (Integer) evt.getNewValue();
				progressBar.setValue(progress);
			}
		}

	}

}
