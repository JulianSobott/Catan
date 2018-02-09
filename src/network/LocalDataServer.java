package network;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import core.Core;
import local.Field;
import local.UI;

public class LocalDataServer extends DataIfc {

	private ServerSocket server;
	private String localServerIP;
	private static final int PORT = 56789;

	private int numClients = 0;
	
	private NewClientListener newClientListener;
	//TODO implement Core with constructor
	Core core = new Core(this);

	private ArrayList<ClientCommunicator> clients = new ArrayList<ClientCommunicator>();

	public LocalDataServer(UI ui) {
		super(ui);
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
		this.newClientListener = new NewClientListener(this, this.server);
		this.newClientListener.start();
		this.numClients++;
	}

	public void addNewClient(Socket client) {
		ClientCommunicator communicator = new ClientCommunicator(this, client);
		clients.add(communicator);
		communicator.start();
	}

	public void recievedNewPacket(int id, Packet packet) {
		switch (packet.getCommand()) {
		case DICE:
			core.dice();
			break;
		case BUILD_VILLAGE:
			core.buildRequest(id, Command.BUILD_VILLAGE, ((Packet.Build) packet.data).getPosition());
			break;
		case BUILD_CITY:
			core.buildRequest(id, Command.BUILD_CITY, ((Packet.Build) packet.data).getPosition());
			break;
		case BUILD_STREET:
			core.buildRequest(id, Command.BUILD_STREET, ((Packet.Build) packet.data).getPosition());
			break;
		case STRING:
			System.out.println("Server reached Message: " + packet.getDebugString());
			break;
		default:
			System.err.println("Unknown Command reached Server");
		}
	}

	public void messageTo(int idxClient, Packet packet) {
		this.clients.get(idxClient).message(packet);
	}

	//Getter Setter
	public int getNumClients() {
		return this.numClients;
	}

	// commands from the core
	public void update_new_map(Field[][] fields) {
		// TODO push to other clients

		update_new_map_local(fields);
	}

	//
	@Override
	public void update_new_map_local(Field[][] fields) {
		ui.getLogic().update_new_map(fields);
	}

	@Override
	public void closeAllRessources() {
		this.newClientListener.stopListen();
		for(ClientCommunicator client : clients) {
			client.stopRunning();
		}
		try {
			this.server.close();
		} catch (IOException e) {
			System.err.println("Can�t close Server at LocalDataServer");
		}
	}
}