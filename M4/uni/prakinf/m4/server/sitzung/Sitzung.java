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
        sendeNachrichtAsync(new M4NachrichtEinfach(M4NachrichtEinfach.Methode.SRV_SPIEL_ENDE));
    }

    public void sitzungVerlassen() {
        try {
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

            if (result)
                server.sitzungenVerteilen();
        } else
            sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_LOGIN, false);

    }

    public void antwortAufAnfrage(boolean ok) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean zug(int x, int y) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void abbrechen() {
        if (sitzungszustand == Sitzungszustand.SPIELT) {
            Logger.errln("Sitzung: Spielabbruch gefordert.");
            if (spiel != null)
                spiel.beenden();
            spiel = null;
            sitzungszustand = Sitzungszustand.ANGEMELDET;
            spielTyp = Spiel.KEINS;
        } else {
            Logger.errln("Sitzung: Abbruch nicht erlaubt!");
        }
    }

    public void neuesSpiel(Spiel spielart, int x, int y) {
        if (sitzungszustand != Sitzungszustand.ANGEMELDET)
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
                sendeErgebnisAsync(M4NachrichtEinfach.Methode.RET_CL_NEUESSPIEL, false);
                break;
        }
    }

    public void mitspielen(String name, Spiel spiel) {

    }

    public void verbindungTrennen() {
        sitzungVerlassen();
        if (sitzungszustand == Sitzungszustand.SPIELT)
            abbrechen();
    }

    // IClient Methoden - Aufruf: Nachricht weiterleiten an Client
    @Override
    public void neuerZustandChomp(Zustand zustand, boolean[][] spielfeld, String gegenspieler) {
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
        Sitzung ziel = server.findeSitzung(name);
        boolean zugestellt = false;
        if (ziel != null) {
            try {
                ziel.nachricht(name, nachricht);
                zugestellt = true;
            } catch (Exception e) {
                Logger.errln("Sitzung: Nachricht konnte nicht zugestellt werden.");
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
