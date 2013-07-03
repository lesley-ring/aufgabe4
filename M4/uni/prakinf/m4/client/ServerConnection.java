package uni.prakinf.m4.client;

import uni.prakinf.m4.server.protokoll.*;

/**
 * Diese Klasse implementiert uni.prakinf.m4.client.IServerConnection
 */
public class ServerConnection implements IServerConnection, M4Annahme {
    private IClient client;
    private IClient.Zustand letzterZustand;
    private Verbindungszustand verbindungszustand;
    private M4TransportThread thread;

    public ServerConnection() {
        this.letzterZustand = IClient.Zustand.ABBRUCH;
        verbindungszustand = Verbindungszustand.GETRENNT;
        client = null;
        thread = null;
    }

    // IServerConnection Methoden
    public void setClient(IClient client) {
        this.client = client;
    }

    public boolean login(String server, String name, String passwort) {
        // Verbindung aufbauen
        thread = new M4TransportThread(this, null, server);
        thread.start();
        verbindungszustand = Verbindungszustand.VERBUNDEN;

        // Anmeldung
        M4NachrichtEinfach login_nr = new M4NachrichtEinfach(M4NachrichtEinfach.Art.CS_LOGIN, false, name, passwort, null);
        thread.sendeNachrichtAsync(login_nr);

        // Antwort abwarten
        M4Nachricht antwort = thread.warteAufNachricht();
        if (antwort instanceof M4NachrichtEinfach) {
            M4NachrichtEinfach antwort_e = (M4NachrichtEinfach) antwort;
            if (antwort_e.getArt() == M4NachrichtEinfach.Art.SC_LOGIN && antwort_e.isErfolg()) {
                return true;
            } else {
                return false;
            }
        }
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

    // M4Annahme Methoden

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtEinfach nachrichtEinfach) {

    }

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtSpielzustand spielzustand) {

    }

    @Override
    public void verbindungsFehler(Object userObject, Exception exception) {
        switch (verbindungszustand) {
            case SPIELT:
            case ANGEMELDET:
                client.verbindungsFehler();
                break;
            case GETRENNT:
            case VERBUNDEN:
                break;
        }
    }

    private enum Verbindungszustand {
        GETRENNT,
        VERBUNDEN,
        ANGEMELDET,
        SPIELT
    }

}
