package network;

import core.Building;
import data.Field;
import java.util.List;
import java.util.Map;
import local.LocalState.GameMode;
import superClasses.GameLogic;

public class RemoteGameLogic extends GameLogic {
	private Server server;
	
	public RemoteGameLogic(Server server) {
		this.server = server;
	}

	@Override
	public void update_new_map(Field[][] fields) {
		server.message_to_client(id, new Packet(Command.NEW_MAP, new Packet.New_Map(fields)));
	}

	@Override
	public void set_mode(GameMode mode) {
		server.message_to_client(id, new Packet(Command.SET_MODE, new Packet.NEW_MODE(mode)));
	}

	@Override
	public void update_buildings(Map<Integer, List<Building>> buildings) {
		//TODO
	}

}
