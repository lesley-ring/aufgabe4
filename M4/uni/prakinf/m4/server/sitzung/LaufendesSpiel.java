package uni.prakinf.m4.server.sitzung;

import uni.prakinf.m4.client.IClient;

public abstract class LaufendesSpiel {
    private Sitzung sitzung;

    // Für die anderen Spiele
    public abstract IClient.Spiel getSpiel();

    public abstract boolean spielZuende();
}
