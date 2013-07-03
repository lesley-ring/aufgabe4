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
        System.out.println("M4ServerTest: Erstelle Transportthread...");
        this.socket = socket;
        thread = new M4TransportThread(this, null, socket);
        thread.start();
        System.out.println("M4ServerTest: Okay.");
    }

    public static void los() {
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("M4ServerTest: Erstelle Socket...");
                    ServerSocket serverSocket = new ServerSocket(M4TransportThread.M4PORT);
                    int i = 1;
                    while (i > 0) {
                        System.out.println("M4ServerTest: Warte auf Anfrage...");
                        new M4ServerTest(serverSocket.accept());
                        i--;
                    }

                } catch (Exception e) {
                    System.out.printf("M4ServerTest: Fehler: %s", e.getMessage());
                }
            }
        }.start();
    }

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtEinfach nachrichtEinfach) {
        System.out.printf("M4ServerTest: Nachricht (einfach). Art: %s \n", nachrichtEinfach.getArt().toString());
        if(nachrichtEinfach.getArt() == M4NachrichtEinfach.Art.CS_LOGIN)
            thread.sendeNachrichtAsync(new M4NachrichtEinfach(M4NachrichtEinfach.Art.SC_LOGIN, true, null, null, null));

    }

    @Override
    public void verarbeiteNachricht(Object userObject, M4NachrichtSpielzustand spielzustand) {
        System.out.printf("M4ServerTest: Nachricht (Spielzustand)\n");
    }

    @Override
    public void verbindungsFehler(Object userObject, Exception exception) {
        System.out.printf("M4ServerTest: Verbindungsfehler: %s\n", exception.getMessage());
    }
}
