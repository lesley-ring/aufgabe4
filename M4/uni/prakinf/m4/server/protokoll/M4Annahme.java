package uni.prakinf.m4.server.protokoll;

public interface M4Annahme {
    public void verarbeiteNachricht(Object userObject, M4NachrichtEinfach nachrichtEinfach);

    public void verarbeiteNachricht(Object userObject, M4NachrichtSpielzustand spielzustand);

    public void verbindungsFehler(Object userObject, Exception exception);
}
