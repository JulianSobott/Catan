package com.catangame.catan.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.catangame.catan.network.Client;
import com.catangame.catan.network.Command;
import com.catangame.catan.network.NewClientListener;
import com.catangame.catan.network.Packet;


public class MainServer {
	public static String getIP;
	private final int PORT = 56789;
	private String serverIPAdress;
	
	private ServerSocket serverSocket;
	private NewServerClientListener newClientListener;
	
	private List<ServerClientCommunicator> allClients = new ArrayList<ServerClientCommunicator>();
	private List<ServerGame> allActiveServerGames = new ArrayList<ServerGame>();
	private List<ServerGame> allJoinableServerGames = new ArrayList<ServerGame>();
	
	private List<Long> availablePublicIDs;
	public static void main(String[] args) {
		new MainServer();
	}
	
	public MainServer() {
		//Getting the local IP Adress
		//TODO delete this when server has static Ip
		DatagramSocket ds;
		try {
			ds = new DatagramSocket();
			ds.connect(InetAddress.getByName("8.8.8.8"), 1002);
			this.serverIPAdress = ds.getLocalAddress().getHostAddress();
			System.out.printf("Local IP-adress from Server: %s\r\n", this.serverIPAdress);
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//Open Server at Port
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("LocalPort: " + serverSocket.getLocalPort());
		} catch (IOException e) {
			System.err.println("Couldn't create Server Socket");
			e.printStackTrace();
		}
		availablePublicIDs = initPublicIDs();
		//NewClientListener listener = new NewClientListener(null, serverSocket);
		//listener.start();
		newClientListener = new NewServerClientListener(this, serverSocket);
		newClientListener.start();
	}
	
	private void start() {
		
	}

	public void addNewClient(Socket clientSocket) {
		ServerClientCommunicator clientCommunicator = new ServerClientCommunicator(this, clientSocket);
		clientCommunicator.start();
		long publicID = getPublicID();
		clientCommunicator.setPublicID(publicID);
		clientCommunicator.message(new Packet(Command.ACCEPT_OFFER));
		this.allClients.add(clientCommunicator);
	}
	
	public void removeClient(long publicID) {
		for(ServerClientCommunicator client : allClients) {
			if(client.getPublicID() == publicID) {
				availablePublicIDs.add(publicID);
				client.stopRunning();
				break;
			}
		}
	}
	
	public void messageFromClient(ServerClientCommunicator clientCommunicator, Packet packet) {
		switch(packet.getCommand()) {
		case SHOW_ALL_JOINABLE_GAMES:
			clientCommunicator.message(new Packet(Command.SHOW_ALL_JOINABLE_GAMES, new Packet.ListData(this.allJoinableServerGames)));
			break;
		default:
			System.err.println("Unknown Command reached mainserver: " + packet.getCommand());
		}
	}
	
	private long getPublicID() {
		Random random = new Random();
		int idx = random.nextInt(availablePublicIDs.size());
		long id = availablePublicIDs.get(idx);
		availablePublicIDs.remove(idx);
		return id;
	}
	
	private List<Long> initPublicIDs(){
		List<Long> ids = new ArrayList<Long>();
		for(long i = 100; i < 300; i++) { //Max 200 Clients
			ids.add(i);
		}
		return ids;
	}
}
