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

	public abstract void closeAllResources();

	// messages from the core

	abstract void update_new_map_local(Field[][] fields);

	// messages from the clients

}