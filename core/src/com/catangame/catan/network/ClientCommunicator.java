package com.catangame.catan.network;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.data.Resource;

//TODO Rename Method "message"

public class ClientCommunicator extends Thread{
	
	private LocalServer localDataServer;
	private Socket client;
	private int id; 
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private boolean running = true;
	
	public ClientCommunicator(LocalServer localDataServer, Socket client) {
		this.localDataServer = localDataServer;
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
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						localDataServer.message_from_client(id, packet);
					}
				});
				
			}catch(IOException e) {
				System.err.println("End of File Exception at ClientCommunicator => stopped running");
				this.running = false;
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						localDataServer.message_from_client(id, new Packet(Command.CONNECTION_LOST));
					}
				});
				
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
			e.printStackTrace();
			System.err.println("Can't close Listener at ClientCommunicator"+  e.getMessage());	
		}
	}

	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return this.id;
	}
}
