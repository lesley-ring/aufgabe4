package uni.prakinf.m4.server.protokoll;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class M4TransportThread extends Thread {
    public final static int M4PORT = 39291;
    public final static int M4CAP = 25;
    public final static int M4TIMEOUT = 100;
    public final static int M4BLOCKTIME = 5000;
    // Bei Konstruktion
    private Socket socket;
    private M4Annahme annahme;
    private Object userObject;
    // Zur Laufzeit
    private BlockingQueue<M4Nachricht> nachrichtenAusgehend;
    private BlockingQueue<M4Nachricht> nachrichtenEingehend;
    private boolean threadSollLaufen;
    private boolean nachrichtenEinreihen;

    private M4TransportThread(M4Annahme annahme, Object userObject) {
        this.annahme = annahme;
        this.userObject = userObject;

        nachrichtenAusgehend = new ArrayBlockingQueue<M4Nachricht>(M4CAP, true);
        nachrichtenEingehend = new ArrayBlockingQueue<M4Nachricht>(M4CAP, true);
        threadSollLaufen = false;
        nachrichtenEinreihen = false;
    }

    /**
     * Kostruiert einen Transportthread, wobei eine Verbindung erst (synchron!) aufgebaut wird.
     *
     * @param annahme    Das Nachrichtenannahmeinterface
     * @param hostname   Der Hostname des Servers
     * @param userObject Tag
     */
    public M4TransportThread(M4Annahme annahme, Object userObject, String hostname) {
        this(annahme, userObject);

        try {
            socket = new Socket();
            socket.setSoTimeout(M4TIMEOUT);
            socket.connect(new InetSocketAddress(hostname, M4PORT));
        } catch (Exception ex) {
            annahme.verbindungsFehler(userObject, ex);
        }
    }

    /**
     * Konstruiert einen Transportthread mit einem bestehenden Socket
     *
     * @param annahme Das Nachrichtenannahmeinterface
     * @param socket  Der Socket der Verbindung
     */
    public M4TransportThread(M4Annahme annahme, Object userObject, Socket socket) {
        this(annahme, userObject);
        this.socket = socket;

        try {
            socket.setSoTimeout(M4TIMEOUT);
        } catch (Exception ex) {
            annahme.verbindungsFehler(userObject, ex);
        }
    }

    @Override
    public void run() {
        ObjectInputStream i;
        ObjectOutputStream o;
        try {
            o = new ObjectOutputStream(socket.getOutputStream());
            i = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ioex) {
            annahme.verbindungsFehler(userObject, ioex);
            return;
        }

        threadSollLaufen = true;

        while (threadSollLaufen) {
            try {
                if (i.available() > 0) {
                    final M4Nachricht nachricht = (M4Nachricht) i.readObject();
                    if (nachricht == null)
                        continue;
                    synchronized (this) {
                        if (nachrichtenEinreihen) {
                            nachrichtenEingehend.put(nachricht);
                        } else new Thread() {
                            @Override
                            public void run() {
                                if (nachricht instanceof M4NachrichtEinfach) {
                                    annahme.verarbeiteNachricht(userObject, (M4NachrichtEinfach) nachricht);
                                } else if (nachricht instanceof M4NachrichtSpielzustand) {
                                    annahme.verarbeiteNachricht(userObject, (M4NachrichtSpielzustand) nachricht);
                                }
                            }
                        }.start();
                    }
                }
                if (nachrichtenAusgehend.peek() != null) {
                    o.writeObject(nachrichtenAusgehend.take());
                }
            } catch (SocketTimeoutException stx) {
                // Nichts tun!
            } catch (Exception e) {
                annahme.verbindungsFehler(userObject, e);
                threadSollLaufen = false;
            }
        }

        try {
            socket.close();
        } catch (Exception ex) {
            // ...
        }
    }

    public void abbruch() {
        threadSollLaufen = false;
        try {
            join();
        } catch (InterruptedException iex) {
            // Nichts tun!
        }
    }

    public void sendeNachrichtAsync(M4Nachricht nachricht) {
        try {
            nachrichtenAusgehend.add(nachricht);
        } catch (Exception e) {
            annahme.verbindungsFehler(userObject, e);
        }
    }

    public synchronized M4Nachricht warteAufNachricht() {
        nachrichtenEinreihen = true;
        try {
            M4Nachricht nachricht = nachrichtenEingehend.poll(M4BLOCKTIME, TimeUnit.MILLISECONDS);
            nachrichtenEinreihen = false;
            return nachricht;
        } catch (Exception e) {
            nachrichtenEinreihen = false;
            return null;
        }
    }

    /*
    private Socket socket;
	private Server server;
	private CommWindow cw;
	private boolean running;
	private boolean notify_on_disconnect;
	private CommStatus status;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private String name;

	public M4TransportThread(Server server, Socket socket, CommWindow cw)
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
