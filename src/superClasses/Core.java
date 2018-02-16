package superClasses;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector3i;

import core.Building;
import local.TradeDemand;
import local.TradeOffer;

public abstract class Core {
	
	public Core() {
	}
	
	public abstract void create_new_map(int map_size, int seed);

	public abstract void register_new_user(String name, Color color);
	
	public abstract void buildRequest(int id, Building.Type buildType, Vector3i position);

	public abstract void nextTurn(int id);

	public abstract void new_trade_demand(TradeDemand tradeDemand);

	public abstract void new_trade_offer(TradeOffer tradeOffer);

	public abstract void acceptOffer(TradeOffer offer);
	
}
