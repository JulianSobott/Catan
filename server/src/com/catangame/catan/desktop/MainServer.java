package com.catangame.catan.desktop;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.catangame.catan.network.Command;
import com.catangame.catan.network.Packet;
import com.catangame.catan.utils.Color;


public class MainServer {
	private final int PORT = 56780;
	public static final String SERVER_DOMAIN = "93.222.148.241";
	
	private ServerSocket serverSocket;
	private NewServerClientListener newClientListener;
	
	private List<ServerClientCommunicator> allClients = new ArrayList<ServerClientCommunicator>();
	private List<ServerClientCommunicator> clientsInLobby = new ArrayList<ServerClientCommunicator>();
	private List<ServerGame> allActiveServerGames = new ArrayList<ServerGame>();
	private List<ServerGame> allJoinableServerGames = new ArrayList<ServerGame>();
	
	private List<Long> availablePublicIDs;
	List<Integer> availableGameIDs = new ArrayList<Integer>();
	public static void main(String[] args) {
		new MainServer();
	}
	
	public MainServer() {
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
		this.clientsInLobby.add(clientCommunicator);
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
			//TODO create Packet Joinable games with mor information
			updateLobby();
			break;
		case CREATE_NEW_GAME:
			ServerGame game = new ServerGame(clientCommunicator);
			Random rand = new Random();
			int idx = rand.nextInt(availableGameIDs.size());
			game.gameID = availableGameIDs.get(idx);
			availableGameIDs.remove(idx);
			allJoinableServerGames.add(game);
			clientCommunicator.setServerGame(game);
			clientCommunicator.setGameID(game.gameID);
			clientCommunicator.setHost(true);
			this.clientsInLobby.remove(clientCommunicator);
			updateLobby();
			System.out.println("Successfully created new Game with ID: " + game.gameID);
			break;
		case JOIN_GAME:
			Packet.JoinGame joinGame = ((Packet.JoinGame) packet.data);
			for(ServerGame game1 : allJoinableServerGames) {
				if(game1.gameID == joinGame.gameID){
					game1.allClients.add(clientCommunicator);
					clientCommunicator.setGameID(game1.gameID);
					clientCommunicator.setServerGame(game1);
					game1.host.message(new Packet(Command.NAME, new Packet.Name(joinGame.playerName, joinGame.color)));
					for(ServerClientCommunicator client : game1.allClients) {
						client.message(new Packet(Command.SHOW_NEW_MEMBER, new Packet.Name(joinGame.playerName, joinGame.color)));
					}
					//TODO add Names from all Players
					clientCommunicator.message(new Packet(Command.SHOW_GUEST_LOBBY, new Packet.StringData(game1.gameName)));
					this.clientsInLobby.remove(clientCommunicator);
					break;
				}
			}
			break;
		case EXIT:
			//TODO add implementation
			//inform ingame
			//left lobby
			if(clientCommunicator.isHost()) {
				//TODO maybe inform guests
				removeGame(clientCommunicator.getGameID());
			}
			break;
		default:
			//Send to receiver
			System.err.println("Unknown command reached mainServer: " + packet.getCommand());
		}
	}
	
	private void updateLobby() {
		List<Packet.JoinableGame> allGames = new ArrayList<Packet.JoinableGame>();
		for(ServerGame game : allJoinableServerGames) {
			allGames.add(new Packet.JoinableGame(game.gameID, game.gameName, game.allClients.size()));
		}
		for(ServerClientCommunicator client : clientsInLobby) {
			client.message(new Packet(Command.SHOW_ALL_JOINABLE_GAMES, new Packet.JoinableGames(allGames)));
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

	public void removeGame(int gameID) {
		for(int i = 0; i < allActiveServerGames.size(); i++) {
			if(allActiveServerGames.get(i).gameID == gameID) {
				allActiveServerGames.get(i).removeGame();
				allActiveServerGames.remove(i);
			}
		}
		for(int i = 0; i < allJoinableServerGames.size(); i++) {
			if(allJoinableServerGames.get(i).gameID == gameID) {
				if(allJoinableServerGames.size() > 0) {
					allJoinableServerGames.get(i).removeGame();
					allJoinableServerGames.remove(i);
				}
			}
		}
	}
	
}
