package network;

import java.util.List;

import local.LocalPlayer;
import superClasses.UI;

public class UICommunicator extends UI {
	private Server server;
	
	public UICommunicator(Server server) {
		this.server = server;
	}

	@Override
	public void show_dice_result(int diceResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init_scoreboard(List<LocalPlayer> player) {
		server.messageToAll(new Packet(Command.INIT_SCOREBOARD, new Packet.Scoreboard(player)));
	}

	@Override
	public void build_game_menu() {
		server.messageToAll(new Packet(Receiver.LocalLogic, Command.START_GAME));
	}

}
