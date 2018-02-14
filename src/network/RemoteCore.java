package network;

import org.jsfml.graphics.Color;

import superClasses.Core;
import superClasses.GameLogic;
import superClasses.UI;

public class RemoteCore extends Core {
	Client client;
	
	public RemoteCore() {
		// TODO Auto-generated constructor stub
	}

	public void setServer(Networkmanager data_connection) {
		// TODO Auto-generated method stub
		
	}

	public void setClientConnection(Client client) {
		this.client = client;
	}

	@Override
	public void create_new_map(int map_size, int seed) {
		//this.client.sendMessage(new Packet(Command.NEW_MAP, new Packet.CreateNewMap(map_size, seed)));
		// TODO only enable this call if core runs on a external server and you are authorized
	}

	public void addUI(UI ui) {
		//client.addUI(ui);
	}

	@Override
	public void register_new_user(String name, Color color) {
		this.client.sendMessage(new Packet(Command.NAME, new Packet.Name(name, color)));
	}

	public void addLogic(GameLogic local_logic) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dice(int id) {
		this.client.sendMessage(new Packet(Command.DICE));
	}

	@Override
	public void nextTurn(int id) {
		this.client.sendMessage(new Packet(Command.NEXT_TURN));
	}

}
