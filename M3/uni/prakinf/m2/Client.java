package uni.prakinf.m2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import uni.prakinf.m2.M2ProtocolMessage.MessageType;

public class Client implements ICommWindowCallback {
	private Socket socket;
	private boolean connected;
	private boolean logged_in;
	private String name;
	private String password;
	private CommWindow cw;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String privateName;

	public Client() {
		privateName = "";
		socket = null;
		connected = false;
		logged_in = false;
		name = "";
		password = "";
	}

	public void start() {
		cw = new ClientCommWindow(this);
		cw.setVisible(true);

		reset();
	}

	private void askForServerAddress() {
		cw.setInputState("Serveradresse:", "OK", true);
	}

	private void askForName() {
		name = "";
		cw.setInputState("Name:", "OK", true);
	}

	private void askForPassword() {
		password = "";
		cw.setInputState("Passwort:", "OK", true);
	}

	private void askForMessage() {
		cw.setInputState(String.format("%s: ", name), "Senden", true);
	}

	private void reset() {
		if (socket != null) {
			try {
				socket.close();
				cw.displayLine("Verbindung getrennt.");
			} catch (Exception ex) {

			}
		}
		socket = null;
		connected = false;
		logged_in = false;
		name = "";
		password = "";
		cw.displayLine("Messaging Client");
		cw.updateChannelList(new String[0]);
		askForServerAddress();
	}

	public void processInput(String s) {
		if (!connected) {
			if (s.isEmpty() || s.startsWith("\\")) {
				askForServerAddress();
				return;
			}
			// Serveradresse verarbeiten
			cw.setInputState("", "...", false);
			connected = connect(s);
			if (connected) {
				askForName();
			} else {
				cw.displayLine("Keine Verbindung.");
				askForServerAddress();
			}
		} else {
			if (!logged_in) {
				if (name.isEmpty() || name.startsWith("\\")) {
					// Namen verarbeiten
					if (s.isEmpty()) {
						askForName();
					} else {
						name = s;
						askForPassword();
					}
				} else {
					// Passwort verarbeiten, anmelden
					if (s.isEmpty() || s.startsWith("\\")) {
						askForPassword();
					} else {
						password = s;
						if (login()) {
							new BroadcastThread().start();
							askForMessage();
						} else {
							name = "";
							password = "";
							askForName();
						}
					}
				}
			} else {
				// Angemeldet
				try {
					if (s.equals("\\ende")) {
						reset();
						return;
					} else if (!privateName.isEmpty()) {
						M2ProtocolMessage message = new M2ProtocolMessage(
								MessageType.MESSAGE_REQUEST, s, privateName);
						output.writeObject(message);
					} else {
						M2ProtocolMessage message = new M2ProtocolMessage(
								MessageType.MESSAGE_REQUEST, s, "");
						output.writeObject(message);
					}
				} catch (Exception ex) {
					cw.displayLine(String.format(
							"Fehler beim Senden der Nachricht: %s",
							ex.toString()));
				}
			}
		}
	}

	private boolean login() {
		try {
			output.writeObject(new M2ProtocolMessage(MessageType.LOGIN_REQUEST,
					name, password));
			M2ProtocolMessage message = (M2ProtocolMessage) input.readObject();
			if (message.getMessageType() == MessageType.LOGIN_RESPONSE_OK) {
				cw.displayLine(String.format("Angemeldet als %s", name));
				logged_in = true;
				return true;
			} else {
				cw.displayLine("Falsches Passwort!");
				logged_in = false;
				return false;
			}
		} catch (Exception ex) {
			cw.displayLine(String.format("Fehler beim Anmeldevorgang: ",
					ex.toString()));
			return false;
		}
	}

	private boolean connect(String remote) {
		try {
			socket = new Socket();
			cw.displayLine(String.format("Name %s wird aufgeloest...", remote));
			InetSocketAddress addr = new InetSocketAddress(remote,
					Server.DEFAULT_PORT);
			cw.displayLine(String.format("IP-Adresse: %s", addr.getAddress()
					.getHostAddress()));
			socket.connect(addr, 2000);
			cw.displayLine(String.format("Verbunden mit %s", remote));

			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());

			return true;
		} catch (Exception ex) {
			cw.displayLine(String.format("Fehler beim Verbinden: %s",
					ex.toString()));
			return false;
		}
	}

	private class BroadcastThread extends Thread {
		public void run() {
			while (connected) {
				try {
					M2ProtocolMessage message = (M2ProtocolMessage) input
							.readObject();
					if (message.getMessageType() == MessageType.MESSAGE_BROADCAST) {
						// Nachricht anzeigen und Prompt erneuern
						cw.displayLine(String.format("[%-12s] %s",
								message.getParamA(), message.getParamB()));
					} else if (message.getMessageType() == MessageType.CHANNEL_LIST) {
						String[] clist = message.getParamA().split(
								message.getParamB());
						String[] clist_b = new String[clist.length + 1];
						System.arraycopy(clist, 0, clist_b, 0, clist.length);
						clist_b[clist.length] = "An alle";

						cw.updateChannelList(clist_b);
					}
				} catch (Exception ex) {

				}
			}
		}
	}

	public void userSelected(String name) {
		if (connected && logged_in) {
			if (name.equals("An alle")) {
				privateName = "";
				askForMessage();
			} else {
				privateName = name;
				cw.setInputState(String.format("%s an %s (Privat): ", this.name, privateName), "Senden", true);
			}
		}
	}
}
