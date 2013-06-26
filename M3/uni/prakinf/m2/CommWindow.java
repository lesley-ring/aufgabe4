package uni.prakinf.m2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CommWindow extends JFrame {
	private static final long serialVersionUID = 5356123325753880954L;

	protected ICommWindowCallback callback;
	protected JMenuBar menu;

	private JTextField messagePrompt;
	private JTextField messageInput;
	private JTextPane displayPane;
	private JList channelList_;
	private JButton sendButton;

	private DefaultListModel channelListModel;

	public CommWindow(ICommWindowCallback callback) {
		this.callback = callback;
		this.getContentPane().setLayout(new BorderLayout());
		this.initCommWindow();
	}

	private void initCommWindow() {
		Font ms = new Font("Monospaced", Font.PLAIN, 14);

		messageInput = new JTextField();
		messagePrompt = new JTextField();
		messagePrompt.setEditable(false);
		sendButton = new JButton();
		displayPane = new JTextPane();
		displayPane.setEditable(false);
		displayPane.setFont(ms);

		channelListModel = new DefaultListModel();
		channelList_ = new JList(channelListModel);
		channelList_.setBackground(Color.LIGHT_GRAY);
		channelList_.setMinimumSize(new Dimension(250, 0));

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout(0, 0));
		bottomPanel.add(messagePrompt, BorderLayout.WEST);
		bottomPanel.add(messageInput, BorderLayout.CENTER);
		bottomPanel.add(sendButton, BorderLayout.EAST);

		add(bottomPanel, BorderLayout.SOUTH);
		add(displayPane, BorderLayout.CENTER);
		add(channelList_, BorderLayout.EAST);

		menu = new JMenuBar();
		add(menu, BorderLayout.NORTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 480);

		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String s = messageInput.getText();
				new Thread() {
					public void run() {
						callback.processInput(s);
					}
				}.start();

				messageInput.setText("");
			}
		});

		messageInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!sendButton.isEnabled())
					return;
				final String s = messageInput.getText();
				new Thread() {
					public void run() {
						callback.processInput(s);
					}
				}.start();

				messageInput.setText("");
			}
		});
		channelList_.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				final String name = (String) channelListModel.get(channelList_
						.getSelectedIndex());
				new Thread() {
					public void run() {
						callback.userSelected(name);
					}
				}.start();
			}
		});

		updateChannelList(new String[0]);
	}

	public void displayLine(String s) {
		if (displayPane.getText().isEmpty())
			displayPane.setText(s);
		else
			displayPane.setText(displayPane.getText() + "\n" + s);
		displayPane.setCaretPosition(displayPane.getText().length());
	}

	public void setInputState(String prompt, String button_text, boolean enabled) {
		messagePrompt.setText(prompt);
		sendButton.setText(button_text);
		sendButton.setEnabled(enabled);
		
		if (enabled)
			messageInput.requestFocus();
	}

	@SuppressWarnings("unchecked")
	public void updateChannelList(String[] names) {
		if (names.length == 0) {
			channelListModel.removeAllElements();
			channelListModel.addElement("       ");
		}

		List<String> nameList = new ArrayList<String>();

		for (String name : names) {
			nameList.add(name);

			Enumeration<String> elements = (Enumeration<String>) channelListModel
					.elements();
			boolean contains = false;
			while (elements.hasMoreElements()) {
				if (elements.nextElement().equals(name)) {
					contains = true;
					break;
				}
			}
			if (!contains)
				channelListModel.addElement(name);
		}

		Enumeration<String> elements = (Enumeration<String>) channelListModel
				.elements();
		while (elements.hasMoreElements()) {
			String name = elements.nextElement();
			if (!nameList.contains(name))
				channelListModel.removeElement(name);
		}
	}
}