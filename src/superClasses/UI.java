package superClasses;

import core.Player;
import data.Language;
import java.util.List;

import local.LocalPlayer;

public abstract class UI {
	
	protected int id;
	
	public UI() {
	}

	public abstract void update_scoreboard(List<LocalPlayer> player);
	public abstract void show_guest_at_lobby(String name);

	public abstract void show_informative_hint(Language text);
	public abstract void show_dice_result(byte result);
	public abstract void update_player_data(Player player);

	public abstract void build_game_menu();
	
	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return this.id;
	}
}
