import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//TODO Rename Method "message"

public class ClientCommunicator extends Thread{
	
	private LocalDataServer localDataServer;
	private Socket client;
	private int id; 
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public ClientCommunicator(LocalDataServer localDataServer, Socket client) {
		this.localDataServer = localDataServer;
		this.client = client;
		this.id = this.localDataServer.getNumClients();
	}
	
	public void run() {
		try {
			this.output = new ObjectOutputStream(this.client.getOutputStream());
			this.input = new ObjectInputStream(this.client.getInputStream());
		}catch(IOException e) {
			System.err.println("Can´t create input and output streams to client");
			e.printStackTrace();
		}
		
		//Listen for Input from Client
		while(true) {
			Packet packet;
			try {
				packet = (Packet) input.readObject();
				this.localDataServer.recievedNewPacket(this.id, packet);
			}catch(IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.err.println("Object is from unknown Class");
				e.printStackTrace();
			}
		}
	}
	
	public void message(Packet p) {
		try {
			this.output.writeObject(p);
			this.output.flush();
		}catch(IOException e) {
			System.err.println("Can´t send Packet to Client with the Code:" + p.getCommand());
		}
		
	}
}
