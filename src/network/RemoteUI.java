package network;

import java.util.List;

import local.LocalPlayer;
import superClasses.UI;

public class RemoteUI extends UI {
	private Server server;
	
	public RemoteUI(Server server) {
		this.server = server;
	}

	@Override
	public void show_dice_result(int diceResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init_scoreboard(List<LocalPlayer> player) {
		System.out.println("init Scoreboard communicator");
		server.messageToAll(new Packet(Command.INIT_SCOREBOARD, new Packet.Scoreboard(player)));
	}

	@Override
	public void build_game_menu() {
		server.messageToAll(new Packet(Command.START_GAME));
	}

	@Override
	public void show_guest_at_lobby(String name) {
		//server.message_to_client(0, new Packet());
	}
}
