package com.catangame.catan.server;

import java.util.ArrayList;
import java.util.List;

import com.catangame.catan.network.Packet;

public class ServerGame {

	public ServerClientCommunicator host;
	public List<ServerClientCommunicator> allClients = new ArrayList<ServerClientCommunicator>();
	public int gameID;
	
	public ServerGame(ServerClientCommunicator clientCommunicator) {
		this.host = clientCommunicator;
	}

	public void messageFromClient(ServerClientCommunicator serverClientCommunicator, Packet packet) {
		if(packet.receiver == 0) {
			host.message(packet);
		}
		for(ServerClientCommunicator client :allClients) {
			if(client.getClientGameID() == packet.receiver) {
				client.message(packet);
				break;
			}
		}
	}

}
