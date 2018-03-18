package com.catangame.catan.server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.data.Resource;
import com.catangame.catan.network.Packet;

//TODO Rename Method "message"

public class ServerClientCommunicator extends Thread implements Serializable{
	
	private MainServer mainServer;
	private Socket client;
	private long publicID;
	private int clientGameID;
	private int gameID;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerGame serverGame;
	
	public ServerGame getServerGame() {
		return serverGame;
	}

	public void setServerGame(ServerGame serverGame) {
		this.serverGame = serverGame;
	}

	private boolean running = true;
	
	public ServerClientCommunicator(MainServer mainServer, Socket client) {
		this.mainServer = mainServer;
		this.client = client;
	}
	
	public void run() {
		try {
			this.output = new ObjectOutputStream(this.client.getOutputStream());
			this.input = new ObjectInputStream(this.client.getInputStream());
		}catch(IOException e) {
			System.err.println("Can't create input and output streams to client");
			e.printStackTrace();
		}catch (Exception e) {
			System.err.println("Can't initialize listener at ClientCommunicator");
		}
		
		//Listen for Input from Client
		while(this.running && output != null && input != null) {
			final Packet packet;
			try {
				packet = (Packet) input.readObject();
				if(serverGame == null) {
					mainServer.messageFromClient(this, packet);	
				}else {
					serverGame.messageFromClient(this, packet);	
				}
					
			}catch(IOException e) {
				System.err.println("End of File Exception at ClientCommunicator => stopped running");
				mainServer.removeClient(publicID);
				mainServer.removeGame(gameID);
				this.running = false;
			} catch (ClassNotFoundException e) {
				System.err.println("Object is from unknown Class");
				e.printStackTrace();
			}
		}
		return;
	}
	
	public void message(Packet p) {
		try {
			this.output.writeObject(p);
			this.output.flush();
			this.output.reset();
		}catch(IOException e) {
			System.err.println("Can't send Packet to Client with the Code:" + p.getCommand());
		}
		
	}
	
	public void stopRunning() {
		this.running = false;
		try {
			this.input.close();
			this.output.close();
		} catch (IOException e) {
			System.err.println("Can't close Listener at ClientCommunicator");
		}
	}


	public void setPublicID(long publicID) {
		this.publicID = publicID;
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public long getPublicID() {
		return publicID;
	}

	public int getClientGameID() {
		return clientGameID;
	}

	public void setClientGameID(int clientGameID) {
		this.clientGameID = clientGameID;
	}
}
