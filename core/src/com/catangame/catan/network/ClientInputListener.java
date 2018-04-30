package com.catangame.catan.network;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.badlogic.gdx.Gdx;

public class ClientInputListener extends Thread{
	
	private Client client;
	private ObjectInputStream input;
	
	private boolean connectionToServer = true;
	
	public ClientInputListener(Client client, ObjectInputStream input) {
		this.client = client;
		this.input = input;
	}
	
	public void run() {
		while(connectionToServer) {
			if(input != null) {		
				try {
					final Packet packet;
					packet = (Packet) input.readObject();
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							client.message_from_core(packet);	
						}
					});				
				}catch(EOFException e) {
					e.printStackTrace();
					System.err.println("Connection to Server closed (ClientInputListener Line 26)");
					this.connectionToServer = false;
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							client.message_from_core(new Packet(Command.CONNECTION_LOST, new Packet.StringData("Host")));
						}
					});			
				}catch(IOException e) {
					e.printStackTrace();
				}catch(ClassNotFoundException e) {
					System.err.println("Object is from unknown Class");
					e.printStackTrace();
				}
			}else {
				
			}
			
		}
		return;
	}
	
	public void closeConnectionToServer() {
		this.connectionToServer = false;
		try {
			this.input.close();
		} catch (IOException e) {
			System.err.println("Can't close Listener at ClientInputListener");
		}
	}
}
