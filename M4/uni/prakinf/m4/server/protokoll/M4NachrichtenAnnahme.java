package uni.prakinf.m4.server.protokoll;

public interface M4NachrichtenAnnahme {
    public void verarbeiteNachricht(M4NachrichtEinfach nachrichtEinfach);
    public void verarbeiteNachricht(M4NachrichtSpielzustand spielzustand);
}
