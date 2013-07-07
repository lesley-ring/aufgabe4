import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.client.IServerConnection;
import uni.prakinf.m4.client.ServerConnection;

public class M4ClientTest {
    IServerConnection serverConnectionA, serverConnectionB;
    public void los() {
        serverConnectionA = new ServerConnection();
        serverConnectionA.setClient(new GameTester(serverConnectionA));
        Logger.logln("M4ClientTest: Anmeldung A...");
        if (!serverConnectionA.login("localhost", "Clemens", "test")) {
            Logger.logln("M4ClientTest: Anmeldung A fehlgeschlagen");
            serverConnectionA.verbindungTrennen();
            return;
        } else
            Logger.logln("M4ClientTest: Anmeldung A okay!");

        if (!serverConnectionA.neuesSpiel(IClient.Spiel.CHOMP, 5, 7)) {
            Logger.errln("Kann kein neues Spiel erstellen!");
            serverConnectionA.verbindungTrennen();
            return;
        }

        Logger.logln("M4ClientTest: Spiel erstellt.");

        serverConnectionB = new ServerConnection();
        serverConnectionB.setClient(new GameTester(serverConnectionB));
        Logger.logln("M4ClientTest: Anmeldung B...");
        if (!serverConnectionB.login("localhost", "Paul", "test")) {
            Logger.errln("M4ClientTest: Anmeldung B fehlgeschlagen.");
            serverConnectionA.verbindungTrennen();
            serverConnectionB.verbindungTrennen();
            return;
        }
        else
            Logger.logln("M4ClientTest: Anmeldung B okay!");

        if(!serverConnectionB.mitspielen("Clemens", IClient.Spiel.CHOMP)) {
            Logger.errln("M4ClientTest: Mitspielen gescheitert.");
        }

        /*try {
            Thread.sleep(2000);
        } catch (Exception x) {

        }

        serverConnectionA.verbindungTrennen();

        try {
            Thread.sleep(2000);
        } catch (Exception x) {

        }

        serverConnectionB.verbindungTrennen();*/
    }
}
