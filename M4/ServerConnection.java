import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection implements IServerConnection {
	private IClient client;
	private ObjectOutputStream conn_out;
	private ObjectInputStream conn_in;
	
	public void setClient(IClient client) {
		this.client = client;
	}
	
	public boolean login(String server, String name, String passwort) {
		return false;
	}
	
	public boolean zug(int x, int y) {
		return false;
	}
	
	public void abbrechen() {
	}
	
	public boolean neuesSpiel(IClient.Spiel spiel, int x, int y) {
		return false;
	}
	
	public boolean mitspielen(String name, IClient.Spiel spiel) {
		return false;
	}
	
	public boolean nachricht(String name, String nachricht) {
		return false;
	}
}
