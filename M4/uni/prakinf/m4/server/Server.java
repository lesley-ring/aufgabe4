package uni.prakinf.m4.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uni.prakinf.m4.server.protokoll.M4NachrichtEinfach;

public class Server { /* implements ICommWindowCallback {
    // TODO Doppelte Anmeldungen ----- OKAY
	// TODO HashMap speichern    ----- OKAY
	// TODO Listenfeld als Liste ----- OKAY
	// TODO Private Chats
	public static final int DEFAULT_PORT = 4444;
	public static final String DEFAULT_CHANNEL_LIST_DELIMITER = "%";

	private ServerListenerThread listener;
	private List<MessageListenerThread> clients;
	private Map<String, String> logins;
	private ServerCommWindow cw;
	private boolean running;

	public Server() {
		clients = new LinkedList<MessageListenerThread>();
		logins = new HashMap<String, String>();
		running = false;
	}

	public void start() {
		cw = new ServerCommWindow(this);
		cw.setVisible(true);

		cw.displayLine("Messaging Server");

		cw.setInputState("An alle:", "Senden", true);

		processInput("\\start");
	}

	private void reset() {
		logins.clear();
	}

	public void startServer() throws IOException {
		cw.displayLine("Starte Server...");
		listener = new ServerListenerThread(this);
		running = true;
		listener.startListening();

		cw.displayLine("Okay.");

		String sIPAddress = InetAddress.getLocalHost().getHostAddress()
				.toString();
		cw.displayLine(String.format("IP-Adresse: %s", sIPAddress));
	}

	public void stopServer() throws InterruptedException, IOException {
		cw.displayLine("Halte Server an...");
		listener.stopListening();
		for (MessageListenerThread client : clients) {
			client.stopCommunication();
		}
		clients.clear();
		running = false;
	}

	public void clientConnected(Socket socket) {
		try {
			MessageListenerThread client = new MessageListenerThread(this,
					socket, cw);
			client.startCommunication();
			synchronized (clients) {
				clients.add(client);
			}
		} catch (Exception ex) {
			cw.displayLine(String.format(
					"Fehler bei Verbindung mit Client: %s\n", ex.toString()));
		}
	}

	public void broadcastMessage(M4NachrichtEinfach message) {
		if (message.getMessageType() == MessageType.MESSAGE_BROADCAST)
			cw.displayLine(String.format("[%-12s] %s", message.getParamA(),
					message.getParamB()));
		synchronized (clients) {
			for (MessageListenerThread client : clients)
				client.sendMessage(message);
		}
	}
	
	public void relayMessage(M4NachrichtEinfach message, MessageListenerThread source) {
		synchronized (clients) {
			for (MessageListenerThread client : clients) {
				if(client.getClientName().equals(message.getParamB()) || client.getClientName().equals(source.getClientName()))
					client.sendMessage(new M4NachrichtEinfach(MessageType.MESSAGE_BROADCAST, source.getClientName(), message.getParamA()));
			}
		}
	}

	public void clientDisconnected(MessageListenerThread client) {
		if (client.isLoggedIn())
			cw.displayLine(String.format("%s hat sich abgemeldet",
					client.getClientName()));

		synchronized (clients) {
			clients.remove(client);
		}

		new ChannelListThread().start();
	}

	public boolean checkLogin(String name, String password) {
		boolean login_valid = false;
		login_valid = PasswordManager.validateLogin(name, password);

		// Ist der Client bereits eingeloggt?
		for (MessageListenerThread client : clients) {
			if (client.isLoggedIn() && client.getClientName().equals(name))
				login_valid = false;
		}

		if (login_valid) {
			cw.displayLine(String.format("%s hat sich angemeldet", name));
			new ChannelListThread().start();
		}

		return login_valid;
	}

	private class ChannelListThread extends Thread {
		public void run() {
			String channel_list = "";
			synchronized (clients) {
				for (MessageListenerThread client : clients) {
					if (client.isLoggedIn()) {
						if (!channel_list.isEmpty())
							channel_list += DEFAULT_CHANNEL_LIST_DELIMITER;
						channel_list += client.getClientName();

					}
				}
				cw.updateChannelList(channel_list
						.split(DEFAULT_CHANNEL_LIST_DELIMITER));
			}
			M4NachrichtEinfach message = new M4NachrichtEinfach(
					MessageType.CHANNEL_LIST, channel_list,
					DEFAULT_CHANNEL_LIST_DELIMITER);
			synchronized (clients) {
				for (MessageListenerThread client : clients) {
					client.sendMessage(message);
				}
			}
		}
	}

	@Override
	public void processInput(String s) {
		if (s.equals("\\start")) {
			try {
				if (!running)
					startServer();
				else
					cw.displayLine("Server läuft bereits!");
			} catch (IOException e) {
				cw.displayLine(String.format("Fehler beim Starten: %s",
						e.toString()));
			}
		} else if (s.equals("\\stop")) {
			try {
				if (running)
					stopServer();
				else
					cw.displayLine("Server läuft nicht!");
			} catch (Exception e) {
				cw.displayLine(String.format("Fehler beim Anhalten: %s",
						e.toString()));
			}
		} else if (s.equals("\\reset")) {
			if (clients.isEmpty()) {
				reset();
				cw.displayLine("Passwörter zurückgesetzt");
			} else {
				cw.displayLine("Es sind noch Clients angemeldet!");
			}
		} else {
			if (running) {
				M4NachrichtEinfach message = new M4NachrichtEinfach(
						MessageType.MESSAGE_BROADCAST, "SERVER", s);
				broadcastMessage(message);
			} else
				cw.displayLine("Server läuft nicht!");
		}
	}
	
	public void userSelected(String name) {
		// Von Serverseite nicht wichtig, also nicht implementieren!!!
	}*/
}
