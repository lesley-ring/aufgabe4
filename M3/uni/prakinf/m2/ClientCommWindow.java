package uni.prakinf.m2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class ClientCommWindow extends CommWindow {

	private static final long serialVersionUID = -789710553128577716L;

	private JMenu clientMenu;
	private JMenuItem exitMenuItem;
	
	public ClientCommWindow(ICommWindowCallback callback) {
		super(callback);
		
		clientMenu = new JMenu("Client");
		exitMenuItem = new JMenuItem("Beenden");
		clientMenu.add(exitMenuItem);
		menu.add(clientMenu);
		
		setTitle("Messaging Client");
		
		setupEventListeners();
	}

	private void setupEventListeners() {
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						callback.processInput("\\ende");
					}
				}.start();
			}
		});
	}

}
