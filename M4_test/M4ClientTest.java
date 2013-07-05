import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;
import uni.prakinf.m4.client.ServerConnection;

public class M4ClientTest implements IClient {
    public void los() {
        IServerConnection serverConnectionA = new ServerConnection();
        serverConnectionA.setClient(this);
        Logger.logln("M4ClientTest: Anmeldung A...");
        if (!serverConnectionA.login("localhost", "Clemens", "test")) {
            Logger.logln("M4ClientTest: Fehler!");
            serverConnectionA.verbindungTrennen();
            return;
        } else
            Logger.logln("M4ClientTest: Anmeldung A okay!");

        if (!serverConnectionA.neuesSpiel(Spiel.CHOMP, 5, 7)) {
            Logger.errln("Kann kein neues Spiel erstellen!");
            serverConnectionA.verbindungTrennen();
            return;
        }

        Logger.logln("M4ClientTest: Spiele Chomp.");

        try {
            Thread.sleep(500);
        } catch (Exception x) {

        }

        serverConnectionA.verbindungTrennen();
    }

    @Override
    public void verbindungsFehler() {
        Logger.logln("M4ClientTest: Verbindungsfehler!");
    }

    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        Logger.logln("M4ClientTest: Neuer Chomp-Zustand!");
        Logger.logf("M4ClientTest: Zustand: %s, gegen: %s\n", zustand.toString(), gegenspieler);
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        Logger.logln("M4ClientTest: Neuer Vier Gewinnt-Zustand!");
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel) {
        Logger.logln("M4ClientTest: Spieler: ");
        for (String sname : name)
            Logger.logln("              " + sname);
    }

    @Override
    public void nachricht(String name, String nachricht) {
        Logger.logf("M4ClientTest: Neue Nachricht von %s: %s\n", name, nachricht);
    }

    @Override
    public void spielZuende() {
        Logger.logln("M4ClientTest: Spiel zuende!");
    }
}
