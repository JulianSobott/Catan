package superClasses;

import local.LocalGameLogic;
import local.LocalUI;

public abstract class Core {
	
	protected UI ui;
	protected GameLogic logic;
	
	public Core() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract void create_new_map(int map_size, int seed);

	public abstract void register_new_user(String name);
	
	public void setLogic(GameLogic logic) {
		this.logic = logic;
	}
	
	public void setUI(LocalUI ui) {
		this.ui = ui;
	}

	public abstract void dice(int id);
	
}
