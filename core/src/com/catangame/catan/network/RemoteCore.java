package com.catangame.catan.network;

import com.catangame.catan.utils.Color;
import com.catangame.catan.math.Vector3i;

import java.util.Map;
import java.util.TreeMap;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.Building.Type;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.Resource;
import com.catangame.catan.local.TradeDemand;
import com.catangame.catan.local.TradeOffer;
import com.catangame.catan.superClasses.Core;
import com.catangame.catan.superClasses.GameLogic;
import com.catangame.catan.superClasses.UI;
import com.catangame.catan.core.Map.GeneratorType;
import com.catangame.catan.core.Building;
import com.catangame.catan.core.Player;

public class RemoteCore extends Core {
	Client client;

	public RemoteCore() {
		// create initial resource database
				TreeMap<Resource, Integer> neededResources = new TreeMap<Resource, Integer>();
				neededResources.put(Resource.WOOD, 1);
				neededResources.put(Resource.CLAY, 1);
				Building.Type.STREET.setNeededResources(neededResources);
				neededResources = new TreeMap<Resource, Integer>();
				neededResources.put(Resource.WOOD, 1);
				neededResources.put(Resource.WOOL, 1);
				neededResources.put(Resource.GRAIN, 1);
				neededResources.put(Resource.CLAY, 1);
				Building.Type.VILLAGE.setNeededResources(neededResources);
				neededResources = new TreeMap<Resource, Integer>();
				neededResources.put(Resource.GRAIN, 2);
				neededResources.put(Resource.ORE, 3);
				Building.Type.CITY.setNeededResources(neededResources);
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

	@Override
	public void removeResources(int id, Map<Resource, Integer> removedResources) {
		this.client.sendMessage(new Packet(Command.TAKE_RESOURCE, new Packet.Resouces(removedResources)));
	}

	@Override
	public void moveRobber(int id, Vector2 position) {
		this.client.sendMessage(new Packet(Command.MOVE_ROBBER, new Packet.Position(position)));
	}

	@Override
	public void stealResource(int id, int player) {
		this.client.sendMessage(new Packet(Command.STEEL_RESOURCE, new Packet.Num(player)));
	}

	

}
