package com.catangame.catan.network;

import com.catangame.catan.core.Player;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.Language;
import com.catangame.catan.data.Resource;

import java.util.List;

import com.catangame.catan.utils.Color;

import com.catangame.catan.local.LocalPlayer;
import com.catangame.catan.local.TradeDemand;
import com.catangame.catan.local.TradeOffer;
import com.catangame.catan.local.gui.Message;
import com.catangame.catan.superClasses.UI;

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

	@Override
	public void showDevelopmentCardWindow(DevCard card) {
		server.message_to_client(id, new Packet(Command.SHOW_DEVELOPMENTCARD_WINDOW, new Packet.Developmentcard(card)));
	}

	@Override
	public void showToMuchResourcesWindow(int numToRemove) {
		server.message_to_client(id, new Packet(Command.SHOW_TO_MUCH_RESOURCES, new Packet.Num(numToRemove)));
	}

	@Override
	public void showMoveRobber() {
		server.message_to_client(id, new Packet(Command.MOVE_ROBBER));
	}

	@Override
	public void showSteelResource(List<Player> surroundingPlayers) {
		server.message_to_client(id, new Packet(Command.STEEL_RESOURCE, new Packet.PlayerList(surroundingPlayers)));
	}

	@Override
	public void showDemandDeclined(int id) {
		server.message_to_client(id, new Packet(Command.DEMAND_DECLINED, new Packet.ID(id)));
	}

	@Override
	public void addNewMessage(Message msg) {
		server.message_to_client(id, new Packet(Command.MESSAGE, new Packet.MessageData(msg)));
	}

}
