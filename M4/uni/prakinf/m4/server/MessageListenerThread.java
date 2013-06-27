package uni.prakinf.m4.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import uni.prakinf.m4.server.protokoll.M4NachrichtEinfach;

public class MessageListenerThread extends Thread { /*
	private Socket socket;
	private Server server;
	private CommWindow cw;
	private boolean running;
	private boolean notify_on_disconnect;
	private CommStatus status;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String name;

	public MessageListenerThread(Server server, Socket socket, CommWindow cw)
			throws SocketException {
		this.server = server;
		this.socket = socket;
		this.cw = cw;
		status = CommStatus.NOT_LOGGED_IN;
		input = null;
		output = null;
		notify_on_disconnect = true;
		socket.setSoTimeout(0);
	}

	public void startCommunication() {
		running = true;
		start();
	}

	public void stopCommunication() throws InterruptedException, IOException {
		notify_on_disconnect = false;
		running = false;
		socket.close();
		join();
	}
	
	public boolean isLoggedIn() {
		return status == CommStatus.LOGGED_IN;
	}
	
	public String getClientName() {
		return name;
	}

	public void run() {
		try {
			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());
		} catch (IOException ioex) {
			running = false;
		}
		while (running) {
			try {
				M4NachrichtEinfach message = (M4NachrichtEinfach) input
						.readObject();
				switch (status) {
				case NOT_LOGGED_IN:
					switch (message.getMessageType()) {
					case LOGIN_REQUEST:
						if (server.checkLogin(message.getParamA(),
								message.getParamB())) {
							// Login erfolgreich
							status = CommStatus.LOGGED_IN;
							name = message.getParamA();
							sendMessageChecked(new M4NachrichtEinfach(
									MessageType.LOGIN_RESPONSE_OK, "", ""));
						} else {
							// Login fehlerhaft
							sendMessageChecked(new M4NachrichtEinfach(
									MessageType.LOGIN_RESPONSE_ERROR, "", ""));
						}
						break;
					default:
						break;
					}
					break;
				case LOGGED_IN:
					switch (message.getMessageType()) {
					case MESSAGE_REQUEST:
						if(message.getParamB().isEmpty())
							server.broadcastMessage(new M4NachrichtEinfach(MessageType.MESSAGE_BROADCAST, name, message.getParamA()));
						else
							server.relayMessage(message, this);
						break;
					default:
						break;
					}
					break;
				}
			} catch (Exception ex) {
				running = false;
			}
		}
		output = null;
		input = null;
		if(notify_on_disconnect)
			server.clientDisconnected(this);

	}

	public void sendMessage(M4NachrichtEinfach message) {
		if (status == CommStatus.LOGGED_IN) {
			sendMessageChecked(message);
		}
	}

	private void sendMessageChecked(M4NachrichtEinfach message) {
		if (output != null) {
			try {
				synchronized (output) {
					output.writeObject(message);
				}
			} catch (Exception ex) {
				cw.displayLine(String.format("%s beim Senden einer Nachricht: %s",
						ex.toString(), ex.getMessage()));
			}
		}
	}

	private enum CommStatus {
		NOT_LOGGED_IN, LOGGED_IN
	} */
}
