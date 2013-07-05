package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.client.IClient;

public abstract class LaufendesSpiel {
    private Sitzung sitzungA;
    private Sitzung sitzungB;
    private Zustand zustand;

    LaufendesSpiel(Sitzung sitzungA) {
        this.sitzungA = sitzungA;
        this.sitzungB = null;
        zustand = Zustand.WARTE_AUF_ANNAHME;
    }

    public void los() {
        if (zustand == Zustand.WARTE_AUF_ANNAHME)
            benachrichtigeClients(sitzungA, Spieler.A);
        else
            System.err.printf("LaufendesSpiel: Falscher Zustand!\n");
    }

    public void zug(Sitzung sitzung, int x, int y) {
        if (sitzung == sitzungA) {
            if (zugGueltig(Spieler.A, x, y) && zustand == Zustand.SITZUNG_A_ZUG) {
                setzeZug(Spieler.A, x, y);
                weiter();
            }
        } else if (sitzung == sitzungB) {
            if (zugGueltig(Spieler.B, x, y) && zustand == Zustand.SITZUNG_B_ZUG) {
                setzeZug(Spieler.B, x, y);
                weiter();
            }
        } else {
            System.err.println("LaufendesSpiel: Fehlerhafte Sitzung übergeben!");
        }
    }

    private void weiter() {
        // Gewinnbedingung testen
        switch (zustand) {
            case SITZUNG_A_ZUG:
            case SITZUNG_B_ZUG:
                break;
        }
    }

    private void benachrichtigeAlle() {
        if (sitzungA != null)
            benachrichtigeClients(sitzungA, Spieler.A);
        if (sitzungB != null)
            benachrichtigeClients(sitzungB, Spieler.B);
    }

    // Für die anderen Spiele
    public abstract IClient.Spiel getSpiel();

    public abstract boolean spielZuende();

    public abstract void benachrichtigeClients(IClient client, Spieler spieler);

    public abstract boolean zugGueltig(Spieler spieler, int x, int y);

    public abstract void setzeZug(Spieler spieler, int x, int y);

    enum Zustand {
        WARTE_AUF_ZWEITE_SITZUNG,
        WARTE_AUF_ANNAHME,
        SITZUNG_A_ZUG,
        SITZUNG_B_ZUG,
        SITZUNG_A_GEWONNEN,
        SITZUNG_B_GEWONNEN,
        ENDE
    }

    enum Spieler {
        A, B
    }
}
