package uni.prakinf.m4.server.protokoll;

import uni.prakinf.m4.server.sitzung.Sitzung;

public class M4DecoderClientToServer {

    public static void decodiereNachricht(Sitzung sitzung, M4NachrichtEinfach nachrichtEinfach) {

        switch (nachrichtEinfach.getMethode()) {
            case CL_LOGIN:
                boolean login_result = sitzung.login("", nachrichtEinfach.getSa(), nachrichtEinfach.getSb());
                M4NachrichtEinfach nr_login_res = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.RET_CL_LOGIN);
                nr_login_res.setB(login_result);
                sitzung.sendeNachrichtAsync(nr_login_res);
                break;
            case CL_MITSPIELEN:
                boolean mitspielen_result = sitzung.mitspielen(nachrichtEinfach.getSa(), nachrichtEinfach.getSpiel());
                M4NachrichtEinfach nr_mitspielen_res = new M4NachrichtEinfach(M4NachrichtEinfach.Methode.RET_CL_MITSPIELEN);
                nr_mitspielen_res.setB(mitspielen_result);
                sitzung.sendeNachrichtAsync(nr_mitspielen_res);
                break;
            case CL_NEUESSPIEL:
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
