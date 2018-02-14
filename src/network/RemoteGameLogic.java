package network;

import data.Field;
import local.LocalState.GameMode;
import superClasses.GameLogic;

public class RemoteGameLogic extends GameLogic {
	private Server server;
	
	public RemoteGameLogic(Server server) {
		this.server = server;
	}

	@Override
	public void update_new_map(Field[][] fields) {
		server.messageToAll(new Packet(Command.NEW_MAP, new Packet.New_Map(fields)));
	}

	@Override
	public void set_mode(GameMode mode) {
		server.messageToAll(new Packet(Command.SET_MODE, new Packet.NEW_MODE(mode)));
	}

}
