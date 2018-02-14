package network;

import superClasses.Core;
import superClasses.LocalLogic;
import superClasses.UI;

public class CoreCommunicator extends Core {
	Client client;
	
	public CoreCommunicator() {
		// TODO Auto-generated constructor stub
	}

	public void setServer(DataIfc data_connection) {
		// TODO Auto-generated method stub
		
	}

	public void setClientConnection(Client client) {
		this.client = client;
	}

	@Override
	public void create_new_map(int map_size, int seed) {
		this.client.sendMessage(new Packet(Command.NEW_MAP, new Packet.CreateNewMap(map_size, seed)));
	}

	public void addUI(UI ui) {
		//client.addUI(ui);
	}

	@Override
	public void register_new_user(String name) {
		this.client.sendMessage(new Packet(Command.NAME, new Packet.Name(name)));
	}

	public void addLogic(LocalLogic local_logic) {
		// TODO Auto-generated method stub
		
	}

}
