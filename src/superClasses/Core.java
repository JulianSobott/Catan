package superClasses;

import org.jsfml.graphics.Color;

import local.LocalGameLogic;
import local.LocalUI;

public abstract class Core {
	
	public Core() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract void create_new_map(int map_size, int seed);

	public abstract void register_new_user(String name, Color color);
	
	public abstract void dice(int id);

	public abstract void nextTurn(int id);
	
}
