public interface IServerConnection {
  public void setClient(IClient client);
	
	public boolean login(String server, String name, String passwort);
	
	public boolean zug(int x, int y);
	
	public void abbrechen();
	
	public boolean neuesSpiel(IClient.Spiel spiel, int x, int y);
	
	public boolean mitspielen(String name, IClient.Spiel spiel);
	
	public boolean nachricht(String name, String nachricht);

}
