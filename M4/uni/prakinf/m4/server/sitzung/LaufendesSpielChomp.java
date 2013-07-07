package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;

public class LaufendesSpielChomp extends LaufendesSpiel {
    private Spieler gewinner;
    private boolean[][] spielfeld;
    private int sgx, sgy;

    public LaufendesSpielChomp(Sitzung sitzungA, int x, int y) {
        super(sitzungA);
        gewinner = null;
        spielfeld = new boolean[x][y];
        sgx = x;
        sgy = y;
    }

    private void spielfeldVorbereiten() {
        int xsize = spielfeld.length;
        int ysize = spielfeld[0].length;

        for (int x = 0; x < xsize; x++)
            for (int y = 0; y < ysize; y++) {
                spielfeld[x][y] = true;
            }
    }

    @Override
    public IClient.Spiel getSpiel() {
        return IClient.Spiel.CHOMP;
    }

    @Override
    public boolean spielZuende() {
        return true;
    }

    @Override
    public Spieler gewinner() {
        return null;
    }

    @Override
    public void benachrichtigeClients(IClient client, Spieler spieler) {
        switch (spieler) {
            case A:
                switch (zustand) {
                    case WARTE_AUF_ZWEITE_SITZUNG:
                        client.neuerZustandChomp(IClient.Zustand.KEIN_SPIELER, spielfeld, "");
                        break;
                    case WARTE_AUF_ANNAHME:
                        client.neuerZustandChomp(IClient.Zustand.ANFRAGE, spielfeld, getSpielerBName());
                        break;
                    case SITZUNG_A_ZUG:
                        client.neuerZustandChomp(IClient.Zustand.ZUG, spielfeld, getSpielerBName());
                        break;
                    case SITZUNG_B_ZUG:
                        client.neuerZustandChomp(IClient.Zustand.WARTEN, spielfeld, getSpielerBName());
                        break;
                    case SITZUNG_A_GEWONNEN:
                        client.neuerZustandChomp(IClient.Zustand.GEWONNEN, spielfeld, getSpielerBName());
                        break;
                    case SITZUNG_B_GEWONNEN:
                        client.neuerZustandChomp(IClient.Zustand.VERLOREN, spielfeld, getSpielerBName());
                        break;
                    case UNENTSCHIEDEN:
                        client.neuerZustandChomp(IClient.Zustand.UNENTSCHIEDEN, spielfeld, getSpielerBName());
                        break;
                }
                break;
            case B:
                switch (zustand) {
                    case SITZUNG_A_ZUG:
                        client.neuerZustandChomp(IClient.Zustand.WARTEN, spielfeld, getSpielerAName());
                        break;
                    case SITZUNG_B_ZUG:
                        client.neuerZustandChomp(IClient.Zustand.ZUG, spielfeld, getSpielerAName());
                        break;
                    case SITZUNG_A_GEWONNEN:
                        client.neuerZustandChomp(IClient.Zustand.VERLOREN, spielfeld, getSpielerAName());
                        break;
                    case SITZUNG_B_GEWONNEN:
                        client.neuerZustandChomp(IClient.Zustand.GEWONNEN, spielfeld, getSpielerAName());
                        break;
                    case UNENTSCHIEDEN:
                        client.neuerZustandChomp(IClient.Zustand.UNENTSCHIEDEN, spielfeld, getSpielerAName());
                        break;
                    case WARTE_AUF_ZWEITE_SITZUNG:
                        Logger.errln("LaufendesSpielChomp: Ungültiger Zustand!");
                        break;
                    case WARTE_AUF_ANNAHME:
                        // Keine Nachricht!
                        break;
                }
                break;
        }
    }

    @Override
    public boolean zugGueltig(Spieler spieler, int x, int y) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setzeZug(Spieler spieler, int x, int y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
