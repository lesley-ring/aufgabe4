package uni.prakinf.m4.server.protokoll;

import uni.prakinf.m4.client.IClient;

import java.io.Serializable;

public class M4NachrichtSpielzustand extends M4Nachricht implements Serializable {
    public static final long serialVersionUID = 4000000002L;

    private IClient.Zustand zustand;
    private IClient.Spiel spiel;
    private String gegenSpieler;
    private boolean spielfeldGueltig;

    private boolean spielfeldChomp[][];
    private IClient.VierGewinntStein spielfeldVierGewinnt[][];

    /**
     * Konstruktor fuer uni.prakinf.m4.client.Chomp-Spielzustand
     *
     * @param zustand      Spielzustand
     * @param gegenSpieler Name des Gegenspielers
     * @param spielfeld    Spielfeld
     */
    public M4NachrichtSpielzustand(IClient.Zustand zustand, String gegenSpieler, boolean spielfeld[][]) {
        this.zustand = zustand;
        this.gegenSpieler = gegenSpieler;
        this.spiel = IClient.Spiel.CHOMP;
        this.spielfeldChomp = spielfeld;
        this.spielfeldVierGewinnt = null;
        this.spielfeldGueltig = (spielfeld != null);
    }

    /**
     * Konstruktor fuer Vier Gewinnt-Spielzustand
     *
     * @param zustand      Spielzustand
     * @param gegenSpieler
     * @param spielfeld    Spielfeld
     */
    public M4NachrichtSpielzustand(IClient.Zustand zustand, String gegenSpieler, IClient.VierGewinntStein spielfeld[][]) {
        this.zustand = zustand;
        this.gegenSpieler = gegenSpieler;
        this.spiel = IClient.Spiel.VIER_GEWINNT;
        this.spielfeldVierGewinnt = spielfeld;
        this.spielfeldChomp = null;
        this.spielfeldGueltig = (spielfeld != null);
    }

    /**
     * Konstruktor fuer Zustand ohne Spielfeld
     *
     * @param zustand
     * @param spiel
     * @param gegenSpieler
     */
    public M4NachrichtSpielzustand(IClient.Zustand zustand, IClient.Spiel spiel, String gegenSpieler) {
        this.zustand = zustand;
        this.spiel = spiel;
        this.gegenSpieler = gegenSpieler;
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

    public String getGegenSpieler() {
        return gegenSpieler;
    }
}
