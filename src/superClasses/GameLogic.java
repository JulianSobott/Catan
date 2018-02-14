package superClasses;

import core.Building;
import data.Field;
import java.util.List;
import local.LocalState.GameMode;

public abstract class GameLogic {

	public GameLogic() {
		
	}

	public abstract void set_mode(GameMode game);

	public abstract void update_buildings(java.util.Map<Integer, List<Building>> buildings);

	public abstract void update_new_map(Field[][] fields);

}
