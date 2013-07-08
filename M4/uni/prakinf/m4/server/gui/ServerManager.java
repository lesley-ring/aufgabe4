package uni.prakinf.m4.server.gui;

import uni.prakinf.m4.Logger;
import uni.prakinf.m4.server.Server;

import java.io.File;

public class ServerManager {
    private ServerManagerWindow serverManagerWindow;
    private Server server;
    private boolean running;

    public static void main(String[] args) {
        new ServerManager().initWindow();
    }

    public ServerManager() {
        server = new Server();
        running = false;

        serverManagerWindow = new ServerManagerWindow(this);
        serverManagerWindow.setLocationRelativeTo(null);
    }

    public void initWindow() {
        serverManagerWindow.setVisible(true);
        Logger.tpane = serverManagerWindow.getLogPane();
    }

    public void startServer() {
        if (!running) {
            server.startServer();
            running = true;
        }
    }

    public void stopServer() {
        if (running) {
            server.stopServer();
            running = false;
        }
    }

    public void resetPasswords() {
        try {
            new File(".cpasswords").delete();
            Logger.logln("Passwörter zurückgesetzt");
        } catch (Exception e) {
            Logger.errf("Fehler beim Zurücksetzen der Passwörter: %s\n", e.getMessage());
        }
    }
}
