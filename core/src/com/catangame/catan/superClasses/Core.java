package com.catangame.catan.superClasses;

import com.catangame.catan.utils.Color;
import com.catangame.catan.math.Vector3i;

import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.Building;
import com.catangame.catan.data.DevCard;
import com.catangame.catan.data.Resource;
import com.catangame.catan.local.TradeDemand;
import com.catangame.catan.local.TradeOffer;
import com.catangame.catan.local.gui.Message;
import com.catangame.catan.core.Map.GeneratorType;
import com.catangame.catan.core.Player;

public abstract class Core {
	
	public Core() {
	}
	
	public abstract void create_new_map(int islandSize, int seed, float[] resourceRatio, GeneratorType generatorType,
			int randomStartBuildings, int startResources);

	public abstract void register_new_user(String name, Color color);
	
	public abstract void buildRequest(int id, Building.Type buildType, Vector3i position);

	public abstract void nextTurn(int id);

	public abstract void new_trade_demand(TradeDemand tradeDemand);

	public abstract void new_trade_offer(TradeOffer tradeOffer);

	public abstract void acceptOffer(TradeOffer offer);

	public abstract void closeTrade();

	public abstract void buyDevelopmentCard(int id);

	public abstract void playCard(int id, DevCard devCard);

	public abstract void removeResources(int id, Map<Resource, Integer> removedResources);

	public abstract void moveRobber(int id, Vector2 position);

	public abstract void stealResource(int id, int player);

	public abstract void declineTradeDemand(int id);

	public abstract void newChatMessage(Message msg);

}
