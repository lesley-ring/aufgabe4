package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;
import uni.prakinf.m4.server.PasswortVerwaltung;
import uni.prakinf.m4.server.Server;
import uni.prakinf.m4.server.protokoll.M4Nachricht;
import uni.prakinf.m4.server.protokoll.M4NachrichtEinfach;
import uni.prakinf.m4.server.protokoll.M4NachrichtSpielzustand;
import uni.prakinf.m4.server.protokoll.M4TransportThread;

public class Sitzung implements IClient {
    private Server server;
    private M4TransportThread thread;
    private Sitzungszustand sitzungszustand;
    private LaufendesSpiel spiel;

    private String sitzung_name;

    public Sitzung(Server server, M4TransportThread thread) {
        this.server = server;
        this.thread = thread;
        sitzungszustand = Sitzungszustand.VERBUNDEN;
        spiel = null;
    }

    public M4TransportThread getThread() {
        return thread;
    }

    public void sendeNachrichtAsync(M4Nachricht nachricht) {
        if (thread != null)
            thread.sendeNachrichtAsync(nachricht);
    }

    public void sendeErgebnisAsync(M4NachrichtEinfach.Methode methode, boolean erfolg) {
        M4NachrichtEinfach nachrichtEinfach = new M4NachrichtEinfach(methode);
        nachrichtEinfach.setB(erfolg);
        thread.sendeNachrichtAsync(nachrichtEinfach);
    }

    // IServerConnection Methoden - Aufruf von außen durch Decoder im Server!
    public void login(String server, String name, String passwort) {
        if (sitzungszustand == Sitzungszustand.VERBUNDEN) {
            boolean result = PasswortVerwaltung.passwortGueltig(name, passwort);
            if (result)
                sitzungszustand = Sitzungszustand.ANGEMELDET;
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_LOGIN, result);
        } else
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_LOGIN, false);

    }

    public void antwortAufAnfrage(boolean ok) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean zug(int x, int y) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void abbrechen() {

    }

    public void neuesSpiel(Spiel spiel, int x, int y) {
        if (sitzungszustand != Sitzungszustand.ANGEMELDET)
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_NEUESSPIEL, false);
        switch (spiel) {
            case CHOMP:
                spiel = new LaufendesSpielChomp(this, x, y);
                sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_NEUESSPIEL, true);
                spiel.los();
                break;
            case VIER_GEWINNT:
                break;
        }
    }

    public void mitspielen(String name, Spiel spiel) {

    }

    public void verbindungTrennen() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // IClient Methoden - Aufruf: Nachricht weiterleiten an Client
    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        M4NachrichtSpielzustand m4z = new M4NachrichtSpielzustand(zustand, gegenspieler, spielfeld);
        thread.sendeNachrichtAsync(m4z);
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        M4NachrichtSpielzustand m4z = new M4NachrichtSpielzustand(zustand, gegenspieler, spielfeld);
        thread.sendeNachrichtAsync(m4z);
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel) {
        M4NachrichtEinfach liste_nr = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.SRV_SPIELERLISTE);
        liste_nr.setLs(name);
        liste_nr.setLspiel(spiel);
        thread.sendeNachrichtAsync(liste_nr);
    }

    @Override
    public void nachricht(String name, String nachricht) {
        M4NachrichtEinfach nr = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.SRV_NACHRICHT);
        nr.setSa(name);
        nr.setSb(nachricht);
        thread.sendeNachrichtAsync(nr);
    }

    // Nie aufgerufene Methoden
    public void setClient(IClient client) {
        // Nie aufgerufen!
    }

    @Override
    public void verbindungsFehler() {
        // Nie aufgerufen!
    }

    private enum Sitzungszustand {
        VERBUNDEN,
        ANGEMELDET,
        SPIELT
    }
}
