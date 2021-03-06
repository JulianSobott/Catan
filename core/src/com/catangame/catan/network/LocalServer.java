package com.catangame.catan.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.catangame.catan.utils.Color;
import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.LocalCore;
import com.catangame.catan.core.LocalFilehandler;
import com.catangame.catan.superClasses.Server;

public class LocalServer extends Server {

	private ServerSocket server;
	private String localServerIP;
	private static final int PORT = 56780;

	private int numClients = 0;

	private NewClientListener newClientListener;

	private ArrayList<ClientCommunicator> clients = new ArrayList<ClientCommunicator>();

	private LocalCore core;

	public LocalServer(LocalCore core) {
		this.core = core;
		//Getting the local IP Adress
		DatagramSocket ds;
		try {
			ds = new DatagramSocket();
			ds.connect(InetAddress.getByName("8.8.8.8"), 1002);
			this.localServerIP = ds.getLocalAddress().getHostAddress();
			core.showIpAtLobby(this.localServerIP);
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
			System.err.println("Couldn't create Server Socket");
			e.printStackTrace();
		}
		this.newClientListener = new NewClientListener(this, this.server);
		this.newClientListener.start();
		this.numClients++;
	}

	public void closeAllResources() {
		this.newClientListener.stopListen();
		for (ClientCommunicator client : clients) {
			client.stopRunning();
		}
		try {
			this.server.close();
		} catch (IOException e) {
			System.err.println("Can't close Server at LocalDataServer");
		}
	}

	//Getter Setter
	public int getNumClients() {
		return this.numClients;
	}

	// network management

	public void addNewClient(Socket client) {
		//TODO maybe add setting ID communicator here
		this.numClients++;
		ClientCommunicator communicator = new ClientCommunicator(this, client);
		clients.add(communicator);
		communicator.start();
	}
	
	
	public void message_from_client(int id, Packet packet) {
		switch (packet.getCommand()) {
		case STRING:
			System.out.println("Server reached Message: " + packet.getDebugString());
			break;
		case NAME:
			String name = ((Packet.Name) packet.data).getName();
			Color color = ((Packet.Name) packet.data).getColor();
			core.register_new_user(name, color);
			break;
		case BUILD_REQUEST:
			core.buildRequest(id, ((Packet.BuildRequest) packet.data).getBuildingType(),
					((Packet.BuildRequest) packet.data).getPosition());
			break;
		case NEXT_TURN:
			core.nextTurn(id);
			break;
		case TRADE_DEMAND:
			core.new_trade_demand(((Packet.TradeDemand) packet.data).getTradeDemand());
			break;
		case TRADE_OFFER:
			core.new_trade_offer(((Packet.TradeOffer) packet.data).getTradeOffer());
			break;
		case ACCEPT_OFFER:
			core.acceptOffer(((Packet.TradeOffer) packet.data).getTradeOffer());
			break;
		case CLOSE_TRADE_WINDOW:
			core.closeTrade();
			break;
		case DEMAND_DECLINED:
			core.declineTradeDemand(((Packet.ID) packet.data).getID());
			break;
		case BUY_DEVELOPMENT_CARD:
			core.buyDevelopmentCard(id);
			break;
		case PLAY_DEVELOPMENTCARD:
			core.playCard(id, ((Packet.Developmentcard) packet.data).getCard());
			break;
		case TAKE_RESOURCE:
			core.removeResources(id, ((Packet.Resouces) packet.data).resources);
			break;
		case MOVE_ROBBER:
			core.moveRobber(id, (Vector2) ((Packet.Position) packet.data).position);
			break;
		case STEEL_RESOURCE:
			core.stealResource(id, (int) ((Packet.Num) packet.data).num);
			break;
		case MESSAGE:
			core.newChatMessage(((Packet.MessageData)packet.data).msg);
			break;
		case CONNECTION_LOST:
			if(packet.data == null) {
				core.clientLostConnection(id);
				break;
			}
			
		default:
			System.err.println("Unknown Command reached Server: " + packet.getCommand());
		}
	}
	
	@Override
	public void message_to_client(int id, Packet packet) {
		for (ClientCommunicator client : clients) {
			if (client.getID() == id) {
				client.message(packet);
				break;
			}
		}
	}

	public void set_id_last_joined(int id) {
		this.clients.get(clients.size() - 1).setID(id);
	}
	
	public void remove_client(int id) {
		int idx = 0;
		for(ClientCommunicator c : clients) {
			if(c.getID() == id) {
				break;
			}else {
				idx++;
			}
		}
		if(idx < clients.size())
			this.clients.remove(idx);
		else {
			System.out.println("Cant Remove Client: " + idx);
			core.register_new_user("Deleted User", Color.BLACK);
		}
	}
}
