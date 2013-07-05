package uni.prakinf.m4.client;

public interface IClient {
    public void verbindungsFehler();

    public void neuerZustandChomp(Zustand zustand, boolean spielfeld[][], String gegenspieler);
    // Ich habe die methode geändert.
    // Wir brauchen doch das ganze Spielfeld!

    public void neuerZustandVierGewinnt(Zustand zustand, VierGewinntStein spielfeld[][], String gegenspieler);

    public void spielerListe(String[] name, Spiel[] spiel);

    public void nachricht(String name, String nachricht);

    public void spielZuende();

    public enum Zustand {
        KEIN_SPIELER,   // Warte auf Gegenspieler
        ANFRAGE,        // Gegenspieler gefunden, spielen?
        ZUG,
        WARTEN,
        GEWONNEN,
        VERLOREN,
        ABBRUCH,
        UNENTSCHIEDEN
    }

    public enum VierGewinntStein {
        LEER,
        GEGNER,
        SPIELER
    }

    public enum Spiel {
        KEINS,
        CHOMP,
        VIER_GEWINNT
    }
}
