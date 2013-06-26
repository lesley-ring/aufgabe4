public interface IServerConnection {
	/**
	 * An dieses IClient-Objekt werden die Statusnachrichten gesendet
	 */
	public void setClient(IClient client);
	
	/**
	 * Anmeldeversuch am Server "server" mit "name" und "passwort".
	 * @return True, falls der Versuch erfolgreich war. Sonst false.
	 */
	public boolean login(String server, String name, String passwort);
	
	/**
	 * Sendet einen Zug mit den Koordinaten x, y an den Server
	 * @return True, falls der Zug erlaubt ist. Sonst false.
	 */
	public boolean zug(int x, int y);
	
	/**
	 * Bricht das aktuelle Spiel ab.
	 */
	public void abbrechen();
	
	/**
	 * Faengt ein neues Spiel der Art "spiel" an. Die Spielfeldgroesse ist x * y.
	 * @return True, falls der Versuch erfolgreich war. Sonst false.
	 */
	public boolean neuesSpiel(IClient.Spiel spiel, int x, int y);
	
	/**
	 * Spielt bei einem Spiel "spiel" des Spielers "name" mit.
	 * @return True, falls der Versuch erfolgreich war. Sonst false.
	 */
	public boolean mitspielen(String name, IClient.Spiel spiel);
	
	/**
	 * Sendet eine Nachricht "nachricht" an den Spieler "name".
	 * @return True, falls die Nachricht zugestellt wurde. Sonst false.
	 */
	public boolean nachricht(String name, String nachricht);

}
