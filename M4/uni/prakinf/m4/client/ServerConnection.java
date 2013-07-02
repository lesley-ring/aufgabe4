package uni.prakinf.m4.client;

import uni.prakinf.m4.server.protokoll.M4NachrichtEinfach;
import uni.prakinf.m4.server.protokoll.M4NachrichtSpielzustand;
import uni.prakinf.m4.server.protokoll.M4NachrichtenAnnahme;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** 
 * Diese Klasse implementiert uni.prakinf.m4.client.IServerConnection
 */
public class ServerConnection implements IServerConnection, M4NachrichtenAnnahme {
	private IClient client;
	private ObjectOutputStream conn_out;
	private ObjectInputStream conn_in;

    private IClient.Zustand letzterZustand;
    private Verbindungszustand verbindungszustand;

    public ServerConnection() {
        this.letzterZustand = IClient.Zustand.ABBRUCH;
        verbindungszustand = Verbindungszustand.GETRENNT;
        conn_out = null;
        conn_in = null;
        client = null;
    }

    // IServerConnection Methoden
    public void setClient(IClient client) {
		this.client = client;
	}

	public boolean login(String server, String name, String passwort) {
		return false;
	}

    public void antwortAufAnfrage(boolean ok) {

    }

    public boolean zug(int x, int y) {
		return false;
	}
	
	public void abbrechen() {
	}
	
	public boolean neuesSpiel(IClient.Spiel spiel, int x, int y) {
		return false;
	}
	
	public boolean mitspielen(String name, IClient.Spiel spiel) {
		return false;
	}
	
	public boolean nachricht(String name, String nachricht) {
		return false;
	}

    // M4NachrichtenAnnahme Methoden


    @Override
    public void verarbeiteNachricht(M4NachrichtEinfach nachrichtEinfach) {

    }

    @Override
    public void verarbeiteNachricht(M4NachrichtSpielzustand spielzustand) {

    }

    @Override
    public void verbindungsFehler(Exception exception) {

    }

    private enum Verbindungszustand {
        GETRENNT,
        VERBUNDEN,
        ANGEMELDET,
        SPIELT
    }

}
