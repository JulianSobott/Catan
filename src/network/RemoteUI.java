package network;

import core.Player;
import data.Language;
import data.Resource;

import java.util.List;

import org.jsfml.graphics.Color;

import local.LocalPlayer;
import local.TradeDemand;
import local.TradeOffer;
import superClasses.UI;

public class RemoteUI extends UI {
	private Server server;

	public RemoteUI(Server server) {
		this.server = server;
	}
	
	@Override 
	public void setID(int id) {
		this.id = id;
		server.message_to_client(id, new Packet(Command.SET_ID, new Packet.ID(id)));
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
		server.message_to_client(id, new Packet(Command.SHOW_NEW_MEMBER, new Packet.Name(name, Color.RED)));
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
	public void show_trade_demand(TradeDemand tradeDemand) {
		server.message_to_client(id, new Packet(Command.TRADE_DEMAND, new Packet.TradeDemand(tradeDemand)));
	}

	@Override
	public void addTradeOffer(TradeOffer tradeOffer) {
		server.message_to_client(id, new Packet(Command.ADD_TRADE_OFFER, new Packet.TradeOffer(tradeOffer)));
	}

	@Override
	public void closeTradeWindow() {
		server.message_to_client(id, new Packet(Command.CLOSE_TRADE_WINDOW));
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public void show_kicked() {
		server.message_to_client(id, new Packet(Command.SHOW_KICKED));
	}
	@Override
	public void showAllPossibleNames(List<Player> player) {
		server.message_to_client(id, new Packet(Command.SHOW_ALL_POSSIBLE_NAMES, new Packet.PlayerList(player)));
	}

	@Override
	public void showEndScreen(int winnerID, List<Player> player) {
		server.message_to_client(id, new Packet(Command.END_SCREEN, new Packet.PlayerList(player)));
	}

}
