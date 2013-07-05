import uni.prakinf.m4.Logger;
import uni.prakinf.m4.server.protokoll.M4Annahme;
import uni.prakinf.m4.server.protokoll.M4NachrichtEinfach;
import uni.prakinf.m4.server.protokoll.M4NachrichtSpielzustand;
import uni.prakinf.m4.server.protokoll.M4TransportThread;

import java.net.ServerSocket;
import java.net.Socket;

public class M4ServerTest implements M4Annahme {
    private Socket socket;
    private M4TransportThread thread;

    public M4ServerTest(Socket socket) {
        Logger.logln("M4ServerTest: Erstelle Transportthread...");
        this.socket = socket;
        thread = new M4TransportThread(this, null, socket);
        thread.start();
        Logger.logln("M4ServerTest: Okay.");
    }

    public static void los() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Logger.logln("M4ServerTest: Erstelle Socket...");
                    ServerSocket serverSocket = new ServerSocket(M4TransportThread.M4PORT);
                    int i = 1;
                    while (i > 0) {
                        Logger.logln("M4ServerTest: Warte auf Anfrage...");
                        new M4ServerTest(serverSocket.accept());
                        i--;
                    }

                } catch (Exception e) {
                    Logger.logf("M4ServerTest: Fehler: %s", e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtEinfach nachrichtEinfach) {
        Logger.logf("M4ServerTest: Nachricht (einfach). Methode: %s \n", nachrichtEinfach.getMethode().toString());
        if (nachrichtEinfach.getMethode() == M4NachrichtEinfach.Methode.CL_LOGIN) {
            M4NachrichtEinfach antwort = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.RET_CL_LOGIN);
            antwort.setB(true);
        }

    }

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtSpielzustand spielzustand) {
        Logger.logf("M4ServerTest: Nachricht (Spielzustand)\n");
    }

    @Override
    public void verbindungsFehler(Object userObject, Exception exception) {
        Logger.logf("M4ServerTest: Verbindungsfehler: %s\n", exception.getMessage());
    }
}
