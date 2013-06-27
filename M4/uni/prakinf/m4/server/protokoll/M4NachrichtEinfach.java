package uni.prakinf.m4.server.protokoll;

import java.io.Serializable;

public class M4NachrichtEinfach implements Serializable {
    public static final long serialVersionUID = 4000000001L;

    private boolean erfolg;
    private String name;
    private String passwort;
    private String nachricht;
    private Art art;

    /**
     * Konstruktor einer einfachen Nachricht.
     *
     * @param art       Die Art der Nachricht
     * @param erfolg    Ob die Operation erfolgreich war (optional)
     * @param name      Der Name des beteiligten Benutzers (optional)
     * @param passwort  Das Passwort im Fall einer Anmeldung (optional)
     * @param nachricht Eine Nachricht als Zeichenkette (optional)
     */
    public M4NachrichtEinfach(Art art, boolean erfolg, String name, String passwort, String nachricht) {
        this.erfolg = erfolg;
        this.name = name;
        this.passwort = passwort;
        this.nachricht = nachricht;
        this.art = art;
    }

    public boolean isErfolg() {
        return erfolg;
    }

    public String getName() {
        return name;
    }

    public String getPasswort() {
        return passwort;
    }

    public String getNachricht() {
        return nachricht;
    }

    public Art getArt() {
        return art;
    }

    public enum Art {
        // Client -> Server
        CS_LOGIN,
        CS_LEAVE_GAME,

        // Server <- Client
        SC_LOGIN,
        SC_LEAVE_GAME
    }

}
