package uni.prakinf.m2;

public class Main {

	public static void main(String[] args) {
		if (args.length < 1)
			return;
		if (args[0].equals("server")) {
			startServer();
		} else {
			startClient();
		}

	}

	public static void startServer() {
		new Server().start();
	}

	public static void startClient() {
		new Client().start();
	}
}
