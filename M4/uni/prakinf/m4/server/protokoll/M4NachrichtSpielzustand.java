package uni.prakinf.m4.server.protokoll;

import uni.prakinf.m4.client.IClient;

import java.io.Serializable;

public class M4NachrichtSpielzustand implements Serializable {
    public static final long serialVersionUID = 4000000002L;

    private IClient.Zustand zustand;
    private IClient.Spiel spiel;
    private boolean spielfeldGueltig;

    private boolean spielfeldChomp[][];
    private IClient.VierGewinntStein spielfeldVierGewinnt[][];

    /**
     * Konstruktor fuer Chomp-Spielzustand
     * @param zustand Spielzustand
     * @param spielfeld Spielfeld
     */
    public M4NachrichtSpielzustand (IClient.Zustand zustand, boolean spielfeld[][]) {
        this.zustand = zustand;
        this.spiel = IClient.Spiel.CHOMP;
        this.spielfeldChomp = spielfeld;
        this.spielfeldVierGewinnt = null;
        this.spielfeldGueltig = (spielfeld != null);
    }

    /**
     * Konstruktor fuer Vier Gewinnt-Spielzustand
     * @param zustand Spielzustand
     * @param spielfeld Spielfeld
     */
    public M4NachrichtSpielzustand (IClient.Zustand zustand, IClient.VierGewinntStein spielfeld[][]) {
        this.zustand = zustand;
        this.spiel = IClient.Spiel.VIER_GEWINNT;
        this.spielfeldVierGewinnt = spielfeld;
        this.spielfeldChomp = null;
        this.spielfeldGueltig = (spielfeld != null);
    }

    /**
     * Konstruktor fuer Zustand ohne Spielfeld
     * @param zustand
     * @param spiel
     */
    public M4NachrichtSpielzustand (IClient.Zustand zustand, IClient.Spiel spiel) {
        this.zustand = zustand;
        this.spiel = spiel;
        this.spielfeldGueltig = false;
        this.spielfeldChomp = null;
        this.spielfeldVierGewinnt = null;
    }

    public IClient.Zustand getZustand() {
        return zustand;
    }

    public IClient.Spiel getSpiel() {
        return spiel;
    }

    public boolean isSpielfeldGueltig() {
        return spielfeldGueltig;
    }

    public boolean[][] getSpielfeldChomp() {
        return spielfeldChomp;
    }

    public IClient.VierGewinntStein[][] getSpielfeldVierGewinnt() {
        return spielfeldVierGewinnt;
    }
}
