package com.catangame.catan.superClasses;

import com.catangame.catan.utils.Color;
import com.catangame.catan.math.Vector3i;

import com.catangame.catan.core.Building;
import com.catangame.catan.data.DevelopmentCard;
import com.catangame.catan.local.TradeDemand;
import com.catangame.catan.local.TradeOffer;
import com.catangame.catan.core.Map.GeneratorType;

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

	public abstract void playCard(int id, DevelopmentCard card);
}
