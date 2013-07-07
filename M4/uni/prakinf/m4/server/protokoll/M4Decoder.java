package uni.prakinf.m4.server.protokoll;

import uni.prakinf.m4.Logger;
import uni.prakinf.m4.client.IClient;
import uni.prakinf.m4.server.sitzung.Sitzung;

public class M4Decoder {

    public static void decodiereClientNachricht(Sitzung sitzung, M4NachrichtEinfach nachrichtEinfach) {
        if (sitzung == null) {
            Logger.errln("Server: Sitzung ist null im Decoder.");
            return;
        }
        switch (nachrichtEinfach.getMethode()) {
            case CL_LOGIN:
                sitzung.login(nachrichtEinfach.getSa(), nachrichtEinfach.getSb());
                break;
            case CL_MITSPIELEN:
                sitzung.mitspielen(nachrichtEinfach.getSa(), nachrichtEinfach.getSpiel());
                break;
            case CL_NEUESSPIEL:
                sitzung.neuesSpiel(nachrichtEinfach.getSpiel(), nachrichtEinfach.getIa(), nachrichtEinfach.getIb());
                break;
            case CL_ZUG:
                sitzung.zug(nachrichtEinfach.getIa(), nachrichtEinfach.getIb());
                break;
            case CL_TRENNEN:
                sitzung.verbindungTrennen();
                break;
            case CL_ABBRECHEN:
                sitzung.abbrechen();
                break;
            case CL_ANTWORT_AUF_ANFRAGE:
                sitzung.antwortAufAnfrage(nachrichtEinfach.isB());
                break;
            case CL_NACHRICHT:
                sitzung.client_nachricht(nachrichtEinfach.getSa(), nachrichtEinfach.getSb());
                break;
            case RET_CL_LOGIN:
            case RET_CL_MITSPIELEN:
            case RET_CL_NACHRICHT:
            case RET_CL_NEUESSPIEL:
            case RET_CL_ZUG:
            case SRV_SPIELERLISTE:
            case SRV_NACHRICHT:
                Logger.errln("Server: Fehlerhafte Nachricht vom Client, ignoriert.");
                break;
            case NOT_SET:
                break;
        }

    }

    public static void decodiereServerNachricht(IClient client, M4NachrichtEinfach nachrichtEinfach) {
        if (client == null) {
            Logger.errln("Client: Client ist null im Decoder.");
            return;
        }
        switch (nachrichtEinfach.getMethode()) {
            case SRV_NACHRICHT:
                client.nachricht(nachrichtEinfach.getSa(), nachrichtEinfach.getSb());
                break;
            case SRV_SPIELERLISTE:
                client.spielerListe(nachrichtEinfach.getLs(), nachrichtEinfach.getLspiel());
                break;
            case SRV_SPIEL_ENDE:
                client.spielZuende();
                break;
            case RET_CL_LOGIN:
            case RET_CL_MITSPIELEN:
            case RET_CL_NACHRICHT:
            case RET_CL_NEUESSPIEL:
            case RET_CL_ZUG:
                Logger.errln("M4Decoder: RET-Nachricht nicht abgefangen!");
                break;
        }
    }

    public static void decodiereSpielzustand(IClient client, M4NachrichtSpielzustand nachrichtSpielzustand) {
        if (client == null) {
            Logger.errln("Client: Client ist null im Decoder (Spielzustand!!).");
            return;
        }

        switch (nachrichtSpielzustand.getSpiel()) {
            case CHOMP:
                /*System.out.println("Client: Spielfeld:");
                System.out.printf("Client: %b %b %b\n", nachrichtSpielzustand.getSpielfeldChomp()[0][1], nachrichtSpielzustand.getSpielfeldChomp()[0][0], nachrichtSpielzustand.getSpielfeldChomp()[0][2]);
                System.out.printf("Client: %b %b %b\n", nachrichtSpielzustand.getSpielfeldChomp()[1][1], nachrichtSpielzustand.getSpielfeldChomp()[1][0], nachrichtSpielzustand.getSpielfeldChomp()[1][2]);
                System.out.printf("Client: %b %b %b\n", nachrichtSpielzustand.getSpielfeldChomp()[2][1], nachrichtSpielzustand.getSpielfeldChomp()[2][0], nachrichtSpielzustand.getSpielfeldChomp()[2][2]);*/
                client.neuerZustandChomp(nachrichtSpielzustand.getZustand(), nachrichtSpielzustand.getSpielfeldChomp(), nachrichtSpielzustand.getGegenSpieler());
                break;
            case VIER_GEWINNT:
                client.neuerZustandVierGewinnt(nachrichtSpielzustand.getZustand(), nachrichtSpielzustand.getSpielfeldVierGewinnt(), nachrichtSpielzustand.getGegenSpieler());
                break;
            case KEINS:
                Logger.errln("Client: Spielzustand ungültig.");
                break;
        }
    }
}
