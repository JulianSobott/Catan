package superClasses;

import core.Building;
import data.Field;
import java.util.List;
import local.LocalState.GameMode;

public abstract class GameLogic {
	protected int id;
	
	public GameLogic() {
		
	}

	public abstract void set_mode(GameMode game);

	public abstract void update_buildings(java.util.Map<Integer, List<Building>> buildings);

	public abstract void update_new_map(Field[][] fields);

	public abstract void add_building(int user, Building building);

	public void setID(int id) {
		this.id = id;
	}
}
