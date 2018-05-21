package com.catangame.catan.desktop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NewServerClientListener extends Thread{
	private MainServer mainServer;
	private ServerSocket serverSocket;
	
	private boolean listenForNewClients = true;
	Socket clientSocket = null;
	
	public NewServerClientListener(MainServer mainServer, ServerSocket serverSocket) {
		this.mainServer = mainServer;
		this.serverSocket = serverSocket;
	}
	public void run() {
		while(listenForNewClients && this.serverSocket != null && !this.serverSocket.isClosed()) {
			try {
				this.clientSocket = this.serverSocket.accept();
			}catch(IOException e) {
				e.printStackTrace();
			}
			System.out.println("Added new Client");
			this.mainServer.addNewClient(clientSocket);
		}
		return;
	}
}
