class Core {
	// data server
	LocalDataServer data_server;

	// map
	Map map = new Map();

	Core(LocalDataServer data_server) {
		this.data_server = data_server;

		// DEBUG
		map.create_map(5, 42);
		data_server.update_new_map(map.fields);

	}
}
