package com.catangame.catan.desktop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.catangame.catan.network.Command;
import com.catangame.catan.network.Packet;

public class ServerGame implements Serializable{

	public ServerClientCommunicator host;
	public List<ServerClientCommunicator> allClients = new ArrayList<ServerClientCommunicator>();
	public int gameID;
	
	public ServerGame(ServerClientCommunicator clientCommunicator) {
		this.host = clientCommunicator;
	}

	public void messageFromClient(ServerClientCommunicator serverClientCommunicator, Packet packet) {
		if(packet.getCommand() == Command.ID_LAST_JOINED) {
			allClients.get(allClients.size()-1).setClientGameID(((Packet.ID)packet.data).getID());
		}else {
			if(packet.receiver == 0) {
				host.message(packet);
			}else {
				for(ServerClientCommunicator client : allClients) {
					if(client.getClientGameID() == packet.receiver) {
						client.message(packet);
						break;
					}
				}
			}	
		}
		
	}

	public void removeGame() {
		//TODO maybe change this and just notify
		for(ServerClientCommunicator client : allClients) {
			client.stopRunning();
		}
	}

}
