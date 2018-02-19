package network;
import java.io.IOException;
import java.io.ObjectInputStream;

import data.Resource;

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
					Packet packet;
					packet = (Packet) input.readObject();
					this.client.message_from_core(packet);
					input.reset();
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
