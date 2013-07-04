package uni.prakinf.m4.server.protokoll;

import uni.prakinf.m4.client.IClient;

import java.io.Serializable;

public class M4NachrichtEinfach extends M4Nachricht implements Serializable {
    public static final long serialVersionUID = 4000000001L;

    private String sa = null, sb = null;
    private int ia = 0, ib = 0;
    private IClient.Spiel spiel = IClient.Spiel.KEINS;
    private boolean b = false;
    private String ls[] = null;
    private IClient.Spiel lspiel[] = null;
    private Methode methode = Methode.NOT_SET;

    public M4NachrichtEinfach(Methode methode) {
        this.methode = methode;
    }

    public M4NachrichtEinfach(Methode methode, String sa, String sb, int ia, int ib, IClient.Spiel spiel, boolean b, String[] ls, IClient.Spiel[] lspiel) {
        this.methode = methode;
        this.sa = sa;
        this.sb = sb;
        this.ia = ia;
        this.ib = ib;
        this.spiel = spiel;
        this.b = b;
        this.ls = ls;
        this.lspiel = lspiel;
    }

    public String getSa() {
        return sa;
    }

    public void setSa(String sa) {
        this.sa = sa;
    }

    public String getSb() {
        return sb;
    }

    public void setSb(String sb) {
        this.sb = sb;
    }

    public int getIa() {
        return ia;
    }

    public void setIa(int ia) {
        this.ia = ia;
    }

    public int getIb() {
        return ib;
    }

    public void setIb(int ib) {
        this.ib = ib;
    }

    public IClient.Spiel getSpiel() {
        return spiel;
    }

    public void setSpiel(IClient.Spiel spiel) {
        this.spiel = spiel;
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public String[] getLs() {
        return ls;
    }

    public void setLs(String[] ls) {
        this.ls = ls;
    }

    public IClient.Spiel[] getLspiel() {
        return lspiel;
    }

    public void setLspiel(IClient.Spiel[] lspiel) {
        this.lspiel = lspiel;
    }

    public Methode getMethode() {
        return methode;
    }

    public void setMethode(Methode methode) {
        this.methode = methode;
    }

    public enum Methode {
        NOT_SET,
        CL_LOGIN,
        CL_NEUESSPIEL,
        CL_ZUG,
        CL_MITSPIELEN,
        CL_NACHRICHT,
        CL_ANTWORT_AUF_ANFRAGE,
        CL_ABBRECHEN,
        RET_CL_LOGIN,
        RET_CL_NEUESSPIEL,
        RET_CL_ZUG,
        RET_CL_MITSPIELEN,
        RET_CL_NACHRICHT,
        SRV_SPIELERLISTE,
        SRV_NACHRICHT
    }

}
