package superClasses;

import data.Field;
import local.LocalState.GameMode;

public abstract class GameLogic {

	public GameLogic() {
		
	}

	public abstract void update_new_map(Field[][] fields);

	public abstract void set_mode(GameMode game);

}
