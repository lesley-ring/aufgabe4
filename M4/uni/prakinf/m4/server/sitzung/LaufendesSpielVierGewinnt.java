package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.server.Server;

public class LaufendesSpielVierGewinnt extends LaufendesSpiel {
    private LaufendesSpiel.Spieler gewinner;
    private Spieler[][] spielfeld;
    private int sgx, sgy;

    public LaufendesSpielVierGewinnt(Sitzung sitzungA, Server server, int x, int y) {
        super(sitzungA, server);
        gewinner = null;
        spielfeld = new Spieler[x][y];
        sgx = x;
        sgy = y;
        spielfeldVorbereiten();
    }

    private void spielfeldVorbereiten() {
        int xsize = spielfeld.length;
        int ysize = spielfeld[0].length;

        for (int x = 0; x < xsize; x++)
            for (int y = 0; y < ysize; y++) {
                spielfeld[x][y] = null;
            }
    }

    @Override
    public IClient.Spiel getSpiel() {
        return IClient.Spiel.VIER_GEWINNT;
    }

    @Override
    public boolean spielZuende() {
        if (hatSpielerGewonnen(Spieler.A)) {
            gewinner = Spieler.A;
            return true;
        } else if (hatSpielerGewonnen(Spieler.B)) {
            gewinner = Spieler.B;
            return true;
        } else if (unentschieden()) {
            gewinner = null;
            return true;
        }
        return false;
    }

    private boolean unentschieden() {
        for (int x = 0; x < sgx; x++)
            for (int y = 0; y < sgy; y++)
                if (spielfeld[x][y] == null)
                    return false;
        return true;
    }

    private boolean hatSpielerGewonnen(Spieler spieler) {
        // Vollständige Reihen
        for (int y = 0; y < sgy; y++) {
            int xcount = 0;
            for (int x = 0; x < sgx; x++) {
                if (spielfeld[x][y] == spieler) {
                    xcount++;
                    if (xcount == 4)
                        return true;
                } else {
                    xcount = 0;
                }
            }
        }

        // Vollständige Spalten
        for (int x = 0; x < sgx; x++) {
            int ycount = 0;
            for (int y = 0; y < sgy; y++) {
                if (spielfeld[x][y] == spieler) {
                    ycount++;
                    if (ycount == 4)
                        return true;
                } else {
                    ycount = 0;
                }
            }
        }

        // Diagonalen X+Y+
        for (int ax = 0 - (sgy - 1); ax < sgx; ax++) {
            int acount = 0;
            for (int y = 0; y < sgy; y++) {
                int rx = ax + y;

                if (rx >= 0 && rx < sgx && y >= 0 && y < sgy) {
                    if (spielfeld[rx][y] == spieler) {
                        acount++;
                        if (acount == 4)
                            return true;
                    }
                } else {
                    acount = 0;
                }

            }
        }

        // Diagonalen X+Y-
        for (int ax = 0 - (sgy - 1); ax < (sgx + (sgy - 1)); ax++) {
            int acount = 0;
            for (int y = 0; y < sgy; y++) {
                int rx = ax + y;
                int ry = (sgy - 1) - y;

                if (rx >= 0 && rx < sgx && ry >= 0 && ry < sgy) {
                    if (spielfeld[rx][ry] == spieler) {
                        acount++;
                        if (acount == 4)
                            return true;
                    }
                } else {
                    acount = 0;
                }

            }
        }

        return false;
    }

    @Override
    public LaufendesSpiel.Spieler gewinner() {
        return gewinner;
    }

    @Override
    public void benachrichtigeClients(IClient client, LaufendesSpiel.Spieler spieler) {
        IClient.VierGewinntStein[][] spielfeld_a = new IClient.VierGewinntStein[sgx][sgy];
        IClient.VierGewinntStein[][] spielfeld_b = new IClient.VierGewinntStein[sgx][sgy];
        for (int x = 0; x < sgx; x++) {
            for (int y = 0; y < sgy; y++) {
                if (spielfeld[x][y] == null) {
                    spielfeld_a[x][y] = IClient.VierGewinntStein.LEER;
                    spielfeld_b[x][y] = IClient.VierGewinntStein.LEER;
                } else if (spielfeld[x][y] == Spieler.A) {
                    spielfeld_a[x][y] = IClient.VierGewinntStein.SPIELER;
                    spielfeld_b[x][y] = IClient.VierGewinntStein.GEGNER;
                } else if (spielfeld[x][y] == Spieler.B) {
                    spielfeld_a[x][y] = IClient.VierGewinntStein.GEGNER;
                    spielfeld_b[x][y] = IClient.VierGewinntStein.SPIELER;
                }
            }
        }
        switch (spieler) {
            case A:
                switch (zustand) {
                    case WARTE_AUF_ZWEITE_SITZUNG:
                        client.neuerZustandVierGewinnt(IClient.Zustand.KEIN_SPIELER, spielfeld_a, "");
                        break;
                    case WARTE_AUF_ANNAHME:
                        client.neuerZustandVierGewinnt(IClient.Zustand.ANFRAGE, spielfeld_a, getSpielerBName());
                        break;
                    case SITZUNG_A_ZUG:
                        client.neuerZustandVierGewinnt(IClient.Zustand.ZUG, spielfeld_a, getSpielerBName());
                        break;
                    case SITZUNG_B_ZUG:
                        client.neuerZustandVierGewinnt(IClient.Zustand.WARTEN, spielfeld_a, getSpielerBName());
                        break;
                    case SITZUNG_A_GEWONNEN:
                        client.neuerZustandVierGewinnt(IClient.Zustand.GEWONNEN, spielfeld_a, getSpielerBName());
                        break;
                    case SITZUNG_B_GEWONNEN:
                        client.neuerZustandVierGewinnt(IClient.Zustand.VERLOREN, spielfeld_a, getSpielerBName());
                        break;
                    case UNENTSCHIEDEN:
                        client.neuerZustandVierGewinnt(IClient.Zustand.UNENTSCHIEDEN, spielfeld_a, getSpielerBName());
                        break;
                    case ABBRUCH:
                        client.neuerZustandVierGewinnt(IClient.Zustand.ABBRUCH, spielfeld_a, getSpielerBName());
                        break;
                }
                break;
            case B:
                switch (zustand) {
                    case SITZUNG_A_ZUG:
                        client.neuerZustandVierGewinnt(IClient.Zustand.WARTEN, spielfeld_b, getSpielerAName());
                        break;
                    case SITZUNG_B_ZUG:
                        client.neuerZustandVierGewinnt(IClient.Zustand.ZUG, spielfeld_b, getSpielerAName());
                        break;
                    case SITZUNG_A_GEWONNEN:
                        client.neuerZustandVierGewinnt(IClient.Zustand.VERLOREN, spielfeld_b, getSpielerAName());
                        break;
                    case SITZUNG_B_GEWONNEN:
                        client.neuerZustandVierGewinnt(IClient.Zustand.GEWONNEN, spielfeld_b, getSpielerAName());
                        break;
                    case UNENTSCHIEDEN:
                        client.neuerZustandVierGewinnt(IClient.Zustand.UNENTSCHIEDEN, spielfeld_b, getSpielerAName());
                        break;
                    case WARTE_AUF_ZWEITE_SITZUNG:
                        Logger.errln("LaufendesSpielVierGewinnt: Ungültiger Zustand!");
                        break;
                    case WARTE_AUF_ANNAHME:
                        // Keine Nachricht!
                        break;
                    case ABBRUCH:
                        client.neuerZustandVierGewinnt(IClient.Zustand.ABBRUCH, spielfeld_b, getSpielerAName());
                        break;
                }
                break;
        }
    }

    @Override
    public boolean zugGueltig(LaufendesSpiel.Spieler spieler, int x, int y) {
        return y == 0 && spielfeld[x][y] == null;
    }

    @Override
    public void setzeZug(LaufendesSpiel.Spieler spieler, int x, int y) {
        for (int zy = (sgy - 1); zy >= 0; zy--) {
            if (spielfeld[x][zy] == null) {
                spielfeld[x][zy] = spieler;
                return;
            }
        }
        Logger.errln("LaufendesSpielVierGewinnt: Zug falsch!!!");
    }
}
