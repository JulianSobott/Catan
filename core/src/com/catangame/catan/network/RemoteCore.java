package com.catangame.catan.network;

import com.catangame.catan.utils.Color;
import com.catangame.catan.math.Vector3i;

import com.catangame.catan.core.Building.Type;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.DevelopmentCard;
import com.catangame.catan.local.TradeDemand;
import com.catangame.catan.local.TradeOffer;
import com.catangame.catan.superClasses.Core;
import com.catangame.catan.superClasses.GameLogic;
import com.catangame.catan.superClasses.UI;
import com.catangame.catan.core.Map.GeneratorType;
import com.catangame.catan.core.Player;

public class RemoteCore extends Core {
	Client client;

	public RemoteCore() {
	}

	public void setClientConnection(Client client) {
		this.client = client;
	}

	@Override
	public void create_new_map(int islandSize, int seed, float[] resourceRatio, GeneratorType generatorType,
			int randomStartBuildings, int startResources) {
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

	@Override
	public void buyDevelopmentCard(int id) {
		this.client.sendMessage(new Packet(Command.BUY_DEVELOPMENT_CARD));
	}

	@Override
	public void playCard(int id, DevCard card) {
		this.client.sendMessage(new Packet(Command.PLAY_DEVELOPMENTCARD, new Packet.Developmentcard(card)));
	}

}
