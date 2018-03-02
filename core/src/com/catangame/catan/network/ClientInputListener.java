package com.catangame.catan.network;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.data.Resource;

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
				}catch(IOException e) {
					e.printStackTrace();
					System.err.println("Connection to Server closed (ClientInputListener Line 26)");
					this.connectionToServer = false;
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
