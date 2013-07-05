package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.client.IClient;

public class LaufendesSpielChomp extends LaufendesSpiel {
    private Spieler gewinner;
    private boolean[][] spielfeld;

    public LaufendesSpielChomp(Sitzung sitzungA, int x, int y) {
        super(sitzungA);
        gewinner = null;
        spielfeld = new boolean[x][y];
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean spielZuende() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Spieler gewinner() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
                }
                break;
            case B:
                break;
        }
    }

    @Override
    public boolean zugGueltig(Spieler spieler, int x, int y) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setzeZug(Spieler spieler, int x, int y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
