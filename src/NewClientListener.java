

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NewClientListener extends Thread{
	
	private LocalDataServer localDataServer;
	private ServerSocket server;
	private Socket client;
	
	private boolean listenForNewClients = true;

	public NewClientListener(LocalDataServer localDataServer, ServerSocket server) {
		this.localDataServer = localDataServer;
		this.server = server;
	}
	
	public void run() {
		while(listenForNewClients) {
			try {
				client = server.accept();
			}catch(IOException e) {
				e.printStackTrace();
			}
			this.localDataServer.addNewClient(client);
		}
		
	}
	
	public void stopListen() {
		this.listenForNewClients = false;
	}

}
