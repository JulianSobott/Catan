package network;

import core.Player;
import data.Language;
import java.util.List;

import local.LocalPlayer;
import superClasses.UI;

public class RemoteUI extends UI {
	private Server server;
	
	public RemoteUI(Server server) {
		this.server = server;
	}

	@Override
	public void show_dice_result(byte diceResult) {
		server.messageToAll(new Packet(Command.DICE_RESULT, new Packet.DiceResult(diceResult)));
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

	@Override
	public void show_informative_hint(Language text) {
		
	}

	@Override
	public void update_player_data(Player player) {
		server.messageToAll(new Packet(Command.PLAYER_DATA, new Packet.PlayerData(player)));
	}
}
