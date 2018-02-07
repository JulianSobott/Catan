import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

class LocalDataServer implements DataIfc {
	
	private ServerSocket server;
	private String localServerIP;
	private static final int PORT = 56789 ;
	
	private ArrayList<ClientCommunicator> clients = new ArrayList<ClientCommunicator>(); 
	
	public LocalDataServer() {
		//Getting the local IP Adress
		DatagramSocket ds;
		try {
			ds = new DatagramSocket();
			ds.connect(InetAddress.getByName("8.8.8.8"), 1002);
			this.localServerIP = ds.getLocalAddress().getHostAddress();
			System.out.printf("Local IP-adress from Server: %s\r\n", this.localServerIP);
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//Open Server at Port 
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Couldn´t create Server Socket");
			e.printStackTrace();
		}
		new NewClientListener(this, this.server).start();
	}

	public void addNewClient(Socket client) {
		ClientCommunicator communicator = new ClientCommunicator(this, client);
		clients.add(communicator);
		communicator.start();
	}

	public void recievedNewPacket(Packet packet) {
			System.out.println("Server Recieved: " + packet.getCode());	
	}

	public void messageTo(int idxClient, String message) {
		this.clients.get(idxClient).message(new Packet(message));
	}
}
