package network;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientInputListener extends Thread{
	
	private RemoteDataClient remoteDataClient;
	private ObjectInputStream input;
	
	private boolean connectionToServer = true;
	
	public ClientInputListener(RemoteDataClient remoteDataClient, ObjectInputStream input) {
		this.remoteDataClient = remoteDataClient;
		this.input = input;
	}
	
	public void run() {
		while(connectionToServer) {
			Packet packet;
			if(input != null) {
				try {
					packet = (Packet) input.readObject();
					this.remoteDataClient.recievedNewMessage(packet);
				}catch(IOException e) {
					e.printStackTrace();
					System.err.println("Connection to Server closed (ClientInputListener Line 26)");
					//this.connectionToServer = false;
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
			System.err.println("Can´t close Listener at ClientInputListener");
		}
	}
}
