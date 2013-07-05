package uni.prakinf.m4.server.protokoll;

import uni.prakinf.m4.server.sitzung.Sitzung;

public class M4DecoderClientToServer {

    public static void decodiereNachricht(Sitzung sitzung, M4NachrichtEinfach nachrichtEinfach) {

        switch (nachrichtEinfach.getMethode()) {
            case CL_LOGIN:
                sitzung.login("", nachrichtEinfach.getSa(), nachrichtEinfach.getSb());
                break;
            case CL_MITSPIELEN:
                sitzung.mitspielen(nachrichtEinfach.getSa(), nachrichtEinfach.getSpiel());
                break;
            case CL_NEUESSPIEL:
                sitzung.neuesSpiel(nachrichtEinfach.getSpiel(), nachrichtEinfach.getIa(), nachrichtEinfach.getIb());
                break;
            case RET_CL_LOGIN:
            case RET_CL_MITSPIELEN:
            case RET_CL_NACHRICHT:
            case RET_CL_NEUESSPIEL:
            case RET_CL_ZUG:
                System.err.println("Server: Fehlerhafte Nachricht vom Client, ignoriert.");
                break;
        }

    }
}
