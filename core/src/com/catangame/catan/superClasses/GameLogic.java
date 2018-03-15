package com.catangame.catan.superClasses;

import com.badlogic.gdx.math.Vector2;
import com.catangame.catan.core.Building;
import com.catangame.catan.data.Field;
import com.catangame.catan.data.Resource;

import java.util.List;
import com.catangame.catan.local.LocalState.GameMode;

public abstract class GameLogic {
	protected int id;
	
	public GameLogic() {
		
	}

	public abstract void set_mode(GameMode game);

	public abstract void update_buildings(java.util.Map<Integer, List<Building>> buildings);

	public abstract void update_new_map(Field[][] fields, java.util.Map<Vector2, Resource> harbours);

	public abstract void add_building(int user, Building building);

	public abstract void setID(int id);

	public abstract void setRobberPosition(Vector2 robberPosition);
}
