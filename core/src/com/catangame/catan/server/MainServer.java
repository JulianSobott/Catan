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

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
	List<Integer> availableGameIDs = new ArrayList<Integer>();
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
		availablePublicIDs = initPublicIDs(100);
		this.initGameIDs(30);
		newClientListener = new NewServerClientListener(this, serverSocket);
		newClientListener.start();
	}


	public void addNewClient(Socket clientSocket) {
		ServerClientCommunicator clientCommunicator = new ServerClientCommunicator(this, clientSocket);
		clientCommunicator.start();
		long publicID = getPublicID();
		clientCommunicator.setPublicID(publicID);
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
		case CREATE_NEW_GAME:
			ServerGame game = new ServerGame(clientCommunicator);
			Random rand = new Random();
			int idx = rand.nextInt(availableGameIDs.size());
			game.gameID = availableGameIDs.get(idx);
			availableGameIDs.remove(idx);
			allJoinableServerGames.add(game);
			System.out.println("Successfully created new Game with ID: " + game.gameID);
			break;
		case JOIN_GAME:
			for(ServerGame game1 : allJoinableServerGames) {
				if(game1.gameID == ((Packet.ID) packet.data).getID()){
					game1.allClients.add(clientCommunicator);
					clientCommunicator.setGameID(game1.gameID);
					clientCommunicator.setServerGame(game1);
					break;
				}
			}
			break;
		default:
			//Send to receiver
			for(ServerClientCommunicator client : allClients) {
				if(client.getClientGameID() == packet.receiver && client.getGameID() == clientCommunicator.getGameID()) {
					client.message(packet);
					break;
				}
			}
		}
	}
	
	private long getPublicID() {
		Random random = new Random();
		int idx = random.nextInt(availablePublicIDs.size());
		long id = availablePublicIDs.get(idx);
		availablePublicIDs.remove(idx);
		return id;
	}
	
	private List<Long> initPublicIDs(int maxClients){
		List<Long> ids = new ArrayList<Long>();
		for(long i = 100; i < 100 + maxClients; i++) { //Max 200 Clients
			ids.add(i);
		}
		return ids;
	}
	private void initGameIDs(int maxGames) {
		for(int i = 100; i < 100 + maxGames; i++) { //Max 200 Clients
			this.availableGameIDs.add(i);
		}
	}
}
