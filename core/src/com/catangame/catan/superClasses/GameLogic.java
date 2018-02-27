package com.catangame.catan.superClasses;

import com.catangame.catan.core.Building;
import com.catangame.catan.data.Field;
import java.util.List;
import com.catangame.catan.local.LocalState.GameMode;

public abstract class GameLogic {
	protected int id;
	
	public GameLogic() {
		
	}

	public abstract void set_mode(GameMode game);

	public abstract void update_buildings(java.util.Map<Integer, List<Building>> buildings);

	public abstract void update_new_map(Field[][] fields);

	public abstract void add_building(int user, Building building);

	public abstract void setID(int id);
}
