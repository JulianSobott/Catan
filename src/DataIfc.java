abstract class DataIfc {
	// ui
	UI ui;


	DataIfc(UI ui) {
		this.ui = ui;
	}

	abstract void update_new_map_local(Field[][] fields);

}