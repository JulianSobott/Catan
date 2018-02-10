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
	}
	
	public void create_new_map(int map_size, int seed) {
		map.create_map(map_size, seed);
		data_server.update_new_map(map.getFields());
	}
	
	public void dice() {
		// TODO Auto-generated method stub
		
	}

	public void buildRequest(int id, Command buildType, Vector2i position) {
		
	}
	
	public Map getMap() {// FIXME should not be needed
		return this.map;
	}
}
