package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.server.PasswortVerwaltung;
import uni.prakinf.m4.server.Server;
import uni.prakinf.m4.server.protokoll.M4Nachricht;
import uni.prakinf.m4.server.protokoll.M4NachrichtEinfach;
import uni.prakinf.m4.server.protokoll.M4NachrichtSpielzustand;
import uni.prakinf.m4.server.protokoll.M4TransportThread;

public class Sitzung implements IClient {
    private Server server;
    private M4TransportThread thread;
    private Sitzungszustand sitzungszustand;
    private LaufendesSpiel spiel;
    private Spiel spielTyp;
    private String sitzungName;

    public Sitzung(Server server, M4TransportThread thread) {
        this.server = server;
        this.thread = thread;
        sitzungszustand = Sitzungszustand.VERBUNDEN;
        sitzungName = "";
        spiel = null;
        spielTyp = Spiel.KEINS;
    }

    public Spiel getSpielTyp() {
        return spielTyp;
    }

    public LaufendesSpiel getSpiel() {
        return spiel;
    }

    public M4TransportThread getThread() {
        return thread;
    }

    public void sendeNachrichtAsync(M4Nachricht nachricht) {
        if (thread != null)
            thread.sendeNachrichtAsync(nachricht);
    }

    public void sendeErgebnisAsync(M4NachrichtEinfach.Methode methode, boolean erfolg) {
        M4NachrichtEinfach nachrichtEinfach = new M4NachrichtEinfach(methode);
        nachrichtEinfach.setB(erfolg);
        thread.sendeNachrichtAsync(nachrichtEinfach);
    }

    @Override
    public void spielZuende() {
        if (sitzungszustand == Sitzungszustand.SPIELT) {
            spiel = null;
            spielTyp = Spiel.KEINS;
            sitzungszustand = Sitzungszustand.ANGEMELDET;

            server.sitzungenVerteilen();
        }
        sendeNachrichtAsync(new M4NachrichtEinfach(M4NachrichtEinfach.Methode.SRV_SPIEL_ENDE));
    }

    public void sitzungVerlassen() {
        try {
            if (sitzungszustand == Sitzungszustand.SPIELT) {
                abbrechen();
            }
            sitzungszustand = Sitzungszustand.VERBUNDEN;
            server.entferne(this, getThread());
            thread.abbruch();
        } catch (Exception ex) {

        }
    }

    // IServerConnection Methoden - Aufruf von außen durch Decoder im Server!
    public void login(String name, String passwort) {
        if (sitzungszustand == Sitzungszustand.VERBUNDEN) {
            boolean result = PasswortVerwaltung.passwortGueltig(name, passwort) && !server.istAngemeldet(name);
            if (result) {
                sitzungszustand = Sitzungszustand.ANGEMELDET;
                sitzungName = name;
            }
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_LOGIN, result);

            if (result) {
                server.sitzungenVerteilen();
                nachricht("Server", "Willkommen auf dem Server!");
            }
        } else
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_LOGIN, false);

    }

    public void antwortAufAnfrage(boolean ok) {
        if (sitzungszustand == Sitzungszustand.SPIELT) {
            spiel.antwortAufAnfrage(this, ok);
        } else {
            Logger.errln("Sitzung: Antwort ohne Anfrage!");
        }
    }

    public void zug(int x, int y) {
        if (sitzungszustand == Sitzungszustand.SPIELT) {
            spiel.zug(this, x, y);
        } else {
            Logger.errln("Sitzung: Zug nicht erlaubt, kein Spiel!");
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_ZUG, false);
        }
    }

    public void abbrechen() {
        if (sitzungszustand == Sitzungszustand.SPIELT) {
            Logger.errln("Sitzung: Spielabbruch gefordert.");
            if (spiel != null)
                spiel.beenden();
            spiel = null;
            sitzungszustand = Sitzungszustand.ANGEMELDET;
            spielTyp = Spiel.KEINS;

            server.sitzungenVerteilen();
        } else {
            Logger.errln("Sitzung: Abbruch nicht erlaubt!");
        }
    }

    public void neuesSpiel(Spiel spielart, int x, int y) {
        if (sitzungszustand != Sitzungszustand.ANGEMELDET || (x < 1) || (y < 1) || ((x*y) < 2) )
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_NEUESSPIEL, false);
        switch (spielart) {
            case CHOMP:
                spiel = new LaufendesSpielChomp(this, x, y);
                sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_NEUESSPIEL, true);
                sitzungszustand = Sitzungszustand.SPIELT;
                spielTyp = Spiel.CHOMP;
                spiel.los();
                break;
            case VIER_GEWINNT:
                spiel = new LaufendesSpielVierGewinnt(this, x, y);
                sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_NEUESSPIEL, true);
                sitzungszustand = Sitzungszustand.SPIELT;
                spielTyp = Spiel.VIER_GEWINNT;
                spiel.los();
                break;
        }
        server.sitzungenVerteilen();
    }

    public void mitspielen(String name, Spiel spiel) {
        if (sitzungszustand != Sitzungszustand.ANGEMELDET) {
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_MITSPIELEN, false);
            return;
        }

        Sitzung sitzungA = server.findeSitzung(name);
        if (sitzungA != null && sitzungA.getSpielTyp() == spiel && !name.equals(getSitzungName()) && sitzungA.getSpiel().istFrei()) {
            // Richtige Sitzung, richtiges Spiel
            sitzungszustand = Sitzungszustand.SPIELT;
            this.spielTyp = spiel;
            this.spiel = sitzungA.getSpiel();
            server.sitzungenVerteilen();

            this.spiel.zweiteSitzung(this);
        } else {
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_MITSPIELEN, false);
        }
    }

    public void verbindungTrennen() {
        sitzungVerlassen();
        if (sitzungszustand == Sitzungszustand.SPIELT)
            abbrechen();
    }

    // IClient Methoden - Aufruf: Nachricht weiterleiten an Client
    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
        /*System.out.println("Server: Spielfeld:");
        System.out.printf("Server: %b %b %b\n", spielfeld[0][1], spielfeld[0][0], spielfeld[0][2]);
        System.out.printf("Server: %b %b %b\n", spielfeld[1][1], spielfeld[1][0], spielfeld[1][2]);
        System.out.printf("Server: %b %b %b\n", spielfeld[2][1], spielfeld[2][0], spielfeld[2][2]);*/
        M4NachrichtSpielzustand m4z = new M4NachrichtSpielzustand(zustand, gegenspieler, spielfeld);
        sendeNachrichtAsync(m4z);
    }

    @Override
    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein[][] spielfeld, String gegenspieler) {
        M4NachrichtSpielzustand m4z = new M4NachrichtSpielzustand(zustand, gegenspieler, spielfeld);
        sendeNachrichtAsync(m4z);
    }

    @Override
    public void spielerListe(String[] name, Spiel[] spiel) {
        M4NachrichtEinfach liste_nr = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.SRV_SPIELERLISTE);
        liste_nr.setLs(name);
        liste_nr.setLspiel(spiel);
        sendeNachrichtAsync(liste_nr);
    }

    @Override
    public void nachricht(String name, String nachricht) {
        // Nachricht von Server an Client
        M4NachrichtEinfach nr = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.SRV_NACHRICHT);
        nr.setSa(name);
        nr.setSb(nachricht);
        sendeNachrichtAsync(nr);
    }

    public void client_nachricht(String name, String nachricht) {
        // Nachricht von Client an Server
        boolean zugestellt = false;
        if (name.isEmpty()) {
            try {
                server.nachrichtAnAlle(getSitzungName(), nachricht);
                zugestellt = true;
            } catch (Exception e) {
                Logger.errln("Sitzung: Massennachricht konnte nicht zugestellt werden.");
            }
        } else {
            Sitzung ziel = server.findeSitzung(name);

            if (ziel != null) {
                try {
                    ziel.nachricht(getSitzungName(), nachricht);
                    zugestellt = true;
                } catch (Exception e) {
                    Logger.errln("Sitzung: Nachricht konnte nicht zugestellt werden.");
                }

            }

        }
        sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_NACHRICHT, zugestellt);
    }

    // Nie aufgerufene Methoden
    public void setClient(IClient client) {
        // Nie aufgerufen!
    }

    @Override
    public void verbindungsFehler() {
        // Nie aufgerufen!
    }

    public String getSitzungName() {
        return sitzungName;
    }

    private enum Sitzungszustand {
        VERBUNDEN,
        ANGEMELDET,
        SPIELT
    }
}
