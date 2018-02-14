package superClasses;

import java.util.List;

import local.LocalPlayer;

public abstract class UI {

	public UI() {
		// TODO Auto-generated constructor stub
	}

	public abstract void show_dice_result(int diceResult);
	public abstract void init_scoreboard(List<LocalPlayer> player);

	public abstract void build_game_menu();
	
}
