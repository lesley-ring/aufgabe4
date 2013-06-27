package uni.prakinf.m4.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerListenerThread extends Thread {/*
        private Server server;
		private boolean running;
		private ServerSocket socket;
		
		public ServerListenerThread(Server server) throws IOException {
			this.server = server;
			socket = new ServerSocket(Server.DEFAULT_PORT);
			socket.setSoTimeout(1000);
		}
		
		public void startListening() {
			running = true;
			start();
		}
		
		public void stopListening() throws InterruptedException, IOException {
			socket.close();
			join();
		}
		
		public void run() {
			while(running) {
				try {
					Socket connected_client = socket.accept();
					server.clientConnected(connected_client);
				} catch (SocketTimeoutException tex) {
					continue;
				} catch (Exception ex) {
					running = false;
				}
			}
		} */
}
