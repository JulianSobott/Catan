package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import local.Field;
import local.LocalLogic;
import local.UI;

//TODO Error handling when Entered a wrong IP address (reentering)
public class RemoteDataClient extends DataIfc {
	private Socket server;
	private static final int PORT = 56789;
	private String serverIP;
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private ClientInputListener clientInputListener;
	
	//TODO implement connection to local Game
	LocalLogic localLogic = new LocalLogic();
	
	public RemoteDataClient(UI ui, String serverIP) {
    super(ui);
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
		
		this.clientInputListener = new ClientInputListener(this, input);
		this.clientInputListener.start();
	}

	public void receivedNewMessage(Packet packet) {
		switch(packet.getCommand()){
		case DICE:
			this.localLogic.diceResult(((Packet.DiceResult) packet.data).getDiceResult());
			break;
		case BUILD_VILLAGE:
			this.localLogic.build(((Packet.Build) packet.data).getIdPlayer(), Command.BUILD_VILLAGE,((Packet.Build) packet.data).getPosition());
			break;
		case BUILD_CITY:
			this.localLogic.build(((Packet.Build) packet.data).getIdPlayer(), Command.BUILD_CITY, ((Packet.Build) packet.data).getPosition());
			break;
		case BUILD_STREET:
			this.localLogic.build(((Packet.Build) packet.data).getIdPlayer(), Command.BUILD_STREET, ((Packet.Build) packet.data).getPosition());
			break;
		case STRING:
			System.out.println("Client reached Message: " + packet.getDebugString());
			break;
		default:
			System.err.println("Unknown Command reached Client");
		}
	}
	
	public void sendMessage(Packet p) {
		try {
			this.output.writeObject(p);
			this.output.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
  
	@Override
	void update_new_map_local(Field[][] fields) {
    // TODO
	}

	@Override
	public void closeAllResources() {
		this.clientInputListener.closeConnectionToServer();
		try {
			this.input.close();
			this.output.close();
			this.server.close();
		} catch (IOException e) {
			System.err.println("Can�t close Listener at ClientCommunicator");
		}
	}
}
