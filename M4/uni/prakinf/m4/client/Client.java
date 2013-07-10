package uni.prakinf.m4.client;

import uni.prakinf.m4.client.forms.GameWindow;
import uni.prakinf.m4.client.forms.LobbyWindow;
import uni.prakinf.m4.client.forms.LoginDialog;
import uni.prakinf.m4.client.forms.NewGameDialog;

import javax.swing.*;
import java.awt.*;

public class Client implements IClient {
    // Zustand
    private ClientState clientState;
    // Fenster
    private LoginDialog loginDialog;
    private LobbyWindow lobbyWindow;
    private NewGameDialog newGameDialog;
    private GameWindow gameWindow;
    // Status
    private ServerConnection serverConnection;
    private String myName;
    private String[] connected_names;
    private IClient.Spiel[] connected_games;
    private String privateMessagePartner;

    public Client() {
        loginDialog = new LoginDialog(this);
        loginDialog.pack();
        loginDialog.setLocationRelativeTo(null);

        lobbyWindow = new LobbyWindow(this);
        lobbyWindow.setPreferredSize(new Dimension(1024, 500));
        lobbyWindow.pack();
        lobbyWindow.setLocationRelativeTo(null);

        newGameDialog = new NewGameDialog(this);
        newGameDialog.pack();
        newGameDialog.setLocationRelativeTo(null);

        gameWindow = new GameWindow(this);
        gameWindow.setPreferredSize(new Dimension(800, 600));
        gameWindow.pack();
        gameWindow.setLocationRelativeTo(null);

        serverConnection = new ServerConnection();
        serverConnection.setClient(this);

        clientState = ClientState.NONE;
        myName = "";
        privateMessagePartner = "";
    }

    public static void main(String[] args) {
        new Client().start();
    }

    public void start() {
        goToState(ClientState.LOGIN);
    }

    /**
     * Wechsel von einem Sitzungszustand zum nächsten
     *
     * @param targetState neuer Sitzungszustand
     */
    private synchronized void goToState(ClientState targetState) {
        if (targetState == clientState)
            return;

        // Abbau des alten Zustandes
        switch (clientState) {
            case SESSION_EST:
                if (targetState == ClientState.LOGIN) {
                    serverConnection.verbindungTrennen();
                    newGameDialog.setVisible(false);
                    lobbyWindow.setVisible(false);
                }
                break;
            case LOGIN:
                if (targetState == ClientState.NONE || targetState == ClientState.SESSION_EST)
                    loginDialog.setVisible(false);
                break;
            case PLAYING_C4:
            case PLAYING_CHOMP:
                if (targetState != ClientState.PLAYING_C4 && targetState != ClientState.PLAYING_CHOMP) {
                    if (targetState != ClientState.SESSION_EST) {
                        lobbyWindow.setVisible(false);
                        serverConnection.verbindungTrennen();
                        newGameDialog.setVisible(false);
                    }
                    gameWindow.setVisible(false);
                }
                break;
            default:
                break;
        }

        // Aufbau des neuen Zustandes
        switch (targetState) {
            case LOGIN:
                loginDialog.setVisible(true);
                loginDialog.resetEntries();
                break;
            case SESSION_EST:
                lobbyWindow.setVisible(true);
                lobbyWindow.resetState();
                lobbyWindow.updatePlayerName(myName);
                break;
            case PLAYING_CHOMP:
            case PLAYING_C4:
                gameWindow.setVisible(true);
                gameWindow.resetState();
                break;
            case NONE:
                System.exit(0);
                break;
            default:
                break;
        }
        clientState = targetState;
    }

    // Hilfsfunktionen

    /**
     * Zeigt eine modale Fehlermeldung an
     *
     * @param msg Fehlernachricht
     */
    private void errorMsg(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Fehler!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Erzeugt eine menschenlesbare Nachricht aus einer Zustandsaufzählung
     *
     * @param enemy   Name des Gegners
     * @param zustand Zustand des Spiels
     * @return
     */
    private String formatStatus(String enemy, Zustand zustand) {
        switch (zustand) {
            case KEIN_SPIELER:
                return "Warte auf Gegenspieler...";
            case ANFRAGE:
                return String.format("Anfrage von %s", enemy);
            case ZUG:
                return "Du bist dran!";
            case WARTEN:
                return String.format("%s ist dran...", enemy);
            case GEWONNEN:
                return "Du hast gewonnen!";
            case VERLOREN:
                return "Du hast verloren!";
            case ABBRUCH:
                return "Spiel abgebrochen.";
            case UNENTSCHIEDEN:
                return "Das Spiel ist unentschieden.";
        }
        return "";
    }

    /**
     * Überprüft, ob der Spielzustand eine Anfrage darstellt und zeigt gegebenfalls ein modales Dialogfeld zur Abfrage.
     *
     * @param zustand      Zustand des Spiels
     * @param gegenspieler Name des Gegners
     */
    private void askForClearance(Zustand zustand, String gegenspieler) {
        if (zustand == Zustand.ANFRAGE) {
            int result = JOptionPane.showConfirmDialog(null, String.format("Darf %s mitspielen?", gegenspieler), "Anfrage", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                serverConnection.antwortAufAnfrage(true);
            } else {
                serverConnection.antwortAufAnfrage(false);
            }
        }
    }

    // LoginDialog

    /**
     * Veranlasst die Anmeldung am Server
     *
     * @param hostname Name des Servers
     * @param name     Benutzername
     * @param password Passwort
     */
    public void onLoginDialogOKClick(String hostname, String name, String password) {
        boolean result = serverConnection.login(hostname, name, password);
        if (result) {
            myName = name;
            goToState(ClientState.SESSION_EST);
        } else {
            errorMsg("Anmeldung fehlgeschlagen!");
        }
    }

    /**
     * Wird bei Verlassen des Login-Dialoges aufgerufen
     */
    public void onLoginDialogExit() {
        goToState(ClientState.NONE);
    }

    // LobbyWindow
    public void onLobbyWindowLogout() {
        goToState(ClientState.LOGIN);
    }

    public void onLobbyWindowExit() {
        goToState(ClientState.NONE);
    }

    public void setPrivateMessagePartner(int index) {
        if (index == -1) {
            privateMessagePartner = "";
            lobbyWindow.updateStatus("Nachricht wird an alle gesendet.");
        } else {
            privateMessagePartner = connected_names[index];
            lobbyWindow.updateStatus(String.format("Nachricht wird an %s gesendet.", privateMessagePartner));
        }

    }

    public void onMessageLineEnter(String message) {
        if (serverConnection.nachricht(privateMessagePartner, message)) {
            lobbyWindow.addLineToMessagePane(String.format("[%12s] %s", myName, message));
        } else {
            lobbyWindow.addLineToMessagePane("Nachricht konnte nicht gesendet werden.");
        }
    }

    public void onJoinGame(int index) {
        boolean result = serverConnection.mitspielen(connected_names[index], connected_games[index]);
        if (!result) {
            errorMsg("Fehler bei Spielteilnahme!");
        }
    }

    public void onNewGame() {
        newGameDialog.setVisible(true);
        newGameDialog.resetState();
    }

    // NewGameDialog
    public void newGame(Spiel game, int sizex, int sizey) {
        boolean result = serverConnection.neuesSpiel(game, sizex, sizey);
        if (!result) {
            errorMsg("Fehler bei Spielstart!");
        }
    }

    // GameWindow
    public void leaveGame() {
        serverConnection.abbrechen();
    }

    public void buttonClicked(int x, int y) {
        boolean result = serverConnection.zug(x, y);
        if (!result) {
            errorMsg("Zug ungültig!");
        }
    }

    // IClient
    @Override
    public void verbindungsFehler() {
        goToState(ClientState.LOGIN);
        errorMsg("Verbindung abgebrochen!");
    }

    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        goToState(ClientState.PLAYING_CHOMP);
        gameWindow.updateWindow((zustand == Zustand.ZUG), formatStatus(gegenspieler, zustand), spielfeld);
        askForClearance(zustand, gegenspieler);
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        goToState(ClientState.PLAYING_C4);
        gameWindow.updateWindow((zustand == Zustand.ZUG), formatStatus(gegenspieler, zustand), spielfeld);
        askForClearance(zustand, gegenspieler);
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel, boolean[] verfuegbar) {
        String[] listItems = new String[name.length];
        for (int i = 0; i < name.length; i++) {
            String s = name[i] + " ";
            switch (spiel[i]) {
                case KEINS:
                    s += "(kein Spiel)";
                    break;
                case CHOMP:
                    s += verfuegbar[i] ? "(möchte Chomp spielen)" : "(spielt gerade Chomp)";
                    break;
                case VIER_GEWINNT:
                    s += verfuegbar[i] ? "(möchte Vier Gewinnt spielen)" : "(spielt gerade Vier Gewinnt)";
                    break;
            }
            listItems[i] = s;
        }

        setPrivateMessagePartner(-1);

        lobbyWindow.updatePlayerList(listItems);

        connected_names = name;
        connected_games = spiel;
    }

    @Override
    public void nachricht(String name, String nachricht) {
        if (!name.equals(myName))
            lobbyWindow.addLineToMessagePane(String.format("[%12s] %s", name, nachricht));
    }

    @Override
    public void spielZuende() {
        goToState(ClientState.SESSION_EST);
    }

    private enum ClientState {
        NONE,
        LOGIN,
        SESSION_EST,
        PLAYING_CHOMP,
        PLAYING_C4
    }
}
