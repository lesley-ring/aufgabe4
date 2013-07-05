package uni.prakinf.m4.client;

import uni.prakinf.m4.Logger;
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
            Logger.logln("ServerConnection: Erstelle Thread...");
            thread = new M4TransportThread(this, null, server);
            thread.start();
            verbindungszustand = Verbindungszustand.VERBUNDEN;
        }

        // Anmeldung
        Logger.logln("ServerConnection: Anmeldung...");
        M4NachrichtEinfach login_nr = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_LOGIN);
        login_nr.setSa(name);
        login_nr.setSb(passwort);
        thread.sendeNachrichtAsync(login_nr);

        // Antwort abwarten
        Logger.logln("ServerConnection: Antwort abwarten...");
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
        M4NachrichtEinfach abbruch = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_ABBRECHEN);
        thread.sendeNachrichtAsync(abbruch);

        // Keine Antwort
    }

    public boolean neuesSpiel(IClient.Spiel spiel, int x, int y) {
        M4NachrichtEinfach ns = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_NEUESSPIEL);
        ns.setSpiel(spiel);
        ns.setIa(x);
        ns.setIb(y);
        thread.sendeNachrichtAsync(ns);

        // Antwort abwarten
        M4NachrichtEinfach antwort = thread.warteAufNachricht(M4NachrichtEinfach.Methode.RET_CL_NEUESSPIEL);

        if (antwort != null) {
            return antwort.isB();
        }
        return false;
    }

    public boolean mitspielen(String name, IClient.Spiel spiel) {
        M4NachrichtEinfach ms = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_MITSPIELEN);
        ms.setSa(name);
        ms.setSpiel(spiel);
        thread.sendeNachrichtAsync(ms);

        // Antwort abwarten
        M4NachrichtEinfach antwort = thread.warteAufNachricht(M4NachrichtEinfach.Methode.RET_CL_MITSPIELEN);

        if (antwort != null) {
            return antwort.isB();
        }
        return false;
    }

    public void nachricht(String name, String nachricht) {

    }

    // M4Annahme Methoden
    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtEinfach nachrichtEinfach) {
        M4Decoder.decodiereServerNachricht(client, nachrichtEinfach);
    }

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtSpielzustand spielzustand) {
        M4Decoder.decodiereSpielzustand(client, spielzustand);
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
                Logger.logf("ServerConnection: Verbindungsfehler (%s)\n", exception.getMessage());
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
                if (thread != null) {
                    thread.sendeNachrichtAsync(new M4NachrichtEinfach(M4NachrichtEinfach.Methode.CL_TRENNEN));
                    thread.abbruch();
                    thread = null;
                    verbindungszustand = Verbindungszustand.GETRENNT;
                }
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
