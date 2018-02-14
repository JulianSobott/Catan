package superClasses;

import data.Field;
import local.LocalState.GameMode;

public abstract class LocalLogic {

	public LocalLogic() {
		
	}

	public abstract void update_new_map(Field[][] fields);

	public abstract void set_mode(GameMode game);

}
