package network;

import core.Player;
import data.Language;
import java.util.List;

import local.LocalPlayer;
import local.TradeDemand;
import superClasses.UI;

public class RemoteUI extends UI {
	private Server server;

	public RemoteUI(Server server) {
		this.server = server;
	}

	@Override
	public void show_dice_result(byte diceResult) {
		server.message_to_client(id, new Packet(Command.DICE_RESULT, new Packet.DiceResult(diceResult)));
	}

	@Override
	public void update_scoreboard(List<LocalPlayer> player) {
		System.out.println("init Scoreboard communicator");
		server.message_to_client(id, new Packet(Command.INIT_SCOREBOARD, new Packet.Scoreboard(player)));
	}

	@Override
	public void build_game_menu() {
		server.message_to_client(id, new Packet(Command.START_GAME));
	}

	@Override
	public void show_guest_at_lobby(String name) {
		// TODO really needed for remote?
	}

	@Override
	public void show_informative_hint(Language text, String replacement) {
		// TODO really needed for remote?
	}

	@Override
	public void update_player_data(Player player) {
		server.message_to_client(id, new Packet(Command.PLAYER_DATA, new Packet.PlayerData(player)));
	}

	@Override
	public void set_current_player(String player) {
		server.message_to_client(id, new Packet(Command.SET_CURR_USER, new Packet.SetCurrUser(player)));
	}

	@Override
	public void show_trade_demand(int demander_id, TradeDemand tradeDemand) {
		server.message_to_client(id, new Packet(Command.TRADE_DEMAND, new Packet.TradeDemand(demander_id, tradeDemand)));
	}

}
