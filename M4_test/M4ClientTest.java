import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;
import uni.prakinf.m4.client.ServerConnection;

public class M4ClientTest implements IClient {
    public void los() {
        IServerConnection conn = new ServerConnection();
        System.out.println("Anmeldung...");
        if(!conn.login("localhost", "Clemens", "test")) {
            System.out.println("Fehler!");
        }
    }


    @Override
    public void verbindungsFehler() {
        System.out.println("Verbindungsfehler!");
    }

    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        System.out.println("Neuer Chomp-Zustand!");
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        System.out.println("Neuer Vier Gewinnt-Zustand!");
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel) {
        System.out.println("Spieler: ");
        for(String sname : name)
            System.out.println(name);
    }

    @Override
    public void nachricht(String name, String nachricht) {
        System.out.printf("Neue Nachricht von %s: %s", name, nachricht);
    }
}
