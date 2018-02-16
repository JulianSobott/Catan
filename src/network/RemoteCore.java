package network;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector3i;

import core.Building.Type;
import local.TradeDemand;
import local.TradeOffer;
import superClasses.Core;
import superClasses.GameLogic;
import superClasses.UI;

public class RemoteCore extends Core {
	Client client;

	public RemoteCore() {
	}

	public void setClientConnection(Client client) {
		this.client = client;
	}

	@Override
	public void create_new_map(int map_size, int seed) {
		//this.client.sendMessage(new Packet(Command.NEW_MAP, new Packet.CreateNewMap(map_size, seed)));
		// TODO only enable this call if core runs on a external server and you are authorized
	}

	public void addUI(UI ui) {
		//client.addUI(ui);
	}

	@Override
	public void register_new_user(String name, Color color) {
		this.client.sendMessage(new Packet(Command.NAME, new Packet.Name(name, color)));
	}

	public void addLogic(GameLogic gameLogic) {
		// TODO delete?

	}

	@Override
	public void nextTurn(int id) {
		this.client.sendMessage(new Packet(Command.NEXT_TURN));
	}

	@Override
	public void buildRequest(int id, Type buildType, Vector3i position) {
		this.client.sendMessage(new Packet(Command.BUILD_REQUEST, new Packet.BuildRequest(buildType, position)));
	}

	@Override
	public void new_trade_demand(TradeDemand tradeDemand) {
		this.client.sendMessage(new Packet(Command.TRADE_DEMAND, new Packet.TradeDemand(tradeDemand)));
	}

	@Override
	public void new_trade_offer(TradeOffer tradeOffer) {
		this.client.sendMessage(new Packet(Command.TRADE_OFFER, new Packet.TradeOffer(tradeOffer)));
	}

	@Override
	public void acceptOffer(TradeOffer offer) {
		this.client.sendMessage(new Packet(Command.ACCEPT_OFFER, new Packet.TradeOffer(offer)));
	}

	@Override
	public void closeTrade() {
		this.client.sendMessage(new Packet(Command.CLOSE_TRADE_WINDOW));
	}

}
