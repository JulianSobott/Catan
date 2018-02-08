package core;
import org.jsfml.system.Vector2i;

import network.Command;
import network.LocalDataServer;

public class Core {
	// data server
	LocalDataServer data_server;

	// map
	Map map = new Map();

	public Core(LocalDataServer data_server) {
		this.data_server = data_server;

		// DEBUG
		map.create_map(Map.map_size, 42);
		data_server.update_new_map(map.getFields());

	}

	public void dice() {
		// TODO Auto-generated method stub
		
	}

	public void buildRequest(int id, Command buildType, Vector2i position) {
		
	}
}
