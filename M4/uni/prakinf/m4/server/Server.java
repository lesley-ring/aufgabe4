package uni.prakinf.m4.server;

import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.server.protokoll.*;
import uni.prakinf.m4.server.sitzung.Sitzung;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Server implements M4Annahme {
    private List<M4TransportThread> threads;
    private List<Sitzung> sitzungen;
    private VerbindungsThread vthread;

    public Server() {
        threads = new LinkedList<M4TransportThread>();
        sitzungen = new LinkedList<Sitzung>();
    }

    public void startServer() {
        Logger.logln("Server: Starte Server...");
        vthread = new VerbindungsThread(this);
        vthread.start();
    }

    public void stopServer() {
        if (vthread != null) {
            synchronized (threads) {
                for (M4TransportThread thread : threads) {
                    thread.abbruch();
                }
                threads.clear();
            }
            synchronized (sitzungen) {
                sitzungen.clear();
            }

            vthread.anhalten();
            vthread = null;
        }

    }

    public void entferne(Sitzung sitzung, M4TransportThread thread) {
        try {
            synchronized (sitzungen) {
                sitzungen.remove(sitzung);
            }
            synchronized (threads) {
                threads.remove(thread);
            }
        } catch (Exception ex) {

        }
        sitzungenVerteilen();
    }

    public Sitzung findeSitzung(String name) {
        synchronized (sitzungen) {
            for (Sitzung sitzung : sitzungen) {
                if (sitzung.getSitzungName().equals(name))
                    return sitzung;
            }
        }
        return null;
    }

    public void nachrichtAnAlle(final String name, final String message) {
        final ArrayList<Sitzung> sessions = new ArrayList<Sitzung>();
        synchronized (sitzungen) {
            for (Sitzung sitzung : sitzungen)
                if (sitzung.getSitzungName() != null && !sitzung.getSitzungName().isEmpty()) {
                    sessions.add(sitzung);
                }
        }

        new Thread() {
            public void run() {
                // Logger.logf("Server: Verteile %d Sitzungen...\n", sessions.size());
                for (Sitzung sitzung : sessions) {
                    try {
                        sitzung.nachricht(name, message);
                    } catch (Exception e) {
                        Logger.errf("Server: Fehler bei Nachrichtenübetragung: %s\n", e.getMessage());
                    }
                }
                // Logger.logln("Server: Sitzungsverteilung fertig.");
            }
        }.start();
    }

    public void sitzungenVerteilen() {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<IClient.Spiel> games = new ArrayList<IClient.Spiel>();
        ArrayList<Boolean> avails = new ArrayList<Boolean>();
        final ArrayList<Sitzung> sessions = new ArrayList<Sitzung>();
        synchronized (sitzungen) {
            for (Sitzung sitzung : sitzungen)
                if (sitzung.getSitzungName() != null && !sitzung.getSitzungName().isEmpty()) {
                    names.add(sitzung.getSitzungName());
                    games.add(sitzung.getSpielTyp());
                    avails.add(sitzung.wartet());
                    sessions.add(sitzung);
                }
        }

        final String[] anames = names.toArray(new String[names.size()]);
        final IClient.Spiel[] agames = games.toArray(new IClient.Spiel[games.size()]);
        final boolean[] aavails = new boolean[avails.size()];
        for(int i = 0; i < avails.size(); i++)
            aavails[i] = avails.get(i);

        new Thread() {
            public void run() {
                // Logger.logf("Server: Verteile %d Sitzungen...\n", sessions.size());
                for (Sitzung sitzung : sessions) {
                    try {
                        sitzung.spielerListe(anames, agames, aavails);
                    } catch (Exception e) {
                        Logger.errf("Server: Fehler bei Sitzungsverteilung: %s\n", e.getMessage());
                    }
                }
                // Logger.logln("Server: Sitzungsverteilung fertig.");
            }
        }.start();
    }

    // M4Annahme Methoden
    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtEinfach nachrichtEinfach) {
        if (userObject instanceof Sitzung)
            M4Decoder.decodiereClientNachricht((Sitzung) userObject, nachrichtEinfach);
        else
            Logger.errln("Server: Fehlerhaftes userObject vom Transportthread!");
    }

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtSpielzustand spielzustand) {
        // Sollte nie aufgerufen werden, da diese Nachrichten vom Client kommen!
        Logger.errln("Server: Fehlerhafte Nachricht vom Client (Spielzustand), ignoriert");
    }

    @Override
    public void verbindungsFehler(Object userObject, Exception exception) {
        if (userObject instanceof Sitzung) {
            Sitzung sitzung = (Sitzung) userObject;
            Logger.errf("Server: Ausnahme in Transportthread: %s\n", exception.getMessage());
            sitzung.verbindungTrennen();
        } else {
            Logger.errln("Server: Fehlerhaftes userObject vom Transportthread!");
        }
    }

    public boolean istAngemeldet(String name) {
        synchronized (sitzungen) {
            for (Sitzung sitzung : sitzungen)
                if (sitzung.getSitzungName() != null && sitzung.getSitzungName().equals(name))
                    return true;
        }
        return false;
    }

    // Verbindungsannahme
    private class VerbindungsThread extends Thread {
        private Server server;
        private boolean weiterlaufen;
        private ServerSocket socket;

        private VerbindungsThread(Server server) {
            this.server = server;
            weiterlaufen = false;
        }

        public void run() {
            weiterlaufen = true;
            try {
                socket = new ServerSocket(M4TransportThread.M4PORT);
                socket.setSoTimeout(1000);
            } catch (Exception e) {
                Logger.errf("Server: Fehler beim Erstellen des ServerSocket: %s\n", e.getMessage());
            }
            Logger.logln("Server: Höre ab...");
            while (weiterlaufen) {
                try {
                    Socket s = socket.accept();
                    M4TransportThread transportThread = new M4TransportThread(server, null, s);
                    Sitzung sitzung = new Sitzung(server, transportThread);
                    transportThread.setUserObject(sitzung);
                    synchronized (server.sitzungen) {
                        server.sitzungen.add(sitzung);
                    }
                    synchronized (server.threads) {
                        server.threads.add(transportThread);
                    }

                    transportThread.start();
                } catch (SocketTimeoutException stex) {
                    // Nichts tun!
                } catch (Exception e) {
                    Logger.errf("Server: Fehler beim Abhören der Verbindung: %s\n", e.getMessage());
                    weiterlaufen = false;
                }
            }
            Logger.logln("Server: Verbindungsthread beenden...");
            try {
                socket.close();
                Logger.logln("Server: Beendet.");
            } catch (Exception e) {
                Logger.errf("Server: Konnte Socket nicht freigeben: %s\n", e.getMessage());
            }
        }

        public void anhalten() {
            weiterlaufen = false;
        }
    }


/* implements ICommWindowCallback {

	public static final int DEFAULT_PORT = 4444;
	public static final String DEFAULT_CHANNEL_LIST_DELIMITER = "%";

	private ServerListenerThread listener;
	private List<M4TransportThread> clients;
	private Map<String, String> logins;
	private ServerCommWindow cw;
	private boolean running;

	public Server() {
		clients = new LinkedList<M4TransportThread>();
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
		for (M4TransportThread client : clients) {
			client.stopCommunication();
		}
		clients.clear();
		running = false;
	}

	public void clientConnected(Socket socket) {
		try {
			M4TransportThread client = new M4TransportThread(this,
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
			for (M4TransportThread client : clients)
				client.sendMessage(message);
		}
	}
	
	public void relayMessage(M4NachrichtEinfach message, M4TransportThread source) {
		synchronized (clients) {
			for (M4TransportThread client : clients) {
				if(client.getClientName().equals(message.getParamB()) || client.getClientName().equals(source.getClientName()))
					client.sendMessage(new M4NachrichtEinfach(MessageType.MESSAGE_BROADCAST, source.getClientName(), message.getParamA()));
			}
		}
	}

	public void clientDisconnected(M4TransportThread client) {
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
		login_valid = PasswortVerwaltung.passwortGueltig(name, password);

		// Ist der Client bereits eingeloggt?
		for (M4TransportThread client : clients) {
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
				for (M4TransportThread client : clients) {
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
				for (M4TransportThread client : clients) {
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
