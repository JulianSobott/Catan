package superClasses;

import core.Player;
import data.Language;
import java.util.List;

import local.LocalPlayer;
import local.TradeDemand;
import local.TradeOffer;

public abstract class UI {
	
	protected int id;
	
	public UI() {
	}

	public abstract void update_scoreboard(List<LocalPlayer> player);
	public abstract void show_guest_at_lobby(String name);

	// leave \p replacementStr empty if you don't want to replace anything
	public abstract void show_informative_hint(Language text, String replacementStr);
	public abstract void show_dice_result(byte result);
	public abstract void set_current_player(String player);
	public abstract void update_player_data(Player player);

	public abstract void build_game_menu();
	
	public abstract void setID(int id);
	public abstract int getID();

	public abstract void show_trade_demand(TradeDemand tradeDemand);

	public abstract void addTradeOffer(TradeOffer tradeOffer);

	public abstract void closeTradeWindow();
}
