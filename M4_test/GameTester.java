import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;

import java.util.Random;

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
                int rx = new Random().nextInt(x);
                int ry = new Random().nextInt(y);
                while(!serverConn.zug(rx, ry)){
                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {

                    }
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
