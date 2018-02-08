package network;


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
		while(listenForNewClients && !this.server.isClosed()) {
			try {
				client = this.server.accept();
			}catch(IOException e) {
				System.err.println("Can´t accept new Clients");
			}
			this.localDataServer.addNewClient(client);
		}
		return;
	}
	
	public void stopListen() {
		this.listenForNewClients = false;
		try {
			this.server.close();
		} catch (IOException e) {
			System.err.println("Can´t close ClientSocket at NewClientListener");
		}
	}

}
