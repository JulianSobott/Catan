class LocalDataServer extends DataIfc {

	LocalDataServer(UI ui) {
		super(ui);
	}

	// commands from the core
	void update_new_map(Field[][] fields) {
		// TODO push to other clients

		update_new_map_local(fields);
	}


	//
	@Override
	public void update_new_map_local(Field[][] fields) {
		ui.logic.update_new_map(fields);
	}

}
