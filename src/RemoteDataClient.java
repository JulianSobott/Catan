import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

//TODO Error handling when Entered a wrong IP address (reentering)
class RemoteDataClient implements DataIfc {
	private Socket server;
	private static final int PORT = 56789;
	private String serverIP;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public RemoteDataClient(String serverIP) {
		this.serverIP = serverIP;
		//Init Connection to server
		try {
			this.server = new Socket(this.serverIP, PORT);
		}catch(UnknownHostException e) {
			System.err.println("Unknown Host! Try to enter a new IP");
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		//Init Output and Input to Server 
		try {
			this.output = new ObjectOutputStream(this.server.getOutputStream());
			this.input = new ObjectInputStream(this.server.getInputStream());
		}catch(IOException e) {
			System.err.println("Can´t create input and output streams to server");
			e.printStackTrace();
		}
		
		new ClientInputListener(this, input).start();
	}

	public void recievedNewMessage(Packet packet) {
		System.out.println("Client Recieved: " + packet.getCode());
	}
	
	public void sendMessage(Packet p) {
		try {
			this.output.writeObject(p);
			this.output.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
