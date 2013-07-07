import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;

public class GameTester implements IClient {
    private IServerConnection serverConn;

    public GameTester(IServerConnection serverConn) {
        this.serverConn = serverConn;
    }

    @Override
    public void verbindungsFehler() {
        Logger.errln("GameTester: Verbindungsfehler.");
    }

    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        int x = 0, y = 0;
        if (spielfeld != null) {
            x = spielfeld.length;
            y = spielfeld[0].length;
        }
        Logger.logf("GameTester: neuer Zustand (%s) gegen %s\n", zustand.toString(), gegenspieler);
        switch (zustand) {
            case KEIN_SPIELER:
                break;
            case ANFRAGE:
                Logger.logln("GameTester: beantworte Anfrage");
                serverConn.antwortAufAnfrage(true);
                break;
            case ZUG:
                int rx = 0;
                int ry = 0;

                for (int px = x - 1; px >= 0; px--)
                    for (int py = y - 1; py >= 0; py--) {
                        if (spielfeld[px][py]) {
                            rx = px;
                            ry = py;
                            px = -1;
                            py = -1;
                        }
                    }
                if (!serverConn.zug(rx, ry)) {
                    Logger.errln("GameTester: Zug ungültig!");
                    serverConn.abbrechen();
                }
                break;
            case WARTEN:
                // nichts
                break;
        }
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

    @Override
    public void spielZuende() {
        Logger.logln("GameTester: Spiel zuende");
        serverConn.verbindungTrennen();
    }
}
