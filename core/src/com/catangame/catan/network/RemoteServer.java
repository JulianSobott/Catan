package com.catangame.catan.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.badlogic.gdx.Gdx;
import com.catangame.catan.core.LocalCore;
import com.catangame.catan.superClasses.Server;

public class RemoteServer extends Server {
	
	private Socket socket;
	private String serverIP = "127.0.0.1";
	private final int PORT = 56789;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private LocalCore core;
	
	private boolean connectionToServer = false;
	
	public RemoteServer(LocalCore core) {
		this.core = core;
		try {
			this.socket = new Socket(this.serverIP, PORT);
			try {
				output = new ObjectOutputStream(this.socket.getOutputStream());
				input = new ObjectInputStream(this.socket.getInputStream());
				connectionToServer = true;
			}catch(IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Create new Online Game
		try {
			output.writeObject(new Packet(Command.CREATE_NEW_GAME));
		} catch (IOException e) {
			System.err.println("Cant Create new Game");
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void message_from_client(int id, Packet packet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void message_to_client(int id, Packet packet) {
		// TODO Auto-generated method stub
		
	}
	
	private void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(connectionToServer && input != null) {
					try {
						final Packet packet;
						packet = (Packet) input.readObject();
						messageFromServer(packet);			
					}catch(IOException e) {
						e.printStackTrace();
						System.err.println("Connection to Server closed (ClientInputListener Line 26)");
						connectionToServer = false;
					}catch(ClassNotFoundException e) {
						System.err.println("Object is from unknown Class");
						e.printStackTrace();
					}
				}
				return;
			}
		}).start();	
	}

	private void messageFromServer(Packet packet) {
		
	}

	@Override
	public void set_id_last_joined(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove_client(int id) {
		// TODO Auto-generated method stub
		
	}

}
