package uni.prakinf.m2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ServerCommWindow extends CommWindow {

	private static final long serialVersionUID = 8436737690033348740L;
	
	private JMenu serverMenu;
	
	private JMenuItem startMenuItem;
	private JMenuItem stopMenuItem;
	private JMenuItem resetMenuItem;

	public ServerCommWindow(ICommWindowCallback callback) {
		super(callback);
		initServerButtons();
	}
	
	private void initServerButtons() {
		startMenuItem = new JMenuItem("Starten");
		stopMenuItem = new JMenuItem("Anhalten");
		resetMenuItem = new JMenuItem("Logins leeren");
		
		serverMenu = new JMenu("Server");
		
		serverMenu.add(startMenuItem);
		serverMenu.add(stopMenuItem);
		serverMenu.add(resetMenuItem);
		menu.add(serverMenu);
		
		setTitle("Messaging Server");
		
		setupEventListeners();
	}

	private void setupEventListeners() {
		startMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						callback.processInput("\\start");
					}
				}.start();
			}
		});
		
		stopMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						callback.processInput("\\stop");
					}
				}.start();
			}
		});
		
		resetMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						callback.processInput("\\reset");
					}
				}.start();
			}
		});
	}

}
