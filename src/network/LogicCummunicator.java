package network;

import data.Field;
import local.LocalState.GameMode;
import superClasses.LocalLogic;

public class LogicCummunicator extends LocalLogic {
	private Server server;
	
	public LogicCummunicator(Server server) {
		this.server = server;
	}

	@Override
	public void update_new_map(Field[][] fields) {
		// TODO Auto-generated method stub

	}

	@Override
	public void set_mode(GameMode mode) {
		server.messageToAll(new Packet(Command.SET_MODE, new Packet.NEW_MODE(mode)));
	}

}
