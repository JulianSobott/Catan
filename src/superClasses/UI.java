package superClasses;

import java.util.List;

import local.LocalPlayer;

public abstract class UI {
	
	protected int id;
	
	public UI() {
		// TODO Auto-generated constructor stub
	}

	public abstract void show_guest_at_lobby(String name);
	public abstract void show_dice_result(int diceResult);
	public abstract void init_scoreboard(List<LocalPlayer> player);

	public abstract void build_game_menu();
	
	public void setID(int id) {
		this.id = id;
	}
	public int getID() {
		return this.id;
	}
}
