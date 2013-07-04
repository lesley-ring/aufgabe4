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
        if (verbindungszustand == Verbindungszustand.ANGEMELDET || verbindungszustand == Verbindungszustand.SPIELT)
            return false;
        if (verbindungszustand == Verbindungszustand.GETRENNT) {
            System.out.println("ServerConnection: Erstelle Thread...");
            thread = new M4TransportThread(this, null, server);
            thread.start();
            verbindungszustand = Verbindungszustand.VERBUNDEN;
        }

        // Anmeldung
        System.out.println("ServerConnection: Anmeldung...");
        M4NachrichtEinfach login_nr = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_LOGIN);
        login_nr.setSa(name);
        login_nr.setSb(passwort);
        thread.sendeNachrichtAsync(login_nr);

        // Antwort abwarten
        System.out.println("ServerConnection: Antwort abwarten...");
        M4NachrichtEinfach antwort = thread.warteAufNachricht(M4NachrichtEinfach.Methode.RET_CL_LOGIN);

        if (antwort != null) {
            if (antwort.isB()) {
                verbindungszustand = Verbindungszustand.ANGEMELDET;
                return true;
            } else {
                verbindungszustand = Verbindungszustand.VERBUNDEN;
            }
        }
        return false;
    }

    public void antwortAufAnfrage(boolean ok) {
        if (verbindungszustand == Verbindungszustand.GETRENNT) {
            client.verbindungsFehler();
            return;
        }
        M4NachrichtEinfach antwort_auf_anfrage = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_ANTWORT_AUF_ANFRAGE);
        antwort_auf_anfrage.setB(true);
        thread.sendeNachrichtAsync(antwort_auf_anfrage);
    }

    public boolean zug(int x, int y) {
        M4NachrichtEinfach zug = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_ZUG);
        zug.setIa(x);
        zug.setIb(y);
        thread.sendeNachrichtAsync(zug);

        // Antwort abwarten
        M4NachrichtEinfach antwort = thread.warteAufNachricht(M4NachrichtEinfach.Methode.RET_CL_ZUG);

        if (antwort != null) {
            return antwort.isB();
        }
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

        // HAllo
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
                System.out.printf("ServerConnection: Verbindungsfehler (%s)\n", exception.getMessage());
                break;
        }
        verbindungszustand = Verbindungszustand.GETRENNT;
    }

    @Override
    public void verbindungTrennen() {
        switch (verbindungszustand) {
            case SPIELT:
            case ANGEMELDET:
            case VERBUNDEN:
                if (thread != null)
                    thread.abbruch();
        }
    }

    private enum Verbindungszustand {
        GETRENNT,
        VERBUNDEN,
        ANGEMELDET,
        SPIELT
    }

}
