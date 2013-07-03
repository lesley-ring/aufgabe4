import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;
import uni.prakinf.m4.client.ServerConnection;

public class M4ClientTest implements IClient {
    public void los() {
        IServerConnection conn = new ServerConnection();
        System.out.println("M4ClientTest: Anmeldung...");
        if(!conn.login("localhost", "Clemens", "test")) {
            System.out.println("M4ClientTest: Fehler!");
        }
        conn.verbindungTrennen();
    }


    @Override
    public void verbindungsFehler() {
        System.out.println("M4ClientTest: Verbindungsfehler!");
    }

    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        System.out.println("M4ClientTest: Neuer uni.prakinf.m4.client.Chomp-Zustand!");
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        System.out.println("M4ClientTest: Neuer Vier Gewinnt-Zustand!");
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel) {
        System.out.println("M4ClientTest: uni.prakinf.m4.client.Spieler: ");
        for(String sname : name)
            System.out.println("M4ClientTest: " + name);
    }

    @Override
    public void nachricht(String name, String nachricht) {
        System.out.printf("M4ClientTest: Neue Nachricht von %s: %s\n", name, nachricht);
    }
}
