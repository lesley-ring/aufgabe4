package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.server.Server;
import uni.prakinf.m4.server.protokoll.M4TransportThread;

public class Sitzung {
    private Server server;
    private M4TransportThread thread;
    private Sitzungszustand sitzungszustand;
    private IClient.Spiel spiel;

    public Sitzung(Server server, M4TransportThread thread) {
        this.server = server;
        this.thread = thread;
        sitzungszustand = Sitzungszustand.VERBUNDEN;
    }

    private enum Sitzungszustand {
        VERBUNDEN,
        ANGEMELDET,
        SPIELT
    }
}
