package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;
import uni.prakinf.m4.server.Server;
import uni.prakinf.m4.server.protokoll.M4TransportThread;

public class Sitzung implements IClient, IServerConnection {
    private Server server;
    private M4TransportThread thread;
    private Sitzungszustand sitzungszustand;
    private IClient.Spiel spiel;

    public Sitzung(Server server, M4TransportThread thread) {
        this.server = server;
        this.thread = thread;
        sitzungszustand = Sitzungszustand.VERBUNDEN;
    }

    // IServerConnection Methoden
    @Override
    public boolean login(String server, String name, String passwort) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void antwortAufAnfrage(boolean ok) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean zug(int x, int y) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void abbrechen() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean neuesSpiel(Spiel spiel, int x, int y) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean mitspielen(String name, Spiel spiel) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void verbindungTrennen() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // IClient Methoden
    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void nachricht(String name, String nachricht) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    // Nie aufgerufene Methoden
    @Override
    public void setClient(IClient client) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void verbindungsFehler() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private enum Sitzungszustand {
        VERBUNDEN,
        ANGEMELDET,
        SPIELT
    }
}
