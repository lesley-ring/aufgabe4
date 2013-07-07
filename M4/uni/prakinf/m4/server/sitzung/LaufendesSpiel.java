package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.server.protokoll.M4NachrichtEinfach;

public abstract class LaufendesSpiel {
    private Sitzung sitzungA;
    private Sitzung sitzungB;

    Zustand zustand;

    LaufendesSpiel(Sitzung sitzungA) {
        this.sitzungA = sitzungA;
        this.sitzungB = null;
        zustand = Zustand.WARTE_AUF_ZWEITE_SITZUNG;
    }

    public String getSpielerAName() {
        if (sitzungA != null)
            return sitzungA.getSitzungName();
        return "";
    }

    public String getSpielerBName() {
        if (sitzungB != null)
            return sitzungB.getSitzungName();
        return "";
    }

    public void los() {
        if (zustand == Zustand.WARTE_AUF_ZWEITE_SITZUNG)
            benachrichtigeClients(sitzungA, Spieler.A);
        else
            Logger.errln("LaufendesSpiel: Falscher Zustand!");
    }

    public boolean istFrei() {
        return zustand == Zustand.WARTE_AUF_ZWEITE_SITZUNG;
    }

    public void zweiteSitzung(Sitzung sitzungB) {
        // TODO Timer?
        if (zustand == Zustand.WARTE_AUF_ZWEITE_SITZUNG) {
            this.sitzungB = sitzungB;
            zustand = Zustand.WARTE_AUF_ANNAHME;
            benachrichtigeAlle();
        } else {
            sitzungB.sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_MITSPIELEN, false);
            Logger.errln("LaufendesSpiel: Falscher Zustand!");
        }

    }

    public void antwortAufAnfrage(Sitzung sitzung, boolean antwort) {
        if (sitzung == sitzungA && zustand == Zustand.WARTE_AUF_ANNAHME) {
            sitzungB.sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_MITSPIELEN, antwort);
            if (antwort) {
                zustand = Zustand.SITZUNG_A_ZUG;
                benachrichtigeAlle();
            } else {
                sitzungB = null;
                zustand = Zustand.WARTE_AUF_ZWEITE_SITZUNG;
                los();
            }

        } else {
            Logger.errln("LaufendesSpiel: Antwort ohne Anfrage");
        }
    }

    public void zug(Sitzung sitzung, int x, int y) {
        if (sitzung == sitzungA) {
            if (zugGueltig(Spieler.A, x, y) && zustand == Zustand.SITZUNG_A_ZUG) {
                sitzungA.sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_ZUG, true);
                setzeZug(Spieler.A, x, y);
                weiter();
            } else {
                sitzungA.sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_ZUG, false);
            }
        } else if (sitzung == sitzungB) {
            if (zugGueltig(Spieler.B, x, y) && zustand == Zustand.SITZUNG_B_ZUG) {
                sitzungB.sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_ZUG, true);
                setzeZug(Spieler.B, x, y);
                weiter();
            } else {
                sitzungB.sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_ZUG, false);
            }
        } else {
            if (sitzung != null)
                sitzung.sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_ZUG, false);
            Logger.errln("LaufendesSpiel: Fehlerhafte Sitzung übergeben!");
        }
    }

    private void weiter() {
        // Gewinnbedingung testen
        switch (zustand) {
            case SITZUNG_A_ZUG:
            case SITZUNG_B_ZUG:
                if (spielZuende()) {
                    Spieler gewinner = gewinner();
                    if (gewinner != null) {
                        switch (gewinner()) {
                            case A:
                                zustand = Zustand.SITZUNG_A_GEWONNEN;
                                break;
                            case B:
                                zustand = Zustand.SITZUNG_B_GEWONNEN;
                                break;

                        }
                    } else {
                        zustand = Zustand.UNENTSCHIEDEN;
                    }
                    benachrichtigeAlle();

                    try {
                        Thread.sleep(4000);
                    } catch (Exception e) {

                    }
                    beenden();
                    return;
                }
                break;
        }

        // Zugweitergabe testen
        switch (zustand) {
            case SITZUNG_A_ZUG:
                zustand = Zustand.SITZUNG_B_ZUG;
                benachrichtigeAlle();
                break;

            case SITZUNG_B_ZUG:
                zustand = Zustand.SITZUNG_A_ZUG;
                benachrichtigeAlle();
                break;
        }
    }

    public void beenden() {
        if (sitzungA != null)
            sitzungA.spielZuende();
        if (sitzungB != null)
            sitzungB.spielZuende();
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

    public abstract Spieler gewinner();

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
        UNENTSCHIEDEN
    }

    enum Spieler {
        A, B
    }
}
