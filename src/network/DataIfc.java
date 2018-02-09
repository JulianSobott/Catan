package network;
import local.Field;
import local.LocalLogic;
import local.UI;

public abstract class DataIfc {
	// ui
	UI ui;
	LocalLogic local_logic;

	DataIfc(UI ui, LocalLogic local_logic) {
		this.ui = ui;
		this.local_logic = local_logic;
	}

	abstract void update_new_map_local(Field[][] fields);

	public abstract void closeAllResources();

}