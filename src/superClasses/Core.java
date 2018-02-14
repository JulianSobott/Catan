package superClasses;

import local.RealLocalLogic;
import local.RealUI;

public abstract class Core {
	
	private UI ui;
	private LocalLogic logic;
	
	public Core() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract void create_new_map(int map_size, int seed);

	public abstract void register_new_user(String name);
	
	public void setLogic(LocalLogic logic) {
		this.logic = logic;
	}
	
	public void setUI(RealUI ui) {
		this.ui = ui;
	}
	
}
