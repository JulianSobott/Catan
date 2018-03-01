package com.catangame.catan.network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.data.Resource;

//TODO Rename Method "message"

public class ClientCommunicator extends Thread{
	
	private Server localDataServer;
	private Socket client;
	private int id; 
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private boolean running = true;
	
	public ClientCommunicator(Server localDataServer, Socket client) {
		this.localDataServer = localDataServer;
		this.client = client;
		this.id = this.localDataServer.getNumClients();
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
			Packet packet;
			try {
				packet = (Packet) input.readObject();
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						localDataServer.message_from_client(id, packet);
					}
				});
				
			}catch(IOException e) {
				System.err.println("End of File Exception at ClientCommunicator => stopped running");
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
			int i = 0;
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

	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return this.id;
	}
}
